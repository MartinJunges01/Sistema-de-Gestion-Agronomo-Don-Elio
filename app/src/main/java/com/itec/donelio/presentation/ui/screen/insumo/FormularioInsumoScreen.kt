package com.itec.donelio.presentation.ui.screen.insumo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.viewmodel.insumo.FormularioInsumoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioInsumoScreen(
    viewModel: FormularioInsumoViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val iconosDisponibles = listOf("ðŸŒ±", "ðŸ’§", "ðŸ’Š", "â›½", "âš™ï¸", "ðŸšœ", "ðŸ“¦", "ðŸ§ª", "ðŸŒ¾", "âœ‚ï¸")
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.guardadoExitoso) {
        if (state.guardadoExitoso) onBack()
    }

    LaunchedEffect(state.errorGeneral) {
        state.errorGeneral?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Registrar Insumo", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(androidx.compose.foundation.rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre del Insumo") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.errorNombre != null,
                supportingText = state.errorNombre?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true
            )
            OutlinedTextField(
                value = state.categoria,
                onValueChange = viewModel::onCategoriaChange,
                label = { Text("CategorÃ­a (Ej: Semilla, Fertilizante)") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.errorCategoria != null,
                supportingText = state.errorCategoria?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true
            )


            Text("Seleccionar Ãcono", fontWeight = FontWeight.Bold)
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().height(100.dp)
            ) {
                items(iconosDisponibles) { icono ->
                    val isSelected = state.icono == icono
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AgriVerde.copy(alpha = 0.2f) else Color.Transparent)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) AgriVerde else Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.onIconoChange(icono) },
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(text = icono, fontSize = 24.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = viewModel::guardar,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = state.isGuardarHabilitado && !state.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = AgriVerde),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Guardar Insumo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

