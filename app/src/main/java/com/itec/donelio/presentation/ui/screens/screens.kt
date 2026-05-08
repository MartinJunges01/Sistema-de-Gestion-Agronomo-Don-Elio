// DonElioHomeScreen.kt
package com.itec.donelio.presentation.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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

// --- SISTEMA DE DISEÑO: AGRICORE ---
val AgriVerde = Color(0xFF2D6A4F) // Verde principal orgánico
val AgriAzul = Color(0xFF1E6091)  // Azul tecnológico/agua
val AgriFondo = Color(0xFFF8F9FA) // Blanco roto para contraste al sol
val TextoPrincipal = Color(0xFF212529)
val TextoSecundario = Color(0xFF6C757D)

enum class Destino {
    Login, Registro,
    Home, Campanias, DetalleCampania, FormularioCampania,
    Insumos, FormularioInsumo, CatalogoInsumos,
    Reportes,
    NuevaTarea, Observaciones, ConfiguracionDB,
    Tareas, Cosechas, FormularioCosecha
}

@Composable
fun DonElioApp() {
    var historial by remember { mutableStateOf(listOf(Destino.Login)) }
    val pantallaActual = historial.last()

    val navegar: (Destino) -> Unit = { destino ->
        if (pantallaActual != destino) {
            historial = historial + destino
        }
    }

    val volverAtras: () -> Unit = {
        if (historial.size > 1) {
            historial = historial.dropLast(1)
        }
    }

    BackHandler(enabled = historial.size > 1) {
        volverAtras()
    }

    Scaffold(
        containerColor = AgriFondo,
        floatingActionButton = {
            if (pantallaActual in listOf(Destino.Home, Destino.Campanias, Destino.CatalogoInsumos, Destino.Cosechas)) {
                FloatingActionButton(
                    onClick = {
                        val destinoNuevo = when(pantallaActual) {
                            Destino.Campanias -> Destino.FormularioCampania
                            Destino.CatalogoInsumos -> Destino.FormularioInsumo
                            Destino.Cosechas -> Destino.FormularioCosecha
                            else -> Destino.NuevaTarea
                        }
                        navegar(destinoNuevo)
                    },
                    containerColor = AgriVerde,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }
            }
        },
        bottomBar = {
            if (pantallaActual != Destino.Login && pantallaActual != Destino.Registro) {
                AgriCoreBottomNav(pantallaActual) { nuevoDestino -> navegar(nuevoDestino) }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (pantallaActual) {
                Destino.Login -> LoginScreen(onLoginSuccess = { navegar(Destino.Home) }, onGoToRegister = { navegar(Destino.Registro) })
                Destino.Registro -> RegistroScreen(onRegisterSuccess = { navegar(Destino.Home) }, onGoToLogin = { navegar(Destino.Login) })

                Destino.Home -> DashboardOperacionesScreen(
                    onGoToConfig = { navegar(Destino.ConfiguracionDB) },
                    onGoToCampanias = { navegar(Destino.Campanias) }
                )

                Destino.Tareas -> TareasScreen(
                    onGoToNuevaTarea = { navegar(Destino.NuevaTarea) },
                    onGoToCampanias = { navegar(Destino.DetalleCampania) },
                    onBack = volverAtras
                )

                Destino.Campanias -> GestionParcelasScreen(
                    onGoToDetail = { navegar(Destino.DetalleCampania) },
                    onBack = volverAtras
                )
                Destino.DetalleCampania -> DetalleCampaniaScreen(
                    onBack = volverAtras,
                    onGoToObs = { navegar(Destino.Observaciones) },
                    onGoToTask = { navegar(Destino.Tareas) },
                    onGoToCosechas = { navegar(Destino.Cosechas) },
                    onGoToInsumos = { navegar(Destino.Insumos) }
                )
                Destino.FormularioCampania -> FormularioCampaniaScreen(onBack = volverAtras)

                Destino.Cosechas -> CosechasScreen(onBack = volverAtras)
                Destino.FormularioCosecha -> FormularioCosechaScreen(onBack = volverAtras)

                Destino.Insumos -> InsumosScreen(
                    onGoToCatalogo = { navegar(Destino.CatalogoInsumos) },
                    onGoToCampanias = { navegar(Destino.DetalleCampania) },
                    onBack = volverAtras
                )
                Destino.CatalogoInsumos -> CatalogoInsumosScreen(
                    onBack = volverAtras,
                    onGoToFormulario = { navegar(Destino.FormularioInsumo) }
                )
                Destino.FormularioInsumo -> FormularioInsumoScreen(onBack = volverAtras)

                Destino.Reportes -> ReportesRendimientoScreen(onBack = volverAtras)

                Destino.NuevaTarea -> NuevaTareaScreen(onBack = volverAtras)
                Destino.Observaciones -> ObservacionesScreen(onBack = volverAtras)
                Destino.ConfiguracionDB -> ConfiguracionDBScreen(onBack = volverAtras)
            }
        }
    }
}

// --- 1. DASHBOARD DE OPERACIONES (HOME FUSIONADO) ---
@Composable
fun DashboardOperacionesScreen(onGoToConfig: () -> Unit, onGoToCampanias: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp), // Espacio para el BottomNav
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { HeaderSectionAgriCore(onGoToConfig) }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Dashboard", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AgriVerde)
                Text("Resumen de operaciones en tiempo real", color = TextoSecundario, fontSize = 14.sp)
            }
        }

        // Widget de Clima y Salud
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Card(
                    modifier = Modifier.weight(1f).height(100.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color(0xFFE9C46A))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Clima", fontWeight = FontWeight.Bold, color = TextoPrincipal)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text("24°C | Humedad 60%", fontSize = 14.sp, color = TextoSecundario)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f).height(100.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Eco, contentDescription = null, tint = AgriVerde)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Salud Lotes", fontWeight = FontWeight.Bold, color = TextoPrincipal)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text("90% Óptimo", fontSize = 14.sp, color = AgriVerde, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Campaña Activa", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal, modifier = Modifier.padding(bottom = 12.dp))
                CampanaSeleccionadaCard(onClick = onGoToCampanias)
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Tareas Pendientes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
                Spacer(modifier = Modifier.height(12.dp))
                TarjetaTarea(titulo = "Riego Lote Sur", descripcion = "Requiere riego antes de las 14:00", icono = Icons.Default.WaterDrop, colorIcono = AgriAzul)
                Spacer(modifier = Modifier.height(12.dp))
                TarjetaTarea(titulo = "Revisión Maquinaria", descripcion = "Tractor 2: Cambio de aceite", icono = Icons.Default.Build, colorIcono = TextoSecundario)
            }
        }
    }
}

