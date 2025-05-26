package com.jahi.pipelinetest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.jahi.pipelinetest.ui.theme.AppDimens
import com.jahi.pipelinetest.ui.theme.Slate100
import com.jahi.pipelinetest.ui.theme.Slate400
import com.jahi.pipelinetest.ui.theme.SurfaceDark

@Composable
private fun CountdownCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    var showTitle by remember { mutableStateOf(true) }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(AppDimens.StandardPadding)
            .clickable { showTitle = !showTitle },
        colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showTitle) {
                Text(text = title, color = Slate400)
            }
            Text(
                text = value,
                color = Slate100,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CountdownsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val now by viewModel.now.collectAsState()

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        CountdownCard(
            title = "Daily Countdown",
            value = viewModel.formatTime(viewModel.durationToEndOfDay(now)),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (viewModel.showYearCountdown) {
            CountdownCard(
                title = "Year Countdown",
                value = viewModel.daysUntilEndOfYear(now).toString() + " days",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        val eventDate = viewModel.eventDate
        if (eventDate.isNotBlank()) {
            val diff = viewModel.durationToEvent(eventDate, now)
            if (diff != null) {
                CountdownCard(
                    title = viewModel.eventName,
                    value = if (viewModel.eventShowTime) {
                        viewModel.formatDuration(diff)
                    } else {
                        "${diff.toDays()} days"
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
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
