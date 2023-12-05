package summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import in_range
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.codebot.models.TodoItem
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter
import kotlin.math.max

val currentDate = LocalDate.now()

fun getMonthName(monthNumber: Int): String {
    val symbols = DateFormatSymbols()
    val monthNames = symbols.months
    return monthNames[monthNumber]
}

fun formatTimeAs24HourClock(dateTime: LocalDateTime): String {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    return dateTime.format(timeFormatter)
}

fun getMondayOfCurrentWeek(date: LocalDate): Pair<String, String> {
    val dayOfWeek = date.dayOfWeek.value
    val monday = date.minusDays((dayOfWeek - 1).toLong())
    val target_string = monday.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    val month = target_string.substring(4, 6)
    val date = target_string.substring(6, 8)
    return Pair(month, date)
}

fun getSundayOfCurrentWeek(date: LocalDate): Pair<String, String> {
    val dayOfWeek = date.dayOfWeek.value
    val sunday = date.plusDays((7 - dayOfWeek).toLong())
    val target_string = sunday.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    val month = target_string.substring(4, 6)
    val date = target_string.substring(6, 8)
    return Pair(month, date)
}

@Composable
fun Summary() {

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
                        recur = jsonItem.recur,
                        misc1 = jsonItem.misc1,
                        misc2 = 0,
                        deleted = 0,
                        starttime = "0"
                    )
                )
            }
        }
    }
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val currentWeekStartDate = currentDate.minusDays(currentDate.dayOfWeek.value.toLong() - 1)
    var currentWeekTable =
        todoListFromDb.filter { LocalDate.parse(it.datetime, formatter) in currentWeekStartDate..currentWeekStartDate.plusDays(6)
                && it.recur != "Daily" && it.recur != "Weekly"}
