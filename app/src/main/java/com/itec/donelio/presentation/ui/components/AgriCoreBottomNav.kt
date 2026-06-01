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
            selected = currentRoute?.startsWith(NavRoute.Home.route) == true,
            onClick = { onNavigate(NavRoute.Home.createRoute()) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AgriVerde, indicatorColor = AgriFondo)
        )
        NavigationBarItem(
            selected = currentRoute?.startsWith(NavRoute.Campanias.route.substringBefore("?")) == true,
            onClick = { onNavigate(NavRoute.Campanias.createRoute(null)) },
            icon = { Icon(Icons.Default.Map, contentDescription = "Campañas") },
            label = { Text("Campañas") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AgriVerde, indicatorColor = AgriFondo)
        )
        NavigationBarItem(
            selected = currentRoute?.startsWith(NavRoute.Tareas.route.substringBefore("?")) == true,
            onClick = { onNavigate(NavRoute.Tareas.createRoute(null)) },
            icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Tareas") },
            label = { Text("Tareas") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AgriVerde, indicatorColor = AgriFondo)
        )
        NavigationBarItem(
            selected = currentRoute?.startsWith(NavRoute.Insumos.route.substringBefore("?")) == true,
            onClick = { onNavigate(NavRoute.Insumos.createRoute(null)) },
            icon = { Icon(Icons.Default.Inventory, contentDescription = "Insumos") },
            label = { Text("Insumos") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AgriVerde, indicatorColor = AgriFondo)
        )
        NavigationBarItem(
            selected = currentRoute?.startsWith(NavRoute.Reportes.route) == true,
            onClick = { onNavigate(NavRoute.Reportes.createRoute()) },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Reportes") },
            label = { Text("Reportes") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AgriVerde, indicatorColor = AgriFondo)
        )
    }
}