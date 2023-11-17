
package summary

import DatabaseManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.EmptyBuildDrawCacheParams.size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Shape
//import androidx.compose.material3.md.sys.shape.corner.full.Circular
import androidx.compose.ui.draw.drawBehind

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color.red
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun HabitCheck(habit: String) {
    val manager = DatabaseManager()
                val db = manager.setupDatabase()
    val todoListFromDb: MutableList<TodoItem> = mutableListOf()

    transaction {
        TodoTable.selectAll().forEach {
            todoListFromDb.add(
                TodoItem(
                    it[TodoTable.id],
                    it[TodoTable.primaryTask],
                    it[TodoTable.secondaryTask],
                    it[TodoTable.priority],
                    it[TodoTable.completed],
                    it[TodoTable.section],
                    it[TodoTable.datetime],
                    it[TodoTable.duration]
                )
            )
        }
    }

    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val currentWeekStartDate = currentDate.minusDays(currentDate.dayOfWeek.value.toLong() - 1)
    val currentWeekTable =
        todoListFromDb.filter { LocalDate.parse(it.datetime, formatter) in currentWeekStartDate..currentWeekStartDate.plusDays(6) }

    var workTodo = currentWeekTable.filter { todoItem -> todoItem.section == "Work" }
    var studyTodo = currentWeekTable.filter { todoItem -> todoItem.section == "Study" }
    var hobbyTodo = currentWeekTable.filter { todoItem -> todoItem.section == "Hobby" }
    var lifeTodo = currentWeekTable.filter { todoItem -> todoItem.section == "Life" }

    var workCompleted = currentWeekTable.filter { todoItem -> todoItem.section == "Work" && todoItem.completed == true }
    var studyCompleted = currentWeekTable.filter { todoItem -> todoItem.section == "Study" && todoItem.completed == true}
    var hobbyCompleted = currentWeekTable.filter { todoItem -> todoItem.section == "Hobby" && todoItem.completed == true}
    var lifeCompleted = currentWeekTable.filter { todoItem -> todoItem.section == "Life" && todoItem.completed == true}

//    println("todos")
//    todoListFromDb.forEach{
//        println(it.primaryTask)
//        println(it.datetime)
//        println(it.completed)
//    }
//    println("currweek")
//    currentWeekTable.forEach{
//        println(it.primaryTask)
//    }
//    println("worktodo")
//    workTodo.forEach{
//        println(it.primaryTask)
//    }
//    println("workcompleted")
//    workCompleted.forEach{
//        println(it.primaryTask)
//    }
//    var workProgress = (todoListFromDb.count{it.section == "Work" && it.completed == true}.toDouble() / todoListFromDb.count{it.section == "Work"}.toDouble()).toFloat()
//    var studyProgress = (todoListFromDb.count{it.section == "Study" && it.completed == true}.toDouble() / todoListFromDb.count{it.section == "Study"}.toDouble()).toFloat()
//    var hobbyProgress = (todoListFromDb.count{it.section == "Hobby" && it.completed == true}.toDouble() / todoListFromDb.count{it.section == "Hobby"}.toDouble()).toFloat()
//    var lifeProgress = (todoListFromDb.count{it.section == "Life" && it.completed == true}.toDouble() / todoListFromDb.count{it.section == "Life"}.toDouble()).toFloat()

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
