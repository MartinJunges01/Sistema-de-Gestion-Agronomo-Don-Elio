// DonElioHomeScreen.kt
package com.itec.donelio.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Stone50 = Color(0xFFFAFAF9)
val Stone800 = Color(0xFF292524)
val Emerald800 = Color(0xFF065F46)
val Emerald600 = Color(0xFF059669)

// 1. Ampliamos los destinos para cubrir todos los Casos de Uso
enum class Destino {
    Home, Campanias, DetalleCampania, FormularioCampania,
    Insumos, FormularioInsumo,
    Reportes,
    NuevaTarea, Observaciones, ConfiguracionDB
}

@Composable
fun DonElioApp() {
    var pantallaActual by remember { mutableStateOf(Destino.Home) }

    Scaffold(
        containerColor = Stone50,
        floatingActionButton = {
            // Mostramos el FAB principal solo en Home, Campañas e Insumos
            if (pantallaActual in listOf(Destino.Home, Destino.Campanias, Destino.Insumos)) {
                FloatingActionButton(
                    onClick = {
                        pantallaActual = when(pantallaActual) {
                            Destino.Campanias -> Destino.FormularioCampania
                            Destino.Insumos -> Destino.FormularioInsumo
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
            // Ocultamos el BottomNav en pantallas de detalle o formularios
            if (pantallaActual in listOf(Destino.Home, Destino.Campanias, Destino.Insumos, Destino.Reportes)) {
                DonElioBottomNav(pantallaActual) { nuevoDestino -> pantallaActual = nuevoDestino }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (pantallaActual) {
                Destino.Home -> DonElioHomeScreen(onGoToConfig = { pantallaActual = Destino.ConfiguracionDB })
                Destino.Campanias -> CampaniasScreen(onGoToDetail = { pantallaActual = Destino.DetalleCampania })
                Destino.DetalleCampania -> DetalleCampaniaScreen(
                    onBack = { pantallaActual = Destino.Campanias },
                    onGoToObs = { pantallaActual = Destino.Observaciones },
                    onGoToTask = { pantallaActual = Destino.NuevaTarea }
                )
                Destino.FormularioCampania -> FormularioCampaniaScreen { pantallaActual = Destino.Campanias }
                Destino.Insumos -> InsumosScreen()
                Destino.FormularioInsumo -> FormularioInsumoScreen { pantallaActual = Destino.Insumos }
                Destino.Reportes -> ReportesScreen()
                Destino.NuevaTarea -> NuevaTareaScreen { pantallaActual = Destino.Home }
                Destino.Observaciones -> ObservacionesScreen { pantallaActual = Destino.DetalleCampania }
                Destino.ConfiguracionDB -> ConfiguracionDBScreen { pantallaActual = Destino.Home }
            }
        }
    }
}

// --- PANTALLAS PRINCIPALES ---

@Composable
fun DonElioHomeScreen(onGoToConfig: () -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { HeaderSection(onGoToConfig) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { MetricsSection() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { TasksSection() }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaniasScreen(onGoToDetail: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Campañas (CU1-CU4)", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50))
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE7E5E4)),
                    modifier = Modifier.fillMaxWidth().clickable { onGoToDetail() }
                ) {
                    ListItem(
                        headlineContent = { Text("Campaña Soja 2026", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("Lotes: Norte, Sur | Inicio: 10/04/26") },
                        leadingContent = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Emerald600) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth().clickable { onGoToDetail() }) {
                    ListItem(
                        headlineContent = { Text("Campaña Trigo Invierno", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("Lote: Este | Estado: En preparación") },
                        leadingContent = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Emerald600) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsumosScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Insumos (CU9)", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50))
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text("Urea Granulada", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("Fertilizante | $15,000/Tn") },
                        trailingContent = { Text("1500 kg", fontWeight = FontWeight.Bold, color = Emerald600) },
                        leadingContent = { Icon(Icons.Default.List, contentDescription = null, tint = Stone800) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Reportes (CU10)", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.White, RoundedCornerShape(16.dp)).padding(16.dp), contentAlignment = Alignment.Center) {
                Text("[Área para Gráfico de Rendimiento]", color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { /* CU11 */ }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Emerald600)) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Exportar Reporte a PDF (CU11)")
            }
        }
    }
}

// --- PANTALLAS DE DETALLE Y FORMULARIOS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCampaniaScreen(onBack: () -> Unit, onGoToObs: () -> Unit, onGoToTask: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Campaña Soja 2026", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Emerald800, titleContentColor = Color.White, navigationIconContentColor = Color.White)
        )
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
                    ListItem(
                        headlineContent = { Text("Cosecha Lote Norte") },
                        supportingContent = { Text("Fecha: 05/05/26 | Silobolsa 1") },
                        trailingContent = { Text("200 Tn", fontWeight = FontWeight.Bold, color = Emerald600) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObservacionesScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Observaciones (CU8)", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50)
        )
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Escribe una nota...") }, modifier = Modifier.fillMaxWidth().height(150.dp), maxLines = 5)
            Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Stone800)) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Adjuntar Fotografía")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionDBScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Base de Datos (CU12/CU13)", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50)
        )
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Stone800)) {
                Icon(Icons.Default.Upload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Exportar Base de Datos (Respaldo)")
            }
            Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Emerald600)) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Importar Base de Datos")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioCampaniaScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Crear Campaña (CU1)", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50)
        )
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Nombre de la Campaña") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Cultivo (Ej: Soja)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Fecha de Inicio") }, modifier = Modifier.fillMaxWidth(), trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) })
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Emerald600), shape = RoundedCornerShape(12.dp)) {
                Text("Guardar Campaña", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioInsumoScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Registrar Insumo (CU9.5)", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50)
        )
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Nombre del Insumo") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Categoría (Ej: Semilla, Fertilizante)") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Cantidad") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Unidad (Kg, Lts)") }, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Emerald600), shape = RoundedCornerShape(12.dp)) {
                Text("Guardar Insumo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaTareaScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Nueva Tarea (CU5.1)", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50)
        )
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Nombre de la Tarea") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Fecha") }, modifier = Modifier.weight(1f), trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) })
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Hora") }, modifier = Modifier.weight(1f))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = true, onCheckedChange = {})
                Text("Activar Notificación de Recordatorio")
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Emerald600), shape = RoundedCornerShape(12.dp)) {
                Text("Guardar Tarea", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- COMPONENTES COMPARTIDOS (Home) ---

@Composable
fun HeaderSection(onGoToConfig: () -> Unit) {
    Surface(color = Emerald800, shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp), shadowElevation = 4.dp) {
        Row(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column {
                Text(text = "Martes, 28 de Abril", color = Color(0xFFA7F3D0), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(text = "Hola, Don Elio", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            // Agregamos el clickable aquí para ir a DB Config
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
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(iconBgColor).padding(4.dp)) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF78716C))
            }
            Column {
                Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1917))
                Text(subtitle, fontSize = 12.sp, color = Color(0xFF78716C))
            }
        }
    }
}

