package com.jahi.pipelinetest

import android.content.Context
import android.content.SharedPreferences
import com.jahi.pipelinetest.model.CustomEvent
import com.jahi.pipelinetest.model.Task
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
                        id = obj.optInt("id", kotlin.random.Random.nextInt()),
                        name = obj.optString("name"),
                        date = obj.optString("date"),
                        showTime = obj.optBoolean("showTime"),
                        showInWidget = obj.optBoolean("showInWidget", false)
                    )
                )
            }
            return list
        }
        set(value) {
            val arr = JSONArray()
            value.forEach {
                val obj = JSONObject()
                obj.put("id", it.id)
                obj.put("name", it.name)
                obj.put("date", it.date)
                obj.put("showTime", it.showTime)
                obj.put("showInWidget", it.showInWidget)
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

    var tasks: MutableList<Task>
        get() {
            val json = prefs.getString("tasks", "[]") ?: "[]"
            val arr = JSONArray(json)
            val list = mutableListOf<Task>()
            val usedIds = mutableSetOf<Int>()
            var nextId = nextTaskId

            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                var id = obj.optInt("id", -1)
                if (id == -1 || usedIds.contains(id)) {
                    Log.w("Prefs", "Regenerating duplicate task id $id")
                    id = nextId
                    nextId++
                }
                usedIds.add(id)
                list.add(
                    Task(
                        id = id,
                        eventId = obj.getInt("eventId"),
                        description = obj.optString("description"),
                        isCompleted = obj.optBoolean("isCompleted"),
                        createdAt = obj.optString(
                            "createdAt",
                            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        ),
                        dueDate = if (obj.has("dueDate") && !obj.isNull("dueDate")) {
                            obj.optString("dueDate")
                        } else {
                            null
                        }
                    )
                )
            }

            return list
        }
        set(value) {
            val arr = JSONArray()
            value.forEach {
                val obj = JSONObject()
                obj.put("id", it.id)
                obj.put("eventId", it.eventId)
                obj.put("description", it.description)
                obj.put("isCompleted", it.isCompleted)
                obj.put("createdAt", it.createdAt)
                it.dueDate?.let { dueDate ->
                    obj.put("dueDate", dueDate)
                }
                arr.put(obj)
            }
            prefs.edit().putString("tasks", arr.toString()).apply()
        }

    var nextTaskId: Int
        get() = prefs.getInt("nextTaskId", 1)
        set(value) { prefs.edit().putInt("nextTaskId", value).apply() }
}
