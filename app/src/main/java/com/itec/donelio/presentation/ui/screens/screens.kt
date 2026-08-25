package com.itec.donelio.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.itec.donelio.presentation.navigation.NavRoute
import com.itec.donelio.presentation.ui.components.AgriCoreBottomNav
import com.itec.donelio.presentation.ui.screen.campania.DetalleCampaniaScreen
import com.itec.donelio.presentation.ui.screen.campania.FormularioCampaniaScreen
import com.itec.donelio.presentation.ui.screen.campania.GestionCampaniasScreen
import com.itec.donelio.presentation.ui.screen.config.ConfiguracionDBScreen
import com.itec.donelio.presentation.ui.screen.cosecha.CosechasScreen
import com.itec.donelio.presentation.ui.screen.cosecha.FormularioCosechaScreen
import com.itec.donelio.presentation.ui.screen.home.DashboardOperacionesScreen
import com.itec.donelio.presentation.ui.screen.insumo.CatalogoInsumosScreen
import com.itec.donelio.presentation.ui.screen.insumo.FormularioInsumoScreen
import com.itec.donelio.presentation.ui.screen.insumo.InsumosScreen
import com.itec.donelio.presentation.ui.screen.login.LoginScreen
import com.itec.donelio.presentation.ui.screen.login.RegistroScreen
import com.itec.donelio.presentation.ui.screen.observacion.ObservacionesScreen
import com.itec.donelio.presentation.ui.screen.reportes.ReportesRendimientoScreen
import com.itec.donelio.presentation.ui.screen.tarea.NuevaTareaScreen
import com.itec.donelio.presentation.ui.screen.tarea.TareasScreen
import com.itec.donelio.presentation.ui.theme.AgriFondo
import com.itec.donelio.presentation.ui.theme.AgriVerde

