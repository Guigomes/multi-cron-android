package com.guigomes.multicron.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.guigomes.multicron.data.db.AppDatabase
import com.guigomes.multicron.data.repository.TimerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Receives a broadcast at midnight, saves daily records for all timers, resets them,
 * and re-schedules the alarm for the next midnight.
 */
class MidnightReceiver : BroadcastReceiver() {

    companion object {
        const val REQUEST_CODE = 1001
    }

    override fun onReceive(context: Context, intent: Intent) {
        val db = AppDatabase.getInstance(context)
        val repository = TimerRepository(db.cronTimerDao(), db.dailyRecordDao())

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                repository.saveDailyRecordsAndReset()
            } finally {
                pendingResult.finish()
            }
        }

        // Re-schedule alarm for the next midnight
        scheduleNextMidnight(context)
    }

    private fun scheduleNextMidnight(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, MidnightReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val midnight = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            midnight.timeInMillis,
            pendingIntent
        )
    }
}
