package com.itec.donelio.presentation.ui.screen.observacion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itec.donelio.domain.model.Observacion
import com.itec.donelio.presentation.ui.components.CampanaSeleccionadaCard
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

    LaunchedEffect(formState.guardadoExitoso) {
        if (formState.guardadoExitoso) formViewModel.resetGuardadoExitoso()
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
            item { CampanaSeleccionadaCard(onClick = onGoToCampaniaDetalle) }

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
                Spacer(modifier = Modifier.height(4.dp))
                Text("📎 Foto adjunta", color = TextoSecundario, fontSize = 12.sp)
            }
        }
    }
}
