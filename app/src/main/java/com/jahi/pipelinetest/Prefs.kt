package com.jahi.pipelinetest

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("time_fomo_prefs", Context.MODE_PRIVATE)

    var showYearCountdown: Boolean
        get() = prefs.getBoolean("showYearCountdown", true)
        set(value) { prefs.edit().putBoolean("showYearCountdown", value).apply() }

    var eventName: String
        get() = prefs.getString("eventName", "") ?: ""
        set(value) { prefs.edit().putString("eventName", value).apply() }

    var eventDate: String
        get() = prefs.getString("eventDate", "") ?: ""
        set(value) { prefs.edit().putString("eventDate", value).apply() }

    var eventShowTime: Boolean
        get() = prefs.getBoolean("eventShowTime", false)
        set(value) { prefs.edit().putBoolean("eventShowTime", value).apply() }

    var currentAge: Int
        get() = prefs.getInt("currentAge", 30)
        set(value) { prefs.edit().putInt("currentAge", value).apply() }

    var targetAge: Int
        get() = prefs.getInt("targetAge", 80)
        set(value) { prefs.edit().putInt("targetAge", value).apply() }
}
