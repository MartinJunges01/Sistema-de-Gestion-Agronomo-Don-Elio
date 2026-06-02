package com.itec.donelio.presentation.ui.screen.tarea

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.presentation.ui.components.CalendarioSemanal
import com.itec.donelio.presentation.ui.components.SelectorCampania
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal
import com.itec.donelio.presentation.ui.theme.TextoSecundario
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
    val tareas by viewModel.tareas.collectAsState()
    val campanias by viewModel.campanias.collectAsState()
    val campaniaIdSeleccionada by viewModel.campaniaIdSeleccionada.collectAsState()
    val fechaSeleccionada by viewModel.fechaSeleccionada.collectAsState()
    val isCampaniaValid by viewModel.isCampaniaValid.collectAsState()

    val pendientes = tareas.filter { !it.confirmar }
    val completadas = tareas.filter { it.confirmar }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Agenda y Tareas", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
        )
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                SelectorCampania(
                    campanias = campanias,
                    selectedCampaniaId = campaniaIdSeleccionada,
                    onCampaniaSelected = { viewModel.seleccionarCampania(it) },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            item {
                Text("Planificación Estratégica", color = TextoSecundario)
                Spacer(modifier = Modifier.height(12.dp))
                CalendarioSemanal(
                    selectedDate = fechaSeleccionada,
                    onDateSelected = { viewModel.seleccionarFecha(it) }
                )
            }

            item { HorizontalDivider(color = Color(0xFFE7E5E4), modifier = Modifier.padding(vertical = 8.dp)) }

            if (tareas.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No hay tareas para esta campaña", color = TextoSecundario, fontSize = 16.sp)
                    }
                }
            } else {
                item {
                    Text("Pendientes", fontWeight = FontWeight.Bold, color = TextoPrincipal, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(pendientes) { tarea ->
                    TarjetaTareaItem(tarea = tarea, onToggle = { viewModel.toggleCompletada(tarea) })
                }

                if (completadas.isNotEmpty()) {
                    item {
                        Text("Completadas", fontWeight = FontWeight.Bold, color = TextoPrincipal, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(completadas) { tarea ->
                        TarjetaTareaItem(tarea = tarea, onToggle = { viewModel.toggleCompletada(tarea) })
                    }
                }
            }

            item {
                Button(
                    onClick = onGoToNuevaTarea,
                    modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriVerde),
                    enabled = isCampaniaValid
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Programar Nueva Tarea")
                }
            }
        }
    }
}

@Composable
private fun TarjetaTareaItem(tarea: Tarea, onToggle: () -> Unit) {
    val completada = tarea.confirmar
    Card(
        colors = CardDefaults.cardColors(containerColor = if (completada) AgriFondo else Color.White),
        border = BorderStroke(1.dp, if (completada) Color(0xFFE7E5E4) else Color(0xFFE7E5E4)),
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
                Text(
                    tarea.nombre,
                    fontWeight = FontWeight.Bold,
                    color = if (completada) TextoSecundario else TextoPrincipal,
                    textDecoration = if (completada) TextDecoration.LineThrough else TextDecoration.None
                )
                Text(
                    "${formatFechaTarea(tarea.fecha)} ${tarea.hora}",
                    fontSize = 14.sp,
                    color = TextoSecundario
                )
            }
        }
    }
}

private fun formatFechaTarea(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
