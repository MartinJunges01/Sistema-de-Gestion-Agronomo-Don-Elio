package com.itec.donelio.presentation.ui.screen.observacion

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import java.io.File
import com.itec.donelio.domain.model.Observacion
import com.itec.donelio.presentation.ui.components.SelectorCampania
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoPrincipal
import com.itec.donelio.presentation.ui.theme.TextoSecundario
import com.itec.donelio.presentation.util.DialogoRazonPermisoCamara
import com.itec.donelio.presentation.util.abrirAjustesPermiso
import com.itec.donelio.presentation.util.recordarPermisoCamara
import com.itec.donelio.presentation.viewmodel.observacion.FormularioObservacionViewModel
import com.itec.donelio.presentation.viewmodel.observacion.ObservacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObservacionesScreen(
    campaniaId: Int = -1,
    onBack: () -> Unit,
    onGoToCampaniaDetalle: () -> Unit,
    formViewModel: FormularioObservacionViewModel = hiltViewModel(),
    listViewModel: ObservacionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val formState by formViewModel.state.collectAsState()
    val observaciones by listViewModel.observaciones.collectAsState()
    val campanias by listViewModel.campanias.collectAsState()
    val campaniaIdSeleccionada by listViewModel.campaniaIdSeleccionada.collectAsState()
    val isCampaniaValid by listViewModel.isCampaniaValid.collectAsState()

    // ── Gestión del permiso de cámara (CameraUtils) ──────────────────────────
    val controlPermiso = recordarPermisoCamara()
    val snackbarHostState = remember { SnackbarHostState() }
    // Acción pendiente: se ejecuta tan pronto el permiso sea concedido
    var accionPendiente by remember { mutableStateOf<(() -> Unit)?>(null) }
    // Controla la visibilidad del diálogo de rationale
    var mostrarDialogoRazon by remember { mutableStateOf(false) }

    // URI temporal para la foto de cámara
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    var showDeleteDialog by remember { mutableStateOf<Observacion?>(null) }
    var showEditDialog by remember { mutableStateOf<Observacion?>(null) }
    val errorMessage by listViewModel.errorMessage.collectAsState()

    // Launcher de la cámara
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        if (exito) {
            formViewModel.onImagenSeleccionada(tempCameraUri)
        }
    }

    // Launcher de galería (no requiere permiso peligroso)
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            formViewModel.onImagenSeleccionada(uri)
        }
    }

    /** Crea una URI temporal en caché para que la cámara escriba la foto. */
    fun createTempUri(): Uri {
        val tempFile = File.createTempFile("temp_img", ".jpg", context.cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    }

    // Cuando el permiso sea concedido, ejecutar la acción pendiente (abrir cámara)
    LaunchedEffect(controlPermiso.permisoConcedido) {
        if (controlPermiso.permisoConcedido) {
            accionPendiente?.invoke()
            accionPendiente = null
        }
    }

    // Activar el diálogo de rationale cuando el sistema lo indique
    LaunchedEffect(controlPermiso.mostrarRazon) {
        if (controlPermiso.mostrarRazon) mostrarDialogoRazon = true
    }

    // Si el permiso fue denegado permanentemente, mostrar Snackbar con acceso a Ajustes
    LaunchedEffect(controlPermiso.denegadoPermanente) {
        if (controlPermiso.denegadoPermanente) {
            val resultado = snackbarHostState.showSnackbar(
                message = "Permiso de cámara denegado permanentemente. Actívalo en Ajustes.",
                actionLabel = "Abrir Ajustes",
                duration = SnackbarDuration.Long
            )
            if (resultado == SnackbarResult.ActionPerformed) {
                abrirAjustesPermiso(context)
            }
            controlPermiso.restaurar()
        }
    }

    // Resetear el guardado exitoso del formulario
    LaunchedEffect(formState.guardadoExitoso) {
        if (formState.guardadoExitoso) formViewModel.resetGuardadoExitoso()
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            listViewModel.clearError()
        }
    }

    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar Observación", fontWeight = FontWeight.Bold) },
            text = { Text("¿Eliminar esta observación? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    listViewModel.eliminarObservacion(showDeleteDialog!!)
                    showDeleteDialog = null
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("Cancelar", color = TextoSecundario) }
            }
        )
    }

    if (showEditDialog != null) {
        var textoEditado by remember { mutableStateOf(showEditDialog!!.texto) }
        AlertDialog(
            onDismissRequest = { showEditDialog = null },
            title = { Text("Editar Observación", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = textoEditado,
                    onValueChange = { textoEditado = it },
                    label = { Text("Texto") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                    maxLines = 5
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val obsEditada = showEditDialog!!.copy(texto = textoEditado.trim())
                        listViewModel.editarObservacion(obsEditada)
                        showEditDialog = null
                    },
                    enabled = textoEditado.isNotBlank() || showEditDialog!!.imagenUri != null
                ) { Text("Guardar", color = AgriVerde) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = null }) { Text("Cancelar", color = TextoSecundario) }
            }
        )
    }

    // Diálogo de rationale: se muestra cuando el usuario negó el permiso una vez
    if (mostrarDialogoRazon) {
        DialogoRazonPermisoCamara(
            enConfirmar = {
                mostrarDialogoRazon = false
                controlPermiso.restaurar()
                controlPermiso.solicitar()
            },
            enDescartar = {
                mostrarDialogoRazon = false
                controlPermiso.restaurar()
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Observaciones", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriFondo)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SelectorCampania(
                    campanias = campanias,
                    selectedCampaniaId = campaniaIdSeleccionada,
                    onCampaniaSelected = { listViewModel.seleccionarCampania(it) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Nueva observación", fontWeight = FontWeight.Bold, color = TextoPrincipal)
                        OutlinedTextField(
                            value = formState.texto,
                            onValueChange = formViewModel::onTextoChange,
                            label = { Text("Escribe una nota...") },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                            maxLines = 5,
                            isError = formState.errorTexto != null,
                            supportingText = formState.errorTexto?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
                        )

                        formState.imagenUri?.let { uri ->
                            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Vista previa",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
                                )
                                IconButton(
                                    onClick = formViewModel::onBorrarImagen,
                                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Borrar imagen", tint = Color.White)
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            // ── Botón Cámara con verificación de permiso ──────────────
                            OutlinedButton(
                                onClick = {
                                    val uri = createTempUri()
                                    tempCameraUri = uri
                                    if (controlPermiso.permisoConcedido) {
                                        // Permiso ya concedido → lanzar cámara directamente
                                        cameraLauncher.launch(uri)
                                    } else {
                                        // Permiso aún no concedido → guardar acción y solicitar
                                        accionPendiente = { cameraLauncher.launch(uri) }
                                        controlPermiso.solicitar()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Cámara")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cámara")
                            }
                            // ── Botón Galería (no requiere permiso peligroso) ─────────
                            OutlinedButton(
                                onClick = { galleryLauncher.launch("image/*") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = "Galería")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Galería")
                            }
                        }

                        Button(
                            onClick = formViewModel::guardar,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            enabled = !formState.isLoading && isCampaniaValid,
                            colors = ButtonDefaults.buttonColors(containerColor = AgriVerde),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (formState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Guardar observación", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            if (observaciones.isNotEmpty()) {
                item { Text("Observaciones registradas", fontWeight = FontWeight.Bold, color = TextoPrincipal) }
                items(observaciones, key = { it.id }) { obs ->
                    ObservacionCard(
                        observacion = obs,
                        onEdit = { showEditDialog = obs },
                        onDelete = { showDeleteDialog = obs }
                    )
                }
            } else {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Aún no hay observaciones", color = TextoSecundario)
                        }
                    }
                }
            }
        }
        } // Cierre del Column

        // ── Snackbar anclado al fondo para feedback de permisos ──────────────────
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { data ->
            Snackbar {
                Text(data.visuals.message)
            }
        }
    } // Cierre del Box
}

@Composable
private fun ObservacionCard(observacion: Observacion, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE7E5E4)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(observacion.texto, color = TextoPrincipal, fontSize = 14.sp, modifier = Modifier.weight(1f))
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TextoSecundario, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                    }
                }
            }
            if (observacion.imagenUri != null) {
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = observacion.imagenUri,
                    contentDescription = "Imagen de la observación",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}
