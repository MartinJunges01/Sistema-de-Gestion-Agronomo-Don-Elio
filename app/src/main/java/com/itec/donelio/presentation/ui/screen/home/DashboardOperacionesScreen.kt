package com.itec.donelio.presentation.ui.screen.home

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.Tarea
import com.itec.donelio.presentation.ui.components.HeaderSectionAgriCore
import com.itec.donelio.presentation.ui.theme.AgriRojoFondo
import com.itec.donelio.presentation.ui.theme.AgriRojoUrgencia
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal
import com.itec.donelio.presentation.ui.theme.TextoSecundario
import com.itec.donelio.presentation.viewmodel.home.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardOperacionesScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onGoToConfig: () -> Unit,
    onGoToDetalle: (campaniaId: Int) -> Unit,
    onGoToTareas: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val campanias by viewModel.campanias.collectAsState()
    val tareas by viewModel.tareasPendientes.collectAsState()
    val userName by viewModel.userName.collectAsState()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { _ -> }

        LaunchedEffect(Unit) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { 
            HeaderSectionAgriCore(
                userName = userName, 
                onGoToConfig = onGoToConfig,
                onLogout = {
                    viewModel.cerrarSesion()
                    onLogoutSuccess()
                }
            ) 
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Dashboard", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AgriVerde)
                Text("Resumen de operaciones en tiempo real", color = TextoSecundario, fontSize = 14.sp)
            }
        }


        if (tareas.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("Tareas Próximas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal, modifier = Modifier.padding(bottom = 8.dp))
                }
            }
            val hoy = System.currentTimeMillis()
            items(tareas, key = { "t_${it.id}" }) { tarea ->
                val isVencida = tarea.fecha < hoy
                val containerColor = if (isVencida) AgriRojoFondo else Color.White
                val iconColor = if (isVencida) AgriRojoUrgencia else AgriVerde
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { onGoToDetalle(tarea.idCampania) },
                    colors = CardDefaults.cardColors(containerColor = containerColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TaskAlt, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(tarea.nombre, fontWeight = FontWeight.Bold, color = TextoPrincipal, fontSize = 16.sp)
                            Text("${formatFecha(tarea.fecha)} - ${tarea.hora}", fontSize = 14.sp, color = TextoSecundario)
                        }
                    }
                }
            }
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), contentAlignment = Alignment.CenterEnd) {
                    TextButton(onClick = onGoToTareas) {
                        Text("Ver todas →", color = AgriVerde, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (campanias.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Agriculture, contentDescription = null, modifier = Modifier.size(64.dp), tint = TextoSecundario)
                        Text("No hay campañas", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal, textAlign = TextAlign.Center)
                        Text("Presiona + para crear una", fontSize = 14.sp, color = TextoSecundario, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("Campañas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal, modifier = Modifier.padding(bottom = 8.dp))
                }
            }
            items(campanias, key = { it.id }) { campania ->
                CampaniaCard(campania = campania, onClick = { onGoToDetalle(campania.id) })
            }
        }
    }
}

@Composable
private fun CampaniaCard(campania: Campania, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
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
                Text(campania.cultivo, fontSize = 14.sp, color = TextoSecundario)
                Text(formatFecha(campania.fechaInicio), fontSize = 12.sp, color = TextoSecundario)
            }
            if (campania.estaActiva) {
                Surface(shape = RoundedCornerShape(8.dp), color = AgriVerde.copy(alpha = 0.1f)) {
                    Text("Activa", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 11.sp, color = AgriVerde, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ChevronRight, contentDescription = "Ver detalle", tint = TextoSecundario)
        }
    }
}

private fun formatFecha(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
