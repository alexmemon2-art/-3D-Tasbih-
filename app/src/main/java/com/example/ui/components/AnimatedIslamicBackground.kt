package com.example.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import com.example.model.IslamicWallpaper
import com.example.model.IslamicWallpapers
import kotlinx.coroutines.delay

@Composable
fun AnimatedIslamicBackground(
    modifier: Modifier = Modifier,
    autoChangeSeconds: Int = 30,
    currentWallpaperIndex: Int? = null,
    onWallpaperChanged: ((IslamicWallpaper) -> Unit)? = null
) {
    val wallpapers = IslamicWallpapers.list
    var internalIndex by remember { mutableIntStateOf(0) }

    val activeIndex = currentWallpaperIndex ?: internalIndex
    val currentWallpaper = wallpapers[activeIndex % wallpapers.size]

    // Automatic wallpaper rotation every 30 seconds
    LaunchedEffect(autoChangeSeconds) {
        while (true) {
            delay(autoChangeSeconds * 1000L)
            internalIndex = (internalIndex + 1) % wallpapers.size
            onWallpaperChanged?.invoke(wallpapers[internalIndex])
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("animated_islamic_background")
    ) {
        // Smooth cinematic Crossfade transition between wallpapers
        Crossfade(
            targetState = currentWallpaper,
            animationSpec = tween(durationMillis = 1800),
            label = "wallpaper_crossfade"
        ) { wallpaper ->
            Image(
                painter = painterResource(id = wallpaper.drawableRes),
                contentDescription = wallpaper.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Luxurious Dark Emerald Vignette Overlay (ensures 100% legibility of 3D beads & text)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xDD071E13), // Deep emerald-black top for title & icons
                            Color(0x990D4A2E), // Translucent emerald mid for 3D beads glow
                            Color(0xEE09291A), // Rich emerald base for counter display
                            Color(0xF805170E)  // Deep solid emerald-black for bottom stats & history
                        )
                    )
                )
        )
    }
}
