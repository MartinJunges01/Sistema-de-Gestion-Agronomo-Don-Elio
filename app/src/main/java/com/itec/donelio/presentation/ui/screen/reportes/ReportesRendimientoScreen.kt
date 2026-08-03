package com.itec.donelio.presentation.ui.screen.reportes

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import com.itec.donelio.domain.model.Campania
import com.itec.donelio.domain.model.CampaniaInsumo
import com.itec.donelio.presentation.ui.components.CardMetricaComparativa
import com.itec.donelio.presentation.ui.theme.AgriAzul
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal
import com.itec.donelio.presentation.ui.theme.TextoSecundario
import com.itec.donelio.presentation.viewmodel.reportes.ReportesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesRendimientoScreen(
    onBack: () -> Unit,
    viewModel: ReportesViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Estados del ViewModel
    val campanias by viewModel.campanias.collectAsState()
    val campaniaIndividual by viewModel.campaniaIndividual.collectAsState()
    val insumosIndividual by viewModel.insumosIndividual.collectAsState()
    val cosechasIndividual by viewModel.cosechasIndividual.collectAsState()
    val pieChartData by viewModel.pieChartData.collectAsState()
    val campaniaA by viewModel.campaniaA.collectAsState()
    val campaniaB by viewModel.campaniaB.collectAsState()
    val insumosA by viewModel.insumosA.collectAsState()
    val insumosB by viewModel.insumosB.collectAsState()
    val exportStatus by viewModel.exportStatus.collectAsState()

    LaunchedEffect(exportStatus) {
        exportStatus?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearExportStatus()
        }
    }

    val csvLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        uri?.let { viewModel.exportarReporteCsv(it, context) }
    }
    val pdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        uri?.let { viewModel.exportarReportePdf(it, context) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        var mostrarMenuExportar by remember { mutableStateOf(false) }

        TopAppBar(
            title = { Text("Reportes y Análisis", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                Box {
                    IconButton(onClick = { mostrarMenuExportar = true }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Exportar", tint = AgriVerde)
                    }
                    DropdownMenu(expanded = mostrarMenuExportar, onDismissRequest = { mostrarMenuExportar = false }) {
                        DropdownMenuItem(
                            text = { Text("Exportar a Excel") },
                            onClick = {
                                mostrarMenuExportar = false
                                if (campaniaIndividual == null) {
                                    Toast.makeText(context, "Seleccione una campaña para exportar", Toast.LENGTH_SHORT).show()
                                } else {
                                    csvLauncher.launch("Reporte_Insumos_${campaniaIndividual!!.nombre}.csv")
                                }
                            },
                            leadingIcon = { Icon(Icons.Default.TableChart, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Exportar a PDF") },
                            onClick = {
                                mostrarMenuExportar = false
                                if (campaniaIndividual == null) {
                                    Toast.makeText(context, "Seleccione una campaña para exportar", Toast.LENGTH_SHORT).show()
                                } else {
                                    pdfLauncher.launch("Reporte_Insumos_${campaniaIndividual!!.nombre}.pdf")
                                }
                            },
                            leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null) }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ──────────────────────────────────────────────
            // SECCIÓN 1: Estadísticas de campaña individual
            // ──────────────────────────────────────────────

            item {
                Text(
                    "Estadísticas de Campaña",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextoPrincipal
                )
                Spacer(modifier = Modifier.height(8.dp))

                DropdownCampania(
                    label = "Seleccionar campaña",
                    campanias = campanias,
                    seleccionada = campaniaIndividual,
                    onSeleccionar = { viewModel.seleccionarCampaniaIndividual(it) }
                )
            }

            if (campaniaIndividual == null) {
                item {
                    PlaceholderSeleccion(mensaje = "Seleccioná una campaña para ver sus estadísticas")
                }
            } else {
                item {
                    val costoTotalInsumos = insumosIndividual.sumOf { it.cantidad * it.precio }
                    val totalCosechado = cosechasIndividual.sumOf { it.cantidad }
                    val unidadCosecha = "Kg/L"

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TarjetaMetrica(
                            titulo = "Costo de Insumos",
                            valor = "$ %.2f".format(costoTotalInsumos),
                            icono = Icons.Default.AttachMoney,
                            color = AgriVerde,
                            modifier = Modifier.weight(1f)
                        )
                        TarjetaMetrica(
                            titulo = "Total Cosechado",
                            valor = if (totalCosechado > 0) "%.1f %s".format(totalCosechado, unidadCosecha) else "Sin registros",
                            icono = Icons.Default.Grain,
                            color = AgriAzul,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // PieChart contextual a la campaña seleccionada
                item {
                    Text(
                        "Distribución de Gastos por Insumo",
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE7E5E4)),
                        modifier = Modifier.fillMaxWidth().height(300.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (pieChartData != null) {
                                PieChart(
                                    modifier = Modifier.fillMaxSize().padding(24.dp),
                                    pieChartData = pieChartData!!,
                                    pieChartConfig = PieChartConfig(
                                        isAnimationEnable = true,
                                        showSliceLabels = true,
                                        sliceLabelTextColor = Color.White,
                                        activeSliceAlpha = 0.9f,
                                        isEllipsizeEnabled = true,
                                        sliceLabelTextSize = 12.sp,
                                        labelVisible = true
                                    )
                                )
                            } else {
                                PlaceholderSeleccion(mensaje = "Sin insumos registrados en esta campaña")
                            }
                        }
                    }
                }
            }

            // Divisor entre secciones
            item {
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFE7E5E4))
            }

            // ──────────────────────────────────────────────
            // SECCIÓN 2: Comparador de campañas
            // Nota: paso preparatorio del Issue #302
            // ──────────────────────────────────────────────

            item {
                Text(
                    "Comparar Campañas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextoPrincipal
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DropdownCampania(
                        label = "Campaña A",
                        campanias = campanias,
                        seleccionada = campaniaA,
                        onSeleccionar = { viewModel.seleccionarCampaniaA(it) },
                        modifier = Modifier.weight(1f)
                    )
                    DropdownCampania(
                        label = "Campaña B",
                        campanias = campanias,
                        seleccionada = campaniaB,
                        onSeleccionar = { viewModel.seleccionarCampaniaB(it) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (campaniaA == null || campaniaB == null) {
                item {
                    PlaceholderSeleccion(mensaje = "Seleccioná dos campañas para comparar sus métricas")
                }
            } else {
                item {
                    val costoA = insumosA.sumOf { it.cantidad * it.precio }
                    val costoB = insumosB.sumOf { it.cantidad * it.precio }
                    val nombreA = campaniaA?.nombre ?: "Campaña A"
                    val nombreB = campaniaB?.nombre ?: "Campaña B"

                    Text("Métricas Comparativas", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextoPrincipal)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        CardMetricaComparativa(
                            titulo = "Costo Insumos",
                            valor1 = "$ %.2f".format(costoA),
                            valor2 = "$ %.2f".format(costoB),
                            color = AgriVerde,
                            modifier = Modifier.weight(1f)
                        )
                        CardMetricaComparativa(
                            titulo = "Rendimiento",
                            valor1 = "— ($nombreA)",
                            valor2 = "— ($nombreB)",
                            color = AgriAzul,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Rendimiento disponible con Issue #298 (Hectáreas/Tn).",
                        fontSize = 11.sp,
                        color = TextoSecundario
                    )
                }

                item {
                    GraficoEvolucionPlaceholder()
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// Componentes privados
// ──────────────────────────────────────────────

/**
 * Dropdown genérico para seleccionar una campaña de una lista real de la BD.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownCampania(
    label: String,
    campanias: List<Campania>,
    seleccionada: Campania?,
    onSeleccionar: (Campania) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = seleccionada?.nombre ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text("Seleccionar...", color = TextoSecundario) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            textStyle = TextStyle(fontSize = 14.sp)
        )
        ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            if (campanias.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No hay campañas registradas", color = TextoSecundario) },
                    onClick = { expandido = false }
                )
            } else {
                campanias.forEach { campania ->
                    DropdownMenuItem(
                        text = { Text(campania.nombre) },
                        onClick = {
                            onSeleccionar(campania)
                            expandido = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta de métrica individual para la Sección 1.
 */
@Composable
private fun TarjetaMetrica(
    titulo: String,
    valor: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE7E5E4)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(titulo, fontSize = 12.sp, color = TextoSecundario, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(valor, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
        }
    }
}

/**
 * Placeholder para las secciones que requieren selección previa de campaña.
 */
@Composable
private fun PlaceholderSeleccion(mensaje: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE7E5E4)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = TextoSecundario,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(mensaje, fontSize = 13.sp, color = TextoSecundario)
        }
    }
}

/**
 * Placeholder para el gráfico de evolución mensual.
 * Scope completo disponible en Issue #302.
 */
@Composable
private fun GraficoEvolucionPlaceholder() {
    Text("Evolución Mensual de Costos", fontWeight = FontWeight.Bold, color = TextoPrincipal)
    Spacer(modifier = Modifier.height(8.dp))
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE7E5E4)),
        modifier = Modifier.fillMaxWidth().height(180.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.BarChart, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(40.dp))
                Text("Gráfico disponible próximamente", fontWeight = FontWeight.Medium, color = TextoPrincipal)
                Text("Seleccioná ambas campañas para activarlo", fontSize = 12.sp, color = TextoSecundario)
            }
        }
    }
}
