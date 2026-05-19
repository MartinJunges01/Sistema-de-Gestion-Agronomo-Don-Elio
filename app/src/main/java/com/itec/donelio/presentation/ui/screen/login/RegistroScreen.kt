package com.itec.donelio.presentation.ui.screen.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itec.donelio.presentation.ui.theme.Emerald600
import com.itec.donelio.presentation.ui.theme.Stone600
import com.itec.donelio.presentation.viewmodel.login.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onRegisterSuccess: () -> Unit,
    onGoToLogin: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.registroExitoso) {
        if (state.registroExitoso) onRegisterSuccess()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Crear Cuenta", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Emerald600)
        Text("Configura tu acceso local", color = Stone600, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(40.dp))
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it; viewModel.limpiarError() },
            label = { Text("Nombre Completo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it; viewModel.limpiarError() },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; viewModel.limpiarError() },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        if (state.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(state.error!!, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { viewModel.registro(nombre, usuario, password) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
            shape = RoundedCornerShape(12.dp),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Registrarse", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onGoToLogin) {
            Text("¿Ya tienes cuenta? Inicia sesión", color = Emerald600, fontWeight = FontWeight.SemiBold)
        }
    }
}
