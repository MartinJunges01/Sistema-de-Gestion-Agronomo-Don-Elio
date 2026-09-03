**[2026-09-03] - Fix teclado bloquea scroll en formularios (Issue #409)**
- Se agregó el modificador .verticalScroll(rememberScrollState()) y .imePadding() a los contenedores Column principales en Insumo, Campaña, Tarea, Observación, y Cosecha.
- Se reemplazó .weight(1f) por .height(32.dp) en espaciadores dentro de Columns con scroll para evitar crashes de UI.

# Changelog

**[2026-08-28] - Fix pre-testing: correcciones de UX y validaciÃ³n (#335, #336, #339)**
- **#335 (fix/cosecha):** Se corrigiÃ³ el flujo de ediciÃ³n de cosechas. `FormularioCosechaScreen` ahora recibe el parÃ¡metro `cosechaId` desde la navegaciÃ³n y muestra el tÃ­tulo dinÃ¡mico "Editar Cosecha" cuando corresponde. `screens.kt` actualizado para pasar `cosechaId` al composable.
- **#336 (fix/cosecha):** Se agregÃ³ `errorFecha` al estado `FormularioCosechaState`. El mapeo de errores en `guardar()` ahora distingue el campo correcto (`errorCantidad` vs `errorFecha` vs `errorGeneral`) segÃºn el mensaje del `ValidarDatosCosechaUseCase`. La UI muestra el error en el campo Fecha correspondiente. Se agregaron 4 nuevos casos de test unitario (Tests 6â€“9).
- **#339 (fix/campania):** Se agregÃ³ `horizontalScroll` al `Row` de chips informativos en `HeaderCampania` para evitar cortes en pantallas estrechas. Los textos de totales en `TabInsumos` y `TabCosechas` usan `softWrap = true` y `fontSize` reducido para asegurar renderizado correcto.

**[2026-08-28] - Merge Unificado de IteraciÃ³n 3 (Issues #352, #353, #354, #360, #373, #374)**
- **#352 / #353 / #374**: Reportes avanzados, evoluciÃ³n histÃ³rica por cultivo con Canvas, filtros multicampaÃ±a, y leyenda ajustada en el PieChart.
- **#354**: SincronizaciÃ³n de insumos tras creaciÃ³n (InsumoVinculacionViewModel).
- **#360**: EstandarizaciÃ³n de UX al validar insumos (Lazy validation on submit).
- **#373**: ValidaciÃ³n en capa de dominio y obligatoriedad de almacÃ©n en cosechas.
- **Deuda TÃ©cnica**: CorrecciÃ³n de firmas redundantes (DT-021), nuevos test de dominio (DT-023) y actualizaciÃ³n del Plan de Pruebas (DT-024). Refactor de Clean Architecture diferido a Issue #398 (DT-022).


**[2026-08-25] - [#357] feat(export): implementar paginacion automatica en reportes PDF**
- Se refactorizo ReportExporter.exportToPdf() para mantener control dinamico de yPosition.
- Se aÃ±adio logica de salto de pagina al superar los 800f en el eje Y.
- Se extrajo el pintado de cabeceras en funciones internas para re-imprimirlas automaticamente al abrir una nueva pagina.

**[2026-08-25] - [#358] test(reportes): test unitario de DoubleBarIndicator y arreglo de mocks**
- Se expuso DoubleBarIndicator con @VisibleForTesting e internal.
- Se agrego el test instrumentado DoubleBarIndicatorTest validando el renderizado cuando max = 0f.
- Se aÃ±adieron hectareas por defecto a las entidades mockeadas en DAO.

**[2026-08-25] - [#359] test(reportes): implementar tests VM-R8 y VM-R9 de guardia de exportacion**
- Se implemento validacion para exportarReporteCsv y exportarReportePdf cuando no hay campaÃ±a seleccionada.
- Ambos validan que exportStatus emite la cadena correcta.

**[2026-08-25] - [#350] feat(reportes): agregar Costo por Hectarea ($/Ha)**
- Se agrego CalcularCostoPorHectareaUseCase para aislar la logica.
- ReportesViewModel inyecta estados transformados a Strings de moneda.
- Se aÃ±adio una tarjeta y grafico de barras para visualizar la diferencia de rentabilidad por hectarea entre campaÃ±as.


**[2026-08-25] - [#351] feat(cultivos): ABM de Cultivos (CatÃƒÂ¡logo estandarizado)**
- **Data/Domain:** Se creÃƒÂ³ la entidad `CultivoEntity` y `Cultivo` (modelo de dominio). Se implementÃƒÂ³ `CultivoDao` con soporte para soft-delete, y se expuso `CultivoRepository` y su implementaciÃƒÂ³n. Se actualizÃƒÂ³ la versiÃƒÂ³n de la base de datos a 7.
- **CampaÃƒÂ±as:** Se reemplazÃƒÂ³ el campo de texto libre `cultivo` en `CampaniaEntity` y `Campania` por `id_cultivo` / `cultivoId` (FK) y `cultivoNombre`, realizando un `INNER JOIN` en todas las consultas de lectura para obtener su descripciÃƒÂ³n del catÃƒÂ¡logo de forma reactiva.
- **UI:** Se implementÃƒÂ³ `CatalogoCultivosScreen` y su `CultivoCatalogoViewModel` para ABM con diÃƒÂ¡logos inline. El formulario de campaÃƒÂ±a ahora utiliza un `ExposedDropdownMenuBox` para seleccionar cultivos de forma estricta, con una opciÃƒÂ³n de inserciÃƒÂ³n rÃƒÂ¡pida para nuevos cultivos en el mismo formulario.
- **Testing:** Se actualizaron todos los tests unitarios e instrumentados afectados, y se aÃƒÂ±adieron pruebas unitarias para `CultivoCatalogoViewModel`.
- **Rama:** `Issue351`

**[2026-08-25] - [#349] feat(db): HectÃƒÂ¡reas por campaÃƒÂ±a y mÃƒÂ©tricas Tn/Ha**
- **Data/Domain:** Se agregÃƒÂ³ el campo `hectareas` (Double) a `CampaniaEntity` y `Campania`. Se incrementÃƒÂ³ la versiÃƒÂ³n de la base de datos Room a 6 implementando la migraciÃƒÂ³n correspondiente.
- **UI:** El `FormularioCampaniaScreen` incluye validaciÃƒÂ³n de este nuevo campo. Se actualizÃƒÂ³ la vista de Reportes para mostrar la mÃƒÂ©trica `Rendimiento: X Tn/Ha`.
- **Rama:** `fix/issue-349-refactor-db`

**[2026-08-25] - [#348] feat(reportes): Top 3 insumos de mayor gasto**
- **UI:** Se agregÃƒÂ³ una nueva tarjeta en la pantalla de Reportes mostrando los 3 insumos con mayor porcentaje de gasto en la campaÃƒÂ±a actual.
- **Rama:** `fix/issue-348-top-insumos`

**[2026-08-25] - [#347] feat(dashboard): Tasa de Cumplimiento de Tareas**
- **Domain:** Se creÃƒÂ³ `ObtenerCumplimientoTareasUseCase` y el modelo `CumplimientoTareas` para calcular la relaciÃƒÂ³n entre tareas confirmadas y tareas totales en el periodo de las campaÃƒÂ±as activas.
- **UI:** Se integrÃƒÂ³ al `HomeViewModel` y se visualiza la tasa de cumplimiento en el `DashboardOperacionesScreen`.
- **Rama:** `fix/issue-347-tasa-cumplimiento`

**[2026-08-25] - [#346] feat(dashboard): Resumen financiero rÃƒÂ¡pido**
- **Domain:** Se creÃƒÂ³ `ObtenerResumenRendimientoUseCase` y el modelo `ResumenRendimiento` para calcular capital invertido (insumos) y total cosechado del mes actual.
- **UI:** Se agregÃƒÂ³ una tarjeta en el `DashboardOperacionesScreen` para mostrar estos indicadores financieros.
- **Rama:** `fix/issue-346-resumen-dashboard`

**[2026-08-25] - [#345] feat(tareas): RediseÃƒÂ±o de pantalla de tareas y filtros**
- **Domain:** Se creÃƒÂ³ `ObtenerTareasFiltradasUseCase` para unificar la bÃƒÂºsqueda de tareas por campaÃƒÂ±a y fecha.
- **UI:** Se implementÃƒÂ³ `SelectorRangoFechas` interactivo (DateRangePicker). La pantalla de Tareas ahora usa este componente para permitir el filtrado de tareas en un rango especÃƒÂ­fico o mostrar pendientes por defecto.
- **Rama:** `fix/issue-345-redisenio-tareas`

**[2026-08-25] - [#344] feat(reportes): Leyenda de insumos con valores absolutos**
- **UI:** Se reemplazÃƒÂ³ el `FlowRow` en `ReportesRendimientoScreen` por un `Column` ordenado, mostrando el porcentaje y el valor absoluto en pesos de cada insumo.
- **Rama:** `fix/issue-344-orden-insumos`

**[2026-08-25] - [#343] feat(reportes): ExportaciÃƒÂ³n de datos de cosechas**
- **Domain:** Se incluyÃƒÂ³ la lista de `cosechas` como parte del modelo enviado al `ReportExporter`.
- **Core:** Se actualizaron las funciones `exportToCsv` y `exportToPdf` para anexar el listado de las cosechas de la campaÃƒÂ±a seleccionada en ambos formatos.
- **Rama:** `fix/issue-343-exportar-cosechas`

**[2026-08-25] - [#341] feat(auth): Persistencia de SesiÃƒÂ³n**
- **Core:** `SessionManager` ahora guarda `isLoggedIn`. Se aÃƒÂ±adiÃƒÂ³ `MainViewModel` para controlar el estado inicial de `MainActivity` mientras se carga el `DataStore`.
- **UI:** El flujo de navegaciÃƒÂ³n dirige al Dashboard (Home) si la sesiÃƒÂ³n estÃƒÂ¡ activa o al Login en caso contrario. El Login fue modificado para persistir tambiÃƒÂ©n a los usuarios Invitados. Se agregÃƒÂ³ funcionalidad de "Cerrar sesiÃƒÂ³n" en el Dashboard.
- **Rama:** `fix/issue-341-persistencia-sesion`

**[2026-08-25] - [#342] feat(campaÃƒÂ±as): Borrado y estilo visual de campaÃƒÂ±as inactivas**
- **Data/Domain:** Se integrÃƒÂ³ `EliminarCampaniaUseCase` en `GestionCampaniasViewModel`. Se confirmÃƒÂ³ que Room maneja la eliminaciÃƒÂ³n en cascada.
- **UI:** Las tarjetas de campaÃƒÂ±as inactivas en `GestionCampaniasScreen` tienen un color atenuado. Se agregÃƒÂ³ un botÃƒÂ³n de papelera y diÃƒÂ¡logo de confirmaciÃƒÂ³n para eliminaciÃƒÂ³n definitiva.
- **Rama:** `fix/issue-342-campanias-inactivas`

**[2026-08-25] - [#338] fix(ux): Teclado y Scroll en Formularios**
- **UI:** Se ajustÃƒÂ³ el manejo de insets en `MainActivity` y se aplicÃƒÂ³ `consumeWindowInsets` en `screens.kt` para evitar el bloqueo de scroll y el bloque blanco superior al abrir el teclado virtual.
- **Rama:** `fix/issue-338-teclado`

**[2026-08-25] - [#337] feat(observaciones): EdiciÃƒÂ³n de fotos en observaciones**
- **Dominio:** Se implementÃƒÂ³ `ValidarObservacionUseCase` y se ajustÃƒÂ³ `EditarObservacionUseCase` para manejar fotos.
- **UI:** El diÃƒÂ¡logo de ediciÃƒÂ³n de observaciones ahora permite modificar o eliminar fotos utilizando cÃƒÂ¡mara y galerÃƒÂ­a con permisos dinÃƒÂ¡micos.
- **Rama:** `fix/issue-337-editar-foto-observacion`

**[2026-08-25] - [#334] fix(insumos): CreaciÃƒÆ’Ã‚Â³n de insumos en el catÃƒÆ’Ã‚Â¡logo**
- **ViewModel:** Se corrigiÃƒÆ’Ã‚Â³ la lectura del `insumoId` en `FormularioInsumoViewModel` para que un valor de `-1` no se trate como ediciÃƒÆ’Ã‚Â³n, habilitando correctamente el flujo de creaciÃƒÆ’Ã‚Â³n.
- **Rama:** `fix/issue-334-creacion-insumos`

**[2026-08-12] - [#304] fix(ux): Pantalla no se desplaza al escribir (IME padding global)**
- **UI:** En `screens.kt`, se aplicÃƒÆ’Ã‚Â³ el modificador `imePadding()` al contenedor principal dentro del `Scaffold` para que el espaciado reaccione al teclado virtual de forma automÃƒÆ’Ã‚Â¡tica.
- **UI:** Este ajuste resuelve globalmente el solapamiento del teclado en todos los formularios de la app.
- **Rama:** `fix/ime-padding-formularios` (stacked sobre `fix/bloquear-modo-oscuro`)

**[2026-08-12] - [#303] fix(ux): Bloquear Modo Oscuro (Forzar Tema Claro)**
- **UI:** En `Theme.kt`, se modificÃƒÆ’Ã‚Â³ `DonElioTheme` para que el parÃƒÆ’Ã‚Â¡metro `darkTheme` siempre sea `false` por defecto, ignorando el setting del sistema.
- **UI:** Se forzÃƒÆ’Ã‚Â³ `isAppearanceLightStatusBars = true` para asegurar que los iconos de la barra de estado siempre sean oscuros.
- **Rama:** `fix/bloquear-modo-oscuro`

**[2026-08-11] - [#294] feat(observaciones): EdiciÃƒÆ’Ã‚Â³n y eliminaciÃƒÆ’Ã‚Â³n de observaciones**
- **Dominio:** Se crearon `EditarObservacionUseCase` y `EliminarObservacionUseCase`.
- **ViewModels:** Se inyectaron los nuevos casos de uso en `ObservacionViewModel` para gestionar las acciones y los errores, exponiÃƒÆ’Ã‚Â©ndolos como estado.
- **UI:** Se agregaron ÃƒÆ’Ã‚Â­conos de editar y eliminar a cada `ObservacionCard` en `ObservacionesScreen`.
- **UI:** Se implementaron diÃƒÆ’Ã‚Â¡logos modales (AlertDialog) para confirmar la eliminaciÃƒÆ’Ã‚Â³n y para editar el texto de la observaciÃƒÆ’Ã‚Â³n in-place.
- **Rama:** `feat/issue-294-edicion-observaciones`

**[2026-08-11] - [#291] fix(tareas): Selector de hora usa TimeInput en vez de texto libre**
- **ViewModels:** `NuevaTareaViewModel` ahora valida que la hora no estÃƒÆ’Ã‚Â© vacÃƒÆ’Ã‚Â­a y que cumpla el formato regex (HH:mm), exponiendo `errorHora`.
- **UI:** En `NuevaTareaScreen` se reemplazÃƒÆ’Ã‚Â³ el `OutlinedTextField` genÃƒÆ’Ã‚Â©rico por un `TimeInput` nativo de Material 3 contenido dentro de un `AlertDialog`, previniendo el ingreso de texto arbitrario.
- **Rama:** `fix/issue-291-timepicker-hora`

**[2026-08-11] - [#285] fix(dashboard): Tareas interactivas y filtradas por vencimiento**
- **DAO/Dominio:** Actualizada la consulta `getTareasPendientesGlobales` para recibir `fechaLimite` y omitir tareas vencidas hace mÃƒÆ’Ã‚Â¡s de 7 dÃƒÆ’Ã‚Â­as.
- **ViewModels:** `HomeViewModel` ahora calcula dinÃƒÆ’Ã‚Â¡micamente la `fechaLimite` y la pasa al `ObtenerTareasPendientesUseCase`.
- **UI:** Las tarjetas de "Tareas PrÃƒÆ’Ã‚Â³ximas" ahora son clickeables (navegan al detalle de la campaÃƒÆ’Ã‚Â±a asociada).
- **UI:** Tratamiento visual condicional: tareas recientes vencidas se muestran con color rojo tenue.
- **UI:** Se agregÃƒÆ’Ã‚Â³ el botÃƒÆ’Ã‚Â³n "Ver todas" que redirige a la lista completa de tareas de la app.
- **Rama:** `fix/issue-285-dashboard-tareas`

**[2026-08-11] - [#287] fix(login): Saludo muestra nombre de usuario en vez de Invitado**
- **ViewModels:** `LoginViewModel` inyecta ahora `SessionManager` y luego del inicio de sesiÃƒÆ’Ã‚Â³n persistirÃƒÆ’Ã‚Â¡ en DataStore el nombre real del usuario recibido del backend.
- **Rama:** `fix/issue-287-saludo-usuario`

**[2026-08-11] - [#290] fix(campanias): ValidaciÃƒÆ’Ã‚Â³n estricta de fechas pasadas en creaciÃƒÆ’Ã‚Â³n**
- **Dominio:** 
  - Creado `ValidarDatosCampaniaUseCase` para concentrar la lÃƒÆ’Ã‚Â³gica de validaciÃƒÆ’Ã‚Â³n (nombre, cultivo y control estricto de no permitir fechas anteriores a hoy, ignorando la regla en modo ediciÃƒÆ’Ã‚Â³n).
  - AÃƒÆ’Ã‚Â±adida capa extra de defensa en `CrearCampaniaUseCase` para lanzar excepciÃƒÆ’Ã‚Â³n si la fecha es menor a hoy (medianoche).
- **ViewModels:** `CampaniaFormViewModel` limpiado completamente. Toda su lÃƒÆ’Ã‚Â³gica condicional fue delegada al nuevo caso de uso, dedicÃƒÆ’Ã‚Â¡ndose exclusivamente a actualizar la UI.
- **UI:** En `FormularioCampaniaScreen`, se configurÃƒÆ’Ã‚Â³ `selectableDates` en el `rememberDatePickerState` para deshabilitar visualmente fechas anteriores a hoy, mejorando sustancialmente la UX.
- **Rama:** `fix/campanias-validacion-fechas`

**[2026-08-11] - [#289] fix(insumos): ValidaciÃƒÆ’Ã‚Â³n de Formulario y DelegaciÃƒÆ’Ã‚Â³n a Dominio**
- **Dominio:** Creado `ValidarInsumoUseCase` para evaluar la obligatoriedad de `nombre` y `categorÃƒÆ’Ã‚Â­a`. Nota: El campo `unidad` no fue incluido en la validaciÃƒÆ’Ã‚Â³n porque no existe en la arquitectura actual del proyecto.
- **ViewModels:** 
  - `FormularioInsumoViewModel` modificado para consumir el caso de uso y exponer un estado ÃƒÆ’Ã‚Âºnico `isGuardarHabilitado`.
  - `InsumoCatalogoViewModel` modificado para inyectar el caso de uso y exponer una funciÃƒÆ’Ã‚Â³n de delegaciÃƒÆ’Ã‚Â³n de validaciÃƒÆ’Ã‚Â³n.
- **UI:** 
  - `FormularioInsumoScreen` muestra mensajes de error en los campos basÃƒÆ’Ã‚Â¡ndose enteramente en el estado unificado, eliminando lÃƒÆ’Ã‚Â³gica de negocio visual.
  - `CatalogoInsumosScreen` refactorizado para el diÃƒÆ’Ã‚Â¡logo inline y agregado un `SnackbarHost` para observar errores del ViewModel.
- **Rama:** `fix/insumos-validacion-formulario`

**[2026-08-02] - [#302] feat(reportes): Implementar ComparaciÃƒÆ’Ã‚Â³n Real entre CampaÃƒÆ’Ã‚Â±as**
- **Dominio:** `ReportesViewModel` ahora expone `cosechasA` y `cosechasB` asociadas a las campaÃƒÆ’Ã‚Â±as seleccionadas en el comparador.
- **UI:** En `ReportesRendimientoScreen`, la secciÃƒÆ’Ã‚Â³n de "MÃƒÆ’Ã‚Â©tricas Comparativas" ahora muestra los verdaderos totales de Costo de Insumos y Rendimiento (Cosechas) para la CampaÃƒÆ’Ã‚Â±a A y la CampaÃƒÆ’Ã‚Â±a B.
- **UI:** Se reemplazÃƒÆ’Ã‚Â³ el `GraficoEvolucionPlaceholder` por un `DoubleBarIndicator`, que consiste en barras de progreso compuestas (Jetpack Compose) para representar visual y proporcionalmente la diferencia de Costos y Rendimiento entre ambas campaÃƒÆ’Ã‚Â±as seleccionadas.
- **Rama:** `feat/comparacion-campanias` (stacked sobre `feat/grafico-desglose-cosechas`)
- **Dominio y UI:** Agregado el estado `desgloseCosechasData` al `ReportesViewModel` que filtra y agrupa dinÃƒÆ’Ã‚Â¡micamente el listado de cosechas en base a su destino (Almacenada vs Vendida/Reservada).
- **UI:** AÃƒÆ’Ã‚Â±adido un nuevo grÃƒÆ’Ã‚Â¡fico `PieChart` en `ReportesRendimientoScreen` para visualizar visualmente las proporciones del destino de las cosechas de la campaÃƒÆ’Ã‚Â±a activa.
- **Tests:** Creado caso de prueba en `ReportesViewModelTest` para asegurar la correcta agrupaciÃƒÆ’Ã‚Â³n matemÃƒÆ’Ã‚Â¡tica de las cosechas.
- **Rama:** `feat/grafico-desglose-cosechas` (stacked sobre `feat/reporte-insumos-mejorado`)
- **ExportaciÃƒÆ’Ã‚Â³n:** El exportador (`ReportExporter`) ahora recibe y pinta el nombre de la campaÃƒÆ’Ã‚Â±a en los archivos CSV y PDF generados. El nombre del archivo sugerido en el `FilePicker` ahora incluye el nombre de la campaÃƒÆ’Ã‚Â±a.
- **ValidaciÃƒÆ’Ã‚Â³n UI:** Se agregÃƒÆ’Ã‚Â³ una guardia en `ReportesRendimientoScreen` que verifica si hay una campaÃƒÆ’Ã‚Â±a seleccionada antes de abrir el `FilePicker`, mostrando un `Toast` si es `null`.
- **Rama:** `feat/reporte-insumos-mejorado` (stacked sobre `feat/migracion-db-insumos`)
- **Base de Datos:** MigraciÃƒÆ’Ã‚Â³n a versiÃƒÆ’Ã‚Â³n 5 (`MIGRATION_4_5`) usando copias de tabla temporales para eliminar la columna `unidad` de Insumos y Cosechas (limitaciÃƒÆ’Ã‚Â³n de SQLite).
- **Dominio y UI:** EliminaciÃƒÆ’Ã‚Â³n del campo `unidad` explÃƒÆ’Ã‚Â­cito en todo el cÃƒÆ’Ã‚Â³digo; se asume Kg/L de manera implÃƒÆ’Ã‚Â­cita para simplificar el modelo y la UI.
- **Tests actualizados** para no requerir o asertar por el campo `unidad`.
- **Rama:** `feat/migracion-db-insumos` (stacked sobre `feat/campanas-historial`)

**[2026-07-29] - [#299] fix(reportes): Eliminar datos mockeados en Dashboard y reestructurar pantalla Reportes**
- **Dashboard (`DashboardOperacionesScreen.kt`):** Eliminadas las tarjetas hardcodeadas "Clima 24Ãƒâ€šÃ‚Â°C" y "Salud Lotes 90% ÃƒÆ’Ã¢â‚¬Å“ptimo". El contenido restante sube automÃƒÆ’Ã‚Â¡ticamente.
- **`ReportesViewModel.kt` reescrito:** Se reemplaza `ObtenerTodosLosInsumosVinculadosUseCase` por `ObtenerInsumosVinculadosUseCase(campaniaId)` contextual. Se inyectan `ObtenerCampaniasUseCase` y `ObtenerCosechasPorCampaniaUseCase`. Nuevos StateFlows: `campanias`, `campaniaIndividual`, `insumosIndividual`, `cosechasIndividual`, `campaniaA/B`, `insumosA/B`. `pieChartData` y `exportableData` ahora son contextuales a la campaÃƒÆ’Ã‚Â±a seleccionada.
- **`ReportesRendimientoScreen.kt` reestructurada en dos secciones:**
  - *SecciÃƒÆ’Ã‚Â³n 1 ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬ï¿½ EstadÃƒÆ’Ã‚Â­sticas individuales:* Dropdown con campaÃƒÆ’Ã‚Â±as reales de BD, tarjetas de costo de insumos y total cosechado, PieChart contextual (por campaÃƒÆ’Ã‚Â±a seleccionada).
  - *SecciÃƒÆ’Ã‚Â³n 2 ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬ï¿½ Comparador:* Dos dropdowns con campaÃƒÆ’Ã‚Â±as reales, `CardMetricaComparativa` con costo real de insumos A vs B, placeholder para grÃƒÆ’Ã‚Â¡fico de evoluciÃƒÆ’Ã‚Â³n (scope #302).
- **ExportaciÃƒÆ’Ã‚Â³n CSV/PDF:** Ahora exporta los insumos de la campaÃƒÆ’Ã‚Â±a seleccionada en SecciÃƒÆ’Ã‚Â³n 1 (en lugar de todos los insumos globales).
- **Tests creados:** `ReportesViewModelTest.kt` con 5 casos Given-When-Then (JUnit 4 + MockK + Turbine).
- **`docs/plan_de_pruebas.md` actualizado** con subsecciÃƒÆ’Ã‚Â³n `ReportesViewModel ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬ï¿½ StateFlows contextuales [#299]`.
- **Nota de scope:** La lÃƒÆ’Ã‚Â³gica de `campaniaA/B` e `insumosA/B` es un paso preparatorio del Issue #302. Documentado en la PR con `Partial-scope: #302`.
- **Rama:** `fix/datos-mock-dashboard-reportes` (stacked sobre `fix/tab-tareas-no-actualiza`)

**[2026-07-22] - [#292] fix(campania): PestaÃƒÆ’Ã‚Â±a Tareas no actualiza datos al cambiar de campaÃƒÆ’Ã‚Â±a**
- **Causa raÃƒÆ’Ã‚Â­z doble resuelta:**
  - `TabTareas` usaba `hiltViewModel(key = "tab_tareas")` con key estÃƒÆ’Ã‚Â¡tica, haciendo que Hilt reutilizara la misma instancia del `TareaViewModel` sin importar la campaÃƒÆ’Ã‚Â±a activa.
  - El `campaniaId` recibido como parÃƒÆ’Ã‚Â¡metro en `TabTareas` nunca se propagaba al ViewModel (que iniciaba con `null` desde `SavedStateHandle`).
- **`TareaViewModel.kt` modificado:** Se agrega el mÃƒÆ’Ã‚Â©todo pÃƒÆ’Ã‚Âºblico `sincronizarCampania(id: Int)` que actualiza `_campaniaIdSeleccionada` solo si el valor difiere del actual (idempotente, evita emisiones innecesarias en el StateFlow).
- **`DetalleCampaniaScreen.kt` modificado:**
  - `TabTareas`: key cambiada a `"tab_tareas_$campaniaId"` + `LaunchedEffect(campaniaId)` que invoca `sincronizarCampania()` como segunda lÃƒÆ’Ã‚Â­nea de defensa.
  - `TabInsumos`: key corregida de `"tab_insumos"` a `"tab_insumos_$campaniaId"` (mismo patrÃƒÆ’Ã‚Â³n de bug identificado).
- **Tests creados:** `TareaViewModelTest.kt` con 5 casos Given-When-Then (JUnit 4 + MockK + Turbine).
- **`docs/plan_de_pruebas.md` actualizado** con subsecciÃƒÆ’Ã‚Â³n `TareaViewModel ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬ï¿½ sincronizarCampania() [#292]`.
- **Rama:** `fix/tab-tareas-no-actualiza` (stacked sobre `fix/permiso-camara-observaciones`)

**[2026-06-30] - [#283] fix: Crash al Abrir la CÃƒÆ’Ã‚Â¡mara ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬ï¿½ Permiso CAMERA no Solicitado**
- **Causa raÃƒÆ’Ã‚Â­z resuelta:** La app lanzaba `cameraLauncher.launch(uri)` directamente sin verificar ni solicitar el permiso `CAMERA` en runtime, causando un `SecurityException` en Android 6.0+ (API 23).
- **Nuevo mÃƒÆ’Ã‚Â³dulo creado:** `presentation/util/CameraUtils.kt` con tres responsabilidades separadas:
  - `EstadoPermisoCamara`: State holder observable con `mutableStateOf` para `permisoConcedido`, `mostrarRazon` y `denegadoPermanente`.
  - `recordarPermisoCamara()`: Composable que gestiona el ciclo completo del permiso usando `ActivityResultContracts.RequestPermission()` y `ActivityCompat.shouldShowRequestPermissionRationale()` para distinguir denegaciÃƒÆ’Ã‚Â³n temporal vs. permanente.
  - `DialogoRazonPermisoCamara()`: `AlertDialog` de rationale que se muestra en primera denegaciÃƒÆ’Ã‚Â³n.
  - `abrirAjustesPermiso()`: Helper que lanza `Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)` cuando el permiso es denegado permanentemente.
- **`ObservacionesScreen.kt` actualizado:** BotÃƒÆ’Ã‚Â³n "Tomar foto" ahora verifica `controlPermiso.permisoConcedido` antes de lanzar la cÃƒÆ’Ã‚Â¡mara. Si no estÃƒÆ’Ã‚Â¡ concedido, guarda la acciÃƒÆ’Ã‚Â³n pendiente y llama a `controlPermiso.solicitar()`. `SnackbarHost` aÃƒÆ’Ã‚Â±adido al `Box` para feedback visual.
- **Flujos cubiertos:** Permiso ya concedido (directo a cÃƒÆ’Ã‚Â¡mara) Ãƒâ€šÃ‚Â· Primera denegaciÃƒÆ’Ã‚Â³n (muestra rationale) Ãƒâ€šÃ‚Â· DenegaciÃƒÆ’Ã‚Â³n permanente (Snackbar con botÃƒÆ’Ã‚Â³n "Abrir Ajustes").
- **Rama:** `fix/permiso-camara-observaciones`

**[2026-06-10] - Cosechas: Fix Crash FK [#284] y ValidaciÃƒÆ’Ã‚Â³n del Formulario [#293]**
- **Issue 7 (#284):** Eliminado el crash `FOREIGN KEY constraint failed` al guardar una cosecha con `campaniaId = -1`. `FormularioCosechaViewModel` ahora inicializa `campaniaId` como `null` cuando `SavedStateHandle` no recibe un id vÃƒÆ’Ã‚Â¡lido (`takeIf { it != -1 }`), inyecta `ObtenerCampaniasUseCase` para exponer `campanias` y `onCampaniaChange()`, y `guardar()` valida `campaniaId == null` emitiendo `errorCampania = "Debe seleccionar una campaÃƒÆ’Ã‚Â±a"` antes de intentar la inserciÃƒÆ’Ã‚Â³n.
- **Issue 7 (UI):** `FormularioCosechaScreen` ahora muestra el componente `SelectorCampania` (etiqueta "CampaÃƒÆ’Ã‚Â±a vinculada") con texto de error debajo cuando falta seleccionar campaÃƒÆ’Ã‚Â±a. El botÃƒÆ’Ã‚Â³n "Guardar" queda deshabilitado mientras `campaniaId == null`.
- **Issue 12 (#293):** `guardar()` ya no hace retorno silencioso con campos vacÃƒÆ’Ã‚Â­os: setea `errorCantidad = "La cantidad es obligatoria"`. Adaptado a migraciÃƒÆ’Ã‚Â³n DB v5 (campo `unidad` eliminado del modelo).
- **Issue 12 (UI):** BotÃƒÆ’Ã‚Â³n "Guardar" deshabilitado si hay errores o campos obligatorios vacÃƒÆ’Ã‚Â­os (`cantidad`, `campaniaId`).
- **Testing:** Creado `FormularioCosechaViewModelTest` con 5 casos (MockK + coroutines-test): sin campaÃƒÆ’Ã‚Â±a, cantidad vacÃƒÆ’Ã‚Â­a, almacenado vÃƒÆ’Ã‚Â¡lido y venta vÃƒÆ’Ã‚Â¡lida.
- **DocumentaciÃƒÆ’Ã‚Â³n:** Marcados como completos Issues #284 y #293 en `.context/roadmap_iteracion_2.md`; agregados escenarios Given-When-Then en `docs/plan_de_pruebas.md`.
- **Rama:** `fix-cosechas-estabilizacion`

**[2026-06-23] - DocumentaciÃƒÆ’Ã‚Â³n de Entrega y Casos de Uso**
- ActualizaciÃƒÆ’Ã‚Â³n de `docs/FLOW.md` incorporando diagramas de flujo interactivos Mermaid para cada una de las 8 ramas principales del sistema.
- CreaciÃƒÆ’Ã‚Â³n de `docs/diferencias_casos_de_uso_2025_2026.md` contrastando la propuesta teÃƒÆ’Ã‚Â³rica original (2025) con la implementaciÃƒÆ’Ã‚Â³n final en Clean Architecture (2026), aplicando el formato tabular de casos de uso requerido en la cursada.

**[2026-06-09] - PlanificaciÃƒÆ’Ã‚Â³n y DivisiÃƒÆ’Ã‚Â³n de IteraciÃƒÆ’Ã‚Â³n 2**
- ActualizaciÃƒÆ’Ã‚Â³n de `docs/bugs_identificados.md` refinando Issues 8, 15, 18, 19, 20 en relaciÃƒÆ’Ã‚Â³n al rediseÃƒÆ’Ã‚Â±o lineal, validaciÃƒÆ’Ã‚Â³n de insumos con Flow y unificaciÃƒÆ’Ã‚Â³n a Toneladas.
- CreaciÃƒÆ’Ã‚Â³n de `.context/iteracion_2.md` con el roadmap maestro priorizado de L1 a L5.
- CreaciÃƒÆ’Ã‚Â³n de `docs/roadmap_desarrolladores.md` organizando las tareas para ejecuciÃƒÆ’Ã‚Â³n en paralelo por 3 desarrolladores, con un desglose granular de ramas Git y orden de ejecuciÃƒÆ’Ã‚Â³n.

**[2026-06-09] - SesiÃƒÆ’Ã‚Â³n de Pruebas Manuales APK Debug ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬ï¿½ DocumentaciÃƒÆ’Ã‚Â³n de 23 Issues**
- Reescritura completa de `docs/bugs_identificados.md` con 23 issues organizados por severidad (L1-L5).
- **L1 (Crashes):** Crash por permisos de cÃƒÆ’Ã‚Â¡mara no solicitados (Issue 6), crash por FK constraint al registrar cosecha sin campaniaId (Issue 7).
- **L2 (Bugs Funcionales):** Saludo siempre muestra "Invitado" (Issue 3 actualizado), catÃƒÆ’Ã‚Â¡logo de insumos sin validaciÃƒÆ’Ã‚Â³n completa (Issue 8), campaÃƒÆ’Ã‚Â±as permiten fechas pasadas (Issue 9), campo hora de tareas sin restricciones (Issue 10), tabs tareas/insumos no se actualizan al cambiar de campaÃƒÆ’Ã‚Â±a (Issue 11), validaciÃƒÆ’Ã‚Â³n faltante en formulario cosechas (Issue 12).
- **L3 (Features Faltantes):** EdiciÃƒÆ’Ã‚Â³n/eliminaciÃƒÆ’Ã‚Â³n de observaciones (Issue 13) y cosechas (Issue 14), separaciÃƒÆ’Ã‚Â³n campaÃƒÆ’Ã‚Â±as activas/inactivas (Issue 15), navegaciÃƒÆ’Ã‚Â³n lateral entre campaÃƒÆ’Ã‚Â±as (Issue 16), campo hectÃƒÆ’Ã‚Â¡reas en cosecha (Issue 17).
- **L4 (Reportes):** Selector de campaÃƒÆ’Ã‚Â±a en grÃƒÆ’Ã‚Â¡fico de insumos (Issue 18), grÃƒÆ’Ã‚Â¡fico desglose cosechas (Issue 19), comparaciÃƒÆ’Ã‚Â³n real entre campaÃƒÆ’Ã‚Â±as (Issue 20).
- **L5 (UX):** Bloquear modo oscuro (Issue 21), teclado cubre campos al escribir (Issue 22), tarjetas mock del dashboard (Issue 23).

**[2026-06-09] - GeneraciÃƒÆ’Ã‚Â³n de APK de Debug para Pruebas**
- Se generÃƒÆ’Ã‚Â³ el archivo APK en versiÃƒÆ’Ã‚Â³n de depuraciÃƒÆ’Ã‚Â³n (debug) mediante Gradle para facilitar las pruebas manuales en dispositivos fÃƒÆ’Ã‚Â­sicos.

**[2026-06-04] - Fase 12: SincronizaciÃƒÆ’Ã‚Â³n, Tests y Refactor (Issue 12.2)**
- **Roadmap:** Sincronizados y marcados como completos los Issues silentes de permisos, exportaciÃƒÆ’Ã‚Â³n/importaciÃƒÆ’Ã‚Â³n de base de datos, BottomNav y Use Cases.
- **Tests Instrumentados:** Diagnosticados y programados para soluciÃƒÆ’Ã‚Â³n los errores de compilaciÃƒÆ’Ã‚Â³n de DAOs (`CampaniaDaoTest` y `CampaniaInsumoDaoTest`) que fallaban por nomenclaturas antiguas.
- **Refactor:** AÃƒÆ’Ã‚Â±adida la tarea para limpiar las importaciones comodÃƒÆ’Ã‚Â­n (`*`) a lo largo del proyecto para apegarse a las mejores prÃƒÆ’Ã‚Â¡cticas de Kotlin.

**[2026-06-04] - Hotfix: CorrecciÃƒÆ’Ã‚Â³n de compilaciÃƒÆ’Ã‚Â³n y rebase de PR**
- **Fix:** Corregido error de compilaciÃƒÆ’Ã‚Â³n en `ReportesViewModel.kt` causado por una importaciÃƒÆ’Ã‚Â³n faltante de la funciÃƒÆ’Ã‚Â³n de extensiÃƒÆ’Ã‚Â³n `map` de `StateFlow`.
- **Git:** Desecho un commit de merge local y rebasada la rama `feature/171` sobre `main` resolviendo los conflictos en `CHANGELOG.md` para permitir un "Rebase and merge" limpio en GitHub.

**[2026-06-02] - Fase 7: ImplementaciÃƒÆ’Ã‚Â³n de Testing y CI/CD (Issue 1 Completo)**
- **Testing Unitario (Dominio):** Refactor de aserciones para corrutinas (cambio de `assertThrows` por `try-catch`) para arreglar fallos silenciosos. Ampliada la cobertura aÃƒÆ’Ã‚Â±adiendo pruebas a Casos de Uso faltantes (`RegistroUseCaseTest`, `EditarCampaniaUseCaseTest`, `EditarTareaUseCaseTest`, `ObtenerCampaniasUseCaseTest`), subiendo la cobertura del paquete de 26% a 36.2%.
- **Testing Unitario (PresentaciÃƒÆ’Ã‚Â³n):** Implementado `LoginViewModelTest` usando Turbine para testear la emisiÃƒÆ’Ã‚Â³n asÃƒÆ’Ã‚Â­ncrona de `StateFlow`.
- **Testing Instrumentado (Datos):** Creados tests en memoria para los DAOs (`UsuarioDaoTest`, `CampaniaDaoTest`, `CampaniaInsumoDaoTest`) simulando un entorno de base de datos Android real con SQLite.
- **Cobertura y CI/CD:** Corregida la tarea de GitHub Actions (`pr_tests.yml`) para invocar la variante correcta de Android (`koverHtmlReportDebug`), permitiendo la correcta lectura de reportes de cobertura en PRs.
- **DocumentaciÃƒÆ’Ã‚Â³n:** Actualizado `plan_de_pruebas.md` documentando el correcto uso de excepciones en corrutinas y el comando especÃƒÆ’Ã‚Â­fico de Kover.

**[2026-06-02] - Fase 7: PlanificaciÃƒÆ’Ã‚Â³n de Estrategia de Testing (Issue 1)**
- **Testing:** DefiniciÃƒÆ’Ã‚Â³n del stack tecnolÃƒÆ’Ã‚Â³gico (MockK, Turbine, Kover, AndroidX Test, Compose Rule).
- **DocumentaciÃƒÆ’Ã‚Â³n Viva:** CreaciÃƒÆ’Ã‚Â³n del documento `docs/plan_de_pruebas.md` que incluye:
  - AnÃƒÆ’Ã‚Â¡lisis detallado de discrepancias entre el diseÃƒÆ’Ã‚Â±o original (2025) y la arquitectura final implementada.
  - Escenarios BDD (Behavior-Driven Development) `Given-When-Then` para todos los mÃƒÆ’Ã‚Â³dulos de la aplicaciÃƒÆ’Ã‚Â³n (CampaÃƒÆ’Ã‚Â±as, Insumos, Tareas, Cosechas, Observaciones, Auth y Backup).
  - IntegraciÃƒÆ’Ã‚Â³n exhaustiva de Edge Cases (Casos de Borde).
  - Estrategias de comandos de ejecuciÃƒÆ’Ã‚Â³n local y metas de cobertura estricta (Kover 80% en domain, 70% en data).
- **Roadmap:** Actualizado `.context/RoadmapOP.md` con el progreso en el Issue 1 de la Fase 7.

**[2026-06-02] - Fase 6: ExportaciÃƒÆ’Ã‚Â³n de Reportes a Archivos (Issue 2)**
- **Dominio:** Creado modelo `InsumoResumen` para abstraer la informaciÃƒÆ’Ã‚Â³n exportable.
- **Utilidad:** Creada clase `ReportExporter` que utiliza SAF y el ContentResolver para escribir los archivos.
- **ExportaciÃƒÆ’Ã‚Â³n CSV:** Implementada conversiÃƒÆ’Ã‚Â³n de datos de gastos por insumo en formato CSV.
- **ExportaciÃƒÆ’Ã‚Â³n PDF:** Implementada generaciÃƒÆ’Ã‚Â³n de documento PDF usando la API nativa de Android `PdfDocument`, dibujando tablas en `Canvas`.
- **UI & ViewModel:** Integrados los launchers `ActivityResultContracts.CreateDocument` en `ReportesRendimientoScreen` y conectados a `ReportesViewModel`.
**[2026-06-01] - Fase 9: RefactorizaciÃƒÆ’Ã‚Â³n de Arquitectura DB y DocumentaciÃƒÆ’Ã‚Â³n de Bugs**
- **Base de Datos:** Eliminado el soporte de borrado lÃƒÆ’Ã‚Â³gico (soft-delete) de la tabla intermedia `CampaniaInsumoEntity`, aplicando borrado fÃƒÆ’Ã‚Â­sico estricto (`DELETE`) en `CampaniaInsumoDao` para mantener la integridad referencial limpia.
- **KSP Fix:** Solucionados conflictos de compilaciÃƒÆ’Ã‚Â³n de Room (KSP) causados por colisiÃƒÆ’Ã‚Â³n de anotaciones `@Delete` y `@Query`.
- **Limpieza de CÃƒÆ’Ã‚Â³digo:** Removida la propiedad `activo` del dominio, mappers y datos semilla de insumos. Se incrementÃƒÆ’Ã‚Â³ la base de datos a la versiÃƒÆ’Ã‚Â³n 4 forzando `fallbackToDestructiveMigration()`.
- **Limpieza de Repositorio:** AÃƒÆ’Ã‚Â±adidos archivos de configuraciÃƒÆ’Ã‚Â³n locales de Android Studio (`.idea/misc.xml`, `.idea/deploymentTargetSelector.xml`) al `.gitignore` y eliminados del rastreo de git.
- **DocumentaciÃƒÆ’Ã‚Â³n:** Creado el archivo `docs/bugs_identificados.md` documentando 4 problemas conocidos listos para la prÃƒÆ’Ã‚Â³xima iteraciÃƒÆ’Ã‚Â³n.

**[2026-06-01] - Optimizaciones de Entorno y Datos de Prueba**
- Migradas rutas locales del JDK (`org.gradle.java.home`) y cachÃƒÆ’Ã‚Â© (`gradle.user.home`) desde `gradle.properties` hacia `local.properties` para prevenir sobreescrituras en repositorio compartido.
- Restaurado botÃƒÆ’Ã‚Â³n condicional de "Cargar datos de prueba" (`BuildConfig.DEBUG`) en `ConfiguracionDBScreen` manteniendo compatibilidad con el nuevo soft-delete (`activo`) de Insumos en el `DataSeederImpl`.
**[2026-05-31] - ImplementaciÃƒÆ’Ã‚Â³n de Backup y CorrecciÃƒÆ’Ã‚Â³n de RegresiÃƒÆ’Ã‚Â³n**
- Implementadas funcionalidades de exportaciÃƒÆ’Ã‚Â³n e importaciÃƒÆ’Ã‚Â³n de base de datos (CU12, CU13) en `ConfiguracionDBScreen`.
- Creados Casos de Uso `CrearBackupUseCase` y `RestaurarBackupUseCase`.
- **Hotfix:** Revertida sobreescritura accidental del archivo `screens.kt` que habÃƒÆ’Ã‚Â­a eliminado la navegaciÃƒÆ’Ã‚Â³n moderna con `NavHost`.
- Restaurados `CosechaDao.kt`, `gradle.properties` y `.idea/misc.xml` para eliminar cambios locales subidos por error en la PR.


**[2026-05-25] - ActualizaciÃƒÆ’Ã‚Â³n de Roadmap y BotÃƒÆ’Ã‚Â³n Invitado**
- ActualizaciÃƒÆ’Ã‚Â³n de `.context/RoadmapOP.md` con issues finalizados de fase 8, 10 y 11.
- AÃƒÆ’Ã‚Â±adido botÃƒÆ’Ã‚Â³n "Invitado" para debug en la pantalla de login (F8/Issue 1.8).

**[2026-05-25] - FinalizaciÃƒÆ’Ã‚Â³n de requerimientos fase 2**
- Implementado swipe semanal para gestiÃƒÆ’Ã‚Â³n visual de Tareas.
- Implementado catÃƒÆ’Ã‚Â¡logo de Insumos con ÃƒÆ’Ã‚Â­conos e integraciÃƒÆ’Ã‚Â³n a base de datos.
- Integrado YCharts para grÃƒÆ’Ã‚Â¡ficos de pie en Dashboard de Reportes.
- AÃƒÆ’Ã‚Â±adido soporte de Soft-Delete (activo) en vinculaciÃƒÆ’Ã‚Â³n de Insumos.
- Forzada versiÃƒÆ’Ã‚Â³n de Room DB a 2 con migraciÃƒÆ’Ã‚Â³n destructiva (entorno dev).
- AÃƒÆ’Ã‚Â±adida DataSeed con iconos e items eliminados para pruebas de UI.
- Solucionados errores WorkerDaemon configurando gradle.user.home en entorno local.
- Actualizados Roadmap y documentaciÃƒÆ’Ã‚Â³n de Arquitectura.

**[2026-05-20] - IntegraciÃƒÆ’Ã‚Â³n de 20 issues de auditorÃƒÆ’Ã‚Â­a en RoadmapOP.md**
- Fusionados los 20 issues detectados en auditorÃƒÆ’Ã‚Â­a de cÃƒÆ’Ã‚Â³digo dentro del `RoadmapOP.md` como Fases 8-12, organizados por criticidad.
- Agregadas notas de referencia cruzada y de dependencia entre issues.
- Eliminado `.context/IssuesPendientes.md` (contenido migrado a RoadmapOP.md).

**[2026-05-19] - Implementar autenticaciÃƒÆ’Ã‚Â³n, refactor Clean Arch y conectar Use Cases muertos**
- **Issue 1 (Login completo):** CreaciÃƒÆ’Ã‚Â³n de `UsuarioDao`, modelo de dominio `Usuario`, mappers, `LoginUseCase` (SHA-256), `RegistroUseCase` y `LoginViewModel`. ConexiÃƒÆ’Ã‚Â³n de `LoginScreen` y `RegistroScreen`.
- **Issue 12 (Refactor Clean Arch):** CreaciÃƒÆ’Ã‚Â³n de 6 UseCases contenedores para queries reactivas. RefactorizaciÃƒÆ’Ã‚Â³n de 6 ViewModels para inyectar UseCases en lugar de repositorios (`CampaniaFormViewModel`, `CampaniaDetailViewModel`, `TareaViewModel`, `CosechaViewModel`, `InsumoVinculacionViewModel` y `ObservacionViewModel`).
- **Issue 13 (Use Cases muertos):** ConexiÃƒÆ’Ã‚Â³n de `EditarTareaUseCase`, `EliminarTareaUseCase`, `EditarInsumoCatalogoUseCase` y creaciÃƒÆ’Ã‚Â³n de `EliminarInsumoCatalogoUseCase`. DiÃƒÆ’Ã‚Â¡logo de ediciÃƒÆ’Ã‚Â³n inline en `CatalogoInsumosScreen`.

**[2026-05-18] - Refactor de GestiÃƒÆ’Ã‚Â³n de CampaÃƒÆ’Ã‚Â±as (F4/Issue9)**
- CreaciÃƒÆ’Ã‚Â³n de `GestionCampaniasViewModel` con carga reactiva de campaÃƒÆ’Ã‚Â±as desde `ObtenerCampaniasUseCase`.
- CreaciÃƒÆ’Ã‚Â³n de `GestionCampaniasScreen` reemplazando `GestionParcelasScreen` (mock) con lista real desde BD.
- CorrecciÃƒÆ’Ã‚Â³n de navegaciÃƒÆ’Ã‚Â³n: `onGoToDetail` ahora recibe `campaniaId` real del item clickeado.
- Estado vacÃƒÆ’Ã‚Â­o con icono e indicaciÃƒÆ’Ã‚Â³n visual para crear campaÃƒÆ’Ã‚Â±a.

**[2026-05-18] - ImplementaciÃƒÆ’Ã‚Â³n de MÃƒÆ’Ã‚Â³dulo de Observaciones (F4/Issue8)**
- CreaciÃƒÆ’Ã‚Â³n de `ObservacionViewModel` con carga reactiva de observaciones por campaÃƒÆ’Ã‚Â±a desde BD.
- CreaciÃƒÆ’Ã‚Â³n de `FormularioObservacionViewModel` con formulario reactivo, validaciÃƒÆ’Ã‚Â³n y conexiÃƒÆ’Ã‚Â³n a `GuardarObservacionUseCase`.
- RediseÃƒÆ’Ã‚Â±o de `ObservacionesScreen` con formulario para guardar + listado reactivo de observaciones registradas.
- ActualizaciÃƒÆ’Ã‚Â³n de `TabObservaciones` en `DetalleCampaniaScreen` con ViewModel por campaÃƒÆ’Ã‚Â±a y ÃƒÆ’Ã‚Âºltimas 3 observaciones.

**[2026-05-18] - ImplementaciÃƒÆ’Ã‚Â³n completa CosechaNoAlmacenada (Venta/Reserva)**
- CreaciÃƒÆ’Ã‚Â³n de `CosechaNoAlmacenadaDao`, modelo de dominio `CosechaNoAlmacenada`, repositorio e implementaciÃƒÆ’Ã‚Â³n.
- CreaciÃƒÆ’Ã‚Â³n de `RegistrarCosechaConVentaUseCase` que inserta cosecha base + detalle de venta/reserva.
- ExposiciÃƒÆ’Ã‚Â³n del DAO en `DonElioDatabase` y DI en `DatabaseModule`/`RepositoryModule`.
- Mappers `toDomain()`/`toEntity()` para `CosechaNoAlmacenadaEntity`.
- `CosechaRepository.insertCosecha()` ahora retorna `Long` (ID generado).
- `CosechaViewModel` ampliado: `almacenadas` (filtrado) y `noAlmacenadasDetalle` (mapa idÃƒÂ¢Ã¢â‚¬Â Ã¢â‚¬â„¢detalle).
- `FormularioCosechaViewModel.guardar()` bifurca entre `RegistrarCosechaUseCase` y `RegistrarCosechaConVentaUseCase` segÃƒÆ’Ã‚Âºn checkbox.
- `CosechasScreen` muestra tipo y precio en cards de venta/reserva.
- `TabCosechas` en `DetalleCampaniaScreen` con key ÃƒÆ’Ã‚Âºnica por campaÃƒÆ’Ã‚Â±a y resumen real de ventas.

**[2026-05-18] - ImplementaciÃƒÆ’Ã‚Â³n de MÃƒÆ’Ã‚Â³dulo de Cosechas (F4/Issue7)**
- CreaciÃƒÆ’Ã‚Â³n de `CosechaViewModel` con carga reactiva de cosechas por campaÃƒÆ’Ã‚Â±a desde BD.
- CreaciÃƒÆ’Ã‚Â³n de `FormularioCosechaViewModel` con formulario reactivo, validaciÃƒÆ’Ã‚Â³n y conexiÃƒÆ’Ã‚Â³n a `RegistrarCosechaUseCase`.
- RefactorizaciÃƒÆ’Ã‚Â³n de `CosechasScreen` con datos reales, separaciÃƒÆ’Ã‚Â³n visual almacenadas/no-almacenadas.
- RefactorizaciÃƒÆ’Ã‚Â³n de `FormularioCosechaScreen` con ViewModel, DatePicker, validaciÃƒÆ’Ã‚Â³n de cantidad y spinner de guardado.
- Agregado parÃƒÆ’Ã‚Â¡metro `campaniaId` opcional a `NavRoute.FormularioCosecha`.
- ActualizaciÃƒÆ’Ã‚Â³n de `TabCosechas` en `DetalleCampaniaScreen` con datos reales desde BD.

**[2026-05-15] - Seed data para testing (debug source set)**
- ConfiguraciÃƒÆ’Ã‚Â³n de `sourceSets { debug { java.srcDir("src/debug/java") } }` en `app/build.gradle.kts`.
- CreaciÃƒÆ’Ã‚Â³n de interfaz `DataSeeder` en `src/main/` con `@BindsOptionalOf` para inyecciÃƒÆ’Ã‚Â³n opcional en Hilt.
- CreaciÃƒÆ’Ã‚Â³n de `DataSeederImpl` en `src/debug/` con 4 campaÃƒÆ’Ã‚Â±as, 8 insumos, 8 tareas, 3 cosechas, 5 vinculaciones y 4 observaciones con fechas fijas mediante `Calendar`.
- CreaciÃƒÆ’Ã‚Â³n de `SeedModule` en `src/debug/` proveyendo `DataSeederImpl` vÃƒÆ’Ã‚Â­a Hilt.
- CreaciÃƒÆ’Ã‚Â³n de `ConfiguracionDBViewModel` con estado `SeedState` (Idle/Cargando/Exito/Error) y mÃƒÆ’Ã‚Â©todo `cargarDatosPrueba()`.
- BotÃƒÆ’Ã‚Â³n "Cargar datos de prueba" en `ConfiguracionDBScreen` visible solo en builds debug, con spinner y Snackbar de feedback.
- ActualizaciÃƒÆ’Ã‚Â³n de `.context/RoadmapOP.md` con Issue 10 de Fase 4.

**[2026-05-15] - ImplementaciÃƒÆ’Ã‚Â³n de MÃƒÆ’Ã‚Â³dulo de Insumos (F4/Issue6)**
- CreaciÃƒÆ’Ã‚Â³n de `InsumoCatalogoViewModel` e `InsumoVinculacionViewModel` con carga reactiva desde BD.
- ConexiÃƒÆ’Ã‚Â³n de `CatalogoInsumosScreen` al catÃƒÆ’Ã‚Â¡logo real con `ObtenerCatalogoInsumosUseCase`.
- ConexiÃƒÆ’Ã‚Â³n de `FormularioInsumoScreen` a `CrearInsumoCatalogoUseCase` con validaciÃƒÆ’Ã‚Â³n y spinner.
- RefactorizaciÃƒÆ’Ã‚Â³n de `InsumosScreen` (vinculaciÃƒÆ’Ã‚Â³n) con datos reales, cÃƒÆ’Ã‚Â¡lculo `cantidad ÃƒÆ’Ã¢â‚¬â€ precio` formateado y atajo "Crear nuevo insumo" si no existe en catÃƒÆ’Ã‚Â¡logo.
- CreaciÃƒÆ’Ã‚Â³n de `FormularioInsumoViewModel` con estado reactivo.
- ActualizaciÃƒÆ’Ã‚Â³n de `TabInsumos` en `DetalleCampaniaScreen` con conteo real y total estimado.

**[2026-05-15] - ImplementaciÃƒÆ’Ã‚Â³n de MÃƒÆ’Ã‚Â³dulo de Tareas (F4/Issue5)**
- CreaciÃƒÆ’Ã‚Â³n de `TareaViewModel` con carga reactiva de tareas por campaÃƒÆ’Ã‚Â±a desde BD.
- CreaciÃƒÆ’Ã‚Â³n de `NuevaTareaViewModel` con formulario reactivo, validaciÃƒÆ’Ã‚Â³n y conexiÃƒÆ’Ã‚Â³n a `CrearTareaUseCase`.
- RefactorizaciÃƒÆ’Ã‚Â³n de `TareasScreen` con datos reales, checkbox de confirmaciÃƒÆ’Ã‚Â³n con `ConfirmarTareaUseCase`, feedback visual (tachado + atenuado).
- RefactorizaciÃƒÆ’Ã‚Â³n de `NuevaTareaScreen` con `DatePickerDialog` M3, validaciÃƒÆ’Ã‚Â³n de nombre y spinner de guardado.
- ActualizaciÃƒÆ’Ã‚Â³n de `TabTareas` en `DetalleCampaniaScreen` con lista real de pendientes y resumen.
- ActualizaciÃƒÆ’Ã‚Â³n de `NavRoute.NuevaTarea` con `campaniaId` opcional.

**[2026-05-14] - Correcciones de bugs y navegaciÃƒÆ’Ã‚Â³n (F4/Issue4)**
- Bugfix: `CrearCampaniaUseCase` ahora acepta parÃƒÆ’Ã‚Â¡metro `cultivo` ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬ï¿½ el campo ya no se pierde al crear campaÃƒÆ’Ã‚Â±as nuevas.
- Bugfix: `CampaniaFormViewModel` pasa `cultivo` al `crearCampaniaUseCase`.
- Bugfix: `GestionParcelasScreen`, `TareasScreen`, `InsumosScreen`, `CosechasScreen`, `ObservacionesScreen` ya no hardcodean `campaniaId=1` ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬ï¿½ todas las rutas aceptan `campaniaId` opcional y lo propagan correctamente.
- Limpieza: eliminado parÃƒÆ’Ã‚Â¡metro `onEditar` no usado en `HeaderCampania`.

**[2026-05-14] - Pantalla Detalle de CampaÃƒÆ’Ã‚Â±a con Tabs y encabezado fijo (F4/Issue4)**
- CreaciÃƒÆ’Ã‚Â³n de `CampaniaDetailViewModel` con `SavedStateHandle` para carga de campaÃƒÆ’Ã‚Â±a por ID + eliminaciÃƒÆ’Ã‚Â³n.
- RediseÃƒÆ’Ã‚Â±o de `DetalleCampaniaScreen` con TopAppBar dinÃƒÆ’Ã‚Â¡mico, encabezado fijo (nombre, cultivo, fechas, estado) y TabRow con 5 tabs: Info, Tareas, Insumos, Cosechas, Observaciones.
- Cada tab muestra resumen informativo y botÃƒÆ’Ã‚Â³n de navegaciÃƒÆ’Ã‚Â³n a su pantalla completa, pasando `campaniaId`.
- ActualizaciÃƒÆ’Ã‚Â³n de `screens.kt` con `navArgument("campaniaId")` extraÃƒÆ’Ã‚Â­do y pasado al ViewModel.
- NavegaciÃƒÆ’Ã‚Â³n desde detalle a ediciÃƒÆ’Ã‚Â³n de campaÃƒÆ’Ã‚Â±a (`onGoToEditar`) con el ID correcto.

**[2026-05-14] - ImplementaciÃƒÆ’Ã‚Â³n de Formulario ABM CampaÃƒÆ’Ã‚Â±as con validaciÃƒÆ’Ã‚Â³n y DatePicker (F4/Issue3)**
- CreaciÃƒÆ’Ã‚Â³n de `CampaniaFormViewModel` con `SavedStateHandle` para modo ediciÃƒÆ’Ã‚Â³n/creaciÃƒÆ’Ã‚Â³n.
- RefactorizaciÃƒÆ’Ã‚Â³n de `FormularioCampaniaScreen` con campos nombre/cultivo validados, DatePicker M3, botÃƒÆ’Ã‚Â³n guardar con spinner.
- ActualizaciÃƒÆ’Ã‚Â³n de `NavRoute.FormularioCampania` con `campaniaId` opcional vÃƒÆ’Ã‚Â­a query param.
- IntegraciÃƒÆ’Ã‚Â³n de `CrearCampaniaUseCase` (creaciÃƒÆ’Ã‚Â³n) y `EditarCampaniaUseCase` (ediciÃƒÆ’Ã‚Â³n) con `LaunchedEffect` para navegaciÃƒÆ’Ã‚Â³n post-guardado.

**[2026-05-14] - Refactor: divisiÃƒÆ’Ã‚Â³n de screens.kt en archivos individuales**
- SeparaciÃƒÆ’Ã‚Â³n de 15 pantallas en archivos por feature (login, home, campania, tarea, cosecha, insumo, observacion, reportes, config).
- ExtracciÃƒÆ’Ã‚Â³n de colores a `theme/AgriCoreColors.kt`.
- Componentes compartidos movidos a `components/` (6 archivos).
- NavegaciÃƒÆ’Ã‚Â³n migrada a `navigation/NavRoutes.kt` con sealed class `NavRoute`.
- SimplificaciÃƒÆ’Ã‚Â³n de la ruta `FormularioCampania` (sin parÃƒÆ’Ã‚Â¡metro opcional).

**[2026-05-14] - ImplementaciÃƒÆ’Ã‚Â³n de HomeViewModel y Dashboard reactivo (F4/Issue2)**
- CreaciÃƒÆ’Ã‚Â³n de `HomeViewModel` con inyecciÃƒÆ’Ã‚Â³n de `ObtenerCampaniasUseCase`.
- RefactorizaciÃƒÆ’Ã‚Â³n de `DashboardOperacionesScreen` para consumir datos reales desde BD.
- Lista reactiva de campaÃƒÆ’Ã‚Â±as con navegaciÃƒÆ’Ã‚Â³n al detalle por ID.
- Estado vacÃƒÆ’Ã‚Â­o con indicaciÃƒÆ’Ã‚Â³n visual para crear una nueva campaÃƒÆ’Ã‚Â±a.

**[2026-05-14] - MigraciÃƒÆ’Ã‚Â³n a Navigation Compose y Scaffold global (F4/Issue1)**
- CreaciÃƒÆ’Ã‚Â³n de `NavRoute` (sealed class) reemplazando enum `Destino`.
- MigraciÃƒÆ’Ã‚Â³n de navegaciÃƒÆ’Ã‚Â³n manual (lista/pila) a `NavHost` + `NavController`.
- ConfiguraciÃƒÆ’Ã‚Â³n de BottomNavigationBar con preservaciÃƒÆ’Ã‚Â³n de estado por pestaÃƒÆ’Ã‚Â±a.
- EliminaciÃƒÆ’Ã‚Â³n de `BackHandler` manual (delegado al NavController).
- DefiniciciÃƒÆ’Ã‚Â³n de rutas con parÃƒÆ’Ã‚Â¡metros (`DetalleCampania`, `FormularioCampania`).

**[2026-05-12] - ImplementaciÃƒÆ’Ã‚Â³n de Casos de Uso (CampaÃƒÆ’Ã‚Â±as y Tareas) - F3/Issue4**
- CreaciÃƒÆ’Ã‚Â³n de `CrearCampaniaUseCase`, `EditarCampaniaUseCase`, `EliminarCampaniaUseCase` y `ObtenerCampaniasUseCase`.
- Cada Use Case con `@Inject constructor` y validaciÃƒÆ’Ã‚Â³n de nombre no vacÃƒÆ’Ã‚Â­o.
- CreaciÃƒÆ’Ã‚Â³n de `CrearTareaUseCase`, `EditarTareaUseCase`, `EliminarTareaUseCase` y `ConfirmarTareaUseCase`.

**[2026-05-14] - ImplementaciÃƒÆ’Ã‚Â³n de Resource<T> y manejo de errores en Use Cases**
- CreaciÃƒÆ’Ã‚Â³n de `Resource<T>` en `domain/model/` con extensiones `onSuccess`, `onError`, `isSuccess`, `isError`.
- RefactorizaciÃƒÆ’Ã‚Â³n de 7 Use Cases para retornar `Flow<Resource<Unit>>` con emisiÃƒÆ’Ã‚Â³n de Loading, Success y Error.
- Manejo de excepciones con try/catch y ejecuciÃƒÆ’Ã‚Â³n en `Dispatchers.IO` mediante `flowOn`.

**[2026-05-14] - CorrecciÃƒÆ’Ã‚Â³n de mapeo Campania, unificaciÃƒÆ’Ã‚Â³n de nomenclatura e implementaciÃƒÆ’Ã‚Â³n de Use Cases faltantes**
- Corregido mapeo bidireccional `Campania` ÃƒÂ¢Ã¢â‚¬Â Ã¢â‚¬ï¿½ `CampaniaEntity`: agregado `cultivo` al modelo de dominio y `estaActiva` a la entidad; eliminados hardcodeos en `Mappers.kt`.
- Renombrado `campaniaId` ÃƒÂ¢Ã¢â‚¬Â Ã¢â‚¬â„¢ `idCampania` en `TareaRepository`, `CosechaRepository` y sus implementaciones.
- Creados modelos de dominio `Observacion` y `CampaniaInsumo` para mantener la pureza de la capa domain.
- Creados `CampaniaInsumoRepository` y `ObservacionRepository` con sus implementaciones y bindings de Hilt.
- Agregados mappers para `ObservacionEntity` ÃƒÂ¢Ã¢â‚¬Â Ã¢â‚¬ï¿½ `Observacion` y `CampaniaInsumoEntity` ÃƒÂ¢Ã¢â‚¬Â Ã¢â‚¬ï¿½ `CampaniaInsumo`.
- Implementados 6 casos de uso: `RegistrarCosechaUseCase`, `CrearInsumoCatalogoUseCase`, `EditarInsumoCatalogoUseCase`, `ObtenerCatalogoInsumosUseCase`, `AsignarInsumoACampaniaUseCase`, `GuardarObservacionUseCase`.

**[2026-05-12] - Card campaÃƒÆ’Ã‚Â±a activa en Tareas/Cosechas/Observaciones + botÃƒÆ’Ã‚Â³n exportar en Reportes + diagrama de flujo**
- TareasScreen, CosechasScreen y ObservacionesScreen: aÃƒÆ’Ã‚Â±adida `CampanaSeleccionadaCard` de la campaÃƒÆ’Ã‚Â±a activa.
- ReportesRendimientoScreen: aÃƒÆ’Ã‚Â±adido botÃƒÆ’Ã‚Â³n de exportar (Excel/PDF) en TopAppBar con `DropdownMenu`.
- Creado `docs/FLOW.md` con diagrama Mermaid de navegaciÃƒÆ’Ã‚Â³n y tabla de cobertura de Casos de Uso.

**[2026-05-12] - Refactor de navegaciÃƒÆ’Ã‚Â³n global, mÃƒÆ’Ã‚Â³dulo de insumos y reportes**
- BottomNav: aÃƒÆ’Ã‚Â±adido acceso directo a `Destino.Insumos`; renombrado "Agenda" ÃƒÂ¢Ã¢â‚¬Â Ã¢â‚¬â„¢ "Tareas" y "Parcelas" ÃƒÂ¢Ã¢â‚¬Â Ã¢â‚¬â„¢ "CampaÃƒÆ’Ã‚Â±as".
- Home: `CampaniaSeleccionadaCard` ahora navega a `DetalleCampania`; botÃƒÆ’Ã‚Â³n + navega a `FormularioCampania`.
- InsumosScreen: reemplazado formulario inline por `ModalBottomSheet` con buscador, selector cantidad/precio y botÃƒÆ’Ã‚Â³n "Agregar al catÃƒÆ’Ã‚Â¡logo".
- FormularioInsumoScreen: simplificado a solo campos Nombre, CategorÃƒÆ’Ã‚Â­a y Unidad.
- ReportesRendimientoScreen: aÃƒÆ’Ã‚Â±adidas tarjetas de mÃƒÆ’Ã‚Â©tricas comparativas (Rendimiento, Ganancias, Costos, Insumos); selector dropdown para comparar dos campaÃƒÆ’Ã‚Â±as; grÃƒÆ’Ã‚Â¡ficos Canvas de evoluciÃƒÆ’Ã‚Â³n mensual (Costos/Insumos) con leyenda bicolor.

**[2026-05-12] - InicializaciÃƒÆ’Ã‚Â³n de documentaciÃƒÆ’Ã‚Â³n de seguimiento**
- CreaciÃƒÆ’Ã‚Â³n de `CHANGELOG.md` en la raÃƒÆ’Ã‚Â­z para el seguimiento de tareas.
- Ajuste de `donelioOP.md` para referenciar `.context/RoadmapOP.md`.

**[2026-05-11] - Avance en Fase 3 (Capa de Dominio)**
- DefiniciÃƒÆ’Ã‚Â³n de modelos de dominio (`data class` puros).
- ImplementaciÃƒÆ’Ã‚Â³n de `Mappers.kt`.
- CreaciÃƒÆ’Ã‚Â³n de interfaces de repositorios (`CampaniaRepository`, `TareaRepository`, etc.).
- ImplementaciÃƒÆ’Ã‚Â³n base de los repositorios en la capa `data`.

**[2026-05-10] - FinalizaciÃƒÆ’Ã‚Â³n de Fase 1 y Fase 2**
- ConfiguraciÃƒÆ’Ã‚Â³n inicial del proyecto, dependencias y estructura de Clean Architecture.
- ImplementaciÃƒÆ’Ã‚Â³n completa de la capa de datos: Entidades Room, TypeConverters y DAOs.
- ConfiguraciÃƒÆ’Ã‚Â³n de Dagger-Hilt para inyecciÃƒÆ’Ã‚Â³n de dependencias.

**[2026-08-21] - Fix InserciÃƒÂ³n de Insumos al CatÃƒÂ¡logo [#334]**
- Se corrigiÃƒÂ³ un error donde FormularioInsumoViewModel leÃƒÂ­a un insumoId = -1 por defecto y causaba que se ejecutara el flujo de actualizaciÃƒÂ³n silenciosamente en lugar de crear uno nuevo.

**[2026-08-21] - Fix EdiciÃƒÂ³n de Cosechas [#335]**
- Se agregÃƒÂ³ el parÃƒÂ¡metro cosechaId a la ruta de navegaciÃƒÂ³n de FormularioCosecha y se vinculÃƒÂ³ el evento onEditarCosecha para permitir la ediciÃƒÂ³n correcta de las cosechas.

**[2026-08-21] - Fix ValidaciÃƒÂ³n de Formulario de Cosechas [#336]**
- Se aÃƒÂ±adiÃƒÂ³ una propiedad errorGeneral para evitar que todos los errores del formulario de cosecha se agruparan errÃƒÂ³neamente en el campo cantidad, mostrando en cambio un Snackbar universal.
**[2026-08-21] - Fix Reportes ExportaciÃƒÂ³n vacÃƒÂ­a y Comparador [#355] [#356]**
- Se agregÃƒÂ³ una guardia en ReportesViewModel para evitar exportar PDFs o CSVs vacÃƒÂ­os cuando no hay datos en la campaÃƒÂ±a seleccionada.
- Se implementÃƒÂ³ una tarjeta de advertencia en ReportesRendimientoScreen para prevenir que el usuario seleccione la misma campaÃƒÂ±a en ambos selectores del comparador, documentando el caso en el plan de pruebas.
**[2026-08-21] - Fix UI Detalles y Reportes [#339] [#340]**
- Se migrÃƒÂ³ el TabRow a ScrollableTabRow en DetalleCampaniaScreen para evitar que los nombres de las pestaÃƒÂ±as se corten o dividan en varias lÃƒÂ­neas.
- Se ocultÃƒÂ³ la leyenda por defecto de los grÃƒÂ¡ficos PieChart en ReportesRendimientoScreen y se creÃƒÂ³ una leyenda manual debajo utilizando FlowRow, solucionando el problema de solapamiento de etiquetas en el grÃƒÂ¡fico.


