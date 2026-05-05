// DonElioHomeScreen.kt
package com.itec.donelio.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas

val Stone50 = Color(0xFFFAFAF9)
val Stone800 = Color(0xFF292524)
val Emerald800 = Color(0xFF065F46)
val Emerald600 = Color(0xFF059669)

enum class Destino {
    Login, Registro,
    Home, Campanias, DetalleCampania, FormularioCampania,
    Insumos, FormularioInsumo, CatalogoInsumos,
    Reportes,
    NuevaTarea, Observaciones, ConfiguracionDB,
    Tareas, Cosechas, FormularioCosecha // Agregados flujos de cosecha
}

@Composable
fun DonElioApp() {
    var pantallaActual by remember { mutableStateOf(Destino.Login) }

    Scaffold(
        containerColor = Stone50,
        floatingActionButton = {
            if (pantallaActual in listOf(Destino.Home, Destino.Campanias, Destino.CatalogoInsumos, Destino.Cosechas)) {
                FloatingActionButton(
                    onClick = {
                        pantallaActual = when(pantallaActual) {
                            Destino.Campanias -> Destino.FormularioCampania
                            Destino.CatalogoInsumos -> Destino.FormularioInsumo
                            Destino.Cosechas -> Destino.FormularioCosecha
                            else -> Destino.NuevaTarea
                        }
                    },
                    containerColor = Emerald600,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }
            }
        },
        bottomBar = {
            if (pantallaActual in listOf(Destino.Home, Destino.Tareas, Destino.Insumos, Destino.Reportes)) {
                DonElioBottomNav(pantallaActual) { nuevoDestino -> pantallaActual = nuevoDestino }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (pantallaActual) {
                Destino.Login -> LoginScreen(onLoginSuccess = { pantallaActual = Destino.Home }, onGoToRegister = { pantallaActual = Destino.Registro })
                Destino.Registro -> RegistroScreen(onRegisterSuccess = { pantallaActual = Destino.Home }, onGoToLogin = { pantallaActual = Destino.Login })

                Destino.Home -> DonElioHomeScreen(
                    onGoToConfig = { pantallaActual = Destino.ConfiguracionDB },
                    onGoToCampanias = { pantallaActual = Destino.Campanias }
                )

                Destino.Tareas -> TareasScreen(
                    onGoToNuevaTarea = { pantallaActual = Destino.NuevaTarea },
                    onGoToCampanias = { pantallaActual = Destino.DetalleCampania }
                )

                Destino.Campanias -> CampaniasScreen(onGoToDetail = { pantallaActual = Destino.DetalleCampania })
                Destino.DetalleCampania -> DetalleCampaniaScreen(
                    onBack = { pantallaActual = Destino.Campanias },
                    onGoToObs = { pantallaActual = Destino.Observaciones },
                    onGoToTask = { pantallaActual = Destino.Tareas },
                    onGoToCosechas = { pantallaActual = Destino.Cosechas },
                    onGoToInsumos = { pantallaActual = Destino.Insumos }
                )
                Destino.FormularioCampania -> FormularioCampaniaScreen { pantallaActual = Destino.Campanias }

                Destino.Cosechas -> CosechasScreen(onBack = { pantallaActual = Destino.DetalleCampania })
                Destino.FormularioCosecha -> FormularioCosechaScreen { pantallaActual = Destino.Cosechas }

                Destino.Insumos -> InsumosScreen(
                    onGoToCatalogo = { pantallaActual = Destino.CatalogoInsumos },
                    onGoToCampanias = { pantallaActual = Destino.DetalleCampania }
                )
                Destino.CatalogoInsumos -> CatalogoInsumosScreen(
                    onBack = { pantallaActual = Destino.Insumos },
                    onGoToFormulario = { pantallaActual = Destino.FormularioInsumo }
                )
                Destino.FormularioInsumo -> FormularioInsumoScreen { pantallaActual = Destino.CatalogoInsumos }
                Destino.Reportes -> ReportesScreen()
                Destino.NuevaTarea -> NuevaTareaScreen { pantallaActual = Destino.Tareas }
                Destino.Observaciones -> ObservacionesScreen { pantallaActual = Destino.DetalleCampania }
                Destino.ConfiguracionDB -> ConfiguracionDBScreen { pantallaActual = Destino.Home }
            }
        }
    }
}

// --- PANTALLA TAREAS (CU5 y CU5.4) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasScreen(onGoToNuevaTarea: () -> Unit, onGoToCampanias: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Tareas", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50))
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text("Campaña Activa", fontWeight = FontWeight.Bold, color = Stone800, modifier = Modifier.padding(vertical = 8.dp))
                CampanaSeleccionadaCard(onClick = onGoToCampanias)
            }
            item {
                Text("Pendientes", fontWeight = FontWeight.Bold, color = Stone800, modifier = Modifier.padding(vertical = 8.dp))
                var completada1 by remember { mutableStateOf(false) }
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().clickable { onGoToNuevaTarea() }) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = completada1, onCheckedChange = { completada1 = it }, colors = CheckboxDefaults.colors(checkedColor = Emerald600))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Riego pendiente", fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                            Text("Lote 4 (Maíz) | Hoy 12:00 PM", fontSize = 14.sp, color = Color(0xFF92400E))
                        }
                    }
                }
            }
            item {
                Text("En Progreso", fontWeight = FontWeight.Bold, color = Stone800, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                var completada2 by remember { mutableStateOf(false) }
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth().clickable { onGoToNuevaTarea() }) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = completada2, onCheckedChange = { completada2 = it }, colors = CheckboxDefaults.colors(checkedColor = Emerald600))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Aplicar fungicida", fontWeight = FontWeight.Bold, color = Stone800)
                            Text("Lote B - Soja | En ejecución", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
            }
            item {
                Button(onClick = onGoToNuevaTarea, modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 16.dp), colors = ButtonDefaults.buttonColors(containerColor = Emerald600)) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Programar Nueva Tarea")
                }
            }
        }
    }
}

