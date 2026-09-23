package com.itec.donelio.presentation.ui.screen.insumo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.itec.donelio.domain.model.CampaniaInsumo
import com.itec.donelio.presentation.ui.components.SelectorCampania
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal
import com.itec.donelio.presentation.ui.theme.TextoSecundario
import com.itec.donelio.presentation.util.FormatUtils
import com.itec.donelio.presentation.viewmodel.insumo.InsumoVinculacionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsumosScreen(
    campaniaId: Int = -1,
    viewModel: InsumoVinculacionViewModel = hiltViewModel(),
    onGoToCatalogo: () -> Unit,
    onGoToCampaniaDetalle: () -> Unit,
    onGoToVincular: () -> Unit,
    onBack: () -> Unit
) {
    val vinculados by viewModel.insumosVinculados.collectAsState()
    val campanias by viewModel.campanias.collectAsState()
    val campaniaIdSeleccionada by viewModel.campaniaIdSeleccionada.collectAsState()
    val isCampaniaValid by viewModel.isCampaniaValid.collectAsState()

    var insumoEditando by remember { mutableStateOf<CampaniaInsumo?>(null) }

    // Agrupar los registros por idInsumo para la vista de acordeón [#455]
    val agrupados = remember(vinculados) {
        vinculados.groupBy { it.idInsumo }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Insumos", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
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
                SelectorCampania(
                    campanias = campanias,
                    selectedCampaniaId = campaniaIdSeleccionada,
                    onCampaniaSelected = { viewModel.seleccionarCampania(it) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                Button(
                    onClick = onGoToVincular,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriVerde),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isCampaniaValid
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Vincular Nuevo Insumo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            item {
                Text("Insumos Ya Vinculados", fontWeight = FontWeight.Bold, color = TextoPrincipal, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
            }

            if (agrupados.isEmpty()) {
                item {
                    Text("No hay insumos vinculados a esta campaña", color = TextoSecundario, fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp))
                }
            } else {
                items(agrupados.entries.toList(), key = { it.key }) { (_, registros) ->
                    InsumoAgrupado(
                        registros = registros,
                        onEditar = { insumoEditando = it },
                        onEliminar = { viewModel.desvincularInsumo(it) }
                    )
                }
            }
        }
    }

    insumoEditando?.let { insumo ->
        DialogEditarCampaniaInsumo(
            insumo = insumo,
            onDismiss = { insumoEditando = null },
            onConfirm = { cantidad, precio ->
                viewModel.editarInsumo(insumo, cantidad, precio)
                insumoEditando = null
            }
        )
    }
}

/**
 * Card agrupada por tipo de insumo con acordeón expansible [#455].
 * Muestra el ícono propio del insumo, totales y permite expandir para
 * ver y operar sobre cada registro individual.
 */
@Composable
fun InsumoAgrupado(
    registros: List<CampaniaInsumo>,
    onEditar: (CampaniaInsumo) -> Unit,
    onEliminar: (CampaniaInsumo) -> Unit
) {
    val primero = registros.first()
    val cantidadTotal = registros.sumOf { it.cantidad }
    val costoTotal = registros.sumOf { it.cantidad * it.precio }
    val nombreBase = primero.nombreInsumo.ifBlank { "Insumo #${primero.idInsumo}" }
    val nombreInsumo = if (primero.insumoActivo) nombreBase else "$nombreBase (Eliminado)"
    val cantidadRegistros = registros.size

    var expandido by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE7E5E4)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // ── Fila principal (siempre visible) ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandido = !expandido }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícono propio del insumo o fallback genérico
                IconoInsumo(icono = primero.iconoInsumo, activo = primero.insumoActivo)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            nombreInsumo,
                            fontWeight = FontWeight.Bold,
                            color = if (primero.insumoActivo) TextoPrincipal else TextoSecundario
                        )
                        if (cantidadRegistros > 1) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Badge(containerColor = AgriVerde) {
                                Text("x$cantidadRegistros", fontSize = 10.sp, color = Color.White)
                            }
                        }
                    }
                    Text(
                        "${FormatUtils.formatCantidad(cantidadTotal)} total - ${FormatUtils.formatMoneda(costoTotal)}",
                        fontSize = 12.sp,
                        color = if (costoTotal > 0) AgriVerde else TextoSecundario
                    )
                }
                Icon(
                    imageVector = if (expandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expandido) "Colapsar" else "Expandir",
                    tint = TextoSecundario
                )
            }

            // ── Registros individuales (acordeón) ──
            AnimatedVisibility(
                visible = expandido,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    HorizontalDivider(color = Color(0xFFE7E5E4))
                    registros.forEach { registro ->
                        FilaRegistroIndividual(
                            registro = registro,
                            onEditar = { onEditar(registro) },
                            onEliminar = { onEliminar(registro) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Fila de un registro individual de aplicación de insumo.
 * Muestra fecha automática, cantidad, precio, costo parcial y acciones editar/eliminar.
 */
@Composable
fun FilaRegistroIndividual(
    registro: CampaniaInsumo,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val fechaFormateada = remember(registro.fechaAplicacion) {
        if (registro.fechaAplicacion > 0L) {
            SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(registro.fechaAplicacion))
        } else {
            "—"
        }
    }
    val costoParcial = registro.cantidad * registro.precio

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Línea decorativa
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(36.dp)
                .padding(end = 0.dp)
        ) {
            HorizontalDivider(
                modifier = Modifier.fillMaxHeight().width(2.dp),
                color = Color(0xFFD1FAE5)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "${FormatUtils.formatCantidad(registro.cantidad)} x ${FormatUtils.formatMoneda(registro.precio)} = ${FormatUtils.formatMoneda(costoParcial)}",
                fontSize = 13.sp,
                color = TextoPrincipal
            )
            Text(
                fechaFormateada,
                fontSize = 11.sp,
                color = TextoSecundario
            )
        }
        IconButton(onClick = onEditar, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TextoSecundario, modifier = Modifier.size(18.dp))
        }
        IconButton(onClick = onEliminar, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
        }
    }
}

/**
 * Muestra el ícono Material apropiado para el insumo según el código de ícono almacenado,
 * o un ícono genérico de semilla si el código no coincide.
 */
@Composable
fun IconoInsumo(icono: String?, activo: Boolean) {
    val alpha = if (activo) 1f else 0.5f
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(Color(0xFFF3F4F6), shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = icono ?: "🌾",
            fontSize = 20.sp,
            modifier = Modifier.alpha(alpha)
        )
    }
}


@Composable
fun DialogEditarCampaniaInsumo(
    insumo: CampaniaInsumo,
    onDismiss: () -> Unit,
    onConfirm: (Double, Double) -> Unit
) {
    var cantidadStr by remember { mutableStateOf(insumo.cantidad.toString()) }
    var precioStr by remember { mutableStateOf(insumo.precio.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Insumo", fontWeight = FontWeight.Bold, color = TextoPrincipal) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(insumo.nombreInsumo, fontWeight = FontWeight.Medium, color = TextoPrincipal)
                OutlinedTextField(
                    value = cantidadStr,
                    onValueChange = { cantidadStr = it },
                    label = { Text("Cantidad") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = precioStr,
                    onValueChange = { precioStr = it },
                    label = { Text("Precio") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val c = cantidadStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val p = precioStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                    if (c > 0) {
                        onConfirm(c, p)
                    }
                }
            ) {
                Text("Guardar", color = AgriVerde, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextoSecundario) }
        },
        containerColor = Color.White
    )
}
