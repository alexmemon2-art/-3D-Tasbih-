package com.example.ui

import android.app.Activity
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.calendar.IslamicCalendarEngine
import com.example.ui.components.AddDhikrDialog
import com.example.ui.components.AnimatedIslamicBackground
import com.example.ui.components.AsmaUlHusnaScreen
import com.example.ui.components.CalendarTab
import com.example.ui.components.CompletionDialog
import com.example.ui.components.CounterDisplay
import com.example.ui.components.DailyReminderScreen
import com.example.ui.components.DhikrFazilatScreen
import com.example.ui.components.DhikrSelector
import com.example.ui.components.ExitConfirmScreen
import com.example.ui.components.Glass3DButton
import com.example.ui.components.Glass3DPill
import com.example.ui.components.HajatAzkarScreen
import com.example.ui.components.HistorySection
import com.example.ui.components.HistoryStatsScreen
import com.example.ui.components.IslamicCalendarScreen
import com.example.ui.components.MasnoonDuasScreen
import com.example.ui.components.NamazGuideScreen
import com.example.ui.components.PrayerQiblaScreen
import com.example.ui.components.SettingsDialog
import com.example.ui.components.SpecialDhikrScreen
import com.example.ui.components.StartSplashScreen
import com.example.ui.components.StatsCards
import com.example.ui.components.Tasbih3DCanvas
import com.example.ui.components.TasbihHeader
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldLight
import com.example.ui.theme.IvorySubtle
import com.example.ui.theme.IvoryText
import com.example.viewmodel.SubScreen
import com.example.viewmodel.TasbihViewModel

