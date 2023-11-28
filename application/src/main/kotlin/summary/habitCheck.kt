
package summary

import DatabaseManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.EmptyBuildDrawCacheParams.size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
//import androidx.compose.material3.md.sys.shape.corner.full.Circular
import androidx.compose.ui.draw.drawBehind
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class TodoItemjson(
    val id: Int,
    val primaryTask: String,
    val secondaryTask: String,
    val priority: Int,
    val completed: Boolean,
    val datetime: String,
    val section: String,
    val duration: Int,
    val starttime: String,
    val recur: String,
    var pid: Int,
    val deleted: Int,
    val misc1: Int,
    val misc2: Int
)

suspend fun fetchTodo_check(): List<TodoItemjson> {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get("http://localhost:8080/todos")
    val jsonString = response.bodyAsText()
    client.close()
    return Json.decodeFromString(jsonString)
}

@Composable
fun HabitCheck(habit: String) {
    val manager = DatabaseManager()
                val db = manager.setupDatabase()
    val todoListFromDb: MutableList<TodoItem> = mutableListOf()
    runBlocking {
        var result: List<TodoItemjson>
        todoListFromDb.clear()
        launch {
            result = fetchTodo_check()
            result.forEach { jsonItem ->
                todoListFromDb.add(
                    TodoItem(
                        id = jsonItem.id,
                        primaryTask = jsonItem.primaryTask,
                        secondaryTask = jsonItem.secondaryTask,
                        priority = jsonItem.priority,
                        completed = jsonItem.completed,
                        section = jsonItem.section,
                        datetime = jsonItem.datetime,
                        duration = jsonItem.duration,
                        pid = jsonItem.pid,
                        recur = jsonItem.recur
                    )
                )
            }
        }
    }

    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val currentWeekStartDate = currentDate.minusDays(currentDate.dayOfWeek.value.toLong() - 1)
    var currentWeekTable =
        todoListFromDb.filter { LocalDate.parse(it.datetime, formatter) in currentWeekStartDate..currentWeekStartDate.plusDays(6)
                && it.recur != "Daily" && it.recur != "Monthly"}
    // add recurring event
    var current_iterator = currentWeekStartDate
    while (!current_iterator.isAfter(currentWeekStartDate.plusDays(6))) {
        val potentialrecur = todoListFromDb.filter { it.recur == "Daily" }
        for (each in potentialrecur) {
            if (todoListFromDb.find { it.datetime == current_iterator.format(formatter) && it.pid == each.id } != null) {
                continue
            }
            val newTodoItem = each.copy(datetime = current_iterator.format(formatter))
            currentWeekTable += newTodoItem
            println(currentWeekTable)
        }
        current_iterator = current_iterator.plusDays(1)
    }
    // *********
    var workTodo = currentWeekTable.filter { todoItem -> todoItem.section == "Work" }
    var studyTodo = currentWeekTable.filter { todoItem -> todoItem.section == "Study" }
    var hobbyTodo = currentWeekTable.filter { todoItem -> todoItem.section == "Hobby" }
    var lifeTodo = currentWeekTable.filter { todoItem -> todoItem.section == "Life" }

    var workCompleted = currentWeekTable.filter { todoItem -> todoItem.section == "Work" && todoItem.completed == true }
    var studyCompleted = currentWeekTable.filter { todoItem -> todoItem.section == "Study" && todoItem.completed == true}
    var hobbyCompleted = currentWeekTable.filter { todoItem -> todoItem.section == "Hobby" && todoItem.completed == true}
    var lifeCompleted = currentWeekTable.filter { todoItem -> todoItem.section == "Life" && todoItem.completed == true}

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val circlePositions = listOf(
            Offset(100f, 50f),
            Offset(200f, 50f),
            Offset(300f, 50f),
            Offset(50f, 175f),
            Offset(150f, 175f),
            Offset(250f, 175f),
            Offset(350f, 175f)
        )
        val Weekday = listOf(
            "Mon",
            "Tue",
            "Wed",
            "Thu",
            "Fri",
            "Sat",
            "Sun"
        )
        val allcomplete = mutableListOf(
            false,
            false,
            false,
            false,
            false,
            false,
            false
        )
        val workcomplete = mutableListOf(
            false,
            false,
            false,
            false,
            false,
            false,
            false
        )
        val studycomplete = mutableListOf(
            false,
            false,
            false,
            false,
            false,
            false,
            false
        )
        val hobbycomplete = mutableListOf(
            false,
            false,
            false,
            false,
            false,
            false,
            false
        )
        val lifecomplete = mutableListOf(
            false,
            false,
            false,
            false,
            false,
            false,
            false
        )

        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
//        println(LocalDate.parse(workTodo[0].datetime, formatter).dayOfWeek.toString())
//        println(workTodo[0].completed)
//        println(LocalDate.parse(workTodo[1].datetime, formatter).dayOfWeek.toString())
//        println(workTodo[1].completed)
//        println(LocalDate.parse(workTodo[2].datetime, formatter).dayOfWeek.toString())
//        println(workTodo[2].completed)
//        println(LocalDate.parse(workTodo[3].datetime, formatter).dayOfWeek.toString())
//        println(workTodo[3].completed)
        listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY").forEachIndexed {index, day ->
            allcomplete[index] = (currentWeekTable.count{ LocalDate.parse(it.datetime, formatter).dayOfWeek.toString() == day}) == (currentWeekTable.count{it.completed == true && LocalDate.parse(it.datetime, formatter).dayOfWeek.toString() == day})
            workcomplete[index] = (workTodo.count{ LocalDate.parse(it.datetime, formatter).dayOfWeek.toString() == day}) == (workCompleted.count{LocalDate.parse(it.datetime, formatter).dayOfWeek.toString() == day})
            studycomplete[index] = (studyTodo.count{ LocalDate.parse(it.datetime, formatter).dayOfWeek.toString() == day}) == (studyCompleted.count{LocalDate.parse(it.datetime, formatter).dayOfWeek.toString() == day})
            hobbycomplete[index] = (hobbyTodo.count{ LocalDate.parse(it.datetime, formatter).dayOfWeek.toString() == day}) == (hobbyCompleted.count{LocalDate.parse(it.datetime, formatter).dayOfWeek.toString() == day})
            lifecomplete[index] = (lifeTodo.count{ LocalDate.parse(it.datetime, formatter).dayOfWeek.toString() == day}) == (lifeCompleted.count{LocalDate.parse(it.datetime, formatter).dayOfWeek.toString() == day})
    }

        val radius = 60f
        circlePositions.forEachIndexed {index, position ->
            var habitCompleted: MutableList<Boolean> = allcomplete
            if (habit == "Work") {
                habitCompleted = workcomplete
            } else if (habit == "Study") {
                habitCompleted = studycomplete
            } else if (habit == "Hobby") {
                habitCompleted = hobbycomplete
            } else if (habit == "Life") {
                habitCompleted = lifecomplete
            }
            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .offset(position.x.dp, y=position.y.dp)
                    .drawBehind {
                        if (habitCompleted[index]) {
                            drawCircle(
                                color = Color(0xFF476810), //primary
                                radius = radius
                            )
                        } else {
                            drawCircle(
                                color = Color(0xFFC5C8B9),
                                radius = radius
                            )
                        }
                    },
                text = Weekday[index],
                color =
                    if (habitCompleted[index]) {
                        Color(0xFFFFFFFF)//onPrimary
                    } else {
                        Color.Black
                }
            )
        }
    }
}
