package com.itec.donelio.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorRangoFechas(
    mostrarDialogo: Boolean,
    onDismiss: () -> Unit,
    onRangoSeleccionado: (Pair<Long, Long>?) -> Unit
) {
    if (mostrarDialogo) {
        val state = rememberDateRangePickerState()

        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        val start = state.selectedStartDateMillis
                        val end = state.selectedEndDateMillis
                        if (start != null && end != null) {
                            onRangoSeleccionado(Pair(start, end))
                        } else {
                            onRangoSeleccionado(null)
                        }
                        onDismiss()
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            DateRangePicker(
                state = state,
                title = {
                    Text(
                        text = "Seleccionar Rango de Fechas",
                        modifier = Modifier.padding(16.dp)
                    )
                },
                headline = {
                    val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                    val startStr = state.selectedStartDateMillis?.let { dateFormat.format(Date(it)) } ?: "Inicio"
                    val endStr = state.selectedEndDateMillis?.let { dateFormat.format(Date(it)) } ?: "Fin"
                    Text(
                        text = "$startStr - $endStr",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                },
                showModeToggle = false,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