@Composable
fun DonElioApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = AgriFondo,
        floatingActionButton = {
            if (currentRoute in listOf(NavRoute.Home.route, NavRoute.Campanias.route, NavRoute.CatalogoInsumos.route, NavRoute.Cosechas.route)) {
                FloatingActionButton(
                    onClick = {
                        val destinoNuevo = when(currentRoute) {
                            NavRoute.Home.route -> NavRoute.FormularioCampania.createRoute()
                            NavRoute.Campanias.route -> NavRoute.FormularioCampania.createRoute()
                            NavRoute.CatalogoInsumos.route -> NavRoute.FormularioInsumo.createRoute()
                            NavRoute.Cosechas.route -> NavRoute.FormularioCosecha.createRoute()
                            else -> NavRoute.NuevaTarea.createRoute()
                        }
                        navController.navigate(destinoNuevo)
                    },
                    containerColor = AgriVerde,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }
            }
        },
        bottomBar = {
            if (currentRoute !in listOf(NavRoute.Login.route, NavRoute.Registro.route)) {
                AgriCoreBottomNav(currentRoute) { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = false
                        }
                        launchSingleTop = true
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .imePadding()
        ) {
            NavHost(
                navController = navController,
                startDestination = NavRoute.Login.route
            ) {
                composable(NavRoute.Login.route) {
                    LoginScreen(
                        onLoginSuccess = { navController.navigate(NavRoute.Home.route) { popUpTo(0) } },
                        onGoToRegister = { navController.navigate(NavRoute.Registro.route) }
                    )
                }
                composable(NavRoute.Registro.route) {
                    RegistroScreen(
                        onRegisterSuccess = { navController.navigate(NavRoute.Home.route) { popUpTo(0) } },
                        onGoToLogin = { navController.popBackStack() }
                    )
                }
                composable(NavRoute.Home.route) {
                    DashboardOperacionesScreen(
                        onGoToConfig = { navController.navigate(NavRoute.ConfiguracionDB.createRoute()) },
                        onGoToDetalle = { campaniaId ->
                            navController.navigate(NavRoute.DetalleCampania.createRoute(campaniaId))
                        },
                        onGoToTareas = { navController.navigate(NavRoute.Tareas.route.replace("?campaniaId={campaniaId}", "")) }
                    )
                }
                composable(
                    route = NavRoute.Campanias.route,
                    arguments = listOf(navArgument("campaniaId") {
                        type = NavType.IntType
                        defaultValue = -1
                    })
                ) { _ ->
                    GestionCampaniasScreen(
                        onGoToDetail = { campaniaIdActual ->
                            navController.navigate(NavRoute.DetalleCampania.createRoute(campaniaIdActual))
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = NavRoute.Tareas.route,
                    arguments = listOf(navArgument("campaniaId") {
                        type = NavType.IntType
                        defaultValue = -1
                    })
                ) { backStackEntry ->
                    val campaniaId = backStackEntry.arguments?.getInt("campaniaId") ?: -1
                    TareasScreen(
                        campaniaId = campaniaId,
                        onGoToNuevaTarea = { navController.navigate(NavRoute.NuevaTarea.createRoute(campaniaId)) },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = NavRoute.DetalleCampania.route,
                    arguments = listOf(navArgument("campaniaId") {
                        type = NavType.IntType
                        defaultValue = -1
                    })
                ) { backStackEntry ->
                    val campaniaId = backStackEntry.arguments?.getInt("campaniaId") ?: -1
                    DetalleCampaniaScreen(
                        onBack = { navController.popBackStack() },
                        onGoToEditar = { id -> navController.navigate(NavRoute.FormularioCampania.createRoute(id)) },
                        onGoToTareas = { id -> navController.navigate(NavRoute.Tareas.createRoute(id)) },
                        onGoToInsumos = { id -> navController.navigate(NavRoute.Insumos.createRoute(id)) },
                        onGoToCosechas = { id -> navController.navigate(NavRoute.Cosechas.createRoute(id)) },
                        onGoToObservaciones = { id -> navController.navigate(NavRoute.Observaciones.createRoute(id)) }
                    )
                }
                composable(
                    route = NavRoute.FormularioCampania.route,
                    arguments = listOf(navArgument("campaniaId") {
                        type = NavType.IntType
                        defaultValue = -1
                    })
                ) {
                    FormularioCampaniaScreen(
                        onBack = { navController.popBackStack() },
                        onGuardadoExitoso = { navController.popBackStack() }
                    )
                }
                composable(
                    route = NavRoute.Cosechas.route,
                    arguments = listOf(navArgument("campaniaId") {
                        type = NavType.IntType
                        defaultValue = -1
                    })
                ) { backStackEntry ->
                    val campaniaId = backStackEntry.arguments?.getInt("campaniaId") ?: -1
                    CosechasScreen(
                        campaniaId = campaniaId,
                        onBack = { navController.popBackStack() },
                        onGoToCampaniaDetalle = { navController.navigate(NavRoute.DetalleCampania.createRoute(campaniaId)) },
                        onEditarCosecha = { cosechaId -> navController.navigate(NavRoute.FormularioCosecha.createRoute(campaniaId, cosechaId)) }
                    )
                }
                composable(
                    route = NavRoute.FormularioCosecha.route,
                    arguments = listOf(
                        navArgument("campaniaId") {
                            type = NavType.IntType
                            defaultValue = -1
                        },
                        navArgument("cosechaId") {
                            type = NavType.IntType
                            defaultValue = -1
                        }
                    )
                ) { backStackEntry ->
                    val campaniaId = backStackEntry.arguments?.getInt("campaniaId") ?: -1
                    FormularioCosechaScreen(
                        campaniaId = campaniaId,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = NavRoute.Insumos.route,
                    arguments = listOf(navArgument("campaniaId") {
                        type = NavType.IntType
                        defaultValue = -1
                    })
                ) { backStackEntry ->
                    val campaniaId = backStackEntry.arguments?.getInt("campaniaId") ?: -1
                    InsumosScreen(
                        campaniaId = campaniaId,
                        onGoToCatalogo = { navController.navigate(NavRoute.CatalogoInsumos.createRoute()) },
                        onGoToCampaniaDetalle = { navController.navigate(NavRoute.DetalleCampania.createRoute(campaniaId)) },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(NavRoute.CatalogoInsumos.route) {
                    CatalogoInsumosScreen(
                        onBack = { navController.popBackStack() },
                        onGoToFormulario = { navController.navigate(NavRoute.FormularioInsumo.createRoute()) }
                    )
                }
                composable(
                    route = NavRoute.FormularioInsumo.route,
                    arguments = listOf(navArgument("insumoId") {
                        type = NavType.IntType
                        defaultValue = -1
                    })
                ) {
                    FormularioInsumoScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(NavRoute.Reportes.route) {
                    ReportesRendimientoScreen(onBack = { navController.popBackStack() })
                }
                composable(
                    route = NavRoute.NuevaTarea.route,
                    arguments = listOf(navArgument("campaniaId") {
                        type = NavType.IntType
                        defaultValue = -1
                    })
                ) { backStackEntry ->
                    val campaniaId = backStackEntry.arguments?.getInt("campaniaId") ?: -1
                    NuevaTareaScreen(
                        campaniaId = campaniaId,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = NavRoute.Observaciones.route,
                    arguments = listOf(navArgument("campaniaId") {
                        type = NavType.IntType
                        defaultValue = -1
                    })
                ) { backStackEntry ->
                    val campaniaId = backStackEntry.arguments?.getInt("campaniaId") ?: -1
                    ObservacionesScreen(
                        campaniaId = campaniaId,
                        onBack = { navController.popBackStack() },
                        onGoToCampaniaDetalle = { navController.navigate(NavRoute.DetalleCampania.createRoute(campaniaId)) }
                    )
                }
                composable(NavRoute.ConfiguracionDB.route) {
                    ConfiguracionDBScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}
