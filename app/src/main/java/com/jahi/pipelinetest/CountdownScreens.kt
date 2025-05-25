package com.jahi.pipelinetest

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun CountdownsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val now by viewModel.now.collectAsState()

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Daily Countdown", fontWeight = FontWeight.Bold)
        Text(text = viewModel.formatDuration(viewModel.durationToEndOfDay(now)))

        if (viewModel.showYearCountdown) {
            Text(text = "Year Countdown", fontWeight = FontWeight.Bold)
            Text(text = viewModel.daysUntilEndOfYear(now).toString() + " days")
        }

        val eventDate = viewModel.eventDate
        if (eventDate.isNotBlank()) {
            Text(text = viewModel.eventName, fontWeight = FontWeight.Bold)
            val diff = viewModel.durationToEvent(eventDate, now)
            if (diff != null) {
                Text(text =
                    if (viewModel.eventShowTime) viewModel.formatDuration(diff) else "${diff.toDays()} days")
            }
        }
    }
}

@Composable
fun LifeHourglassScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        val current = viewModel.currentAge
        val target = viewModel.targetAge
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
