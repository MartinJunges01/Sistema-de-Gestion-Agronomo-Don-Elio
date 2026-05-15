package com.itec.donelio.presentation.ui.screen.campania

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itec.donelio.presentation.ui.theme.AgriAzul
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal
import com.itec.donelio.presentation.ui.theme.TextoSecundario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionParcelasScreen(onGoToDetail: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Gestión de Parcelas", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Supervisión de lotes activos", color = TextoSecundario)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(3) { index ->
                val progresos = listOf(0.8f, 0.4f, 0.95f)
                val cultivos = listOf("Soja (Lote Norte)", "Maíz (Lote Sur)", "Trigo (Lote Este)")
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onGoToDetail() },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(cultivos[index], fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextoPrincipal)
                            Icon(Icons.Default.WaterDrop, contentDescription = "Riego", tint = AgriAzul)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Madurez del cultivo", fontSize = 14.sp, color = TextoSecundario)
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progresos[index] },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = AgriVerde,
                            trackColor = AgriFondo,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Predicción de cosecha: ${(progresos[index] * 100).toInt()}% listo", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AgriVerde)
                    }
                }
            }
        }
    }
}
