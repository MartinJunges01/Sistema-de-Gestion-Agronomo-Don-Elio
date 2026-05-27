package com.itec.donelio.core.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.itec.donelio.presentation.MainActivity
import com.itec.donelio.R

class TareaReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val tareaId = inputData.getInt("tareaId", -1)
        val tareaNombre = inputData.getString("tareaNombre") ?: "Tarea pendiente"
        val mensaje = inputData.getString("mensaje") ?: "Tienes una tarea programada"

        if (tareaId == -1) return Result.failure()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            tareaId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, "tareas_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info) 
            .setContentTitle("Don Elio - $tareaNombre")
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(tareaId, builder.build())
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

        return Result.success()
    }
}
