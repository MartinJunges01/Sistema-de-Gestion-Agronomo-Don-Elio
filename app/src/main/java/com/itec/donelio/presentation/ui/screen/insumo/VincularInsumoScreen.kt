package com.itec.donelio.presentation.ui.screen.insumo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal
import com.itec.donelio.presentation.ui.theme.TextoSecundario
import com.itec.donelio.presentation.viewmodel.insumo.InsumoVinculacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VincularInsumoScreen(
    campaniaId: Int = -1,
    viewModel: InsumoVinculacionViewModel = hiltViewModel(),
    onGoToCatalogo: () -> Unit,
    onBack: () -> Unit
) {
    val catalogo by viewModel.catalogo.collectAsState()
    val isCampaniaValid by viewModel.isCampaniaValid.collectAsState()

    var busqueda by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

    val filtrados = if (busqueda.isBlank()) catalogo else catalogo.filter { it.nombre.contains(busqueda, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vincular Insumo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
            )
        },
        containerColor = AgriFondo
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
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
                    onClick = onGoToCatalogo,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Crear nuevo insumo") }
            }

            filtrados.take(5).forEach { insumo ->
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
                        onBack()
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriVerde),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isCampaniaValid && insumoSeleccionado != null && cantidad.isNotBlank()
                ) { Text("Vincular a Campaña") }

                OutlinedButton(
                    onClick = onGoToCatalogo,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Agregar al catálogo") }
            }
        }
    }
}
