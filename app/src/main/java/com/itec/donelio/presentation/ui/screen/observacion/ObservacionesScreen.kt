package com.itec.donelio.presentation.ui.screen.observacion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itec.donelio.presentation.ui.components.CampanaSeleccionadaCard
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.TextoPrincipal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObservacionesScreen(campaniaId: Int = -1, onBack: () -> Unit, onGoToCampaniaDetalle: () -> Unit) {
    var nota by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Observaciones (CU8)", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CampanaSeleccionadaCard(onClick = onGoToCampaniaDetalle)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = nota, onValueChange = { nota = it }, label = { Text("Escribe una nota...") }, modifier = Modifier.fillMaxWidth().height(150.dp), maxLines = 5)
            Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = TextoPrincipal)) { Icon(Icons.Default.CameraAlt, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Adjuntar Fotografía") }
        }
    }
}
