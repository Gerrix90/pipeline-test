package com.jahi.pipelinetest.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun openDateTimePicker(
    context: Context,
    value: String,
    showTime: Boolean,
    onSelected: (String) -> Unit
) {
    var dateTime = run {
        try {
            LocalDateTime.parse(value)
        } catch (_: Exception) {
            try {
                LocalDate.parse(value).atStartOfDay()
            } catch (_: Exception) {
                LocalDateTime.now()
            }
        }
    }

    DatePickerDialog(
        context,
        { _, year, month, day ->
            dateTime = dateTime.withYear(year).withMonth(month + 1).withDayOfMonth(day)
            if (showTime) {
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        dateTime = dateTime.withHour(hour).withMinute(minute)
                        onSelected(dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    },
                    dateTime.hour,
                    dateTime.minute,
                    true
                ).show()
            } else {
                onSelected(dateTime.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
            }
        },
        dateTime.year,
        dateTime.monthValue - 1,
        dateTime.dayOfMonth
    ).show()
}
