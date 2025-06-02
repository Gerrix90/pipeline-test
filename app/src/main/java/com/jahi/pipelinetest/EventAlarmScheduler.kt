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

internal fun scheduleEventAlarms(context: Context, events: List<CustomEvent>) {
    cancelEventAlarms(context)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    events.take(MAX_ALARMS).forEachIndexed { index, event ->
        if (event.date.isBlank()) return@forEachIndexed
        val time = parseEventDateTimeOrNull(event.date) ?: return@forEachIndexed
        val triggerAt = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (triggerAt <= System.currentTimeMillis()) return@forEachIndexed
        val intent = Intent(context, EventAlarmReceiver::class.java).apply {
            putExtra(EventAlarmReceiver.EXTRA_EVENT_NAME, event.name)
            putExtra(EventAlarmReceiver.EXTRA_EVENT_ID, index)
        }
        val pending = PendingIntent.getBroadcast(
            context,
            index,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pending)
        }
    }
}

internal fun cancelEventAlarms(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    for (i in 0 until MAX_ALARMS) {
        val intent = Intent(context, EventAlarmReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            context,
            i,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pending?.let { alarmManager.cancel(it) }
    }
}
