package com.jahi.pipelinetest

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

internal fun parseEventDateTimeOrNull(dateString: String): LocalDateTime? {
    return try {
        LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    } catch (_: DateTimeParseException) {
        try {
            LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
                .atStartOfDay()
        } catch (_: DateTimeParseException) {
            null
        }
    }
}
