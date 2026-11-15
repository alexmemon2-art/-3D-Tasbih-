package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppSettings
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldLight

@Composable
fun TasbihHeader(
    settings: AppSettings,
    isOnline: Boolean = false,
    onToggleSound: () -> Unit,
    onToggleVibration: () -> Unit,
    onToggleLock: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(6.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0x36FFFFFF),
                        Color(0x1E0D4A2E),
                        Color(0x12071E13)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color(0x70FFFFFF), Color(0x55D4AF37), GlassBorder)
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🌿 3D تسبیح کاؤنٹر",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GoldBright,
                textAlign = TextAlign.Center,
                letterSpacing = 0.6.sp,
                modifier = Modifier.testTag("header_title")
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Allah Ka Zikr • سبحان الله",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GoldLight.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (isOnline) Color(0x3310B981) else Color(0x33F59E0B)
                        )
                        .border(
                            0.8.dp,
                            if (isOnline) Color(0xFF34D399) else GoldBright.copy(alpha = 0.6f),
                            CircleShape
                        )
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                        .testTag("network_status_badge")
                ) {
                    Text(
                        text = if (isOnline) "🌐 Online" else "📴 Offline Ready",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOnline) Color(0xFF6EE7B7) else GoldBright
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Quick Toggles: Sound, Vibration, Count Lock
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Glass3DCircleIconButton(
                        icon = if (settings.soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "Toggle Sound",
                        isActive = settings.soundEnabled,
                        onClick = onToggleSound,
                        testTag = "header_sound_toggle"
                    )

                    Glass3DCircleIconButton(
                        icon = Icons.Default.Vibration,
                        contentDescription = "Toggle Vibration",
                        isActive = settings.vibrationEnabled,
                        onClick = onToggleVibration,
                        testTag = "header_vibration_toggle"
                    )

                    Glass3DCircleIconButton(
                        icon = if (settings.countLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "Toggle Count Lock",
                        isActive = settings.countLocked,
                        onClick = onToggleLock,
                        testTag = "header_lock_toggle"
                    )
                }

                // Right Actions: Sindhi/Ramadan Calendar, Settings, Exit
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Glass3DCircleIconButton(
                        icon = Icons.Default.CalendarMonth,
                        contentDescription = "Open Islamic Sindhi & Ramadan Calendar",
                        isActive = false,
                        onClick = onOpenCalendar,
                        testTag = "header_calendar_button"
                    )

                    Glass3DCircleIconButton(
                        icon = Icons.Default.Settings,
                        contentDescription = "Open Settings",
                        isActive = false,
                        onClick = onOpenSettings,
                        testTag = "header_settings_button"
                    )

                    Glass3DCircleIconButton(
                        icon = Icons.Default.PowerSettingsNew,
                        contentDescription = "Close / Exit App",
                        isActive = false,
                        tint = Color(0xFFFF8A80),
                        borderColor = Color(0x66EF5350),
                        onClick = onOpenExit,
                        testTag = "header_exit_button"
                    )
                }
            }
        }
    }
}
