package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioFeedback
import com.example.data.NightModeLogic
import com.example.data.TasbihPreferences
import com.example.haptic.HapticHelper
import com.example.model.AppSettings
import com.example.model.AsmaName
import com.example.model.DailyDayStat
import com.example.model.DefaultDhikrs
import com.example.model.Dhikr
import com.example.model.DhikrLevelInfo
import com.example.model.DhikrLevels
import com.example.model.HajatAzkar
import com.example.model.HistoryItem
import com.example.model.MasnoonDua
import com.example.model.ReminderSettings
import com.example.model.SpecialDhikr
import com.example.model.SpecialDhikrsData
import com.example.notifications.TasbihReminderScheduler
import com.example.ui.components.CalendarTab
import com.example.widget.TasbihWidgetUpdater
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class SubScreen {
    HISTORY_STATS,
    PRAYER_QIBLA,
    ASMA_99,
    FAZILAT,
    MASNOON_DUAS,
    DAILY_REMINDER,
    HAJAT_AZKAR,
    SPECIAL_DHIKR,
    NAMAZ_GUIDE,
    CHALLENGES,
    NIGHT_TIMER
}

data class TasbihUiState(
    val dhikrs: List<Dhikr> = emptyList(),
    val currentDhikr: Dhikr = DefaultDhikrs.list.first(),
    val count: Int = 0,
    val sessionCount: Int = 0,
    val todayCount: Int = 0,
    val yesterdayCount: Int = 0,
    val weekCount: Int = 0,
    val streakDays: Int = 0,
    val totalCount: Long = 0L,
    val completedRounds: Int = 0,
    val weeklyBreakdown: List<DailyDayStat> = emptyList(),
    val dhikrLifetimes: Map<String, Long> = emptyMap(),
    val levelInfo: DhikrLevelInfo = DhikrLevels.calculate(0L),
    val history: List<HistoryItem> = emptyList(),
    val settings: AppSettings = AppSettings(),
    val reminderSettings: ReminderSettings = ReminderSettings(),
    val showCompletionDialog: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val showAddDhikrDialog: Boolean = false,
    val showStartingScreen: Boolean = true,
    val showExitScreen: Boolean = false,
    val showCalendarScreen: Boolean = false,
    val calendarInitialTab: CalendarTab = CalendarTab.SINDHI_HIJRI,
    val activeSubScreen: SubScreen? = null,
    val prayerQiblaInitialTab: Int = 0,
    val hilalOffsetDays: Int = 0,
    val selectedCityName: String = "Hyderabad",
    val wallpaperIndex: Int = 0,
    val tasbihFatimaCount: Int = 0,
    val weeklyChallengeTarget: Int = 5000,
    val todayHadeesDone: Boolean = false,
    val autoNightMode: Boolean = false,
    val manualNightMode: Boolean = false,
    val isNightModeEffective: Boolean = false,
    val timerDurationMinutes: Int = 10,
    val timerRemainingMillis: Long = 10 * 60_000L,
    val isTimerRunning: Boolean = false,
    val isTimerCompleted: Boolean = false
)

class TasbihViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = TasbihPreferences(application)
    private val audioFeedback = AudioFeedback(application)
    private val hapticHelper = HapticHelper(application)
    private var timerJob: Job? = null

    private val _uiState = MutableStateFlow(TasbihUiState())
    val uiState: StateFlow<TasbihUiState> = _uiState.asStateFlow()

    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        syncFromPreferences(resetSessionCount = true)
    }

    /**
     * Syncs UI state with SharedPreferences (called on startup and onResume so Home Screen Widget
     * taps are immediately reflected inside the app).
     */
    fun syncFromPreferences(resetSessionCount: Boolean = false) {
        val customDhikrs = prefs.getCustomDhikrs()
        val allDhikrs = DefaultDhikrs.list + customDhikrs
        val savedDhikrId = prefs.getActiveDhikrId()
        val activeDhikr = allDhikrs.find { it.id == savedDhikrId }
            ?: _uiState.value.currentDhikr.takeIf { it.id == savedDhikrId }
            ?: allDhikrs.first()

        val settings = prefs.getSettings()
        val todayCount = prefs.getTodayCount()
        val yesterdayCount = prefs.getYesterdayCount()
        val weekCount = prefs.getWeekTotal()
        val streakDays = prefs.getStreak()
        val totalCount = prefs.getTotalCount()
        val completedRounds = prefs.getCompletedRounds()
        val history = prefs.getHistory()
        val weekly = prefs.getLast7DaysBreakdown()
        val lifetimes = allDhikrs.associate { it.id to prefs.getDhikrLifetimeCount(it.id) }
        val reminders = prefs.getReminderSettings()
        val currentCount = prefs.getCurrentSessionDhikrCount()
        val hilalOffset = prefs.getHilalOffset()
        val cityName = prefs.getSelectedCityName()
        val weeklyChallengeTarget = prefs.getWeeklyChallengeTarget()
        val todayHadeesDone = prefs.isTodayHadeesChallengeDone()
        val autoNight = prefs.getAutoNightModeEnabled()
        val manualNight = prefs.getManualNightModeEnabled()
        val nightEffective = NightModeLogic.isEffectiveNightMode(autoNight, manualNight)

        _uiState.update {
            it.copy(
                dhikrs = allDhikrs,
                currentDhikr = activeDhikr,
                count = currentCount,
                sessionCount = if (resetSessionCount) 0 else it.sessionCount,
                todayCount = todayCount,
                yesterdayCount = yesterdayCount,
                weekCount = weekCount,
                streakDays = streakDays,
                totalCount = totalCount,
                completedRounds = completedRounds,
                weeklyBreakdown = weekly,
                dhikrLifetimes = lifetimes,
                levelInfo = DhikrLevels.calculate(totalCount),
                history = history,
                settings = settings,
                reminderSettings = reminders,
                hilalOffsetDays = hilalOffset,
                selectedCityName = cityName,
                weeklyChallengeTarget = weeklyChallengeTarget,
                todayHadeesDone = todayHadeesDone,
                autoNightMode = autoNight,
                manualNightMode = manualNight,
                isNightModeEffective = nightEffective
            )
        }
    }

    fun increment() {
        val state = _uiState.value
        val newCount = state.count + 1
        val newSessionCount = state.sessionCount + 1
        val newTodayCount = state.todayCount + 1
        val newTotalCount = state.totalCount + 1
        val target = state.currentDhikr.target

        val targetReached = newCount >= target

        // Audio & haptic feedback
        if (state.settings.soundEnabled) {
            if (!targetReached) {
                audioFeedback.playBeadClick(state.settings.clickSoundStyle)
            }
        }
        if (state.settings.vibrationEnabled && !targetReached) {
            if (newCount % 11 == 0) {
                hapticHelper.vibrateSeparator()
            } else {
                hapticHelper.vibrateClick()
            }
        }

        var updatedHistory = state.history
        var updatedCompletedRounds = state.completedRounds

        if (targetReached) {
            if (state.settings.soundEnabled) {
                audioFeedback.playCompletionChime(state.settings.completionSoundStyle)
            }
            if (state.settings.vibrationEnabled) {
                hapticHelper.vibrateTargetReached()
            }

            updatedCompletedRounds += 1
            prefs.saveCompletedRounds(updatedCompletedRounds)

            val newHistoryItem = HistoryItem(
                id = UUID.randomUUID().toString(),
                dhikrArabic = state.currentDhikr.arabic,
                dhikrTitle = state.currentDhikr.transliteration,
                count = target,
                timestamp = System.currentTimeMillis(),
                formattedTime = timeFormat.format(Date())
            )
            updatedHistory = listOf(newHistoryItem) + state.history
            prefs.saveHistory(updatedHistory)
        }

        // Persist counts, per-dhikr lifetime, streak, and widget count
        val finalCount = if (targetReached && state.settings.autoReset) 0 else newCount
        prefs.saveCurrentSessionDhikrCount(finalCount)
        prefs.saveTodayCount(newTodayCount)
        prefs.saveTotalCount(newTotalCount)
        val newDhikrLifetime = prefs.incrementDhikrLifetimeCount(state.currentDhikr.id, 1L)
        val updatedStreak = prefs.recordActiveDayForStreak()

        val updatedLifetimes = state.dhikrLifetimes.toMutableMap().apply {
            this[state.currentDhikr.id] = newDhikrLifetime
        }

        _uiState.update {
            it.copy(
                count = finalCount,
                sessionCount = newSessionCount,
                todayCount = newTodayCount,
                weekCount = prefs.getWeekTotal(),
                streakDays = updatedStreak,
                totalCount = newTotalCount,
                completedRounds = updatedCompletedRounds,
                weeklyBreakdown = prefs.getLast7DaysBreakdown(),
                dhikrLifetimes = updatedLifetimes,
                levelInfo = DhikrLevels.calculate(newTotalCount),
                history = updatedHistory,
                showCompletionDialog = targetReached
            )
        }

        TasbihWidgetUpdater.updateAllWidgets(getApplication())
    }

    fun reset() {
        if (_uiState.value.settings.countLocked) return
        prefs.saveCurrentSessionDhikrCount(0)
        _uiState.update { it.copy(count = 0) }
        TasbihWidgetUpdater.updateAllWidgets(getApplication())
    }

    fun undo() {
        val state = _uiState.value
        if (state.count <= 0 || state.settings.countLocked) return
        val newCount = state.count - 1
        val newSessionCount = (state.sessionCount - 1).coerceAtLeast(0)
        val newTodayCount = (state.todayCount - 1).coerceAtLeast(0)
        val newTotalCount = (state.totalCount - 1).coerceAtLeast(0L)

        prefs.saveCurrentSessionDhikrCount(newCount)
        prefs.saveTodayCount(newTodayCount)
        prefs.saveTotalCount(newTotalCount)
        val newLifetime = prefs.incrementDhikrLifetimeCount(state.currentDhikr.id, -1L)
        val updatedLifetimes = state.dhikrLifetimes.toMutableMap().apply {
            this[state.currentDhikr.id] = newLifetime
        }

        _uiState.update {
            it.copy(
                count = newCount,
                sessionCount = newSessionCount,
                todayCount = newTodayCount,
                weekCount = prefs.getWeekTotal(),
                totalCount = newTotalCount,
                weeklyBreakdown = prefs.getLast7DaysBreakdown(),
                dhikrLifetimes = updatedLifetimes,
                levelInfo = DhikrLevels.calculate(newTotalCount)
            )
        }
        TasbihWidgetUpdater.updateAllWidgets(getApplication())
    }

    fun toggleCountLock() {
        val updated = _uiState.value.settings.copy(countLocked = !_uiState.value.settings.countLocked)
        prefs.saveSettings(updated)
        _uiState.update { it.copy(settings = updated) }
        TasbihWidgetUpdater.updateAllWidgets(getApplication())
    }

    fun selectDhikr(dhikr: Dhikr) {
        prefs.saveActiveDhikrId(dhikr.id)
        prefs.saveCurrentSessionDhikrCount(0)
        _uiState.update {
            it.copy(
                currentDhikr = dhikr,
                count = 0
            )
        }
        TasbihWidgetUpdater.updateAllWidgets(getApplication())
    }

    fun selectAsmaAsDhikr(asma: AsmaName, target: Int = 100) {
        val asmaId = "asma_${asma.number}"
        val asmaDhikr = Dhikr(
            id = asmaId,
            arabic = "يَا ${asma.arabic}",
            transliteration = "Ya ${asma.transliteration}",
            meaning = "${asma.romanUrdu} • ${asma.englishMeaning}",
            target = target.coerceIn(1, 10000),
            isCustom = false,
            fazilat = asma.benefit,
            reference = "Asma-ul-Husna #${asma.number}"
        )
        prefs.saveActiveDhikrId(asmaId)
        prefs.saveCurrentSessionDhikrCount(0)
        _uiState.update {
            it.copy(
                currentDhikr = asmaDhikr,
                count = 0,
                activeSubScreen = null
            )
        }
        TasbihWidgetUpdater.updateAllWidgets(getApplication())
    }

    fun selectDuaAsDhikr(dua: MasnoonDua) {
        val duaId = "dua_${dua.number}"
        val duaDhikr = Dhikr(
            id = duaId,
            arabic = dua.arabic,
            transliteration = dua.title,
            meaning = "${dua.romanUrdu} • ${dua.englishMeaning}",
            target = dua.recommendedCount.coerceIn(1, 10000),
            isCustom = false,
            fazilat = dua.transliteration,
            reference = dua.reference
        )
        prefs.saveActiveDhikrId(duaId)
        prefs.saveCurrentSessionDhikrCount(0)
        _uiState.update {
            it.copy(
                currentDhikr = duaDhikr,
                count = 0,
                activeSubScreen = null
            )
        }
        TasbihWidgetUpdater.updateAllWidgets(getApplication())
    }

    fun selectHajatAsDhikr(azkar: HajatAzkar) {
        val hajatDhikr = Dhikr(
            id = azkar.id,
            arabic = azkar.dua,
            transliteration = azkar.title,
            meaning = azkar.urdu,
            target = azkar.targetCount.coerceIn(1, 10000),
            isCustom = false,
            fazilat = "${azkar.whenToRead} (${azkar.countLabel})",
            reference = azkar.reference
        )
        prefs.saveActiveDhikrId(azkar.id)
        prefs.saveCurrentSessionDhikrCount(0)
        _uiState.update {
            it.copy(
                currentDhikr = hajatDhikr,
                count = 0,
                activeSubScreen = null
            )
        }
        TasbihWidgetUpdater.updateAllWidgets(getApplication())
    }

    fun selectSpecialDhikr(special: SpecialDhikr) {
        val specialDhikr = Dhikr(
            id = special.id,
            arabic = special.arabic,
            transliteration = special.title,
            meaning = special.meaning,
            target = special.recommendedCount.coerceIn(1, 10000),
            isCustom = false,
            fazilat = special.fazilat,
            reference = special.reference
        )
        prefs.saveActiveDhikrId(special.id)
        prefs.saveCurrentSessionDhikrCount(0)
        _uiState.update {
            it.copy(
                currentDhikr = specialDhikr,
                count = 0,
                activeSubScreen = null
            )
        }
        TasbihWidgetUpdater.updateAllWidgets(getApplication())
    }

    fun incrementTasbihFatima() {
        val state = _uiState.value
        val current = state.tasbihFatimaCount
        if (current >= SpecialDhikrsData.TASBIH_FATIMA_TOTAL) return

        val next = current + 1
        val completed = next >= SpecialDhikrsData.TASBIH_FATIMA_TOTAL
        val stepBoundary = next == 33 || next == 66

        if (state.settings.soundEnabled) {
            if (completed) {
                audioFeedback.playCompletionChime(state.settings.completionSoundStyle)
            } else {
                audioFeedback.playBeadClick(state.settings.clickSoundStyle)
            }
        }
        if (state.settings.vibrationEnabled) {
            when {
                completed -> hapticHelper.vibrateTargetReached()
                stepBoundary -> hapticHelper.vibrateSeparator()
                else -> hapticHelper.vibrateClick()
            }
        }

        val newToday = state.todayCount + 1
        val newTotal = state.totalCount + 1
        prefs.saveTodayCount(newToday)
        prefs.saveTotalCount(newTotal)
        val updatedStreak = prefs.recordActiveDayForStreak()

        var updatedHistory = state.history
        var updatedRounds = state.completedRounds
        if (completed) {
            updatedRounds += 1
            prefs.saveCompletedRounds(updatedRounds)
            val newHistoryItem = HistoryItem(
                id = UUID.randomUUID().toString(),
                dhikrArabic = "سُبْحَانَ اللَّهِ • الْحَمْدُ لِلَّهِ • اللَّهُ أَكْبَرُ",
                dhikrTitle = "Tasbih-e-Fatima (33+33+34)",
                count = SpecialDhikrsData.TASBIH_FATIMA_TOTAL,
                timestamp = System.currentTimeMillis(),
                formattedTime = timeFormat.format(Date())
            )
            updatedHistory = listOf(newHistoryItem) + state.history
            prefs.saveHistory(updatedHistory)
        }

        _uiState.update {
            it.copy(
                tasbihFatimaCount = next,
                sessionCount = state.sessionCount + 1,
                todayCount = newToday,
                weekCount = prefs.getWeekTotal(),
                streakDays = updatedStreak,
                totalCount = newTotal,
                completedRounds = updatedRounds,
                weeklyBreakdown = prefs.getLast7DaysBreakdown(),
                levelInfo = DhikrLevels.calculate(newTotal),
                history = updatedHistory
            )
        }
        TasbihWidgetUpdater.updateAllWidgets(getApplication())
    }

    fun resetTasbihFatima() {
        _uiState.update { it.copy(tasbihFatimaCount = 0) }
    }

    fun nextDhikr() {
        val state = _uiState.value
        val currentIndex = state.dhikrs.indexOfFirst { it.id == state.currentDhikr.id }
        val nextIndex = if (currentIndex >= 0 && currentIndex < state.dhikrs.size - 1) {
            currentIndex + 1
        } else {
            0
        }
        val next = state.dhikrs[nextIndex]
        selectDhikr(next)
        dismissCompletionDialog()
    }

    fun addCustomDhikr(dhikr: Dhikr) {
        val currentCustom = prefs.getCustomDhikrs()
        val updatedCustom = currentCustom + dhikr
        prefs.saveCustomDhikrs(updatedCustom)

        val allDhikrs = DefaultDhikrs.list + updatedCustom
        prefs.saveActiveDhikrId(dhikr.id)
        prefs.saveCurrentSessionDhikrCount(0)

        _uiState.update {
            it.copy(
                dhikrs = allDhikrs,
                currentDhikr = dhikr,
                count = 0,
                showAddDhikrDialog = false
            )
        }
        TasbihWidgetUpdater.updateAllWidgets(getApplication())
    }

    fun updateSettings(newSettings: AppSettings) {
        prefs.saveSettings(newSettings)
        val updatedDhikr = _uiState.value.currentDhikr.copy(target = newSettings.defaultTarget)
        _uiState.update {
            it.copy(
                settings = newSettings,
                currentDhikr = updatedDhikr,
                showSettingsDialog = false
            )
        }
        TasbihWidgetUpdater.updateAllWidgets(getApplication())
    }

    fun updateDailyGoal(goal: Int) {
        val valid = goal.coerceIn(10, 50000)
        val updated = _uiState.value.settings.copy(dailyGoal = valid)
        prefs.saveSettings(updated)
        _uiState.update { it.copy(settings = updated) }
    }

    fun updateWeeklyChallengeTarget(target: Int) {
        val valid = target.coerceIn(500, 100000)
        prefs.saveWeeklyChallengeTarget(valid)
        _uiState.update { it.copy(weeklyChallengeTarget = valid) }
    }

    fun toggleTodayHadeesChallengeDone() {
        val nextDone = !_uiState.value.todayHadeesDone
        prefs.setTodayHadeesChallengeDone(nextDone)
        _uiState.update { it.copy(todayHadeesDone = nextDone) }
    }

    fun startChallengeDhikr(dhikrId: String, target: Int) {
        val state = _uiState.value
        val baseDhikr = state.dhikrs.find { it.id == dhikrId }
            ?: DefaultDhikrs.list.find { it.id == dhikrId }
            ?: state.currentDhikr
        val updatedDhikr = baseDhikr.copy(target = target.coerceIn(1, 10000))
        prefs.saveActiveDhikrId(updatedDhikr.id)
        prefs.saveCurrentSessionDhikrCount(0)
        _uiState.update {
            it.copy(
                currentDhikr = updatedDhikr,
                count = 0,
                activeSubScreen = null
            )
        }
        TasbihWidgetUpdater.updateAllWidgets(getApplication())
    }

    fun updateReminderSettings(newReminders: ReminderSettings) {
        prefs.saveReminderSettings(newReminders)
        _uiState.update { it.copy(reminderSettings = newReminders) }
        TasbihReminderScheduler.scheduleAll(getApplication(), newReminders, _uiState.value.selectedCityName)
    }

    fun clearHistory() {
        prefs.saveHistory(emptyList())
        _uiState.update { it.copy(history = emptyList()) }
    }

    fun dismissCompletionDialog() {
        _uiState.update { it.copy(showCompletionDialog = false) }
    }

    fun showSettings(show: Boolean) {
        _uiState.update { it.copy(showSettingsDialog = show) }
    }

    fun showAddDhikr(show: Boolean) {
        _uiState.update { it.copy(showAddDhikrDialog = show) }
    }

    fun dismissStartingScreen() {
        _uiState.update { it.copy(showStartingScreen = false) }
    }

    fun openStartingScreen() {
        _uiState.update { it.copy(showStartingScreen = true) }
    }

    fun showExit(show: Boolean) {
        _uiState.update { it.copy(showExitScreen = show) }
    }

    fun nextWallpaper() {
        _uiState.update { it.copy(wallpaperIndex = (it.wallpaperIndex + 1) % 10) }
    }

    fun setWallpaperIndex(index: Int) {
        _uiState.update { it.copy(wallpaperIndex = index % 10) }
    }

    fun openCalendar(tab: CalendarTab = CalendarTab.SINDHI_HIJRI) {
        _uiState.update {
            it.copy(
                showCalendarScreen = true,
                activeSubScreen = null,
                calendarInitialTab = tab
            )
        }
    }

    fun closeCalendar() {
        _uiState.update { it.copy(showCalendarScreen = false) }
    }

    fun openSubScreen(screen: SubScreen, prayerTab: Int = 0) {
        _uiState.update {
            it.copy(
                activeSubScreen = screen,
                showCalendarScreen = false,
                showSettingsDialog = false,
                prayerQiblaInitialTab = prayerTab
            )
        }
    }

    fun closeSubScreen() {
        _uiState.update { it.copy(activeSubScreen = null) }
    }

    fun setHilalOffset(offset: Int) {
        val clamped = offset.coerceIn(-2, 2)
        prefs.saveHilalOffset(clamped)
        _uiState.update { it.copy(hilalOffsetDays = clamped) }
    }

    fun setSelectedCity(cityName: String) {
        prefs.saveSelectedCityName(cityName)
        _uiState.update { it.copy(selectedCityName = cityName) }
        TasbihReminderScheduler.scheduleAll(getApplication(), _uiState.value.reminderSettings, cityName)
    }

    fun toggleAutoNightMode(enabled: Boolean) {
        prefs.saveAutoNightModeEnabled(enabled)
        _uiState.update { state ->
            state.copy(
                autoNightMode = enabled,
                isNightModeEffective = NightModeLogic.isEffectiveNightMode(enabled, state.manualNightMode)
            )
        }
    }

    fun toggleManualNightMode(enabled: Boolean) {
        prefs.saveManualNightModeEnabled(enabled)
        _uiState.update { state ->
            state.copy(
                manualNightMode = enabled,
                isNightModeEffective = NightModeLogic.isEffectiveNightMode(state.autoNightMode, enabled)
            )
        }
    }

    fun selectTimerMinutes(minutes: Int) {
        val valid = minutes.coerceIn(1, 120)
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                timerDurationMinutes = valid,
                timerRemainingMillis = valid * 60_000L,
                isTimerRunning = false,
                isTimerCompleted = false
            )
        }
    }

    fun startDhikrTimer() {
        val current = _uiState.value
        if (current.isTimerRunning) return
        val startMillis = if (current.timerRemainingMillis <= 0L || current.isTimerCompleted) {
            current.timerDurationMinutes.coerceAtLeast(1) * 60_000L
        } else {
            current.timerRemainingMillis
        }

        _uiState.update {
            it.copy(
                timerRemainingMillis = startMillis,
                isTimerRunning = true,
                isTimerCompleted = false
            )
        }

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = startMillis
            while (remaining > 0L) {
                delay(1000L)
                remaining = (remaining - 1000L).coerceAtLeast(0L)
                _uiState.update { it.copy(timerRemainingMillis = remaining) }
            }
            val latestSettings = _uiState.value.settings
            if (latestSettings.soundEnabled) {
                audioFeedback.playCompletionChime(latestSettings.completionSoundStyle)
            }
            if (latestSettings.vibrationEnabled) {
                hapticHelper.vibrateTargetReached()
            }
            _uiState.update {
                it.copy(
                    timerRemainingMillis = 0L,
                    isTimerRunning = false,
                    isTimerCompleted = true
                )
            }
        }
    }

    fun pauseDhikrTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isTimerRunning = false) }
    }

    fun stopDhikrTimer() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                timerRemainingMillis = 0L,
                isTimerRunning = false,
                isTimerCompleted = false
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        audioFeedback.release()
    }
}
