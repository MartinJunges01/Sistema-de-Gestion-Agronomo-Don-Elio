package com.itec.donelio.presentation.ui.screen.insumo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.presentation.ui.theme.Emerald600
import com.itec.donelio.presentation.ui.theme.Stone50
import com.itec.donelio.presentation.ui.theme.Stone600
import com.itec.donelio.presentation.ui.theme.Stone900
import com.itec.donelio.presentation.viewmodel.insumo.InsumoCatalogoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoInsumosScreen(
    viewModel: InsumoCatalogoViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onGoToFormulario: () -> Unit
) {
    val catalogo by viewModel.catalogo.collectAsState()
    var insumoAEditar by remember { mutableStateOf<Insumo?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Catálogo de Insumos", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50)
        )
        if (catalogo.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(64.dp), tint = Stone600)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No hay insumos en el catálogo", color = Stone600, fontSize = 16.sp)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(catalogo) { insumo ->
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth()) {
                        ListItem(
                            headlineContent = { Text(insumo.nombre, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text("${insumo.categoria} | ${insumo.unidad}") },
                            leadingContent = { 
                                if (insumo.icono != null) {
                                    Text(insumo.icono, fontSize = 24.sp)
                                } else {
                                    Icon(Icons.Default.Inventory, contentDescription = null, tint = Emerald600)
                                }
                            },
                            trailingContent = {
                                Row {
                                    IconButton(onClick = { insumoAEditar = insumo }) { Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Stone900) }
                                    IconButton(onClick = { viewModel.eliminarInsumo(insumo) }) { Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFDC2626)) }
                                }
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
                item {
                    Button(
                        onClick = onGoToFormulario,
                        modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agregar Nuevo Insumo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (insumoAEditar != null) {
        DialogEditarInsumo(
            insumo = insumoAEditar!!,
            onDismiss = { insumoAEditar = null },
            onGuardar = { insumoEditado ->
                viewModel.editarInsumo(insumoEditado)
                insumoAEditar = null
            }
        )
    }
}

@Composable
private fun DialogEditarInsumo(
    insumo: Insumo,
    onDismiss: () -> Unit,
    onGuardar: (Insumo) -> Unit
) {
    var nombre by remember { mutableStateOf(insumo.nombre) }
    var categoria by remember { mutableStateOf(insumo.categoria) }
    var unidad by remember { mutableStateOf(insumo.unidad) }
    var icono by remember { mutableStateOf(insumo.icono) }
    val iconosDisponibles = listOf("🌱", "💧", "💊", "⛽", "⚙️", "🚜", "📦", "🧪", "🌾", "✂️")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Insumo", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoría") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = unidad,
                    onValueChange = { unidad = it },
                    label = { Text("Unidad") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Icono", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth().height(100.dp)
                ) {
                    gridItems(iconosDisponibles) { i ->
                        val isSelected = icono == i
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Emerald600.copy(alpha = 0.2f) else Color.Transparent)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Emerald600 else Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { icono = i },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = i, fontSize = 20.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onGuardar(insumo.copy(nombre = nombre.trim(), categoria = categoria.trim(), unidad = unidad.trim(), icono = icono))
                },
                enabled = nombre.isNotBlank()
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
