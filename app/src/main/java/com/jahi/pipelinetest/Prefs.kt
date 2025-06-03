package com.jahi.pipelinetest

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.jahi.pipelinetest.model.CustomEvent
import com.jahi.pipelinetest.model.Task
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Prefs(context: Context) {
    
    companion object {
        private const val PREFS_NAME = "time_fomo_prefs"
        private const val ENCRYPTED_PREFS_NAME = "encrypted_time_fomo_prefs"
        private const val ENCRYPTED_PREFS_FALLBACK_NAME = "encrypted_time_fomo_prefs_fallback"
        private const val LOG_TAG = "Prefs"
        private const val ERROR_MESSAGE = "Failed to create encrypted preferences, falling back to regular"
        
        // Preference keys
        private const val KEY_SHOW_YEAR_COUNTDOWN = "showYearCountdown"
        private const val KEY_CUSTOM_EVENTS = "customEvents"
        private const val KEY_CURRENT_AGE = "currentAge"
        private const val KEY_TARGET_AGE = "targetAge"
        private const val KEY_TASKS = "tasks"
        private const val KEY_NEXT_TASK_ID = "nextTaskId"
        private const val KEY_ELEVEN_LABS_API_KEY = "elevenLabsApiKey"
        
        // Default values
        private const val DEFAULT_CURRENT_AGE = 30
        private const val DEFAULT_TARGET_AGE = 80
        private const val DEFAULT_NEXT_TASK_ID = 1
    }
    
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val encryptedPrefs: SharedPreferences by lazy {
        try {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                ENCRYPTED_PREFS_NAME,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(LOG_TAG, ERROR_MESSAGE, e)
            context.getSharedPreferences(ENCRYPTED_PREFS_FALLBACK_NAME, Context.MODE_PRIVATE)
        }
    }

    var showYearCountdown: Boolean
        get() = prefs.getBoolean(KEY_SHOW_YEAR_COUNTDOWN, true)
        set(value) { prefs.edit().putBoolean(KEY_SHOW_YEAR_COUNTDOWN, value).apply() }

    var customEvents: MutableList<CustomEvent>
        get() {
            val json = prefs.getString(KEY_CUSTOM_EVENTS, "[]") ?: "[]"
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
            prefs.edit().putString(KEY_CUSTOM_EVENTS, arr.toString()).apply()
        }

    var currentAge: Int
        get() = prefs.getInt(KEY_CURRENT_AGE, DEFAULT_CURRENT_AGE)
        set(value) { prefs.edit().putInt(KEY_CURRENT_AGE, value).apply() }

    var targetAge: Int
        get() = prefs.getInt(KEY_TARGET_AGE, DEFAULT_TARGET_AGE)
        set(value) { prefs.edit().putInt(KEY_TARGET_AGE, value).apply() }

    var tasks: MutableList<Task>
        get() {
            val json = prefs.getString(KEY_TASKS, "[]") ?: "[]"
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
            prefs.edit().putString(KEY_TASKS, arr.toString()).apply()
        }

    var nextTaskId: Int
        get() = prefs.getInt(KEY_NEXT_TASK_ID, DEFAULT_NEXT_TASK_ID)
        set(value) { prefs.edit().putInt(KEY_NEXT_TASK_ID, value).apply() }

    var elevenLabsApiKey: String
        get() = encryptedPrefs.getString(KEY_ELEVEN_LABS_API_KEY, "") ?: ""
        set(value) { encryptedPrefs.edit().putString(KEY_ELEVEN_LABS_API_KEY, value).apply() }
}
