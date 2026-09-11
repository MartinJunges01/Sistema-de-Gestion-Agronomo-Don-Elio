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
    onGoToVincular: () -> Unit,
    onBack: () -> Unit
) {
    val vinculados by viewModel.insumosVinculados.collectAsState()
    val campanias by viewModel.campanias.collectAsState()
    val campaniaIdSeleccionada by viewModel.campaniaIdSeleccionada.collectAsState()
    val isCampaniaValid by viewModel.isCampaniaValid.collectAsState()

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

            if (vinculados.isEmpty()) {
                item {
                    Text("No hay insumos vinculados a esta campaña", color = TextoSecundario, fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp))
                }
            } else {
                items(vinculados) { vinculado ->
                    val nombreBase = vinculado.nombreInsumo.ifBlank { "Insumo #${vinculado.idInsumo}" }
                    val nombreInsumo = if (vinculado.insumoActivo) nombreBase else "$nombreBase (Eliminado)"
                    val total = vinculado.cantidad * vinculado.precio
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE7E5E4)), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Vinculado", tint = AgriVerde)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(nombreInsumo, fontWeight = FontWeight.Bold, color = if (vinculado.insumoActivo) TextoPrincipal else TextoSecundario)
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
}
