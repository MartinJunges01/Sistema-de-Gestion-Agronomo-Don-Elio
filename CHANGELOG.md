# Changelog

**[2026-08-25] - [#341] feat(auth): Persistencia de Sesión**
- **Core:** `SessionManager` ahora guarda `isLoggedIn`. Se añadió `MainViewModel` para controlar el estado inicial de `MainActivity` mientras se carga el `DataStore`.
- **UI:** El flujo de navegación dirige al Dashboard (Home) si la sesión está activa o al Login en caso contrario. El Login fue modificado para persistir también a los usuarios Invitados. Se agregó funcionalidad de "Cerrar sesión" en el Dashboard.
- **Rama:** `fix/issue-341-persistencia-sesion`

**[2026-08-25] - [#342] feat(campañas): Borrado y estilo visual de campañas inactivas**
- **Data/Domain:** Se integró `EliminarCampaniaUseCase` en `GestionCampaniasViewModel`. Se confirmó que Room maneja la eliminación en cascada.
- **UI:** Las tarjetas de campañas inactivas en `GestionCampaniasScreen` tienen un color atenuado. Se agregó un botón de papelera y diálogo de confirmación para eliminación definitiva.
- **Rama:** `fix/issue-342-campanias-inactivas`

**[2026-08-25] - [#338] fix(ux): Teclado y Scroll en Formularios**
- **UI:** Se ajustó el manejo de insets en `MainActivity` y se aplicó `consumeWindowInsets` en `screens.kt` para evitar el bloqueo de scroll y el bloque blanco superior al abrir el teclado virtual.
- **Rama:** `fix/issue-338-teclado`

**[2026-08-25] - [#337] feat(observaciones): Edición de fotos en observaciones**
- **Dominio:** Se implementó `ValidarObservacionUseCase` y se ajustó `EditarObservacionUseCase` para manejar fotos.
- **UI:** El diálogo de edición de observaciones ahora permite modificar o eliminar fotos utilizando cámara y galería con permisos dinámicos.
- **Rama:** `fix/issue-337-editar-foto-observacion`

**[2026-08-25] - [#334] fix(insumos): CreaciÃ³n de insumos en el catÃ¡logo**
- **ViewModel:** Se corrigiÃ³ la lectura del `insumoId` en `FormularioInsumoViewModel` para que un valor de `-1` no se trate como ediciÃ³n, habilitando correctamente el flujo de creaciÃ³n.
- **Rama:** `fix/issue-334-creacion-insumos`

**[2026-08-12] - [#304] fix(ux): Pantalla no se desplaza al escribir (IME padding global)**
- **UI:** En `screens.kt`, se aplicÃ³ el modificador `imePadding()` al contenedor principal dentro del `Scaffold` para que el espaciado reaccione al teclado virtual de forma automÃ¡tica.
- **UI:** Este ajuste resuelve globalmente el solapamiento del teclado en todos los formularios de la app.
- **Rama:** `fix/ime-padding-formularios` (stacked sobre `fix/bloquear-modo-oscuro`)

**[2026-08-12] - [#303] fix(ux): Bloquear Modo Oscuro (Forzar Tema Claro)**
- **UI:** En `Theme.kt`, se modificÃ³ `DonElioTheme` para que el parÃ¡metro `darkTheme` siempre sea `false` por defecto, ignorando el setting del sistema.
- **UI:** Se forzÃ³ `isAppearanceLightStatusBars = true` para asegurar que los iconos de la barra de estado siempre sean oscuros.
- **Rama:** `fix/bloquear-modo-oscuro`

**[2026-08-11] - [#294] feat(observaciones): EdiciÃ³n y eliminaciÃ³n de observaciones**
- **Dominio:** Se crearon `EditarObservacionUseCase` y `EliminarObservacionUseCase`.
- **ViewModels:** Se inyectaron los nuevos casos de uso en `ObservacionViewModel` para gestionar las acciones y los errores, exponiÃ©ndolos como estado.
- **UI:** Se agregaron Ã­conos de editar y eliminar a cada `ObservacionCard` en `ObservacionesScreen`.
- **UI:** Se implementaron diÃ¡logos modales (AlertDialog) para confirmar la eliminaciÃ³n y para editar el texto de la observaciÃ³n in-place.
- **Rama:** `feat/issue-294-edicion-observaciones`

**[2026-08-11] - [#291] fix(tareas): Selector de hora usa TimeInput en vez de texto libre**
- **ViewModels:** `NuevaTareaViewModel` ahora valida que la hora no estÃ© vacÃ­a y que cumpla el formato regex (HH:mm), exponiendo `errorHora`.
- **UI:** En `NuevaTareaScreen` se reemplazÃ³ el `OutlinedTextField` genÃ©rico por un `TimeInput` nativo de Material 3 contenido dentro de un `AlertDialog`, previniendo el ingreso de texto arbitrario.
- **Rama:** `fix/issue-291-timepicker-hora`

**[2026-08-11] - [#285] fix(dashboard): Tareas interactivas y filtradas por vencimiento**
- **DAO/Dominio:** Actualizada la consulta `getTareasPendientesGlobales` para recibir `fechaLimite` y omitir tareas vencidas hace mÃ¡s de 7 dÃ­as.
- **ViewModels:** `HomeViewModel` ahora calcula dinÃ¡micamente la `fechaLimite` y la pasa al `ObtenerTareasPendientesUseCase`.
- **UI:** Las tarjetas de "Tareas PrÃ³ximas" ahora son clickeables (navegan al detalle de la campaÃ±a asociada).
- **UI:** Tratamiento visual condicional: tareas recientes vencidas se muestran con color rojo tenue.
- **UI:** Se agregÃ³ el botÃ³n "Ver todas" que redirige a la lista completa de tareas de la app.
- **Rama:** `fix/issue-285-dashboard-tareas`

**[2026-08-11] - [#287] fix(login): Saludo muestra nombre de usuario en vez de Invitado**
- **ViewModels:** `LoginViewModel` inyecta ahora `SessionManager` y luego del inicio de sesiÃ³n persistirÃ¡ en DataStore el nombre real del usuario recibido del backend.
- **Rama:** `fix/issue-287-saludo-usuario`

**[2026-08-11] - [#290] fix(campanias): ValidaciÃ³n estricta de fechas pasadas en creaciÃ³n**
- **Dominio:** 
  - Creado `ValidarDatosCampaniaUseCase` para concentrar la lÃ³gica de validaciÃ³n (nombre, cultivo y control estricto de no permitir fechas anteriores a hoy, ignorando la regla en modo ediciÃ³n).
  - AÃ±adida capa extra de defensa en `CrearCampaniaUseCase` para lanzar excepciÃ³n si la fecha es menor a hoy (medianoche).
- **ViewModels:** `CampaniaFormViewModel` limpiado completamente. Toda su lÃ³gica condicional fue delegada al nuevo caso de uso, dedicÃ¡ndose exclusivamente a actualizar la UI.
- **UI:** En `FormularioCampaniaScreen`, se configurÃ³ `selectableDates` en el `rememberDatePickerState` para deshabilitar visualmente fechas anteriores a hoy, mejorando sustancialmente la UX.
- **Rama:** `fix/campanias-validacion-fechas`

**[2026-08-11] - [#289] fix(insumos): ValidaciÃ³n de Formulario y DelegaciÃ³n a Dominio**
- **Dominio:** Creado `ValidarInsumoUseCase` para evaluar la obligatoriedad de `nombre` y `categorÃ­a`. Nota: El campo `unidad` no fue incluido en la validaciÃ³n porque no existe en la arquitectura actual del proyecto.
- **ViewModels:** 
  - `FormularioInsumoViewModel` modificado para consumir el caso de uso y exponer un estado Ãºnico `isGuardarHabilitado`.
  - `InsumoCatalogoViewModel` modificado para inyectar el caso de uso y exponer una funciÃ³n de delegaciÃ³n de validaciÃ³n.
- **UI:** 
  - `FormularioInsumoScreen` muestra mensajes de error en los campos basÃ¡ndose enteramente en el estado unificado, eliminando lÃ³gica de negocio visual.
  - `CatalogoInsumosScreen` refactorizado para el diÃ¡logo inline y agregado un `SnackbarHost` para observar errores del ViewModel.
- **Rama:** `fix/insumos-validacion-formulario`

**[2026-08-02] - [#302] feat(reportes): Implementar ComparaciÃ³n Real entre CampaÃ±as**
- **Dominio:** `ReportesViewModel` ahora expone `cosechasA` y `cosechasB` asociadas a las campaÃ±as seleccionadas en el comparador.
- **UI:** En `ReportesRendimientoScreen`, la secciÃ³n de "MÃ©tricas Comparativas" ahora muestra los verdaderos totales de Costo de Insumos y Rendimiento (Cosechas) para la CampaÃ±a A y la CampaÃ±a B.
- **UI:** Se reemplazÃ³ el `GraficoEvolucionPlaceholder` por un `DoubleBarIndicator`, que consiste en barras de progreso compuestas (Jetpack Compose) para representar visual y proporcionalmente la diferencia de Costos y Rendimiento entre ambas campaÃ±as seleccionadas.
- **Rama:** `feat/comparacion-campanias` (stacked sobre `feat/grafico-desglose-cosechas`)
- **Dominio y UI:** Agregado el estado `desgloseCosechasData` al `ReportesViewModel` que filtra y agrupa dinÃ¡micamente el listado de cosechas en base a su destino (Almacenada vs Vendida/Reservada).
- **UI:** AÃ±adido un nuevo grÃ¡fico `PieChart` en `ReportesRendimientoScreen` para visualizar visualmente las proporciones del destino de las cosechas de la campaÃ±a activa.
- **Tests:** Creado caso de prueba en `ReportesViewModelTest` para asegurar la correcta agrupaciÃ³n matemÃ¡tica de las cosechas.
- **Rama:** `feat/grafico-desglose-cosechas` (stacked sobre `feat/reporte-insumos-mejorado`)
- **ExportaciÃ³n:** El exportador (`ReportExporter`) ahora recibe y pinta el nombre de la campaÃ±a en los archivos CSV y PDF generados. El nombre del archivo sugerido en el `FilePicker` ahora incluye el nombre de la campaÃ±a.
- **ValidaciÃ³n UI:** Se agregÃ³ una guardia en `ReportesRendimientoScreen` que verifica si hay una campaÃ±a seleccionada antes de abrir el `FilePicker`, mostrando un `Toast` si es `null`.
- **Rama:** `feat/reporte-insumos-mejorado` (stacked sobre `feat/migracion-db-insumos`)
- **Base de Datos:** MigraciÃ³n a versiÃ³n 5 (`MIGRATION_4_5`) usando copias de tabla temporales para eliminar la columna `unidad` de Insumos y Cosechas (limitaciÃ³n de SQLite).
- **Dominio y UI:** EliminaciÃ³n del campo `unidad` explÃ­cito en todo el cÃ³digo; se asume Kg/L de manera implÃ­cita para simplificar el modelo y la UI.
- **Tests actualizados** para no requerir o asertar por el campo `unidad`.
- **Rama:** `feat/migracion-db-insumos` (stacked sobre `feat/campanas-historial`)

**[2026-07-29] - [#299] fix(reportes): Eliminar datos mockeados en Dashboard y reestructurar pantalla Reportes**
- **Dashboard (`DashboardOperacionesScreen.kt`):** Eliminadas las tarjetas hardcodeadas "Clima 24Â°C" y "Salud Lotes 90% Ã“ptimo". El contenido restante sube automÃ¡ticamente.
- **`ReportesViewModel.kt` reescrito:** Se reemplaza `ObtenerTodosLosInsumosVinculadosUseCase` por `ObtenerInsumosVinculadosUseCase(campaniaId)` contextual. Se inyectan `ObtenerCampaniasUseCase` y `ObtenerCosechasPorCampaniaUseCase`. Nuevos StateFlows: `campanias`, `campaniaIndividual`, `insumosIndividual`, `cosechasIndividual`, `campaniaA/B`, `insumosA/B`. `pieChartData` y `exportableData` ahora son contextuales a la campaÃ±a seleccionada.
- **`ReportesRendimientoScreen.kt` reestructurada en dos secciones:**
  - *SecciÃ³n 1 â€” EstadÃ­sticas individuales:* Dropdown con campaÃ±as reales de BD, tarjetas de costo de insumos y total cosechado, PieChart contextual (por campaÃ±a seleccionada).
  - *SecciÃ³n 2 â€” Comparador:* Dos dropdowns con campaÃ±as reales, `CardMetricaComparativa` con costo real de insumos A vs B, placeholder para grÃ¡fico de evoluciÃ³n (scope #302).
- **ExportaciÃ³n CSV/PDF:** Ahora exporta los insumos de la campaÃ±a seleccionada en SecciÃ³n 1 (en lugar de todos los insumos globales).
- **Tests creados:** `ReportesViewModelTest.kt` con 5 casos Given-When-Then (JUnit 4 + MockK + Turbine).
- **`docs/plan_de_pruebas.md` actualizado** con subsecciÃ³n `ReportesViewModel â€” StateFlows contextuales [#299]`.
- **Nota de scope:** La lÃ³gica de `campaniaA/B` e `insumosA/B` es un paso preparatorio del Issue #302. Documentado en la PR con `Partial-scope: #302`.
- **Rama:** `fix/datos-mock-dashboard-reportes` (stacked sobre `fix/tab-tareas-no-actualiza`)

**[2026-07-22] - [#292] fix(campania): PestaÃ±a Tareas no actualiza datos al cambiar de campaÃ±a**
- **Causa raÃ­z doble resuelta:**
  - `TabTareas` usaba `hiltViewModel(key = "tab_tareas")` con key estÃ¡tica, haciendo que Hilt reutilizara la misma instancia del `TareaViewModel` sin importar la campaÃ±a activa.
  - El `campaniaId` recibido como parÃ¡metro en `TabTareas` nunca se propagaba al ViewModel (que iniciaba con `null` desde `SavedStateHandle`).
- **`TareaViewModel.kt` modificado:** Se agrega el mÃ©todo pÃºblico `sincronizarCampania(id: Int)` que actualiza `_campaniaIdSeleccionada` solo si el valor difiere del actual (idempotente, evita emisiones innecesarias en el StateFlow).
- **`DetalleCampaniaScreen.kt` modificado:**
  - `TabTareas`: key cambiada a `"tab_tareas_$campaniaId"` + `LaunchedEffect(campaniaId)` que invoca `sincronizarCampania()` como segunda lÃ­nea de defensa.
  - `TabInsumos`: key corregida de `"tab_insumos"` a `"tab_insumos_$campaniaId"` (mismo patrÃ³n de bug identificado).
- **Tests creados:** `TareaViewModelTest.kt` con 5 casos Given-When-Then (JUnit 4 + MockK + Turbine).
- **`docs/plan_de_pruebas.md` actualizado** con subsecciÃ³n `TareaViewModel â€” sincronizarCampania() [#292]`.
- **Rama:** `fix/tab-tareas-no-actualiza` (stacked sobre `fix/permiso-camara-observaciones`)

**[2026-06-30] - [#283] fix: Crash al Abrir la CÃ¡mara â€” Permiso CAMERA no Solicitado**
- **Causa raÃ­z resuelta:** La app lanzaba `cameraLauncher.launch(uri)` directamente sin verificar ni solicitar el permiso `CAMERA` en runtime, causando un `SecurityException` en Android 6.0+ (API 23).
- **Nuevo mÃ³dulo creado:** `presentation/util/CameraUtils.kt` con tres responsabilidades separadas:
  - `EstadoPermisoCamara`: State holder observable con `mutableStateOf` para `permisoConcedido`, `mostrarRazon` y `denegadoPermanente`.
  - `recordarPermisoCamara()`: Composable que gestiona el ciclo completo del permiso usando `ActivityResultContracts.RequestPermission()` y `ActivityCompat.shouldShowRequestPermissionRationale()` para distinguir denegaciÃ³n temporal vs. permanente.
  - `DialogoRazonPermisoCamara()`: `AlertDialog` de rationale que se muestra en primera denegaciÃ³n.
  - `abrirAjustesPermiso()`: Helper que lanza `Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)` cuando el permiso es denegado permanentemente.
- **`ObservacionesScreen.kt` actualizado:** BotÃ³n "Tomar foto" ahora verifica `controlPermiso.permisoConcedido` antes de lanzar la cÃ¡mara. Si no estÃ¡ concedido, guarda la acciÃ³n pendiente y llama a `controlPermiso.solicitar()`. `SnackbarHost` aÃ±adido al `Box` para feedback visual.
- **Flujos cubiertos:** Permiso ya concedido (directo a cÃ¡mara) Â· Primera denegaciÃ³n (muestra rationale) Â· DenegaciÃ³n permanente (Snackbar con botÃ³n "Abrir Ajustes").
- **Rama:** `fix/permiso-camara-observaciones`

**[2026-06-10] - Cosechas: Fix Crash FK [#284] y ValidaciÃ³n del Formulario [#293]**
- **Issue 7 (#284):** Eliminado el crash `FOREIGN KEY constraint failed` al guardar una cosecha con `campaniaId = -1`. `FormularioCosechaViewModel` ahora inicializa `campaniaId` como `null` cuando `SavedStateHandle` no recibe un id vÃ¡lido (`takeIf { it != -1 }`), inyecta `ObtenerCampaniasUseCase` para exponer `campanias` y `onCampaniaChange()`, y `guardar()` valida `campaniaId == null` emitiendo `errorCampania = "Debe seleccionar una campaÃ±a"` antes de intentar la inserciÃ³n.
- **Issue 7 (UI):** `FormularioCosechaScreen` ahora muestra el componente `SelectorCampania` (etiqueta "CampaÃ±a vinculada") con texto de error debajo cuando falta seleccionar campaÃ±a. El botÃ³n "Guardar" queda deshabilitado mientras `campaniaId == null`.
- **Issue 12 (#293):** `guardar()` ya no hace retorno silencioso con campos vacÃ­os: setea `errorCantidad = "La cantidad es obligatoria"`. Adaptado a migraciÃ³n DB v5 (campo `unidad` eliminado del modelo).
- **Issue 12 (UI):** BotÃ³n "Guardar" deshabilitado si hay errores o campos obligatorios vacÃ­os (`cantidad`, `campaniaId`).
- **Testing:** Creado `FormularioCosechaViewModelTest` con 5 casos (MockK + coroutines-test): sin campaÃ±a, cantidad vacÃ­a, almacenado vÃ¡lido y venta vÃ¡lida.
- **DocumentaciÃ³n:** Marcados como completos Issues #284 y #293 en `.context/roadmap_iteracion_2.md`; agregados escenarios Given-When-Then en `docs/plan_de_pruebas.md`.
- **Rama:** `fix-cosechas-estabilizacion`

**[2026-06-23] - DocumentaciÃ³n de Entrega y Casos de Uso**
- ActualizaciÃ³n de `docs/FLOW.md` incorporando diagramas de flujo interactivos Mermaid para cada una de las 8 ramas principales del sistema.
- CreaciÃ³n de `docs/diferencias_casos_de_uso_2025_2026.md` contrastando la propuesta teÃ³rica original (2025) con la implementaciÃ³n final en Clean Architecture (2026), aplicando el formato tabular de casos de uso requerido en la cursada.

**[2026-06-09] - PlanificaciÃ³n y DivisiÃ³n de IteraciÃ³n 2**
- ActualizaciÃ³n de `docs/bugs_identificados.md` refinando Issues 8, 15, 18, 19, 20 en relaciÃ³n al rediseÃ±o lineal, validaciÃ³n de insumos con Flow y unificaciÃ³n a Toneladas.
- CreaciÃ³n de `.context/iteracion_2.md` con el roadmap maestro priorizado de L1 a L5.
- CreaciÃ³n de `docs/roadmap_desarrolladores.md` organizando las tareas para ejecuciÃ³n en paralelo por 3 desarrolladores, con un desglose granular de ramas Git y orden de ejecuciÃ³n.

**[2026-06-09] - SesiÃ³n de Pruebas Manuales APK Debug â€” DocumentaciÃ³n de 23 Issues**
- Reescritura completa de `docs/bugs_identificados.md` con 23 issues organizados por severidad (L1-L5).
- **L1 (Crashes):** Crash por permisos de cÃ¡mara no solicitados (Issue 6), crash por FK constraint al registrar cosecha sin campaniaId (Issue 7).
- **L2 (Bugs Funcionales):** Saludo siempre muestra "Invitado" (Issue 3 actualizado), catÃ¡logo de insumos sin validaciÃ³n completa (Issue 8), campaÃ±as permiten fechas pasadas (Issue 9), campo hora de tareas sin restricciones (Issue 10), tabs tareas/insumos no se actualizan al cambiar de campaÃ±a (Issue 11), validaciÃ³n faltante en formulario cosechas (Issue 12).
- **L3 (Features Faltantes):** EdiciÃ³n/eliminaciÃ³n de observaciones (Issue 13) y cosechas (Issue 14), separaciÃ³n campaÃ±as activas/inactivas (Issue 15), navegaciÃ³n lateral entre campaÃ±as (Issue 16), campo hectÃ¡reas en cosecha (Issue 17).
- **L4 (Reportes):** Selector de campaÃ±a en grÃ¡fico de insumos (Issue 18), grÃ¡fico desglose cosechas (Issue 19), comparaciÃ³n real entre campaÃ±as (Issue 20).
- **L5 (UX):** Bloquear modo oscuro (Issue 21), teclado cubre campos al escribir (Issue 22), tarjetas mock del dashboard (Issue 23).

**[2026-06-09] - GeneraciÃ³n de APK de Debug para Pruebas**
- Se generÃ³ el archivo APK en versiÃ³n de depuraciÃ³n (debug) mediante Gradle para facilitar las pruebas manuales en dispositivos fÃ­sicos.

**[2026-06-04] - Fase 12: SincronizaciÃ³n, Tests y Refactor (Issue 12.2)**
- **Roadmap:** Sincronizados y marcados como completos los Issues silentes de permisos, exportaciÃ³n/importaciÃ³n de base de datos, BottomNav y Use Cases.
- **Tests Instrumentados:** Diagnosticados y programados para soluciÃ³n los errores de compilaciÃ³n de DAOs (`CampaniaDaoTest` y `CampaniaInsumoDaoTest`) que fallaban por nomenclaturas antiguas.
- **Refactor:** AÃ±adida la tarea para limpiar las importaciones comodÃ­n (`*`) a lo largo del proyecto para apegarse a las mejores prÃ¡cticas de Kotlin.

**[2026-06-04] - Hotfix: CorrecciÃ³n de compilaciÃ³n y rebase de PR**
- **Fix:** Corregido error de compilaciÃ³n en `ReportesViewModel.kt` causado por una importaciÃ³n faltante de la funciÃ³n de extensiÃ³n `map` de `StateFlow`.
- **Git:** Desecho un commit de merge local y rebasada la rama `feature/171` sobre `main` resolviendo los conflictos en `CHANGELOG.md` para permitir un "Rebase and merge" limpio en GitHub.

**[2026-06-02] - Fase 7: ImplementaciÃ³n de Testing y CI/CD (Issue 1 Completo)**
- **Testing Unitario (Dominio):** Refactor de aserciones para corrutinas (cambio de `assertThrows` por `try-catch`) para arreglar fallos silenciosos. Ampliada la cobertura aÃ±adiendo pruebas a Casos de Uso faltantes (`RegistroUseCaseTest`, `EditarCampaniaUseCaseTest`, `EditarTareaUseCaseTest`, `ObtenerCampaniasUseCaseTest`), subiendo la cobertura del paquete de 26% a 36.2%.
- **Testing Unitario (PresentaciÃ³n):** Implementado `LoginViewModelTest` usando Turbine para testear la emisiÃ³n asÃ­ncrona de `StateFlow`.
- **Testing Instrumentado (Datos):** Creados tests en memoria para los DAOs (`UsuarioDaoTest`, `CampaniaDaoTest`, `CampaniaInsumoDaoTest`) simulando un entorno de base de datos Android real con SQLite.
- **Cobertura y CI/CD:** Corregida la tarea de GitHub Actions (`pr_tests.yml`) para invocar la variante correcta de Android (`koverHtmlReportDebug`), permitiendo la correcta lectura de reportes de cobertura en PRs.
- **DocumentaciÃ³n:** Actualizado `plan_de_pruebas.md` documentando el correcto uso de excepciones en corrutinas y el comando especÃ­fico de Kover.

**[2026-06-02] - Fase 7: PlanificaciÃ³n de Estrategia de Testing (Issue 1)**
- **Testing:** DefiniciÃ³n del stack tecnolÃ³gico (MockK, Turbine, Kover, AndroidX Test, Compose Rule).
- **DocumentaciÃ³n Viva:** CreaciÃ³n del documento `docs/plan_de_pruebas.md` que incluye:
  - AnÃ¡lisis detallado de discrepancias entre el diseÃ±o original (2025) y la arquitectura final implementada.
  - Escenarios BDD (Behavior-Driven Development) `Given-When-Then` para todos los mÃ³dulos de la aplicaciÃ³n (CampaÃ±as, Insumos, Tareas, Cosechas, Observaciones, Auth y Backup).
  - IntegraciÃ³n exhaustiva de Edge Cases (Casos de Borde).
  - Estrategias de comandos de ejecuciÃ³n local y metas de cobertura estricta (Kover 80% en domain, 70% en data).
- **Roadmap:** Actualizado `.context/RoadmapOP.md` con el progreso en el Issue 1 de la Fase 7.

**[2026-06-02] - Fase 6: ExportaciÃ³n de Reportes a Archivos (Issue 2)**
- **Dominio:** Creado modelo `InsumoResumen` para abstraer la informaciÃ³n exportable.
- **Utilidad:** Creada clase `ReportExporter` que utiliza SAF y el ContentResolver para escribir los archivos.
- **ExportaciÃ³n CSV:** Implementada conversiÃ³n de datos de gastos por insumo en formato CSV.
- **ExportaciÃ³n PDF:** Implementada generaciÃ³n de documento PDF usando la API nativa de Android `PdfDocument`, dibujando tablas en `Canvas`.
- **UI & ViewModel:** Integrados los launchers `ActivityResultContracts.CreateDocument` en `ReportesRendimientoScreen` y conectados a `ReportesViewModel`.
**[2026-06-01] - Fase 9: RefactorizaciÃ³n de Arquitectura DB y DocumentaciÃ³n de Bugs**
- **Base de Datos:** Eliminado el soporte de borrado lÃ³gico (soft-delete) de la tabla intermedia `CampaniaInsumoEntity`, aplicando borrado fÃ­sico estricto (`DELETE`) en `CampaniaInsumoDao` para mantener la integridad referencial limpia.
- **KSP Fix:** Solucionados conflictos de compilaciÃ³n de Room (KSP) causados por colisiÃ³n de anotaciones `@Delete` y `@Query`.
- **Limpieza de CÃ³digo:** Removida la propiedad `activo` del dominio, mappers y datos semilla de insumos. Se incrementÃ³ la base de datos a la versiÃ³n 4 forzando `fallbackToDestructiveMigration()`.
- **Limpieza de Repositorio:** AÃ±adidos archivos de configuraciÃ³n locales de Android Studio (`.idea/misc.xml`, `.idea/deploymentTargetSelector.xml`) al `.gitignore` y eliminados del rastreo de git.
- **DocumentaciÃ³n:** Creado el archivo `docs/bugs_identificados.md` documentando 4 problemas conocidos listos para la prÃ³xima iteraciÃ³n.

**[2026-06-01] - Optimizaciones de Entorno y Datos de Prueba**
- Migradas rutas locales del JDK (`org.gradle.java.home`) y cachÃ© (`gradle.user.home`) desde `gradle.properties` hacia `local.properties` para prevenir sobreescrituras en repositorio compartido.
- Restaurado botÃ³n condicional de "Cargar datos de prueba" (`BuildConfig.DEBUG`) en `ConfiguracionDBScreen` manteniendo compatibilidad con el nuevo soft-delete (`activo`) de Insumos en el `DataSeederImpl`.
**[2026-05-31] - ImplementaciÃ³n de Backup y CorrecciÃ³n de RegresiÃ³n**
- Implementadas funcionalidades de exportaciÃ³n e importaciÃ³n de base de datos (CU12, CU13) en `ConfiguracionDBScreen`.
- Creados Casos de Uso `CrearBackupUseCase` y `RestaurarBackupUseCase`.
- **Hotfix:** Revertida sobreescritura accidental del archivo `screens.kt` que habÃ­a eliminado la navegaciÃ³n moderna con `NavHost`.
- Restaurados `CosechaDao.kt`, `gradle.properties` y `.idea/misc.xml` para eliminar cambios locales subidos por error en la PR.


**[2026-05-25] - ActualizaciÃ³n de Roadmap y BotÃ³n Invitado**
- ActualizaciÃ³n de `.context/RoadmapOP.md` con issues finalizados de fase 8, 10 y 11.
- AÃ±adido botÃ³n "Invitado" para debug en la pantalla de login (F8/Issue 1.8).

**[2026-05-25] - FinalizaciÃ³n de requerimientos fase 2**
- Implementado swipe semanal para gestiÃ³n visual de Tareas.
- Implementado catÃ¡logo de Insumos con Ã­conos e integraciÃ³n a base de datos.
- Integrado YCharts para grÃ¡ficos de pie en Dashboard de Reportes.
- AÃ±adido soporte de Soft-Delete (activo) en vinculaciÃ³n de Insumos.
- Forzada versiÃ³n de Room DB a 2 con migraciÃ³n destructiva (entorno dev).
- AÃ±adida DataSeed con iconos e items eliminados para pruebas de UI.
- Solucionados errores WorkerDaemon configurando gradle.user.home en entorno local.
- Actualizados Roadmap y documentaciÃ³n de Arquitectura.

**[2026-05-20] - IntegraciÃ³n de 20 issues de auditorÃ­a en RoadmapOP.md**
- Fusionados los 20 issues detectados en auditorÃ­a de cÃ³digo dentro del `RoadmapOP.md` como Fases 8-12, organizados por criticidad.
- Agregadas notas de referencia cruzada y de dependencia entre issues.
- Eliminado `.context/IssuesPendientes.md` (contenido migrado a RoadmapOP.md).

**[2026-05-19] - Implementar autenticaciÃ³n, refactor Clean Arch y conectar Use Cases muertos**
- **Issue 1 (Login completo):** CreaciÃ³n de `UsuarioDao`, modelo de dominio `Usuario`, mappers, `LoginUseCase` (SHA-256), `RegistroUseCase` y `LoginViewModel`. ConexiÃ³n de `LoginScreen` y `RegistroScreen`.
- **Issue 12 (Refactor Clean Arch):** CreaciÃ³n de 6 UseCases contenedores para queries reactivas. RefactorizaciÃ³n de 6 ViewModels para inyectar UseCases en lugar de repositorios (`CampaniaFormViewModel`, `CampaniaDetailViewModel`, `TareaViewModel`, `CosechaViewModel`, `InsumoVinculacionViewModel` y `ObservacionViewModel`).
- **Issue 13 (Use Cases muertos):** ConexiÃ³n de `EditarTareaUseCase`, `EliminarTareaUseCase`, `EditarInsumoCatalogoUseCase` y creaciÃ³n de `EliminarInsumoCatalogoUseCase`. DiÃ¡logo de ediciÃ³n inline en `CatalogoInsumosScreen`.

**[2026-05-18] - Refactor de GestiÃ³n de CampaÃ±as (F4/Issue9)**
- CreaciÃ³n de `GestionCampaniasViewModel` con carga reactiva de campaÃ±as desde `ObtenerCampaniasUseCase`.
- CreaciÃ³n de `GestionCampaniasScreen` reemplazando `GestionParcelasScreen` (mock) con lista real desde BD.
- CorrecciÃ³n de navegaciÃ³n: `onGoToDetail` ahora recibe `campaniaId` real del item clickeado.
- Estado vacÃ­o con icono e indicaciÃ³n visual para crear campaÃ±a.

**[2026-05-18] - ImplementaciÃ³n de MÃ³dulo de Observaciones (F4/Issue8)**
- CreaciÃ³n de `ObservacionViewModel` con carga reactiva de observaciones por campaÃ±a desde BD.
- CreaciÃ³n de `FormularioObservacionViewModel` con formulario reactivo, validaciÃ³n y conexiÃ³n a `GuardarObservacionUseCase`.
- RediseÃ±o de `ObservacionesScreen` con formulario para guardar + listado reactivo de observaciones registradas.
- ActualizaciÃ³n de `TabObservaciones` en `DetalleCampaniaScreen` con ViewModel por campaÃ±a y Ãºltimas 3 observaciones.

**[2026-05-18] - ImplementaciÃ³n completa CosechaNoAlmacenada (Venta/Reserva)**
- CreaciÃ³n de `CosechaNoAlmacenadaDao`, modelo de dominio `CosechaNoAlmacenada`, repositorio e implementaciÃ³n.
- CreaciÃ³n de `RegistrarCosechaConVentaUseCase` que inserta cosecha base + detalle de venta/reserva.
- ExposiciÃ³n del DAO en `DonElioDatabase` y DI en `DatabaseModule`/`RepositoryModule`.
- Mappers `toDomain()`/`toEntity()` para `CosechaNoAlmacenadaEntity`.
- `CosechaRepository.insertCosecha()` ahora retorna `Long` (ID generado).
- `CosechaViewModel` ampliado: `almacenadas` (filtrado) y `noAlmacenadasDetalle` (mapa idâ†’detalle).
- `FormularioCosechaViewModel.guardar()` bifurca entre `RegistrarCosechaUseCase` y `RegistrarCosechaConVentaUseCase` segÃºn checkbox.
- `CosechasScreen` muestra tipo y precio en cards de venta/reserva.
- `TabCosechas` en `DetalleCampaniaScreen` con key Ãºnica por campaÃ±a y resumen real de ventas.

**[2026-05-18] - ImplementaciÃ³n de MÃ³dulo de Cosechas (F4/Issue7)**
- CreaciÃ³n de `CosechaViewModel` con carga reactiva de cosechas por campaÃ±a desde BD.
- CreaciÃ³n de `FormularioCosechaViewModel` con formulario reactivo, validaciÃ³n y conexiÃ³n a `RegistrarCosechaUseCase`.
- RefactorizaciÃ³n de `CosechasScreen` con datos reales, separaciÃ³n visual almacenadas/no-almacenadas.
- RefactorizaciÃ³n de `FormularioCosechaScreen` con ViewModel, DatePicker, validaciÃ³n de cantidad y spinner de guardado.
- Agregado parÃ¡metro `campaniaId` opcional a `NavRoute.FormularioCosecha`.
- ActualizaciÃ³n de `TabCosechas` en `DetalleCampaniaScreen` con datos reales desde BD.

**[2026-05-15] - Seed data para testing (debug source set)**
- ConfiguraciÃ³n de `sourceSets { debug { java.srcDir("src/debug/java") } }` en `app/build.gradle.kts`.
- CreaciÃ³n de interfaz `DataSeeder` en `src/main/` con `@BindsOptionalOf` para inyecciÃ³n opcional en Hilt.
- CreaciÃ³n de `DataSeederImpl` en `src/debug/` con 4 campaÃ±as, 8 insumos, 8 tareas, 3 cosechas, 5 vinculaciones y 4 observaciones con fechas fijas mediante `Calendar`.
- CreaciÃ³n de `SeedModule` en `src/debug/` proveyendo `DataSeederImpl` vÃ­a Hilt.
- CreaciÃ³n de `ConfiguracionDBViewModel` con estado `SeedState` (Idle/Cargando/Exito/Error) y mÃ©todo `cargarDatosPrueba()`.
- BotÃ³n "Cargar datos de prueba" en `ConfiguracionDBScreen` visible solo en builds debug, con spinner y Snackbar de feedback.
- ActualizaciÃ³n de `.context/RoadmapOP.md` con Issue 10 de Fase 4.

**[2026-05-15] - ImplementaciÃ³n de MÃ³dulo de Insumos (F4/Issue6)**
- CreaciÃ³n de `InsumoCatalogoViewModel` e `InsumoVinculacionViewModel` con carga reactiva desde BD.
- ConexiÃ³n de `CatalogoInsumosScreen` al catÃ¡logo real con `ObtenerCatalogoInsumosUseCase`.
- ConexiÃ³n de `FormularioInsumoScreen` a `CrearInsumoCatalogoUseCase` con validaciÃ³n y spinner.
- RefactorizaciÃ³n de `InsumosScreen` (vinculaciÃ³n) con datos reales, cÃ¡lculo `cantidad Ã— precio` formateado y atajo "Crear nuevo insumo" si no existe en catÃ¡logo.
- CreaciÃ³n de `FormularioInsumoViewModel` con estado reactivo.
- ActualizaciÃ³n de `TabInsumos` en `DetalleCampaniaScreen` con conteo real y total estimado.

**[2026-05-15] - ImplementaciÃ³n de MÃ³dulo de Tareas (F4/Issue5)**
- CreaciÃ³n de `TareaViewModel` con carga reactiva de tareas por campaÃ±a desde BD.
- CreaciÃ³n de `NuevaTareaViewModel` con formulario reactivo, validaciÃ³n y conexiÃ³n a `CrearTareaUseCase`.
- RefactorizaciÃ³n de `TareasScreen` con datos reales, checkbox de confirmaciÃ³n con `ConfirmarTareaUseCase`, feedback visual (tachado + atenuado).
- RefactorizaciÃ³n de `NuevaTareaScreen` con `DatePickerDialog` M3, validaciÃ³n de nombre y spinner de guardado.
- ActualizaciÃ³n de `TabTareas` en `DetalleCampaniaScreen` con lista real de pendientes y resumen.
- ActualizaciÃ³n de `NavRoute.NuevaTarea` con `campaniaId` opcional.

**[2026-05-14] - Correcciones de bugs y navegaciÃ³n (F4/Issue4)**
- Bugfix: `CrearCampaniaUseCase` ahora acepta parÃ¡metro `cultivo` â€” el campo ya no se pierde al crear campaÃ±as nuevas.
- Bugfix: `CampaniaFormViewModel` pasa `cultivo` al `crearCampaniaUseCase`.
- Bugfix: `GestionParcelasScreen`, `TareasScreen`, `InsumosScreen`, `CosechasScreen`, `ObservacionesScreen` ya no hardcodean `campaniaId=1` â€” todas las rutas aceptan `campaniaId` opcional y lo propagan correctamente.
- Limpieza: eliminado parÃ¡metro `onEditar` no usado en `HeaderCampania`.

**[2026-05-14] - Pantalla Detalle de CampaÃ±a con Tabs y encabezado fijo (F4/Issue4)**
- CreaciÃ³n de `CampaniaDetailViewModel` con `SavedStateHandle` para carga de campaÃ±a por ID + eliminaciÃ³n.
- RediseÃ±o de `DetalleCampaniaScreen` con TopAppBar dinÃ¡mico, encabezado fijo (nombre, cultivo, fechas, estado) y TabRow con 5 tabs: Info, Tareas, Insumos, Cosechas, Observaciones.
- Cada tab muestra resumen informativo y botÃ³n de navegaciÃ³n a su pantalla completa, pasando `campaniaId`.
- ActualizaciÃ³n de `screens.kt` con `navArgument("campaniaId")` extraÃ­do y pasado al ViewModel.
- NavegaciÃ³n desde detalle a ediciÃ³n de campaÃ±a (`onGoToEditar`) con el ID correcto.

**[2026-05-14] - ImplementaciÃ³n de Formulario ABM CampaÃ±as con validaciÃ³n y DatePicker (F4/Issue3)**
- CreaciÃ³n de `CampaniaFormViewModel` con `SavedStateHandle` para modo ediciÃ³n/creaciÃ³n.
- RefactorizaciÃ³n de `FormularioCampaniaScreen` con campos nombre/cultivo validados, DatePicker M3, botÃ³n guardar con spinner.
- ActualizaciÃ³n de `NavRoute.FormularioCampania` con `campaniaId` opcional vÃ­a query param.
- IntegraciÃ³n de `CrearCampaniaUseCase` (creaciÃ³n) y `EditarCampaniaUseCase` (ediciÃ³n) con `LaunchedEffect` para navegaciÃ³n post-guardado.

**[2026-05-14] - Refactor: divisiÃ³n de screens.kt en archivos individuales**
- SeparaciÃ³n de 15 pantallas en archivos por feature (login, home, campania, tarea, cosecha, insumo, observacion, reportes, config).
- ExtracciÃ³n de colores a `theme/AgriCoreColors.kt`.
- Componentes compartidos movidos a `components/` (6 archivos).
- NavegaciÃ³n migrada a `navigation/NavRoutes.kt` con sealed class `NavRoute`.
- SimplificaciÃ³n de la ruta `FormularioCampania` (sin parÃ¡metro opcional).

**[2026-05-14] - ImplementaciÃ³n de HomeViewModel y Dashboard reactivo (F4/Issue2)**
- CreaciÃ³n de `HomeViewModel` con inyecciÃ³n de `ObtenerCampaniasUseCase`.
- RefactorizaciÃ³n de `DashboardOperacionesScreen` para consumir datos reales desde BD.
- Lista reactiva de campaÃ±as con navegaciÃ³n al detalle por ID.
- Estado vacÃ­o con indicaciÃ³n visual para crear una nueva campaÃ±a.

**[2026-05-14] - MigraciÃ³n a Navigation Compose y Scaffold global (F4/Issue1)**
- CreaciÃ³n de `NavRoute` (sealed class) reemplazando enum `Destino`.
- MigraciÃ³n de navegaciÃ³n manual (lista/pila) a `NavHost` + `NavController`.
- ConfiguraciÃ³n de BottomNavigationBar con preservaciÃ³n de estado por pestaÃ±a.
- EliminaciÃ³n de `BackHandler` manual (delegado al NavController).
- DefiniciciÃ³n de rutas con parÃ¡metros (`DetalleCampania`, `FormularioCampania`).

**[2026-05-12] - ImplementaciÃ³n de Casos de Uso (CampaÃ±as y Tareas) - F3/Issue4**
- CreaciÃ³n de `CrearCampaniaUseCase`, `EditarCampaniaUseCase`, `EliminarCampaniaUseCase` y `ObtenerCampaniasUseCase`.
- Cada Use Case con `@Inject constructor` y validaciÃ³n de nombre no vacÃ­o.
- CreaciÃ³n de `CrearTareaUseCase`, `EditarTareaUseCase`, `EliminarTareaUseCase` y `ConfirmarTareaUseCase`.

**[2026-05-14] - ImplementaciÃ³n de Resource<T> y manejo de errores en Use Cases**
- CreaciÃ³n de `Resource<T>` en `domain/model/` con extensiones `onSuccess`, `onError`, `isSuccess`, `isError`.
- RefactorizaciÃ³n de 7 Use Cases para retornar `Flow<Resource<Unit>>` con emisiÃ³n de Loading, Success y Error.
- Manejo de excepciones con try/catch y ejecuciÃ³n en `Dispatchers.IO` mediante `flowOn`.

**[2026-05-14] - CorrecciÃ³n de mapeo Campania, unificaciÃ³n de nomenclatura e implementaciÃ³n de Use Cases faltantes**
- Corregido mapeo bidireccional `Campania` â†” `CampaniaEntity`: agregado `cultivo` al modelo de dominio y `estaActiva` a la entidad; eliminados hardcodeos en `Mappers.kt`.
- Renombrado `campaniaId` â†’ `idCampania` en `TareaRepository`, `CosechaRepository` y sus implementaciones.
- Creados modelos de dominio `Observacion` y `CampaniaInsumo` para mantener la pureza de la capa domain.
- Creados `CampaniaInsumoRepository` y `ObservacionRepository` con sus implementaciones y bindings de Hilt.
- Agregados mappers para `ObservacionEntity` â†” `Observacion` y `CampaniaInsumoEntity` â†” `CampaniaInsumo`.
- Implementados 6 casos de uso: `RegistrarCosechaUseCase`, `CrearInsumoCatalogoUseCase`, `EditarInsumoCatalogoUseCase`, `ObtenerCatalogoInsumosUseCase`, `AsignarInsumoACampaniaUseCase`, `GuardarObservacionUseCase`.

**[2026-05-12] - Card campaÃ±a activa en Tareas/Cosechas/Observaciones + botÃ³n exportar en Reportes + diagrama de flujo**
- TareasScreen, CosechasScreen y ObservacionesScreen: aÃ±adida `CampanaSeleccionadaCard` de la campaÃ±a activa.
- ReportesRendimientoScreen: aÃ±adido botÃ³n de exportar (Excel/PDF) en TopAppBar con `DropdownMenu`.
- Creado `docs/FLOW.md` con diagrama Mermaid de navegaciÃ³n y tabla de cobertura de Casos de Uso.

**[2026-05-12] - Refactor de navegaciÃ³n global, mÃ³dulo de insumos y reportes**
- BottomNav: aÃ±adido acceso directo a `Destino.Insumos`; renombrado "Agenda" â†’ "Tareas" y "Parcelas" â†’ "CampaÃ±as".
- Home: `CampaniaSeleccionadaCard` ahora navega a `DetalleCampania`; botÃ³n + navega a `FormularioCampania`.
- InsumosScreen: reemplazado formulario inline por `ModalBottomSheet` con buscador, selector cantidad/precio y botÃ³n "Agregar al catÃ¡logo".
- FormularioInsumoScreen: simplificado a solo campos Nombre, CategorÃ­a y Unidad.
- ReportesRendimientoScreen: aÃ±adidas tarjetas de mÃ©tricas comparativas (Rendimiento, Ganancias, Costos, Insumos); selector dropdown para comparar dos campaÃ±as; grÃ¡ficos Canvas de evoluciÃ³n mensual (Costos/Insumos) con leyenda bicolor.

**[2026-05-12] - InicializaciÃ³n de documentaciÃ³n de seguimiento**
- CreaciÃ³n de `CHANGELOG.md` en la raÃ­z para el seguimiento de tareas.
- Ajuste de `donelioOP.md` para referenciar `.context/RoadmapOP.md`.

**[2026-05-11] - Avance en Fase 3 (Capa de Dominio)**
- DefiniciÃ³n de modelos de dominio (`data class` puros).
- ImplementaciÃ³n de `Mappers.kt`.
- CreaciÃ³n de interfaces de repositorios (`CampaniaRepository`, `TareaRepository`, etc.).
- ImplementaciÃ³n base de los repositorios en la capa `data`.

**[2026-05-10] - FinalizaciÃ³n de Fase 1 y Fase 2**
- ConfiguraciÃ³n inicial del proyecto, dependencias y estructura de Clean Architecture.
- ImplementaciÃ³n completa de la capa de datos: Entidades Room, TypeConverters y DAOs.
- ConfiguraciÃ³n de Dagger-Hilt para inyecciÃ³n de dependencias.

**[2026-08-21] - Fix Inserción de Insumos al Catálogo [#334]**
- Se corrigió un error donde FormularioInsumoViewModel leía un insumoId = -1 por defecto y causaba que se ejecutara el flujo de actualización silenciosamente en lugar de crear uno nuevo.

**[2026-08-21] - Fix Edición de Cosechas [#335]**
- Se agregó el parámetro cosechaId a la ruta de navegación de FormularioCosecha y se vinculó el evento onEditarCosecha para permitir la edición correcta de las cosechas.

**[2026-08-21] - Fix Validación de Formulario de Cosechas [#336]**
- Se añadió una propiedad errorGeneral para evitar que todos los errores del formulario de cosecha se agruparan erróneamente en el campo cantidad, mostrando en cambio un Snackbar universal.
**[2026-08-21] - Fix Reportes Exportación vacía y Comparador [#355] [#356]**
- Se agregó una guardia en ReportesViewModel para evitar exportar PDFs o CSVs vacíos cuando no hay datos en la campaña seleccionada.
- Se implementó una tarjeta de advertencia en ReportesRendimientoScreen para prevenir que el usuario seleccione la misma campaña en ambos selectores del comparador, documentando el caso en el plan de pruebas.
**[2026-08-21] - Fix UI Detalles y Reportes [#339] [#340]**
- Se migró el TabRow a ScrollableTabRow en DetalleCampaniaScreen para evitar que los nombres de las pestañas se corten o dividan en varias líneas.
- Se ocultó la leyenda por defecto de los gráficos PieChart en ReportesRendimientoScreen y se creó una leyenda manual debajo utilizando FlowRow, solucionando el problema de solapamiento de etiquetas en el gráfico.
