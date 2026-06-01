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
    val formState by formViewModel.state.collectAsState()
    val observaciones by listViewModel.observaciones.collectAsState()
    val campanias by listViewModel.campanias.collectAsState()
    val campaniaIdSeleccionada by listViewModel.campaniaIdSeleccionada.collectAsState()

    LaunchedEffect(formState.guardadoExitoso) {
        if (formState.guardadoExitoso) formViewModel.resetGuardadoExitoso()
    }

    val context = LocalContext.current
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            formViewModel.onImagenSeleccionada(tempCameraUri)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            formViewModel.onImagenSeleccionada(uri)
        }
    }

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

                        // Vista previa de la imagen seleccionada
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

                        // Fila de botones para adjuntar imagen
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = {
                                    tempCameraUri = createTempUri()
                                    tempCameraUri?.let { cameraLauncher.launch(it) }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Cámara")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cámara")
                            }
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
                            enabled = !formState.isLoading,
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
                    ObservacionCard(observacion = obs)
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
    }
}

@Composable
private fun ObservacionCard(observacion: Observacion) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE7E5E4)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(observacion.texto, color = TextoPrincipal, fontSize = 14.sp)
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
