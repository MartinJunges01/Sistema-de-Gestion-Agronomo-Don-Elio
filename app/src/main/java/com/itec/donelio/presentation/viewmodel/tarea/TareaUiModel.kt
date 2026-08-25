package com.itec.donelio.presentation.viewmodel.tarea

import com.itec.donelio.domain.model.Tarea

data class TareaUiModel(
    val tarea: Tarea,
    val isVencida: Boolean,
    val campaniaNombre: String
)
