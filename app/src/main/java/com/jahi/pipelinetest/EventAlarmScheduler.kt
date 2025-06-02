package com.jahi.pipelinetest

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.jahi.pipelinetest.model.CustomEvent
import java.time.ZoneId
import com.jahi.pipelinetest.parseEventDateTimeOrNull

private const val MAX_ALARMS = 50

internal fun scheduleEventAlarms(context: Context, events: List<CustomEvent>): Int {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    var count = 0
    events.take(MAX_ALARMS).forEach { event ->
        if (event.date.isBlank()) return@forEach
        val time = parseEventDateTimeOrNull(event.date) ?: return@forEach
        val triggerAt = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (triggerAt <= System.currentTimeMillis()) return@forEach
        val intent = Intent(context, EventAlarmReceiver::class.java).apply {
            putExtra(EventAlarmReceiver.EXTRA_EVENT_NAME, event.name)
            putExtra(EventAlarmReceiver.EXTRA_EVENT_ID, event.id)
        }
        val pending = PendingIntent.getBroadcast(
            context,
            event.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pending)
        }
        count++
    }
    return count
}

internal fun cancelEventAlarms(context: Context, events: List<CustomEvent>) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    events.forEach { event ->
        val intent = Intent(context, EventAlarmReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            context,
            event.id,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pending?.let { alarmManager.cancel(it) }
    }
}

