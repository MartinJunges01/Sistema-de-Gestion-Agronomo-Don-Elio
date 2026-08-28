# Changelog

**[2026-08-28] - Fix pre-testing: correcciones de UX y validación (#335, #336, #339)**
- **#335 (fix/cosecha):** Se corrigió el flujo de edición de cosechas. `FormularioCosechaScreen` ahora recibe el parámetro `cosechaId` desde la navegación y muestra el título dinámico "Editar Cosecha" cuando corresponde. `screens.kt` actualizado para pasar `cosechaId` al composable.
- **#336 (fix/cosecha):** Se agregó `errorFecha` al estado `FormularioCosechaState`. El mapeo de errores en `guardar()` ahora distingue el campo correcto (`errorCantidad` vs `errorFecha` vs `errorGeneral`) según el mensaje del `ValidarDatosCosechaUseCase`. La UI muestra el error en el campo Fecha correspondiente. Se agregaron 4 nuevos casos de test unitario (Tests 6–9).
- **#339 (fix/campania):** Se agregó `horizontalScroll` al `Row` de chips informativos en `HeaderCampania` para evitar cortes en pantallas estrechas. Los textos de totales en `TabInsumos` y `TabCosechas` usan `softWrap = true` y `fontSize` reducido para asegurar renderizado correcto.

**[2026-08-28] - Merge Unificado de Iteración 3 (Issues #352, #353, #354, #360, #373, #374)**
- **#352 / #353 / #374**: Reportes avanzados, evolución histórica por cultivo con Canvas, filtros multicampaña, y leyenda ajustada en el PieChart.
- **#354**: Sincronización de insumos tras creación (InsumoVinculacionViewModel).
- **#360**: Estandarización de UX al validar insumos (Lazy validation on submit).
- **#373**: Validación en capa de dominio y obligatoriedad de almacén en cosechas.
- **Deuda Técnica**: Corrección de firmas redundantes (DT-021), nuevos test de dominio (DT-023) y actualización del Plan de Pruebas (DT-024). Refactor de Clean Architecture diferido a Issue #398 (DT-022).


**[2026-08-25] - [#357] feat(export): implementar paginacion automatica en reportes PDF**
- Se refactorizo ReportExporter.exportToPdf() para mantener control dinamico de yPosition.
- Se añadio logica de salto de pagina al superar los 800f en el eje Y.
- Se extrajo el pintado de cabeceras en funciones internas para re-imprimirlas automaticamente al abrir una nueva pagina.

**[2026-08-25] - [#358] test(reportes): test unitario de DoubleBarIndicator y arreglo de mocks**
- Se expuso DoubleBarIndicator con @VisibleForTesting e internal.
- Se agrego el test instrumentado DoubleBarIndicatorTest validando el renderizado cuando max = 0f.
- Se añadieron hectareas por defecto a las entidades mockeadas en DAO.

**[2026-08-25] - [#359] test(reportes): implementar tests VM-R8 y VM-R9 de guardia de exportacion**
- Se implemento validacion para exportarReporteCsv y exportarReportePdf cuando no hay campaña seleccionada.
- Ambos validan que exportStatus emite la cadena correcta.

**[2026-08-25] - [#350] feat(reportes): agregar Costo por Hectarea ($/Ha)**
- Se agrego CalcularCostoPorHectareaUseCase para aislar la logica.
- ReportesViewModel inyecta estados transformados a Strings de moneda.
- Se añadio una tarjeta y grafico de barras para visualizar la diferencia de rentabilidad por hectarea entre campañas.


**[2026-08-25] - [#351] feat(cultivos): ABM de Cultivos (CatÃ¡logo estandarizado)**
- **Data/Domain:** Se creÃ³ la entidad `CultivoEntity` y `Cultivo` (modelo de dominio). Se implementÃ³ `CultivoDao` con soporte para soft-delete, y se expuso `CultivoRepository` y su implementaciÃ³n. Se actualizÃ³ la versiÃ³n de la base de datos a 7.
- **CampaÃ±as:** Se reemplazÃ³ el campo de texto libre `cultivo` en `CampaniaEntity` y `Campania` por `id_cultivo` / `cultivoId` (FK) y `cultivoNombre`, realizando un `INNER JOIN` en todas las consultas de lectura para obtener su descripciÃ³n del catÃ¡logo de forma reactiva.
- **UI:** Se implementÃ³ `CatalogoCultivosScreen` y su `CultivoCatalogoViewModel` para ABM con diÃ¡logos inline. El formulario de campaÃ±a ahora utiliza un `ExposedDropdownMenuBox` para seleccionar cultivos de forma estricta, con una opciÃ³n de inserciÃ³n rÃ¡pida para nuevos cultivos en el mismo formulario.
- **Testing:** Se actualizaron todos los tests unitarios e instrumentados afectados, y se aÃ±adieron pruebas unitarias para `CultivoCatalogoViewModel`.
- **Rama:** `Issue351`

**[2026-08-25] - [#349] feat(db): HectÃ¡reas por campaÃ±a y mÃ©tricas Tn/Ha**
- **Data/Domain:** Se agregÃ³ el campo `hectareas` (Double) a `CampaniaEntity` y `Campania`. Se incrementÃ³ la versiÃ³n de la base de datos Room a 6 implementando la migraciÃ³n correspondiente.
- **UI:** El `FormularioCampaniaScreen` incluye validaciÃ³n de este nuevo campo. Se actualizÃ³ la vista de Reportes para mostrar la mÃ©trica `Rendimiento: X Tn/Ha`.
- **Rama:** `fix/issue-349-refactor-db`

**[2026-08-25] - [#348] feat(reportes): Top 3 insumos de mayor gasto**
- **UI:** Se agregÃ³ una nueva tarjeta en la pantalla de Reportes mostrando los 3 insumos con mayor porcentaje de gasto en la campaÃ±a actual.
- **Rama:** `fix/issue-348-top-insumos`

**[2026-08-25] - [#347] feat(dashboard): Tasa de Cumplimiento de Tareas**
- **Domain:** Se creÃ³ `ObtenerCumplimientoTareasUseCase` y el modelo `CumplimientoTareas` para calcular la relaciÃ³n entre tareas confirmadas y tareas totales en el periodo de las campaÃ±as activas.
- **UI:** Se integrÃ³ al `HomeViewModel` y se visualiza la tasa de cumplimiento en el `DashboardOperacionesScreen`.
- **Rama:** `fix/issue-347-tasa-cumplimiento`

**[2026-08-25] - [#346] feat(dashboard): Resumen financiero rÃ¡pido**
- **Domain:** Se creÃ³ `ObtenerResumenRendimientoUseCase` y el modelo `ResumenRendimiento` para calcular capital invertido (insumos) y total cosechado del mes actual.
- **UI:** Se agregÃ³ una tarjeta en el `DashboardOperacionesScreen` para mostrar estos indicadores financieros.
- **Rama:** `fix/issue-346-resumen-dashboard`

**[2026-08-25] - [#345] feat(tareas): RediseÃ±o de pantalla de tareas y filtros**
- **Domain:** Se creÃ³ `ObtenerTareasFiltradasUseCase` para unificar la bÃºsqueda de tareas por campaÃ±a y fecha.
- **UI:** Se implementÃ³ `SelectorRangoFechas` interactivo (DateRangePicker). La pantalla de Tareas ahora usa este componente para permitir el filtrado de tareas en un rango especÃ­fico o mostrar pendientes por defecto.
- **Rama:** `fix/issue-345-redisenio-tareas`

**[2026-08-25] - [#344] feat(reportes): Leyenda de insumos con valores absolutos**
- **UI:** Se reemplazÃ³ el `FlowRow` en `ReportesRendimientoScreen` por un `Column` ordenado, mostrando el porcentaje y el valor absoluto en pesos de cada insumo.
- **Rama:** `fix/issue-344-orden-insumos`

**[2026-08-25] - [#343] feat(reportes): ExportaciÃ³n de datos de cosechas**
- **Domain:** Se incluyÃ³ la lista de `cosechas` como parte del modelo enviado al `ReportExporter`.
- **Core:** Se actualizaron las funciones `exportToCsv` y `exportToPdf` para anexar el listado de las cosechas de la campaÃ±a seleccionada en ambos formatos.
- **Rama:** `fix/issue-343-exportar-cosechas`

**[2026-08-25] - [#341] feat(auth): Persistencia de SesiÃ³n**
- **Core:** `SessionManager` ahora guarda `isLoggedIn`. Se aÃ±adiÃ³ `MainViewModel` para controlar el estado inicial de `MainActivity` mientras se carga el `DataStore`.
- **UI:** El flujo de navegaciÃ³n dirige al Dashboard (Home) si la sesiÃ³n estÃ¡ activa o al Login en caso contrario. El Login fue modificado para persistir tambiÃ©n a los usuarios Invitados. Se agregÃ³ funcionalidad de "Cerrar sesiÃ³n" en el Dashboard.
- **Rama:** `fix/issue-341-persistencia-sesion`

**[2026-08-25] - [#342] feat(campaÃ±as): Borrado y estilo visual de campaÃ±as inactivas**
- **Data/Domain:** Se integrÃ³ `EliminarCampaniaUseCase` en `GestionCampaniasViewModel`. Se confirmÃ³ que Room maneja la eliminaciÃ³n en cascada.
- **UI:** Las tarjetas de campaÃ±as inactivas en `GestionCampaniasScreen` tienen un color atenuado. Se agregÃ³ un botÃ³n de papelera y diÃ¡logo de confirmaciÃ³n para eliminaciÃ³n definitiva.
- **Rama:** `fix/issue-342-campanias-inactivas`

**[2026-08-25] - [#338] fix(ux): Teclado y Scroll en Formularios**
- **UI:** Se ajustÃ³ el manejo de insets en `MainActivity` y se aplicÃ³ `consumeWindowInsets` en `screens.kt` para evitar el bloqueo de scroll y el bloque blanco superior al abrir el teclado virtual.
- **Rama:** `fix/issue-338-teclado`

**[2026-08-25] - [#337] feat(observaciones): EdiciÃ³n de fotos en observaciones**
- **Dominio:** Se implementÃ³ `ValidarObservacionUseCase` y se ajustÃ³ `EditarObservacionUseCase` para manejar fotos.
- **UI:** El diÃ¡logo de ediciÃ³n de observaciones ahora permite modificar o eliminar fotos utilizando cÃ¡mara y galerÃ­a con permisos dinÃ¡micos.
- **Rama:** `fix/issue-337-editar-foto-observacion`

**[2026-08-25] - [#334] fix(insumos): CreaciÃƒÂ³n de insumos en el catÃƒÂ¡logo**
- **ViewModel:** Se corrigiÃƒÂ³ la lectura del `insumoId` en `FormularioInsumoViewModel` para que un valor de `-1` no se trate como ediciÃƒÂ³n, habilitando correctamente el flujo de creaciÃƒÂ³n.
- **Rama:** `fix/issue-334-creacion-insumos`

**[2026-08-12] - [#304] fix(ux): Pantalla no se desplaza al escribir (IME padding global)**
- **UI:** En `screens.kt`, se aplicÃƒÂ³ el modificador `imePadding()` al contenedor principal dentro del `Scaffold` para que el espaciado reaccione al teclado virtual de forma automÃƒÂ¡tica.
- **UI:** Este ajuste resuelve globalmente el solapamiento del teclado en todos los formularios de la app.
- **Rama:** `fix/ime-padding-formularios` (stacked sobre `fix/bloquear-modo-oscuro`)

**[2026-08-12] - [#303] fix(ux): Bloquear Modo Oscuro (Forzar Tema Claro)**
- **UI:** En `Theme.kt`, se modificÃƒÂ³ `DonElioTheme` para que el parÃƒÂ¡metro `darkTheme` siempre sea `false` por defecto, ignorando el setting del sistema.
- **UI:** Se forzÃƒÂ³ `isAppearanceLightStatusBars = true` para asegurar que los iconos de la barra de estado siempre sean oscuros.
- **Rama:** `fix/bloquear-modo-oscuro`

**[2026-08-11] - [#294] feat(observaciones): EdiciÃƒÂ³n y eliminaciÃƒÂ³n de observaciones**
- **Dominio:** Se crearon `EditarObservacionUseCase` y `EliminarObservacionUseCase`.
- **ViewModels:** Se inyectaron los nuevos casos de uso en `ObservacionViewModel` para gestionar las acciones y los errores, exponiÃƒÂ©ndolos como estado.
- **UI:** Se agregaron ÃƒÂ­conos de editar y eliminar a cada `ObservacionCard` en `ObservacionesScreen`.
- **UI:** Se implementaron diÃƒÂ¡logos modales (AlertDialog) para confirmar la eliminaciÃƒÂ³n y para editar el texto de la observaciÃƒÂ³n in-place.
- **Rama:** `feat/issue-294-edicion-observaciones`

**[2026-08-11] - [#291] fix(tareas): Selector de hora usa TimeInput en vez de texto libre**
- **ViewModels:** `NuevaTareaViewModel` ahora valida que la hora no estÃƒÂ© vacÃƒÂ­a y que cumpla el formato regex (HH:mm), exponiendo `errorHora`.
- **UI:** En `NuevaTareaScreen` se reemplazÃƒÂ³ el `OutlinedTextField` genÃƒÂ©rico por un `TimeInput` nativo de Material 3 contenido dentro de un `AlertDialog`, previniendo el ingreso de texto arbitrario.
- **Rama:** `fix/issue-291-timepicker-hora`

**[2026-08-11] - [#285] fix(dashboard): Tareas interactivas y filtradas por vencimiento**
- **DAO/Dominio:** Actualizada la consulta `getTareasPendientesGlobales` para recibir `fechaLimite` y omitir tareas vencidas hace mÃƒÂ¡s de 7 dÃƒÂ­as.
- **ViewModels:** `HomeViewModel` ahora calcula dinÃƒÂ¡micamente la `fechaLimite` y la pasa al `ObtenerTareasPendientesUseCase`.
- **UI:** Las tarjetas de "Tareas PrÃƒÂ³ximas" ahora son clickeables (navegan al detalle de la campaÃƒÂ±a asociada).
- **UI:** Tratamiento visual condicional: tareas recientes vencidas se muestran con color rojo tenue.
- **UI:** Se agregÃƒÂ³ el botÃƒÂ³n "Ver todas" que redirige a la lista completa de tareas de la app.
- **Rama:** `fix/issue-285-dashboard-tareas`

**[2026-08-11] - [#287] fix(login): Saludo muestra nombre de usuario en vez de Invitado**
- **ViewModels:** `LoginViewModel` inyecta ahora `SessionManager` y luego del inicio de sesiÃƒÂ³n persistirÃƒÂ¡ en DataStore el nombre real del usuario recibido del backend.
- **Rama:** `fix/issue-287-saludo-usuario`

**[2026-08-11] - [#290] fix(campanias): ValidaciÃƒÂ³n estricta de fechas pasadas en creaciÃƒÂ³n**
- **Dominio:** 
  - Creado `ValidarDatosCampaniaUseCase` para concentrar la lÃƒÂ³gica de validaciÃƒÂ³n (nombre, cultivo y control estricto de no permitir fechas anteriores a hoy, ignorando la regla en modo ediciÃƒÂ³n).
  - AÃƒÂ±adida capa extra de defensa en `CrearCampaniaUseCase` para lanzar excepciÃƒÂ³n si la fecha es menor a hoy (medianoche).
- **ViewModels:** `CampaniaFormViewModel` limpiado completamente. Toda su lÃƒÂ³gica condicional fue delegada al nuevo caso de uso, dedicÃƒÂ¡ndose exclusivamente a actualizar la UI.
- **UI:** En `FormularioCampaniaScreen`, se configurÃƒÂ³ `selectableDates` en el `rememberDatePickerState` para deshabilitar visualmente fechas anteriores a hoy, mejorando sustancialmente la UX.
- **Rama:** `fix/campanias-validacion-fechas`

**[2026-08-11] - [#289] fix(insumos): ValidaciÃƒÂ³n de Formulario y DelegaciÃƒÂ³n a Dominio**
- **Dominio:** Creado `ValidarInsumoUseCase` para evaluar la obligatoriedad de `nombre` y `categorÃƒÂ­a`. Nota: El campo `unidad` no fue incluido en la validaciÃƒÂ³n porque no existe en la arquitectura actual del proyecto.
- **ViewModels:** 
  - `FormularioInsumoViewModel` modificado para consumir el caso de uso y exponer un estado ÃƒÂºnico `isGuardarHabilitado`.
  - `InsumoCatalogoViewModel` modificado para inyectar el caso de uso y exponer una funciÃƒÂ³n de delegaciÃƒÂ³n de validaciÃƒÂ³n.
- **UI:** 
  - `FormularioInsumoScreen` muestra mensajes de error en los campos basÃƒÂ¡ndose enteramente en el estado unificado, eliminando lÃƒÂ³gica de negocio visual.
  - `CatalogoInsumosScreen` refactorizado para el diÃƒÂ¡logo inline y agregado un `SnackbarHost` para observar errores del ViewModel.
- **Rama:** `fix/insumos-validacion-formulario`

**[2026-08-02] - [#302] feat(reportes): Implementar ComparaciÃƒÂ³n Real entre CampaÃƒÂ±as**
- **Dominio:** `ReportesViewModel` ahora expone `cosechasA` y `cosechasB` asociadas a las campaÃƒÂ±as seleccionadas en el comparador.
- **UI:** En `ReportesRendimientoScreen`, la secciÃƒÂ³n de "MÃƒÂ©tricas Comparativas" ahora muestra los verdaderos totales de Costo de Insumos y Rendimiento (Cosechas) para la CampaÃƒÂ±a A y la CampaÃƒÂ±a B.
- **UI:** Se reemplazÃƒÂ³ el `GraficoEvolucionPlaceholder` por un `DoubleBarIndicator`, que consiste en barras de progreso compuestas (Jetpack Compose) para representar visual y proporcionalmente la diferencia de Costos y Rendimiento entre ambas campaÃƒÂ±as seleccionadas.
- **Rama:** `feat/comparacion-campanias` (stacked sobre `feat/grafico-desglose-cosechas`)
- **Dominio y UI:** Agregado el estado `desgloseCosechasData` al `ReportesViewModel` que filtra y agrupa dinÃƒÂ¡micamente el listado de cosechas en base a su destino (Almacenada vs Vendida/Reservada).
- **UI:** AÃƒÂ±adido un nuevo grÃƒÂ¡fico `PieChart` en `ReportesRendimientoScreen` para visualizar visualmente las proporciones del destino de las cosechas de la campaÃƒÂ±a activa.
- **Tests:** Creado caso de prueba en `ReportesViewModelTest` para asegurar la correcta agrupaciÃƒÂ³n matemÃƒÂ¡tica de las cosechas.
- **Rama:** `feat/grafico-desglose-cosechas` (stacked sobre `feat/reporte-insumos-mejorado`)
- **ExportaciÃƒÂ³n:** El exportador (`ReportExporter`) ahora recibe y pinta el nombre de la campaÃƒÂ±a en los archivos CSV y PDF generados. El nombre del archivo sugerido en el `FilePicker` ahora incluye el nombre de la campaÃƒÂ±a.
- **ValidaciÃƒÂ³n UI:** Se agregÃƒÂ³ una guardia en `ReportesRendimientoScreen` que verifica si hay una campaÃƒÂ±a seleccionada antes de abrir el `FilePicker`, mostrando un `Toast` si es `null`.
- **Rama:** `feat/reporte-insumos-mejorado` (stacked sobre `feat/migracion-db-insumos`)
- **Base de Datos:** MigraciÃƒÂ³n a versiÃƒÂ³n 5 (`MIGRATION_4_5`) usando copias de tabla temporales para eliminar la columna `unidad` de Insumos y Cosechas (limitaciÃƒÂ³n de SQLite).
- **Dominio y UI:** EliminaciÃƒÂ³n del campo `unidad` explÃƒÂ­cito en todo el cÃƒÂ³digo; se asume Kg/L de manera implÃƒÂ­cita para simplificar el modelo y la UI.
- **Tests actualizados** para no requerir o asertar por el campo `unidad`.
- **Rama:** `feat/migracion-db-insumos` (stacked sobre `feat/campanas-historial`)

**[2026-07-29] - [#299] fix(reportes): Eliminar datos mockeados en Dashboard y reestructurar pantalla Reportes**
- **Dashboard (`DashboardOperacionesScreen.kt`):** Eliminadas las tarjetas hardcodeadas "Clima 24Ã‚Â°C" y "Salud Lotes 90% Ãƒâ€œptimo". El contenido restante sube automÃƒÂ¡ticamente.
- **`ReportesViewModel.kt` reescrito:** Se reemplaza `ObtenerTodosLosInsumosVinculadosUseCase` por `ObtenerInsumosVinculadosUseCase(campaniaId)` contextual. Se inyectan `ObtenerCampaniasUseCase` y `ObtenerCosechasPorCampaniaUseCase`. Nuevos StateFlows: `campanias`, `campaniaIndividual`, `insumosIndividual`, `cosechasIndividual`, `campaniaA/B`, `insumosA/B`. `pieChartData` y `exportableData` ahora son contextuales a la campaÃƒÂ±a seleccionada.
- **`ReportesRendimientoScreen.kt` reestructurada en dos secciones:**
  - *SecciÃƒÂ³n 1 Ã¢â‚¬â€� EstadÃƒÂ­sticas individuales:* Dropdown con campaÃƒÂ±as reales de BD, tarjetas de costo de insumos y total cosechado, PieChart contextual (por campaÃƒÂ±a seleccionada).
  - *SecciÃƒÂ³n 2 Ã¢â‚¬â€� Comparador:* Dos dropdowns con campaÃƒÂ±as reales, `CardMetricaComparativa` con costo real de insumos A vs B, placeholder para grÃƒÂ¡fico de evoluciÃƒÂ³n (scope #302).
- **ExportaciÃƒÂ³n CSV/PDF:** Ahora exporta los insumos de la campaÃƒÂ±a seleccionada en SecciÃƒÂ³n 1 (en lugar de todos los insumos globales).
- **Tests creados:** `ReportesViewModelTest.kt` con 5 casos Given-When-Then (JUnit 4 + MockK + Turbine).
- **`docs/plan_de_pruebas.md` actualizado** con subsecciÃƒÂ³n `ReportesViewModel Ã¢â‚¬â€� StateFlows contextuales [#299]`.
- **Nota de scope:** La lÃƒÂ³gica de `campaniaA/B` e `insumosA/B` es un paso preparatorio del Issue #302. Documentado en la PR con `Partial-scope: #302`.
- **Rama:** `fix/datos-mock-dashboard-reportes` (stacked sobre `fix/tab-tareas-no-actualiza`)

**[2026-07-22] - [#292] fix(campania): PestaÃƒÂ±a Tareas no actualiza datos al cambiar de campaÃƒÂ±a**
- **Causa raÃƒÂ­z doble resuelta:**
  - `TabTareas` usaba `hiltViewModel(key = "tab_tareas")` con key estÃƒÂ¡tica, haciendo que Hilt reutilizara la misma instancia del `TareaViewModel` sin importar la campaÃƒÂ±a activa.
  - El `campaniaId` recibido como parÃƒÂ¡metro en `TabTareas` nunca se propagaba al ViewModel (que iniciaba con `null` desde `SavedStateHandle`).
- **`TareaViewModel.kt` modificado:** Se agrega el mÃƒÂ©todo pÃƒÂºblico `sincronizarCampania(id: Int)` que actualiza `_campaniaIdSeleccionada` solo si el valor difiere del actual (idempotente, evita emisiones innecesarias en el StateFlow).
- **`DetalleCampaniaScreen.kt` modificado:**
  - `TabTareas`: key cambiada a `"tab_tareas_$campaniaId"` + `LaunchedEffect(campaniaId)` que invoca `sincronizarCampania()` como segunda lÃƒÂ­nea de defensa.
  - `TabInsumos`: key corregida de `"tab_insumos"` a `"tab_insumos_$campaniaId"` (mismo patrÃƒÂ³n de bug identificado).
- **Tests creados:** `TareaViewModelTest.kt` con 5 casos Given-When-Then (JUnit 4 + MockK + Turbine).
- **`docs/plan_de_pruebas.md` actualizado** con subsecciÃƒÂ³n `TareaViewModel Ã¢â‚¬â€� sincronizarCampania() [#292]`.
- **Rama:** `fix/tab-tareas-no-actualiza` (stacked sobre `fix/permiso-camara-observaciones`)

**[2026-06-30] - [#283] fix: Crash al Abrir la CÃƒÂ¡mara Ã¢â‚¬â€� Permiso CAMERA no Solicitado**
- **Causa raÃƒÂ­z resuelta:** La app lanzaba `cameraLauncher.launch(uri)` directamente sin verificar ni solicitar el permiso `CAMERA` en runtime, causando un `SecurityException` en Android 6.0+ (API 23).
- **Nuevo mÃƒÂ³dulo creado:** `presentation/util/CameraUtils.kt` con tres responsabilidades separadas:
  - `EstadoPermisoCamara`: State holder observable con `mutableStateOf` para `permisoConcedido`, `mostrarRazon` y `denegadoPermanente`.
  - `recordarPermisoCamara()`: Composable que gestiona el ciclo completo del permiso usando `ActivityResultContracts.RequestPermission()` y `ActivityCompat.shouldShowRequestPermissionRationale()` para distinguir denegaciÃƒÂ³n temporal vs. permanente.
  - `DialogoRazonPermisoCamara()`: `AlertDialog` de rationale que se muestra en primera denegaciÃƒÂ³n.
  - `abrirAjustesPermiso()`: Helper que lanza `Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)` cuando el permiso es denegado permanentemente.
- **`ObservacionesScreen.kt` actualizado:** BotÃƒÂ³n "Tomar foto" ahora verifica `controlPermiso.permisoConcedido` antes de lanzar la cÃƒÂ¡mara. Si no estÃƒÂ¡ concedido, guarda la acciÃƒÂ³n pendiente y llama a `controlPermiso.solicitar()`. `SnackbarHost` aÃƒÂ±adido al `Box` para feedback visual.
- **Flujos cubiertos:** Permiso ya concedido (directo a cÃƒÂ¡mara) Ã‚Â· Primera denegaciÃƒÂ³n (muestra rationale) Ã‚Â· DenegaciÃƒÂ³n permanente (Snackbar con botÃƒÂ³n "Abrir Ajustes").
- **Rama:** `fix/permiso-camara-observaciones`

**[2026-06-10] - Cosechas: Fix Crash FK [#284] y ValidaciÃƒÂ³n del Formulario [#293]**
- **Issue 7 (#284):** Eliminado el crash `FOREIGN KEY constraint failed` al guardar una cosecha con `campaniaId = -1`. `FormularioCosechaViewModel` ahora inicializa `campaniaId` como `null` cuando `SavedStateHandle` no recibe un id vÃƒÂ¡lido (`takeIf { it != -1 }`), inyecta `ObtenerCampaniasUseCase` para exponer `campanias` y `onCampaniaChange()`, y `guardar()` valida `campaniaId == null` emitiendo `errorCampania = "Debe seleccionar una campaÃƒÂ±a"` antes de intentar la inserciÃƒÂ³n.
- **Issue 7 (UI):** `FormularioCosechaScreen` ahora muestra el componente `SelectorCampania` (etiqueta "CampaÃƒÂ±a vinculada") con texto de error debajo cuando falta seleccionar campaÃƒÂ±a. El botÃƒÂ³n "Guardar" queda deshabilitado mientras `campaniaId == null`.
- **Issue 12 (#293):** `guardar()` ya no hace retorno silencioso con campos vacÃƒÂ­os: setea `errorCantidad = "La cantidad es obligatoria"`. Adaptado a migraciÃƒÂ³n DB v5 (campo `unidad` eliminado del modelo).
- **Issue 12 (UI):** BotÃƒÂ³n "Guardar" deshabilitado si hay errores o campos obligatorios vacÃƒÂ­os (`cantidad`, `campaniaId`).
- **Testing:** Creado `FormularioCosechaViewModelTest` con 5 casos (MockK + coroutines-test): sin campaÃƒÂ±a, cantidad vacÃƒÂ­a, almacenado vÃƒÂ¡lido y venta vÃƒÂ¡lida.
- **DocumentaciÃƒÂ³n:** Marcados como completos Issues #284 y #293 en `.context/roadmap_iteracion_2.md`; agregados escenarios Given-When-Then en `docs/plan_de_pruebas.md`.
- **Rama:** `fix-cosechas-estabilizacion`

**[2026-06-23] - DocumentaciÃƒÂ³n de Entrega y Casos de Uso**
- ActualizaciÃƒÂ³n de `docs/FLOW.md` incorporando diagramas de flujo interactivos Mermaid para cada una de las 8 ramas principales del sistema.
- CreaciÃƒÂ³n de `docs/diferencias_casos_de_uso_2025_2026.md` contrastando la propuesta teÃƒÂ³rica original (2025) con la implementaciÃƒÂ³n final en Clean Architecture (2026), aplicando el formato tabular de casos de uso requerido en la cursada.

**[2026-06-09] - PlanificaciÃƒÂ³n y DivisiÃƒÂ³n de IteraciÃƒÂ³n 2**
- ActualizaciÃƒÂ³n de `docs/bugs_identificados.md` refinando Issues 8, 15, 18, 19, 20 en relaciÃƒÂ³n al rediseÃƒÂ±o lineal, validaciÃƒÂ³n de insumos con Flow y unificaciÃƒÂ³n a Toneladas.
- CreaciÃƒÂ³n de `.context/iteracion_2.md` con el roadmap maestro priorizado de L1 a L5.
- CreaciÃƒÂ³n de `docs/roadmap_desarrolladores.md` organizando las tareas para ejecuciÃƒÂ³n en paralelo por 3 desarrolladores, con un desglose granular de ramas Git y orden de ejecuciÃƒÂ³n.

**[2026-06-09] - SesiÃƒÂ³n de Pruebas Manuales APK Debug Ã¢â‚¬â€� DocumentaciÃƒÂ³n de 23 Issues**
- Reescritura completa de `docs/bugs_identificados.md` con 23 issues organizados por severidad (L1-L5).
- **L1 (Crashes):** Crash por permisos de cÃƒÂ¡mara no solicitados (Issue 6), crash por FK constraint al registrar cosecha sin campaniaId (Issue 7).
- **L2 (Bugs Funcionales):** Saludo siempre muestra "Invitado" (Issue 3 actualizado), catÃƒÂ¡logo de insumos sin validaciÃƒÂ³n completa (Issue 8), campaÃƒÂ±as permiten fechas pasadas (Issue 9), campo hora de tareas sin restricciones (Issue 10), tabs tareas/insumos no se actualizan al cambiar de campaÃƒÂ±a (Issue 11), validaciÃƒÂ³n faltante en formulario cosechas (Issue 12).
- **L3 (Features Faltantes):** EdiciÃƒÂ³n/eliminaciÃƒÂ³n de observaciones (Issue 13) y cosechas (Issue 14), separaciÃƒÂ³n campaÃƒÂ±as activas/inactivas (Issue 15), navegaciÃƒÂ³n lateral entre campaÃƒÂ±as (Issue 16), campo hectÃƒÂ¡reas en cosecha (Issue 17).
- **L4 (Reportes):** Selector de campaÃƒÂ±a en grÃƒÂ¡fico de insumos (Issue 18), grÃƒÂ¡fico desglose cosechas (Issue 19), comparaciÃƒÂ³n real entre campaÃƒÂ±as (Issue 20).
- **L5 (UX):** Bloquear modo oscuro (Issue 21), teclado cubre campos al escribir (Issue 22), tarjetas mock del dashboard (Issue 23).

**[2026-06-09] - GeneraciÃƒÂ³n de APK de Debug para Pruebas**
- Se generÃƒÂ³ el archivo APK en versiÃƒÂ³n de depuraciÃƒÂ³n (debug) mediante Gradle para facilitar las pruebas manuales en dispositivos fÃƒÂ­sicos.

**[2026-06-04] - Fase 12: SincronizaciÃƒÂ³n, Tests y Refactor (Issue 12.2)**
- **Roadmap:** Sincronizados y marcados como completos los Issues silentes de permisos, exportaciÃƒÂ³n/importaciÃƒÂ³n de base de datos, BottomNav y Use Cases.
- **Tests Instrumentados:** Diagnosticados y programados para soluciÃƒÂ³n los errores de compilaciÃƒÂ³n de DAOs (`CampaniaDaoTest` y `CampaniaInsumoDaoTest`) que fallaban por nomenclaturas antiguas.
- **Refactor:** AÃƒÂ±adida la tarea para limpiar las importaciones comodÃƒÂ­n (`*`) a lo largo del proyecto para apegarse a las mejores prÃƒÂ¡cticas de Kotlin.

**[2026-06-04] - Hotfix: CorrecciÃƒÂ³n de compilaciÃƒÂ³n y rebase de PR**
- **Fix:** Corregido error de compilaciÃƒÂ³n en `ReportesViewModel.kt` causado por una importaciÃƒÂ³n faltante de la funciÃƒÂ³n de extensiÃƒÂ³n `map` de `StateFlow`.
- **Git:** Desecho un commit de merge local y rebasada la rama `feature/171` sobre `main` resolviendo los conflictos en `CHANGELOG.md` para permitir un "Rebase and merge" limpio en GitHub.

**[2026-06-02] - Fase 7: ImplementaciÃƒÂ³n de Testing y CI/CD (Issue 1 Completo)**
- **Testing Unitario (Dominio):** Refactor de aserciones para corrutinas (cambio de `assertThrows` por `try-catch`) para arreglar fallos silenciosos. Ampliada la cobertura aÃƒÂ±adiendo pruebas a Casos de Uso faltantes (`RegistroUseCaseTest`, `EditarCampaniaUseCaseTest`, `EditarTareaUseCaseTest`, `ObtenerCampaniasUseCaseTest`), subiendo la cobertura del paquete de 26% a 36.2%.
- **Testing Unitario (PresentaciÃƒÂ³n):** Implementado `LoginViewModelTest` usando Turbine para testear la emisiÃƒÂ³n asÃƒÂ­ncrona de `StateFlow`.
- **Testing Instrumentado (Datos):** Creados tests en memoria para los DAOs (`UsuarioDaoTest`, `CampaniaDaoTest`, `CampaniaInsumoDaoTest`) simulando un entorno de base de datos Android real con SQLite.
- **Cobertura y CI/CD:** Corregida la tarea de GitHub Actions (`pr_tests.yml`) para invocar la variante correcta de Android (`koverHtmlReportDebug`), permitiendo la correcta lectura de reportes de cobertura en PRs.
- **DocumentaciÃƒÂ³n:** Actualizado `plan_de_pruebas.md` documentando el correcto uso de excepciones en corrutinas y el comando especÃƒÂ­fico de Kover.

**[2026-06-02] - Fase 7: PlanificaciÃƒÂ³n de Estrategia de Testing (Issue 1)**
- **Testing:** DefiniciÃƒÂ³n del stack tecnolÃƒÂ³gico (MockK, Turbine, Kover, AndroidX Test, Compose Rule).
- **DocumentaciÃƒÂ³n Viva:** CreaciÃƒÂ³n del documento `docs/plan_de_pruebas.md` que incluye:
  - AnÃƒÂ¡lisis detallado de discrepancias entre el diseÃƒÂ±o original (2025) y la arquitectura final implementada.
  - Escenarios BDD (Behavior-Driven Development) `Given-When-Then` para todos los mÃƒÂ³dulos de la aplicaciÃƒÂ³n (CampaÃƒÂ±as, Insumos, Tareas, Cosechas, Observaciones, Auth y Backup).
  - IntegraciÃƒÂ³n exhaustiva de Edge Cases (Casos de Borde).
  - Estrategias de comandos de ejecuciÃƒÂ³n local y metas de cobertura estricta (Kover 80% en domain, 70% en data).
- **Roadmap:** Actualizado `.context/RoadmapOP.md` con el progreso en el Issue 1 de la Fase 7.

**[2026-06-02] - Fase 6: ExportaciÃƒÂ³n de Reportes a Archivos (Issue 2)**
- **Dominio:** Creado modelo `InsumoResumen` para abstraer la informaciÃƒÂ³n exportable.
- **Utilidad:** Creada clase `ReportExporter` que utiliza SAF y el ContentResolver para escribir los archivos.
- **ExportaciÃƒÂ³n CSV:** Implementada conversiÃƒÂ³n de datos de gastos por insumo en formato CSV.
- **ExportaciÃƒÂ³n PDF:** Implementada generaciÃƒÂ³n de documento PDF usando la API nativa de Android `PdfDocument`, dibujando tablas en `Canvas`.
- **UI & ViewModel:** Integrados los launchers `ActivityResultContracts.CreateDocument` en `ReportesRendimientoScreen` y conectados a `ReportesViewModel`.
**[2026-06-01] - Fase 9: RefactorizaciÃƒÂ³n de Arquitectura DB y DocumentaciÃƒÂ³n de Bugs**
- **Base de Datos:** Eliminado el soporte de borrado lÃƒÂ³gico (soft-delete) de la tabla intermedia `CampaniaInsumoEntity`, aplicando borrado fÃƒÂ­sico estricto (`DELETE`) en `CampaniaInsumoDao` para mantener la integridad referencial limpia.
- **KSP Fix:** Solucionados conflictos de compilaciÃƒÂ³n de Room (KSP) causados por colisiÃƒÂ³n de anotaciones `@Delete` y `@Query`.
- **Limpieza de CÃƒÂ³digo:** Removida la propiedad `activo` del dominio, mappers y datos semilla de insumos. Se incrementÃƒÂ³ la base de datos a la versiÃƒÂ³n 4 forzando `fallbackToDestructiveMigration()`.
- **Limpieza de Repositorio:** AÃƒÂ±adidos archivos de configuraciÃƒÂ³n locales de Android Studio (`.idea/misc.xml`, `.idea/deploymentTargetSelector.xml`) al `.gitignore` y eliminados del rastreo de git.
- **DocumentaciÃƒÂ³n:** Creado el archivo `docs/bugs_identificados.md` documentando 4 problemas conocidos listos para la prÃƒÂ³xima iteraciÃƒÂ³n.

**[2026-06-01] - Optimizaciones de Entorno y Datos de Prueba**
- Migradas rutas locales del JDK (`org.gradle.java.home`) y cachÃƒÂ© (`gradle.user.home`) desde `gradle.properties` hacia `local.properties` para prevenir sobreescrituras en repositorio compartido.
- Restaurado botÃƒÂ³n condicional de "Cargar datos de prueba" (`BuildConfig.DEBUG`) en `ConfiguracionDBScreen` manteniendo compatibilidad con el nuevo soft-delete (`activo`) de Insumos en el `DataSeederImpl`.
**[2026-05-31] - ImplementaciÃƒÂ³n de Backup y CorrecciÃƒÂ³n de RegresiÃƒÂ³n**
- Implementadas funcionalidades de exportaciÃƒÂ³n e importaciÃƒÂ³n de base de datos (CU12, CU13) en `ConfiguracionDBScreen`.
- Creados Casos de Uso `CrearBackupUseCase` y `RestaurarBackupUseCase`.
- **Hotfix:** Revertida sobreescritura accidental del archivo `screens.kt` que habÃƒÂ­a eliminado la navegaciÃƒÂ³n moderna con `NavHost`.
- Restaurados `CosechaDao.kt`, `gradle.properties` y `.idea/misc.xml` para eliminar cambios locales subidos por error en la PR.


**[2026-05-25] - ActualizaciÃƒÂ³n de Roadmap y BotÃƒÂ³n Invitado**
- ActualizaciÃƒÂ³n de `.context/RoadmapOP.md` con issues finalizados de fase 8, 10 y 11.
- AÃƒÂ±adido botÃƒÂ³n "Invitado" para debug en la pantalla de login (F8/Issue 1.8).

**[2026-05-25] - FinalizaciÃƒÂ³n de requerimientos fase 2**
- Implementado swipe semanal para gestiÃƒÂ³n visual de Tareas.
- Implementado catÃƒÂ¡logo de Insumos con ÃƒÂ­conos e integraciÃƒÂ³n a base de datos.
- Integrado YCharts para grÃƒÂ¡ficos de pie en Dashboard de Reportes.
- AÃƒÂ±adido soporte de Soft-Delete (activo) en vinculaciÃƒÂ³n de Insumos.
- Forzada versiÃƒÂ³n de Room DB a 2 con migraciÃƒÂ³n destructiva (entorno dev).
- AÃƒÂ±adida DataSeed con iconos e items eliminados para pruebas de UI.
- Solucionados errores WorkerDaemon configurando gradle.user.home en entorno local.
- Actualizados Roadmap y documentaciÃƒÂ³n de Arquitectura.

**[2026-05-20] - IntegraciÃƒÂ³n de 20 issues de auditorÃƒÂ­a en RoadmapOP.md**
- Fusionados los 20 issues detectados en auditorÃƒÂ­a de cÃƒÂ³digo dentro del `RoadmapOP.md` como Fases 8-12, organizados por criticidad.
- Agregadas notas de referencia cruzada y de dependencia entre issues.
- Eliminado `.context/IssuesPendientes.md` (contenido migrado a RoadmapOP.md).

**[2026-05-19] - Implementar autenticaciÃƒÂ³n, refactor Clean Arch y conectar Use Cases muertos**
- **Issue 1 (Login completo):** CreaciÃƒÂ³n de `UsuarioDao`, modelo de dominio `Usuario`, mappers, `LoginUseCase` (SHA-256), `RegistroUseCase` y `LoginViewModel`. ConexiÃƒÂ³n de `LoginScreen` y `RegistroScreen`.
- **Issue 12 (Refactor Clean Arch):** CreaciÃƒÂ³n de 6 UseCases contenedores para queries reactivas. RefactorizaciÃƒÂ³n de 6 ViewModels para inyectar UseCases en lugar de repositorios (`CampaniaFormViewModel`, `CampaniaDetailViewModel`, `TareaViewModel`, `CosechaViewModel`, `InsumoVinculacionViewModel` y `ObservacionViewModel`).
- **Issue 13 (Use Cases muertos):** ConexiÃƒÂ³n de `EditarTareaUseCase`, `EliminarTareaUseCase`, `EditarInsumoCatalogoUseCase` y creaciÃƒÂ³n de `EliminarInsumoCatalogoUseCase`. DiÃƒÂ¡logo de ediciÃƒÂ³n inline en `CatalogoInsumosScreen`.

**[2026-05-18] - Refactor de GestiÃƒÂ³n de CampaÃƒÂ±as (F4/Issue9)**
- CreaciÃƒÂ³n de `GestionCampaniasViewModel` con carga reactiva de campaÃƒÂ±as desde `ObtenerCampaniasUseCase`.
- CreaciÃƒÂ³n de `GestionCampaniasScreen` reemplazando `GestionParcelasScreen` (mock) con lista real desde BD.
- CorrecciÃƒÂ³n de navegaciÃƒÂ³n: `onGoToDetail` ahora recibe `campaniaId` real del item clickeado.
- Estado vacÃƒÂ­o con icono e indicaciÃƒÂ³n visual para crear campaÃƒÂ±a.

**[2026-05-18] - ImplementaciÃƒÂ³n de MÃƒÂ³dulo de Observaciones (F4/Issue8)**
- CreaciÃƒÂ³n de `ObservacionViewModel` con carga reactiva de observaciones por campaÃƒÂ±a desde BD.
- CreaciÃƒÂ³n de `FormularioObservacionViewModel` con formulario reactivo, validaciÃƒÂ³n y conexiÃƒÂ³n a `GuardarObservacionUseCase`.
- RediseÃƒÂ±o de `ObservacionesScreen` con formulario para guardar + listado reactivo de observaciones registradas.
- ActualizaciÃƒÂ³n de `TabObservaciones` en `DetalleCampaniaScreen` con ViewModel por campaÃƒÂ±a y ÃƒÂºltimas 3 observaciones.

**[2026-05-18] - ImplementaciÃƒÂ³n completa CosechaNoAlmacenada (Venta/Reserva)**
- CreaciÃƒÂ³n de `CosechaNoAlmacenadaDao`, modelo de dominio `CosechaNoAlmacenada`, repositorio e implementaciÃƒÂ³n.
- CreaciÃƒÂ³n de `RegistrarCosechaConVentaUseCase` que inserta cosecha base + detalle de venta/reserva.
- ExposiciÃƒÂ³n del DAO en `DonElioDatabase` y DI en `DatabaseModule`/`RepositoryModule`.
- Mappers `toDomain()`/`toEntity()` para `CosechaNoAlmacenadaEntity`.
- `CosechaRepository.insertCosecha()` ahora retorna `Long` (ID generado).
- `CosechaViewModel` ampliado: `almacenadas` (filtrado) y `noAlmacenadasDetalle` (mapa idÃ¢â€ â€™detalle).
- `FormularioCosechaViewModel.guardar()` bifurca entre `RegistrarCosechaUseCase` y `RegistrarCosechaConVentaUseCase` segÃƒÂºn checkbox.
- `CosechasScreen` muestra tipo y precio en cards de venta/reserva.
- `TabCosechas` en `DetalleCampaniaScreen` con key ÃƒÂºnica por campaÃƒÂ±a y resumen real de ventas.

**[2026-05-18] - ImplementaciÃƒÂ³n de MÃƒÂ³dulo de Cosechas (F4/Issue7)**
- CreaciÃƒÂ³n de `CosechaViewModel` con carga reactiva de cosechas por campaÃƒÂ±a desde BD.
- CreaciÃƒÂ³n de `FormularioCosechaViewModel` con formulario reactivo, validaciÃƒÂ³n y conexiÃƒÂ³n a `RegistrarCosechaUseCase`.
- RefactorizaciÃƒÂ³n de `CosechasScreen` con datos reales, separaciÃƒÂ³n visual almacenadas/no-almacenadas.
- RefactorizaciÃƒÂ³n de `FormularioCosechaScreen` con ViewModel, DatePicker, validaciÃƒÂ³n de cantidad y spinner de guardado.
- Agregado parÃƒÂ¡metro `campaniaId` opcional a `NavRoute.FormularioCosecha`.
- ActualizaciÃƒÂ³n de `TabCosechas` en `DetalleCampaniaScreen` con datos reales desde BD.

**[2026-05-15] - Seed data para testing (debug source set)**
- ConfiguraciÃƒÂ³n de `sourceSets { debug { java.srcDir("src/debug/java") } }` en `app/build.gradle.kts`.
- CreaciÃƒÂ³n de interfaz `DataSeeder` en `src/main/` con `@BindsOptionalOf` para inyecciÃƒÂ³n opcional en Hilt.
- CreaciÃƒÂ³n de `DataSeederImpl` en `src/debug/` con 4 campaÃƒÂ±as, 8 insumos, 8 tareas, 3 cosechas, 5 vinculaciones y 4 observaciones con fechas fijas mediante `Calendar`.
- CreaciÃƒÂ³n de `SeedModule` en `src/debug/` proveyendo `DataSeederImpl` vÃƒÂ­a Hilt.
- CreaciÃƒÂ³n de `ConfiguracionDBViewModel` con estado `SeedState` (Idle/Cargando/Exito/Error) y mÃƒÂ©todo `cargarDatosPrueba()`.
- BotÃƒÂ³n "Cargar datos de prueba" en `ConfiguracionDBScreen` visible solo en builds debug, con spinner y Snackbar de feedback.
- ActualizaciÃƒÂ³n de `.context/RoadmapOP.md` con Issue 10 de Fase 4.

**[2026-05-15] - ImplementaciÃƒÂ³n de MÃƒÂ³dulo de Insumos (F4/Issue6)**
- CreaciÃƒÂ³n de `InsumoCatalogoViewModel` e `InsumoVinculacionViewModel` con carga reactiva desde BD.
- ConexiÃƒÂ³n de `CatalogoInsumosScreen` al catÃƒÂ¡logo real con `ObtenerCatalogoInsumosUseCase`.
- ConexiÃƒÂ³n de `FormularioInsumoScreen` a `CrearInsumoCatalogoUseCase` con validaciÃƒÂ³n y spinner.
- RefactorizaciÃƒÂ³n de `InsumosScreen` (vinculaciÃƒÂ³n) con datos reales, cÃƒÂ¡lculo `cantidad Ãƒâ€” precio` formateado y atajo "Crear nuevo insumo" si no existe en catÃƒÂ¡logo.
- CreaciÃƒÂ³n de `FormularioInsumoViewModel` con estado reactivo.
- ActualizaciÃƒÂ³n de `TabInsumos` en `DetalleCampaniaScreen` con conteo real y total estimado.

**[2026-05-15] - ImplementaciÃƒÂ³n de MÃƒÂ³dulo de Tareas (F4/Issue5)**
- CreaciÃƒÂ³n de `TareaViewModel` con carga reactiva de tareas por campaÃƒÂ±a desde BD.
- CreaciÃƒÂ³n de `NuevaTareaViewModel` con formulario reactivo, validaciÃƒÂ³n y conexiÃƒÂ³n a `CrearTareaUseCase`.
- RefactorizaciÃƒÂ³n de `TareasScreen` con datos reales, checkbox de confirmaciÃƒÂ³n con `ConfirmarTareaUseCase`, feedback visual (tachado + atenuado).
- RefactorizaciÃƒÂ³n de `NuevaTareaScreen` con `DatePickerDialog` M3, validaciÃƒÂ³n de nombre y spinner de guardado.
- ActualizaciÃƒÂ³n de `TabTareas` en `DetalleCampaniaScreen` con lista real de pendientes y resumen.
- ActualizaciÃƒÂ³n de `NavRoute.NuevaTarea` con `campaniaId` opcional.

**[2026-05-14] - Correcciones de bugs y navegaciÃƒÂ³n (F4/Issue4)**
- Bugfix: `CrearCampaniaUseCase` ahora acepta parÃƒÂ¡metro `cultivo` Ã¢â‚¬â€� el campo ya no se pierde al crear campaÃƒÂ±as nuevas.
- Bugfix: `CampaniaFormViewModel` pasa `cultivo` al `crearCampaniaUseCase`.
- Bugfix: `GestionParcelasScreen`, `TareasScreen`, `InsumosScreen`, `CosechasScreen`, `ObservacionesScreen` ya no hardcodean `campaniaId=1` Ã¢â‚¬â€� todas las rutas aceptan `campaniaId` opcional y lo propagan correctamente.
- Limpieza: eliminado parÃƒÂ¡metro `onEditar` no usado en `HeaderCampania`.

**[2026-05-14] - Pantalla Detalle de CampaÃƒÂ±a con Tabs y encabezado fijo (F4/Issue4)**
- CreaciÃƒÂ³n de `CampaniaDetailViewModel` con `SavedStateHandle` para carga de campaÃƒÂ±a por ID + eliminaciÃƒÂ³n.
- RediseÃƒÂ±o de `DetalleCampaniaScreen` con TopAppBar dinÃƒÂ¡mico, encabezado fijo (nombre, cultivo, fechas, estado) y TabRow con 5 tabs: Info, Tareas, Insumos, Cosechas, Observaciones.
- Cada tab muestra resumen informativo y botÃƒÂ³n de navegaciÃƒÂ³n a su pantalla completa, pasando `campaniaId`.
- ActualizaciÃƒÂ³n de `screens.kt` con `navArgument("campaniaId")` extraÃƒÂ­do y pasado al ViewModel.
- NavegaciÃƒÂ³n desde detalle a ediciÃƒÂ³n de campaÃƒÂ±a (`onGoToEditar`) con el ID correcto.

**[2026-05-14] - ImplementaciÃƒÂ³n de Formulario ABM CampaÃƒÂ±as con validaciÃƒÂ³n y DatePicker (F4/Issue3)**
- CreaciÃƒÂ³n de `CampaniaFormViewModel` con `SavedStateHandle` para modo ediciÃƒÂ³n/creaciÃƒÂ³n.
- RefactorizaciÃƒÂ³n de `FormularioCampaniaScreen` con campos nombre/cultivo validados, DatePicker M3, botÃƒÂ³n guardar con spinner.
- ActualizaciÃƒÂ³n de `NavRoute.FormularioCampania` con `campaniaId` opcional vÃƒÂ­a query param.
- IntegraciÃƒÂ³n de `CrearCampaniaUseCase` (creaciÃƒÂ³n) y `EditarCampaniaUseCase` (ediciÃƒÂ³n) con `LaunchedEffect` para navegaciÃƒÂ³n post-guardado.

**[2026-05-14] - Refactor: divisiÃƒÂ³n de screens.kt en archivos individuales**
- SeparaciÃƒÂ³n de 15 pantallas en archivos por feature (login, home, campania, tarea, cosecha, insumo, observacion, reportes, config).
- ExtracciÃƒÂ³n de colores a `theme/AgriCoreColors.kt`.
- Componentes compartidos movidos a `components/` (6 archivos).
- NavegaciÃƒÂ³n migrada a `navigation/NavRoutes.kt` con sealed class `NavRoute`.
- SimplificaciÃƒÂ³n de la ruta `FormularioCampania` (sin parÃƒÂ¡metro opcional).

**[2026-05-14] - ImplementaciÃƒÂ³n de HomeViewModel y Dashboard reactivo (F4/Issue2)**
- CreaciÃƒÂ³n de `HomeViewModel` con inyecciÃƒÂ³n de `ObtenerCampaniasUseCase`.
- RefactorizaciÃƒÂ³n de `DashboardOperacionesScreen` para consumir datos reales desde BD.
- Lista reactiva de campaÃƒÂ±as con navegaciÃƒÂ³n al detalle por ID.
- Estado vacÃƒÂ­o con indicaciÃƒÂ³n visual para crear una nueva campaÃƒÂ±a.

**[2026-05-14] - MigraciÃƒÂ³n a Navigation Compose y Scaffold global (F4/Issue1)**
- CreaciÃƒÂ³n de `NavRoute` (sealed class) reemplazando enum `Destino`.
- MigraciÃƒÂ³n de navegaciÃƒÂ³n manual (lista/pila) a `NavHost` + `NavController`.
- ConfiguraciÃƒÂ³n de BottomNavigationBar con preservaciÃƒÂ³n de estado por pestaÃƒÂ±a.
- EliminaciÃƒÂ³n de `BackHandler` manual (delegado al NavController).
- DefiniciciÃƒÂ³n de rutas con parÃƒÂ¡metros (`DetalleCampania`, `FormularioCampania`).

**[2026-05-12] - ImplementaciÃƒÂ³n de Casos de Uso (CampaÃƒÂ±as y Tareas) - F3/Issue4**
- CreaciÃƒÂ³n de `CrearCampaniaUseCase`, `EditarCampaniaUseCase`, `EliminarCampaniaUseCase` y `ObtenerCampaniasUseCase`.
- Cada Use Case con `@Inject constructor` y validaciÃƒÂ³n de nombre no vacÃƒÂ­o.
- CreaciÃƒÂ³n de `CrearTareaUseCase`, `EditarTareaUseCase`, `EliminarTareaUseCase` y `ConfirmarTareaUseCase`.

**[2026-05-14] - ImplementaciÃƒÂ³n de Resource<T> y manejo de errores en Use Cases**
- CreaciÃƒÂ³n de `Resource<T>` en `domain/model/` con extensiones `onSuccess`, `onError`, `isSuccess`, `isError`.
- RefactorizaciÃƒÂ³n de 7 Use Cases para retornar `Flow<Resource<Unit>>` con emisiÃƒÂ³n de Loading, Success y Error.
- Manejo de excepciones con try/catch y ejecuciÃƒÂ³n en `Dispatchers.IO` mediante `flowOn`.

**[2026-05-14] - CorrecciÃƒÂ³n de mapeo Campania, unificaciÃƒÂ³n de nomenclatura e implementaciÃƒÂ³n de Use Cases faltantes**
- Corregido mapeo bidireccional `Campania` Ã¢â€ â€� `CampaniaEntity`: agregado `cultivo` al modelo de dominio y `estaActiva` a la entidad; eliminados hardcodeos en `Mappers.kt`.
- Renombrado `campaniaId` Ã¢â€ â€™ `idCampania` en `TareaRepository`, `CosechaRepository` y sus implementaciones.
- Creados modelos de dominio `Observacion` y `CampaniaInsumo` para mantener la pureza de la capa domain.
- Creados `CampaniaInsumoRepository` y `ObservacionRepository` con sus implementaciones y bindings de Hilt.
- Agregados mappers para `ObservacionEntity` Ã¢â€ â€� `Observacion` y `CampaniaInsumoEntity` Ã¢â€ â€� `CampaniaInsumo`.
- Implementados 6 casos de uso: `RegistrarCosechaUseCase`, `CrearInsumoCatalogoUseCase`, `EditarInsumoCatalogoUseCase`, `ObtenerCatalogoInsumosUseCase`, `AsignarInsumoACampaniaUseCase`, `GuardarObservacionUseCase`.

**[2026-05-12] - Card campaÃƒÂ±a activa en Tareas/Cosechas/Observaciones + botÃƒÂ³n exportar en Reportes + diagrama de flujo**
- TareasScreen, CosechasScreen y ObservacionesScreen: aÃƒÂ±adida `CampanaSeleccionadaCard` de la campaÃƒÂ±a activa.
- ReportesRendimientoScreen: aÃƒÂ±adido botÃƒÂ³n de exportar (Excel/PDF) en TopAppBar con `DropdownMenu`.
- Creado `docs/FLOW.md` con diagrama Mermaid de navegaciÃƒÂ³n y tabla de cobertura de Casos de Uso.

**[2026-05-12] - Refactor de navegaciÃƒÂ³n global, mÃƒÂ³dulo de insumos y reportes**
- BottomNav: aÃƒÂ±adido acceso directo a `Destino.Insumos`; renombrado "Agenda" Ã¢â€ â€™ "Tareas" y "Parcelas" Ã¢â€ â€™ "CampaÃƒÂ±as".
- Home: `CampaniaSeleccionadaCard` ahora navega a `DetalleCampania`; botÃƒÂ³n + navega a `FormularioCampania`.
- InsumosScreen: reemplazado formulario inline por `ModalBottomSheet` con buscador, selector cantidad/precio y botÃƒÂ³n "Agregar al catÃƒÂ¡logo".
- FormularioInsumoScreen: simplificado a solo campos Nombre, CategorÃƒÂ­a y Unidad.
- ReportesRendimientoScreen: aÃƒÂ±adidas tarjetas de mÃƒÂ©tricas comparativas (Rendimiento, Ganancias, Costos, Insumos); selector dropdown para comparar dos campaÃƒÂ±as; grÃƒÂ¡ficos Canvas de evoluciÃƒÂ³n mensual (Costos/Insumos) con leyenda bicolor.

**[2026-05-12] - InicializaciÃƒÂ³n de documentaciÃƒÂ³n de seguimiento**
- CreaciÃƒÂ³n de `CHANGELOG.md` en la raÃƒÂ­z para el seguimiento de tareas.
- Ajuste de `donelioOP.md` para referenciar `.context/RoadmapOP.md`.

**[2026-05-11] - Avance en Fase 3 (Capa de Dominio)**
- DefiniciÃƒÂ³n de modelos de dominio (`data class` puros).
- ImplementaciÃƒÂ³n de `Mappers.kt`.
- CreaciÃƒÂ³n de interfaces de repositorios (`CampaniaRepository`, `TareaRepository`, etc.).
- ImplementaciÃƒÂ³n base de los repositorios en la capa `data`.

**[2026-05-10] - FinalizaciÃƒÂ³n de Fase 1 y Fase 2**
- ConfiguraciÃƒÂ³n inicial del proyecto, dependencias y estructura de Clean Architecture.
- ImplementaciÃƒÂ³n completa de la capa de datos: Entidades Room, TypeConverters y DAOs.
- ConfiguraciÃƒÂ³n de Dagger-Hilt para inyecciÃƒÂ³n de dependencias.

**[2026-08-21] - Fix InserciÃ³n de Insumos al CatÃ¡logo [#334]**
- Se corrigiÃ³ un error donde FormularioInsumoViewModel leÃ­a un insumoId = -1 por defecto y causaba que se ejecutara el flujo de actualizaciÃ³n silenciosamente en lugar de crear uno nuevo.

**[2026-08-21] - Fix EdiciÃ³n de Cosechas [#335]**
- Se agregÃ³ el parÃ¡metro cosechaId a la ruta de navegaciÃ³n de FormularioCosecha y se vinculÃ³ el evento onEditarCosecha para permitir la ediciÃ³n correcta de las cosechas.

**[2026-08-21] - Fix ValidaciÃ³n de Formulario de Cosechas [#336]**
- Se aÃ±adiÃ³ una propiedad errorGeneral para evitar que todos los errores del formulario de cosecha se agruparan errÃ³neamente en el campo cantidad, mostrando en cambio un Snackbar universal.
**[2026-08-21] - Fix Reportes ExportaciÃ³n vacÃ­a y Comparador [#355] [#356]**
- Se agregÃ³ una guardia en ReportesViewModel para evitar exportar PDFs o CSVs vacÃ­os cuando no hay datos en la campaÃ±a seleccionada.
- Se implementÃ³ una tarjeta de advertencia en ReportesRendimientoScreen para prevenir que el usuario seleccione la misma campaÃ±a en ambos selectores del comparador, documentando el caso en el plan de pruebas.
**[2026-08-21] - Fix UI Detalles y Reportes [#339] [#340]**
- Se migrÃ³ el TabRow a ScrollableTabRow en DetalleCampaniaScreen para evitar que los nombres de las pestaÃ±as se corten o dividan en varias lÃ­neas.
- Se ocultÃ³ la leyenda por defecto de los grÃ¡ficos PieChart en ReportesRendimientoScreen y se creÃ³ una leyenda manual debajo utilizando FlowRow, solucionando el problema de solapamiento de etiquetas en el grÃ¡fico.