// --- PANTALLA DETALLE CAMPAÑA (CU2 - HUB CENTRAL) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCampaniaScreen(onBack: () -> Unit, onGoToObs: () -> Unit, onGoToTask: () -> Unit, onGoToCosechas: () -> Unit, onGoToInsumos: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Campaña Soja 2026", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Emerald800, titleContentColor = Color.White, navigationIconContentColor = Color.White),
            actions = {
                IconButton(onClick = { /* Confirmar Eliminación (CU3) */ }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar Campaña", tint = Color.White)
                }
            }
        )
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Text("Gestión de la Campaña", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Stone800, modifier = Modifier.padding(bottom = 8.dp))
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                    ModuleCard(title = "Tareas", icon = Icons.Default.CheckCircle, onClick = onGoToTask, modifier = Modifier.weight(1f))
                    ModuleCard(title = "Insumos", icon = Icons.Default.Inventory, onClick = onGoToInsumos, modifier = Modifier.weight(1f))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                    ModuleCard(title = "Cosechas", icon = Icons.Default.Agriculture, onClick = onGoToCosechas, modifier = Modifier.weight(1f))
                    ModuleCard(title = "Observaciones", icon = Icons.Default.NoteAlt, onClick = onGoToObs, modifier = Modifier.weight(1f))
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { /* CU4: Editar Campaña */ }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Stone800)) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Editar Datos de Campaña")
                }
            }
        }
    }
}

@Composable
fun ModuleCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = modifier.height(100.dp).clickable { onClick() }) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = title, tint = Emerald600, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, color = Stone800)
        }
    }
}

