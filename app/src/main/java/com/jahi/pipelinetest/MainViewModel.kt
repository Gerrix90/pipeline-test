package com.jahi.pipelinetest

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainViewModel(private val prefs: Prefs) : ViewModel() {
    var screen by mutableStateOf(0)
        private set

    var showSettings by mutableStateOf(false)
        private set

    private val _now = MutableStateFlow(Instant.now())
    val now: StateFlow<Instant> = _now.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                _now.value = Instant.now()
                delay(1000)
            }
        }
    }

    fun selectScreen(index: Int) { screen = index }
    fun openSettings() { showSettings = true }
    fun closeSettings() { showSettings = false }

    // Preference wrappers
    var showYearCountdown: Boolean
        get() = prefs.showYearCountdown
        set(value) { prefs.showYearCountdown = value }

    var eventName: String
        get() = prefs.eventName
        set(value) { prefs.eventName = value }

    var eventDate: String
        get() = prefs.eventDate
        set(value) { prefs.eventDate = value }

    var eventShowTime: Boolean
        get() = prefs.eventShowTime
        set(value) { prefs.eventShowTime = value }

    var currentAge: Int
        get() = prefs.currentAge
        set(value) { prefs.currentAge = value }

    var targetAge: Int
        get() = prefs.targetAge
        set(value) { prefs.targetAge = value }

    fun durationToEndOfDay(now: Instant = this.now.value): Duration {
        val z = ZoneId.systemDefault()
        val endOfDay = LocalDate.now(z).plusDays(1).atStartOfDay(z).toInstant()
        return Duration.between(now, endOfDay)
    }

    fun daysUntilEndOfYear(now: Instant = this.now.value): Long {
        val z = ZoneId.systemDefault()
        val endOfYear = LocalDate.now(z).withMonth(12).withDayOfMonth(31).plusDays(1)
            .atStartOfDay(z).toInstant()
        return Duration.between(now, endOfYear).toDays()
    }

    fun durationToEvent(dateStr: String, now: Instant = this.now.value): Duration? {
        val trimmed = dateStr.trim()
        val z = ZoneId.systemDefault()
        val dateTime = try {
            LocalDateTime.parse(trimmed, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (e: Exception) {
            try {
                LocalDate.parse(trimmed, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay()
            } catch (e2: Exception) {
                return null
            }
        }
        val instant = dateTime.atZone(z).toInstant()
        return Duration.between(now, instant)
    }

    fun formatDuration(d: Duration): String {
        var seconds = d.seconds
        val days = seconds / 86400
        seconds %= 86400
        val hours = seconds / 3600
        seconds %= 3600
        val minutes = seconds / 60
        seconds %= 60
        return String.format("%d days %02d:%02d:%02d", days, hours, minutes, seconds)
    }

    fun formatTime(d: Duration): String {
        var seconds = d.seconds
        val hours = seconds / 3600
        seconds %= 3600
        val minutes = seconds / 60
        seconds %= 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}

class MainViewModelFactory(private val prefs: Prefs) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(prefs) as T
    }
}
