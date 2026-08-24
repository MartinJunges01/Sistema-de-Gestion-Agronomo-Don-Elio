package com.itec.donelio.presentation.ui.screen.campania

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal
import com.itec.donelio.presentation.ui.theme.TextoSecundario
import com.itec.donelio.presentation.viewmodel.campania.GestionCampaniasViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.foundation.ExperimentalFoundationApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GestionCampaniasScreen(
    viewModel: GestionCampaniasViewModel = hiltViewModel(),
    onGoToDetail: (campaniaId: Int) -> Unit,
    onBack: () -> Unit
) {
    val campaniasActivas by viewModel.campaniasActivas.collectAsState()
    val campaniasInactivas by viewModel.campaniasInactivas.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var campaniaAEliminar by remember { mutableStateOf<Campania?>(null) }
    var showHistorial by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.errorMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    if (campaniaAEliminar != null) {
        AlertDialog(
            onDismissRequest = { campaniaAEliminar = null },
            title = { Text("¿Eliminar permanentemente la campaña?", fontWeight = FontWeight.Bold) },
            text = { Text("Esta acción eliminará todos los datos asociados (cosechas, insumos, observaciones, tareas) y no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarCampaniaPermanente(campaniaAEliminar!!)
                        campaniaAEliminar = null
                    }
                ) { Text("Eliminar", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { campaniaAEliminar = null }) { Text("Cancelar", color = TextoSecundario) }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            TopAppBar(
                title = { Text("Gestión de Campañas", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (campaniasActivas.isEmpty() && campaniasInactivas.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Agriculture, contentDescription = null, modifier = Modifier.size(64.dp), tint = TextoSecundario)
                            Text("No hay campañas", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextoPrincipal)
                            Text("Presiona + para crear una", fontSize = 14.sp, color = TextoSecundario)
                        }
                    }
                } else {
                    if (campaniasActivas.isNotEmpty()) {
                        item {
                            Text("Activas", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextoPrincipal, modifier = Modifier.padding(bottom = 4.dp))
                        }
                        items(campaniasActivas, key = { it.id }) { campania ->
                            CampaniaCard(
                                campania = campania,
                                onClick = { onGoToDetail(campania.id) },
                                onDelete = null
                            )
                        }
                    }

                    if (campaniasInactivas.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showHistorial = !showHistorial }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Historial (${campaniasInactivas.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextoSecundario)
                                Icon(if (showHistorial) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null, tint = TextoSecundario)
                            }
                        }
                        if (showHistorial) {
                            items(campaniasInactivas, key = { it.id }) { campania ->
                                CampaniaCard(
                                    campania = campania,
                                    onClick = { onGoToDetail(campania.id) },
                                    onDelete = { campaniaAEliminar = campania }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CampaniaCard(campania: Campania, onClick: () -> Unit, onDelete: (() -> Unit)?) {
    val backgroundColor = if (campania.estaActiva) Color.White else Color.Gray.copy(alpha = 0.1f)
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = if (campania.estaActiva) AgriVerde else TextoSecundario,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(campania.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextoPrincipal)
                Text(campania.cultivo.ifBlank { "—" }, fontSize = 14.sp, color = TextoSecundario)
                Text(formatFecha(campania.fechaInicio), fontSize = 12.sp, color = TextoSecundario)
            }
            if (campania.estaActiva) {
                Surface(shape = RoundedCornerShape(8.dp), color = AgriVerde.copy(alpha = 0.1f)) {
                    Text("Activa", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 11.sp, color = AgriVerde, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ChevronRight, contentDescription = "Ver detalle", tint = TextoSecundario)
            } else if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(androidx.compose.material.icons.filled.Delete, contentDescription = "Eliminar", tint = Color.Red.copy(alpha = 0.7f))
                }
            }
        }
    }
}

private fun formatFecha(timestamp: Long): String {
    if (timestamp <= 0) return "—"
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
