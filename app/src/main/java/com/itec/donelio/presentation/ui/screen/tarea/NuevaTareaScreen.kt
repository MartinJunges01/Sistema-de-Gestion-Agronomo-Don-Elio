package com.itec.donelio.presentation.ui.screen.tarea

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.itec.donelio.presentation.ui.components.SelectorCampania
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal
import com.itec.donelio.presentation.viewmodel.tarea.NuevaTareaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaTareaScreen(
    campaniaId: Int = -1,
    viewModel: NuevaTareaViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val campanias by viewModel.campanias.collectAsState()

    LaunchedEffect(state.guardadoExitoso) {
        if (state.guardadoExitoso) onBack()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Nueva Tarea", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text("Campaña vinculada", fontWeight = FontWeight.Bold, color = TextoPrincipal, modifier = Modifier.padding(bottom = 8.dp))
                SelectorCampania(
                    campanias = campanias,
                    selectedCampaniaId = state.campaniaId,
                    onCampaniaSelected = viewModel::onCampaniaChange,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 0.dp, vertical = 0.dp)
                )
                if (state.errorCampania != null) {
                    Text(
                        text = state.errorCampania!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            OutlinedTextField(
                value = state.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre de la Tarea") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.errorNombre != null,
                supportingText = state.errorNombre?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                var showDatePicker by remember { mutableStateOf(false) }
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = state.fecha
                )

                OutlinedTextField(
                    value = formatFecha(state.fecha),
                    onValueChange = {},
                    label = { Text("Fecha") },
                    modifier = Modifier.weight(1f),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                        }
                    }
                )

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { viewModel.onFechaChange(it) }
                                showDatePicker = false
                            }) { Text("Aceptar") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                OutlinedTextField(
                    value = state.hora,
                    onValueChange = viewModel::onHoraChange,
                    label = { Text("Hora") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text("HH:mm") },
                    trailingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = state.notificar,
                    onCheckedChange = viewModel::onNotificarChange,
                    colors = CheckboxDefaults.colors(checkedColor = AgriVerde)
                )
                Text("Activar Notificación de Recordatorio", color = TextoPrincipal)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = viewModel::guardar,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !state.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = AgriVerde),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Guardar Tarea", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun formatFecha(timestamp: Long): String {
    if (timestamp <= 0) return ""
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
