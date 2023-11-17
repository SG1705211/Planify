import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.jodatime.date
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction


fun addHoursToTimeString(time: String, hoursToAdd: Int): String {
    // Parse the string to get hours and minutes
    val parts = time.split(":")
    val hours = parts[0].toInt()
    val minutes = parts[1].toInt()

    // Add the desired hours
    val totalHours = hours + hoursToAdd

    // Ensure the hours wrap around 24 if needed
    val wrappedHours = totalHours % 24

    // Convert back to a string in the desired format
    return String.format("%02d:%02d", wrappedHours, minutes)
}

fun formatDate(input: String): String {
    val year = input.substring(0, 4)
    val month = input.substring(4, 6)
    val day = input.substring(6, 8)
    return "$year-$month-$day"
}
@Composable
fun homeCalendar() {
    val currentDate = LocalDate.now()
    var month = currentDate.monthValue
    var year = currentDate.year
    var date = currentDate.dayOfMonth
    val manager = DatabaseManager()
                val db = manager.setupDatabase()
    val events = remember { mutableStateListOf<Event>()}
//    val events_reference = listOf(
//        Event(1, "2023-11-03", "08:00", "09:00", "Practice", "Discuss ongoing projects", "", 2, false, "Work"),
//        Event(1, "2023-11-02", "09:00", "11:00", "Discussion", "Discuss ongoing projects", "", 2, false, "Work"),
//        Event(1, "2023-11-02", "10:00", "11:00", "Studying", "Discuss ongoing projects", "", 2, false, "Work"),
//        Event(1, "2023-11-02", "11:00", "12:00", "Doing Assignments", "Discuss ongoing projects", "", 2, false, "Work"),
//        Event(1, "2023-11-02", "12:00", "13:00", "Reviewing", "Discuss ongoing projects", "", 2, false, "Work"),
//        Event(1, "2023-11-02", "10:00", "11:00", "Team Meeting", "Discuss ongoing projects", "", 2, false, "Work"),
//        Event(2, "2023-11-03", "15:00", "16:00", "Client Call", "Catch up call with client", "", 1, false, "Work"),
//        Event(3, "2023-11-03", "09:00", "10:00", "Workout", "Morning exercise", "", 3, false, "Personal"),
//        Event(4, "2023-11-04", "20:00", "21:00", "Dinner", "Dinner with family", "", 3, false, "Personal"),
//        Event(5, "2023-11-05", "14:00", "15:00", "Project Review", "Review project milestones", "", 2, false, "Work"),
//        Event(6, "2023-11-04", "18:00", "19:00", "Reading", "Read a book", "", 3, false, "Personal"),
//        Event(7, "2023-11-05", "12:00", "13:00", "Lunch Break", "Take a break and have lunch", "", 3, false, "Personal")
//    )

    transaction {
        events.clear()
        TodoTable.selectAll().forEach {
            events.add(
                Event(
                    it[TodoTable.id],
                    formatDate(it[TodoTable.datetime]),
                    it[TodoTable.starttime],
                    addHoursToTimeString(it[TodoTable.starttime], it[TodoTable.duration]),
                    it[TodoTable.primaryTask],
                    it[TodoTable.primaryTask],
                    it[TodoTable.secondaryTask],
                    it[TodoTable.priority],
                    false,
                    it[TodoTable.section]
                )
            )
        }
    }
    Box(
        modifier = Modifier
            .padding(16.dp)
            .size(2500.dp, 400.dp) // Adjust these values as per your desired size
            .background(Color(0xFFede6fa)), // Optional: If you want a background color
             contentAlignment = Alignment.TopEnd
    ) {
        DailyCalendar(date, month, year,events)
    }
}