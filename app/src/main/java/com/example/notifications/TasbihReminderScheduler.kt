package com.example.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.calendar.IslamicCalendarEngine
import com.example.data.TasbihPreferences
import com.example.model.ReminderSettings
import java.util.Calendar

object TasbihReminderScheduler {

    private const val REQ_FAJR = 2001
    private const val REQ_ZUHAR = 2002
    private const val REQ_ASAR = 2003
    private const val REQ_MAGHRIB = 2004
    private const val REQ_ISHA = 2005
    private const val REQ_CUSTOM = 2006

    private const val REQ_AZAN_FAJR = 3001
    private const val REQ_AZAN_ZUHAR = 3002
    private const val REQ_AZAN_ASAR = 3003
    private const val REQ_AZAN_MAGHRIB = 3004
    private const val REQ_AZAN_ISHA = 3005

    fun scheduleAllFromPreferences(context: Context) {
        val prefs = TasbihPreferences(context)
        scheduleAll(context, prefs.getReminderSettings(), prefs.getSelectedCityName())
    }

    fun scheduleAll(context: Context, settings: ReminderSettings, cityName: String) {
        TasbihReminderReceiver.ensureChannels(context)
        val prefs = TasbihPreferences(context)
        val locationConfig = prefs.getLocationTimezoneConfig()
        val customTimes = prefs.getCustomPrayerTimesConfig()
        val city = IslamicCalendarEngine.resolveEffectiveCity(cityName, locationConfig)
        val basePrayerTimes = IslamicCalendarEngine.computeDailyPrayerTimes(
            IslamicCalendarEngine.getTodayGregorian(),
            city
        )
        val prayerTimes = IslamicCalendarEngine.applyCustomPrayerTimes(basePrayerTimes, customTimes)

        // Namaz ke baad Tasbih reminders (+12 mins after each Namaz start time)
        scheduleOrCancelDailyMinutes(
            context = context,
            enabled = settings.fajrEnabled,
            requestCode = REQ_FAJR,
            totalMinutes = prayerTimes.fajrMinutes + 12,
            channelId = TasbihReminderReceiver.CHANNEL_TASBIH,
            title = "Fajr ke baad Tasbih • فجر",
            body = "Fajr ki namaz ke baad 33x SubhanAllah, 33x Alhamdulillah, 34x Allahu Akbar zaroor padhein."
        )
        scheduleOrCancelDailyMinutes(
            context = context,
            enabled = settings.zuhrEnabled,
            requestCode = REQ_ZUHAR,
            totalMinutes = prayerTimes.zuhrMinutes + 15,
            channelId = TasbihReminderReceiver.CHANNEL_TASBIH,
            title = "Zuhr ke baad Tasbih • ظہر",
            body = "Zuhr ke baad Tasbih-e-Fatima aur Istighfar ka waqt hai."
        )
        scheduleOrCancelDailyMinutes(
            context = context,
            enabled = settings.asrEnabled,
            requestCode = REQ_ASAR,
            totalMinutes = prayerTimes.asrMinutes + 12,
            channelId = TasbihReminderReceiver.CHANNEL_TASBIH,
            title = "Asr ke baad Tasbih • عصر",
            body = "Asr ke baad shaam ke azkar aur Tasbih padh lein."
        )
        scheduleOrCancelDailyMinutes(
            context = context,
            enabled = settings.maghribEnabled,
            requestCode = REQ_MAGHRIB,
            totalMinutes = prayerTimes.maghribMinutes + 10,
            channelId = TasbihReminderReceiver.CHANNEL_TASBIH,
            title = "Maghrib ke baad Tasbih • مغرب",
            body = "Maghrib ke baad Durood Sharif aur Tasbih ka ahtimam karein."
        )
        scheduleOrCancelDailyMinutes(
            context = context,
            enabled = settings.ishaEnabled,
            requestCode = REQ_ISHA,
            totalMinutes = prayerTimes.ishaMinutes + 15,
            channelId = TasbihReminderReceiver.CHANNEL_TASBIH,
            title = "Isha ke baad Tasbih • عشاء",
            body = "Sone se pehle Tasbih-e-Fatima aur Astaghfirullah mukammal karein."
        )

        // Optional Azan / Prayer time alerts
        scheduleOrCancelDailyMinutes(
            context = context,
            enabled = settings.azanAlertsEnabled,
            requestCode = REQ_AZAN_FAJR,
            totalMinutes = prayerTimes.fajrMinutes,
            channelId = TasbihReminderReceiver.CHANNEL_AZAN,
            title = "🕌 Fajr Prayer Time (${city.name})",
            body = "Fajr ka waqt shuru ho gaya hai (${prayerTimes.fajr})."
        )
        scheduleOrCancelDailyMinutes(
            context = context,
            enabled = settings.azanAlertsEnabled,
            requestCode = REQ_AZAN_ZUHAR,
            totalMinutes = prayerTimes.zuhrMinutes,
            channelId = TasbihReminderReceiver.CHANNEL_AZAN,
            title = "🕌 Zuhr Prayer Time (${city.name})",
            body = "Zuhr ki namaz ka waqt ho gaya hai (${prayerTimes.zuhr})."
        )
        scheduleOrCancelDailyMinutes(
            context = context,
            enabled = settings.azanAlertsEnabled,
            requestCode = REQ_AZAN_ASAR,
            totalMinutes = prayerTimes.asrMinutes,
            channelId = TasbihReminderReceiver.CHANNEL_AZAN,
            title = "🕌 Asr Prayer Time (${city.name})",
            body = "Asr ki namaz ka waqt ho gaya hai (${prayerTimes.asr})."
        )
        scheduleOrCancelDailyMinutes(
            context = context,
            enabled = settings.azanAlertsEnabled,
            requestCode = REQ_AZAN_MAGHRIB,
            totalMinutes = prayerTimes.maghribMinutes,
            channelId = TasbihReminderReceiver.CHANNEL_AZAN,
            title = "🕌 Maghrib Prayer Time (${city.name})",
            body = "Maghrib ka waqt ho gaya hai (${prayerTimes.maghrib})."
        )
        scheduleOrCancelDailyMinutes(
            context = context,
            enabled = settings.azanAlertsEnabled,
            requestCode = REQ_AZAN_ISHA,
            totalMinutes = prayerTimes.ishaMinutes,
            channelId = TasbihReminderReceiver.CHANNEL_AZAN,
            title = "🕌 Isha Prayer Time (${city.name})",
            body = "Isha ki namaz ka waqt ho gaya hai (${prayerTimes.isha})."
        )

        // Custom preferred daily time reminder
        scheduleOrCancelDailyHourMinute(
            context = context,
            enabled = settings.customReminderEnabled,
            requestCode = REQ_CUSTOM,
            hour = settings.customHour,
            minute = settings.customMinute,
            channelId = TasbihReminderReceiver.CHANNEL_TASBIH,
            title = "⏰ Daily Tasbih Time",
            body = "Aap ka muqarrar shuda Zikr-e-Ilahi ka waqt ho gaya hai. Apni Tasbih shuru karein."
        )
    }