@Composable
fun TasbihScreen(
    viewModel: TasbihViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    // Handle Keep Screen Awake preference
    DisposableEffect(uiState.settings.keepScreenOn) {
        if (uiState.settings.keepScreenOn) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Android System Back Navigation Handler
    BackHandler {
        when {
            uiState.showExitScreen -> viewModel.showExit(false)
            uiState.activeSubScreen != null -> viewModel.closeSubScreen()
            uiState.showCalendarScreen -> viewModel.closeCalendar()
            uiState.showStartingScreen -> activity?.finish()
            uiState.showSettingsDialog -> viewModel.showSettings(false)
            uiState.showAddDhikrDialog -> viewModel.showAddDhikr(false)
            uiState.showCompletionDialog -> viewModel.dismissCompletionDialog()
            else -> viewModel.showExit(true)
        }
    }

    val currentMode = when {
        uiState.showExitScreen -> ScreenMode.EXIT
        uiState.activeSubScreen == SubScreen.HISTORY_STATS -> ScreenMode.HISTORY_STATS
        uiState.activeSubScreen == SubScreen.PRAYER_QIBLA -> ScreenMode.PRAYER_QIBLA
        uiState.activeSubScreen == SubScreen.ASMA_99 -> ScreenMode.ASMA_99
        uiState.activeSubScreen == SubScreen.FAZILAT -> ScreenMode.FAZILAT
        uiState.activeSubScreen == SubScreen.MASNOON_DUAS -> ScreenMode.MASNOON_DUAS
        uiState.activeSubScreen == SubScreen.DAILY_REMINDER -> ScreenMode.DAILY_REMINDER
        uiState.activeSubScreen == SubScreen.HAJAT_AZKAR -> ScreenMode.HAJAT_AZKAR
        uiState.activeSubScreen == SubScreen.SPECIAL_DHIKR -> ScreenMode.SPECIAL_DHIKR
        uiState.activeSubScreen == SubScreen.NAMAZ_GUIDE -> ScreenMode.NAMAZ_GUIDE
        uiState.showCalendarScreen -> ScreenMode.CALENDAR
        uiState.showStartingScreen -> ScreenMode.STARTING
        else -> ScreenMode.MAIN
    }

    AnimatedContent(
        targetState = currentMode,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_mode_transition"
    ) { mode ->
        when (mode) {
            ScreenMode.STARTING -> {
                StartSplashScreen(
                    onStartClick = { viewModel.dismissStartingScreen() }
                )
            }
            ScreenMode.CALENDAR -> {
                IslamicCalendarScreen(
                    wallpaperIndex = uiState.wallpaperIndex,
                    hilalOffsetDays = uiState.hilalOffsetDays,
                    selectedCityName = uiState.selectedCityName,
                    initialTab = uiState.calendarInitialTab,
                    onHilalOffsetChange = { viewModel.setHilalOffset(it) },
                    onCityChange = { viewModel.setSelectedCity(it) },
                    onBack = { viewModel.closeCalendar() }
                )
            }
            ScreenMode.HISTORY_STATS -> {
                HistoryStatsScreen(
                    wallpaperIndex = uiState.wallpaperIndex,
                    todayCount = uiState.todayCount,
                    yesterdayCount = uiState.yesterdayCount,
                    weekCount = uiState.weekCount,
                    streakDays = uiState.streakDays,
                    totalAllTime = uiState.totalCount,
                    dailyGoal = uiState.settings.dailyGoal,
                    levelInfo = uiState.levelInfo,
                    weeklyBreakdown = uiState.weeklyBreakdown,
                    dhikrs = uiState.dhikrs,
                    dhikrLifetimes = uiState.dhikrLifetimes,
                    recentHistory = uiState.history,
                    onUpdateDailyGoal = { viewModel.updateDailyGoal(it) },
                    onClearHistory = { viewModel.clearHistory() },
                    onBack = { viewModel.closeSubScreen() }
                )
            }
            ScreenMode.PRAYER_QIBLA -> {
                PrayerQiblaScreen(
                    wallpaperIndex = uiState.wallpaperIndex,
                    selectedCityName = uiState.selectedCityName,
                    reminderSettings = uiState.reminderSettings,
                    initialTab = uiState.prayerQiblaInitialTab,
                    onCityChange = { viewModel.setSelectedCity(it) },
                    onSaveReminders = { viewModel.updateReminderSettings(it) },
                    onBack = { viewModel.closeSubScreen() }
                )
            }
            ScreenMode.ASMA_99 -> {
                AsmaUlHusnaScreen(
                    wallpaperIndex = uiState.wallpaperIndex,
                    onSelectNameForTasbih = { asma, target -> viewModel.selectAsmaAsDhikr(asma, target) },
                    onBack = { viewModel.closeSubScreen() }
                )
            }
            ScreenMode.FAZILAT -> {
                DhikrFazilatScreen(
                    wallpaperIndex = uiState.wallpaperIndex,
                    dhikrs = uiState.dhikrs,
                    currentDhikr = uiState.currentDhikr,
                    onSelectAndCount = {
                        viewModel.selectDhikr(it)
                        viewModel.closeSubScreen()
                    },
                    onBack = { viewModel.closeSubScreen() }
                )
            }
            ScreenMode.MASNOON_DUAS -> {
                MasnoonDuasScreen(
                    wallpaperIndex = uiState.wallpaperIndex,
                    onReciteAsTasbih = { viewModel.selectDuaAsDhikr(it) },
                    onBack = { viewModel.closeSubScreen() }
                )
            }
            ScreenMode.DAILY_REMINDER -> {
                DailyReminderScreen(
                    wallpaperIndex = uiState.wallpaperIndex,
                    onBack = { viewModel.closeSubScreen() }
                )
            }
            ScreenMode.HAJAT_AZKAR -> {
                HajatAzkarScreen(
                    wallpaperIndex = uiState.wallpaperIndex,
                    onStartZikr = { viewModel.selectHajatAsDhikr(it) },
                    onBack = { viewModel.closeSubScreen() }
                )
            }
            ScreenMode.SPECIAL_DHIKR -> {
                SpecialDhikrScreen(
                    wallpaperIndex = uiState.wallpaperIndex,
                    tasbihFatimaTotalCount = uiState.tasbihFatimaCount,
                    onTapTasbihFatima = { viewModel.incrementTasbihFatima() },
                    onResetTasbihFatima = { viewModel.resetTasbihFatima() },
                    onSelectSpecialDhikr = { viewModel.selectSpecialDhikr(it) },
                    onBack = { viewModel.closeSubScreen() }
                )
            }
            ScreenMode.NAMAZ_GUIDE -> {
                NamazGuideScreen(
                    wallpaperIndex = uiState.wallpaperIndex,
                    onOpenQiblaCompass = { viewModel.openSubScreen(SubScreen.PRAYER_QIBLA, prayerTab = 0) },
                    onBack = { viewModel.closeSubScreen() }
                )
            }
            ScreenMode.EXIT -> {
                ExitConfirmScreen(
                    sessionCount = uiState.sessionCount,
                    todayCount = uiState.todayCount,
                    completedRounds = uiState.completedRounds,
                    onCancelExit = { viewModel.showExit(false) },
                    onConfirmExit = { activity?.finish() }
                )
            }
            ScreenMode.MAIN -> {
                Scaffold(
                    modifier = modifier.fillMaxSize(),
                    containerColor = Color.Transparent
                ) { innerPadding ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        AnimatedIslamicBackground(
                            autoChangeSeconds = 30,
                            currentWallpaperIndex = uiState.wallpaperIndex
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .widthIn(max = 560.dp)
                                    .verticalScroll(rememberScrollState())
                                    .padding(bottom = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top
                            ) {
                                TasbihHeader(
                                    settings = uiState.settings,
                                    onToggleSound = {
                                        viewModel.updateSettings(
                                            uiState.settings.copy(soundEnabled = !uiState.settings.soundEnabled)
                                        )
                                    },
                                    onToggleVibration = {
                                        viewModel.updateSettings(
                                            uiState.settings.copy(vibrationEnabled = !uiState.settings.vibrationEnabled)
                                        )
                                    },
                                    onToggleLock = { viewModel.toggleCountLock() },
                                    onOpenCalendar = { viewModel.openCalendar(CalendarTab.SINDHI_HIJRI) },
                                    onOpenSettings = { viewModel.showSettings(true) },
                                    onOpenExit = { viewModel.showExit(true) }
                                )

                                // 3D Interactive Rotating Tasbih Beads Ring
                                Tasbih3DCanvas(
                                    count = uiState.count,
                                    target = uiState.currentDhikr.target,
                                    beadStyle = uiState.settings.beadStyle,
                                    onIncrement = { viewModel.increment() }
                                )

                                // Dhikr Selection Bar
                                DhikrSelector(
                                    dhikrs = uiState.dhikrs,
                                    selectedDhikr = uiState.currentDhikr,
                                    onSelectDhikr = { viewModel.selectDhikr(it) },
                                    onAddCustomClick = { viewModel.showAddDhikr(true) },
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Counter Box (with Count Lock & Meaning/Fazilat shortcut)
                                CounterDisplay(
                                    count = uiState.count,
                                    dhikr = uiState.currentDhikr,
                                    countLocked = uiState.settings.countLocked,
                                    volumeKeyEnabled = uiState.settings.volumeKeyCount,
                                    onIncrement = { viewModel.increment() },
                                    onReset = { viewModel.reset() },
                                    onUndo = { viewModel.undo() },
                                    onToggleLock = { viewModel.toggleCountLock() },
                                    onOpenFazilat = { viewModel.openSubScreen(SubScreen.FAZILAT) },
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Islamic Features Hub Card (Stats/Goals, Prayer/Qibla, Reminders, 99 Names, Duas, Aaj ki Baat, Hajat, Special Dhikr, Fazilat)
                                IslamicToolsHubCard(
                                    onOpenStats = { viewModel.openSubScreen(SubScreen.HISTORY_STATS) },
                                    onOpenPrayerQibla = { viewModel.openSubScreen(SubScreen.PRAYER_QIBLA, prayerTab = 0) },
                                    onOpenReminders = { viewModel.openSubScreen(SubScreen.PRAYER_QIBLA, prayerTab = 1) },
                                    onOpenAsma99 = { viewModel.openSubScreen(SubScreen.ASMA_99) },
                                    onOpenDuas = { viewModel.openSubScreen(SubScreen.MASNOON_DUAS) },
                                    onOpenFazilat = { viewModel.openSubScreen(SubScreen.FAZILAT) },
                                    onOpenDailyBaat = { viewModel.openSubScreen(SubScreen.DAILY_REMINDER) },
                                    onOpenHajatAzkar = { viewModel.openSubScreen(SubScreen.HAJAT_AZKAR) },
                                    onOpenSpecialDhikr = { viewModel.openSubScreen(SubScreen.SPECIAL_DHIKR) },
                                    onOpenNamazGuide = { viewModel.openSubScreen(SubScreen.NAMAZ_GUIDE) },
                                    onOpenSettings = { viewModel.showSettings(true) },
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Daily Goal, Level, Streak & Today/Yesterday/Week/Lifetime Stats
                                StatsCards(
                                    todayCount = uiState.todayCount,
                                    yesterdayCount = uiState.yesterdayCount,
                                    weekCount = uiState.weekCount,
                                    streakDays = uiState.streakDays,
                                    totalAllTime = uiState.totalCount,
                                    dailyGoal = uiState.settings.dailyGoal,
                                    levelInfo = uiState.levelInfo,
                                    onOpenFullStats = { viewModel.openSubScreen(SubScreen.HISTORY_STATS) },
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Islamic Sindhi Calendar & Ramadan Calendar Quick-Access Card
                                CalendarQuickAccessCard(
                                    hilalOffsetDays = uiState.hilalOffsetDays,
                                    onOpenSindhiCalendar = { viewModel.openCalendar(CalendarTab.SINDHI_HIJRI) },
                                    onOpenRamadanCalendar = { viewModel.openCalendar(CalendarTab.RAMADAN) },
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                HistorySection(
                                    history = uiState.history,
                                    onClearHistory = { viewModel.clearHistory() },
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                // Designer Footer
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(140.dp)
                                            .height(1.dp)
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(Color.Transparent, GoldBright.copy(alpha = 0.7f), Color.Transparent)
                                                )
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "✨ Designed by: Usama Memon ✨",
                                        fontSize = 14.sp,
                                        fontStyle = FontStyle.Italic,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldBright,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "3D Tasbih App • V.${BuildConfig.VERSION_NAME}",
                                        fontSize = 11.sp,
                                        color = IvorySubtle,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Modals / Dialogs
                    if (uiState.showSettingsDialog) {
                        SettingsDialog(
                            settings = uiState.settings,
                            currentWallpaperIndex = uiState.wallpaperIndex,
                            onNextWallpaper = { viewModel.nextWallpaper() },
                            onSelectWallpaper = { viewModel.setWallpaperIndex(it) },
                            onOpenHistoryStats = { viewModel.openSubScreen(SubScreen.HISTORY_STATS) },
                            onOpenReminders = { viewModel.openSubScreen(SubScreen.PRAYER_QIBLA, prayerTab = 1) },
                            onOpenPrayerQibla = { viewModel.openSubScreen(SubScreen.PRAYER_QIBLA, prayerTab = 0) },
                            onOpenAsma99 = { viewModel.openSubScreen(SubScreen.ASMA_99) },
                            onOpenDuas = { viewModel.openSubScreen(SubScreen.MASNOON_DUAS) },
                            onOpenDailyBaat = { viewModel.openSubScreen(SubScreen.DAILY_REMINDER) },
                            onOpenHajatAzkar = { viewModel.openSubScreen(SubScreen.HAJAT_AZKAR) },
                            onOpenSpecialDhikr = { viewModel.openSubScreen(SubScreen.SPECIAL_DHIKR) },
                            onOpenNamazGuide = { viewModel.openSubScreen(SubScreen.NAMAZ_GUIDE) },
                            onSaveSettings = { viewModel.updateSettings(it) },
                            onDismiss = { viewModel.showSettings(false) }
                        )
                    }

                    if (uiState.showCompletionDialog) {
                        CompletionDialog(
                            dhikr = uiState.currentDhikr,
                            targetCount = uiState.currentDhikr.target,
                            onContinue = { viewModel.dismissCompletionDialog() },
                            onNextDhikr = { viewModel.nextDhikr() }
                        )
                    }

                    if (uiState.showAddDhikrDialog) {
                        AddDhikrDialog(
                            onAddDhikr = { viewModel.addCustomDhikr(it) },
                            onDismiss = { viewModel.showAddDhikr(false) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IslamicToolsHubCard(
    onOpenStats: () -> Unit,
    onOpenPrayerQibla: () -> Unit,
    onOpenReminders: () -> Unit,
    onOpenAsma99: () -> Unit,
    onOpenDuas: () -> Unit,
    onOpenFazilat: () -> Unit,
    onOpenDailyBaat: () -> Unit,
    onOpenHajatAzkar: () -> Unit,
    onOpenSpecialDhikr: () -> Unit,
    onOpenNamazGuide: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0x32FFFFFF), Color(0x18FFFFFF), Color(0x0E071E13))
                )
            )
            .border(
                1.2.dp,
                Brush.verticalGradient(listOf(Color(0x70FFFFFF), Color(0x50D4AF37), GlassBorder)),
                RoundedCornerShape(24.dp)
            )
            .padding(16.dp)
            .testTag("home_islamic_hub_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✨ Islamic Glass Menu & Tools",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GoldBright
                )
                Text(
                    text = "اسلامی فیچرز",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldLight
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Glass3DButton(
                    onClick = onOpenFazilat,
                    modifier = Modifier.weight(1f),
                    isPrimaryGold = false,
                    shape = RoundedCornerShape(18.dp),
                    testTag = "hub_fazilat_button"
                ) {
                    Text("📿 Tasbih List", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IvoryText)
                }

                Glass3DButton(
                    onClick = onOpenNamazGuide,
                    modifier = Modifier.weight(1f),
                    isPrimaryGold = true,
                    shape = RoundedCornerShape(18.dp),
                    testTag = "hub_namaz_guide_button"
                ) {
                    Text("🕌 Namaz Guide", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldDeepDark)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Glass3DButton(
                    onClick = onOpenDuas,
                    modifier = Modifier.weight(1f),
                    isPrimaryGold = false,
                    shape = RoundedCornerShape(18.dp),
                    testTag = "hub_duas_button"
                ) {
                    Text("🤲 Masnoon Duas", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldLight)
                }

                Glass3DButton(
                    onClick = onOpenAsma99,
                    modifier = Modifier.weight(1f),
                    isPrimaryGold = true,
                    shape = RoundedCornerShape(18.dp),
                    testTag = "hub_asma99_button"
                ) {
                    Text("🕋 99 Names", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldDeepDark)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Glass3DButton(
                    onClick = onOpenDailyBaat,
                    modifier = Modifier.weight(1f),
                    isPrimaryGold = false,
                    shape = RoundedCornerShape(18.dp),
                    testTag = "hub_daily_baat_button"
                ) {
                    Text("📖 Aaj ki Baat", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldBright)
                }

                Glass3DButton(
                    onClick = onOpenStats,
                    modifier = Modifier.weight(1f),
                    isPrimaryGold = false,
                    shape = RoundedCornerShape(18.dp),
                    testTag = "hub_stats_button"
                ) {
                    Text("📊 History & Goals", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IvoryText)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Glass3DButton(
                    onClick = onOpenPrayerQibla,
                    modifier = Modifier.weight(1f),
                    isPrimaryGold = false,
                    shape = RoundedCornerShape(18.dp),
                    testTag = "hub_prayer_qibla_button"
                ) {
                    Text("🧭 Namaz & Qibla", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IvoryText)
                }

                Glass3DButton(
                    onClick = onOpenReminders,
                    modifier = Modifier.weight(1f),
                    isPrimaryGold = false,
                    shape = RoundedCornerShape(18.dp),
                    testTag = "hub_reminders_button"
                ) {
                    Text("⏰ Reminders", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IvoryText)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                com.example.ui.components.Glass3DButton(
                    onClick = onOpenHajatAzkar,
                    modifier = Modifier.weight(1f),
                    isPrimaryGold = false,
                    shape = RoundedCornerShape(18.dp),
                    testTag = "hub_hajat_azkar_button"
                ) {
                    Text("🤲 Hajat Azkar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IvoryText)
                }

                com.example.ui.components.Glass3DButton(
                    onClick = onOpenSpecialDhikr,
                    modifier = Modifier.weight(1f),
                    isPrimaryGold = false,
                    shape = RoundedCornerShape(18.dp),
                    testTag = "hub_special_dhikr_button"
                ) {
                    Text("📿 Tasbih-e-Fatima", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldLight)
                }
            }

            com.example.ui.components.Glass3DButton(
                onClick = onOpenSettings,
                modifier = Modifier.fillMaxWidth(),
                isPrimaryGold = false,
                shape = RoundedCornerShape(18.dp),
                testTag = "hub_settings_button"
            ) {
                Text("⚙️ Settings & Themes (ترتیبات)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldBright)
            }
        }
    }
}

@Composable
private fun CalendarQuickAccessCard(
    hilalOffsetDays: Int,
    onOpenSindhiCalendar: () -> Unit,
    onOpenRamadanCalendar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val todayHijri = remember(hilalOffsetDays) {
        IslamicCalendarEngine.getTodayHijri(hilalOffsetDays)
    }
    val todayGreg = remember { IslamicCalendarEngine.getTodayGregorian() }
    val todaySindhiMonth = IslamicCalendarEngine.sindhiMonths.getOrElse(todayHijri.month - 1) { "" }
    val nextRamadanYear = remember(hilalOffsetDays) {
        IslamicCalendarEngine.getDefaultRamadanYear(hilalOffsetDays)
    }
    val daysToRamadan = remember(nextRamadanYear, hilalOffsetDays) {
        IslamicCalendarEngine.daysUntilRamadan(nextRamadanYear, hilalOffsetDays)
    }

    val ramadanSummary = when {
        daysToRamadan > 0 -> "Ramadan $nextRamadanYear AH in $daysToRamadan days"
        daysToRamadan in -29..0 -> "Ramadan Day ${1 - daysToRamadan} ($nextRamadanYear AH)"
        else -> "Ramadan $nextRamadanYear AH Schedule"
    }

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
            .clickable { onOpenSindhiCalendar() }
            .padding(16.dp)
            .testTag("home_calendar_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "اسلامي سنڌي ۽ رمضان ڪئلينڊر",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${todayHijri.day} $todaySindhiMonth ${todayHijri.year} ھ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldBright
                    )
                    Text(
                        text = "${IslamicCalendarEngine.formatGregorianFull(todayGreg)} • $ramadanSummary",
                        fontSize = 11.sp,
                        color = IvorySubtle
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Glass3DPill(
                    selected = false,
                    onClick = onOpenSindhiCalendar,
                    modifier = Modifier.weight(1f),
                    testTag = "home_open_sindhi_calendar"
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Open Sindhi Calendar",
                        tint = GoldBright,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "سنڌي ڪئلينڊر",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IvoryText
                    )
                }

                Glass3DPill(
                    selected = true,
                    onClick = onOpenRamadanCalendar,
                    modifier = Modifier.weight(1f),
                    testTag = "home_open_ramadan_calendar"
                ) {
                    Icon(
                        imageVector = Icons.Default.NightsStay,
                        contentDescription = "Open Ramadan Calendar",
                        tint = EmeraldDeepDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "رمضان ڪئلينڊر",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldDeepDark
                    )
                }
            }
        }
    }
}

private enum class ScreenMode {
    STARTING,
    MAIN,
    CALENDAR,
    HISTORY_STATS,
    PRAYER_QIBLA,
    ASMA_99,
    FAZILAT,
    MASNOON_DUAS,
    DAILY_REMINDER,
    HAJAT_AZKAR,
    SPECIAL_DHIKR,
    NAMAZ_GUIDE,
    EXIT
}
