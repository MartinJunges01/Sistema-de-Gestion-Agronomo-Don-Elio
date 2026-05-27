# Changelog

**[2026-05-26] - Sistema de Recordatorios de Tareas (F5/Issue4)**
- Implementación de recordatorios de tareas en segundo plano usando `WorkManager`.
- Creado `TareaReminderWorker` y `WorkManagerTaskReminderScheduler` para encolar notificaciones.
- Añadido Notification Channel `"tareas_channel"` en `DonElioApplication`.
- Modificada la base de datos (`TareaDao` y `TareaRepository`) para retornar el ID de inserción.
- Actualizados `CrearTareaUseCase`, `EditarTareaUseCase`, `ConfirmarTareaUseCase` y `EliminarTareaUseCase` para orquestar la creación y cancelación de notificaciones programadas.

**[2026-05-26] - Configuración de Permisos de Notificaciones (F5/Issue3)**
- Se añadió el permiso `<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />` al `AndroidManifest.xml` (necesario para Android 13+).
- Implementada la solicitud nativa en tiempo de ejecución en `DashboardOperacionesScreen` a través de `rememberLauncherForActivityResult` de Compose. 

**[2026-05-26] - Implementación de Imágenes en Observaciones (F5/Issue2)**
- Añadida librería Coil para carga asíncrona de imágenes (`AsyncImage`).
- Configurado `FileProvider` y agregado permisos de cámara en `AndroidManifest.xml`.
- Modificada `ObservacionesScreen` con botones "Cámara" y "Galería" utilizando `rememberLauncherForActivityResult`.
- Modificado `FormularioObservacionViewModel` para copiar la imagen seleccionada al almacenamiento interno de la app antes de guardar la URI en la BD, previniendo la rotura de enlaces si el usuario borra la foto de la galería original.
- Actualizadas las tarjetas de la lista de observaciones para renderizar la foto en miniatura.

**[2026-05-26] - Corrección de Soft-Delete en Catálogo de Insumos**
- Añadido campo `activo` a la tabla `insumos` (`InsumoEntity`) y al modelo de dominio `Insumo`.
- Refactorizado `InsumoDao` para usar soft-delete (`UPDATE insumos SET activo = 0`) al eliminar del catálogo.
- Modificada la query `getCatalogoInsumos` para filtrar por `activo = 1`.
- Incrementada la versión de Room DB a 3 para aplicar los cambios en el esquema.
- Solucionado el bug crítico donde eliminar un insumo del catálogo borraba en cascada el historial de las campañas pasadas.

**[2026-05-25] - Actualización de Roadmap y Botón Invitado**
- Actualización de `.context/RoadmapOP.md` con issues finalizados de fase 8, 10 y 11.
- Añadido botón "Invitado" para debug en la pantalla de login (F8/Issue 1.8).

**[2026-05-25] - Finalización de requerimientos fase 2**
- Implementado swipe semanal para gestión visual de Tareas.
- Implementado catálogo de Insumos con íconos e integración a base de datos.
- Integrado YCharts para gráficos de pie en Dashboard de Reportes.
- Añadido soporte de Soft-Delete (activo) en vinculación de Insumos.
- Forzada versión de Room DB a 2 con migración destructiva (entorno dev).
- Añadida DataSeed con iconos e items eliminados para pruebas de UI.
- Solucionados errores WorkerDaemon configurando gradle.user.home en entorno local.
- Actualizados Roadmap y documentación de Arquitectura.

**[2026-05-20] - Integración de 20 issues de auditoría en RoadmapOP.md**
- Fusionados los 20 issues detectados en auditoría de código dentro del `RoadmapOP.md` como Fases 8-12, organizados por criticidad.
- Agregadas notas de referencia cruzada y de dependencia entre issues.
- Eliminado `.context/IssuesPendientes.md` (contenido migrado a RoadmapOP.md).

**[2026-05-19] - Implementar autenticación, refactor Clean Arch y conectar Use Cases muertos**
- **Issue 1 (Login completo):** Creación de `UsuarioDao`, modelo de dominio `Usuario`, mappers, `LoginUseCase` (SHA-256), `RegistroUseCase` y `LoginViewModel`. Conexión de `LoginScreen` y `RegistroScreen`.
- **Issue 12 (Refactor Clean Arch):** Creación de 6 UseCases contenedores para queries reactivas. Refactorización de 6 ViewModels para inyectar UseCases en lugar de repositorios (`CampaniaFormViewModel`, `CampaniaDetailViewModel`, `TareaViewModel`, `CosechaViewModel`, `InsumoVinculacionViewModel` y `ObservacionViewModel`).
- **Issue 13 (Use Cases muertos):** Conexión de `EditarTareaUseCase`, `EliminarTareaUseCase`, `EditarInsumoCatalogoUseCase` y creación de `EliminarInsumoCatalogoUseCase`. Diálogo de edición inline en `CatalogoInsumosScreen`.

