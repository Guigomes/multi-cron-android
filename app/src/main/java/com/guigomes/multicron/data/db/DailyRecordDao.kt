package com.guigomes.multicron.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.guigomes.multicron.data.model.DailyRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: DailyRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<DailyRecord>)

    @Query("SELECT * FROM daily_records WHERE timerId = :timerId ORDER BY date DESC")
    fun getRecordsForTimer(timerId: Long): Flow<List<DailyRecord>>

    @Query("SELECT * FROM daily_records ORDER BY date DESC, timerId ASC")
    fun getAllRecords(): Flow<List<DailyRecord>>
}
