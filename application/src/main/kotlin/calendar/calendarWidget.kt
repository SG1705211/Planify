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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.jodatime.date
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun addHoursToTimeString(time: String, hoursToAdd: Int): String {
    val parts = time.split(":")
    val hours = parts[0].toInt()
    val minutes = parts[1].toInt()
    val totalHours = hours + hoursToAdd
    val wrappedHours = totalHours % 24
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
    val events = remember { mutableStateListOf<Event>() }
    runBlocking {
        var result: List<TodoItemjson>
        events.clear()
        launch {
            result = fetchTodos()
            result.forEach { jsonItem ->
                events.add(
                    Event(
                        id = jsonItem.id,
                        primaryTask = jsonItem.primaryTask,
                        secondaryTask = jsonItem.secondaryTask,
                        priority = jsonItem.priority,
                        completed = jsonItem.completed,
                        section = jsonItem.section,
                        date = formatDate(jsonItem.datetime),
                        startTime = jsonItem.starttime,
                        endTime = addHoursToTimeString(jsonItem.starttime, jsonItem.duration),
                        title = jsonItem.primaryTask,
                        recur = jsonItem.recur,
                        pid = jsonItem.pid
                    )
                )
            }
        }
    }
    Box(
        modifier = Modifier.padding(16.dp).size(2500.dp, 400.dp)
            .background(Color(0xFFede6fa)),
        contentAlignment = Alignment.TopEnd
    ) {
        DailyCalendar(date, month, year, events)
    }
}