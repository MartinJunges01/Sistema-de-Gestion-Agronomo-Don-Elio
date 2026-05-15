package com.itec.donelio.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itec.donelio.presentation.ui.theme.TextoPrincipal
import com.itec.donelio.presentation.ui.theme.TextoSecundario

@Composable
fun TarjetaTarea(titulo: String, descripcion: String, icono: ImageVector, colorIcono: Color) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(colorIcono.copy(alpha = 0.1f)).padding(12.dp)) { Icon(icono, contentDescription = null, tint = colorIcono) }
            Spacer(modifier = Modifier.width(16.dp))
            Column { Text(titulo, fontWeight = FontWeight.Bold, color = TextoPrincipal); Text(descripcion, fontSize = 14.sp, color = TextoSecundario) }
        }
    }
}
