package com.itec.donelio.presentation.ui.screen.cosecha

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.model.CosechaNoAlmacenada
import com.itec.donelio.presentation.ui.components.SelectorCampania
import com.itec.donelio.presentation.ui.components.CampaniaSeleccionadaCard
import androidx.compose.runtime.remember
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal
import com.itec.donelio.presentation.ui.theme.TextoSecundario
import com.itec.donelio.presentation.viewmodel.cosecha.CosechaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CosechasScreen(
    campaniaId: Int = -1,
    onBack: () -> Unit,
    onGoToCampaniaDetalle: () -> Unit,
    viewModel: CosechaViewModel = hiltViewModel()
) {
    val cosechas by viewModel.cosechas.collectAsState()
    val noAlmacenadasDetalle by viewModel.noAlmacenadasDetalle.collectAsState()
    val campanias by viewModel.campanias.collectAsState()
    val campaniaIdSeleccionada by viewModel.campaniaIdSeleccionada.collectAsState()
    val campaniaActiva = remember(campanias, campaniaIdSeleccionada) {
        campanias.find { it.id == campaniaIdSeleccionada }
    }

    val almacenadas = cosechas.filter { it.almacen.isNotBlank() }
    val noAlmacenadas = cosechas.filter { it.almacen.isBlank() }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Gestión de Cosechas", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
        )
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SelectorCampania(
                    campanias = campanias,
                    selectedCampaniaId = campaniaIdSeleccionada,
                    onCampaniaSelected = { viewModel.seleccionarCampania(it) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            item {
                CampaniaSeleccionadaCard(
                    campania = campaniaActiva,
                    onClick = onGoToCampaniaDetalle
                )
            }

            if (almacenadas.isNotEmpty()) {
                item { Text("Cosechas Almacenadas", fontWeight = FontWeight.Bold, color = TextoPrincipal) }
                items(almacenadas) { cosecha ->
                    CosechaCard(cosecha = cosecha, esAlmacenada = true)
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            if (noAlmacenadas.isNotEmpty()) {
                item { Text("Cosechas No Almacenadas (Venta/Reserva)", fontWeight = FontWeight.Bold, color = TextoPrincipal) }
                items(noAlmacenadas) { cosecha ->
                    val detalle = noAlmacenadasDetalle[cosecha.id]
                    CosechaCard(cosecha = cosecha, esAlmacenada = false, detalle = detalle)
                }
            }

            if (cosechas.isEmpty()) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                        ) {
                            Text("Sin cosechas registradas", fontWeight = FontWeight.Medium, color = TextoSecundario)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CosechaCard(cosecha: Cosecha, esAlmacenada: Boolean, detalle: CosechaNoAlmacenada? = null) {
    val borderColor = if (esAlmacenada) AgriVerde else Color(0xFFD97706)
    val cantidadColor = if (esAlmacenada) AgriVerde else Color(0xFFB45309)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        ListItem(
            headlineContent = { Text("${formatCantidad(cosecha.cantidad)} ${cosecha.unidad}", fontWeight = FontWeight.Bold, color = cantidadColor) },
            supportingContent = {
                if (esAlmacenada) {
                    Text(cosecha.almacen, color = TextoSecundario)
                } else if (detalle != null) {
                    Text("${detalle.tipo} · $ ${"%,.2f".format(detalle.precio)}", color = TextoSecundario)
                } else {
                    Text(formatFecha(cosecha.fecha), color = TextoSecundario)
                }
            },
            trailingContent = {
                Text(formatFecha(cosecha.fecha), color = TextoSecundario, fontSize = 12.sp)
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

private fun formatCantidad(cantidad: Double): String {
    return if (cantidad == cantidad.toLong().toDouble()) {
        cantidad.toLong().toString()
    } else {
        "%,.2f".format(cantidad)
    }
}

private fun formatFecha(timestamp: Long): String {
    if (timestamp <= 0) return ""
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
