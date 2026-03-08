package com.guigomes.multicron.ui.viewmodel

import android.app.Application
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.guigomes.multicron.data.db.AppDatabase
import com.guigomes.multicron.data.model.CronTimer
import com.guigomes.multicron.data.repository.TimerRepository
import com.guigomes.multicron.receiver.MidnightReceiver
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = TimerRepository(db.cronTimerDao(), db.dailyRecordDao())

    /** Live list of all timers from the database. */
    val timers: StateFlow<List<CronTimer>> = repository.allTimers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        scheduleMidnightAlarm()
    }

    // -------------------------------------------------------------------------
    // Timer operations
    // -------------------------------------------------------------------------

    fun addTimer(name: String) {
        viewModelScope.launch {
            repository.addTimer(name.trim())
        }
    }

    fun deleteTimer(timer: CronTimer) {
        viewModelScope.launch {
            repository.deleteTimer(timer)
        }
    }

    /**
     * Toggle play/pause for [timer].
     * - If [timer] is currently running → stop it.
     * - If [timer] is stopped → stop the currently running timer (if any) and start [timer].
     */
    fun toggleTimer(timer: CronTimer) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val currentTimers = timers.value

            if (timer.startedAt != null) {
                // Timer is running → stop it
                val elapsed = (now - timer.startedAt) / 1000
                repository.updateTimer(
                    timer.copy(
                        accumulatedSeconds = timer.accumulatedSeconds + elapsed,
                        startedAt = null
                    )
                )
            } else {
                // Stop whichever timer is currently running
                val runningTimer = currentTimers.firstOrNull { it.startedAt != null }
                if (runningTimer != null) {
                    val elapsed = (now - runningTimer.startedAt!!) / 1000
                    repository.updateTimer(
                        runningTimer.copy(
                            accumulatedSeconds = runningTimer.accumulatedSeconds + elapsed,
                            startedAt = null
                        )
                    )
                }
                // Start the requested timer
                repository.updateTimer(timer.copy(startedAt = now))
            }
        }
    }

    // -------------------------------------------------------------------------
    // Midnight handling
    // -------------------------------------------------------------------------

    private fun scheduleMidnightAlarm() {
        val context = getApplication<Application>()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, MidnightReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            MidnightReceiver.REQUEST_CODE,
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // Fallback: inexact alarm (may fire a few minutes late at midnight)
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                midnight.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                midnight.timeInMillis,
                pendingIntent
            )
        }
    }
}
