package summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun Achievement(
    eventsInRange: List<TodoItem>,
    factor: Int
) {
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    var workWeekDuration = eventsInRange.filter{ it.section == "Work" && it.completed == true}.map { it.duration }.sum().toFloat()
    var studyWeekDuration = eventsInRange.filter{ it.section == "Study" && it.completed == true}.map { it.duration }.sum().toFloat()
    var hobbyWeekDuration = eventsInRange.filter{ it.section == "Hobby" && it.completed == true}.map { it.duration }.sum().toFloat()
    var lifeWeekDuration = eventsInRange.filter{ it.section == "Life" && it.completed == true}.map { it.duration }.sum().toFloat()
//    val todaysEvents = eventsInRange.filter{it.datetime == currentDate.toString() && it.completecnt >= 21}
    var habitsCompletedWeekly: MutableList<String> = mutableListOf()

    if (workWeekDuration >= 40 * factor) {
        habitsCompletedWeekly.add("Work Maniac")
    } else if (studyWeekDuration >= 40 * factor) {
        habitsCompletedWeekly.add("Knowledge Sponge")
    } else if (hobbyWeekDuration >= 10 * factor) {
        habitsCompletedWeekly.add("Jack of All Trades")
    } else if (lifeWeekDuration >= 10 * factor) {
        habitsCompletedWeekly.add("Life Hacker")
    }

//    todaysEvents.forEach {
//        habitsCompletedWeekly.add("Habit \"" + it.primaryTask + "\" Completed")
//    }

    Column {
        Text(
            text = "Achievements",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        habitsCompletedWeekly.forEach { habit ->
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .height(40.dp)
                    .width(200.dp)
                    .background(MaterialTheme.colorScheme.tertiary),
                contentAlignment = Alignment.Center,
                content = {
                    Text(
                        text = habit,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            )
        }
    }
}