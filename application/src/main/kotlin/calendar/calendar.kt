import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import net.codebot.models.TodoItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter


fun convert_date(date: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val parsedDate = LocalDate.parse(date, inputFormatter)
    return parsedDate.format(outputFormatter)
}

@Serializable
data class Event(
    val id: Int,
    var date: String,
    var startTime: String,
    var endTime: String,
    var title: String,
    val primaryTask: String,
    val secondaryTask: String,
    val priority: Int,
    var completed: Boolean,
    val section: String,
    val recur: String,
    val pid: Int,
    val misc1: Int
) {


    @Composable
    fun displayEvent() {
        Box(
            modifier = Modifier.fillMaxWidth().padding(8.dp).border(
                1.dp, Color.Gray, shape = RoundedCornerShape(4.dp)
            ), contentAlignment = Alignment.TopStart
        ) {
            Text(
                text = title, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun WeeklyCalendar(month: Int, year: Int, events_list: List<Event>) {
    val scrollState = rememberScrollState()
    val firstDayOfMonth = LocalDate.of(year, month, 1).dayOfWeek.value % 7
    val daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth()
    val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val monthNames = listOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )
    val monthName = monthNames[month - 1]
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
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dayNames.forEach { dayName ->
                Box(
                    modifier = Modifier.weight(1f), contentAlignment = Alignment.Center
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
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (j in 0..6) {
                        val isToday =
                            ((j == today.dayOfWeek.getValue()) && (currentDate == today.dayOfMonth) && (year == today.year) && (month == today.monthValue))
                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier.weight(1f).aspectRatio(1f).background(
                                if (isToday) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            ).border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
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
                                var eventsOnThisDate = events_list.filter { it.date == currentDateString
                                        && it.recur != "Daily" && it.recur != "Weekly"}
                                val potentialrecur = events_list.filter {
                                    it.recur == "Daily" && in_range(
                                        convert_date(currentDateString), convert_date(it.date), it.misc1.toString()
                                    )
                                }
                                for (each in potentialrecur) {
                                    if (events_list.find { it.date == currentDateString && it.pid == each.id } != null) {
                                        continue
                                    }
                                    val eachRecur = MutableList(1) { each.copy() }
                                    eachRecur[0].date = currentDateString
                                    if (each.date <= currentDateString) {
                                        eventsOnThisDate = eventsOnThisDate + eachRecur
                                    }
                                }


                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                val potentialrecur_Weekly = events_list.filter {
                                    it.recur == "Weekly" && in_range(
                                        convert_date(currentDateString), convert_date(it.date), it.misc1.toString()
                                    )
                                }
                                for (each in potentialrecur_Weekly) {
                                    if (LocalDate.parse(each.date, formatter).dayOfWeek == LocalDate.of(
                                            year, month, currentDate
                                        ).dayOfWeek

                                    ) {
                                        if (events_list.find { it.date == currentDateString && it.pid == each.id } != null) {
                                            continue
                                        }
                                        val eachRecur = MutableList(1) { each.copy() }
                                        eachRecur[0].date = currentDateString
                                        if (each.date <= currentDateString) {
                                            eventsOnThisDate = eventsOnThisDate + eachRecur
                                        }
                                    }
                                }

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
fun DailyCalendar(date: Int, month: Int, year: Int, events_list: List<Event>) {
    var selectedDate = LocalDate.of(year, month, date)
    var currentDateString = selectedDate.toString()
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
    var eventsOnThisDay = events_list.filter { it.date == selectedDate.toString()
            && it.recur != "Daily" && it.recur != "Weekly"}

    val potentialrecur = events_list.filter {
        it.recur == "Daily" && in_range(convert_date(currentDateString), convert_date(it.date), it.misc1.toString())
    }
    for (each in potentialrecur) {
        if (events_list.find { it.date == currentDateString && it.pid == each.id } != null) {
            continue
        }

        val eachRecur = MutableList(1) { each }
        eachRecur[0].date = currentDateString
        eventsOnThisDay += eachRecur[0]
    }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val potentialrecur_Weekly = events_list.filter {
        it.recur == "Weekly"
                && in_range(convert_date(currentDateString), convert_date(it.date), it.misc1.toString())
    }
//    println(potentialrecur_Weekly)
    for (each in potentialrecur_Weekly) {
        if (LocalDate.parse(each.date, formatter).dayOfWeek == selectedDate.dayOfWeek) {
            if (events_list.find { it.date == currentDateString && it.pid == each.id } != null) {
                continue
            }
            val eachRecur = MutableList(1) { each }
            eachRecur[0].date = currentDateString
            eventsOnThisDay = eventsOnThisDay + eachRecur
        }
    }



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
                it.startTime.substringBefore(":").toInt() <= hour && (it.endTime.substringBefore(":")
                    .toInt() > hour) || (it.endTime.substringBefore(":")
                    .toInt() == hour && (it.endTime.substringAfter(":").toInt() > 0))
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
    val events_list = remember { mutableStateListOf<Event>() }
    runBlocking {
        var result: List<TodoItem>
        events_list.clear()
        launch {
            result = fetchTodos()
            result.forEach { jsonItem ->
                events_list.add(
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
    val maxDateInMonth = LocalDate.of(year, month, 1).lengthOfMonth()
    if (date > maxDateInMonth) {
        date = maxDateInMonth
    }
    val selectedDate = LocalDate.of(year, month, date)
    if (mode == 0) {
        WeeklyCalendar(month, year, events_list)
    } else {
        DailyCalendar(date, month, year, events_list)
    }
    val today = LocalDate.now()
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter
    ) {
        Row {
            FilledTonalButton(
                onClick = {
                    mode = 1
                    date = today.dayOfMonth
                    month = today.monthValue
                    year = today.year
                }, modifier = Modifier.size(105.dp, 32.dp)
            ) {
                Text("Daily")
            }
            FilledTonalButton(
                onClick = {
                    date = today.dayOfMonth
                    month = today.monthValue
                    year = today.year
                    mode = 0
                }, modifier = Modifier.size(105.dp, 32.dp)
            ) {

                Text("Weekly")
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
                                date = LocalDate.of(year, month, 1).lengthOfMonth()
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