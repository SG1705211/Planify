import androidx.compose.foundation.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate;
import androidx.compose.material.Button
import androidx.compose.runtime.*
import java.time.format.DateTimeFormatter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import java.time.LocalTime
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Divider
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

data class Event(
    val id: Int,
    val date: String,
    var startTime: String,
    var endTime: String,
    var title: String,
    val primaryTask: String,
    val secondaryTask: String,
    val priority: Int,
    var completed: Boolean,
    val section: String
) {
    @Composable
    fun displayEvent() {
        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, Color.Gray,
                    shape = RoundedCornerShape(4.dp)), contentAlignment = Alignment.TopStart


        ) {
            Text(
                text = title, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp)
            )
        }
    }
}


@Composable
fun MonthlyCalendar(month: Int, year: Int, events: List<Event>) {
    // Get the day of the week for the first day of the month
    val scrollState = rememberScrollState()
    val firstDayOfMonth = LocalDate.of(year, month, 1).dayOfWeek.value % 7
    val daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth()

    // List of day names
    val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    // Month names list
    val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    val monthName = monthNames[month - 1]  // Arrays are 0-indexed, so subtract 1 from the month number

    // Use a vertical Column to hold the month and year, day names, and the calendar dates
    Column {
        // Month and Year
        Text(
            text = "$monthName $year",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp, top = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Day Names Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dayNames.forEach { dayName ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = dayName, fontWeight = FontWeight.Bold)
                }
            }
        }

        Column(
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            // Calendar Dates
            var currentDate = 1
            val today = LocalDate.now()

            for (i in 0..5) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (j in 0..6) {
                        // Define the condition for the current date
                        val isToday = today.monthValue == month && today.dayOfMonth == currentDate && today.year == year

                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(
                                    if (isToday) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
                        ) {
                            if ((i == 0 && j >= firstDayOfMonth) || (i > 0 && currentDate <= daysInMonth)) {
                                Text(
                                    text = currentDate.toString(),
                                    color = if (isToday) Color.Black else Color.Unspecified,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )

                                // Check for events on this date
                                val currentDateString = LocalDate.of(year, month, currentDate).toString()
                                val eventsOnThisDate = events.filter { it.date == currentDateString }


                                // Display events
                                Column(
                                    modifier = Modifier.verticalScroll(rememberScrollState())
                                ) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    eventsOnThisDate.forEach { event ->
                                        event.displayEvent()
                                    }
                                }

                                currentDate++
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun DailyCalendar(date: Int, month: Int, year: Int, events: List<Event>) {
    var selectedDate = LocalDate.of(year, month, date)

    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
    val eventsOnThisDay = events.filter { it.date == selectedDate.toString() }
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        // Display the date
        item {
            Text(text = dateFormatter.format(selectedDate), fontSize = 24.sp)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Display the hours of the day
        for (hour in 0..23) {
            val eventsThisHour = eventsOnThisDay.filter {
                it.startTime.substringBefore(":").toInt() <= hour && it.endTime.substringBefore(":").toInt() > hour
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                ) {
                    // Display the hour
                    Text(
                        text = "${if (hour < 10) "0$hour" else hour}:00",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(0.dp, 4.dp, 16.dp, 4.dp).width(60.dp)
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        // Display the events for this hour
                        eventsThisHour.forEach { event ->
                            Box(
                                modifier = Modifier.background(MaterialTheme.colorScheme.tertiaryContainer)
                                    .padding(4.dp)
                            ) {
                                Text(text = "${event.title} (${event.startTime}-${event.endTime})")
                            }
                        }
                    }

                }
            }

            // Add Divider after each hour row
            item {
                Divider(modifier = Modifier.padding(vertical = 2.dp))
            }
        }
    }
}


@Composable
fun Calendar() {
    val currentDate = LocalDate.now()
    var month by remember { mutableStateOf(currentDate.monthValue) }
    var year by remember { mutableStateOf(currentDate.year) }
    var date by remember { mutableStateOf(currentDate.dayOfMonth) }
    var mode by remember { mutableStateOf(0) }
    Database.connect("jdbc:sqlite:chinook.db")
    val events = remember { mutableStateListOf<Event>()}
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
    val maxDateInMonth = LocalDate.of(year, month, 1).lengthOfMonth()
    if (date > maxDateInMonth) {
        date = maxDateInMonth
    }

    val selectedDate = LocalDate.of(year, month, date)
    if (mode == 0) {
        MonthlyCalendar(month, year, events)
    } else {
        DailyCalendar(date, month, year, events)
    }
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter
    ) {
        Row {
            FilledTonalButton(
                onClick = {
                    mode = 1
                }, modifier = Modifier.size(105.dp, 32.dp)
            ) {
                Text("Daily")
            }
            FilledTonalButton(
                onClick = {
                    mode = 0
                }, modifier = Modifier.size(105.dp, 32.dp)
            ) {
                Text("Monthly")
            }

        }

    }
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd
    ) {
        Row {
            FilledTonalButton(
                onClick = {
                    if (mode == 0) {
                        if (month > 1) month-- else {
                            month = 12
                            year--
                        }
                    } else {
                        if (date > 1) {
                            date--
                        } else {
                            if (month > 1) {
                                month--
                                date = LocalDate.of(year, month, 1)
                                    .lengthOfMonth()  // Set date to the last day of the previous month
                            } else {
                                month = 12
                                year--
                                date = 31
                            }
                        }
                    }

                }, modifier = Modifier.size(70.dp, 30.dp)
            ) {
                Text("<")
            }
            FilledTonalButton(
                onClick = {
                    if (mode == 0) {
                        if (month < 12) month++ else {
                            month = 1
                            year++
                        }
                    } else {
                        if (date < selectedDate.lengthOfMonth()) {
                            date++
                        } else {
                            if (month < 12) {
                                month++
                                date = 1
                            } else {
                                month = 1
                                date = 1
                                year++
                            }
                        }
                    }
                }, modifier = Modifier.size(70.dp, 30.dp)
            ) {
                Text(">")
            }

        }
    }
}