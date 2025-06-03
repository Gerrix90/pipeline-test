package com.jahi.pipelinetest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat

class TaskAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val desc = intent.getStringExtra(EXTRA_TASK_DESC) ?: return
        val taskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID)
        val notificationId = if (taskId == DEFAULT_TASK_ID) desc.hashCode() else taskId
        if (intent.action == ACTION_DISMISS) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.cancel(notificationId)
            return
        }
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "TaskAlarm:WakeLock"
        )
        wakeLock.acquire(30000)
        try {
            createChannel(context)
            val dismissIntent = Intent(context, TaskAlarmReceiver::class.java).apply {
            action = ACTION_DISMISS
            putExtra(EXTRA_TASK_DESC, desc)
            putExtra(EXTRA_TASK_ID, taskId)
        }
            val dismissPending = PendingIntent.getBroadcast(
                context,
                notificationId,
                dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val openAppIntent = Intent(context, MainActivity::class.java)
            val openAppPending = PendingIntent.getActivity(
                context,
                notificationId + 1000,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(desc)
                .setContentText(context.getString(R.string.task_alarm_triggered))
                .setContentIntent(openAppPending)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setFullScreenIntent(openAppPending, true)
                .addAction(0, context.getString(R.string.dismiss), dismissPending)
                .build()
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(notificationId, notification)
        } finally {
            if (wakeLock.isHeld) {
                wakeLock.release()
            }
        }
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.task_alarm_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.task_alarm_channel_description)
                setBypassDnd(false)
                setShowBadge(true)
                enableVibration(true)
                enableLights(true)
            }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    companion object {
        const val EXTRA_TASK_DESC = "task_desc"
        const val EXTRA_TASK_ID = "task_id"
        const val DEFAULT_TASK_ID = -1
        const val ACTION_DISMISS = "com.jahi.pipelinetest.ACTION_DISMISS_TASK"
        const val CHANNEL_ID = "task_alarm"
    }
}
