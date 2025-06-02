package com.jahi.pipelinetest

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Parse a date string that may be in ISO_LOCAL_DATE or ISO_LOCAL_DATE_TIME format.
 * @throws DateTimeParseException if the date cannot be parsed.
 */
fun parseEventDateTime(dateString: String): LocalDateTime {
    return try {
        LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    } catch (e: DateTimeParseException) {
        LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay()
    }
}

/**
 * Parses the date string and returns null if it cannot be parsed.
 */
fun parseEventDateTimeOrNull(dateString: String): LocalDateTime? =
    try {
        parseEventDateTime(dateString)
    } catch (_: DateTimeParseException) {
        null
    }
