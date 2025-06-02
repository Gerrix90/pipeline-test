package com.jahi.pipelinetest

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.jahi.pipelinetest.model.CustomEvent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal fun scheduleEventAlarms(context: Context, events: List<CustomEvent>) {
    cancelEventAlarms(context)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    events.forEachIndexed { index, event ->
        if (event.date.isBlank()) return@forEachIndexed
        val time = parseEventDateTime(event.date) ?: return@forEachIndexed
        val triggerAt = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (triggerAt <= System.currentTimeMillis()) return@forEachIndexed
        val intent = Intent(context, EventAlarmReceiver::class.java).apply {
            putExtra(EventAlarmReceiver.EXTRA_EVENT_NAME, event.name)
        }
        val pending = PendingIntent.getBroadcast(
            context,
            index,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pending)
    }
}

internal fun cancelEventAlarms(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    for (i in 0 until 50) {
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

private fun parseEventDateTime(dateString: String): LocalDateTime? {
    return try {
        LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    } catch (_: Exception) {
        try {
            LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay()
        } catch (_: Exception) {
            null
        }
    }
}
