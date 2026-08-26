package com.itec.donelio.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal
import com.itec.donelio.presentation.ui.theme.TextoSecundario

import com.itec.donelio.domain.model.Campania
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CampaniaSeleccionadaCard(campania: Campania?, onClick: () -> Unit) {
    if (campania == null) return

    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fechaInicioStr = sdf.format(Date(campania.fechaInicio))

    Card(
        modifier = Modifier.fillMaxWidth().height(110.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE7E5E4)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(AgriVerde.copy(alpha = 0.1f)).padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Eco, contentDescription = null, tint = AgriVerde, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(campania.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextoPrincipal)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Cultivo: ${campania.cultivoNombre} · Inicio: $fechaInicioStr", fontSize = 14.sp, color = TextoSecundario)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = "Ver Detalles", tint = TextoSecundario)
        }
    }
}
