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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioCosechaScreen(onBack: () -> Unit) {
    var almacenado by remember { mutableStateOf(true) }
    var cultivo by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var almacen by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Registrar Cosecha", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = cultivo, onValueChange = { cultivo = it }, label = { Text("Cultivo") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = cantidad, onValueChange = { cantidad = it }, label = { Text("Cantidad") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = unidad, onValueChange = { unidad = it }, label = { Text("Unidad (Ej. Tn)") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Fecha") }, modifier = Modifier.fillMaxWidth(), trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) })

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Checkbox(checked = almacenado, onCheckedChange = { almacenado = it }, colors = CheckboxDefaults.colors(checkedColor = AgriVerde))
                Text("Almacenar en el establecimiento", fontWeight = FontWeight.Medium)
            }

            if (almacenado) {
                OutlinedTextField(value = almacen, onValueChange = { almacen = it }, label = { Text("Almacén (Silo, Silobolsa)") }, modifier = Modifier.fillMaxWidth())
            } else {
                OutlinedTextField(value = tipo, onValueChange = { tipo = it }, label = { Text("Tipo (Venta, Alimento Vacuno, Reserva)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio (Opcional)") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) })
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = AgriVerde), shape = RoundedCornerShape(12.dp)) { Text("Guardar Registro", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}
