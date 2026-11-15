package com.example.ui.components

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.calendar.IslamicCalendarEngine
import com.example.location.LiveLocationHelper
import com.example.model.CustomPrayerTimesConfig
import com.example.model.LocationTimezoneConfig
import com.example.model.ReminderSettings
import com.example.notifications.TasbihReminderScheduler
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IvoryMuted
import com.example.ui.theme.IvorySubtle
import com.example.ui.theme.IvoryText
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun PrayerQiblaScreen(
    wallpaperIndex: Int,
    selectedCityName: String,
    reminderSettings: ReminderSettings,
    locationConfig: LocationTimezoneConfig = LocationTimezoneConfig(),
    customPrayerTimes: CustomPrayerTimesConfig = CustomPrayerTimesConfig(),
    isOnline: Boolean = false,
    isDetectingLocation: Boolean = false,
    locationStatusMessage: String? = null,
    initialTab: Int = 0, // 0 = Prayer & Qibla, 1 = Reminders
    onCityChange: (String) -> Unit,
    onToggleUseLiveLocation: (Boolean) -> Unit = {},
    onDetectLiveLocation: () -> Unit = {},
    onUpdateTimezone: (useCustom: Boolean, offsetHours: Double) -> Unit = { _, _ -> },
    onUpdateCustomPrayerTimes: (CustomPrayerTimesConfig) -> Unit = {},
    onResetCustomPrayerTimes: () -> Unit = {},
    onSaveReminders: (ReminderSettings) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activeTab by remember(initialTab) { mutableIntStateOf(initialTab) }
    var hanafiAsr by remember { mutableStateOf(true) }
    var hasLocationPermission by remember {
        mutableStateOf(LiveLocationHelper.hasLocationPermission(context))
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val granted = grants.values.any { it } || LiveLocationHelper.hasLocationPermission(context)
        hasLocationPermission = granted
        if (granted) {
            Toast.makeText(context, "✅ Location Permission Enable Ho Gai!", Toast.LENGTH_SHORT).show()
            onDetectLiveLocation()
        } else {
            Toast.makeText(
                context,
                "⚠️ Live Location ke liye Location permission zaruri hai",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Live 12-hour clock ticker
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            nowMillis = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val effectiveCity = remember(selectedCityName, locationConfig) {
        IslamicCalendarEngine.resolveEffectiveCity(selectedCityName, locationConfig)
    }
    val todayGreg = remember { IslamicCalendarEngine.getTodayGregorian() }
    val calculatedPrayerTimes = remember(todayGreg, effectiveCity, hanafiAsr) {
        IslamicCalendarEngine.computeDailyPrayerTimes(todayGreg, effectiveCity, hanafiAsr)
    }
    val prayerTimes = remember(calculatedPrayerTimes, customPrayerTimes) {
        IslamicCalendarEngine.applyCustomPrayerTimes(calculatedPrayerTimes, customPrayerTimes)
    }
    val qiblaBearing = remember(effectiveCity) {
        IslamicCalendarEngine.computeQiblaBearingDegrees(effectiveCity)
    }

    val activeTzLabel = IslamicCalendarEngine.formatTimezoneOffsetLabel(effectiveCity.timezoneHours)
    val clock12hText = remember(nowMillis, locationConfig.useCustomTimezone, effectiveCity.timezoneHours) {
        val formatter = SimpleDateFormat("hh:mm:ss a", Locale.US)
        if (locationConfig.useCustomTimezone) {
            val offsetMs = (effectiveCity.timezoneHours * 3_600_000.0).roundToInt()
            val tz = TimeZone.getTimeZone("GMT").apply { rawOffset = offsetMs }
            formatter.timeZone = tz
        }
        formatter.format(Date(nowMillis))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("prayer_qibla_screen")
    ) {
        AnimatedIslamicBackground(
            autoChangeSeconds = 30,
            currentWallpaperIndex = wallpaperIndex
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Glass3DCircleIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        isActive = false,
                        onClick = onBack,
                        testTag = "prayer_back_button"
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🕌 نماز، قبلہ اور ریمائنڈرز",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldBright
                        )
                        Text(
                            text = if (isOnline) "🌐 Online • 12-Hour Schedule" else "📴 Offline Ready • 12-Hour Schedule",
                            fontSize = 11.sp,
                            color = if (isOnline) Color(0xFF6EE7B7) else IvorySubtle
                        )
                    }
                    Spacer(modifier = Modifier.size(42.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Glass3DPill(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        modifier = Modifier.weight(1f),
                        testTag = "tab_prayer_qibla"
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            tint = if (activeTab == 0) EmeraldDeepDark else GoldBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Namaz & Qibla",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == 0) EmeraldDeepDark else IvoryText
                        )
                    }

                    Glass3DPill(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        modifier = Modifier.weight(1f),
                        testTag = "tab_reminders"
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = if (activeTab == 1) EmeraldDeepDark else GoldBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "⏰ Reminders",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == 1) EmeraldDeepDark else IvoryText
                        )
                    }
                }
            }

            AnimatedContent(
                targetState = activeTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "prayer_qibla_tab_anim",
                modifier = Modifier.weight(1f)
            ) { tab ->
                if (tab == 0) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 560.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Live Location & Permission Card
                        PrayerGlassCard {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.GpsFixed,
                                            contentDescription = "Live Location",
                                            tint = GoldBright,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "📍 Live Location & Permission",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldBright
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(
                                                if (hasLocationPermission) Color(0x3310B981) else Color(0x33EF5350)
                                            )
                                            .border(
                                                0.8.dp,
                                                if (hasLocationPermission) Color(0xFF34D399) else Color(0xFFFF8A80),
                                                CircleShape
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (hasLocationPermission) "✅ Enabled" else "⚠️ Disabled",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (hasLocationPermission) Color(0xFF6EE7B7) else Color(0xFFFF8A80)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                if (locationConfig.liveLatitude != null && locationConfig.liveLongitude != null) {
                                    Text(
                                        text = "📌 Saved GPS: ${locationConfig.liveCityName.ifBlank { "Live GPS" }} " +
                                            "(${String.format(Locale.US, "%.4f, %.4f", locationConfig.liveLatitude, locationConfig.liveLongitude)})",
                                        fontSize = 12.sp,
                                        color = IvoryText
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    ReminderToggleRow(
                                        label = "Use Live GPS Location for Namaz & Qibla",
                                        checked = locationConfig.useLiveLocation,
                                        onCheckedChange = { enabled ->
                                            if (enabled && !LiveLocationHelper.hasLocationPermission(context)) {
                                                locationPermissionLauncher.launch(LiveLocationHelper.REQUIRED_PERMISSIONS)
                                            } else {
                                                onToggleUseLiveLocation(enabled)
                                            }
                                        }
                                    )
                                } else {
                                    Text(
                                        text = "Online ya Offline GPS se apni exact live location hasil karein taa ke Namaz aur Qibla 100% durust hon.",
                                        fontSize = 11.sp,
                                        color = IvorySubtle,
                                        lineHeight = 15.sp
                                    )
                                }

                                if (!locationStatusMessage.isNullOrBlank()) {
                                    Text(
                                        text = locationStatusMessage,
                                        fontSize = 11.sp,
                                        color = GoldLight,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (!hasLocationPermission) {
                                        Glass3DButton(
                                            onClick = {
                                                locationPermissionLauncher.launch(LiveLocationHelper.REQUIRED_PERMISSIONS)
                                            },
                                            modifier = Modifier.weight(1f),
                                            isPrimaryGold = true,
                                            height = 44.dp,
                                            testTag = "btn_enable_location_permission"
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = "Enable Location Permission",
                                                tint = EmeraldDeepDark,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Enable Location Permission",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = EmeraldDeepDark
                                            )
                                        }
                                    } else {
                                        Glass3DButton(
                                            onClick = {
                                                if (!LiveLocationHelper.isLocationProviderEnabled(context)) {
                                                    Toast.makeText(
                                                        context,
                                                        "Phone ki GPS Location ON karein",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    LiveLocationHelper.openDeviceLocationSettings(context)
                                                } else {
                                                    onDetectLiveLocation()
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            isPrimaryGold = true,
                                            enabled = !isDetectingLocation,
                                            height = 44.dp,
                                            testTag = "btn_detect_live_location"
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.GpsFixed,
                                                contentDescription = "Detect Live Location",
                                                tint = EmeraldDeepDark,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isDetectingLocation) {
                                                    "⏳ Detecting GPS..."
                                                } else {
                                                    "📍 Detect Live Location"
                                                },
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = EmeraldDeepDark
                                            )
                                        }

                                        Glass3DButton(
                                            onClick = { LiveLocationHelper.openAppPermissionSettings(context) },
                                            modifier = Modifier.width(96.dp),
                                            isPrimaryGold = false,
                                            height = 44.dp,
                                            testTag = "btn_location_app_settings"
                                        ) {
                                            Text(
                                                text = "⚙️ Perms",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GoldBright
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Timezone & 12-Hour Clock Card
                        PrayerGlassCard {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Public,
                                            contentDescription = "Timezone",
                                            tint = GoldBright,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "🕒 Timezone & 12-Hour Format",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldBright
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(GoldPrimary.copy(alpha = 0.2f))
                                            .border(0.8.dp, GoldBright, CircleShape)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "12h AM/PM",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldBright
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Current Time: $clock12hText  •  $activeTzLabel",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IvoryText,
                                    modifier = Modifier.testTag("tv_12h_clock_timezone")
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                ReminderToggleRow(
                                    label = "Custom Timezone Offset Set Karein",
                                    checked = locationConfig.useCustomTimezone,
                                    onCheckedChange = { enabled ->
                                        val defaultOffset = if (enabled) {
                                            locationConfig.customTimezoneHours
                                        } else {
                                            IslamicCalendarEngine.getDeviceTimezoneOffsetHours()
                                        }
                                        onUpdateTimezone(enabled, defaultOffset)
                                    }
                                )

                                if (locationConfig.useCustomTimezone) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Apna Timezone Chunein (UTC Offset):",
                                        fontSize = 11.sp,
                                        color = IvorySubtle,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(vertical = 2.dp)
                                    ) {
                                        items(IslamicCalendarEngine.supportedTimezoneOffsets) { offset ->
                                            val isSel = abs(offset - locationConfig.customTimezoneHours) < 0.05
                                            Glass3DPill(
                                                selected = isSel,
                                                onClick = { onUpdateTimezone(true, offset) }
                                            ) {
                                                Text(
                                                    text = IslamicCalendarEngine.formatTimezoneOffsetLabel(offset),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSel) EmeraldDeepDark else IvoryText
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // City Selector (fallback / manual cities)
                        PrayerGlassCard {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🏙️ City (${effectiveCity.name})",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldBright
                                    )
                                    Glass3DPill(
                                        selected = hanafiAsr,
                                        onClick = { hanafiAsr = !hanafiAsr }
                                    ) {
                                        Text(
                                            text = if (hanafiAsr) "Asr: Hanafi" else "Asr: Shafi",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (hanafiAsr) EmeraldDeepDark else IvoryText
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(vertical = 2.dp)
                                ) {
                                    items(IslamicCalendarEngine.sindhCities) { city ->
                                        val isSel = !locationConfig.useLiveLocation && city.name == effectiveCity.name
                                        Glass3DPill(
                                            selected = isSel,
                                            onClick = { onCityChange(city.name) }
                                        ) {
                                            Text(
                                                text = city.name,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSel) EmeraldDeepDark else IvoryText
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Live Qibla Compass
                        QiblaCompassCard(
                            cityName = effectiveCity.name,
                            qiblaBearing = qiblaBearing
                        )

                        // Daily 5 Namaz Times Card + Custom Namaz Time Override
                        PrayerGlassCard {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                val cal = Calendar.getInstance()
                                val nowMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

                                data class PrayerRowItem(
                                    val key: String,
                                    val label: String,
                                    val timeStr: String,
                                    val minutes: Int,
                                    val editable: Boolean,
                                    val isOverridden: Boolean
                                )

                                val prayers = listOf(
                                    PrayerRowItem(
                                        "fajr",
                                        "فجر • Fajr",
                                        prayerTimes.fajr,
                                        prayerTimes.fajrMinutes,
                                        true,
                                        customPrayerTimes.useCustomTimes && customPrayerTimes.fajrMinutes != null
                                    ),
                                    PrayerRowItem(
                                        "sunrise",
                                        "طلوعِ آفتاب • Sunrise",
                                        prayerTimes.sunrise,
                                        prayerTimes.sunriseMinutes,
                                        false,
                                        false
                                    ),
                                    PrayerRowItem(
                                        "zuhr",
                                        "ظہر • Zuhr",
                                        prayerTimes.zuhr,
                                        prayerTimes.zuhrMinutes,
                                        true,
                                        customPrayerTimes.useCustomTimes && customPrayerTimes.zuhrMinutes != null
                                    ),
                                    PrayerRowItem(
                                        "asr",
                                        "عصر • Asr",
                                        prayerTimes.asr,
                                        prayerTimes.asrMinutes,
                                        true,
                                        customPrayerTimes.useCustomTimes && customPrayerTimes.asrMinutes != null
                                    ),
                                    PrayerRowItem(
                                        "maghrib",
                                        "مغرب • Maghrib",
                                        prayerTimes.maghrib,
                                        prayerTimes.maghribMinutes,
                                        true,
                                        customPrayerTimes.useCustomTimes && customPrayerTimes.maghribMinutes != null
                                    ),
                                    PrayerRowItem(
                                        "isha",
                                        "عشاء • Isha",
                                        prayerTimes.isha,
                                        prayerTimes.ishaMinutes,
                                        true,
                                        customPrayerTimes.useCustomTimes && customPrayerTimes.ishaMinutes != null
                                    )
                                )
                                val nextIdx = prayers.indexOfFirst { it.minutes > nowMinutes }.let {
                                    if (it == -1) 0 else it
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "🕌 Namaz Auqat (${effectiveCity.name})",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldBright
                                        )
                                        Text(
                                            text = "12-Hour Format • Kisi bhi Namaz par tap kar ke Custom Time rakhein",
                                            fontSize = 11.sp,
                                            color = IvorySubtle
                                        )
                                    }
                                    if (customPrayerTimes.useCustomTimes) {
                                        Glass3DPill(
                                            selected = false,
                                            onClick = onResetCustomPrayerTimes,
                                            testTag = "btn_reset_custom_prayer_times"
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Reset Custom Times",
                                                tint = GoldBright,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Auto Reset",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GoldBright
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                prayers.forEachIndexed { idx, item ->
                                    val isNext = idx == nextIdx
                                    val openPicker = {
                                        val initHour = (item.minutes / 60).coerceIn(0, 23)
                                        val initMin = (item.minutes % 60).coerceIn(0, 59)
                                        TimePickerDialog(
                                            context,
                                            { _, pickedHour, pickedMinute ->
                                                val totalMin = pickedHour * 60 + pickedMinute
                                                val updated = when (item.key) {
                                                    "fajr" -> customPrayerTimes.copy(useCustomTimes = true, fajrMinutes = totalMin)
                                                    "zuhr" -> customPrayerTimes.copy(useCustomTimes = true, zuhrMinutes = totalMin)
                                                    "asr" -> customPrayerTimes.copy(useCustomTimes = true, asrMinutes = totalMin)
                                                    "maghrib" -> customPrayerTimes.copy(useCustomTimes = true, maghribMinutes = totalMin)
                                                    "isha" -> customPrayerTimes.copy(useCustomTimes = true, ishaMinutes = totalMin)
                                                    else -> customPrayerTimes
                                                }
                                                onUpdateCustomPrayerTimes(updated)
                                                Toast.makeText(
                                                    context,
                                                    "✅ ${item.label}: ${IslamicCalendarEngine.formatMinutes12h(totalMin)} save ho gaya",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            initHour,
                                            initMin,
                                            false // 12-hour AM/PM picker
                                        ).show()
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isNext) GoldPrimary.copy(alpha = 0.22f)
                                                else Color.White.copy(alpha = 0.05f)
                                            )
                                            .border(
                                                1.dp,
                                                if (isNext) GoldBright else Color.Transparent,
                                                RoundedCornerShape(12.dp)
                                            )
                                            .then(
                                                if (item.editable) {
                                                    Modifier.clickable { openPicker() }
                                                } else {
                                                    Modifier
                                                }
                                            )
                                            .padding(horizontal = 14.dp, vertical = 11.dp)
                                            .testTag("prayer_row_${item.key}"),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (isNext) "${item.label}  (Next)" else item.label,
                                                fontSize = 14.sp,
                                                fontWeight = if (isNext) FontWeight.ExtraBold else FontWeight.SemiBold,
                                                color = if (isNext) GoldBright else IvoryText
                                            )
                                            if (item.isOverridden) {
                                                Text(
                                                    text = "✏️ Custom Time Set",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = GoldLight
                                                )
                                            }
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = item.timeStr,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (isNext) GoldBright else GoldLight
                                            )
                                            if (item.editable) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Set Custom ${item.label} Time",
                                                    tint = GoldBright.copy(alpha = 0.8f),
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }
                                    }
                                    if (idx < prayers.lastIndex) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                } else {
                    RemindersSettingsContent(
                        context = context,
                        selectedCityName = effectiveCity.name,
                        prayerTimes = prayerTimes,
                        reminderSettings = reminderSettings,
                        onSaveReminders = onSaveReminders
                    )
                }
            }
        }
    }
}

@Composable
private fun QiblaCompassCard(
    cityName: String,
    qiblaBearing: Double
) {
    val context = LocalContext.current
    var azimuthDeg by remember { mutableFloatStateOf(0f) }
    var hasSensor by remember { mutableStateOf(true) }

    DisposableEffect(context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val rotSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (rotSensor == null && (accelSensor == null || magSensor == null)) {
            hasSensor = false
            onDispose { }
        } else {
            val gravity = FloatArray(3)
            val geomagnetic = FloatArray(3)
            val rMat = FloatArray(9)
            val orientation = FloatArray(3)
            var hasGravity = false
            var hasMag = false

            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                        SensorManager.getRotationMatrixFromVector(rMat, event.values)
                        SensorManager.getOrientation(rMat, orientation)
                        val deg = ((Math.toDegrees(orientation[0].toDouble()) + 360.0) % 360.0).toFloat()
                        azimuthDeg = azimuthDeg * 0.8f + deg * 0.2f
                    } else {
                        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                            System.arraycopy(event.values, 0, gravity, 0, 3)
                            hasGravity = true
                        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                            System.arraycopy(event.values, 0, geomagnetic, 0, 3)
                            hasMag = true
                        }
                        if (hasGravity && hasMag && SensorManager.getRotationMatrix(rMat, null, gravity, geomagnetic)) {
                            SensorManager.getOrientation(rMat, orientation)
                            val deg = ((Math.toDegrees(orientation[0].toDouble()) + 360.0) % 360.0).toFloat()
                            azimuthDeg = azimuthDeg * 0.8f + deg * 0.2f
                        }
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            }

            if (rotSensor != null) {
                sensorManager.registerListener(listener, rotSensor, SensorManager.SENSOR_DELAY_UI)
            } else {
                sensorManager.registerListener(listener, accelSensor, SensorManager.SENSOR_DELAY_UI)
                sensorManager.registerListener(listener, magSensor, SensorManager.SENSOR_DELAY_UI)
            }

            onDispose {
                sensorManager.unregisterListener(listener)
            }
        }
    }

    val relativeQiblaDeg = ((qiblaBearing.toFloat() - azimuthDeg) + 360f) % 360f
    val diffFromTop = abs(if (relativeQiblaDeg > 180f) relativeQiblaDeg - 360f else relativeQiblaDeg)
    val isAligned = hasSensor && diffFromTop <= 6f

    PrayerGlassCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🧭 Qibla Direction ($cityName)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = GoldBright
            )
            Text(
                text = "Kaaba Bearing: ${qiblaBearing.roundToInt()}° from True North",
                fontSize = 12.sp,
                color = IvorySubtle,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            Box(
                modifier = Modifier.size(190.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.minDimension / 2f - 10.dp.toPx()

                    // Outer bezel
                    drawCircle(
                        color = if (isAligned) Color(0xFF34D399) else GoldPrimary,
                        radius = radius,
                        center = center,
                        style = Stroke(width = 3.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0x22000000),
                        radius = radius - 3.dp.toPx(),
                        center = center
                    )

                    // North indicator rotated by -azimuthDeg
                    rotate(-azimuthDeg, pivot = center) {
                        drawLine(
                            color = Color(0xFFFF6B6B),
                            start = center,
                            end = Offset(center.x, center.y - radius + 14.dp.toPx()),
                            strokeWidth = 2.5.dp.toPx()
                        )
                    }

                    // Qibla golden needle rotated by relativeQiblaDeg
                    rotate(relativeQiblaDeg, pivot = center) {
                        val needlePath = Path().apply {
                            moveTo(center.x, center.y - radius + 12.dp.toPx())
                            lineTo(center.x - 10.dp.toPx(), center.y + 8.dp.toPx())
                            lineTo(center.x, center.y)
                            lineTo(center.x + 10.dp.toPx(), center.y + 8.dp.toPx())
                            close()
                        }
                        drawPath(
                            path = needlePath,
                            color = if (isAligned) Color(0xFF34D399) else Color(0xFFFDE047)
                        )
                    }

                    drawCircle(
                        color = GoldBright,
                        radius = 7.dp.toPx(),
                        center = center
                    )
                }

                Column(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🕋",
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when {
                    !hasSensor -> "Magnetometer not detected on this device — face ${qiblaBearing.roundToInt()}° (toward Makkah)."
                    isAligned -> "✨ Aligned directly toward the Holy Kaaba!"
                    else -> "Rotate phone until the golden arrow points straight up (${relativeQiblaDeg.roundToInt()}°)."
                },
                fontSize = 12.sp,
                fontWeight = if (isAligned) FontWeight.Bold else FontWeight.Normal,
                color = if (isAligned) Color(0xFF6EE7B7) else IvorySubtle,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RemindersSettingsContent(
    context: Context,
    selectedCityName: String,
    prayerTimes: com.example.calendar.DailyPrayerTimes,
    reminderSettings: ReminderSettings,
    onSaveReminders: (ReminderSettings) -> Unit
) {
    var fajr by remember(reminderSettings) { mutableStateOf(reminderSettings.fajrEnabled) }
    var zuhr by remember(reminderSettings) { mutableStateOf(reminderSettings.zuhrEnabled) }
    var asr by remember(reminderSettings) { mutableStateOf(reminderSettings.asrEnabled) }
    var maghrib by remember(reminderSettings) { mutableStateOf(reminderSettings.maghribEnabled) }
    var isha by remember(reminderSettings) { mutableStateOf(reminderSettings.ishaEnabled) }
    var azanAlerts by remember(reminderSettings) { mutableStateOf(reminderSettings.azanAlertsEnabled) }
    var customEnabled by remember(reminderSettings) { mutableStateOf(reminderSettings.customReminderEnabled) }
    var customHour by remember(reminderSettings) { mutableIntStateOf(reminderSettings.customHour) }
    var customMinute by remember(reminderSettings) { mutableIntStateOf(reminderSettings.customMinute) }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "Please allow Notifications to receive Tasbih & Namaz reminders.", Toast.LENGTH_SHORT).show()
        }
    }

    fun requestNotifPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val ok = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!ok) {
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 560.dp)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PrayerGlassCard {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "⏰ Namaz ke Baad Tasbih Reminders",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldBright
                )
                Text(
                    text = "Get a gentle notification after each prayer ($selectedCityName • 12h schedule).",
                    fontSize = 11.sp,
                    color = IvorySubtle,
                    modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                )

                ReminderToggleRow("Fajr ke baad (${prayerTimes.fajr})", fajr) {
                    if (it) requestNotifPermissionIfNeeded()
                    fajr = it
                }
                ReminderToggleRow("Zuhr ke baad (${prayerTimes.zuhr})", zuhr) {
                    if (it) requestNotifPermissionIfNeeded()
                    zuhr = it
                }
                ReminderToggleRow("Asr ke baad (${prayerTimes.asr})", asr) {
                    if (it) requestNotifPermissionIfNeeded()
                    asr = it
                }
                ReminderToggleRow("Maghrib ke baad (${prayerTimes.maghrib})", maghrib) {
                    if (it) requestNotifPermissionIfNeeded()
                    maghrib = it
                }
                ReminderToggleRow("Isha ke baad (${prayerTimes.isha})", isha) {
                    if (it) requestNotifPermissionIfNeeded()
                    isha = it
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = GlassBorder,
                    thickness = 0.7.dp
                )

                ReminderToggleRow("🕌 Azan / Prayer Time Alerts", azanAlerts) {
                    if (it) requestNotifPermissionIfNeeded()
                    azanAlerts = it
                }
            }
        }

        // Custom Preferred Time Card
        PrayerGlassCard {
            Column(modifier = Modifier.fillMaxWidth()) {
                ReminderToggleRow("🌙 Apna Pasandida Waqt (Custom Time)", customEnabled) {
                    if (it) requestNotifPermissionIfNeeded()
                    customEnabled = it
                }

                Spacer(modifier = Modifier.height(8.dp))

                val amPm = if (customHour < 12) "AM" else "PM"
                val h12 = when {
                    customHour == 0 -> 12
                    customHour > 12 -> customHour - 12
                    else -> customHour
                }
                val formattedTime = String.format(Locale.US, "%d:%02d %s", h12, customMinute, amPm)

                Glass3DButton(
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, pickedHour, pickedMinute ->
                                customHour = pickedHour
                                customMinute = pickedMinute
                                customEnabled = true
                                requestNotifPermissionIfNeeded()
                            },
                            customHour,
                            customMinute,
                            false
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimaryGold = false,
                    height = 48.dp,
                    testTag = "pick_custom_reminder_time"
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Pick Reminder Time",
                        tint = GoldBright,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Selected Time: $formattedTime (Tap to Change)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldBright
                    )
                }
            }
        }

        // Save + Test Notification
        Glass3DButton(
            onClick = {
                requestNotifPermissionIfNeeded()
                onSaveReminders(
                    ReminderSettings(
                        fajrEnabled = fajr,
                        zuhrEnabled = zuhr,
                        asrEnabled = asr,
                        maghribEnabled = maghrib,
                        ishaEnabled = isha,
                        azanAlertsEnabled = azanAlerts,
                        customReminderEnabled = customEnabled,
                        customHour = customHour,
                        customMinute = customMinute
                    )
                )
                Toast.makeText(context, "Reminder Settings Save Ho Gaye ✅", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
            isPrimaryGold = true,
            height = 52.dp,
            testTag = "save_reminders_button"
        ) {
            Text(
                text = "✅ Save & Schedule Reminders",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = EmeraldDeepDark
            )
        }

        Glass3DButton(
            onClick = {
                requestNotifPermissionIfNeeded()
                TasbihReminderScheduler.sendTestNotification(context)
            },
            modifier = Modifier.fillMaxWidth(),
            isPrimaryGold = false,
            height = 48.dp,
            testTag = "test_notification_button"
        ) {
            Text(
                text = "🔔 Send Test Reminder Notification",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GoldBright
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ReminderToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = IvoryText,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = GoldPrimary,
                uncheckedThumbColor = IvoryMuted,
                uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
            )
        )
    }
}

@Composable
private fun PrayerGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0x28FFFFFF), Color(0x14FFFFFF), Color(0x0AFFFFFF))
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(listOf(Color(0x55FFFFFF), GlassBorder, Color(0x18000000))),
                RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        content()
    }
}
