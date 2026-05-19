package com.itec.donelio.presentation.ui.screen.campania

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal
import com.itec.donelio.presentation.ui.theme.TextoSecundario
import com.itec.donelio.presentation.viewmodel.campania.CampaniaDetailViewModel
import com.itec.donelio.presentation.viewmodel.tarea.TareaViewModel
import com.itec.donelio.presentation.viewmodel.insumo.InsumoVinculacionViewModel
import com.itec.donelio.presentation.viewmodel.cosecha.CosechaViewModel
import com.itec.donelio.presentation.viewmodel.observacion.ObservacionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCampaniaScreen(
    viewModel: CampaniaDetailViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onGoToEditar: (Int) -> Unit,
    onGoToTareas: (Int) -> Unit,
    onGoToInsumos: (Int) -> Unit,
    onGoToCosechas: (Int) -> Unit,
    onGoToObservaciones: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val tabTitles = listOf("Info", "Tareas", "Insumos", "Cosechas", "Observaciones")
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(state.deleteSuccess) {
        if (state.deleteSuccess) onBack()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(state.campaign?.nombre ?: "Detalle", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriVerde, titleContentColor = Color.White, navigationIconContentColor = Color.White),
            actions = {
                IconButton(onClick = { viewModel.eliminarCampania() }) { Icon(Icons.Default.Delete, contentDescription = "Eliminar Campaña", tint = Color.White) }
            }
        )

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AgriVerde)
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(state.error ?: "", color = TextoSecundario)
                }
            }
        } else {
            state.campaign?.let { campaign ->
                HeaderCampania(campaign = campaign)

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = AgriVerde
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                when (selectedTab) {
                    0 -> TabInfo(campaign = campaign, onEditar = { onGoToEditar(campaign.id) })
                    1 -> TabTareas(campaniaId = campaign.id, onGoToTareas = { onGoToTareas(campaign.id) })
                    2 -> TabInsumos(campaniaId = campaign.id, onGoToInsumos = { onGoToInsumos(campaign.id) })
                    3 -> TabCosechas(campaniaId = campaign.id, onGoToCosechas = { onGoToCosechas(campaign.id) })
                    4 -> TabObservaciones(
                        campaniaId = campaign.id,
                        onGoToObservaciones = { onGoToObservaciones(campaign.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderCampania(campaign: com.itec.donelio.domain.model.Campania) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Eco, contentDescription = null, tint = AgriVerde, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(campaign.nombre, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextoPrincipal)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            InfoChip(label = "Cultivo", value = campaign.cultivo.ifBlank { "—" })
            InfoChip(label = "Inicio", value = formatFecha(campaign.fechaInicio))
            InfoChip(label = "Estado", value = if (campaign.estaActiva) "Activa" else "Inactiva")
        }
    }
}

@Composable
private fun InfoChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = TextoSecundario)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextoPrincipal)
    }
}

@Composable
private fun TabInfo(campaign: com.itec.donelio.domain.model.Campania, onEditar: () -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFE7E5E4))) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetalleFila(label = "Nombre", value = campaign.nombre)
                    DetalleFila(label = "Cultivo", value = campaign.cultivo.ifBlank { "Sin especificar" })
                    DetalleFila(label = "Fecha de inicio", value = formatFecha(campaign.fechaInicio))
                    DetalleFila(label = "Estado", value = if (campaign.estaActiva) "Activa" else "Inactiva")
                }
            }
        }
        item {
            Button(
                onClick = onEditar,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriVerde),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editar Datos de Campaña", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DetalleFila(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextoSecundario, fontSize = 14.sp)
        Text(value, color = TextoPrincipal, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

@Composable
private fun TabTareas(campaniaId: Int, onGoToTareas: () -> Unit) {
    val tareaViewModel: TareaViewModel = hiltViewModel(key = "tab_tareas")
    val tareas by tareaViewModel.tareas.collectAsState()
    val pendientes = tareas.filter { !it.confirmar }
    val completadas = tareas.filter { it.confirmar }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Resumen de Tareas", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextoPrincipal)
                    Text("$pendientes tareas pendientes${if (completadas.isNotEmpty()) ", ${completadas.size} completadas" else ""}", color = TextoSecundario, fontSize = 14.sp)
                }
            }
        }
        pendientes.take(3).forEach { tarea ->
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = false,
                            onCheckedChange = { tareaViewModel.toggleCompletada(tarea) },
                            colors = CheckboxDefaults.colors(checkedColor = AgriVerde)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(tarea.nombre, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextoPrincipal)
                            Text("${formatFecha(tarea.fecha)} ${tarea.hora}", fontSize = 12.sp, color = TextoSecundario)
                        }
                    }
                }
            }
        }
        item {
            Button(onClick = onGoToTareas, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = AgriVerde), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver todas las tareas")
            }
        }
    }
}

