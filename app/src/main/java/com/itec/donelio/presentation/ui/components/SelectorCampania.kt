package com.itec.donelio.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.presentation.ui.theme.AgriVerde

@Composable
fun SelectorCampania(
    campanias: List<Campania>,
    selectedCampaniaId: Int?,
    onCampaniaSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedCampania = campanias.find { it.id == selectedCampaniaId }
    val displayText = selectedCampania?.nombre ?: if (campanias.isEmpty()) "Sin campañas" else "Seleccionar Campaña"

    Box(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        OutlinedCard(
            modifier = Modifier.fillMaxWidth().clickable { if (campanias.isNotEmpty()) expanded = true },
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = displayText,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedCampania != null) AgriVerde else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Seleccionar campaña")
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            campanias.forEach { campania ->
                DropdownMenuItem(
                    text = { Text(campania.nombre) },
                    onClick = {
                        onCampaniaSelected(campania.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
