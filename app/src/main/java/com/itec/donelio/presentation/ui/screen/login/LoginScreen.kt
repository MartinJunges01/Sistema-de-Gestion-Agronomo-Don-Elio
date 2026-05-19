package com.itec.donelio.presentation.ui.screen.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itec.donelio.presentation.ui.theme.AgriVerde
import com.itec.donelio.presentation.ui.theme.TextoSecundario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onGoToRegister: () -> Unit) {
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Surface(shape = RoundedCornerShape(24.dp), color = AgriVerde.copy(alpha = 0.1f), modifier = Modifier.size(100.dp)) { Icon(Icons.Default.Agriculture, contentDescription = null, modifier = Modifier.padding(20.dp), tint = AgriVerde) }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Don Elio", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = AgriVerde)
        Text("Sistema de Gestión Agrícola", color = TextoSecundario, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(48.dp))
        OutlinedTextField(value = usuario, onValueChange = { usuario = it }, label = { Text("Nombre de usuario") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = PasswordVisualTransformation())
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onLoginSuccess, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = AgriVerde), shape = RoundedCornerShape(12.dp)) { Text("Ingresar", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onGoToRegister) { Text("¿No tienes cuenta? Regístrate aquí", color = AgriVerde, fontWeight = FontWeight.SemiBold) }
    }
}