@Composable
private fun TabInsumos(campaniaId: Int, onGoToInsumos: () -> Unit) {
    val vm: InsumoVinculacionViewModel = hiltViewModel(key = "tab_insumos")
    val vinculados by vm.insumosVinculados.collectAsState()
    val total = vinculados.sumOf { it.cantidad * it.precio }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Resumen de Insumos", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextoPrincipal)
                    Text("${vinculados.size} insumos vinculados", color = TextoSecundario, fontSize = 14.sp)
                    Text("Total estimado: $ ${"%,.2f".format(total)}", color = AgriVerde, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
        item {
            Button(onClick = onGoToInsumos, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = AgriVerde), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gestionar insumos")
            }
        }
    }
}

@Composable
private fun TabCosechas(campaniaId: Int, onGoToCosechas: () -> Unit) {
    val vm: CosechaViewModel = hiltViewModel(key = "tab_cosechas_$campaniaId")
    val almacenadas by vm.almacenadas.collectAsState()
    val noAlmacenadasDetalle by vm.noAlmacenadasDetalle.collectAsState()
    val totalAlmacenado = almacenadas.sumOf { it.cantidad }
    val totalNoAlmacenado = noAlmacenadasDetalle.values.sumOf { it.precio }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Resumen de Cosechas", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextoPrincipal)
                    Text(
                        "${almacenadas.size} cosechas almacenadas · ${noAlmacenadasDetalle.size} vendidas/reservadas",
                        color = TextoSecundario, fontSize = 14.sp
                    )
                    if (almacenadas.isNotEmpty() || noAlmacenadasDetalle.isNotEmpty()) {
                        Text(
                            "${formatCantidad(totalAlmacenado)} almacenadas · $ ${"%,.2f".format(totalNoAlmacenado)} en ventas",
                            color = AgriVerde, fontWeight = FontWeight.Bold, fontSize = 14.sp
                        )
                    }
                }
            }
        }
        item {
            Button(onClick = onGoToCosechas, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = AgriVerde), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.Agriculture, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver cosechas")
            }
        }
    }
}

@Composable
private fun TabObservaciones(campaniaId: Int, onGoToObservaciones: () -> Unit) {
    val vm: ObservacionViewModel = hiltViewModel(key = "tab_observaciones_$campaniaId")
    val observaciones by vm.observaciones.collectAsState()
    val ultimas = observaciones.take(3)

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Resumen de Observaciones", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextoPrincipal)
                    if (observaciones.isEmpty()) {
                        Text("Sin observaciones registradas", color = TextoSecundario, fontSize = 14.sp)
                    } else {
                        Text("${observaciones.size} observaciones registradas", color = TextoSecundario, fontSize = 14.sp)
                    }
                }
            }
        }
        ultimas.forEach { obs ->
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(obs.texto, color = TextoPrincipal, fontSize = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
        item {
            Button(onClick = onGoToObservaciones, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = AgriVerde), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.NoteAlt, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ir a observaciones")
            }
        }
    }
}

private fun formatFecha(timestamp: Long): String {
    if (timestamp <= 0) return "—"
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatCantidad(cantidad: Double): String {
    return if (cantidad == cantidad.toLong().toDouble()) {
        "${cantidad.toLong()} Kg"
    } else {
        "%,.2f Kg".format(cantidad)
    }
}