// --- 2. GESTIÓN DE PARCELAS (CAMPAÑAS FUSIONADO) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionParcelasScreen(onGoToDetail: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Gestión de Parcelas", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Supervisión de lotes activos", color = TextoSecundario)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(3) { index ->
                val progresos = listOf(0.8f, 0.4f, 0.95f)
                val cultivos = listOf("Soja (Lote Norte)", "Maíz (Lote Sur)", "Trigo (Lote Este)")
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onGoToDetail() },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(cultivos[index], fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextoPrincipal)
                            Icon(Icons.Default.WaterDrop, contentDescription = "Riego", tint = AgriAzul)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Madurez del cultivo", fontSize = 14.sp, color = TextoSecundario)
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progresos[index] },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = AgriVerde,
                            trackColor = AgriFondo,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Predicción de cosecha: ${(progresos[index] * 100).toInt()}% listo", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AgriVerde)
                    }
                }
            }
        }
    }
}

// --- 3. CALENDARIO Y TAREAS FUSIONADO ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasScreen(onGoToNuevaTarea: () -> Unit, onGoToCampanias: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Agenda y Tareas", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
        )
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Calendario Selector
            item {
                Text("Planificación Estratégica", color = TextoSecundario)
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(7) { dia ->
                        val seleccionado = dia == 2
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (seleccionado) AgriVerde else Color.White)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("MAY", fontSize = 12.sp, color = if (seleccionado) Color.White else TextoSecundario)
                                Text("${10 + dia}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (seleccionado) Color.White else TextoPrincipal)
                            }
                        }
                    }
                }
            }

            item { Divider(color = Color(0xFFE7E5E4), modifier = Modifier.padding(vertical = 8.dp)) }

            item {
                Text("Pendientes de Hoy", fontWeight = FontWeight.Bold, color = TextoPrincipal, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                var completada1 by remember { mutableStateOf(false) }
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth().clickable { onGoToNuevaTarea() }) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = completada1, onCheckedChange = { completada1 = it }, colors = CheckboxDefaults.colors(checkedColor = AgriVerde))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Riego Lote Sur", fontWeight = FontWeight.Bold, color = TextoPrincipal)
                            Text("Requiere riego antes de las 14:00", fontSize = 14.sp, color = TextoSecundario)
                        }
                    }
                }
            }

            item {
                Text("En Progreso", fontWeight = FontWeight.Bold, color = TextoPrincipal, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
                Spacer(modifier = Modifier.height(8.dp))
                var completada2 by remember { mutableStateOf(false) }
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth().clickable { onGoToNuevaTarea() }) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = completada2, onCheckedChange = { completada2 = it }, colors = CheckboxDefaults.colors(checkedColor = AgriVerde))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Siembra Lote Este", fontWeight = FontWeight.Bold, color = TextoPrincipal)
                            Text("Aplicación de semillas certificadas", fontSize = 14.sp, color = TextoSecundario)
                        }
                    }
                }
            }

            item {
                Button(onClick = onGoToNuevaTarea, modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 16.dp), colors = ButtonDefaults.buttonColors(containerColor = AgriVerde)) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Programar Nueva Tarea")
                }
            }
        }
    }
}

