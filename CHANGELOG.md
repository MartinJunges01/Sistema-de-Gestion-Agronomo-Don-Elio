# Changelog

**[2026-08-25] - [#334] fix(insumos): Creación de insumos en el catálogo**
- **ViewModel:** Se corrigió la lectura del `insumoId` en `FormularioInsumoViewModel` para que un valor de `-1` no se trate como edición, habilitando correctamente el flujo de creación.
- **Rama:** `fix/issue-334-creacion-insumos`

**[2026-08-12] - [#304] fix(ux): Pantalla no se desplaza al escribir (IME padding global)**
- **UI:** En `screens.kt`, se aplicó el modificador `imePadding()` al contenedor principal dentro del `Scaffold` para que el espaciado reaccione al teclado virtual de forma automática.
- **UI:** Este ajuste resuelve globalmente el solapamiento del teclado en todos los formularios de la app.
- **Rama:** `fix/ime-padding-formularios` (stacked sobre `fix/bloquear-modo-oscuro`)

**[2026-08-12] - [#303] fix(ux): Bloquear Modo Oscuro (Forzar Tema Claro)**
- **UI:** En `Theme.kt`, se modificó `DonElioTheme` para que el parámetro `darkTheme` siempre sea `false` por defecto, ignorando el setting del sistema.
- **UI:** Se forzó `isAppearanceLightStatusBars = true` para asegurar que los iconos de la barra de estado siempre sean oscuros.
- **Rama:** `fix/bloquear-modo-oscuro`

**[2026-08-11] - [#294] feat(observaciones): Edición y eliminación de observaciones**
- **Dominio:** Se crearon `EditarObservacionUseCase` y `EliminarObservacionUseCase`.
- **ViewModels:** Se inyectaron los nuevos casos de uso en `ObservacionViewModel` para gestionar las acciones y los errores, exponiéndolos como estado.
- **UI:** Se agregaron íconos de editar y eliminar a cada `ObservacionCard` en `ObservacionesScreen`.
- **UI:** Se implementaron diálogos modales (AlertDialog) para confirmar la eliminación y para editar el texto de la observación in-place.
- **Rama:** `feat/issue-294-edicion-observaciones`

**[2026-08-11] - [#291] fix(tareas): Selector de hora usa TimeInput en vez de texto libre**
- **ViewModels:** `NuevaTareaViewModel` ahora valida que la hora no esté vacía y que cumpla el formato regex (HH:mm), exponiendo `errorHora`.
- **UI:** En `NuevaTareaScreen` se reemplazó el `OutlinedTextField` genérico por un `TimeInput` nativo de Material 3 contenido dentro de un `AlertDialog`, previniendo el ingreso de texto arbitrario.
- **Rama:** `fix/issue-291-timepicker-hora`

**[2026-08-11] - [#285] fix(dashboard): Tareas interactivas y filtradas por vencimiento**
- **DAO/Dominio:** Actualizada la consulta `getTareasPendientesGlobales` para recibir `fechaLimite` y omitir tareas vencidas hace más de 7 días.
- **ViewModels:** `HomeViewModel` ahora calcula dinámicamente la `fechaLimite` y la pasa al `ObtenerTareasPendientesUseCase`.
- **UI:** Las tarjetas de "Tareas Próximas" ahora son clickeables (navegan al detalle de la campaña asociada).
- **UI:** Tratamiento visual condicional: tareas recientes vencidas se muestran con color rojo tenue.
- **UI:** Se agregó el botón "Ver todas" que redirige a la lista completa de tareas de la app.
- **Rama:** `fix/issue-285-dashboard-tareas`

**[2026-08-11] - [#287] fix(login): Saludo muestra nombre de usuario en vez de Invitado**
- **ViewModels:** `LoginViewModel` inyecta ahora `SessionManager` y luego del inicio de sesión persistirá en DataStore el nombre real del usuario recibido del backend.
- **Rama:** `fix/issue-287-saludo-usuario`

**[2026-08-11] - [#290] fix(campanias): Validación estricta de fechas pasadas en creación**
- **Dominio:** 
  - Creado `ValidarDatosCampaniaUseCase` para concentrar la lógica de validación (nombre, cultivo y control estricto de no permitir fechas anteriores a hoy, ignorando la regla en modo edición).
  - Añadida capa extra de defensa en `CrearCampaniaUseCase` para lanzar excepción si la fecha es menor a hoy (medianoche).
- **ViewModels:** `CampaniaFormViewModel` limpiado completamente. Toda su lógica condicional fue delegada al nuevo caso de uso, dedicándose exclusivamente a actualizar la UI.
- **UI:** En `FormularioCampaniaScreen`, se configuró `selectableDates` en el `rememberDatePickerState` para deshabilitar visualmente fechas anteriores a hoy, mejorando sustancialmente la UX.
- **Rama:** `fix/campanias-validacion-fechas`

**[2026-08-11] - [#289] fix(insumos): Validación de Formulario y Delegación a Dominio**
- **Dominio:** Creado `ValidarInsumoUseCase` para evaluar la obligatoriedad de `nombre` y `categoría`. Nota: El campo `unidad` no fue incluido en la validación porque no existe en la arquitectura actual del proyecto.
- **ViewModels:** 
  - `FormularioInsumoViewModel` modificado para consumir el caso de uso y exponer un estado único `isGuardarHabilitado`.
  - `InsumoCatalogoViewModel` modificado para inyectar el caso de uso y exponer una función de delegación de validación.
- **UI:** 
  - `FormularioInsumoScreen` muestra mensajes de error en los campos basándose enteramente en el estado unificado, eliminando lógica de negocio visual.
  - `CatalogoInsumosScreen` refactorizado para el diálogo inline y agregado un `SnackbarHost` para observar errores del ViewModel.
- **Rama:** `fix/insumos-validacion-formulario`

**[2026-08-02] - [#302] feat(reportes): Implementar Comparación Real entre Campañas**
- **Dominio:** `ReportesViewModel` ahora expone `cosechasA` y `cosechasB` asociadas a las campañas seleccionadas en el comparador.
- **UI:** En `ReportesRendimientoScreen`, la sección de "Métricas Comparativas" ahora muestra los verdaderos totales de Costo de Insumos y Rendimiento (Cosechas) para la Campaña A y la Campaña B.
- **UI:** Se reemplazó el `GraficoEvolucionPlaceholder` por un `DoubleBarIndicator`, que consiste en barras de progreso compuestas (Jetpack Compose) para representar visual y proporcionalmente la diferencia de Costos y Rendimiento entre ambas campañas seleccionadas.
- **Rama:** `feat/comparacion-campanias` (stacked sobre `feat/grafico-desglose-cosechas`)
- **Dominio y UI:** Agregado el estado `desgloseCosechasData` al `ReportesViewModel` que filtra y agrupa dinámicamente el listado de cosechas en base a su destino (Almacenada vs Vendida/Reservada).
- **UI:** Añadido un nuevo gráfico `PieChart` en `ReportesRendimientoScreen` para visualizar visualmente las proporciones del destino de las cosechas de la campaña activa.
- **Tests:** Creado caso de prueba en `ReportesViewModelTest` para asegurar la correcta agrupación matemática de las cosechas.
- **Rama:** `feat/grafico-desglose-cosechas` (stacked sobre `feat/reporte-insumos-mejorado`)
- **Exportación:** El exportador (`ReportExporter`) ahora recibe y pinta el nombre de la campaña en los archivos CSV y PDF generados. El nombre del archivo sugerido en el `FilePicker` ahora incluye el nombre de la campaña.
- **Validación UI:** Se agregó una guardia en `ReportesRendimientoScreen` que verifica si hay una campaña seleccionada antes de abrir el `FilePicker`, mostrando un `Toast` si es `null`.
- **Rama:** `feat/reporte-insumos-mejorado` (stacked sobre `feat/migracion-db-insumos`)
- **Base de Datos:** Migración a versión 5 (`MIGRATION_4_5`) usando copias de tabla temporales para eliminar la columna `unidad` de Insumos y Cosechas (limitación de SQLite).
- **Dominio y UI:** Eliminación del campo `unidad` explícito en todo el código; se asume Kg/L de manera implícita para simplificar el modelo y la UI.
- **Tests actualizados** para no requerir o asertar por el campo `unidad`.
- **Rama:** `feat/migracion-db-insumos` (stacked sobre `feat/campanas-historial`)

**[2026-07-29] - [#299] fix(reportes): Eliminar datos mockeados en Dashboard y reestructurar pantalla Reportes**
- **Dashboard (`DashboardOperacionesScreen.kt`):** Eliminadas las tarjetas hardcodeadas "Clima 24°C" y "Salud Lotes 90% Óptimo". El contenido restante sube automáticamente.
- **`ReportesViewModel.kt` reescrito:** Se reemplaza `ObtenerTodosLosInsumosVinculadosUseCase` por `ObtenerInsumosVinculadosUseCase(campaniaId)` contextual. Se inyectan `ObtenerCampaniasUseCase` y `ObtenerCosechasPorCampaniaUseCase`. Nuevos StateFlows: `campanias`, `campaniaIndividual`, `insumosIndividual`, `cosechasIndividual`, `campaniaA/B`, `insumosA/B`. `pieChartData` y `exportableData` ahora son contextuales a la campaña seleccionada.
- **`ReportesRendimientoScreen.kt` reestructurada en dos secciones:**
  - *Sección 1 — Estadísticas individuales:* Dropdown con campañas reales de BD, tarjetas de costo de insumos y total cosechado, PieChart contextual (por campaña seleccionada).
  - *Sección 2 — Comparador:* Dos dropdowns con campañas reales, `CardMetricaComparativa` con costo real de insumos A vs B, placeholder para gráfico de evolución (scope #302).
- **Exportación CSV/PDF:** Ahora exporta los insumos de la campaña seleccionada en Sección 1 (en lugar de todos los insumos globales).
- **Tests creados:** `ReportesViewModelTest.kt` con 5 casos Given-When-Then (JUnit 4 + MockK + Turbine).
- **`docs/plan_de_pruebas.md` actualizado** con subsección `ReportesViewModel — StateFlows contextuales [#299]`.
- **Nota de scope:** La lógica de `campaniaA/B` e `insumosA/B` es un paso preparatorio del Issue #302. Documentado en la PR con `Partial-scope: #302`.
- **Rama:** `fix/datos-mock-dashboard-reportes` (stacked sobre `fix/tab-tareas-no-actualiza`)

**[2026-07-22] - [#292] fix(campania): Pestaña Tareas no actualiza datos al cambiar de campaña**
- **Causa raíz doble resuelta:**
  - `TabTareas` usaba `hiltViewModel(key = "tab_tareas")` con key estática, haciendo que Hilt reutilizara la misma instancia del `TareaViewModel` sin importar la campaña activa.
  - El `campaniaId` recibido como parámetro en `TabTareas` nunca se propagaba al ViewModel (que iniciaba con `null` desde `SavedStateHandle`).
- **`TareaViewModel.kt` modificado:** Se agrega el método público `sincronizarCampania(id: Int)` que actualiza `_campaniaIdSeleccionada` solo si el valor difiere del actual (idempotente, evita emisiones innecesarias en el StateFlow).
- **`DetalleCampaniaScreen.kt` modificado:**
  - `TabTareas`: key cambiada a `"tab_tareas_$campaniaId"` + `LaunchedEffect(campaniaId)` que invoca `sincronizarCampania()` como segunda línea de defensa.
  - `TabInsumos`: key corregida de `"tab_insumos"` a `"tab_insumos_$campaniaId"` (mismo patrón de bug identificado).
- **Tests creados:** `TareaViewModelTest.kt` con 5 casos Given-When-Then (JUnit 4 + MockK + Turbine).
- **`docs/plan_de_pruebas.md` actualizado** con subsección `TareaViewModel — sincronizarCampania() [#292]`.
- **Rama:** `fix/tab-tareas-no-actualiza` (stacked sobre `fix/permiso-camara-observaciones`)

**[2026-06-30] - [#283] fix: Crash al Abrir la Cámara — Permiso CAMERA no Solicitado**
- **Causa raíz resuelta:** La app lanzaba `cameraLauncher.launch(uri)` directamente sin verificar ni solicitar el permiso `CAMERA` en runtime, causando un `SecurityException` en Android 6.0+ (API 23).
- **Nuevo módulo creado:** `presentation/util/CameraUtils.kt` con tres responsabilidades separadas:
  - `EstadoPermisoCamara`: State holder observable con `mutableStateOf` para `permisoConcedido`, `mostrarRazon` y `denegadoPermanente`.
  - `recordarPermisoCamara()`: Composable que gestiona el ciclo completo del permiso usando `ActivityResultContracts.RequestPermission()` y `ActivityCompat.shouldShowRequestPermissionRationale()` para distinguir denegación temporal vs. permanente.
  - `DialogoRazonPermisoCamara()`: `AlertDialog` de rationale que se muestra en primera denegación.
  - `abrirAjustesPermiso()`: Helper que lanza `Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)` cuando el permiso es denegado permanentemente.
- **`ObservacionesScreen.kt` actualizado:** Botón "Tomar foto" ahora verifica `controlPermiso.permisoConcedido` antes de lanzar la cámara. Si no está concedido, guarda la acción pendiente y llama a `controlPermiso.solicitar()`. `SnackbarHost` añadido al `Box` para feedback visual.
- **Flujos cubiertos:** Permiso ya concedido (directo a cámara) · Primera denegación (muestra rationale) · Denegación permanente (Snackbar con botón "Abrir Ajustes").
- **Rama:** `fix/permiso-camara-observaciones`

**[2026-06-10] - Cosechas: Fix Crash FK [#284] y Validación del Formulario [#293]**
- **Issue 7 (#284):** Eliminado el crash `FOREIGN KEY constraint failed` al guardar una cosecha con `campaniaId = -1`. `FormularioCosechaViewModel` ahora inicializa `campaniaId` como `null` cuando `SavedStateHandle` no recibe un id válido (`takeIf { it != -1 }`), inyecta `ObtenerCampaniasUseCase` para exponer `campanias` y `onCampaniaChange()`, y `guardar()` valida `campaniaId == null` emitiendo `errorCampania = "Debe seleccionar una campaña"` antes de intentar la inserción.
- **Issue 7 (UI):** `FormularioCosechaScreen` ahora muestra el componente `SelectorCampania` (etiqueta "Campaña vinculada") con texto de error debajo cuando falta seleccionar campaña. El botón "Guardar" queda deshabilitado mientras `campaniaId == null`.
- **Issue 12 (#293):** `guardar()` ya no hace retorno silencioso con campos vacíos: setea `errorCantidad = "La cantidad es obligatoria"`. Adaptado a migración DB v5 (campo `unidad` eliminado del modelo).
- **Issue 12 (UI):** Botón "Guardar" deshabilitado si hay errores o campos obligatorios vacíos (`cantidad`, `campaniaId`).
- **Testing:** Creado `FormularioCosechaViewModelTest` con 5 casos (MockK + coroutines-test): sin campaña, cantidad vacía, almacenado válido y venta válida.
- **Documentación:** Marcados como completos Issues #284 y #293 en `.context/roadmap_iteracion_2.md`; agregados escenarios Given-When-Then en `docs/plan_de_pruebas.md`.
- **Rama:** `fix-cosechas-estabilizacion`

**[2026-06-23] - Documentación de Entrega y Casos de Uso**
- Actualización de `docs/FLOW.md` incorporando diagramas de flujo interactivos Mermaid para cada una de las 8 ramas principales del sistema.
- Creación de `docs/diferencias_casos_de_uso_2025_2026.md` contrastando la propuesta teórica original (2025) con la implementación final en Clean Architecture (2026), aplicando el formato tabular de casos de uso requerido en la cursada.

**[2026-06-09] - Planificación y División de Iteración 2**
- Actualización de `docs/bugs_identificados.md` refinando Issues 8, 15, 18, 19, 20 en relación al rediseño lineal, validación de insumos con Flow y unificación a Toneladas.
- Creación de `.context/iteracion_2.md` con el roadmap maestro priorizado de L1 a L5.
- Creación de `docs/roadmap_desarrolladores.md` organizando las tareas para ejecución en paralelo por 3 desarrolladores, con un desglose granular de ramas Git y orden de ejecución.

**[2026-06-09] - Sesión de Pruebas Manuales APK Debug — Documentación de 23 Issues**
- Reescritura completa de `docs/bugs_identificados.md` con 23 issues organizados por severidad (L1-L5).
- **L1 (Crashes):** Crash por permisos de cámara no solicitados (Issue 6), crash por FK constraint al registrar cosecha sin campaniaId (Issue 7).
- **L2 (Bugs Funcionales):** Saludo siempre muestra "Invitado" (Issue 3 actualizado), catálogo de insumos sin validación completa (Issue 8), campañas permiten fechas pasadas (Issue 9), campo hora de tareas sin restricciones (Issue 10), tabs tareas/insumos no se actualizan al cambiar de campaña (Issue 11), validación faltante en formulario cosechas (Issue 12).
- **L3 (Features Faltantes):** Edición/eliminación de observaciones (Issue 13) y cosechas (Issue 14), separación campañas activas/inactivas (Issue 15), navegación lateral entre campañas (Issue 16), campo hectáreas en cosecha (Issue 17).
- **L4 (Reportes):** Selector de campaña en gráfico de insumos (Issue 18), gráfico desglose cosechas (Issue 19), comparación real entre campañas (Issue 20).
- **L5 (UX):** Bloquear modo oscuro (Issue 21), teclado cubre campos al escribir (Issue 22), tarjetas mock del dashboard (Issue 23).

**[2026-06-09] - Generación de APK de Debug para Pruebas**
- Se generó el archivo APK en versión de depuración (debug) mediante Gradle para facilitar las pruebas manuales en dispositivos físicos.

**[2026-06-04] - Fase 12: Sincronización, Tests y Refactor (Issue 12.2)**
- **Roadmap:** Sincronizados y marcados como completos los Issues silentes de permisos, exportación/importación de base de datos, BottomNav y Use Cases.
- **Tests Instrumentados:** Diagnosticados y programados para solución los errores de compilación de DAOs (`CampaniaDaoTest` y `CampaniaInsumoDaoTest`) que fallaban por nomenclaturas antiguas.
- **Refactor:** Añadida la tarea para limpiar las importaciones comodín (`*`) a lo largo del proyecto para apegarse a las mejores prácticas de Kotlin.

**[2026-06-04] - Hotfix: Corrección de compilación y rebase de PR**
- **Fix:** Corregido error de compilación en `ReportesViewModel.kt` causado por una importación faltante de la función de extensión `map` de `StateFlow`.
- **Git:** Desecho un commit de merge local y rebasada la rama `feature/171` sobre `main` resolviendo los conflictos en `CHANGELOG.md` para permitir un "Rebase and merge" limpio en GitHub.

**[2026-06-02] - Fase 7: Implementación de Testing y CI/CD (Issue 1 Completo)**
- **Testing Unitario (Dominio):** Refactor de aserciones para corrutinas (cambio de `assertThrows` por `try-catch`) para arreglar fallos silenciosos. Ampliada la cobertura añadiendo pruebas a Casos de Uso faltantes (`RegistroUseCaseTest`, `EditarCampaniaUseCaseTest`, `EditarTareaUseCaseTest`, `ObtenerCampaniasUseCaseTest`), subiendo la cobertura del paquete de 26% a 36.2%.
- **Testing Unitario (Presentación):** Implementado `LoginViewModelTest` usando Turbine para testear la emisión asíncrona de `StateFlow`.
- **Testing Instrumentado (Datos):** Creados tests en memoria para los DAOs (`UsuarioDaoTest`, `CampaniaDaoTest`, `CampaniaInsumoDaoTest`) simulando un entorno de base de datos Android real con SQLite.
- **Cobertura y CI/CD:** Corregida la tarea de GitHub Actions (`pr_tests.yml`) para invocar la variante correcta de Android (`koverHtmlReportDebug`), permitiendo la correcta lectura de reportes de cobertura en PRs.
- **Documentación:** Actualizado `plan_de_pruebas.md` documentando el correcto uso de excepciones en corrutinas y el comando específico de Kover.

**[2026-06-02] - Fase 7: Planificación de Estrategia de Testing (Issue 1)**
- **Testing:** Definición del stack tecnológico (MockK, Turbine, Kover, AndroidX Test, Compose Rule).
- **Documentación Viva:** Creación del documento `docs/plan_de_pruebas.md` que incluye:
  - Análisis detallado de discrepancias entre el diseño original (2025) y la arquitectura final implementada.
  - Escenarios BDD (Behavior-Driven Development) `Given-When-Then` para todos los módulos de la aplicación (Campañas, Insumos, Tareas, Cosechas, Observaciones, Auth y Backup).
  - Integración exhaustiva de Edge Cases (Casos de Borde).
  - Estrategias de comandos de ejecución local y metas de cobertura estricta (Kover 80% en domain, 70% en data).
- **Roadmap:** Actualizado `.context/RoadmapOP.md` con el progreso en el Issue 1 de la Fase 7.

**[2026-06-02] - Fase 6: Exportación de Reportes a Archivos (Issue 2)**
- **Dominio:** Creado modelo `InsumoResumen` para abstraer la información exportable.
- **Utilidad:** Creada clase `ReportExporter` que utiliza SAF y el ContentResolver para escribir los archivos.
- **Exportación CSV:** Implementada conversión de datos de gastos por insumo en formato CSV.
- **Exportación PDF:** Implementada generación de documento PDF usando la API nativa de Android `PdfDocument`, dibujando tablas en `Canvas`.
- **UI & ViewModel:** Integrados los launchers `ActivityResultContracts.CreateDocument` en `ReportesRendimientoScreen` y conectados a `ReportesViewModel`.
**[2026-06-01] - Fase 9: Refactorización de Arquitectura DB y Documentación de Bugs**
- **Base de Datos:** Eliminado el soporte de borrado lógico (soft-delete) de la tabla intermedia `CampaniaInsumoEntity`, aplicando borrado físico estricto (`DELETE`) en `CampaniaInsumoDao` para mantener la integridad referencial limpia.
- **KSP Fix:** Solucionados conflictos de compilación de Room (KSP) causados por colisión de anotaciones `@Delete` y `@Query`.
- **Limpieza de Código:** Removida la propiedad `activo` del dominio, mappers y datos semilla de insumos. Se incrementó la base de datos a la versión 4 forzando `fallbackToDestructiveMigration()`.
- **Limpieza de Repositorio:** Añadidos archivos de configuración locales de Android Studio (`.idea/misc.xml`, `.idea/deploymentTargetSelector.xml`) al `.gitignore` y eliminados del rastreo de git.
- **Documentación:** Creado el archivo `docs/bugs_identificados.md` documentando 4 problemas conocidos listos para la próxima iteración.

**[2026-06-01] - Optimizaciones de Entorno y Datos de Prueba**
- Migradas rutas locales del JDK (`org.gradle.java.home`) y caché (`gradle.user.home`) desde `gradle.properties` hacia `local.properties` para prevenir sobreescrituras en repositorio compartido.
- Restaurado botón condicional de "Cargar datos de prueba" (`BuildConfig.DEBUG`) en `ConfiguracionDBScreen` manteniendo compatibilidad con el nuevo soft-delete (`activo`) de Insumos en el `DataSeederImpl`.
**[2026-05-31] - Implementación de Backup y Corrección de Regresión**
- Implementadas funcionalidades de exportación e importación de base de datos (CU12, CU13) en `ConfiguracionDBScreen`.
- Creados Casos de Uso `CrearBackupUseCase` y `RestaurarBackupUseCase`.
- **Hotfix:** Revertida sobreescritura accidental del archivo `screens.kt` que había eliminado la navegación moderna con `NavHost`.
- Restaurados `CosechaDao.kt`, `gradle.properties` y `.idea/misc.xml` para eliminar cambios locales subidos por error en la PR.


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
- Home: `CampaniaSeleccionadaCard` ahora navega a `DetalleCampania`; botón + navega a `FormularioCampania`.
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
