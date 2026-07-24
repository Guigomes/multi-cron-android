package com.guigomes.multicron.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.guigomes.multicron.data.model.CronTimer
import com.guigomes.multicron.data.model.DailyRecord

@Database(
    entities = [CronTimer::class, DailyRecord::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cronTimerDao(): CronTimerDao
    abstract fun dailyRecordDao(): DailyRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "multicron_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