// --- 4. REPORTES DE RENDIMIENTO FUSIONADO ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesRendimientoScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Reportes y Análisis", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
        )
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = AgriVerde)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Producción Total", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                            Text("850 Tn", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = AgriAzul)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Rentabilidad", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                            Text("+12.4%", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Text("Gráfico de Cosecha (Estacional)", fontWeight = FontWeight.Bold, color = TextoPrincipal, modifier = Modifier.padding(bottom = 8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val barWidth = size.width / 7f
                            val maxVal = 100f
                            val values = listOf(40f, 60f, 30f, 90f)
                            values.forEachIndexed { index, value ->
                                val barHeight = (value / maxVal) * size.height
                                drawRoundRect(
                                    color = AgriVerde,
                                    topLeft = Offset((index * 2) * barWidth, size.height - barHeight),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(8f, 8f)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text("Evolución de Costos", fontWeight = FontWeight.Bold, color = TextoPrincipal, modifier = Modifier.padding(bottom = 8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
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
                                drawLine(color = AgriAzul, start = Offset(startX, startY), end = Offset(endX, endY), strokeWidth = 8f, cap = StrokeCap.Round)
                                drawCircle(color = AgriVerde, radius = 12f, center = Offset(startX, startY))
                                if (i == values.size - 2) drawCircle(color = AgriVerde, radius = 12f, center = Offset(endX, endY))
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- PANTALLAS DE ACCESO ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onGoToRegister: () -> Unit) {
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Surface(shape = RoundedCornerShape(24.dp), color = AgriVerde.copy(alpha = 0.1f), modifier = Modifier.size(100.dp)) { Icon(Icons.Default.Agriculture, contentDescription = null, modifier = Modifier.padding(20.dp), tint = AgriVerde) }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Don Elio", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = AgriVerde)
        Text("Sistema de Gestión Agrícola", color = TextoSecundario, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(48.dp))
        OutlinedTextField(value = usuario, onValueChange = { usuario = it }, label = { Text("Nombre de usuario") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = PasswordVisualTransformation())
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onLoginSuccess, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = AgriVerde), shape = RoundedCornerShape(12.dp)) { Text("Ingresar", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onGoToRegister) { Text("¿No tienes cuenta? Regístrate aquí", color = AgriVerde, fontWeight = FontWeight.SemiBold) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(onRegisterSuccess: () -> Unit, onGoToLogin: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Crear Cuenta", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = AgriVerde)
        Text("Configura tu acceso local", color = TextoSecundario, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(40.dp))
        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = usuario, onValueChange = { usuario = it }, label = { Text("Nombre de usuario") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = PasswordVisualTransformation())
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onRegisterSuccess, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = AgriVerde), shape = RoundedCornerShape(12.dp)) { Text("Registrarse", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onGoToLogin) { Text("¿Ya tienes cuenta? Inicia sesión", color = AgriVerde, fontWeight = FontWeight.SemiBold) }
    }
}

// --- INSUMOS (VINCULACIÓN) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsumosScreen(onGoToCatalogo: () -> Unit, onGoToCampanias: () -> Unit, onBack: () -> Unit) {
    var menuExpandido by remember { mutableStateOf(false) }
    var insumoSeleccionado by remember { mutableStateOf("") }
    val insumosDisponibles = listOf("Herbicida Atrazina", "Fertilizante DAP", "Insecticida Lambda", "Semilla Trigo 50kg")

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Vincular Insumos (CU9)", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } },
            actions = {
                TextButton(onClick = onGoToCatalogo) {
                    Icon(Icons.Default.Settings, contentDescription = "Catálogo", modifier = Modifier.size(20.dp), tint = AgriVerde)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Catálogo", color = AgriVerde)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
        )
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text("Campaña Activa", fontWeight = FontWeight.Bold, color = TextoPrincipal, modifier = Modifier.padding(vertical = 8.dp))
                CampanaSeleccionadaCard(onClick = onGoToCampanias)
            }

            item {
                Text("Agregar Nuevo Insumo a la Campaña", fontWeight = FontWeight.Bold, color = TextoPrincipal, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))

                ExposedDropdownMenuBox(
                    expanded = menuExpandido,
                    onExpandedChange = { menuExpandido = it }
                ) {
                    OutlinedTextField(
                        value = if (insumoSeleccionado.isEmpty()) "Seleccionar un insumo..." else insumoSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpandido) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = menuExpandido,
                        onDismissRequest = { menuExpandido = false }
                    ) {
                        insumosDisponibles.forEach { insumo ->
                            DropdownMenuItem(
                                text = { Text(insumo) },
                                onClick = {
                                    insumoSeleccionado = insumo
                                    menuExpandido = false
                                }
                            )
                        }
                    }
                }

                if (insumoSeleccionado.isNotEmpty()) {
                    Button(
                        onClick = { insumoSeleccionado = "" },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AgriVerde)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Vincular Insumo")
                    }
                }
            }

            item {
                Text("Insumos Ya Vinculados", fontWeight = FontWeight.Bold, color = TextoPrincipal, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
            }
            items(listOf("Urea Granulada", "Fungicida Glifosato", "Semilla Soja 50kg")) { insumo ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Vinculado", tint = AgriVerde)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(insumo, fontWeight = FontWeight.Bold, color = TextoPrincipal)
                            Text("Vinculado a campaña activa", fontSize = 12.sp, color = TextoSecundario)
                        }
                        IconButton(onClick = { /* Lógica para eliminar */ }) {
                            Icon(Icons.Default.Delete, contentDescription = "Desvincular", tint = Color(0xFFDC2626))
                        }
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
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
        )
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(listOf(
                Triple("Urea Granulada", "Fertilizante", "1500 kg"),
                Triple("Fungicida Glifosato", "Herbicida", "200 Lts"),
                Triple("Semilla Soja 50kg", "Semilla", "80 bolsas"),
                Triple("Herbicida Atrazina", "Herbicida", "300 Lts")
            )) { (nombre, categoria, stock) ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text(nombre, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("$categoria | Stock: $stock") },
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
                Button(onClick = onGoToFormulario, modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 16.dp), colors = ButtonDefaults.buttonColors(containerColor = AgriVerde), shape = RoundedCornerShape(12.dp)) {
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
fun DetalleCampaniaScreen(onBack: () -> Unit, onGoToObs: () -> Unit, onGoToTask: () -> Unit, onGoToCosechas: () -> Unit, onGoToInsumos: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Campaña Soja 2026", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriVerde, titleContentColor = Color.White, navigationIconContentColor = Color.White),
            actions = {
                IconButton(onClick = { }) { Icon(Icons.Default.Delete, contentDescription = "Eliminar Campaña", tint = Color.White) }
            }
        )
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Text("Gestión de la Campaña", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextoPrincipal, modifier = Modifier.padding(bottom = 8.dp))
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
                Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = TextoPrincipal)) {
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
            Icon(icon, contentDescription = title, tint = AgriVerde, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, color = TextoPrincipal)
        }
    }
}

