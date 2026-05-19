package com.itec.donelio.presentation.ui.screen.insumo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.itec.donelio.domain.model.Insumo
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal
import com.itec.donelio.presentation.ui.theme.TextoSecundario
import com.itec.donelio.presentation.viewmodel.insumo.InsumoCatalogoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoInsumosScreen(
    viewModel: InsumoCatalogoViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onGoToFormulario: () -> Unit
) {
    val catalogo by viewModel.catalogo.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Catálogo de Insumos", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
        )
        if (catalogo.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(64.dp), tint = TextoSecundario)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No hay insumos en el catálogo", color = TextoSecundario, fontSize = 16.sp)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(catalogo) { insumo ->
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth()) {
                        ListItem(
                            headlineContent = { Text(insumo.nombre, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text("${insumo.categoria} | ${insumo.unidad}") },
                            leadingContent = { Icon(Icons.Default.Inventory, contentDescription = null, tint = AgriVerde) },
                            trailingContent = {
                                Row {
                                    IconButton(onClick = { }) { Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TextoPrincipal) }
                                    IconButton(onClick = { }) { Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFDC2626)) }
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
                        colors = ButtonDefaults.buttonColors(containerColor = AgriVerde),
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
}
