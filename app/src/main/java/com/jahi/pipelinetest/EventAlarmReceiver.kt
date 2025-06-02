package com.jahi.pipelinetest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class EventAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val eventName = intent.getStringExtra(EXTRA_EVENT_NAME) ?: return
        val eventId = intent.getIntExtra(EXTRA_EVENT_ID, DEFAULT_EVENT_ID)
        val notificationId = if (eventId == DEFAULT_EVENT_ID) eventName.hashCode() else eventId
        if (intent.action == ACTION_DISMISS) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.cancel(notificationId)
            return
        }
        createChannel(context)
        val dismissIntent = Intent(context, EventAlarmReceiver::class.java).apply {
            action = ACTION_DISMISS
            putExtra(EXTRA_EVENT_NAME, eventName)
            putExtra(EXTRA_EVENT_ID, eventId)
        }
        val dismissPending = PendingIntent.getBroadcast(
            context,
            notificationId,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(eventName)
            .setContentText(context.getString(R.string.event_alarm_triggered))
            .setAutoCancel(true)
            .addAction(0, context.getString(R.string.dismiss), dismissPending)
            .build()
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(notificationId, notification)
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.event_alarm_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.event_alarm_channel_description)
            }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    companion object {
        const val EXTRA_EVENT_NAME = "event_name"
        const val EXTRA_EVENT_ID = "event_id"
        const val DEFAULT_EVENT_ID = -1
        const val ACTION_DISMISS = "com.jahi.pipelinetest.ACTION_DISMISS_ALARM"
        const val CHANNEL_ID = "event_alarm"
    }
}
