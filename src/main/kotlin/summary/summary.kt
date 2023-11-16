package summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.font.FontFamily
import kotlinx.coroutines.delay
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.text.DateFormatSymbols
import java.time.Month

val currentDate = LocalDate.now()
val currentDateTime = LocalDateTime.now()

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
    Class.forName("org.sqlite.JDBC")
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

    val (monday_month, monday_day) = getMondayOfCurrentWeek(currentDate)
    val (sunday_month, sunday_day) = getSundayOfCurrentWeek(currentDate)

    var completed = todoListFromDb.filter { it.completed == true }

    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val currentWeekStartDate = currentDate.minusDays(currentDate.dayOfWeek.value.toLong() - 1)
    val currentWeekTable =
        todoListFromDb.filter { LocalDate.parse(it.datetime, formatter) in currentWeekStartDate..currentDate }


// Current Month
    val currentMonthStartDate = currentDate.withDayOfMonth(1)
    val lastDayOfMonth = currentDate.lengthOfMonth()
    val currentMonthEndDate = currentMonthStartDate.plusDays(lastDayOfMonth.toLong() - 1)
    val currentMonthTable = todoListFromDb.filter {
        LocalDate.parse(it.datetime, formatter) in currentMonthStartDate..currentMonthEndDate
    }

