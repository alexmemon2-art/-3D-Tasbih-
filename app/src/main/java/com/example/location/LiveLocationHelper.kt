package com.example.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

data class LiveLocationResult(
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val isFromCache: Boolean = false
)

object LiveLocationHelper {

    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    fun isLocationProviderEnabled(context: Context): Boolean {
        return try {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                ?: return false
            lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (_: Exception) {
            false
        }
    }

    fun openDeviceLocationSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun openAppPermissionSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    @SuppressLint("MissingPermission")
    suspend fun fetchCurrentLocation(context: Context): Result<LiveLocationResult> {
        if (!hasLocationPermission(context)) {
            return Result.failure(IllegalStateException("Location permission disabled"))
        }
        val appContext = context.applicationContext
        val lm = appContext.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return Result.failure(IllegalStateException("Location service unavailable"))

        val freshLocation = withTimeoutOrNull(6500L) {
            requestSingleLocationUpdate(lm)
        }

        val bestLastKnown = getBestLastKnownLocation(lm)
        val chosen = freshLocation ?: bestLastKnown
            ?: return Result.failure(
                IllegalStateException(
                    if (isLocationProviderEnabled(appContext)) {
                        "GPS signal abhi nahi mila — khuli jagah par dobara try karein"
                    } else {
                        "Phone ki Location (GPS) ON karein"
                    }
                )
            )

        val resolvedCity = resolveCityName(appContext, chosen.latitude, chosen.longitude)
        return Result.success(
            LiveLocationResult(
                latitude = chosen.latitude,
                longitude = chosen.longitude,
                cityName = resolvedCity,
                isFromCache = freshLocation == null
            )
        )
    }

    @SuppressLint("MissingPermission")
    private fun getBestLastKnownLocation(lm: LocationManager): Location? {
        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )
        var best: Location? = null
        for (provider in providers) {
            try {
                val loc = lm.getLastKnownLocation(provider) ?: continue
                if (best == null || loc.time > best.time) {
                    best = loc
                }
            } catch (_: Exception) {}
        }
        return best
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestSingleLocationUpdate(lm: LocationManager): Location? =
        suspendCancellableCoroutine { cont ->
            val activeProviders = buildList {
                if (runCatching { lm.isProviderEnabled(LocationManager.GPS_PROVIDER) }.getOrDefault(false)) {
                    add(LocationManager.GPS_PROVIDER)
                }
                if (runCatching { lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) }.getOrDefault(false)) {
                    add(LocationManager.NETWORK_PROVIDER)
                }
            }

            if (activeProviders.isEmpty()) {
                cont.resume(null)
                return@suspendCancellableCoroutine
            }

            var resumed = false
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    if (!resumed) {
                        resumed = true
                        try {
                            lm.removeUpdates(this)
                        } catch (_: Exception) {}
                        cont.resume(location)
                    }
                }

                @Deprecated("Deprecated in Android API 29")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            try {
                for (provider in activeProviders) {
                    lm.requestLocationUpdates(
                        provider,
                        0L,
                        0f,
                        listener,
                        Looper.getMainLooper()
                    )
                }
            } catch (_: Exception) {
                if (!resumed) {
                    resumed = true
                    cont.resume(null)
                }
            }

            cont.invokeOnCancellation {
                try {
                    lm.removeUpdates(listener)
                } catch (_: Exception) {}
            }
        }

    private suspend fun resolveCityName(context: Context, lat: Double, lon: Double): String {
        val coordFallback = String.format(Locale.US, "Live GPS (%.2f, %.2f)", lat, lon)
        if (!Geocoder.isPresent()) return coordFallback
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    withTimeoutOrNull(2500L) {
                        suspendCancellableCoroutine { cont ->
                            geocoder.getFromLocation(lat, lon, 1) { addresses ->
                                val addr = addresses.firstOrNull()
                                val city = addr?.locality
                                    ?: addr?.subAdminArea
                                    ?: addr?.adminArea
                                    ?: addr?.countryName
                                cont.resume(city ?: coordFallback)
                            }
                        }
                    } ?: coordFallback
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    val addr = addresses?.firstOrNull()
                    addr?.locality
                        ?: addr?.subAdminArea
                        ?: addr?.adminArea
                        ?: addr?.countryName
                        ?: coordFallback
                }
            } catch (_: Exception) {
                coordFallback
            }
        }
    }
}
