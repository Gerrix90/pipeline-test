package com.jahi.pipelinetest

import android.content.Context
import android.content.SharedPreferences
import com.jahi.pipelinetest.model.CustomEvent
import org.json.JSONArray
import org.json.JSONObject

class Prefs(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("time_fomo_prefs", Context.MODE_PRIVATE)

    var showYearCountdown: Boolean
        get() = prefs.getBoolean("showYearCountdown", true)
        set(value) { prefs.edit().putBoolean("showYearCountdown", value).apply() }

    var customEvents: MutableList<CustomEvent>
        get() {
            val json = prefs.getString("customEvents", "[]") ?: "[]"
            val arr = JSONArray(json)
            val list = mutableListOf<CustomEvent>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    CustomEvent(
                        name = obj.optString("name"),
                        date = obj.optString("date"),
                        showTime = obj.optBoolean("showTime")
                    )
                )
            }
            return list
        }
        set(value) {
            val arr = JSONArray()
            value.forEach {
                val obj = JSONObject()
                obj.put("name", it.name)
                obj.put("date", it.date)
                obj.put("showTime", it.showTime)
                arr.put(obj)
            }
            prefs.edit().putString("customEvents", arr.toString()).apply()
        }

    var currentAge: Int
        get() = prefs.getInt("currentAge", 30)
        set(value) { prefs.edit().putInt("currentAge", value).apply() }

    var targetAge: Int
        get() = prefs.getInt("targetAge", 80)
        set(value) { prefs.edit().putInt("targetAge", value).apply() }
}
