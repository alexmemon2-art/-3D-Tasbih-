package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.location.LiveLocationHelper
import com.example.model.AppSettings
import com.example.model.BeadStyle
import com.example.model.ClickSoundStyle
import com.example.model.CompletionSoundStyle
import com.example.model.IslamicWallpapers
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IvoryMuted
import com.example.ui.theme.IvorySubtle
import com.example.ui.theme.IvoryText
import com.example.widget.TasbihWidgetUpdater

@Composable
fun SettingsDialog(
    settings: AppSettings,
    currentWallpaperIndex: Int = 0,
    onNextWallpaper: () -> Unit = {},
    onSelectWallpaper: (Int) -> Unit = {},
    onOpenHistoryStats: () -> Unit = {},
    onOpenReminders: () -> Unit = {},
    onOpenPrayerQibla: () -> Unit = {},
    onOpenAsma99: () -> Unit = {},
    onOpenDuas: () -> Unit = {},
    onOpenDailyBaat: () -> Unit = {},
    onOpenHajatAzkar: () -> Unit = {},
    onOpenSpecialDhikr: () -> Unit = {},
    onOpenNamazGuide: () -> Unit = {},
    onOpenChallenges: () -> Unit = {},
    onOpenNightTimer: () -> Unit = {},
    onOpenVoiceSettings: () -> Unit = {},
    onDetectLiveLocation: () -> Unit = {},
    onSaveSettings: (AppSettings) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
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
            Toast.makeText(context, "⚠️ Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    var targetText by remember { mutableStateOf(settings.defaultTarget.toString()) }
    var dailyGoalText by remember { mutableStateOf(settings.dailyGoal.toString()) }
    var soundEnabled by remember { mutableStateOf(settings.soundEnabled) }
    var vibrationEnabled by remember { mutableStateOf(settings.vibrationEnabled) }
    var autoReset by remember { mutableStateOf(settings.autoReset) }
    var keepScreenOn by remember { mutableStateOf(settings.keepScreenOn) }
    var countLocked by remember { mutableStateOf(settings.countLocked) }
    var selectedBeadStyle by remember { mutableStateOf(settings.beadStyle) }
    var clickSoundStyle by remember { mutableStateOf(settings.clickSoundStyle) }
    var completionSoundStyle by remember { mutableStateOf(settings.completionSoundStyle) }

    val wallpapers = IslamicWallpapers.list
    val activeWallpaper = wallpapers[currentWallpaperIndex % wallpapers.size]

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = EmeraldDeepDark,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    listOf(Color(0x80FFFFFF), Color(0x66D4AF37), Color(0x20000000))
                )
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("settings_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚙️ Settings",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldBright
                    )
                    Text(
                        text = "ترتیبات",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Jump Feature Buttons (History, Reminders, Prayer/Qibla, 99 Names)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Glass3DButton(
                        onClick = onOpenHistoryStats,
                        modifier = Modifier.weight(1f),
                        isPrimaryGold = false,
                        height = 46.dp,
                        testTag = "settings_history_btn"
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "History & Stats",
                            tint = GoldBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "History",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldBright
                        )
                    }

                    Glass3DButton(
                        onClick = onOpenReminders,
                        modifier = Modifier.weight(1f),
                        isPrimaryGold = false,
                        height = 46.dp,
                        testTag = "settings_reminder_btn"
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Reminders",
                            tint = GoldBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Reminders",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldBright
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Glass3DButton(
                        onClick = onOpenPrayerQibla,
                        modifier = Modifier.weight(1f),
                        isPrimaryGold = false,
                        height = 46.dp,
                        testTag = "settings_prayer_btn"
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Prayer & Qibla",
                            tint = GoldBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Qibla & Namaz",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldBright
                        )
                    }

                    Glass3DButton(
                        onClick = onOpenAsma99,
                        modifier = Modifier.weight(1f),
                        isPrimaryGold = false,
                        height = 46.dp,
                        testTag = "settings_asma_btn"
                    ) {
                        Text(
                            text = "🕋 99 Names",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldBright
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Glass3DButton(
                    onClick = onOpenDuas,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimaryGold = false,
                    height = 46.dp,
                    testTag = "settings_duas_btn"
                ) {
                    Text(
                        text = "🤲 Masnoon Duas Collection",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldBright
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Glass3DButton(
                        onClick = onOpenDailyBaat,
                        modifier = Modifier.weight(1f),
                        isPrimaryGold = false,
                        height = 46.dp,
                        testTag = "settings_daily_baat_btn"
                    ) {
                        Text(
                            text = "☀️ Aaj ki Baat",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldBright
                        )
                    }

                    Glass3DButton(
                        onClick = onOpenHajatAzkar,
                        modifier = Modifier.weight(1f),
                        isPrimaryGold = false,
                        height = 46.dp,
                        testTag = "settings_hajat_btn"
                    ) {
                        Text(
                            text = "🤲 Hajat Azkar",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldBright
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Glass3DButton(
                    onClick = onOpenSpecialDhikr,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimaryGold = false,
                    height = 46.dp,
                    testTag = "settings_special_dhikr_btn"
                ) {
                    Text(
                        text = "📿 Tasbih-e-Fatima & Special Dhikrs",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldBright
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Glass3DButton(
                    onClick = onOpenNamazGuide,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimaryGold = false,
                    height = 46.dp,
                    testTag = "settings_namaz_guide_btn"
                ) {
                    Text(
                        text = "🕌 Namaz Parhne Ka Tareeqa (Guide)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldBright
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Glass3DButton(
                    onClick = onOpenChallenges,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimaryGold = false,
                    height = 46.dp,
                    testTag = "settings_challenges_btn"
                ) {
                    Text(
                        text = "🎯 Weekly & Daily Dhikr Challenges",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldBright
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Glass3DButton(
                    onClick = onOpenNightTimer,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimaryGold = false,
                    height = 46.dp,
                    testTag = "settings_night_timer_btn"
                ) {
                    Text(
                        text = "🌙 Auto Night Mode & Dhikr Timer",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldBright
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Glass3DButton(
                    onClick = onOpenVoiceSettings,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimaryGold = false,
                    height = 46.dp,
                    testTag = "settings_voice_btn"
                ) {
                    Text(
                        text = "🎙️ Awaz Ka Intikhab (Online & Offline)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldBright
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Glass3DButton(
                    onClick = {
                        if (!hasLocationPermission) {
                            locationPermissionLauncher.launch(LiveLocationHelper.REQUIRED_PERMISSIONS)
                        } else {
                            onDetectLiveLocation()
                            Toast.makeText(context, "📍 Live Location Update Ho Rahi Hai...", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimaryGold = !hasLocationPermission,
                    height = 46.dp,
                    testTag = "settings_location_permission_btn"
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location Permission",
                        tint = if (!hasLocationPermission) EmeraldDeepDark else GoldBright,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (hasLocationPermission) {
                            "📍 Live Location Enabled (Tap to Refresh GPS)"
                        } else {
                            "🔓 Enable Location Permission (Live GPS)"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!hasLocationPermission) EmeraldDeepDark else GoldBright
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = GlassBorder, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // Default Target count
                Text(
                    text = "Default Target Count",
                    fontSize = 13.sp,
                    color = IvorySubtle,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = targetText,
                    onValueChange = { targetText = it.filter { char -> char.isDigit() }.take(4) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("target_input_field"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldBright,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = IvoryText,
                        unfocusedTextColor = IvoryText,
                        cursorColor = GoldBright,
                        focusedContainerColor = Color(0x18000000),
                        unfocusedContainerColor = Color(0x18000000)
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(33, 99, 100, 500, 1000).forEach { preset ->
                        val isSelected = targetText == preset.toString()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) GoldBright else Color.White.copy(alpha = 0.12f)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) Color.White else GlassBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { targetText = preset.toString() }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = preset.toString(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) EmeraldDeepDark else IvoryText
                            )
                        }
                    }
                }

                // Daily Goal setting
                Text(
                    text = "Daily Goal (Rozana Hadaf)",
                    fontSize = 13.sp,
                    color = IvorySubtle,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = dailyGoalText,
                    onValueChange = { dailyGoalText = it.filter { char -> char.isDigit() }.take(5) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldBright,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = IvoryText,
                        unfocusedTextColor = IvoryText,
                        cursorColor = GoldBright,
                        focusedContainerColor = Color(0x18000000),
                        unfocusedContainerColor = Color(0x18000000)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = GlassBorder, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(10.dp))

                // 3D Bead Appearance Styles
                Text(
                    text = "3D Bead Appearance",
                    fontSize = 13.sp,
                    color = IvorySubtle,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BeadStyle.values().forEach { style ->
                        val isSelected = style == selectedBeadStyle
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) GoldPrimary.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f)
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.8.dp,
                                    color = if (isSelected) GoldBright else GlassBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedBeadStyle = style }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color(style.primaryHex))
                                        .border(1.dp, Color(style.accentHex), CircleShape)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = style.displayName.split(" ").first(),
                                    fontSize = 10.sp,
                                    color = if (isSelected) GoldBright else IvorySubtle,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Sound Options
                Text(
                    text = "🔊 Click Sound Style",
                    fontSize = 13.sp,
                    color = IvorySubtle,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    items(ClickSoundStyle.values()) { style ->
                        Glass3DPill(
                            selected = style == clickSoundStyle,
                            onClick = { clickSoundStyle = style }
                        ) {
                            Text(
                                text = style.displayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (style == clickSoundStyle) EmeraldDeepDark else IvoryText
                            )
                        }
                    }
                }

                Text(
                    text = "🔔 Target Completion Voice / Sound",
                    fontSize = 13.sp,
                    color = IvorySubtle,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    items(CompletionSoundStyle.values()) { style ->
                        Glass3DPill(
                            selected = style == completionSoundStyle,
                            onClick = { completionSoundStyle = style }
                        ) {
                            Text(
                                text = style.displayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (style == completionSoundStyle) EmeraldDeepDark else IvoryText
                            )
                        }
                    }
                }

                HorizontalDivider(color = GlassBorder, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // Core Toggles (Sound, Vibration, Lock, Auto-reset, Screen On)
                SettingSwitchRow(
                    label = "🔊 Sound On/Off",
                    checked = soundEnabled,
                    onCheckedChange = { soundEnabled = it },
                    testTag = "sound_toggle"
                )
                SettingSwitchRow(
                    label = "📳 Vibration On/Off",
                    checked = vibrationEnabled,
                    onCheckedChange = { vibrationEnabled = it },
                    testTag = "vibration_toggle"
                )
                SettingSwitchRow(
                    label = "🔒 Count Lock (Protect Reset)",
                    checked = countLocked,
                    onCheckedChange = { countLocked = it },
                    testTag = "count_lock_toggle"
                )
                SettingSwitchRow(
                    label = "Auto-reset on Target",
                    checked = autoReset,
                    onCheckedChange = { autoReset = it },
                    testTag = "autoreset_toggle"
                )
                SettingSwitchRow(
                    label = "Keep Screen Awake",
                    checked = keepScreenOn,
                    onCheckedChange = { keepScreenOn = it },
                    testTag = "screen_awake_toggle"
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = GlassBorder, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // Home Screen Widget Controls (Small & Large Options)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Widgets,
                                contentDescription = "Home Screen Widgets",
                                tint = GoldBright,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "📱 Home Screen Widgets",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        }
                        Text(
                            text = "Tap to pin Small (2x1) or Large (4x2) Tasbih counter directly on your Home Screen.",
                            fontSize = 11.sp,
                            color = IvorySubtle,
                            modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Glass3DPill(
                                selected = false,
                                onClick = {
                                    val requested = TasbihWidgetUpdater.requestPinWidget(context, isLarge = false)
                                    if (!requested) {
                                        Toast.makeText(
                                            context,
                                            "Long-press Home Screen -> Widgets -> Tasbih Compact Widget",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                testTag = "pin_small_widget_button"
                            ) {
                                Text("Compact", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldBright)
                            }
                            Glass3DPill(
                                selected = false,
                                onClick = {
                                    val requested = TasbihWidgetUpdater.requestPinQuickWidget(context)
                                    if (!requested) {
                                        Toast.makeText(
                                            context,
                                            "Long-press Home Screen -> Widgets -> Quick Tasbih Widget",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                testTag = "pin_quick_widget_button"
                            ) {
                                Text("Quick 2x2", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldBright)
                            }
                            Glass3DPill(
                                selected = true,
                                onClick = {
                                    val requested = TasbihWidgetUpdater.requestPinWidget(context, isLarge = true)
                                    if (!requested) {
                                        Toast.makeText(
                                            context,
                                            "Long-press Home Screen -> Widgets -> Tasbih Detailed Widget",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                testTag = "pin_large_widget_button"
                            ) {
                                Text("Large", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldDeepDark)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 30s Islamic Wallpapers Rotation & Switcher
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(18.dp))
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x35FFFFFF), Color(0x18FFFFFF), Color(0x0CFFFFFF))
                            )
                        )
                        .border(
                            1.2.dp,
                            Brush.verticalGradient(
                                listOf(Color(0x80FFFFFF), Color(0x50D4AF37), Color(0x20000000))
                            ),
                            RoundedCornerShape(18.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Islamic Wallpapers",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight,
                                letterSpacing = 0.5.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(GoldPrimary.copy(alpha = 0.2f))
                                    .border(1.dp, GoldBright.copy(alpha = 0.5f), CircleShape)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Auto 30s",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldBright
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = activeWallpaper.arabicName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldBright,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "${activeWallpaper.name} (${(currentWallpaperIndex % wallpapers.size) + 1}/10)",
                            fontSize = 11.sp,
                            color = IvorySubtle,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                        )

                        Glass3DButton(
                            onClick = onNextWallpaper,
                            modifier = Modifier.fillMaxWidth(),
                            isPrimaryGold = false,
                            height = 46.dp,
                            testTag = "settings_next_wallpaper_button"
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Next Wallpaper",
                                tint = GoldBright,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Next Wallpaper Now (اگلا وال پیپر)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldBright
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(wallpapers) { index, wallpaper ->
                                val isSelected = index == (currentWallpaperIndex % wallpapers.size)
                                Box(
                                    modifier = Modifier
                                        .size(width = 46.dp, height = 46.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            width = if (isSelected) 2.dp else 0.8.dp,
                                            color = if (isSelected) GoldBright else GlassBorder,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable { onSelectWallpaper(index) }
                                ) {
                                    Image(
                                        painter = painterResource(id = wallpaper.drawableRes),
                                        contentDescription = wallpaper.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Developer Branding & One-Tap Direct Dialer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(18.dp))
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x35FFFFFF), Color(0x18FFFFFF), Color(0x0CFFFFFF))
                            )
                        )
                        .border(
                            1.2.dp,
                            Brush.verticalGradient(
                                listOf(Color(0x80FFFFFF), Color(0x50D4AF37), Color(0x20000000))
                            ),
                            RoundedCornerShape(18.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Developer & Support • V.${com.example.BuildConfig.VERSION_NAME}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Developer: Usama Memon",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = IvoryText
                        )
                        Text(
                            text = "For feedback, custom Azkar or assistance",
                            fontSize = 11.sp,
                            color = IvoryMuted,
                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                        )

                        Glass3DButton(
                            onClick = {
                                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:03103205422")
                                }
                                context.startActivity(dialIntent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            isPrimaryGold = true,
                            height = 48.dp,
                            testTag = "settings_developer_dialer_button"
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Call Usama Memon",
                                tint = EmeraldDeepDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Call Developer: 0310-3205422",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDeepDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Glass3DButton(
                    onClick = {
                        val parsedTarget = targetText.toIntOrNull() ?: settings.defaultTarget
                        val validTarget = parsedTarget.coerceIn(1, 10000)
                        val parsedGoal = dailyGoalText.toIntOrNull() ?: settings.dailyGoal
                        val validGoal = parsedGoal.coerceIn(10, 50000)
                        onSaveSettings(
                            settings.copy(
                                defaultTarget = validTarget,
                                dailyGoal = validGoal,
                                soundEnabled = soundEnabled,
                                vibrationEnabled = vibrationEnabled,
                                autoReset = autoReset,
                                keepScreenOn = keepScreenOn,
                                countLocked = countLocked,
                                beadStyle = selectedBeadStyle,
                                clickSoundStyle = clickSoundStyle,
                                completionSoundStyle = completionSoundStyle
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimaryGold = true,
                    shape = RoundedCornerShape(14.dp),
                    height = 50.dp,
                    testTag = "save_settings_button"
                ) {
                    Text(
                        text = "✅ Save Settings",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldDeepDark
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
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
