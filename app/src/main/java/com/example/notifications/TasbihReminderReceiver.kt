package com.example.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

class TasbihReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            TasbihReminderScheduler.scheduleAllFromPreferences(context)
            return
        }

        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Tasbih & Dhikr Reminder"
        val body = intent.getStringExtra(EXTRA_BODY) ?: "Namaz ke baad Tasbih-e-Fatima (33x SubhanAllah, 33x Alhamdulillah, 34x Allahu Akbar) padhein."
        val notifId = intent.getIntExtra(EXTRA_NOTIF_ID, 1001)
        val channelId = intent.getStringExtra(EXTRA_CHANNEL_ID) ?: CHANNEL_TASBIH

        showNotification(context, notifId, channelId, title, body)
    }

    companion object {
        const val CHANNEL_TASBIH = "tasbih_reminders_channel"
        const val CHANNEL_AZAN = "prayer_azan_channel"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_BODY = "extra_body"
        const val EXTRA_NOTIF_ID = "extra_notif_id"
        const val EXTRA_CHANNEL_ID = "extra_channel_id"

        fun ensureChannels(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val tasbihChannel = NotificationChannel(
                    CHANNEL_TASBIH,
                    "Tasbih & Namaz Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Reminders for daily Tasbih and after-prayer dhikr"
                }
                val azanChannel = NotificationChannel(
                    CHANNEL_AZAN,
                    "Prayer & Azan Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alerts at daily Islamic prayer times"
                }
                nm.createNotificationChannel(tasbihChannel)
                nm.createNotificationChannel(azanChannel)
            }
        }

        fun showNotification(
            context: Context,
            notifId: Int,
            channelId: String,
            title: String,
            body: String
        ) {
            ensureChannels(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (!granted) return
            }

            val openIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingOpen = PendingIntent.getActivity(
                context,
                notifId,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingOpen)
                .build()

            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(notifId, notification)
        }
    }
}
