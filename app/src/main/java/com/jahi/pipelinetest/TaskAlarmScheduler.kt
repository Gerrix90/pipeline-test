package com.jahi.pipelinetest

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.jahi.pipelinetest.model.Task
import java.time.ZoneId

private const val MAX_TASK_ALARMS = 50

internal fun scheduleTaskAlarms(context: Context, tasks: List<Task>): Int {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    var count = 0
    val schedulable = tasks.mapNotNull { task ->
        val due = task.dueDate ?: return@mapNotNull null
        val time = parseEventDateTimeOrNull(due) ?: return@mapNotNull null
        val triggerAt = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (triggerAt <= System.currentTimeMillis() || task.isCompleted) return@mapNotNull null
        task to triggerAt
    }

    if (schedulable.size > MAX_TASK_ALARMS) {
        android.util.Log.w(
            "TaskAlarmScheduler",
            "Found ${schedulable.size} valid tasks for alarms, but limit is $MAX_TASK_ALARMS. Scheduling the first $MAX_TASK_ALARMS."
        )
    }

    schedulable.take(MAX_TASK_ALARMS).forEach { (task, triggerAt) ->
        val intent = Intent(context, TaskAlarmReceiver::class.java).apply {
            putExtra(TaskAlarmReceiver.EXTRA_TASK_DESC, task.description)
            putExtra(TaskAlarmReceiver.EXTRA_TASK_ID, task.id)
        }
        val pending = PendingIntent.getBroadcast(
            context,
            task.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(triggerAt, pending),
                    pending
                )
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
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

internal fun cancelTaskAlarms(context: Context, tasks: List<Task>) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    tasks.forEach { task ->
        val intent = Intent(context, TaskAlarmReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            context,
            task.id,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pending?.let { alarmManager.cancel(it) }
    }
}
