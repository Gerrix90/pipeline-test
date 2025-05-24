package com.jahi.pipelinetest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CountdownsScreen(
    prefs: Prefs,
    modifier: Modifier = Modifier
) {
    var now by remember { mutableStateOf(Instant.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = Instant.now()
            delay(1000)
        }
    }

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Daily Countdown", fontWeight = FontWeight.Bold)
        Text(text = formatDuration(durationToEndOfDay(now)))

        if (prefs.showYearCountdown) {
            Text(text = "Year Countdown", fontWeight = FontWeight.Bold)
            Text(text = daysUntilEndOfYear(now).toString() + " days")
        }

        val eventDate = prefs.eventDate
        if (eventDate.isNotBlank()) {
            Text(text = prefs.eventName, fontWeight = FontWeight.Bold)
            val diff = durationToEvent(now, eventDate)
            if (diff != null) {
                Text(text =
                    if (prefs.eventShowTime) formatDuration(diff) else "${diff.toDays()} days")
            }
        }
    }
}

private fun durationToEndOfDay(now: Instant): Duration {
    val z = ZoneId.systemDefault()
    val endOfDay = LocalDate.now(z).plusDays(1).atStartOfDay(z).toInstant()
    return Duration.between(now, endOfDay)
}

private fun daysUntilEndOfYear(now: Instant): Long {
    val z = ZoneId.systemDefault()
    val endOfYear = LocalDate.now(z).withMonth(12).withDayOfMonth(31).plusDays(1)
        .atStartOfDay(z).toInstant()
    return Duration.between(now, endOfYear).toDays()
}

private fun durationToEvent(now: Instant, dateStr: String): Duration? {
    return try {
        val date = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val z = ZoneId.systemDefault()
        val instant = date.atZone(z).toInstant()
        Duration.between(now, instant)
    } catch (e: Exception) {
        null
    }
}

private fun formatDuration(d: Duration): String {
    var seconds = d.seconds
    val days = seconds / 86400
    seconds %= 86400
    val hours = seconds / 3600
    seconds %= 3600
    val minutes = seconds / 60
    seconds %= 60
    return String.format("%d days %02d:%02d:%02d", days, hours, minutes, seconds)
}

@Composable
fun LifeHourglassScreen(prefs: Prefs, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        val current = prefs.currentAge
        val target = prefs.targetAge
        Text(text = "Life Hourglass", fontWeight = FontWeight.Bold)
        for (year in 1..target) {
            val label = when {
                year < current -> "Year $year \uD83D\uDD73" // empty hourglass emoji
                year == current -> "Year $year \u23F3" // hourglass not done
                else -> "Year $year \u231B" // full hourglass
            }
            Text(text = label, fontSize = 18.sp)
        }
    }
}
