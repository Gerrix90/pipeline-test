package com.jahi.pipelinetest

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Parse a date string that may contain a full date-time or just a date.
 * Returns null if parsing fails.
 */
internal fun parseEventDateTime(dateString: String): LocalDateTime? {
    return try {
        LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    } catch (_: Exception) {
        try {
            LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay()
        } catch (_: Exception) {
            null
        }
    }
}