// --- PANTALLAS DE COSECHAS ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CosechasScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Gestión de Cosechas", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo))
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text("Cosechas Almacenadas", fontWeight = FontWeight.Bold, color = TextoPrincipal) }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, AgriVerde), modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text("Lote Norte - Soja", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("Silobolsa 1 | 05/05/2026") },
                        trailingContent = { Text("200 Tn", fontWeight = FontWeight.Bold, color = AgriVerde) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { Text("Cosechas No Almacenadas (Venta/Reserva)", fontWeight = FontWeight.Bold, color = TextoPrincipal) }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFD97706)), modifier = Modifier.fillMaxWidth()) {
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
    var cultivo by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var almacen by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Registrar Cosecha", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = cultivo, onValueChange = { cultivo = it }, label = { Text("Cultivo") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = cantidad, onValueChange = { cantidad = it }, label = { Text("Cantidad") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = unidad, onValueChange = { unidad = it }, label = { Text("Unidad (Ej. Tn)") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Fecha") }, modifier = Modifier.fillMaxWidth(), trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) })

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Checkbox(checked = almacenado, onCheckedChange = { almacenado = it }, colors = CheckboxDefaults.colors(checkedColor = AgriVerde))
                Text("Almacenar en el establecimiento", fontWeight = FontWeight.Medium)
            }

            if (almacenado) {
                OutlinedTextField(value = almacen, onValueChange = { almacen = it }, label = { Text("Almacén (Silo, Silobolsa)") }, modifier = Modifier.fillMaxWidth())
            } else {
                OutlinedTextField(value = tipo, onValueChange = { tipo = it }, label = { Text("Tipo (Venta, Alimento Vacuno, Reserva)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio (Opcional)") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) })
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = AgriVerde), shape = RoundedCornerShape(12.dp)) { Text("Guardar Registro", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

// --- OBSERVACIONES ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObservacionesScreen(onBack: () -> Unit) {
    var nota by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Observaciones (CU8)", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = nota, onValueChange = { nota = it }, label = { Text("Escribe una nota...") }, modifier = Modifier.fillMaxWidth().height(150.dp), maxLines = 5)
            Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = TextoPrincipal)) { Icon(Icons.Default.CameraAlt, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Adjuntar Fotografía") }
        }
    }
}

