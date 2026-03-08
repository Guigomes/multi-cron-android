package com.guigomes.multicron.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.guigomes.multicron.data.model.CronTimer
import kotlinx.coroutines.flow.Flow

@Dao
interface CronTimerDao {

    @Query("SELECT * FROM cron_timers ORDER BY id ASC")
    fun getAllTimers(): Flow<List<CronTimer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timer: CronTimer): Long

    @Update
    suspend fun update(timer: CronTimer)

    @Delete
    suspend fun delete(timer: CronTimer)

    @Query("UPDATE cron_timers SET accumulatedSeconds = 0, startedAt = NULL")
    suspend fun resetAllTimers()

    @Query("SELECT * FROM cron_timers")
    suspend fun getAllTimersSnapshot(): List<CronTimer>
}
