package com.itec.donelio.presentation.ui.screen.campania

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
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
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.viewmodel.campania.CampaniaFormViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioCampaniaScreen(
    viewModel: CampaniaFormViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onGuardadoExitoso: () -> Unit,
    onGoToCatalogoCultivos: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.guardadoExitoso) {
        if (state.guardadoExitoso) {
            onGuardadoExitoso()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(if (state.isEditMode) "Editar Campaña" else "Crear Campaña", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(androidx.compose.foundation.rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre de la Campaña") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.errorNombre != null,
                supportingText = state.errorNombre?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true
            )

            // Selector de Cultivos desde el Catálogo
            val cultivos by viewModel.cultivos.collectAsState()
            var dropdownExpanded by remember { mutableStateOf(false) }
            var showNuevoCultivoDialog by remember { mutableStateOf(false) }
            val cultivoSeleccionado = cultivos.find { it.id == state.cultivoId }
            val textoCultivo = cultivoSeleccionado?.nombre ?: ""

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = textoCultivo,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Cultivo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        isError = state.errorCultivo != null,
                        supportingText = state.errorCultivo?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )

                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        cultivos.forEach { cultivo ->
                            DropdownMenuItem(
                                text = { Text(cultivo.nombre) },
                                onClick = {
                                    viewModel.onCultivoIdChange(cultivo.id)
                                    dropdownExpanded = false
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("+ Agregar nuevo cultivo...", color = AgriVerde, fontWeight = FontWeight.Bold) },
                            onClick = {
                                showNuevoCultivoDialog = true
                                dropdownExpanded = false
                            }
                        )
                    }
                }

                IconButton(
                    onClick = onGoToCatalogoCultivos,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configurar Catálogo",
                        tint = AgriVerde
                    )
                }
            }

            if (showNuevoCultivoDialog) {
                var nuevoCultivoNombre by remember { mutableStateOf("") }
                AlertDialog(
                    onDismissRequest = { showNuevoCultivoDialog = false },
                    title = { Text("Agregar Nuevo Cultivo", fontWeight = FontWeight.Bold) },
                    text = {
                        OutlinedTextField(
                            value = nuevoCultivoNombre,
                            onValueChange = { nuevoCultivoNombre = it },
                            label = { Text("Nombre del Cultivo") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (nuevoCultivoNombre.isNotBlank()) {
                                    viewModel.crearYSeleccionarNuevoCultivo(nuevoCultivoNombre.trim())
                                }
                                showNuevoCultivoDialog = false
                            },
                            enabled = nuevoCultivoNombre.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = AgriVerde)
                        ) { Text("Guardar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showNuevoCultivoDialog = false }) { Text("Cancelar") }
                    }
                )
            }

            OutlinedTextField(
                value = state.hectareas,
                onValueChange = viewModel::onHectareasChange,
                label = { Text("Hectáreas") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.errorHectareas != null,
                supportingText = state.errorHectareas?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )

            var showDatePicker by remember { mutableStateOf(false) }
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = state.fechaInicio,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val hoy = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.timeInMillis
                        return utcTimeMillis >= hoy
                    }
                }
            )

            OutlinedTextField(
                value = formatFecha(state.fechaInicio),
                onValueChange = {},
                label = { Text("Fecha de Inicio") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                isError = state.errorFecha != null,
                supportingText = state.errorFecha?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                    }
                }
            )

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let {
                                viewModel.onFechaChange(it)
                            }
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

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = viewModel::guardar,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !state.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = AgriVerde),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (state.isEditMode) "Actualizar Campaña" else "Guardar Campaña",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
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
