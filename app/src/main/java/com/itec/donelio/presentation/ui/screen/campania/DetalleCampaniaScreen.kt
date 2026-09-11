package com.itec.donelio.presentation.ui.screen.campania

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
    onGoToObservaciones: (Int) -> Unit,
    onGoToNuevaTarea: (Int) -> Unit,
    onGoToNuevoInsumo: (Int) -> Unit,
    onGoToNuevaCosecha: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.finishSuccess) {
        if (state.finishSuccess) onBack()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { 
                Text(
                    text = state.campania?.nombre ?: "Detalle", 
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) 
            },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriVerde, titleContentColor = Color.White, navigationIconContentColor = Color.White),
            actions = {
                if (state.idAnterior != null) {
                    IconButton(onClick = { viewModel.navegarA(state.idAnterior!!) }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Anterior", tint = Color.White)
                    }
                }
                if (state.idSiguiente != null) {
                    IconButton(onClick = { viewModel.navegarA(state.idSiguiente!!) }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Siguiente", tint = Color.White)
                    }
                }
                if (state.campania?.estaActiva == true) {
                    IconButton(onClick = { viewModel.finalizarCampania() }) { 
                        Icon(Icons.Default.Archive, contentDescription = "Finalizar Campaña", tint = Color.White) 
                    }
                }
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
            val campania = state.campania
            if (campania != null) {
                HeaderCampania(campania = campania)

                LazyVerticalGrid(
                    columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        TabInfo(campania = campania, onEditar = { onGoToEditar(campania.id) })
                    }
                    item {
                        CardModuloTareas(campaniaId = campania.id, onGoToTareas = { onGoToTareas(campania.id) }, onGoToNueva = { onGoToNuevaTarea(campania.id) })
                    }
                    item {
                        CardModuloInsumos(campaniaId = campania.id, onGoToInsumos = { onGoToInsumos(campania.id) }, onGoToNuevo = { onGoToNuevoInsumo(campania.id) })
                    }
                    item {
                        CardModuloCosechas(campaniaId = campania.id, onGoToCosechas = { onGoToCosechas(campania.id) }, onGoToNueva = { onGoToNuevaCosecha(campania.id) })
                    }
                    item {
                        CardModuloObservaciones(
                            campaniaId = campania.id,
                            onGoToObservaciones = { onGoToObservaciones(campania.id) },
                            onGoToNuevaObservacion = { onGoToObservaciones(campania.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderCampania(campania: com.itec.donelio.domain.model.Campania) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Eco, contentDescription = null, tint = AgriVerde, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(campania.nombre, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextoPrincipal)
        }
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoChip(label = "Cultivo", value = campania.cultivoNombre.ifBlank { "—" })
            InfoChip(label = "Inicio", value = formatFecha(campania.fechaInicio))
            InfoChip(label = "Estado", value = if (campania.estaActiva) "Activa" else "Inactiva")
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
private fun TabInfo(campania: com.itec.donelio.domain.model.Campania, onEditar: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFE7E5E4))) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetalleFila(label = "Nombre", value = campania.nombre)
                DetalleFila(label = "Cultivo", value = campania.cultivoNombre.ifBlank { "Sin especificar" })
                DetalleFila(label = "Fecha de inicio", value = formatFecha(campania.fechaInicio))
                DetalleFila(label = "Estado", value = if (campania.estaActiva) "Activa" else "Inactiva")
            }
        }
        Button(
            onClick = onEditar,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AgriVerde),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Editar Datos", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
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
private fun ModuloCardBase(
    title: String,
    icon: ImageVector,
    summary: String,
    subSummary: String? = null,
    onCardClick: () -> Unit,
    onQuickAddClick: (() -> Unit)? = null
) {
    Card(
        onClick = onCardClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE7E5E4)),
        modifier = Modifier.fillMaxWidth().aspectRatio(0.9f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AgriFondo),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = AgriVerde, modifier = Modifier.size(24.dp))
                }
                if (onQuickAddClick != null) {
                    IconButton(
                        onClick = onQuickAddClick,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AgriVerde)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextoPrincipal)
                Spacer(modifier = Modifier.height(4.dp))
                Text(summary, color = TextoSecundario, fontSize = 13.sp, lineHeight = 16.sp)
                if (subSummary != null) {
                    Text(subSummary, color = AgriVerde, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable
private fun CardModuloTareas(campaniaId: Int, onGoToTareas: () -> Unit, onGoToNueva: () -> Unit) {
    val vm: TareaViewModel = hiltViewModel(key = "card_tareas_$campaniaId")
    val tareasUi by vm.tareasUi.collectAsState()
    val tareas = tareasUi.map { it.tarea }
    val pendientes = tareas.count { !it.confirmar }
    val completadas = tareas.count { it.confirmar }

    LaunchedEffect(campaniaId) { vm.sincronizarCampania(campaniaId) }

    ModuloCardBase(
        title = "Tareas",
        icon = Icons.Default.CheckCircle,
        summary = "$pendientes pendientes",
        subSummary = "$completadas completadas",
        onCardClick = onGoToTareas,
        onQuickAddClick = onGoToNueva
    )
}

@Composable
private fun CardModuloInsumos(campaniaId: Int, onGoToInsumos: () -> Unit, onGoToNuevo: () -> Unit) {
    val vm: InsumoVinculacionViewModel = hiltViewModel(key = "card_insumos_$campaniaId")
    val vinculados by vm.insumosVinculados.collectAsState()
    val total = vinculados.sumOf { it.cantidad * it.precio }

    LaunchedEffect(campaniaId) { vm.seleccionarCampania(campaniaId) }

    ModuloCardBase(
        title = "Insumos",
        icon = Icons.Default.Inventory,
        summary = "${vinculados.size} insumos",
        subSummary = "$ ${"%,.2f".format(total)}",
        onCardClick = onGoToInsumos,
        onQuickAddClick = onGoToNuevo
    )
}

@Composable
private fun CardModuloCosechas(campaniaId: Int, onGoToCosechas: () -> Unit, onGoToNueva: () -> Unit) {
    val vm: CosechaViewModel = hiltViewModel(key = "card_cosechas_$campaniaId")
    val almacenadas by vm.almacenadas.collectAsState()
    val totalAlmacenado = almacenadas.sumOf { it.cantidad }

    LaunchedEffect(campaniaId) { vm.sincronizarCampania(campaniaId) }

    ModuloCardBase(
        title = "Cosechas",
        icon = Icons.Default.Agriculture,
        summary = "${almacenadas.size} registradas",
        subSummary = formatCantidad(totalAlmacenado),
        onCardClick = onGoToCosechas,
        onQuickAddClick = onGoToNueva
    )
}

@Composable
private fun CardModuloObservaciones(
    campaniaId: Int,
    onGoToObservaciones: () -> Unit,
    onGoToNuevaObservacion: () -> Unit
) {
    val vm: ObservacionViewModel = hiltViewModel(key = "card_observaciones_$campaniaId")
    val observaciones by vm.observaciones.collectAsState()

    LaunchedEffect(campaniaId) { vm.seleccionarCampania(campaniaId) }

    ModuloCardBase(
        title = "Observaciones",
        icon = Icons.Default.NoteAlt,
        summary = "${observaciones.size} registradas",
        onCardClick = onGoToObservaciones,
        onQuickAddClick = onGoToNuevaObservacion
    )
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