// --- PANTALLAS DE COSECHAS (CU6 Y CU7) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CosechasScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Gestión de Cosechas", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50))
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text("Cosechas Almacenadas", fontWeight = FontWeight.Bold, color = Stone800) }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Emerald600), modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text("Lote Norte - Soja", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("Silobolsa 1 | 05/05/2026") },
                        trailingContent = { Text("200 Tn", fontWeight = FontWeight.Bold, color = Emerald800) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { Text("Cosechas No Almacenadas (Venta/Reserva)", fontWeight = FontWeight.Bold, color = Stone800) }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)), border = BorderStroke(1.dp, Color(0xFFD97706)), modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text("Lote Sur - Venta Directa", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("Precio: $45,000 | 10/05/2026") },
                        trailingContent = { Text("150 Tn", fontWeight = FontWeight.Bold, color = Color(0xFFB45309)) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioCosechaScreen(onBack: () -> Unit) {
    var almacenado by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Registrar Cosecha", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Cultivo") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Cantidad") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Unidad (Ej. Tn)") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Fecha") }, modifier = Modifier.fillMaxWidth(), trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) })

            // Switch CU7
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Checkbox(checked = almacenado, onCheckedChange = { almacenado = it }, colors = CheckboxDefaults.colors(checkedColor = Emerald600))
                Text("Almacenar en el establecimiento", fontWeight = FontWeight.Medium)
            }

            if (almacenado) {
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Almacén (Silo, Silobolsa)") }, modifier = Modifier.fillMaxWidth())
            } else {
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Tipo (Venta, Alimento Vacuno, Reserva)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Precio (Opcional)") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) })
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Emerald600), shape = RoundedCornerShape(12.dp)) { Text("Guardar Registro", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}
// --- PANTALLA REPORTES ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Reportes (CU10)", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50))
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Producción por Campaña", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Stone800)
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val barWidth = size.width / 9f
                                val maxVal = 520f
                                val values = listOf(380f, 450f, 210f, 520f)
                                val colors = listOf(Emerald600, Emerald800, Color(0xFFD1FAE5), Stone800)
                                values.forEachIndexed { index, value ->
                                    val barHeight = (value / maxVal) * size.height
                                    val startX = (index * 2 + 1) * barWidth
                                    drawRoundRect(color = colors[index], topLeft = Offset(startX, size.height - barHeight), size = Size(barWidth, barHeight), cornerRadius = CornerRadius(8f, 8f))
                                }
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceAround) {
                            Text("Sj '25", fontSize = 12.sp, color = Color.Gray)
                            Text("Sj '26", fontSize = 12.sp, color = Color.Gray)
                            Text("Tr '25", fontSize = 12.sp, color = Color.Gray)
                            Text("Mz '25", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Evolución de Costos", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Stone800)
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                            Canvas(modifier = Modifier.fillMaxSize().padding(vertical = 16.dp)) {
                                val values = listOf(8000f, 9200f, 11000f, 12450f)
                                val minVal = 7000f
                                val maxVal = 13000f
                                val stepX = size.width / (values.size - 1)
                                for (i in 0 until values.size - 1) {
                                    val startX = i * stepX
                                    val startY = size.height - ((values[i] - minVal) / (maxVal - minVal) * size.height)
                                    val endX = (i + 1) * stepX
                                    val endY = size.height - ((values[i + 1] - minVal) / (maxVal - minVal) * size.height)
                                    drawLine(color = Emerald600, start = Offset(startX, startY), end = Offset(endX, endY), strokeWidth = 8f, cap = StrokeCap.Round)
                                    drawCircle(color = Emerald800, radius = 12f, center = Offset(startX, startY))
                                    if (i == values.size - 2) drawCircle(color = Emerald800, radius = 12f, center = Offset(endX, endY))
                                }
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Ene", fontSize = 12.sp, color = Color.Gray)
                            Text("Feb", fontSize = 12.sp, color = Color.Gray)
                            Text("Mar", fontSize = 12.sp, color = Color.Gray)
                            Text("Abr", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Insumos Aplicados", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Stone800)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("• Urea (40%)", color = Emerald800, fontSize = 14.sp)
                            Text("• Fungicida (25%)", color = Emerald600, fontSize = 14.sp)
                            Text("• Semillas (20%)", color = Stone800, fontSize = 14.sp)
                            Text("• Otros (15%)", color = Color.Gray, fontSize = 14.sp)
                        }
                        Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeWidth = 40f
                                drawArc(color = Emerald800, startAngle = -90f, sweepAngle = 144f, useCenter = false, style = Stroke(strokeWidth))
                                drawArc(color = Emerald600, startAngle = 54f, sweepAngle = 90f, useCenter = false, style = Stroke(strokeWidth))
                                drawArc(color = Stone800, startAngle = 144f, sweepAngle = 72f, useCenter = false, style = Stroke(strokeWidth))
                                drawArc(color = Color.LightGray, startAngle = 216f, sweepAngle = 54f, useCenter = false, style = Stroke(strokeWidth))
                            }
                        }
                    }
                }
            }
            item {
                Button(onClick = { /* CU11 */ }, modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 16.dp), colors = ButtonDefaults.buttonColors(containerColor = Stone800)) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Exportar Reporte a PDF")
                }
            }
        }
    }
}

