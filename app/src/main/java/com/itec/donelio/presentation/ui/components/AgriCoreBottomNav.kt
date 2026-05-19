package com.itec.donelio.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.itec.donelio.presentation.navigation.NavRoute
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde

@Composable
fun AgriCoreBottomNav(currentRoute: String?, onNavigate: (String) -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(
            selected = currentRoute == NavRoute.Home.route,
            onClick = { onNavigate(NavRoute.Home.route) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AgriVerde, indicatorColor = AgriFondo)
        )
        NavigationBarItem(
            selected = currentRoute == NavRoute.Campanias.route,
            onClick = { onNavigate(NavRoute.Campanias.route) },
            icon = { Icon(Icons.Default.Map, contentDescription = "Campañas") },
            label = { Text("Campañas") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AgriVerde, indicatorColor = AgriFondo)
        )
        NavigationBarItem(
            selected = currentRoute == NavRoute.Tareas.route,
            onClick = { onNavigate(NavRoute.Tareas.route) },
            icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Tareas") },
            label = { Text("Tareas") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AgriVerde, indicatorColor = AgriFondo)
        )
        NavigationBarItem(
            selected = currentRoute == NavRoute.Insumos.route,
            onClick = { onNavigate(NavRoute.Insumos.route) },
            icon = { Icon(Icons.Default.Inventory, contentDescription = "Insumos") },
            label = { Text("Insumos") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AgriVerde, indicatorColor = AgriFondo)
        )
        NavigationBarItem(
            selected = currentRoute == NavRoute.Reportes.route,
            onClick = { onNavigate(NavRoute.Reportes.route) },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Reportes") },
            label = { Text("Reportes") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AgriVerde, indicatorColor = AgriFondo)
        )
    }
}
