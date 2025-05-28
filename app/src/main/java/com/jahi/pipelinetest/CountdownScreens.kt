package com.jahi.pipelinetest

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.ui.geometry.Offset
import com.jahi.pipelinetest.ui.theme.AppDimens
import com.jahi.pipelinetest.ui.theme.Slate100
import com.jahi.pipelinetest.ui.theme.Slate400
import com.jahi.pipelinetest.ui.theme.SurfaceDark
import com.jahi.pipelinetest.ui.theme.Turquoise400
import com.jahi.pipelinetest.ui.theme.Indigo200
import com.jahi.pipelinetest.ui.theme.Yellow300
import androidx.compose.ui.text.style.TextAlign

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
                Text(
                    text = title,
                    color = Slate400,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = value,
                color = Slate100,
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
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

        viewModel.events.forEach { event ->
            if (event.date.isNotBlank()) {
                val diff = viewModel.durationToEvent(event.date, now)
                if (diff != null) {
                    CountdownCard(
                        title = event.name,
                        value = if (event.showTime) {
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
}

@Composable
fun LifeHourglassScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        val current = viewModel.currentAge
        val target = viewModel.targetAge

        Text(text = "Life Hourglass", fontWeight = FontWeight.Bold)
        Text(
            text = "Every grain of sand is a moment, every year tells its own story.",
            color = Slate400,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val valid = current >= 0 && target > 0
        if (!valid) {
            Text(
                text = "Set your age in the settings menu.",
                color = Slate400,
                modifier = Modifier.padding(16.dp)
            )
        }

        val gridState = rememberLazyGridState()
        LazyVerticalGrid(
            columns = GridCells.Adaptive(80.dp),
            state = gridState,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(target + 1) { year ->
                val state = when {
                    !valid -> HourglassState.NEUTRAL
                    year < current -> HourglassState.PAST
                    year == current -> HourglassState.CURRENT
                    else -> HourglassState.FUTURE
                }
                HourglassItem(year, state)
            }
        }

        if (valid) {
            val remaining = target - current
            Text(
                text = if (remaining >= 0) {
                    "$remaining years remaining until $target"
                } else {
                    "Congrats on surpassing $target!"
                },
                color = Slate400,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

private enum class HourglassState { PAST, CURRENT, FUTURE, NEUTRAL }

@Composable
private fun HourglassItem(year: Int, state: HourglassState) {
    var hovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (hovered) 1.1f else 1f)

    val infinite = rememberInfiniteTransition()
    val grainOffset by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Restart
        )
    )

    val color = when (state) {
        HourglassState.PAST -> Slate400
        HourglassState.CURRENT -> Turquoise400
        HourglassState.FUTURE -> Indigo200
        HourglassState.NEUTRAL -> Slate400
    }

    val disabledAlpha = if (state == HourglassState.PAST) 0.4f else 1f

    Column(
        modifier = Modifier
            .padding(4.dp)
            .size(60.dp)
            .alpha(disabledAlpha)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        hovered = true
                        tryAwaitRelease()
                        hovered = false
                    }
                )
            }
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Text(
                text = when (state) {
                    HourglassState.PAST -> "\u231B" // hourglass done
                    HourglassState.CURRENT -> "\u23F3" // hourglass running
                    HourglassState.FUTURE -> "\u231B" // hourglass full
                    HourglassState.NEUTRAL -> "\u231B"
                },
                fontSize = 24.sp,
                color = color
            )
            if (state == HourglassState.CURRENT) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val y = size.height * grainOffset * 0.5f
                    val radius = 2.dp.toPx()
                    drawCircle(
                        color = Yellow300,
                        radius = radius,
                        center = Offset(size.width / 2, y)
                    )
                }
            }
        }
        Text(text = year.toString(), fontSize = 12.sp, color = color)
    }
}
