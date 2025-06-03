package com.jahi.pipelinetest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            // Reschedule all event and task alarms after device reboot
            val prefs = Prefs(context)
            
            // Reschedule event alarms
            val events = prefs.customEvents
            if (events.isNotEmpty()) {
                val eventCount = scheduleEventAlarms(context, events)
                android.util.Log.i("BootReceiver", "Rescheduled $eventCount event alarms after boot")
            }
            
            // Reschedule task alarms
            val tasks = prefs.tasks
            if (tasks.isNotEmpty()) {
                val taskCount = scheduleTaskAlarms(context, tasks)
                android.util.Log.i("BootReceiver", "Rescheduled $taskCount task alarms after boot")
            }
        }
    }
}