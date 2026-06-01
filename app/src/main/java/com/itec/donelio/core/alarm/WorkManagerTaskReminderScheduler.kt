package com.itec.donelio.core.alarm

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.itec.donelio.core.worker.TareaReminderWorker
import com.itec.donelio.domain.model.Tarea
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkManagerTaskReminderScheduler @Inject constructor(
    private val context: Context
) : TaskReminderScheduler {

    private val workManager = WorkManager.getInstance(context)

    override fun schedule(tarea: Tarea) {
        cancel(tarea.id)

        if (!tarea.notificar || tarea.confirmar) return

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val parsedTime = try { sdf.parse(tarea.hora) } catch (e: Exception) { null }
        
        val calendar = Calendar.getInstance().apply {
            timeInMillis = tarea.fecha
            if (parsedTime != null) {
                val timeCalendar = Calendar.getInstance().apply { time = parsedTime }
                set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }

        val targetTimeMillis = calendar.timeInMillis
        val currentTimeMillis = System.currentTimeMillis()

        // 1. Recordatorio del mismo día
        val delaySameDay = targetTimeMillis - currentTimeMillis
        if (delaySameDay > 0) {
            val data = Data.Builder()
                .putInt("tareaId", tarea.id)
                .putString("tareaNombre", tarea.nombre)
                .putString("mensaje", "Hoy tienes programada esta tarea.")
                .build()

            val request = OneTimeWorkRequestBuilder<TareaReminderWorker>()
                .setInitialDelay(delaySameDay, TimeUnit.MILLISECONDS)
                .addTag(tarea.id.toString())
                .setInputData(data)
                .build()

            workManager.enqueue(request)
        }

        // 2. Recordatorio 2 días antes
        val delayTwoDaysBefore = targetTimeMillis - TimeUnit.DAYS.toMillis(2) - currentTimeMillis
        if (delayTwoDaysBefore > 0) {
            val data = Data.Builder()
                .putInt("tareaId", tarea.id)
                .putString("tareaNombre", tarea.nombre)
                .putString("mensaje", "En 2 días tienes programada esta tarea.")
                .build()

            val request = OneTimeWorkRequestBuilder<TareaReminderWorker>()
                .setInitialDelay(delayTwoDaysBefore, TimeUnit.MILLISECONDS)
                .addTag(tarea.id.toString())
                .setInputData(data)
                .build()

            workManager.enqueue(request)
        }
    }

    override fun cancel(tareaId: Int) {
        workManager.cancelAllWorkByTag(tareaId.toString())
    }
}
