package com.guigomes.multicron

import org.junit.Test
import org.junit.Assert.*

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
}
