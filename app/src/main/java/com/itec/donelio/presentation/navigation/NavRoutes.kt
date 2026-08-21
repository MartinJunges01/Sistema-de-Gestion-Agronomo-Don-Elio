package com.itec.donelio.presentation.navigation

sealed class NavRoute(val route: String) {
    // Estandarización: Todos tienen una función createRoute()

    data object Login : NavRoute("login") { fun createRoute() = route }
    data object Registro : NavRoute("registro") { fun createRoute() = route }
    data object Home : NavRoute("home") { fun createRoute() = route }

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

    data object CatalogoInsumos : NavRoute("catalogo_insumos") { fun createRoute() = route }
    data object FormularioInsumo : NavRoute("formulario_insumo") { fun createRoute() = route }

    data object Cosechas : NavRoute("cosechas?campaniaId={campaniaId}") {
        fun createRoute(campaniaId: Int? = null): String =
            if (campaniaId != null) "cosechas?campaniaId=$campaniaId" else "cosechas"
    }

    data object FormularioCosecha : NavRoute("formulario_cosecha?campaniaId={campaniaId}&cosechaId={cosechaId}") {
        fun createRoute(campaniaId: Int? = null, cosechaId: Int? = null): String {
            val params = mutableListOf<String>()
            if (campaniaId != null) params.add("campaniaId=$campaniaId")
            if (cosechaId != null) params.add("cosechaId=$cosechaId")
            return if (params.isNotEmpty()) "formulario_cosecha?${params.joinToString("&")}" else "formulario_cosecha"
        }
    }

    data object Observaciones : NavRoute("observaciones?campaniaId={campaniaId}") {
        fun createRoute(campaniaId: Int? = null): String =
            if (campaniaId != null) "observaciones?campaniaId=$campaniaId" else "observaciones"
    }

    data object Reportes : NavRoute("reportes") { fun createRoute() = route }
    data object ConfiguracionDB : NavRoute("configuracion_db") { fun createRoute() = route }

    companion object {
        val bottomNavRoutes = listOf(Home, Campanias, Tareas, Insumos, Reportes)
    }
}