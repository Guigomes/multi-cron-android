package com.guigomes.multicron.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cron_timers")
data class CronTimer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    /** Accumulated seconds for the current day (not yet saved to DailyRecord). */
    val accumulatedSeconds: Long = 0,
    /** Epoch millis when this timer was last started; null if not running. */
    val startedAt: Long? = null
)