// --- PANTALLA HOME ---
@Composable
fun DonElioHomeScreen(onGoToConfig: () -> Unit, onGoToCampanias: () -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { HeaderSection(onGoToConfig) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { MetricsSection() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Campaña Activa", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Stone800, modifier = Modifier.padding(bottom = 12.dp))
                CampanaSeleccionadaCard(onClick = onGoToCampanias)
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { TasksSection() }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// --- PANTALLAS DE ACCESO ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onGoToRegister: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color(0xFFD1FAE5), modifier = Modifier.size(100.dp)) { Icon(Icons.Default.Agriculture, contentDescription = null, modifier = Modifier.padding(20.dp), tint = Emerald800) }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Don Elio", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Emerald800)
        Text("Sistema de Gestión Agrícola", color = Color.Gray, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(48.dp))
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Nombre de usuario") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = PasswordVisualTransformation())
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onLoginSuccess, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Emerald600), shape = RoundedCornerShape(12.dp)) { Text("Ingresar", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onGoToRegister) { Text("¿No tienes cuenta? Regístrate aquí", color = Emerald800, fontWeight = FontWeight.SemiBold) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(onRegisterSuccess: () -> Unit, onGoToLogin: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Crear Cuenta", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Emerald800)
        Text("Configura tu acceso local", color = Color.Gray, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(40.dp))
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Nombre de usuario") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = PasswordVisualTransformation())
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onRegisterSuccess, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Emerald600), shape = RoundedCornerShape(12.dp)) { Text("Registrarse", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onGoToLogin) { Text("¿Ya tienes cuenta? Inicia sesión", color = Emerald800, fontWeight = FontWeight.SemiBold) }
    }
}

// --- CAMPANAS ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaniasScreen(onGoToDetail: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Campañas (CU1-CU4)", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50))
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth().clickable { onGoToDetail() }) {
                    ListItem(headlineContent = { Text("Campaña Soja 2026", fontWeight = FontWeight.Bold) }, supportingContent = { Text("Lotes: Norte, Sur | Inicio: 10/04/26") }, leadingContent = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Emerald600) }, trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) }, colors = ListItemDefaults.colors(containerColor = Color.Transparent))
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth().clickable { onGoToDetail() }) {
                    ListItem(headlineContent = { Text("Campaña Trigo Invierno", fontWeight = FontWeight.Bold) }, supportingContent = { Text("Lote: Este | Estado: En preparación") }, leadingContent = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Emerald600) }, trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) }, colors = ListItemDefaults.colors(containerColor = Color.Transparent))
                }
            }
        }
    }
}

