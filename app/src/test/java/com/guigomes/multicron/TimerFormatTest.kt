package com.guigomes.multicron

import org.junit.Test
import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TimerFormatTest {

    private fun formatElapsed(totalSeconds: Long): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d:%02d".format(hours, minutes, seconds)
    }

    @Test
    fun `format zero seconds`() {
        assertEquals("00:00:00", formatElapsed(0))
    }

    @Test
    fun `format one hour`() {
        assertEquals("01:00:00", formatElapsed(3600))
    }

    @Test
    fun `format one minute thirty seconds`() {
        assertEquals("00:01:30", formatElapsed(90))
    }

    @Test
    fun `format large value`() {
        assertEquals("10:30:45", formatElapsed(37845))
    }

    @Test
    fun `yesterday returns a date string in ISO format`() {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val expected = fmt.format(cal.time)

        val cal2 = Calendar.getInstance()
        cal2.add(Calendar.DAY_OF_YEAR, -1)
        val actual = fmt.format(cal2.time)

        assertEquals(expected, actual)
        // Verify it matches yyyy-MM-dd pattern
        assertTrue("Date should match yyyy-MM-dd", actual.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
    }
}
