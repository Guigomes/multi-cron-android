package com.guigomes.multicron.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_records",
    foreignKeys = [
        ForeignKey(
            entity = CronTimer::class,
            parentColumns = ["id"],
            childColumns = ["timerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("timerId")]
)
data class DailyRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timerId: Long,
    /** ISO date string: yyyy-MM-dd */
    val date: String,
    val elapsedSeconds: Long
)