**[2026-05-18] - Refactor de Gestión de Campañas (F4/Issue9)**
- Creación de `GestionCampaniasViewModel` con carga reactiva de campañas desde `ObtenerCampaniasUseCase`.
- Creación de `GestionCampaniasScreen` reemplazando `GestionParcelasScreen` (mock) con lista real desde BD.
- Corrección de navegación: `onGoToDetail` ahora recibe `campaniaId` real del item clickeado.
- Estado vacío con icono e indicación visual para crear campaña.

**[2026-05-18] - Implementación de Módulo de Observaciones (F4/Issue8)**
- Creación de `ObservacionViewModel` con carga reactiva de observaciones por campaña desde BD.
- Creación de `FormularioObservacionViewModel` con formulario reactivo, validación y conexión a `GuardarObservacionUseCase`.
- Rediseño de `ObservacionesScreen` con formulario para guardar + listado reactivo de observaciones registradas.
- Actualización de `TabObservaciones` en `DetalleCampaniaScreen` con ViewModel por campaña y últimas 3 observaciones.

**[2026-05-18] - Implementación completa CosechaNoAlmacenada (Venta/Reserva)**
- Creación de `CosechaNoAlmacenadaDao`, modelo de dominio `CosechaNoAlmacenada`, repositorio e implementación.
- Creación de `RegistrarCosechaConVentaUseCase` que inserta cosecha base + detalle de venta/reserva.
- Exposición del DAO en `DonElioDatabase` y DI en `DatabaseModule`/`RepositoryModule`.
- Mappers `toDomain()`/`toEntity()` para `CosechaNoAlmacenadaEntity`.
- `CosechaRepository.insertCosecha()` ahora retorna `Long` (ID generado).
- `CosechaViewModel` ampliado: `almacenadas` (filtrado) y `noAlmacenadasDetalle` (mapa id→detalle).
- `FormularioCosechaViewModel.guardar()` bifurca entre `RegistrarCosechaUseCase` y `RegistrarCosechaConVentaUseCase` según checkbox.
- `CosechasScreen` muestra tipo y precio en cards de venta/reserva.
- `TabCosechas` en `DetalleCampaniaScreen` con key única por campaña y resumen real de ventas.

**[2026-05-18] - Implementación de Módulo de Cosechas (F4/Issue7)**
- Creación de `CosechaViewModel` con carga reactiva de cosechas por campaña desde BD.
- Creación de `FormularioCosechaViewModel` con formulario reactivo, validación y conexión a `RegistrarCosechaUseCase`.
- Refactorización de `CosechasScreen` con datos reales, separación visual almacenadas/no-almacenadas.
- Refactorización de `FormularioCosechaScreen` con ViewModel, DatePicker, validación de cantidad y spinner de guardado.
- Agregado parámetro `campaniaId` opcional a `NavRoute.FormularioCosecha`.
- Actualización de `TabCosechas` en `DetalleCampaniaScreen` con datos reales desde BD.

**[2026-05-15] - Seed data para testing (debug source set)**
- Configuración de `sourceSets { debug { java.srcDir("src/debug/java") } }` en `app/build.gradle.kts`.
- Creación de interfaz `DataSeeder` en `src/main/` con `@BindsOptionalOf` para inyección opcional en Hilt.
- Creación de `DataSeederImpl` en `src/debug/` con 4 campañas, 8 insumos, 8 tareas, 3 cosechas, 5 vinculaciones y 4 observaciones con fechas fijas mediante `Calendar`.
- Creación de `SeedModule` en `src/debug/` proveyendo `DataSeederImpl` vía Hilt.
- Creación de `ConfiguracionDBViewModel` con estado `SeedState` (Idle/Cargando/Exito/Error) y método `cargarDatosPrueba()`.
- Botón "Cargar datos de prueba" en `ConfiguracionDBScreen` visible solo en builds debug, con spinner y Snackbar de feedback.
- Actualización de `.context/RoadmapOP.md` con Issue 10 de Fase 4.

