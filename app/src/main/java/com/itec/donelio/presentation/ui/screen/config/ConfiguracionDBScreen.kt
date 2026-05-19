package com.itec.donelio.presentation.ui.screen.config

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itec.donelio.BuildConfig
import com.itec.donelio.presentation.viewmodel.config.ConfiguracionDBViewModel
import com.itec.donelio.presentation.viewmodel.config.SeedState
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionDBScreen(
    onBack: () -> Unit,
    viewModel: ConfiguracionDBViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val seedState by viewModel.seedState.collectAsState()

    LaunchedEffect(seedState) {
        when (seedState) {
            is SeedState.Exito -> {
                snackbarHostState.showSnackbar("Datos de prueba cargados correctamente")
                viewModel.resetSeedState()
            }
            is SeedState.Error -> {
                snackbarHostState.showSnackbar((seedState as SeedState.Error).mensaje)
                viewModel.resetSeedState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            TopAppBar(
                title = { Text("Base de Datos (CU12/CU13)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
            )
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (BuildConfig.DEBUG) {
                    Button(
                        onClick = { viewModel.cargarDatosPrueba() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AgriVerde),
                        enabled = seedState !is SeedState.Cargando
                    ) {
                        if (seedState is SeedState.Cargando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cargar datos de prueba")
                    }
                }
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TextoPrincipal)
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Exportar Base de Datos (Respaldo)")
                }
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriVerde)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Importar Base de Datos")
                }
            }
        }
    }
}
