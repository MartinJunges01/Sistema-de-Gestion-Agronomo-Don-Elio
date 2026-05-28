package com.itec.donelio.presentation.ui.screen.config

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itec.donelio.presentation.viewmodel.BackupUiState
import com.itec.donelio.presentation.viewmodel.BackupViewModel
import com.itec.donelio.presentation.viewmodel.config.ConfiguracionDBViewModel
import com.itec.donelio.presentation.viewmodel.config.SeedState
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionDBScreen(
    onBack: () -> Unit,
    configViewModel: ConfiguracionDBViewModel = hiltViewModel(),
    backupViewModel: BackupViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val seedState by configViewModel.seedState.collectAsState()
    val backupState by backupViewModel.uiState.collectAsState()
    val restoreCompleted by backupViewModel.restoreCompleted.collectAsState()

    var showRestoreDialog by remember { mutableStateOf(false) }
    var pendingRestoreUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val createBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        uri?.let { backupViewModel.crearBackup(it) }
    }

    val openRestoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            pendingRestoreUri = it
            showRestoreDialog = true
        }
    }

    LaunchedEffect(seedState) {
        when (seedState) {
            is SeedState.Exito -> {
                snackbarHostState.showSnackbar("Datos de prueba cargados correctamente")
                configViewModel.resetSeedState()
            }
            is SeedState.Error -> {
                snackbarHostState.showSnackbar((seedState as SeedState.Error).mensaje)
                configViewModel.resetSeedState()
            }
            else -> {}
        }
    }

    LaunchedEffect(backupState) {
        when (backupState) {
            is BackupUiState.Success -> {
                snackbarHostState.showSnackbar((backupState as BackupUiState.Success).message)
            }
            is BackupUiState.Error -> {
                snackbarHostState.showSnackbar((backupState as BackupUiState.Error).message)
            }
            else -> {}
        }
    }

    LaunchedEffect(restoreCompleted) {
        if (restoreCompleted) {
            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
            Runtime.getRuntime().exit(0)
        }
    }

    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = {
                showRestoreDialog = false
                pendingRestoreUri = null
            },
            title = { Text("Restaurar copia de seguridad") },
            text = { Text("Se sobrescribirán TODOS los datos actuales. Esta acción no se puede deshacer. ¿Desea continuar?") },
            confirmButton = {
                TextButton(onClick = {
                    showRestoreDialog = false
                    pendingRestoreUri?.let { backupViewModel.restaurarBackup(it) }
                    pendingRestoreUri = null
                }) {
                    Text("Restaurar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRestoreDialog = false
                    pendingRestoreUri = null
                }) {
                    Text("Cancelar")
                }
            }
        )
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
                Button(
                    onClick = { createBackupLauncher.launch("don_elio_backup.db") },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TextoPrincipal),
                    enabled = backupState !is BackupUiState.Loading
                ) {
                    if (backupState is BackupUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.Upload, contentDescription = null)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Exportar Base de Datos (Respaldo)")
                }
                Button(
                    onClick = {
                        openRestoreLauncher.launch(
                            arrayOf("application/octet-stream", "application/x-sqlite3", "*/*")
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriVerde),
                    enabled = backupState !is BackupUiState.Loading
                ) {
                    if (backupState is BackupUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.Download, contentDescription = null)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Importar Base de Datos")
                }
            }
        }
    }
}