**[2026-05-15] - Implementación de Módulo de Insumos (F4/Issue6)**
- Creación de `InsumoCatalogoViewModel` e `InsumoVinculacionViewModel` con carga reactiva desde BD.
- Conexión de `CatalogoInsumosScreen` al catálogo real con `ObtenerCatalogoInsumosUseCase`.
- Conexión de `FormularioInsumoScreen` a `CrearInsumoCatalogoUseCase` con validación y spinner.
- Refactorización de `InsumosScreen` (vinculación) con datos reales, cálculo `cantidad × precio` formateado y atajo "Crear nuevo insumo" si no existe en catálogo.
- Creación de `FormularioInsumoViewModel` con estado reactivo.
- Actualización de `TabInsumos` en `DetalleCampaniaScreen` con conteo real y total estimado.

**[2026-05-15] - Implementación de Módulo de Tareas (F4/Issue5)**
- Creación de `TareaViewModel` con carga reactiva de tareas por campaña desde BD.
- Creación de `NuevaTareaViewModel` con formulario reactivo, validación y conexión a `CrearTareaUseCase`.
- Refactorización de `TareasScreen` con datos reales, checkbox de confirmación con `ConfirmarTareaUseCase`, feedback visual (tachado + atenuado).
- Refactorización de `NuevaTareaScreen` con `DatePickerDialog` M3, validación de nombre y spinner de guardado.
- Actualización de `TabTareas` en `DetalleCampaniaScreen` con lista real de pendientes y resumen.
- Actualización de `NavRoute.NuevaTarea` con `campaniaId` opcional.

**[2026-05-14] - Correcciones de bugs y navegación (F4/Issue4)**
- Bugfix: `CrearCampaniaUseCase` ahora acepta parámetro `cultivo` — el campo ya no se pierde al crear campañas nuevas.
- Bugfix: `CampaniaFormViewModel` pasa `cultivo` al `crearCampaniaUseCase`.
- Bugfix: `GestionParcelasScreen`, `TareasScreen`, `InsumosScreen`, `CosechasScreen`, `ObservacionesScreen` ya no hardcodean `campaniaId=1` — todas las rutas aceptan `campaniaId` opcional y lo propagan correctamente.
- Limpieza: eliminado parámetro `onEditar` no usado en `HeaderCampania`.

**[2026-05-14] - Pantalla Detalle de Campaña con Tabs y encabezado fijo (F4/Issue4)**
- Creación de `CampaniaDetailViewModel` con `SavedStateHandle` para carga de campaña por ID + eliminación.
- Rediseño de `DetalleCampaniaScreen` con TopAppBar dinámico, encabezado fijo (nombre, cultivo, fechas, estado) y TabRow con 5 tabs: Info, Tareas, Insumos, Cosechas, Observaciones.
- Cada tab muestra resumen informativo y botón de navegación a su pantalla completa, pasando `campaniaId`.
- Actualización de `screens.kt` con `navArgument("campaniaId")` extraído y pasado al ViewModel.
- Navegación desde detalle a edición de campaña (`onGoToEditar`) con el ID correcto.

**[2026-05-14] - Implementación de Formulario ABM Campañas con validación y DatePicker (F4/Issue3)**
- Creación de `CampaniaFormViewModel` con `SavedStateHandle` para modo edición/creación.
- Refactorización de `FormularioCampaniaScreen` con campos nombre/cultivo validados, DatePicker M3, botón guardar con spinner.
- Actualización de `NavRoute.FormularioCampania` con `campaniaId` opcional vía query param.
- Integración de `CrearCampaniaUseCase` (creación) y `EditarCampaniaUseCase` (edición) con `LaunchedEffect` para navegación post-guardado.

**[2026-05-14] - Refactor: división de screens.kt en archivos individuales**
- Separación de 15 pantallas en archivos por feature (login, home, campania, tarea, cosecha, insumo, observacion, reportes, config).
- Extracción de colores a `theme/AgriCoreColors.kt`.
- Componentes compartidos movidos a `components/` (6 archivos).
- Navegación migrada a `navigation/NavRoutes.kt` con sealed class `NavRoute`.
- Simplificación de la ruta `FormularioCampania` (sin parámetro opcional).

**[2026-05-14] - Implementación de HomeViewModel y Dashboard reactivo (F4/Issue2)**
- Creación de `HomeViewModel` con inyección de `ObtenerCampaniasUseCase`.
- Refactorización de `DashboardOperacionesScreen` para consumir datos reales desde BD.
- Lista reactiva de campañas con navegación al detalle por ID.
- Estado vacío con indicación visual para crear una nueva campaña.

**[2026-05-14] - Migración a Navigation Compose y Scaffold global (F4/Issue1)**
- Creación de `NavRoute` (sealed class) reemplazando enum `Destino`.
- Migración de navegación manual (lista/pila) a `NavHost` + `NavController`.
- Configuración de BottomNavigationBar con preservación de estado por pestaña.
- Eliminación de `BackHandler` manual (delegado al NavController).
- Definicición de rutas con parámetros (`DetalleCampania`, `FormularioCampania`).

