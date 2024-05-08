package calendar

import addHoursToTimeString
import formatDate
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class CalendarKtTest{
    @Test
    fun addHoursToTimeString() {
        assertEquals("00:30",addHoursToTimeString("00:30",24))
        assertEquals("12:30",addHoursToTimeString("00:30",12))
        assertEquals("00:00",addHoursToTimeString("00:00",24))
        assertEquals("15:01",addHoursToTimeString("00:01",15))
        assertEquals("10:01",addHoursToTimeString("08:01",2))
        assertEquals("00:00",addHoursToTimeString("00:00",48))
        assertEquals("00:00",addHoursToTimeString("12:00",12))
        assertEquals("00:00",addHoursToTimeString("12:00",36))
        assertEquals("12:00",addHoursToTimeString("00:00",36))
    }

    @Test
    fun formatDate() {
        assertEquals("2020-04-25",formatDate("20200425"))
        assertEquals("2024-05-22",formatDate("20240522"))
        assertEquals("2020-01-01",formatDate("20200101"))
        assertEquals("1999-09-09",formatDate("19990909"))
        assertEquals("1212-12-12",formatDate("12121212"))
        assertEquals("1414-12-13",formatDate("14141213"))
        assertEquals("1999-12-31",formatDate("19991231"))
        assertEquals("1980-08-25",formatDate("19800825"))
        assertEquals("1000-10-10",formatDate("10001010"))
    }
}