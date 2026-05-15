package com.itec.donelio.presentation.ui.screen.cosecha

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itec.donelio.presentation.ui.components.CampanaSeleccionadaCard
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CosechasScreen(campaniaId: Int = -1, onBack: () -> Unit, onGoToCampaniaDetalle: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Gestión de Cosechas", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo))
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                CampanaSeleccionadaCard(onClick = onGoToCampaniaDetalle)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item { Text("Cosechas Almacenadas", fontWeight = FontWeight.Bold, color = TextoPrincipal) }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, AgriVerde), modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text("Lote Norte - Soja", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("Silobolsa 1 | 05/05/2026") },
                        trailingContent = { Text("200 Tn", fontWeight = FontWeight.Bold, color = AgriVerde) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { Text("Cosechas No Almacenadas (Venta/Reserva)", fontWeight = FontWeight.Bold, color = TextoPrincipal) }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFD97706)), modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text("Lote Sur - Venta Directa", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("Precio: $45,000 | 10/05/2026") },
                        trailingContent = { Text("150 Tn", fontWeight = FontWeight.Bold, color = Color(0xFFB45309)) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }
}
