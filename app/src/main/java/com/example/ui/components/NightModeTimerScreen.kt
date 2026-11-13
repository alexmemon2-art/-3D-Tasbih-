package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NightModeLogic
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IvoryMuted
import com.example.ui.theme.IvorySubtle
import com.example.ui.theme.IvoryText
import com.example.widget.TasbihWidgetUpdater
import java.util.Calendar
import java.util.Locale

@Composable
fun NightModeTimerScreen(
    wallpaperIndex: Int,
    autoNightMode: Boolean,
    manualNightMode: Boolean,
    isNightModeEffective: Boolean,
    timerDurationMinutes: Int,
    timerRemainingMillis: Long,
    isTimerRunning: Boolean,
    isTimerCompleted: Boolean,
    onToggleAutoNightMode: (Boolean) -> Unit,
    onToggleManualNightMode: (Boolean) -> Unit,
    onSelectTimerMinutes: (Int) -> Unit,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onStopTimer: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val inAutoWindowNow = remember {
        NightModeLogic.isAutoNightWindow(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
    }

    val totalSeconds = ((timerRemainingMillis + 999L) / 1000L).coerceAtLeast(0L)
    val mins = (totalSeconds / 60L).toInt()
    val secs = (totalSeconds % 60L).toInt()
    val formattedTimer = if (isTimerCompleted) {
        "✅ Complete!"
    } else {
        String.format(Locale.US, "%02d:%02d", mins, secs)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("night_mode_timer_screen")
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
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Glass3DCircleIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    isActive = false,
                    onClick = onBack,
                    testTag = "night_timer_back_arrow"
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🌙 Night Mode & Timer",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldBright
                    )
                    Text(
                        text = "رات موڈ اور ذکر ٹائمر",
                        fontSize = 11.sp,
                        color = IvorySubtle
                    )
                }
                Spacer(modifier = Modifier.size(42.dp))
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .widthIn(max = 560.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Auto Night Mode Card
                item(key = "auto_night_card") {
                    NightModeSwitchCard(
                        title = "🌙 Auto Night Mode",
                        subtitle = if (inAutoWindowNow) {
                            "Raat 9 PM se subah 6 AM tak auto on • Active window"
                        } else {
                            "Raat 9 PM se subah 6 AM tak auto on • Standby"
                        },
                        checked = autoNightMode,
                        onCheckedChange = onToggleAutoNightMode,
                        testTag = "switch_auto_night_mode"
                    )
                }

                // Manual Night Mode Card
                item(key = "manual_night_card") {
                    NightModeSwitchCard(
                        title = "🌙 Abhi Night Mode On Karein",
                        subtitle = if (isNightModeEffective) {
                            "Night dimming active — aankhon ke liye aaramdeh dark screen"
                        } else {
                            "Raat ke zikr aur tahajjud ke liye soft dark screen"
                        },
                        checked = manualNightMode,
                        onCheckedChange = onToggleManualNightMode,
                        testTag = "switch_manual_night_mode"
                    )
                }

                // Dhikr Timer Card
                item(key = "dhikr_timer_card") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(6.dp, RoundedCornerShape(22.dp))
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0x36D4AF37), Color(0x1EFFFFFF), Color(0x100D4A2E))
                                )
                            )
                            .border(
                                1.2.dp,
                                Brush.verticalGradient(listOf(GoldBright.copy(alpha = 0.8f), GlassBorder)),
                                RoundedCornerShape(22.dp)
                            )
                            .padding(18.dp)
                            .testTag("dhikr_timer_card")
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "Dhikr Timer",
                                    tint = GoldBright,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "⏱️ Dhikr Timer",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldBright
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = formattedTimer,
                                fontSize = if (isTimerCompleted) 34.sp else 48.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isTimerCompleted) GoldBright else IvoryText,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.testTag("tv_timer_display")
                            )

                            Text(
                                text = when {
                                    isTimerCompleted -> "MashaAllah! Dhikr timer mukammal ho gaya."
                                    isTimerRunning -> "Timer chal raha hai — aap Wapas ja kar Tasbih bhi kar sakte hain"
                                    else -> "Apna pasandida waqt chunein aur zikr shuru karein"
                                },
                                fontSize = 11.sp,
                                color = IvorySubtle,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Duration Presets
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(5, 10, 15, 30).forEach { minutes ->
                                    val selected = timerDurationMinutes == minutes && !isTimerCompleted
                                    Glass3DPill(
                                        selected = selected,
                                        onClick = { onSelectTimerMinutes(minutes) },
                                        modifier = Modifier.weight(1f),
                                        testTag = "timer_preset_${minutes}m"
                                    ) {
                                        Text(
                                            text = "$minutes Min",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (selected) EmeraldDeepDark else IvoryText
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Start/Pause & Stop Controls
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Glass3DButton(
                                    onClick = {
                                        if (isTimerRunning) onPauseTimer() else onStartTimer()
                                    },
                                    modifier = Modifier.weight(1f),
                                    isPrimaryGold = true,
                                    height = 50.dp,
                                    testTag = "btn_start_pause_timer"
                                ) {
                                    Icon(
                                        imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isTimerRunning) "Pause Timer" else "Start Timer",
                                        tint = EmeraldDeepDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isTimerRunning) "Pause" else "▶️ Shuru Karein",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = EmeraldDeepDark
                                    )
                                }

                                Glass3DButton(
                                    onClick = onStopTimer,
                                    modifier = Modifier.weight(1f),
                                    isPrimaryGold = false,
                                    height = 50.dp,
                                    testTag = "btn_stop_timer"
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Stop,
                                        contentDescription = "Stop Timer",
                                        tint = Color(0xFFFF8A80),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "⏹️ Rok Dein",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFF8A80)
                                    )
                                }
                            }
                        }
                    }
                }

                // Quick Tasbih Widget Pin Card
                item(key = "quick_widget_card") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(18.dp))
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0x28FFFFFF), Color(0x14FFFFFF), Color(0x0AFFFFFF))
                                )
                            )
                            .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
                            .padding(14.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Widgets,
                                    contentDescription = "Quick Tasbih Widget",
                                    tint = GoldBright,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "📲 Quick Tasbih Home Widget",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                            }
                            Text(
                                text = "Bina app khole phone ki home screen par tap kar ke Tasbih ginti karein (app ke sath live sync).",
                                fontSize = 11.sp,
                                color = IvoryMuted,
                                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                            )
                            Glass3DButton(
                                onClick = {
                                    val requested = TasbihWidgetUpdater.requestPinQuickWidget(context)
                                    if (!requested) {
                                        Toast.makeText(
                                            context,
                                            "Home Screen ko long-press karein -> Widgets -> Quick Tasbih Widget",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                isPrimaryGold = false,
                                height = 46.dp,
                                testTag = "btn_pin_quick_widget"
                            ) {
                                Text(
                                    text = "📲 Add Quick Tasbih Widget to Home",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldBright
                                )
                            }
                        }
                    }
                }

                item(key = "night_timer_bottom_spacer") {
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            // Bottom Wapas Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Glass3DButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimaryGold = false,
                    height = 50.dp,
                    testTag = "night_timer_wapas_button"
                ) {
                    Text(
                        text = if (isTimerRunning) "📿 Wapas Counter Par Jayein" else "🔄 Wapas",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldBright
                    )
                }
            }
        }
    }
}

@Composable
private fun NightModeSwitchCard(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0x28FFFFFF), Color(0x14FFFFFF), Color(0x0AFFFFFF))
                )
            )
            .border(
                1.dp,
                if (checked) GoldBright.copy(alpha = 0.7f) else GlassBorder,
                RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.NightsStay,
                    contentDescription = title,
                    tint = if (checked) GoldBright else IvorySubtle,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldBright
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = IvorySubtle,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.testTag(testTag),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = GoldPrimary,
                    uncheckedThumbColor = IvorySubtle,
                    uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
                )
            )
        }
    }
}
