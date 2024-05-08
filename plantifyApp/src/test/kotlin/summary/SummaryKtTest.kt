package summary

import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals


class SummaryKtTest{


    @Test
    fun getMonthName() {
        assertEquals("January", getMonthName(0))
        assertEquals("February", getMonthName(1))
        assertEquals("March", getMonthName(2))
        assertEquals("April", getMonthName(3))
        assertEquals("May", getMonthName(4))
        assertEquals("June", getMonthName(5))
        assertEquals("July", getMonthName(6))
        assertEquals("August", getMonthName(7))
        assertEquals("September", getMonthName(8))
        assertEquals("October", getMonthName(9))
        assertEquals("November", getMonthName(10))
        assertEquals("December", getMonthName(11))
    }

    @Test
    fun formatTimeAs24HourClock() {
        val input1 = LocalDateTime.of(2023, 12, 4, 15, 0)
        val input2 = LocalDateTime.of(2023, 12, 5, 12, 9)
        val input3 = LocalDateTime.of(2023, 12, 2, 0, 59)
        val input4 = LocalDateTime.of(2023, 12, 1, 0, 0)
        val input5 = LocalDateTime.of(2023, 12, 10, 23, 59)
        val input6 = LocalDateTime.of(2023, 12, 10, 1, 0)
        val input7 = LocalDateTime.of(2023, 12, 9, 0, 1)
        val output1 = formatTimeAs24HourClock(input1)
        val output2 = formatTimeAs24HourClock(input2)
        val output3 = formatTimeAs24HourClock(input3)
        val output4 = formatTimeAs24HourClock(input4)
        val output5 = formatTimeAs24HourClock(input5)
        val output6 = formatTimeAs24HourClock(input6)
        val output7 = formatTimeAs24HourClock(input7)
        assertEquals("15:00",output1)
        assertEquals("12:09",output2)
        assertEquals("00:59",output3)
        assertEquals("00:00",output4)
        assertEquals("23:59",output5)
        assertEquals("01:00",output6)
        assertEquals("00:01",output7)
    }

    @Test
    fun getMondayOfCurrentWeek() {
        val input1 = LocalDate.of(2023, 9, 1) //Fri
        val input2 = LocalDate.of(2023, 7, 1) //Sat
        val input3 = LocalDate.of(2021, 1, 1) //Fri
        val input4 = LocalDate.of(2023, 12, 5) //Tue
        val input5 = LocalDate.of(2023, 2, 5) //Sun
        val output1 = getMondayOfCurrentWeek(input1)
        val output2 = getMondayOfCurrentWeek(input2)
        val output3 = getMondayOfCurrentWeek(input3)
        val output4 = getMondayOfCurrentWeek(input4)
        val output5 = getMondayOfCurrentWeek(input5)
        assertEquals(Pair("08","28"),output1)
        assertEquals(Pair("06","26"),output2)
        assertEquals(Pair("12","28"),output3)
        assertEquals(Pair("12","04"),output4)
        assertEquals(Pair("01","30"),output5)
    }

    @Test
    fun getSundayOfCurrentWeek() {
        val input1 = LocalDate.of(2023, 9, 1) //Fri
        val input2 = LocalDate.of(2023, 7, 1) //Sat
        val input3 = LocalDate.of(2021, 1, 1) //Fri
        val input4 = LocalDate.of(2023, 12, 5) //Tue
        val input5 = LocalDate.of(2023, 2, 6) //Mon
        val output1 = getSundayOfCurrentWeek(input1)
        val output2 = getSundayOfCurrentWeek(input2)
        val output3 = getSundayOfCurrentWeek(input3)
        val output4 = getSundayOfCurrentWeek(input4)
        val output5 = getSundayOfCurrentWeek(input5)
        assertEquals(Pair("09","03"),output1)
        assertEquals(Pair("07","02"),output2)
        assertEquals(Pair("01","03"),output3)
        assertEquals(Pair("12","10"),output4)
        assertEquals(Pair("02","12"),output5)
    }
}