**[2026-05-12] - Implementación de Casos de Uso (Campañas y Tareas) - F3/Issue4**
- Creación de `CrearCampaniaUseCase`, `EditarCampaniaUseCase`, `EliminarCampaniaUseCase` y `ObtenerCampaniasUseCase`.
- Cada Use Case con `@Inject constructor` y validación de nombre no vacío.
- Creación de `CrearTareaUseCase`, `EditarTareaUseCase`, `EliminarTareaUseCase` y `ConfirmarTareaUseCase`.

**[2026-05-14] - Implementación de Resource<T> y manejo de errores en Use Cases**
- Creación de `Resource<T>` en `domain/model/` con extensiones `onSuccess`, `onError`, `isSuccess`, `isError`.
- Refactorización de 7 Use Cases para retornar `Flow<Resource<Unit>>` con emisión de Loading, Success y Error.
- Manejo de excepciones con try/catch y ejecución en `Dispatchers.IO` mediante `flowOn`.

**[2026-05-14] - Corrección de mapeo Campania, unificación de nomenclatura e implementación de Use Cases faltantes**
- Corregido mapeo bidireccional `Campania` ↔ `CampaniaEntity`: agregado `cultivo` al modelo de dominio y `estaActiva` a la entidad; eliminados hardcodeos en `Mappers.kt`.
- Renombrado `campaniaId` → `idCampania` en `TareaRepository`, `CosechaRepository` y sus implementaciones.
- Creados modelos de dominio `Observacion` y `CampaniaInsumo` para mantener la pureza de la capa domain.
- Creados `CampaniaInsumoRepository` y `ObservacionRepository` con sus implementaciones y bindings de Hilt.
- Agregados mappers para `ObservacionEntity` ↔ `Observacion` y `CampaniaInsumoEntity` ↔ `CampaniaInsumo`.
- Implementados 6 casos de uso: `RegistrarCosechaUseCase`, `CrearInsumoCatalogoUseCase`, `EditarInsumoCatalogoUseCase`, `ObtenerCatalogoInsumosUseCase`, `AsignarInsumoACampaniaUseCase`, `GuardarObservacionUseCase`.

**[2026-05-12] - Card campaña activa en Tareas/Cosechas/Observaciones + botón exportar en Reportes + diagrama de flujo**
- TareasScreen, CosechasScreen y ObservacionesScreen: añadida `CampanaSeleccionadaCard` de la campaña activa.
- ReportesRendimientoScreen: añadido botón de exportar (Excel/PDF) en TopAppBar con `DropdownMenu`.
- Creado `docs/FLOW.md` con diagrama Mermaid de navegación y tabla de cobertura de Casos de Uso.

**[2026-05-12] - Refactor de navegación global, módulo de insumos y reportes**
- BottomNav: añadido acceso directo a `Destino.Insumos`; renombrado "Agenda" → "Tareas" y "Parcelas" → "Campañas".
- Home: `CampanaSeleccionadaCard` ahora navega a `DetalleCampania`; botón + navega a `FormularioCampania`.
- InsumosScreen: reemplazado formulario inline por `ModalBottomSheet` con buscador, selector cantidad/precio y botón "Agregar al catálogo".
- FormularioInsumoScreen: simplificado a solo campos Nombre, Categoría y Unidad.
- ReportesRendimientoScreen: añadidas tarjetas de métricas comparativas (Rendimiento, Ganancias, Costos, Insumos); selector dropdown para comparar dos campañas; gráficos Canvas de evolución mensual (Costos/Insumos) con leyenda bicolor.

**[2026-05-12] - Inicialización de documentación de seguimiento**
- Creación de `CHANGELOG.md` en la raíz para el seguimiento de tareas.
- Ajuste de `donelioOP.md` para referenciar `.context/RoadmapOP.md`.

**[2026-05-11] - Avance en Fase 3 (Capa de Dominio)**
- Definición de modelos de dominio (`data class` puros).
- Implementación de `Mappers.kt`.
- Creación de interfaces de repositorios (`CampaniaRepository`, `TareaRepository`, etc.).
- Implementación base de los repositorios en la capa `data`.

**[2026-05-10] - Finalización de Fase 1 y Fase 2**
- Configuración inicial del proyecto, dependencias y estructura de Clean Architecture.
- Implementación completa de la capa de datos: Entidades Room, TypeConverters y DAOs.
- Configuración de Dagger-Hilt para inyección de dependencias.
