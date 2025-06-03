package com.jahi.pipelinetest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule all event alarms after device reboot
            val prefs = Prefs(context)
            val events = prefs.customEvents
            
            if (events.isNotEmpty()) {
                val scheduledCount = scheduleEventAlarms(context, events)
                android.util.Log.i("BootReceiver", "Rescheduled $scheduledCount event alarms after boot")
            }
        }
    }
}