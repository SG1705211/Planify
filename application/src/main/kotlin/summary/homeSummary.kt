package summary

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.material.Button
import androidx.compose.runtime.*
import java.time.format.DateTimeFormatter
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import androidx.compose.foundation.Image
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.res.painterResource


//barchart
//https://github.com/developerchunk/BarGraph-JetpackCompose/tree/main/app/src/main/java/com/example/customchar


@Composable
fun DailyCalendar(date: Int, month: Int, year: Int) {
    val selectedDate = LocalDate.of(year, month, date)
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Display the date
        Text(text = dateFormatter.format(selectedDate), fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Display the hours of the day
        (0..23).forEach { hour ->
            Text(
                text = "${if (hour < 10) "0$hour" else hour}:00",
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun ButtonBox(date: String? = null, time: String? = null) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .height(50.dp)
            .width(250.dp)
            .background(MaterialTheme.colorScheme.background), // A more muted shade of blue
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date ?: time ?: "",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


@Composable
fun homeCalendar() {
    Box(
        modifier = Modifier
            .size(250.dp, 400.dp) // Adjust these values as per your desired size
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.errorContainer), // Optional: If you want a background color
        contentAlignment = Alignment.TopStart
    ) {
        DailyCalendar(date = 14, month = 10, year = 2023)
    }
}


@Composable
fun HomeSummary() {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var Date1 by remember { mutableStateOf(currentDate.format(DateTimeFormatter.ofPattern("dd"))) }
    var Date2 by remember { mutableStateOf(currentDate.plusDays(7).format(DateTimeFormatter.ofPattern("dd"))) }
    var monthNumber by remember { mutableStateOf(currentDate.format(DateTimeFormatter.ofPattern("MM")).toInt() - 1) }
    var currMonth by remember { mutableStateOf(getMonthName(monthNumber)) }
    var monthNumber2 by remember { mutableStateOf(currentDate.plusDays(7).format(DateTimeFormatter.ofPattern("MM")).toInt() - 1) }
    var currMonth2 by remember { mutableStateOf(getMonthName(monthNumber2)) }
    var currYear by remember { mutableStateOf(currentDate.format(DateTimeFormatter.ofPattern("yyyy"))) }
    var currentTimeFormatted by remember { mutableStateOf(formatTimeAs24HourClock(LocalDateTime.now())) }
    var dayProgress by remember {mutableStateOf(0.0)}
    var weekProgress by remember {mutableStateOf(0.0)}
    var monthProgress by remember {mutableStateOf(0.0)}
    var yearProgress by remember {mutableStateOf(0.0)}

    LaunchedEffect(Unit) {
        while (true) {
            currentDate = LocalDate.now()
            Date1 = currentDate.format(DateTimeFormatter.ofPattern("dd"))
            Date2 = currentDate.plusDays(7).format(DateTimeFormatter.ofPattern("dd"))
            monthNumber = currentDate.format(DateTimeFormatter.ofPattern("MM")).toInt() - 1
            currMonth = getMonthName(monthNumber)
            currYear = currentDate.format(DateTimeFormatter.ofPattern("yyyy"))
            currentTimeFormatted = formatTimeAs24HourClock(LocalDateTime.now())
            var currentTime = LocalDateTime.now()
            var daysPassed = currentDate.dayOfWeek.value - 1
            var hoursPassed = currentTime.hour
            val daysPassedInMonth = currentDate.dayOfMonth
            val totalDaysInMonth = currentDate.month.length(currentDate.isLeapYear)
            val daysPassedInYear = currentDate.dayOfYear
            val totalDaysInYear = if (currentDate.isLeapYear) 366 else 365
            dayProgress = hoursPassed / 24.0
            weekProgress = daysPassed / 7.0
            monthProgress = daysPassedInMonth.toDouble() / totalDaysInMonth
            yearProgress = daysPassedInYear.toDouble() / totalDaysInYear
            delay(1000) // Update every second or as needed
        }
    }

    Row(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
    ) {
        val backgroundPainter = painterResource("welcome_background.jpeg")
        Box(
            modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .fillMaxSize(0.65f)
                .height(175.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Image(
                painter = backgroundPainter,
                contentDescription = null, // Set a content description if needed
                contentScale = ContentScale.Crop, // Adjust the content scale as needed
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome Home",
                    fontFamily = FontFamily.Cursive,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val date = currMonth + " " + Date1 + ", " + currYear
                    ButtonBox(date = date)
                    ButtonBox(time = currentTimeFormatted)
                }
            }
        }
        Box(
            modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .height(175.dp)
                .fillMaxWidth()
                .background(Color.White),
        ) {
            val progress = 0.4f

            Row {
                Column(
                    modifier = Modifier
//                        .padding(10.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Day",
                        Modifier
                            .padding(10.dp)
                            .height(20.dp))
                    Text("Week",
                        Modifier
                            .padding(10.dp)
                            .height(20.dp))
                    Text("Month",
                        Modifier
                            .padding(10.dp)
                            .height(20.dp))
                    Text("Year",
                        Modifier
                            .padding(10.dp)
                            .height(20.dp))
                }
                Column(
                    modifier = Modifier
//                        .padding(10.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        backgroundColor = Color.LightGray,
                        color = MaterialTheme.colorScheme.primary,
                        progress = dayProgress.toFloat(),
                        modifier = Modifier
                            .padding(10.dp)
                            .height(20.dp)
                            .fillMaxWidth()
                    )

                    LinearProgressIndicator(
                        backgroundColor = Color.LightGray,
                        color = MaterialTheme.colorScheme.primary,
                        progress = weekProgress.toFloat(),
                        modifier = Modifier
                            .padding(10.dp)
                            .height(20.dp)
                            .fillMaxWidth()
                    )

                    LinearProgressIndicator(
                        backgroundColor = Color.LightGray,
                        color = MaterialTheme.colorScheme.primary,
                        progress = monthProgress.toFloat(),
                        modifier = Modifier
                            .padding(10.dp)
                            .height(20.dp)
                            .fillMaxWidth()
                    )

                    LinearProgressIndicator(
                        backgroundColor = Color.LightGray,
                        color = MaterialTheme.colorScheme.primary,
                        progress = yearProgress.toFloat(),
                        modifier = Modifier
                            .padding(10.dp)
                            .height(20.dp)
                            .fillMaxWidth()

                    )


                }

            }

        }


    }
}


