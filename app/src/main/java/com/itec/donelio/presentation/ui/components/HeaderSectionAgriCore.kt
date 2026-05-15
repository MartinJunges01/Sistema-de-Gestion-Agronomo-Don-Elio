package com.itec.donelio.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itec.donelio.presentation.ui.theme.AgriVerde

@Composable
fun HeaderSectionAgriCore(onGoToConfig: () -> Unit) {
    Surface(color = AgriVerde, shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp), shadowElevation = 4.dp) {
        Row(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column {
                Text(text = "Martes, 28 de Abril", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(text = "Hola, Don Elio", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            Surface(color = Color.Black.copy(alpha = 0.2f), shape = RoundedCornerShape(50), modifier = Modifier.clickable { onGoToConfig() }) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Offline", tint = Color.White, modifier = Modifier.size(14.dp))
                    Text("Local / DB", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
