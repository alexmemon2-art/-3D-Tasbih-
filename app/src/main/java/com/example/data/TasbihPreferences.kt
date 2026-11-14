package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppSettings
import com.example.model.BeadStyle
import com.example.model.ClickSoundStyle
import com.example.model.CompletionSoundStyle
import com.example.model.DailyDayStat
import com.example.model.Dhikr
import com.example.model.ReminderSettings
import com.example.model.HistoryItem
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object NightModeLogic {
    fun isAutoNightWindow(hourOfDay: Int): Boolean = hourOfDay >= 21 || hourOfDay < 6

    fun isEffectiveNightMode(
        autoEnabled: Boolean,
        manualEnabled: Boolean,
        hourOfDay: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    ): Boolean = manualEnabled || (autoEnabled && isAutoNightWindow(hourOfDay))
}

class TasbihPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("tasbih_3d_prefs", Context.MODE_PRIVATE)

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val shortDayFormat = SimpleDateFormat("EEE", Locale.US)

    fun getTodayDate(): String = dateFormat.format(Date())

    private fun getDateStringOffset(daysOffset: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, daysOffset)
        return dateFormat.format(cal.time)
    }

    fun getSavedDate(): String = prefs.getString(KEY_SAVED_DATE, "") ?: ""

    fun saveTodayDate(date: String) {
        prefs.edit().putString(KEY_SAVED_DATE, date).apply()
    }

    fun getTodayCount(): Int {
        val today = getTodayDate()
        val dailyVal = prefs.getInt(KEY_DAILY_PREFIX + today, -1)
        if (dailyVal >= 0) return dailyVal
        val saved = getSavedDate()
        return if (today == saved) {
            prefs.getInt(KEY_TODAY_COUNT, 0)
        } else {
            0
        }
    }

    fun saveTodayCount(count: Int) {
        val today = getTodayDate()
        prefs.edit()
            .putString(KEY_SAVED_DATE, today)
            .putInt(KEY_TODAY_COUNT, count)
            .putInt(KEY_DAILY_PREFIX + today, count)
            .apply()
    }

    fun getYesterdayCount(): Int {
        val yesterday = getDateStringOffset(-1)
        return prefs.getInt(KEY_DAILY_PREFIX + yesterday, 0)
    }

    fun getWeekTotal(): Int {
        var total = 0
        for (i in 0..6) {
            val key = KEY_DAILY_PREFIX + getDateStringOffset(-i)
            val count = if (i == 0) getTodayCount() else prefs.getInt(key, 0)
            total += count
        }
        return total
    }

    fun getLast7DaysBreakdown(): List<DailyDayStat> {
        val list = mutableListOf<DailyDayStat>()
        for (i in 6 downTo 0) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_MONTH, -i)
            val key = dateFormat.format(cal.time)
            val label = if (i == 0) "Today" else shortDayFormat.format(cal.time)
            val count = if (i == 0) getTodayCount() else prefs.getInt(KEY_DAILY_PREFIX + key, 0)
            list.add(DailyDayStat(dateKey = key, label = label, count = count))
        }
        return list
    }

    fun getStreak(): Int {
        val storedStreak = prefs.getInt(KEY_STREAK, 0)
        val lastActive = prefs.getString(KEY_LAST_STREAK_DATE, "") ?: ""
        val today = getTodayDate()
        val yesterday = getDateStringOffset(-1)

        return when {
            lastActive == today -> storedStreak.coerceAtLeast(if (getTodayCount() > 0) 1 else 0)
            lastActive == yesterday -> storedStreak
            else -> if (getTodayCount() > 0) 1 else 0
        }
    }

    fun recordActiveDayForStreak(): Int {
        val today = getTodayDate()
        val yesterday = getDateStringOffset(-1)
        val lastActive = prefs.getString(KEY_LAST_STREAK_DATE, "") ?: ""
        val currentStreak = prefs.getInt(KEY_STREAK, 0)

        val newStreak = when (lastActive) {
            today -> currentStreak.coerceAtLeast(1)
            yesterday -> currentStreak + 1
            else -> 1
        }

        prefs.edit()
            .putString(KEY_LAST_STREAK_DATE, today)
            .putInt(KEY_STREAK, newStreak)
            .apply()
        return newStreak
    }

    fun getDhikrLifetimeCount(dhikrId: String): Long =
        prefs.getLong(KEY_DHIKR_LIFETIME_PREFIX + dhikrId, 0L)

    fun saveDhikrLifetimeCount(dhikrId: String, count: Long) {
        prefs.edit().putLong(KEY_DHIKR_LIFETIME_PREFIX + dhikrId, count).apply()
    }

    fun incrementDhikrLifetimeCount(dhikrId: String, delta: Long = 1L): Long {
        val updated = (getDhikrLifetimeCount(dhikrId) + delta).coerceAtLeast(0L)
        saveDhikrLifetimeCount(dhikrId, updated)
        return updated
    }

    fun getCurrentSessionDhikrCount(): Int = prefs.getInt(KEY_CURRENT_COUNT, 0)

    fun saveCurrentSessionDhikrCount(count: Int) {
        prefs.edit().putInt(KEY_CURRENT_COUNT, count.coerceAtLeast(0)).apply()
    }

    fun getTotalCount(): Long = prefs.getLong(KEY_TOTAL_COUNT, 0L)

    fun saveTotalCount(count: Long) {
        prefs.edit().putLong(KEY_TOTAL_COUNT, count).apply()
    }

    fun getCompletedRounds(): Int = prefs.getInt(KEY_COMPLETED_ROUNDS, 0)

    fun saveCompletedRounds(rounds: Int) {
        prefs.edit().putInt(KEY_COMPLETED_ROUNDS, rounds).apply()
    }

    fun getActiveDhikrId(): String = prefs.getString(KEY_ACTIVE_DHIKR_ID, "subhanallah") ?: "subhanallah"

    fun saveActiveDhikrId(id: String) {
        prefs.edit().putString(KEY_ACTIVE_DHIKR_ID, id).apply()
    }

    fun getSettings(): AppSettings {
        val beadStyleName = prefs.getString(KEY_BEAD_STYLE, BeadStyle.EMERALD_GOLD.name)
        val style = try {
            BeadStyle.valueOf(beadStyleName ?: BeadStyle.EMERALD_GOLD.name)
        } catch (_: Exception) {
            BeadStyle.EMERALD_GOLD
        }

        val clickStyleName = prefs.getString(KEY_CLICK_SOUND_STYLE, ClickSoundStyle.WOODEN_BEAD.name)
        val clickStyle = try {
            ClickSoundStyle.valueOf(clickStyleName ?: ClickSoundStyle.WOODEN_BEAD.name)
        } catch (_: Exception) {
            ClickSoundStyle.WOODEN_BEAD
        }

        val completionStyleName = prefs.getString(KEY_COMPLETION_SOUND_STYLE, CompletionSoundStyle.HARMONIC_CHIME.name)
        val completionStyle = try {
            CompletionSoundStyle.valueOf(completionStyleName ?: CompletionSoundStyle.HARMONIC_CHIME.name)
        } catch (_: Exception) {
            CompletionSoundStyle.HARMONIC_CHIME
        }

        return AppSettings(
            defaultTarget = prefs.getInt(KEY_DEFAULT_TARGET, 33),
            soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true),
            vibrationEnabled = prefs.getBoolean(KEY_VIBRATION_ENABLED, true),
            autoReset = prefs.getBoolean(KEY_AUTO_RESET, true),
            keepScreenOn = prefs.getBoolean(KEY_KEEP_SCREEN_ON, false),
            countLocked = prefs.getBoolean(KEY_COUNT_LOCKED, false),
            beadStyle = style,
            clickSoundStyle = clickStyle,
            completionSoundStyle = completionStyle,
            dailyGoal = prefs.getInt(KEY_DAILY_GOAL, 300).coerceIn(10, 50000)
        )
    }

    fun saveSettings(settings: AppSettings) {
        prefs.edit()
            .putInt(KEY_DEFAULT_TARGET, settings.defaultTarget)
            .putBoolean(KEY_SOUND_ENABLED, settings.soundEnabled)
            .putBoolean(KEY_VIBRATION_ENABLED, settings.vibrationEnabled)
            .putBoolean(KEY_AUTO_RESET, settings.autoReset)
            .putBoolean(KEY_KEEP_SCREEN_ON, settings.keepScreenOn)
            .putBoolean(KEY_COUNT_LOCKED, settings.countLocked)
            .putString(KEY_BEAD_STYLE, settings.beadStyle.name)
            .putString(KEY_CLICK_SOUND_STYLE, settings.clickSoundStyle.name)
            .putString(KEY_COMPLETION_SOUND_STYLE, settings.completionSoundStyle.name)
            .putInt(KEY_DAILY_GOAL, settings.dailyGoal.coerceIn(10, 50000))
            .apply()
    }

    fun getReminderSettings(): ReminderSettings {
        return ReminderSettings(
            fajrEnabled = prefs.getBoolean(KEY_REMINDER_FAJR, false),
            zuhrEnabled = prefs.getBoolean(KEY_REMINDER_ZUHAR, false),
            asrEnabled = prefs.getBoolean(KEY_REMINDER_ASAR, false),
            maghribEnabled = prefs.getBoolean(KEY_REMINDER_MAGHRIB, false),
            ishaEnabled = prefs.getBoolean(KEY_REMINDER_ISHA, false),
            azanAlertsEnabled = prefs.getBoolean(KEY_AZAN_ALERTS, false),
            customReminderEnabled = prefs.getBoolean(KEY_CUSTOM_REMINDER_ENABLED, false),
            customHour = prefs.getInt(KEY_CUSTOM_REMINDER_HOUR, 21).coerceIn(0, 23),
            customMinute = prefs.getInt(KEY_CUSTOM_REMINDER_MIN, 0).coerceIn(0, 59)
        )
    }

    fun saveReminderSettings(reminders: ReminderSettings) {
        prefs.edit()
            .putBoolean(KEY_REMINDER_FAJR, reminders.fajrEnabled)
            .putBoolean(KEY_REMINDER_ZUHAR, reminders.zuhrEnabled)
            .putBoolean(KEY_REMINDER_ASAR, reminders.asrEnabled)
            .putBoolean(KEY_REMINDER_MAGHRIB, reminders.maghribEnabled)
            .putBoolean(KEY_REMINDER_ISHA, reminders.ishaEnabled)
            .putBoolean(KEY_AZAN_ALERTS, reminders.azanAlertsEnabled)
            .putBoolean(KEY_CUSTOM_REMINDER_ENABLED, reminders.customReminderEnabled)
            .putInt(KEY_CUSTOM_REMINDER_HOUR, reminders.customHour.coerceIn(0, 23))
            .putInt(KEY_CUSTOM_REMINDER_MIN, reminders.customMinute.coerceIn(0, 59))
            .apply()
    }

    fun getCustomDhikrs(): List<Dhikr> {
        val jsonStr = prefs.getString(KEY_CUSTOM_DHIKRS, null) ?: return emptyList()
        val list = mutableListOf<Dhikr>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Dhikr(
                        id = obj.getString("id"),
                        arabic = obj.getString("arabic"),
                        transliteration = obj.getString("transliteration"),
                        meaning = obj.getString("meaning"),
                        target = obj.getInt("target"),
                        isCustom = true,
                        fazilat = obj.optString("fazilat", ""),
                        reference = obj.optString("reference", "")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun saveCustomDhikrs(list: List<Dhikr>) {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("arabic", item.arabic)
            obj.put("transliteration", item.transliteration)
            obj.put("meaning", item.meaning)
            obj.put("target", item.target)
            obj.put("fazilat", item.fazilat)
            obj.put("reference", item.reference)
            array.put(obj)
        }
        prefs.edit().putString(KEY_CUSTOM_DHIKRS, array.toString()).apply()
    }

    fun getHistory(): List<HistoryItem> {
        val jsonStr = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        val list = mutableListOf<HistoryItem>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    HistoryItem(
                        id = obj.getString("id"),
                        dhikrArabic = obj.getString("dhikrArabic"),
                        dhikrTitle = obj.getString("dhikrTitle"),
                        count = obj.getInt("count"),
                        timestamp = obj.getLong("timestamp"),
                        formattedTime = obj.getString("formattedTime")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun saveHistory(list: List<HistoryItem>) {
        val array = JSONArray()
        for (item in list.take(50)) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("dhikrArabic", item.dhikrArabic)
            obj.put("dhikrTitle", item.dhikrTitle)
            obj.put("count", item.count)
            obj.put("timestamp", item.timestamp)
            obj.put("formattedTime", item.formattedTime)
            array.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, array.toString()).apply()
    }

    fun getHilalOffset(): Int = prefs.getInt(KEY_HILAL_OFFSET, 0).coerceIn(-2, 2)

    fun saveHilalOffset(offset: Int) {
        prefs.edit().putInt(KEY_HILAL_OFFSET, offset.coerceIn(-2, 2)).apply()
    }

    fun getSelectedCityName(): String = prefs.getString(KEY_SELECTED_CITY, "Hyderabad") ?: "Hyderabad"

    fun saveSelectedCityName(name: String) {
        prefs.edit().putString(KEY_SELECTED_CITY, name).apply()
    }

    fun getWeeklyChallengeTarget(): Int =
        prefs.getInt(KEY_WEEKLY_CHALLENGE_TARGET, 5000).coerceIn(500, 100000)

    fun saveWeeklyChallengeTarget(target: Int) {
        prefs.edit().putInt(KEY_WEEKLY_CHALLENGE_TARGET, target.coerceIn(500, 100000)).apply()
    }

    fun isTodayHadeesChallengeDone(): Boolean {
        val saved = prefs.getString(KEY_HADEES_CHALLENGE_DATE, "") ?: ""
        return saved == getTodayDate()
    }

    fun setTodayHadeesChallengeDone(done: Boolean) {
        val value = if (done) getTodayDate() else ""
        prefs.edit().putString(KEY_HADEES_CHALLENGE_DATE, value).apply()
    }

    fun getAutoNightModeEnabled(): Boolean = prefs.getBoolean(KEY_AUTO_NIGHT_ENABLED, false)

    fun saveAutoNightModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_NIGHT_ENABLED, enabled).apply()
    }

    fun getManualNightModeEnabled(): Boolean = prefs.getBoolean(KEY_MANUAL_NIGHT_ENABLED, false)

    fun saveManualNightModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_MANUAL_NIGHT_ENABLED, enabled).apply()
    }

    companion object {
        private const val KEY_SAVED_DATE = "saved_date"
        private const val KEY_TODAY_COUNT = "today_count"
        private const val KEY_DAILY_PREFIX = "daily_"
        private const val KEY_STREAK = "streak"
        private const val KEY_LAST_STREAK_DATE = "last_streak_date"
        private const val KEY_DHIKR_LIFETIME_PREFIX = "dhikr_lifetime_"
        private const val KEY_CURRENT_COUNT = "current_session_dhikr_count"
        private const val KEY_TOTAL_COUNT = "total_count"
        private const val KEY_COMPLETED_ROUNDS = "completed_rounds"
        private const val KEY_ACTIVE_DHIKR_ID = "active_dhikr_id"
        private const val KEY_DEFAULT_TARGET = "default_target"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_AUTO_RESET = "auto_reset"
        private const val KEY_KEEP_SCREEN_ON = "keep_screen_on"
        private const val KEY_COUNT_LOCKED = "count_locked"
        private const val KEY_BEAD_STYLE = "bead_style"
        private const val KEY_CLICK_SOUND_STYLE = "click_sound_style"
        private const val KEY_COMPLETION_SOUND_STYLE = "completion_sound_style"
        private const val KEY_DAILY_GOAL = "daily_goal"
        private const val KEY_REMINDER_FAJR = "reminder_fajar"
        private const val KEY_REMINDER_ZUHAR = "reminder_zuhar"
        private const val KEY_REMINDER_ASAR = "reminder_asar"
        private const val KEY_REMINDER_MAGHRIB = "reminder_maghrib"
        private const val KEY_REMINDER_ISHA = "reminder_isha"
        private const val KEY_AZAN_ALERTS = "azan_alerts_enabled"
        private const val KEY_CUSTOM_REMINDER_ENABLED = "custom_reminder_enabled"
        private const val KEY_CUSTOM_REMINDER_HOUR = "custom_reminder_hour"
        private const val KEY_CUSTOM_REMINDER_MIN = "custom_reminder_min"
        private const val KEY_CUSTOM_DHIKRS = "custom_dhikrs"
        private const val KEY_HISTORY = "history"
        private const val KEY_HILAL_OFFSET = "hilal_offset"
        private const val KEY_SELECTED_CITY = "selected_city"
        private const val KEY_WEEKLY_CHALLENGE_TARGET = "weekly_challenge_target"
        private const val KEY_HADEES_CHALLENGE_DATE = "hadees_challenge_date"
        private const val KEY_AUTO_NIGHT_ENABLED = "auto_night_enabled"
        private const val KEY_MANUAL_NIGHT_ENABLED = "manual_night_enabled"
    }
}
