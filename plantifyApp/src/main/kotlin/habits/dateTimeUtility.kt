import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun detectValidDateTime(input: String): Boolean {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return try {
        LocalTime.parse(input, formatter)
        true
    } catch (e: DateTimeParseException) {
        false
    }
}

fun detectValidInteger(value: String): Boolean {
    return try {
        value.toInt()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

fun dateIsInRange(date: String, start: String, end: String): Boolean {
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val targetDate = LocalDate.parse(date, formatter)
    val startDate = LocalDate.parse(start, formatter)
    val endDate = LocalDate.parse(end, formatter)
    return !targetDate.isBefore(startDate) && !targetDate.isAfter(endDate)
}
