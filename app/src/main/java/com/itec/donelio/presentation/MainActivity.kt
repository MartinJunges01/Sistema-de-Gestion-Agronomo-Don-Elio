package com.itec.donelio.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.itec.donelio.presentation.ui.screens.DonElioApp
import com.itec.donelio.presentation.ui.theme.DonElioTheme
import com.itec.donelio.presentation.ui.theme.Stone50
import dagger.hilt.android.AndroidEntryPoint

import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.itec.donelio.presentation.viewmodel.MainViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        setContent {
            DonElioTheme {
                val isLoggedIn by mainViewModel.isLoggedIn.collectAsState()
                
                if (isLoggedIn == null) {
                    // Splash screen lógico (pantalla en blanco mientras carga Datastore)
                    Box(modifier = Modifier.fillMaxSize())
                } else {
                    DonElioApp(isLoggedIn = isLoggedIn!!)
                }
            }
        }
    }
}