// --- INSUMOS (VINCULACIÓN) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsumosScreen(onGoToCatalogo: () -> Unit, onGoToCampanias: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Vincular Insumos (CU9)", fontWeight = FontWeight.Bold) },
            actions = {
                TextButton(onClick = onGoToCatalogo) {
                    Icon(Icons.Default.Settings, contentDescription = "Catálogo", modifier = Modifier.size(20.dp), tint = Emerald600)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Catálogo", color = Emerald600)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50)
        )
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Campaña Activa
            item {
                Text("Campaña Activa", fontWeight = FontWeight.Bold, color = Stone800, modifier = Modifier.padding(vertical = 8.dp))
                CampanaSeleccionadaCard(onClick = onGoToCampanias)
            }

            // Insumos ya vinculados
            item {
                Text("Insumos Vinculados", fontWeight = FontWeight.Bold, color = Stone800, modifier = Modifier.padding(vertical = 8.dp))
            }
            items(listOf("Urea Granulada", "Fungicida Glifosato", "Semilla Soja 50kg")) { insumo ->
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5)), border = BorderStroke(1.dp, Emerald600), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Vinculado", tint = Emerald600)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(insumo, fontWeight = FontWeight.Bold, color = Stone800)
                            Text("Vinculado a campaña activa", fontSize = 12.sp, color = Emerald800)
                        }
                        Icon(Icons.Default.Delete, contentDescription = "Desvincular", tint = Color(0xFFDC2626))
                    }
                }
            }

            // Insumos disponibles para vincular
            item {
                Text("Disponibles para Vincular", fontWeight = FontWeight.Bold, color = Stone800, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
            }
            items(listOf("Herbicida Atrazina", "Fertilizante DAP", "Insecticida Lambda")) { insumo ->
                var vinculado by remember { mutableStateOf(false) }
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = vinculado, onCheckedChange = { vinculado = it })
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(insumo, fontWeight = FontWeight.Bold, color = Stone800)
                            Text("Toca para vincular", fontSize = 12.sp, color = Color.Gray)
                        }
                        if (vinculado) Icon(Icons.Default.CheckCircle, contentDescription = "Vinculado", tint = Emerald600)
                    }
                }
            }
        }
    }
}

// --- CATÁLOGO GENERAL DE INSUMOS ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoInsumosScreen(onBack: () -> Unit, onGoToFormulario: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Catálogo de Insumos", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50)
        )
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(listOf(
                Triple("Urea Granulada", "Fertilizante", "1500 kg"),
                Triple("Fungicida Glifosato", "Herbicida", "200 Lts"),
                Triple("Semilla Soja 50kg", "Semilla", "80 bolsas"),
                Triple("Herbicida Atrazina", "Herbicida", "300 Lts"),
                Triple("Fertilizante DAP", "Fertilizante", "2000 kg"),
                Triple("Insecticida Lambda", "Insecticida", "150 Lts")
            )) { (nombre, categoria, stock) ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text(nombre, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("$categoria | Stock: $stock") },
                        leadingContent = { Icon(Icons.Default.Inventory, contentDescription = null, tint = Emerald600) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
            item {
                Button(onClick = onGoToFormulario, modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 16.dp), colors = ButtonDefaults.buttonColors(containerColor = Emerald600), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar Nuevo Insumo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- DETALLE CAMPANIA ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCampaniaScreen(onBack: () -> Unit, onGoToObs: () -> Unit, onGoToTask: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Campaña Soja 2026", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Emerald800, titleContentColor = Color.White, navigationIconContentColor = Color.White))
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = onGoToTask, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Stone800)) { Text("Ver Tareas (CU5)", fontSize = 12.sp) }
                    Button(onClick = onGoToObs, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Stone800)) { Text("Observaciones (CU8)", fontSize = 12.sp) }
                }
            }
            item { Text("Cosechas Registradas (CU6)", fontWeight = FontWeight.Bold, fontSize = 18.sp) }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth()) {
                    ListItem(headlineContent = { Text("Cosecha Lote Norte") }, supportingContent = { Text("Fecha: 05/05/26 | Silobolsa 1") }, trailingContent = { Text("200 Tn", fontWeight = FontWeight.Bold, color = Emerald600) }, colors = ListItemDefaults.colors(containerColor = Color.Transparent))
                }
            }
        }
    }
}

