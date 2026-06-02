package com.itec.donelio.presentation.ui.screen.insumo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.itec.donelio.presentation.ui.components.SelectorCampania
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal
import com.itec.donelio.presentation.ui.theme.TextoSecundario
import com.itec.donelio.presentation.viewmodel.insumo.InsumoVinculacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsumosScreen(
    campaniaId: Int = -1,
    viewModel: InsumoVinculacionViewModel = hiltViewModel(),
    onGoToCatalogo: () -> Unit,
    onGoToCampaniaDetalle: () -> Unit,
    onBack: () -> Unit
) {
    val vinculados by viewModel.insumosVinculados.collectAsState()
    val catalogo by viewModel.catalogo.collectAsState()
    val campanias by viewModel.campanias.collectAsState()
    val campaniaIdSeleccionada by viewModel.campaniaIdSeleccionada.collectAsState()
    val isCampaniaValid by viewModel.isCampaniaValid.collectAsState()
    val catalogoMap = remember(catalogo) { catalogo.associateBy { it.id } }

    var mostrarBottomSheet by remember { mutableStateOf(false) }
    var busqueda by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

    val filtrados = if (busqueda.isBlank()) catalogo else catalogo.filter { it.nombre.contains(busqueda, ignoreCase = true) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Vincular Insumos (CU9)", fontWeight = FontWeight.Bold) },
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
                    onClick = { mostrarBottomSheet = true },
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

            if (vinculados.isEmpty()) {
                item {
                    Text("No hay insumos vinculados a esta campaña", color = TextoSecundario, fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp))
                }
            } else {
                items(vinculados) { vinculado ->
                    val nombreInsumo = catalogoMap[vinculado.idInsumo]?.nombre ?: "Insumo #${vinculado.idInsumo}"
                    val total = vinculado.cantidad * vinculado.precio
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Vinculado", tint = AgriVerde)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(nombreInsumo, fontWeight = FontWeight.Bold, color = TextoPrincipal)
                                Text(
                                    "${"%.2f".format(vinculado.cantidad)} × $ ${"%,.2f".format(vinculado.precio)} = $ ${"%,.2f".format(total)}",
                                    fontSize = 12.sp,
                                    color = if (vinculado.precio > 0) AgriVerde else TextoSecundario
                                )
                            }
                            IconButton(onClick = { viewModel.desvincularInsumo(vinculado) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Desvincular", tint = Color(0xFFDC2626))
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { mostrarBottomSheet = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Vincular Insumo a Campaña", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextoPrincipal)

                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    label = { Text("Buscar insumo en catálogo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )

                if (filtrados.isEmpty() && busqueda.isNotBlank()) {
                    Text("El insumo no existe en el catálogo", color = TextoSecundario, fontSize = 14.sp)
                    OutlinedButton(
                        onClick = {
                            mostrarBottomSheet = false
                            onGoToCatalogo()
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Crear nuevo insumo") }
                }

                filtrados.forEach { insumo ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { busqueda = insumo.nombre },
                        color = if (busqueda == insumo.nombre) AgriVerde.copy(alpha = 0.1f) else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("${insumo.nombre} (${insumo.categoria})", modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp), color = TextoPrincipal)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = cantidad,
                        onValueChange = { cantidad = it },
                        label = { Text("Cantidad") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = { Text("Precio (opcional)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    val insumoSeleccionado = catalogo.find { it.nombre == busqueda }
                    Button(
                        onClick = {
                            if (insumoSeleccionado != null) {
                                viewModel.asignarInsumo(
                                    idInsumo = insumoSeleccionado.id,
                                    cantidad = cantidad.toDoubleOrNull() ?: 0.0,
                                    precio = precio.toDoubleOrNull() ?: 0.0
                                )
                            }
                            mostrarBottomSheet = false
                            busqueda = ""
                            cantidad = ""
                            precio = ""
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AgriVerde),
                        shape = RoundedCornerShape(12.dp),
                        enabled = isCampaniaValid && insumoSeleccionado != null && cantidad.isNotBlank()
                    ) { Text("Vincular a Campaña") }

                    OutlinedButton(
                        onClick = { onGoToCatalogo() },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Agregar al catálogo") }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