//    println(currentWeekTable)
    var current_iterator = currentWeekStartDate
    while (!current_iterator.isAfter(currentWeekStartDate.plusDays(6))) {

        val potentialrecur = todoListFromDb.filter { it.recur == "Daily"
                && in_range( current_iterator.format(formatter).toString(), it.datetime, it.misc1.toString())}
        for (each in potentialrecur) {
            if (todoListFromDb.find { it.datetime == current_iterator.format(formatter) && it.pid == each.id } != null) {
                continue
            }
            val newTodoItem = each.copy(datetime = current_iterator.format(formatter))
            currentWeekTable += newTodoItem
        }

        val potentialrecurweekly = todoListFromDb.filter { it.recur == "Weekly"
                && in_range( current_iterator.format(formatter).toString(), it.datetime, it.misc1.toString())
                && isSameDayOfWeek(current_iterator.format(formatter).toString(), it.datetime)
        }
        for (each in potentialrecurweekly) {
            if (todoListFromDb.find { it.datetime == current_iterator.format(formatter) && it.pid == each.id } != null) {
                continue
            }
            val newTodoItem = each.copy(datetime = current_iterator.format(formatter))
            currentWeekTable += newTodoItem
        }
        current_iterator = current_iterator.plusDays(1)
    }


    val currentMonthStartDate = currentDate.withDayOfMonth(1)
    val lastDayOfMonth = currentDate.lengthOfMonth()
    val currentMonthEndDate = currentMonthStartDate.plusDays(lastDayOfMonth.toLong() - 1)
    var currentMonthTable = todoListFromDb.filter {
        LocalDate.parse(it.datetime, formatter) in currentMonthStartDate..currentMonthEndDate
                && it.recur != "Daily" && it.recur != "Weekly"
    }
    current_iterator = currentMonthStartDate
    while (!current_iterator.isAfter(currentMonthEndDate)) {
        val potentialrecur = todoListFromDb.filter { it.recur == "Daily" &&
            in_range( current_iterator.format(formatter).toString(), it.datetime, it.misc1.toString())}
        for (each in potentialrecur) {
            if (todoListFromDb.find { it.datetime == current_iterator.format(formatter) && it.pid == each.id } != null) {
                continue
            }

            val newTodoItem = each.copy(datetime = current_iterator.format(formatter))
            currentMonthTable += newTodoItem
        }
        val potentialrecurweekly = todoListFromDb.filter { it.recur == "Weekly"
                && in_range( current_iterator.format(formatter).toString(), it.datetime, it.misc1.toString())
                && isSameDayOfWeek(current_iterator.format(formatter).toString(), it.datetime)
        }
        for (each in potentialrecurweekly) {
            if (todoListFromDb.find { it.datetime == current_iterator.format(formatter) && it.pid == each.id } != null) {
                continue
            }
            val newTodoItem = each.copy(datetime = current_iterator.format(formatter))
            currentMonthTable += newTodoItem
        }
        current_iterator = current_iterator.plusDays(1)
    }





    val currentYearStartDate = currentDate.withDayOfYear(1)
    val currentYearEndDate = currentYearStartDate.plusYears(1).minusDays(1)
    var currentYearTable = todoListFromDb.filter {
        LocalDate.parse(it.datetime, formatter) in currentYearStartDate..currentYearEndDate
                && it.recur != "Daily" && it.recur != "Weekly"
    }

    current_iterator = currentYearStartDate
    while (!current_iterator.isAfter(currentYearEndDate)) {
        val potentialrecur = todoListFromDb.filter { it.recur == "Daily"
                && in_range( current_iterator.format(formatter).toString(), it.datetime, it.misc1.toString())}
        for (each in potentialrecur) {
            if (todoListFromDb.find { it.datetime == current_iterator.format(formatter) && it.pid == each.id } != null) {
                continue
            }
            val newTodoItem = each.copy(datetime = current_iterator.format(formatter))
            currentYearTable += newTodoItem
        }
        val potentialrecurweekly = todoListFromDb.filter { it.recur == "Weekly"
                && in_range( current_iterator.format(formatter).toString(), it.datetime, it.misc1.toString())
                && isSameDayOfWeek(current_iterator.format(formatter).toString(), it.datetime)
        }
        for (each in potentialrecurweekly) {
            if (todoListFromDb.find { it.datetime == current_iterator.format(formatter) && it.pid == each.id } != null) {
                continue
            }
            val newTodoItem = each.copy(datetime = current_iterator.format(formatter))
            currentYearTable += newTodoItem
        }
        current_iterator = current_iterator.plusDays(1)
    }
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var Date1 by remember { mutableStateOf(getMondayOfCurrentWeek(currentDate).second) }
    var Date2 by remember { mutableStateOf(getSundayOfCurrentWeek(currentDate).second) }
    var currMonth by remember { mutableStateOf(getMondayOfCurrentWeek(currentDate).first) }
    var currMonth2 by remember { mutableStateOf(getSundayOfCurrentWeek(currentDate).first) }

    var currYear by remember { mutableStateOf(currentDate.format(DateTimeFormatter.ofPattern("yyyy"))) }

    LaunchedEffect(Unit) {
        while (true) {
            currentDate = LocalDate.now()
            Date1 = getMondayOfCurrentWeek(currentDate).second
            Date2 = getSundayOfCurrentWeek(currentDate).second

            currMonth = getMondayOfCurrentWeek(currentDate).first
            currMonth2 = getSundayOfCurrentWeek(currentDate).first
            currYear = currentDate.format(DateTimeFormatter.ofPattern("yyyy"))
            delay(1000)
        }
    }

    var progress by remember { mutableStateOf(0.1f) }
    var selectedSection by remember { mutableStateOf("Weekly") }
    var habit by remember { mutableStateOf("All") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            ReportTypeSelection(selectedSection) { newSection ->
                selectedSection = newSection
            }
        }

        item {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .height(100.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center

            ) {

                when (selectedSection) {
                    "Weekly" -> {
                        if (currentWeekTable.isEmpty()) {
                            progress = 0f
                        }
                        else {
                            progress = (currentWeekTable.count{it.completed == true}.toDouble() / currentWeekTable.size).toFloat()
                        }
                        Text(
                            text = "Week of " + getMonthName(currMonth.toInt() - 1) + " " + Date1 + " - " +
                                    getMonthName(currMonth2.toInt() - 1) + " " + Date2,

                            fontFamily = FontFamily.Cursive,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    "Monthly" -> {
                        if (currentMonthTable.isEmpty()) {
                            progress = 0f
                        }
                        else{

                            progress = (currentMonthTable.count{it.completed == true}.toDouble() / currentMonthTable.size).toFloat()
                        }
                        Text(
                            text = Month.of(currMonth.toInt()).toString(),
                            fontFamily = FontFamily.Cursive,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    "Annual" -> {
                        if (currentYearTable.isEmpty()) {
                            progress = 0f
                        }
                        else {
                            progress = (currentYearTable.count{it.completed == true}.toDouble() / currentYearTable.size).toFloat()
                        }
                        Text(
                            text = currYear,
                            fontFamily = FontFamily.Cursive,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        item {
            TaskProgress(
                progress = progress
            )
        }

        item {
            when (selectedSection) {

                "Weekly" -> {
                    Text(
                        text = "This Week's Focus Stats",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        item {
                            var monDuration = currentWeekTable.filter {
                                LocalDate.parse(
                                    it.datetime,
                                    formatter
                                ).dayOfWeek.toString() == "MONDAY" && it.completed
                            }.map { it.duration }.sum().toFloat()
                            var tueDuration = currentWeekTable.filter {
                                LocalDate.parse(
                                    it.datetime,
                                    formatter
                                ).dayOfWeek.toString() == "TUESDAY" && it.completed == true
                            }.map { it.duration }.sum().toFloat()
                            var wedDuration = currentWeekTable.filter {
                                LocalDate.parse(
                                    it.datetime,
                                    formatter
                                ).dayOfWeek.toString() == "WEDNESDAY" && it.completed == true
                            }.map { it.duration }.sum().toFloat()
                            var thuDuration = currentWeekTable.filter {
                                LocalDate.parse(
                                    it.datetime,
                                    formatter
                                ).dayOfWeek.toString() == "THURSDAY" && it.completed == true
                            }.map { it.duration }.sum().toFloat()
                            var friDuration = currentWeekTable.filter {
                                LocalDate.parse(
                                    it.datetime,
                                    formatter
                                ).dayOfWeek.toString() == "FRIDAY" && it.completed == true
                            }.map { it.duration }.sum().toFloat()
                            var satDuration = currentWeekTable.filter {
                                LocalDate.parse(
                                    it.datetime,
                                    formatter
                                ).dayOfWeek.toString() == "SATURDAY" && it.completed == true
                            }.map { it.duration }.sum().toFloat()
                            var sunDuration = currentWeekTable.filter {
                                LocalDate.parse(
                                    it.datetime,
                                    formatter
                                ).dayOfWeek.toString() == "SUNDAY" && it.completed == true
                            }.map { it.duration }.sum().toFloat()
                            Chart(
                                data = mapOf(
                                    Pair("Mon", monDuration),
                                    Pair("Tue", tueDuration),
                                    Pair("Wed", wedDuration),
                                    Pair("Thu", thuDuration),
                                    Pair("Fri", friDuration),
                                    Pair("Sat", satDuration),
                                    Pair("Sun", sunDuration)
                                ), barwidth = 30.dp, graphWidth = 530.dp,
                                max_value = max(0.1f, listOf(
                                    monDuration,
                                    tueDuration,
                                    wedDuration,
                                    thuDuration,
                                    friDuration,
                                    satDuration,
                                    sunDuration
                                ).max())
                            )
                        }

                        item {
                            Column {
                                HabitSelection(habit) { newSection ->
                                    habit = newSection
                                }
                                HabitCheck(habit)
                            }
                        }
                    }
                }

                "Monthly" -> {
                    var maxvalue = 0.1f
                    val groupedByDateTime = currentMonthTable
                        .groupBy { it.datetime.substring(6, 8) }
                        .mapValues { (_, events) ->
                            events.filter { it.completed }
                                .sumOf { it.duration }
                                .toFloat()
                        }
                    groupedByDateTime.forEach { _, sumDuration ->
                        if (sumDuration > maxvalue) {
                            maxvalue = sumDuration
                        }
                    }

                    val daysInMonth = currentMonthStartDate.lengthOfMonth()
                    val resultMap = (1..daysInMonth).associate { day ->
                        val dayString =
                            day.toString().padStart(2, '0') // Ensure two-digit format (e.g., "01", "02", ..., "31")
                        val sumDuration = groupedByDateTime[dayString]?.toFloat() ?: 0f
                        dayString to sumDuration
                    }

                    Text(
                        text = "This Month's Focus Stats",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    LazyRow {
                        item {
                            Chart(
                                data = resultMap,
                                barwidth = 25.dp, graphWidth = 1700.dp, max_value = maxvalue
                            )
                        }
                    }

                }

                "Annual" -> {
                    val janDuration = currentYearTable.filter {
                        LocalDate.parse(it.datetime, formatter).month == Month.JANUARY && it.completed
                    }.sumOf { it.duration }.toFloat()
                    val febDuration = currentYearTable.filter {
                        LocalDate.parse(it.datetime, formatter).month == Month.FEBRUARY && it.completed
                    }.sumOf { it.duration }.toFloat()
                    val marDuration = currentYearTable.filter {
                        LocalDate.parse(it.datetime, formatter).month == Month.MARCH && it.completed
                    }.sumOf { it.duration }.toFloat()
                    val aprDuration = currentYearTable.filter {
                        LocalDate.parse(it.datetime, formatter).month == Month.APRIL && it.completed
                    }.sumOf { it.duration }.toFloat()
                    val mayDuration = currentYearTable.filter {
                        LocalDate.parse(it.datetime, formatter).month == Month.MAY && it.completed
                    }.sumOf { it.duration }.toFloat()
                    val junDuration = currentYearTable.filter {
                        LocalDate.parse(it.datetime, formatter).month == Month.JUNE && it.completed
                    }.sumOf { it.duration }.toFloat()
                    val julDuration = currentYearTable.filter {
                        LocalDate.parse(it.datetime, formatter).month == Month.JULY && it.completed
                    }.sumOf { it.duration }.toFloat()
                    val augDuration = currentYearTable.filter {
                        LocalDate.parse(it.datetime, formatter).month == Month.AUGUST && it.completed
                    }.sumOf { it.duration }.toFloat()
                    val sepDuration = currentYearTable.filter {
                        LocalDate.parse(it.datetime, formatter).month == Month.SEPTEMBER && it.completed
                    }.sumOf { it.duration }.toFloat()
                    val octDuration = currentYearTable.filter {
                        LocalDate.parse(it.datetime, formatter).month == Month.OCTOBER && it.completed
                    }.sumOf { it.duration }.toFloat()
                    val novDuration = currentYearTable.filter {
                        LocalDate.parse(it.datetime, formatter).month == Month.NOVEMBER && it.completed
                    }.sumOf { it.duration }.toFloat()
                    val decDuration = currentYearTable.filter {
                        LocalDate.parse(it.datetime, formatter).month == Month.DECEMBER && it.completed
                    }.sumOf { it.duration }.toFloat()

                    Text(
                        text = "This Year's Focus Stats",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Chart(

                        data = mapOf(

                            Pair("Jan", janDuration),
                            Pair("Feb", febDuration),
                            Pair("Mar", marDuration),
                            Pair("Apr", aprDuration),
                            Pair("May", mayDuration),
                            Pair("Jun", junDuration),
                            Pair("Jul", julDuration),
                            Pair("Aug", augDuration),
                            Pair("Sep", sepDuration),
                            Pair("Oct", octDuration),
                            Pair("Nov", novDuration),
                            Pair("Dec", decDuration),

                            ), barwidth = 30.dp, graphWidth = 900.dp,
                        max_value = max(0.1f, listOf(
                            janDuration,
                            febDuration,
                            marDuration,
                            aprDuration,
                            mayDuration,
                            junDuration,
                            julDuration,
                            augDuration,
                            sepDuration,
                            octDuration,
                            novDuration,
                            decDuration
                        ).max())
                    )


                }
            }
        }
        item {
            when (selectedSection) {

                "Weekly" -> Achievement(currentWeekTable, 1)
                "Monthly" -> Achievement(currentMonthTable, 4)
                "Annual" -> Achievement(currentYearTable, 48)
            }
        }
    }
}
