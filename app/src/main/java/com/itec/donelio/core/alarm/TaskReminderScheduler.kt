package com.itec.donelio.core.alarm

import com.itec.donelio.domain.model.Tarea

interface TaskReminderScheduler {
    fun schedule(tarea: Tarea)
    fun cancel(tareaId: Int)
}
