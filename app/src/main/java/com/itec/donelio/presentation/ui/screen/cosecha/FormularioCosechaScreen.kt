package com.itec.donelio.presentation.ui.screen.cosecha

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal
import com.itec.donelio.presentation.viewmodel.cosecha.FormularioCosechaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioCosechaScreen(
    campaniaId: Int = -1,
    viewModel: FormularioCosechaViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.guardadoExitoso) {
        if (state.guardadoExitoso) onBack()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Registrar Cosecha", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                OutlinedTextField(
                    value = state.cantidad,
                    onValueChange = viewModel::onCantidadChange,
                    label = { Text("Cantidad (Tn)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.errorCantidad != null,
                    supportingText = state.errorCantidad?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    singleLine = true
                )

            var showDatePicker by remember { mutableStateOf(false) }
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = state.fecha
            )

            OutlinedTextField(
                value = formatFecha(state.fecha),
                onValueChange = {},
                label = { Text("Fecha") },
                modifier = Modifier.fillMaxWidth(),
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

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Checkbox(
                    checked = state.almacenado,
                    onCheckedChange = viewModel::onAlmacenadoChange,
                    colors = CheckboxDefaults.colors(checkedColor = AgriVerde)
                )
                Text("Almacenar en el establecimiento", fontWeight = FontWeight.Medium, color = TextoPrincipal)
            }

            if (state.almacenado) {
                OutlinedTextField(
                    value = state.almacen,
                    onValueChange = viewModel::onAlmacenChange,
                    label = { Text("Almacén (Silo, Silobolsa)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            } else {
                OutlinedTextField(
                    value = state.tipo,
                    onValueChange = viewModel::onTipoChange,
                    label = { Text("Tipo (Venta, Alimento Vacuno, Reserva)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = state.precio,
                    onValueChange = viewModel::onPrecioChange,
                    label = { Text("Precio (Opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                   //error visual
                    isError = state.errorPrecio != null,
                    supportingText = state.errorPrecio?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = viewModel::guardar,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !state.isLoading && state.errorPrecio == null && state.errorCantidad == null,
                colors = ButtonDefaults.buttonColors(containerColor = AgriVerde),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Guardar Registro", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
