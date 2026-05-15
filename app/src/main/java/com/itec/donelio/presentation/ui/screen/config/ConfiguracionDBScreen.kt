package com.itec.donelio.presentation.ui.screen.config

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionDBScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Base de Datos (CU12/CU13)", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = TextoPrincipal)) { Icon(Icons.Default.Upload, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Exportar Base de Datos (Respaldo)") }
            Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = AgriVerde)) { Icon(Icons.Default.Download, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Importar Base de Datos") }
        }
    }
}
