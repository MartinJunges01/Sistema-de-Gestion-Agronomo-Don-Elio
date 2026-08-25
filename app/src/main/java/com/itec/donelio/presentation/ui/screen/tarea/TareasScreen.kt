package com.itec.donelio.presentation.ui.screen.tarea

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itec.donelio.presentation.ui.components.SelectorRangoFechas
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal
import com.itec.donelio.presentation.ui.theme.TextoSecundario
import com.itec.donelio.presentation.viewmodel.tarea.TareaUiModel
import com.itec.donelio.presentation.viewmodel.tarea.TareaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasScreen(
    campaniaId: Int = -1,
    viewModel: TareaViewModel = hiltViewModel(),
    onGoToNuevaTarea: () -> Unit,
    onBack: () -> Unit
) {
    val tareasUi by viewModel.tareasUi.collectAsState()
    val campanias by viewModel.campanias.collectAsState()
    val filtroCampania by viewModel.filtroCampania.collectAsState()
    val filtroFechas by viewModel.filtroFechas.collectAsState()
    val isCampaniaValid by viewModel.isCampaniaValid.collectAsState()

    var mostrarSelectorFechas by remember { mutableStateOf(false) }
    var mostrarMenuCampanias by remember { mutableStateOf(false) }

    val pendientes = tareasUi.filter { !it.tarea.confirmar }
    val completadas = tareasUi.filter { it.tarea.confirmar }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Agenda y Tareas", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
        )

        // BARRA DE FILTROS
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.FilterList, contentDescription = "Filtros", tint = TextoSecundario)
            
            // Filtro Campaña
            Box {
                FilterChip(
                    selected = filtroCampania != null,
                    onClick = { mostrarMenuCampanias = true },
                    label = { 
                        val nombre = if (filtroCampania == null) "Todas las Campañas" 
                                     else campanias.find { it.id == filtroCampania }?.nombre ?: "Campaña"
                        Text(nombre)
                    },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AgriVerde.copy(alpha = 0.1f), selectedLabelColor = AgriVerde)
                )
                DropdownMenu(expanded = mostrarMenuCampanias, onDismissRequest = { mostrarMenuCampanias = false }) {
                    DropdownMenuItem(
                        text = { Text("Todas las Campañas", fontWeight = FontWeight.Bold) },
                        onClick = { viewModel.seleccionarCampania(null); mostrarMenuCampanias = false }
                    )
                    HorizontalDivider()
                    campanias.forEach { c ->
                        DropdownMenuItem(text = { Text(c.nombre) }, onClick = { viewModel.seleccionarCampania(c.id); mostrarMenuCampanias = false })
                    }
                }
            }

            // Filtro Fechas
            FilterChip(
                selected = filtroFechas != null,
                onClick = { mostrarSelectorFechas = true },
                label = { 
                    val texto = if (filtroFechas == null) "Fechas" else "Rango Activo"
                    Text(texto) 
                },
                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp)) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AgriVerde.copy(alpha = 0.1f), selectedLabelColor = AgriVerde)
            )

            // Limpiar
            if (filtroCampania != null || filtroFechas != null) {
                IconButton(onClick = { viewModel.limpiarFiltros() }) {
                    Icon(Icons.Default.Clear, contentDescription = "Limpiar Filtros", tint = Color.Red.copy(alpha = 0.7f))
                }
            }
        }

        HorizontalDivider(color = Color(0xFFE7E5E4))

        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (tareasUi.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No hay tareas que coincidan con los filtros", color = TextoSecundario, fontSize = 16.sp)
                    }
                }
            } else {
                item {
                    Text(if (filtroFechas == null) "Próximas y Pendientes" else "Tareas Filtradas (Pendientes)", fontWeight = FontWeight.Bold, color = TextoPrincipal, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (pendientes.isEmpty()) {
                    item { Text("No hay tareas pendientes.", color = TextoSecundario, fontSize = 14.sp) }
                } else {
                    items(pendientes) { uiModel ->
                        TarjetaTareaItem(uiModel = uiModel, onToggle = { viewModel.toggleCompletada(uiModel.tarea) })
                    }
                }

                if (completadas.isNotEmpty()) {
                    item {
                        Text("Completadas", fontWeight = FontWeight.Bold, color = TextoPrincipal, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(completadas) { uiModel ->
                        TarjetaTareaItem(uiModel = uiModel, onToggle = { viewModel.toggleCompletada(uiModel.tarea) })
                    }
                }
            }

            item {
                Button(
                    onClick = onGoToNuevaTarea,
                    modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriVerde),
                    enabled = isCampaniaValid || filtroCampania != null // Se permite si hay una campaña seleccionada
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Programar Nueva Tarea")
                }
            }
        }
    }

    if (mostrarSelectorFechas) {
        SelectorRangoFechas(
            mostrarDialogo = true,
            onDismiss = { mostrarSelectorFechas = false },
            onRangoSeleccionado = { rango -> viewModel.seleccionarFechas(rango) }
        )
    }
}

@Composable
private fun TarjetaTareaItem(uiModel: TareaUiModel, onToggle: () -> Unit) {
    val tarea = uiModel.tarea
    val completada = tarea.confirmar
    val isVencida = uiModel.isVencida && !completada

    val bgColor = when {
        completada -> AgriFondo
        isVencida -> Color(0xFFFEF2F2)
        else -> Color.White
    }
    
    val borderColor = when {
        completada -> Color(0xFFE7E5E4)
        isVencida -> Color(0xFFFCA5A5)
        else -> Color(0xFFE7E5E4)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = completada,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = AgriVerde)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = tarea.nombre,
                        fontWeight = FontWeight.Bold,
                        color = if (completada) TextoSecundario else if (isVencida) Color(0xFFB91C1C) else TextoPrincipal,
                        textDecoration = if (completada) TextDecoration.LineThrough else TextDecoration.None,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${formatFechaTarea(tarea.fecha)} ${tarea.hora}",
                        fontSize = 13.sp,
                        color = if (isVencida && !completada) Color(0xFFDC2626) else TextoSecundario,
                        fontWeight = if (isVencida && !completada) FontWeight.Bold else FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = AgriVerde.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = uiModel.campaniaNombre,
                            fontSize = 11.sp,
                            color = AgriVerde,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatFechaTarea(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
