package com.itec.donelio.presentation.navigation

sealed class NavRoute(val route: String) {
    data object Login : NavRoute("login")
    data object Registro : NavRoute("registro")
    data object Home : NavRoute("home")
    data object Campanias : NavRoute("campanias?campaniaId={campaniaId}") {
        fun createRoute(campaniaId: Int? = null): String =
            if (campaniaId != null) "campanias?campaniaId=$campaniaId" else "campanias"
    }
    data object DetalleCampania : NavRoute("detalle_campania/{campaniaId}") {
        fun createRoute(campaniaId: Int) = "detalle_campania/$campaniaId"
    }
    data object FormularioCampania : NavRoute("formulario_campania?campaniaId={campaniaId}") {
        fun createRoute(campaniaId: Int? = null): String =
            if (campaniaId != null) "formulario_campania?campaniaId=$campaniaId"
            else "formulario_campania"
    }
    data object Tareas : NavRoute("tareas?campaniaId={campaniaId}") {
        fun createRoute(campaniaId: Int? = null): String =
            if (campaniaId != null) "tareas?campaniaId=$campaniaId" else "tareas"
    }
    data object NuevaTarea : NavRoute("nueva_tarea?campaniaId={campaniaId}") {
        fun createRoute(campaniaId: Int? = null): String =
            if (campaniaId != null) "nueva_tarea?campaniaId=$campaniaId" else "nueva_tarea"
    }
    data object Insumos : NavRoute("insumos?campaniaId={campaniaId}") {
        fun createRoute(campaniaId: Int? = null): String =
            if (campaniaId != null) "insumos?campaniaId=$campaniaId" else "insumos"
    }
    data object CatalogoInsumos : NavRoute("catalogo_insumos")
    data object FormularioInsumo : NavRoute("formulario_insumo")
    data object Cosechas : NavRoute("cosechas?campaniaId={campaniaId}") {
        fun createRoute(campaniaId: Int? = null): String =
            if (campaniaId != null) "cosechas?campaniaId=$campaniaId" else "cosechas"
    }
    data object FormularioCosecha : NavRoute("formulario_cosecha")
    data object Observaciones : NavRoute("observaciones?campaniaId={campaniaId}") {
        fun createRoute(campaniaId: Int? = null): String =
            if (campaniaId != null) "observaciones?campaniaId=$campaniaId" else "observaciones"
    }
    data object Reportes : NavRoute("reportes")
    data object ConfiguracionDB : NavRoute("configuracion_db")

    companion object {
        val bottomNavRoutes = listOf(Home, Campanias, Tareas, Insumos, Reportes)
    }
}