// --- CONFIGURACION DB ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionDBScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Base de Datos (CU12/CU13)", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = TextoPrincipal)) { Icon(Icons.Default.Upload, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Exportar Base de Datos (Respaldo)") }
            Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = AgriVerde)) { Icon(Icons.Default.Download, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Importar Base de Datos") }
        }
    }
}

// --- FORMULARIOS ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioCampaniaScreen(onBack: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var cultivo by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Crear Campaña (CU1)", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre de la Campaña") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = cultivo, onValueChange = { cultivo = it }, label = { Text("Cultivo (Ej: Soja)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Fecha de Inicio") }, modifier = Modifier.fillMaxWidth(), trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) })
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = AgriVerde), shape = RoundedCornerShape(12.dp)) { Text("Guardar Campaña", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioInsumoScreen(onBack: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Registrar Insumo (CU9.5)", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre del Insumo") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría (Ej: Semilla, Fertilizante)") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = cantidad, onValueChange = { cantidad = it }, label = { Text("Cantidad") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = unidad, onValueChange = { unidad = it }, label = { Text("Unidad (Kg, Lts)") }, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = AgriVerde), shape = RoundedCornerShape(12.dp)) { Text("Guardar Insumo", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaTareaScreen(onBack: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var recordatorio by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Nueva Tarea (CU5.1)", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre de la Tarea") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Fecha") }, modifier = Modifier.weight(1f), trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) })
                OutlinedTextField(value = hora, onValueChange = { hora = it }, label = { Text("Hora") }, modifier = Modifier.weight(1f))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = recordatorio, onCheckedChange = { recordatorio = it }, colors = CheckboxDefaults.colors(checkedColor = AgriVerde))
                Text("Activar Notificación de Recordatorio")
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = AgriVerde), shape = RoundedCornerShape(12.dp)) { Text("Guardar Tarea", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

// --- COMPONENTES COMPARTIDOS RE-ESTILIZADOS ---
@Composable
fun HeaderSectionAgriCore(onGoToConfig: () -> Unit) {
    Surface(color = AgriVerde, shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp), shadowElevation = 4.dp) {
        Row(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column {
                Text(text = "Martes, 28 de Abril", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(text = "Hola, Don Elio", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            Surface(color = Color.Black.copy(alpha = 0.2f), shape = RoundedCornerShape(50), modifier = Modifier.clickable { onGoToConfig() }) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Offline", tint = Color.White, modifier = Modifier.size(14.dp))
                    Text("Local / DB", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CampanaSeleccionadaCard(onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().height(110.dp).clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(AgriVerde.copy(alpha = 0.1f)).padding(12.dp), contentAlignment = Alignment.Center) { Icon(imageVector = Icons.Default.Eco, contentDescription = null, tint = AgriVerde, modifier = Modifier.size(28.dp)) }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) { Text("Campaña Soja 2026", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextoPrincipal); Spacer(modifier = Modifier.height(4.dp)); Text("Lote Norte - 450 Tn estimadas", fontSize = 14.sp, color = TextoSecundario) }
            Icon(Icons.Default.ChevronRight, contentDescription = "Ver Detalles", tint = TextoSecundario)
        }
    }
}

@Composable
fun TarjetaTarea(titulo: String, descripcion: String, icono: androidx.compose.ui.graphics.vector.ImageVector, colorIcono: Color) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(colorIcono.copy(alpha = 0.1f)).padding(12.dp)) { Icon(icono, contentDescription = null, tint = colorIcono) }
            Spacer(modifier = Modifier.width(16.dp))
            Column { Text(titulo, fontWeight = FontWeight.Bold, color = TextoPrincipal); Text(descripcion, fontSize = 14.sp, color = TextoSecundario) }
        }
    }
}

@Composable
fun AgriCoreBottomNav(pantallaActual: Destino, onNavigate: (Destino) -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(
            selected = pantallaActual == Destino.Home,
            onClick = { onNavigate(Destino.Home) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AgriVerde, indicatorColor = AgriFondo)
        )
        NavigationBarItem(
            selected = pantallaActual == Destino.Campanias,
            onClick = { onNavigate(Destino.Campanias) },
            icon = { Icon(Icons.Default.Map, contentDescription = "Parcelas") },
            label = { Text("Parcelas") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AgriVerde, indicatorColor = AgriFondo)
        )
        NavigationBarItem(
            selected = pantallaActual == Destino.Tareas,
            onClick = { onNavigate(Destino.Tareas) },
            icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Agenda") },
            label = { Text("Agenda") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AgriVerde, indicatorColor = AgriFondo)
        )
        NavigationBarItem(
            selected = pantallaActual == Destino.Reportes,
            onClick = { onNavigate(Destino.Reportes) },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Reportes") },
            label = { Text("Reportes") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AgriVerde, indicatorColor = AgriFondo)
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
fun AgriCorePreview() {
    MaterialTheme {
        DonElioApp()
    }
}