
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.codebot.models.TodoItem
import java.time.LocalDate

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
        var result: List<TodoItem>
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
                        pid = jsonItem.pid,
                        misc1 = jsonItem.misc1

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