@Composable
fun TasksSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = "Tareas Próximas y Alertas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Stone800, modifier = Modifier.padding(bottom = 16.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)), shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, topStart = 4.dp, bottomStart = 4.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.Warning, contentDescription = "Alerta", tint = Color(0xFFD97706))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Riego pendiente", fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                    Text("Lote 4 (Maíz) necesita riego antes del mediodía.", fontSize = 14.sp, color = Color(0xFF92400E))
                }
                Icon(Icons.Default.ChevronRight, contentDescription = "Ver más", tint = Color(0xFF92400E))
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(Color(0xFFF5F5F4)).padding(8.dp)) {
                    Icon(Icons.Default.WaterDrop, contentDescription = "Tarea", tint = Color(0xFFA8A29E))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Aplicar fungicida", fontWeight = FontWeight.Bold, color = Stone800)
                    Text("Lote B - Soja. 50 L de mezcla lista.", fontSize = 14.sp, color = Color(0xFF78716C))
                    Text("Mañana, 08:00 AM", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Emerald600, modifier = Modifier.padding(top = 8.dp))
                }
                Icon(Icons.Default.CheckCircle, contentDescription = "Completar", tint = Color(0xFFA8A29E), modifier = Modifier.size(28.dp))
            }
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
            selected = pantallaActual == Destino.Campanias,
            onClick = { onNavigate(Destino.Campanias) },
            icon = { Icon(Icons.Default.Eco, contentDescription = "Campañas") },
            label = { Text("Campañas") },
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