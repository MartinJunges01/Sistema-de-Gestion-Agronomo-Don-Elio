package com.itec.donelio.presentation.ui.screen.reportes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import com.itec.donelio.presentation.ui.components.CardMetricaComparativa
import com.itec.donelio.presentation.ui.theme.AgriAzul
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal
import com.itec.donelio.presentation.ui.theme.TextoSecundario
import com.itec.donelio.presentation.viewmodel.reportes.ReportesViewModel
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesRendimientoScreen(
    onBack: () -> Unit,
    viewModel: ReportesViewModel = hiltViewModel()
) {
    val pieChartData by viewModel.pieChartData.collectAsState()
    val exportStatus by viewModel.exportStatus.collectAsState()
    val context = LocalContext.current
    
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

    val campanas = listOf("Campaña Soja 2026", "Campaña Maíz 2026", "Campaña Trigo 2025")
    var campania1Expandido by remember { mutableStateOf(false) }
    var campania2Expandido by remember { mutableStateOf(false) }
    var campania1 by remember { mutableStateOf(campanas[0]) }
    var campania2 by remember { mutableStateOf(campanas[1]) }

    Column(modifier = Modifier.fillMaxSize()) {
        var mostrarMenuExportar by remember { mutableStateOf(false) }

        TopAppBar(
            title = { Text("Reportes y Análisis", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
            actions = {
                Box {
                    IconButton(onClick = { mostrarMenuExportar = true }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Exportar", tint = AgriVerde)
                    }
                    DropdownMenu(
                        expanded = mostrarMenuExportar,
                        onDismissRequest = { mostrarMenuExportar = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Exportar a Excel") },
                            onClick = { 
                                mostrarMenuExportar = false 
                                csvLauncher.launch("Reporte_Insumos_Don_Elio.csv")
                            },
                            leadingIcon = { Icon(Icons.Default.TableChart, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Exportar a PDF") },
                            onClick = { 
                                mostrarMenuExportar = false
                                pdfLauncher.launch("Reporte_Don_Elio.pdf")
                            },
                            leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null) }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
        )
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            item {
                Text("Comparar Campañas", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextoPrincipal)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    ExposedDropdownMenuBox(
                        expanded = campania1Expandido,
                        onExpandedChange = { campania1Expandido = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = campania1,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Campaña A") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = campania1Expandido) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            textStyle = TextStyle(fontSize = 14.sp)
                        )
                        ExposedDropdownMenu(expanded = campania1Expandido, onDismissRequest = { campania1Expandido = false }) {
                            campanas.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = { campania1 = item; campania1Expandido = false }
                                )
                            }
                        }
                    }
                    ExposedDropdownMenuBox(
                        expanded = campania2Expandido,
                        onExpandedChange = { campania2Expandido = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = campania2,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Campaña B") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = campania2Expandido) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            textStyle = TextStyle(fontSize = 14.sp)
                        )
                        ExposedDropdownMenu(expanded = campania2Expandido, onDismissRequest = { campania2Expandido = false }) {
                            campanas.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = { campania2 = item; campania2Expandido = false }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text("Métricas Comparativas", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextoPrincipal)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    CardMetricaComparativa(
                        titulo = "Rendimiento",
                        valor1 = "4.2 Tn/ha",
                        valor2 = "3.8 Tn/ha",
                        color = AgriVerde,
                        modifier = Modifier.weight(1f)
                    )
                    CardMetricaComparativa(
                        titulo = "Ganancias",
                        valor1 = "$1,200,000",
                        valor2 = "$980,000",
                        color = AgriAzul,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    CardMetricaComparativa(
                        titulo = "Costos Totales",
                        valor1 = "$450,000",
                        valor2 = "$520,000",
                        color = Color(0xFFD97706),
                        modifier = Modifier.weight(1f)
                    )
                    CardMetricaComparativa(
                        titulo = "Insumos Totales",
                        valor1 = "2,400 kg",
                        valor2 = "3,100 kg",
                        color = TextoPrincipal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text("Evolución Mensual de Costos", fontWeight = FontWeight.Bold, color = TextoPrincipal)
                Spacer(modifier = Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth().height(220.dp)) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(top = 16.dp, bottom = 24.dp, start = 8.dp, end = 8.dp)) {
                            val meses = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun")
                            val valores1 = listOf(80f, 95f, 70f, 110f, 90f, 120f)
                            val valores2 = listOf(60f, 85f, 90f, 75f, 100f, 95f)
                            val maxVal = 130f
                            val stepX = size.width / (meses.size - 1)

                            for (i in 0 until meses.size - 1) {
                                val x1 = i * stepX
                                val y1 = size.height - (valores1[i] / maxVal * size.height)
                                val x2 = (i + 1) * stepX
                                val y2 = size.height - (valores1[i + 1] / maxVal * size.height)
                                drawLine(color = AgriVerde, start = Offset(x1, y1), end = Offset(x2, y2), strokeWidth = 6f, cap = StrokeCap.Round)
                                drawCircle(color = AgriVerde, radius = 8f, center = Offset(x1, y1))
                                if (i == meses.size - 2) drawCircle(color = AgriVerde, radius = 8f, center = Offset(x2, y2))
                            }

                            for (i in 0 until meses.size - 1) {
                                val x1 = i * stepX
                                val y1 = size.height - (valores2[i] / maxVal * size.height)
                                val x2 = (i + 1) * stepX
                                val y2 = size.height - (valores2[i + 1] / maxVal * size.height)
                                drawLine(color = Color(0xFFD97706), start = Offset(x1, y1), end = Offset(x2, y2), strokeWidth = 6f, cap = StrokeCap.Round)
                                drawCircle(color = Color(0xFFD97706), radius = 8f, center = Offset(x1, y1))
                                if (i == meses.size - 2) drawCircle(color = Color(0xFFD97706), radius = 8f, center = Offset(x2, y2))
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter), horizontalArrangement = Arrangement.SpaceEvenly) {
                            listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun").forEach { Text(it, fontSize = 11.sp, color = TextoSecundario) }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp), modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(2.dp)).background(AgriVerde))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(campania1, fontSize = 12.sp, color = TextoSecundario)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFD97706)))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(campania2, fontSize = 12.sp, color = TextoSecundario)
                    }
                }
            }

            item {
                Text("Distribución de Gastos por Insumo", fontWeight = FontWeight.Bold, color = TextoPrincipal)
                Spacer(modifier = Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth().height(320.dp)) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        if (pieChartData != null) {
                            val pieChartConfig = PieChartConfig(
                                isAnimationEnable = true,
                                showSliceLabels = true,
                                sliceLabelTextColor = Color.White,
                                activeSliceAlpha = 0.9f,
                                isEllipsizeEnabled = true,
                                sliceLabelTextSize = 12.sp,
                                labelVisible = true
                            )
                            PieChart(
                                modifier = Modifier.fillMaxSize().padding(24.dp),
                                pieChartData = pieChartData!!,
                                pieChartConfig = pieChartConfig
                            )
                        } else {
                            Text("No hay datos suficientes para el gráfico", color = TextoSecundario)
                        }
                    }
                }
            }
        }
    }
}
