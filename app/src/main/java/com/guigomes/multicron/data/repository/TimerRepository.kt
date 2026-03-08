package com.guigomes.multicron.data.repository

import com.guigomes.multicron.data.db.CronTimerDao
import com.guigomes.multicron.data.db.DailyRecordDao
import com.guigomes.multicron.data.model.CronTimer
import com.guigomes.multicron.data.model.DailyRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class TimerRepository(
    private val cronTimerDao: CronTimerDao,
    private val dailyRecordDao: DailyRecordDao
) {

    val allTimers: Flow<List<CronTimer>> = cronTimerDao.getAllTimers()

    val allDailyRecords: Flow<List<DailyRecord>> = dailyRecordDao.getAllRecords()

    suspend fun addTimer(name: String): Long = cronTimerDao.insert(CronTimer(name = name))

    suspend fun updateTimer(timer: CronTimer) = cronTimerDao.update(timer)

    suspend fun deleteTimer(timer: CronTimer) = cronTimerDao.delete(timer)

    /**
     * Saves elapsed seconds for all timers as DailyRecords for [date], then resets all timers.
     * Called at midnight.
     */
    suspend fun saveDailyRecordsAndReset(date: String = LocalDate.now().toString()) {
        val timers = cronTimerDao.getAllTimersSnapshot()
        val now = System.currentTimeMillis()

        val records = timers.mapNotNull { timer ->
            val elapsed = if (timer.startedAt != null) {
                timer.accumulatedSeconds + (now - timer.startedAt) / 1000
            } else {
                timer.accumulatedSeconds
            }
            if (elapsed > 0) {
                DailyRecord(timerId = timer.id, date = date, elapsedSeconds = elapsed)
            } else {
                null
            }
        }

        if (records.isNotEmpty()) {
            dailyRecordDao.insertAll(records)
        }
        cronTimerDao.resetAllTimers()
    }

    fun getDailyRecordsForTimer(timerId: Long): Flow<List<DailyRecord>> =
        dailyRecordDao.getRecordsForTimer(timerId)
}
