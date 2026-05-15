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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itec.donelio.presentation.ui.theme.TextoPrincipal
import com.itec.donelio.presentation.ui.theme.TextoSecundario

@Composable
fun CardMetricaComparativa(
    titulo: String,
    valor1: String,
    valor2: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.Start) {
            Text(titulo, fontSize = 12.sp, color = TextoSecundario, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(color))
                Spacer(modifier = Modifier.width(6.dp))
                Text(valor1, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFD1D5DB)))
                Spacer(modifier = Modifier.width(6.dp))
                Text(valor2, fontSize = 14.sp, color = TextoSecundario)
            }
        }
    }
}
