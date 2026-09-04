package com.itec.donelio.presentation.ui.screen.reportes

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
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
    val desgloseCosechasData by viewModel.desgloseCosechasData.collectAsState()
    val top3Insumos by viewModel.top3Insumos.collectAsState()
    val rendimientoTnHa by viewModel.rendimientoTnHa.collectAsState()
    val campaniaA by viewModel.campaniaA.collectAsState()
    val campaniaB by viewModel.campaniaB.collectAsState()
    val insumosA by viewModel.insumosA.collectAsState()
    val insumosB by viewModel.insumosB.collectAsState()
    val cosechasA by viewModel.cosechasA.collectAsState()
    val cosechasB by viewModel.cosechasB.collectAsState()
    val costoPorHectarea by viewModel.costoPorHectarea.collectAsState()
    val costoHaStringA by viewModel.costoHaStringA.collectAsState()
    val costoHaStringB by viewModel.costoHaStringB.collectAsState()
    val costoHaFloatA by viewModel.costoHaFloatA.collectAsState()
    val costoHaFloatB by viewModel.costoHaFloatB.collectAsState()
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
            // SECCIÓN 0: Filtros Avanzados y Evolución
            // ──────────────────────────────────────────────
            
            item {
                val filtroCampanias by viewModel.filtroCampaniasMulti.collectAsState()
                val filtroRangoFechas by viewModel.filtroRangoFechas.collectAsState()
                val resumenFiltrado by viewModel.resumenFiltrado.collectAsState()

                Text(
                    "Resumen Productivo-Financiero (Filtros Avanzados)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextoPrincipal
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Filtro Campañas (Multi-select)
                Text("Filtrar por Campañas:", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextoPrincipal)
                Spacer(modifier = Modifier.height(8.dp))
                @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
                androidx.compose.foundation.layout.FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    campanias.forEach { campania ->
                        val isSelected = filtroCampanias.contains(campania.id)
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.toggleFiltroCampania(campania.id) },
                            label = { Text(campania.nombre, fontSize = 12.sp) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Resumen Cards
                if (resumenFiltrado != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TarjetaMetrica(
                            titulo = "Capital Invertido",
                            valor = "$ %.2f".format(resumenFiltrado!!.capitalInvertido),
                            icono = Icons.Default.AttachMoney,
                            color = AgriVerde,
                            modifier = Modifier.weight(1f)
                        )
                        TarjetaMetrica(
                            titulo = "Volumen Cosechado",
                            valor = "%.1f Tn".format(resumenFiltrado!!.totalCosechado),
                            icono = Icons.Default.Grain,
                            color = AgriAzul,
                            modifier = Modifier.weight(1f)
                        )
                        TarjetaMetrica(
                            titulo = "Costo/Tn",
                            valor = "$ %.2f".format(resumenFiltrado!!.costoPorTonelada),
                            icono = Icons.Default.MonetizationOn,
                            color = Color(0xFFb91c1c),
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    PlaceholderSeleccion(mensaje = "Seleccioná filtros para ver el resumen")
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                val cultivos by viewModel.cultivos.collectAsState()
                val cultivoSeleccionado by viewModel.cultivoSeleccionado.collectAsState()
                val evolucion by viewModel.evolucionCultivo.collectAsState()

                Text(
                    "Evolución Histórica por Cultivo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextoPrincipal
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                var expandidoCultivos by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandidoCultivos,
                    onExpandedChange = { expandidoCultivos = it }
                ) {
                    OutlinedTextField(
                        value = cultivoSeleccionado?.nombre ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Seleccionar Cultivo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoCultivos) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expandidoCultivos, onDismissRequest = { expandidoCultivos = false }) {
                        cultivos.forEach { cult ->
                            DropdownMenuItem(
                                text = { Text(cult.nombre) },
                                onClick = {
                                    viewModel.seleccionarCultivo(cult)
                                    expandidoCultivos = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (cultivoSeleccionado == null) {
                    PlaceholderSeleccion(mensaje = "Seleccioná un cultivo para ver su evolución")
                } else if (evolucion.isEmpty()) {
                    PlaceholderSeleccion(mensaje = "Sin datos históricos para este cultivo")
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE7E5E4)),
                        modifier = Modifier.fillMaxWidth().height(250.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            // Simple Canvas Line Chart implementation
                            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                val maxRend = evolucion.maxOfOrNull { it.rendimientoTnHa } ?: 1.0
                                val paddingLeft = 60f
                                val paddingTop = 40f
                                val paddingBottom = 120f
                                val paddingRight = 40f
                                val width = size.width - paddingLeft - paddingRight
                                val height = size.height - paddingTop - paddingBottom
                                val stepX = if (evolucion.size > 1) width / (evolucion.size - 1) else width

                                // Text paint configuration
                                val textPaint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.DKGRAY
                                    textSize = 28f
                                    isAntiAlias = true
                                    textAlign = android.graphics.Paint.Align.RIGHT
                                }

                                // Draw Axes
                                drawLine(
                                    color = Color.LightGray,
                                    start = androidx.compose.ui.geometry.Offset(paddingLeft, paddingTop),
                                    end = androidx.compose.ui.geometry.Offset(paddingLeft, size.height - paddingBottom),
                                    strokeWidth = 2f
                                )
                                drawLine(
                                    color = Color.LightGray,
                                    start = androidx.compose.ui.geometry.Offset(paddingLeft, size.height - paddingBottom),
                                    end = androidx.compose.ui.geometry.Offset(size.width - paddingRight, size.height - paddingBottom),
                                    strokeWidth = 2f
                                )

                                // Draw Path and points
                                val path = androidx.compose.ui.graphics.Path()
                                evolucion.forEachIndexed { index, punto ->
                                    val x = paddingLeft + index * stepX
                                    val y = size.height - paddingBottom - ((punto.rendimientoTnHa / maxRend) * height).toFloat()
                                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                    drawCircle(
                                        color = AgriVerde,
                                        radius = 6f,
                                        center = androidx.compose.ui.geometry.Offset(x, y)
                                    )
                                    
                                    // Draw X-axis label (campania nombre)
                                    val campaniaName = punto.campaniaNombre
                                    drawContext.canvas.nativeCanvas.apply {
                                        save()
                                        rotate(-45f, x, size.height - paddingBottom + 30f)
                                        drawText(campaniaName, x, size.height - paddingBottom + 30f, textPaint)
                                        restore()
                                    }
                                }
                                drawPath(
                                    path = path,
                                    color = AgriVerde,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                                )
                            }
                        }
                    }
                }
            }

            item {
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFE7E5E4))
            }

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
                    val unidadCosecha = "Tn"

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
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TarjetaMetrica(
                            titulo = "Rendimiento (Tn/Ha)",
                            valor = rendimientoTnHa,
                            icono = Icons.Default.Agriculture,
                            color = Color(0xFFd97706),
                            modifier = Modifier.weight(1f)
                        )
                        TarjetaMetrica(
                            titulo = "Costo/Ha",
                            valor = costoPorHectarea,
                            icono = Icons.Default.MonetizationOn,
                            color = Color(0xFFb91c1c),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (top3Insumos.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Top 3 Insumos de Mayor Gasto",
                            fontWeight = FontWeight.Bold,
                            color = TextoPrincipal
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE7E5E4)),
                            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                val formatMoneda = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("es", "AR"))
                                top3Insumos.forEach { insumoGasto ->
                                    Column {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("#${insumoGasto.posicion}", fontWeight = FontWeight.Bold, color = AgriVerde, fontSize = 16.sp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(insumoGasto.nombre, fontWeight = FontWeight.Medium, color = TextoPrincipal, fontSize = 14.sp)
                                            }
                                            Text(formatMoneda.format(insumoGasto.costo), fontWeight = FontWeight.Bold, color = TextoPrincipal, fontSize = 14.sp)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            LinearProgressIndicator(
                                                progress = { insumoGasto.porcentaje / 100f },
                                                modifier = Modifier.weight(1f).height(6.dp),
                                                color = AgriVerde,
                                                trackColor = Color(0xFFE7E5E4),
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("${String.format(java.util.Locale("es", "AR"), "%.1f", insumoGasto.porcentaje)}%", color = TextoSecundario, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
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
                        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(bottom = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (pieChartData != null) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    PieChart(
                                        modifier = Modifier.fillMaxWidth().aspectRatio(1f).padding(16.dp),
                                        pieChartData = pieChartData!!,
                                        pieChartConfig = PieChartConfig(
                                            isAnimationEnable = true,
                                            showSliceLabels = true,
                                            sliceLabelTextColor = Color.White,
                                            activeSliceAlpha = 0.9f,
                                            isEllipsizeEnabled = true,
                                            sliceLabelTextSize = 12.sp,
                                            labelVisible = false
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    val totalGasto = pieChartData!!.slices.sumOf { it.value.toDouble() }
                                    val currencyFormat = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("es", "AR"))
                                    
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        pieChartData!!.slices.forEach { slice ->
                                            val porcentaje = if (totalGasto > 0) (slice.value / totalGasto) * 100 else 0.0
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(12.dp)
                                                            .clip(androidx.compose.foundation.shape.CircleShape)
                                                            .background(slice.color)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(slice.label, fontSize = 13.sp, color = TextoPrincipal)
                                                }
                                                Text(
                                                    text = "${String.format(java.util.Locale.US, "%.1f", porcentaje)}% - ${currencyFormat.format(slice.value)}",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = TextoPrincipal
                                                )
                                            }
                                        }
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFE7E5E4))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Total General:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
                                            Text(currencyFormat.format(totalGasto), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
                                        }
                                    }
                                }
                            } else {
                                PlaceholderSeleccion(mensaje = "Sin insumos registrados en esta campaña")
                            }
                        }
                    }
                }

                // PieChart de desglose de cosechas
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Distribución de Destino de Cosecha",
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE7E5E4)),
                        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(bottom = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (desgloseCosechasData != null) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    PieChart(
                                        modifier = Modifier.fillMaxWidth().aspectRatio(1f).padding(16.dp),
                                        pieChartData = desgloseCosechasData!!,
                                        pieChartConfig = PieChartConfig(
                                            isAnimationEnable = true,
                                            showSliceLabels = true,
                                            sliceLabelTextColor = Color.White,
                                            activeSliceAlpha = 0.9f,
                                            isEllipsizeEnabled = true,
                                            sliceLabelTextSize = 12.sp,
                                            labelVisible = false
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
                                    androidx.compose.foundation.layout.FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                                    ) {
                                        val totalCosechas = desgloseCosechasData!!.slices.sumOf { it.value.toDouble() }
                                        desgloseCosechasData!!.slices.forEach { slice ->
                                            val porcentaje = if (totalCosechas > 0) (slice.value / totalCosechas) * 100 else 0.0
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(12.dp)
                                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                                        .background(slice.color)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    "${slice.label} (${String.format(java.util.Locale.US, "%.1f", porcentaje)}% - ${String.format(java.util.Locale.US, "%.1f Tn", slice.value)})", 
                                                    fontSize = 12.sp, 
                                                    color = TextoPrincipal
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                PlaceholderSeleccion(mensaje = "Sin cosechas registradas en esta campaña")
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
            // SECCIÓN 2: Comparador de campañas [#302]
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
            } else if (campaniaA?.id == campaniaB?.id) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                        border = BorderStroke(1.dp, Color(0xFFFED7AA)),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Advertencia",
                                tint = Color(0xFFEA580C),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Las campañas seleccionadas son iguales. Elegí campañas distintas para comparar.",
                                fontSize = 14.sp,
                                color = Color(0xFF9A3412),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else {
                item {
                    val costoA = insumosA.sumOf { it.cantidad * it.precio }
                    val costoB = insumosB.sumOf { it.cantidad * it.precio }
                    val rendimientoA = cosechasA.sumOf { it.cantidad }
                    val rendimientoB = cosechasB.sumOf { it.cantidad }
                    
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
                            valor1 = "%.2f".format(rendimientoA),
                            valor2 = "%.2f".format(rendimientoB),
                            color = AgriAzul,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        CardMetricaComparativa(
                            titulo = "Costo/Ha",
                            valor1 = costoHaStringA,
                            valor2 = costoHaStringB,
                            color = Color(0xFFb91c1c),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    val costoA = insumosA.sumOf { it.cantidad * it.precio }.toFloat()
                    val costoB = insumosB.sumOf { it.cantidad * it.precio }.toFloat()
                    val rendimientoA = cosechasA.sumOf { it.cantidad }.toFloat()
                    val rendimientoB = cosechasB.sumOf { it.cantidad }.toFloat()
                    val maxCosto = maxOf(costoA, costoB, 1f)
                    val maxRendimiento = maxOf(rendimientoA, rendimientoB, 1f)
                    val maxCostoHa = maxOf(costoHaFloatA, costoHaFloatB, 1f)
                    
                    val nombreA = campaniaA?.nombre ?: "Campaña A"
                    val nombreB = campaniaB?.nombre ?: "Campaña B"

                    Text("Gráfico de Comparación", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextoPrincipal)
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE7E5E4)),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Comparacion de Costos
                            Text("Costos Totales", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
                            Spacer(modifier = Modifier.height(8.dp))
                            DoubleBarIndicator(nombreA, costoA, maxCosto, AgriVerde, nombreB, costoB, maxCosto, AgriVerde.copy(alpha = 0.5f))
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Comparacion de Rendimiento
                            Text("Rendimiento", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
                            Spacer(modifier = Modifier.height(8.dp))
                            DoubleBarIndicator(nombreA, rendimientoA, maxRendimiento, AgriAzul, nombreB, rendimientoB, maxRendimiento, AgriAzul.copy(alpha = 0.5f))

                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Comparacion de Costo/Ha
                            Text("Costo por Hectárea", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
                            Spacer(modifier = Modifier.height(8.dp))
                            DoubleBarIndicator(nombreA, costoHaFloatA, maxCostoHa, Color(0xFFb91c1c), nombreB, costoHaFloatB, maxCostoHa, Color(0xFFb91c1c).copy(alpha = 0.5f))
                        }
                    }
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

@VisibleForTesting
@Composable
internal fun DoubleBarIndicator(
    labelA: String, valueA: Float, maxA: Float, colorA: Color,
    labelB: String, valueB: Float, maxB: Float, colorB: Color
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(labelA, modifier = Modifier.weight(0.3f), fontSize = 12.sp, color = TextoSecundario)
            LinearProgressIndicator(
                progress = { if (maxA > 0f) valueA / maxA else 0f },
                color = colorA,
                trackColor = colorA.copy(alpha = 0.2f),
                modifier = Modifier.weight(0.5f).height(12.dp)
            )
            Text(
                "%.2f".format(valueA),
                modifier = Modifier.weight(0.2f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(labelB, modifier = Modifier.weight(0.3f), fontSize = 12.sp, color = TextoSecundario)
            LinearProgressIndicator(
                progress = { if (maxB > 0f) valueB / maxB else 0f },
                color = colorB,
                trackColor = colorB.copy(alpha = 0.2f),
                modifier = Modifier.weight(0.5f).height(12.dp)
            )
            Text(
                "%.2f".format(valueB),
                modifier = Modifier.weight(0.2f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
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

