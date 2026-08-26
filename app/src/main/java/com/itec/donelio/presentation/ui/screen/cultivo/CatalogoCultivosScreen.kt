package com.itec.donelio.presentation.ui.screen.cultivo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itec.donelio.domain.model.Cultivo
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.Stone600
import com.itec.donelio.presentation.ui.theme.Stone900
import com.itec.donelio.presentation.viewmodel.cultivo.CultivoCatalogoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoCultivosScreen(
    viewModel: CultivoCatalogoViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val catalogo by viewModel.catalogo.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var cultivoAEditar by remember { mutableStateOf<Cultivo?>(null) }
    var showAgregarDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de Cultivos", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (catalogo.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Eco, contentDescription = null, modifier = Modifier.size(64.dp), tint = Stone600)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No hay cultivos en el catálogo", color = Stone600, fontSize = 16.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(catalogo) { cultivo ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE7E5E4)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ListItem(
                                headlineContent = { Text(cultivo.nombre, fontWeight = FontWeight.Bold) },
                                leadingContent = { Icon(Icons.Default.Eco, contentDescription = null, tint = AgriVerde) },
                                trailingContent = {
                                    Row {
                                        IconButton(onClick = { cultivoAEditar = cultivo }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Stone900)
                                        }
                                        IconButton(onClick = { viewModel.eliminarCultivo(cultivo.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFDC2626))
                                        }
                                    }
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                        }
                    }
                }
            }
            
            // Botón en la parte inferior para agregar nuevo cultivo
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Button(
                    onClick = { showAgregarDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriVerde),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar Nuevo Cultivo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (cultivoAEditar != null) {
        DialogEditarCultivo(
            cultivo = cultivoAEditar!!,
            onDismiss = { cultivoAEditar = null },
            onGuardar = { cultivoEditado ->
                viewModel.editarCultivo(cultivoEditado)
                cultivoAEditar = null
            },
            onValidar = viewModel::validarCultivo
        )
    }

    if (showAgregarDialog) {
        DialogAgregarCultivo(
            onDismiss = { showAgregarDialog = false },
            onGuardar = { nombre ->
                viewModel.crearCultivo(nombre)
                showAgregarDialog = false
            },
            onValidar = viewModel::validarCultivo
        )
    }
}

@Composable
private fun DialogEditarCultivo(
    cultivo: Cultivo,
    onDismiss: () -> Unit,
    onGuardar: (Cultivo) -> Unit,
    onValidar: (String) -> Boolean
) {
    var nombre by remember { mutableStateOf(cultivo.nombre) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Cultivo", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del Cultivo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onGuardar(cultivo.copy(nombre = nombre.trim())) },
                enabled = onValidar(nombre),
                colors = ButtonDefaults.buttonColors(containerColor = AgriVerde)
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun DialogAgregarCultivo(
    onDismiss: () -> Unit,
    onGuardar: (String) -> Unit,
    onValidar: (String) -> Boolean
) {
    var nombre by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Nuevo Cultivo", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del Cultivo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onGuardar(nombre.trim()) },
                enabled = onValidar(nombre),
                colors = ButtonDefaults.buttonColors(containerColor = AgriVerde)
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