    fun sendTestNotification(context: Context) {
        TasbihReminderReceiver.showNotification(
            context = context,
            notifId = 9999,
            channelId = TasbihReminderReceiver.CHANNEL_TASBIH,
            title = "⏰ Tasbih Reminder Active",
            body = "Namaz aur rozana Tasbih reminders ab set ho chuke hain. JazakAllahu Khairan!"
        )
    }

    private fun scheduleOrCancelDailyMinutes(
        context: Context,
        enabled: Boolean,
        requestCode: Int,
        totalMinutes: Int,
        channelId: String,
        title: String,
        body: String
    ) {
        val wrapped = ((totalMinutes % 1440) + 1440) % 1440
        scheduleOrCancelDailyHourMinute(
            context = context,
            enabled = enabled,
            requestCode = requestCode,
            hour = wrapped / 60,
            minute = wrapped % 60,
            channelId = channelId,
            title = title,
            body = body
        )
    }

    private fun scheduleOrCancelDailyHourMinute(
        context: Context,
        enabled: Boolean,
        requestCode: Int,
        hour: Int,
        minute: Int,
        channelId: String,
        title: String,
        body: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TasbihReminderReceiver::class.java).apply {
            putExtra(TasbihReminderReceiver.EXTRA_TITLE, title)
            putExtra(TasbihReminderReceiver.EXTRA_BODY, body)
            putExtra(TasbihReminderReceiver.EXTRA_NOTIF_ID, requestCode)
            putExtra(TasbihReminderReceiver.EXTRA_CHANNEL_ID, channelId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (!enabled) {
            alarmManager.cancel(pendingIntent)
            return
        }

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            cal.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}