// Current Year
    val currentYearStartDate = currentDate.withDayOfYear(1)
    val currentYearEndDate = currentYearStartDate.plusYears(1).minusDays(1)
    val currentYearTable = todoListFromDb.filter {
        LocalDate.parse(it.datetime, formatter) in currentYearStartDate..currentYearEndDate
    }

    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var Date1 by remember { mutableStateOf(getMondayOfCurrentWeek(currentDate).second) }
    var Date2 by remember { mutableStateOf(getSundayOfCurrentWeek(currentDate).second) }
    var monthNumber by remember { mutableStateOf(currentDate.format(DateTimeFormatter.ofPattern("MM")).toInt() - 1) }
    var currMonth by remember { mutableStateOf(getMondayOfCurrentWeek(currentDate).first) }
    var currMonth2 by remember { mutableStateOf(getSundayOfCurrentWeek(currentDate).first) }
    var monthNumber2 by remember {
        mutableStateOf(
            currentDate.plusDays(7).format(DateTimeFormatter.ofPattern("MM")).toInt() - 1
        )
    }

    var currYear by remember { mutableStateOf(currentDate.format(DateTimeFormatter.ofPattern("yyyy"))) }

    LaunchedEffect(Unit) {
        while (true) {
            currentDate = LocalDate.now()
            Date1 = getMondayOfCurrentWeek(currentDate).second
            Date2 = getSundayOfCurrentWeek(currentDate).second
//            monthNumber = currentDate.format(DateTimeFormatter.ofPattern("MM")).toInt() - 1
            currMonth = getMondayOfCurrentWeek(currentDate).first
            currMonth2 = getSundayOfCurrentWeek(currentDate).first
            currYear = currentDate.format(DateTimeFormatter.ofPattern("yyyy"))
            delay(1000) // Update every second or as needed
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
                    "Weekly" -> Text(
                        text = "Week of " + getMonthName(currMonth.toInt() - 1) + " " + Date1 + " - " +
                                getMonthName(currMonth2.toInt() - 1) + " " + Date2,

                        fontFamily = FontFamily.Cursive,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    "Monthly" -> Text(
                        text = currMonth,
                        fontFamily = FontFamily.Cursive,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    "Annual" -> Text(
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

        item {
            TaskProgress(
                progress = progress
            )
        }

        item {
            when (selectedSection) {
                "Weekly" -> LazyRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    item {
                        var monDuration = currentWeekTable.filter{ LocalDate.parse(it.datetime, formatter).dayOfWeek.toString() == "MONDAY" && it.completed == true}.map { it.duration }.sum().toFloat()
                        var tueDuration = currentWeekTable.filter{ LocalDate.parse(it.datetime, formatter).dayOfWeek.toString() == "TUESDAY"&& it.completed == true}.map { it.duration }.sum().toFloat()
                        var wedDuration = currentWeekTable.filter{ LocalDate.parse(it.datetime, formatter).dayOfWeek.toString() == "WEDNESDAY"&& it.completed == true}.map { it.duration }.sum().toFloat()
                        var thuDuration = currentWeekTable.filter{ LocalDate.parse(it.datetime, formatter).dayOfWeek.toString() == "THURSDAY"&& it.completed == true}.map { it.duration }.sum().toFloat()
                        var friDuration = currentWeekTable.filter{ LocalDate.parse(it.datetime, formatter).dayOfWeek.toString() == "FRIDAY"&& it.completed == true}.map { it.duration }.sum().toFloat()
                        var satDuration = currentWeekTable.filter{ LocalDate.parse(it.datetime, formatter).dayOfWeek.toString() == "SATURDAY"&& it.completed == true}.map { it.duration }.sum().toFloat()
                        var sunDuration = currentWeekTable.filter{ LocalDate.parse(it.datetime, formatter).dayOfWeek.toString() == "SUNDAY"&& it.completed == true}.map { it.duration }.sum().toFloat()
                        println(monDuration)
                        Chart(
                            data = mapOf(
                                Pair("Mon", monDuration),
                                Pair("Tue", tueDuration),
                                Pair("Wed", wedDuration),
                                Pair("Thu", thuDuration),
                                Pair("Fri", friDuration),
                                Pair("Sat", satDuration),
                                Pair("Sun", sunDuration),
//                                Pair("Mon", monDuration),
//                                Pair("Tue", tueDuration),
//                                Pair("Wed", wedDuration),
//                                Pair("Thu", thuDuration),
//                                Pair("Fri", friDuration),
//                                Pair("Sat", satDuration),
//                                Pair("Sun", sunDuration),
                            ), barwidth = 30.dp, graphWidth = 530.dp,
                            max_value = 0.3f
//                            listOf(monDuration, tueDuration, wedDuration, thuDuration, friDuration, satDuration, sunDuration).max()
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
//                }

//                "Monthly" -> {
//                    var maxvalue = 0.0f
//                    val groupedByDateTime = currentMonthTable
//                        .groupBy { it.datetime.substring(6, 8) }
//                        .mapValues { (_, events) ->
//                            events.filter { it.completed }
//                                .sumOf { it.duration }
//                                .toFloat()
//                        }
//                    groupedByDateTime.forEach { _, sumDuration ->
//                        if (sumDuration > maxvalue) {
//                            maxvalue = sumDuration
//                        }
//                    }
//                    val resultMap = groupedByDateTime.map { (datetime, sumDuration) ->
//                        Pair(datetime.toString(), sumDuration.toFloat())
//                    }.toMap()

//                    Chart(
//                        data = resultMap,
//                        barwidth = 50.dp, graphWidth = 530.dp, max_value = maxvalue
//                    )
//                }

//                "Annual" -> {
//                    val janDuration = currentWeekTable.filter {
//                        LocalDate.parse(it.datetime, formatter).month == Month.JANUARY && it.completed
//                    }.sumOf { it.duration }.toFloat()
//                    val febDuration = currentWeekTable.filter {
//                        LocalDate.parse(it.datetime, formatter).month == Month.FEBRUARY && it.completed
//                    }.sumOf { it.duration }.toFloat()
//                    val marDuration = currentWeekTable.filter {
//                        LocalDate.parse(it.datetime, formatter).month == Month.MARCH && it.completed
//                    }.sumOf { it.duration }.toFloat()
//                    val aprDuration = currentWeekTable.filter {
//                        LocalDate.parse(it.datetime, formatter).month == Month.APRIL && it.completed
//                    }.sumOf { it.duration }.toFloat()
//                    val mayDuration = currentWeekTable.filter {
//                        LocalDate.parse(it.datetime, formatter).month == Month.MAY && it.completed
//                    }.sumOf { it.duration }.toFloat()
//                    val junDuration = currentWeekTable.filter {
//                        LocalDate.parse(it.datetime, formatter).month == Month.JUNE && it.completed
//                    }.sumOf { it.duration }.toFloat()
//                    val julDuration = currentWeekTable.filter {
//                        LocalDate.parse(it.datetime, formatter).month == Month.JULY && it.completed
//                    }.sumOf { it.duration }.toFloat()
//                    val augDuration = currentWeekTable.filter {
//                        LocalDate.parse(it.datetime, formatter).month == Month.AUGUST && it.completed
//                    }.sumOf { it.duration }.toFloat()
//                    val sepDuration = currentWeekTable.filter {
//                        LocalDate.parse(it.datetime, formatter).month == Month.SEPTEMBER && it.completed
//                    }.sumOf { it.duration }.toFloat()
//                    val octDuration = currentWeekTable.filter {
//                        LocalDate.parse(it.datetime, formatter).month == Month.OCTOBER && it.completed
//                    }.sumOf { it.duration }.toFloat()
//                    val novDuration = currentWeekTable.filter {
//                        LocalDate.parse(it.datetime, formatter).month == Month.NOVEMBER && it.completed
//                    }.sumOf { it.duration }.toFloat()
//                    val decDuration = currentWeekTable.filter {
//                        LocalDate.parse(it.datetime, formatter).month == Month.DECEMBER && it.completed
//                    }.sumOf { it.duration }.toFloat()
//
//                    Chart(
//
//                        data = mapOf(
//
//                            Pair("Jan", janDuration),
//                            Pair("Feb", febDuration),
//                            Pair("Mar", marDuration),
//                            Pair("Apr", aprDuration),
//                            Pair("May", mayDuration),
//                            Pair("Jun", junDuration),
//                            Pair("Jul", julDuration),
//                            Pair("Aug", augDuration),
//                            Pair("Sep", sepDuration),
//                            Pair("Oct", octDuration),
//                            Pair("Nov", novDuration),
//                            Pair("Dec", decDuration),
//
//                            ), barwidth = 30.dp, graphWidth = 900.dp,
//                        max_value = listOf(
//                            janDuration,
//                            febDuration,
//                            marDuration,
//                            aprDuration,
//                            mayDuration,
//                            junDuration,
//                            julDuration,
//                            augDuration,
//                            sepDuration,
//                            octDuration,
//                            novDuration,
//                            decDuration
//                        ).max()
//                    )

                }
            }
        }
        item {
            when (selectedSection) {

                "Weekly" -> Achievement(currentWeekTable, 1)
//                "Monthly" -> Achievement(currentMonthTable, 4)
//                "Annual" -> Achievement(currentYearTable, 48)
            }
        }
    }
}

//barchart
//https://github.com/developerchunk/BarGraph-JetpackCompose/tree/main/app/src/main/java/com/example/customchar
