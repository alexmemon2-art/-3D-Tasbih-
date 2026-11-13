package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.TasbihPreferences
import com.example.model.DefaultDhikrs
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object TasbihWidgetUpdater {
    const val ACTION_INCREMENT = "com.example.ACTION_WIDGET_INCREMENT"
    const val ACTION_RESET = "com.example.ACTION_WIDGET_RESET"

    fun updateAllWidgets(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val prefs = TasbihPreferences(context)
        val allDhikrs = DefaultDhikrs.list + prefs.getCustomDhikrs()
        val activeId = prefs.getActiveDhikrId()
        val activeDhikr = allDhikrs.find { it.id == activeId } ?: DefaultDhikrs.list.first()
        val settings = prefs.getSettings()
        val target = activeDhikr.target.coerceAtLeast(1)
        val count = prefs.getCurrentSessionDhikrCount()
        val todayCount = prefs.getTodayCount()
        val streak = prefs.getStreak()

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPending = PendingIntent.getActivity(
            context,
            501,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val smallIncIntent = Intent(context, TasbihSmallWidgetProvider::class.java).apply {
            action = ACTION_INCREMENT
        }
        val smallIncPending = PendingIntent.getBroadcast(
            context,
            502,
            smallIncIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val largeIncIntent = Intent(context, TasbihLargeWidgetProvider::class.java).apply {
            action = ACTION_INCREMENT
        }
        val largeIncPending = PendingIntent.getBroadcast(
            context,
            503,
            largeIncIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val largeResetIntent = Intent(context, TasbihLargeWidgetProvider::class.java).apply {
            action = ACTION_RESET
        }
        val largeResetPending = PendingIntent.getBroadcast(
            context,
            504,
            largeResetIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Update Small Widgets
        val smallIds = manager.getAppWidgetIds(ComponentName(context, TasbihSmallWidgetProvider::class.java))
        for (id in smallIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_tasbih_small)
            views.setTextViewText(R.id.widget_small_dhikr, activeDhikr.arabic)
            views.setTextViewText(R.id.widget_small_count, "$count / $target")
            views.setOnClickPendingIntent(R.id.widget_small_open_app, openPending)
            views.setOnClickPendingIntent(R.id.widget_small_plus_btn, smallIncPending)
            manager.updateAppWidget(id, views)
        }

        // Update Large Widgets
        val largeIds = manager.getAppWidgetIds(ComponentName(context, TasbihLargeWidgetProvider::class.java))
        for (id in largeIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_tasbih_large)
            views.setTextViewText(R.id.widget_large_arabic, activeDhikr.arabic)
            views.setTextViewText(R.id.widget_large_title, "${activeDhikr.transliteration} • Today: $todayCount")
            views.setTextViewText(R.id.widget_large_streak, "🔥 ${streak}d • Today $todayCount")
            views.setTextViewText(R.id.widget_large_count, count.toString())
            views.setTextViewText(
                R.id.widget_large_target,
                if (settings.countLocked) "Target: $target • 🔒 Locked" else "Target: $target"
            )
            views.setOnClickPendingIntent(R.id.widget_large_header, openPending)
            views.setOnClickPendingIntent(R.id.widget_large_plus_btn, largeIncPending)
            views.setOnClickPendingIntent(R.id.widget_large_reset_btn, largeResetPending)
            manager.updateAppWidget(id, views)
        }
    }

    fun handleWidgetIncrement(context: Context) {
        val prefs = TasbihPreferences(context)
        val allDhikrs = DefaultDhikrs.list + prefs.getCustomDhikrs()
        val activeId = prefs.getActiveDhikrId()
        val activeDhikr = allDhikrs.find { it.id == activeId } ?: DefaultDhikrs.list.first()
        val settings = prefs.getSettings()
        val target = activeDhikr.target.coerceAtLeast(1)

        val currentCount = prefs.getCurrentSessionDhikrCount() + 1
        val todayCount = prefs.getTodayCount() + 1
        val totalCount = prefs.getTotalCount() + 1

        prefs.saveTodayCount(todayCount)
        prefs.saveTotalCount(totalCount)
        prefs.incrementDhikrLifetimeCount(activeDhikr.id, 1L)
        prefs.recordActiveDayForStreak()

        if (currentCount >= target) {
            prefs.saveCompletedRounds(prefs.getCompletedRounds() + 1)
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val item = com.example.model.HistoryItem(
                id = UUID.randomUUID().toString(),
                dhikrArabic = activeDhikr.arabic,
                dhikrTitle = "${activeDhikr.transliteration} (Widget)",
                count = target,
                timestamp = System.currentTimeMillis(),
                formattedTime = timeFormat.format(Date())
            )
            prefs.saveHistory(listOf(item) + prefs.getHistory())
            prefs.saveCurrentSessionDhikrCount(if (settings.autoReset) 0 else currentCount)
        } else {
            prefs.saveCurrentSessionDhikrCount(currentCount)
        }

        updateAllWidgets(context)
    }

    fun handleWidgetReset(context: Context) {
        val prefs = TasbihPreferences(context)
        if (!prefs.getSettings().countLocked) {
            prefs.saveCurrentSessionDhikrCount(0)
            updateAllWidgets(context)
        }
    }

    fun requestPinWidget(context: Context, isLarge: Boolean): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = AppWidgetManager.getInstance(context)
            if (manager.isRequestPinAppWidgetSupported) {
                val provider = ComponentName(
                    context,
                    if (isLarge) TasbihLargeWidgetProvider::class.java else TasbihSmallWidgetProvider::class.java
                )
                return manager.requestPinAppWidget(provider, null, null)
            }
        }
        return false
    }
}

class TasbihSmallWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        TasbihWidgetUpdater.updateAllWidgets(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == TasbihWidgetUpdater.ACTION_INCREMENT) {
            TasbihWidgetUpdater.handleWidgetIncrement(context)
        }
    }
}

class TasbihLargeWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        TasbihWidgetUpdater.updateAllWidgets(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            TasbihWidgetUpdater.ACTION_INCREMENT -> TasbihWidgetUpdater.handleWidgetIncrement(context)
            TasbihWidgetUpdater.ACTION_RESET -> TasbihWidgetUpdater.handleWidgetReset(context)
        }
    }
}