// --- OBSERVACIONES ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObservacionesScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Observaciones (CU8)", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Escribe una nota...") }, modifier = Modifier.fillMaxWidth().height(150.dp), maxLines = 5)
            Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Stone800)) { Icon(Icons.Default.CameraAlt, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Adjuntar Fotografía") }
        }
    }
}

// --- CONFIGURACION DB ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionDBScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Base de Datos (CU12/CU13)", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Stone800)) { Icon(Icons.Default.Upload, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Exportar Base de Datos (Respaldo)") }
            Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Emerald600)) { Icon(Icons.Default.Download, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Importar Base de Datos") }
        }
    }
}

// --- FORMULARIO CAMPANIA ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioCampaniaScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Crear Campaña (CU1)", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Nombre de la Campaña") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Cultivo (Ej: Soja)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Fecha de Inicio") }, modifier = Modifier.fillMaxWidth(), trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) })
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Emerald600), shape = RoundedCornerShape(12.dp)) { Text("Guardar Campaña", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

// --- FORMULARIO INSUMO ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioInsumoScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Registrar Insumo (CU9.5)", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Nombre del Insumo") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Categoría (Ej: Semilla, Fertilizante)") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) { OutlinedTextField(value = "", onValueChange = {}, label = { Text("Cantidad") }, modifier = Modifier.weight(1f)); OutlinedTextField(value = "", onValueChange = {}, label = { Text("Unidad (Kg, Lts)") }, modifier = Modifier.weight(1f)) }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Emerald600), shape = RoundedCornerShape(12.dp)) { Text("Guardar Insumo", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

// --- NUEVA TAREA ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaTareaScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Nueva Tarea (CU5.1)", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Nombre de la Tarea") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) { OutlinedTextField(value = "", onValueChange = {}, label = { Text("Fecha") }, modifier = Modifier.weight(1f), trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) }); OutlinedTextField(value = "", onValueChange = {}, label = { Text("Hora") }, modifier = Modifier.weight(1f)) }
            Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = true, onCheckedChange = {}); Text("Activar Notificación de Recordatorio") }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Emerald600), shape = RoundedCornerShape(12.dp)) { Text("Guardar Tarea", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

// --- COMPONENTES COMPARTIDOS ---
@Composable
fun HeaderSection(onGoToConfig: () -> Unit) {
    Surface(color = Emerald800, shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp), shadowElevation = 4.dp) {
        Row(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column {
                Text(text = "Martes, 28 de Abril", color = Color(0xFFA7F3D0), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(text = "Hola, Don Elio", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            Surface(color = Color(0xFF064E3B).copy(alpha = 0.5f), shape = RoundedCornerShape(50), border = BorderStroke(1.dp, Color(0xFF047857)), modifier = Modifier.clickable { onGoToConfig() }) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Offline", tint = Color.White, modifier = Modifier.size(14.dp))
                    Text("Local / DB", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MetricsSection() {
    Column {
        Text(text = "Resumen Actual", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Stone800, modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(horizontal = 16.dp)) {
            item { MetricCard("Costos Est.", "$12,450", "+5% vs mes anterior", Color(0xFFFEF3C7), Color(0xFFB45309), Icons.Default.AttachMoney) }
            item { MetricCard("Prod. Total", "450 Tn", "Soja - Campaña '26", Color(0xFFD1FAE5), Emerald800, Icons.Default.Agriculture) }
            item { MetricCard("Insumo Top", "Urea", "1,200 kg aplicados", Color(0xFFF5F5F4), Color(0xFF44403C), Icons.Default.Inventory) }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, subtitle: String, iconBgColor: Color, iconColor: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(modifier = Modifier.width(160.dp).height(112.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Color(0xFFF5F5F4))) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(iconBgColor).padding(4.dp)) { Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp)) }
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF78716C))
            }
            Column { Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1917)); Text(subtitle, fontSize = 12.sp, color = Color(0xFF78716C)) }
        }
    }
}

@Composable
fun CampanaSeleccionadaCard(onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().height(110.dp).clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Color(0xFFF5F5F4)), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Row(modifier = Modifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color(0xFFD1FAE5)).padding(12.dp), contentAlignment = Alignment.Center) { Icon(imageVector = Icons.Default.Eco, contentDescription = null, tint = Emerald800, modifier = Modifier.size(28.dp)) }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) { Text("Campaña Soja 2026", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Stone800); Spacer(modifier = Modifier.height(4.dp)); Text("Lote Norte - 450 Tn estimadas", fontSize = 14.sp, color = Color(0xFF78716C)) }
            Icon(Icons.Default.ChevronRight, contentDescription = "Ver Detalles", tint = Color(0xFFA8A29E))
        }
    }
}

@Composable
fun TasksSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = "Tareas Próximas y Alertas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Stone800, modifier = Modifier.padding(bottom = 16.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)), shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, topStart = 4.dp, bottomStart = 4.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) { Icon(Icons.Default.Warning, contentDescription = "Alerta", tint = Color(0xFFD97706)); Spacer(modifier = Modifier.width(12.dp)); Column(modifier = Modifier.weight(1f)) { Text("Riego pendiente", fontWeight = FontWeight.Bold, color = Color(0xFF78350F)); Text("Lote 4 (Maíz) necesita riego antes del mediodía.", fontSize = 14.sp, color = Color(0xFF92400E)) }; Icon(Icons.Default.ChevronRight, contentDescription = "Ver más", tint = Color(0xFF92400E)) }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) { Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(Color(0xFFF5F5F4)).padding(8.dp)) { Icon(Icons.Default.WaterDrop, contentDescription = "Tarea", tint = Color(0xFFA8A29E)) }; Spacer(modifier = Modifier.width(12.dp)); Column(modifier = Modifier.weight(1f)) { Text("Aplicar fungicida", fontWeight = FontWeight.Bold, color = Stone800); Text("Lote B - Soja. 50 L de mezcla lista.", fontSize = 14.sp, color = Color(0xFF78716C)); Text("Mañana, 08:00 AM", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Emerald600, modifier = Modifier.padding(top = 8.dp)) }; Icon(Icons.Default.CheckCircle, contentDescription = "Completar", tint = Color(0xFFA8A29E), modifier = Modifier.size(28.dp)) }
        }
    }
}

@Composable
fun DonElioBottomNav(pantallaActual: Destino, onNavigate: (Destino) -> Unit) {
    NavigationBar(containerColor = Stone50, tonalElevation = 8.dp) {
        NavigationBarItem(
            selected = pantallaActual == Destino.Home,
            onClick = { onNavigate(Destino.Home) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF064E3B), selectedTextColor = Color(0xFF064E3B), indicatorColor = Color(0xFFD1FAE5))
        )
        NavigationBarItem(
            selected = pantallaActual == Destino.Tareas,
            onClick = { onNavigate(Destino.Tareas) },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Tareas") },
            label = { Text("Tareas") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF064E3B), selectedTextColor = Color(0xFF064E3B), indicatorColor = Color(0xFFD1FAE5))
        )
        NavigationBarItem(
            selected = pantallaActual == Destino.Insumos,
            onClick = { onNavigate(Destino.Insumos) },
            icon = { Icon(Icons.Default.Inventory, contentDescription = "Insumos") },
            label = { Text("Insumos") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF064E3B), selectedTextColor = Color(0xFF064E3B), indicatorColor = Color(0xFFD1FAE5))
        )
        NavigationBarItem(
            selected = pantallaActual == Destino.Reportes,
            onClick = { onNavigate(Destino.Reportes) },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Reportes") },
            label = { Text("Reportes") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF064E3B), selectedTextColor = Color(0xFF064E3B), indicatorColor = Color(0xFFD1FAE5))
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
fun DonElioAppPreview() {
    MaterialTheme {
        DonElioApp()
    }
}