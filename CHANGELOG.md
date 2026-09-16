**[2026-09-16] - feat(insumos): [#455] Multi-registro de Insumos (Acumulación Histórica)**
- **DB/Domain**: Se eliminó el índice único `(id_campania, id_insumo)` en `CampaniaInsumoEntity`. El conflicto `REPLACE` se cambió a `IGNORE`. Se agregó la propiedad `fechaAplicacion` autogenerada. Version de BD subida a 8.
- **UI (Insumos)**: `InsumosScreen` se reescribió para agrupar los insumos repetidos usando un acordeón expansible, sumando las cantidades en el resumen, y mostrando registros individuales con fecha y subtotal debajo.
- **UI (Reportes)**: `ReportesRendimientoScreen` también adoptó el formato de acordeón expansible en la leyenda del gráfico de torta, permitiendo hacer drill-down.
- **UI**: Se agregó soporte para la edición y eliminación de cada registro individual en ambas pantallas, apoyado en nuevas funciones `editarInsumo()` y `eliminarInsumo()` en el `ReportesViewModel`.
- **Tests**: Se actualizó `AsignarInsumoACampaniaUseCaseTest` con aserciones automáticas de fechas y pruebas de multi-registro independiente. Se agregó test de integración en `CampaniaInsumoDaoTest`.

**[2026-09-14] - Estabilizacion y correcciones de bugs UI/UX de Iteracion 5 (PRs 451, 452, 457)**
- Se aplico truncamiento de nombres en eje X del grafico de evolucion (Issue #434).
- Se corrigio el renderizado del punto de datos unico en graficos de rendimiento (Issue #438).
- Se restauraron y corrigieron problemas de encoding con los iconos/emojis del Formulario Insumos (Issue #440).

**[2026-09-12] - fix(ui): estandarización de números, contadores de campaña y cálculo de ingresos [out-of-scope]**

> âš ï¸ Correcciones adicionales derivadas de la segunda ronda de testeo manual sobre la rama de pruebas `test/verificacion-issues-441-437-439`.

- `ObtenerResumenRendimientoUseCase`: Se cambió el filtro de ingresos. Ya no busca la palabra exacta "venta" en el campo `tipo` (ya que es de escritura libre), sino que asume como ingreso toda `CosechaNoAlmacenada` cuyo `precio > 0.0`.
- `TareaViewModel` y `DetalleCampaniaScreen`: El card del menú ahora utiliza `todasLasTareas` para contabilizar y mostrar correctamente la cantidad de tareas "completadas" (antes mostraba 0 porque el flujo principal las ocultaba).
- `DetalleCampaniaScreen` (Cosechas): El card ahora contabiliza **todas** las cosechas (almacenadas + ventas/reservas) en lugar de solo las almacenadas. Además, la unidad de medida se cambió de "Kg" a "Tn" para mantener consistencia.
- `FormatUtils` **[NUEVO]**: Se creó un utilitario centralizado con la configuración `Locale("es", "AR")` para asegurar que en toda la aplicación los miles se separen con punto (`.`) y los decimales con coma (`,`).
- `DashboardOperacionesScreen`, `CosechasScreen`, `InsumosScreen`: Refactorizados para usar `FormatUtils` en lugar de formatos de texto ad-hoc.

> âš ï¸ Estos cambios fueron detectados durante la verificación manual post-merge de las ramas de la iteración 5. No estaban contemplados en los issues originales, pero afectaban la correcta funcionalidad del sistema.

- `ObtenerResumenRendimientoUseCase`: Corregido el cálculo de `ingresosBrutos`. Antes multiplicaba `cantidad * venta.precio` (asumiendo precio unitario). Ahora suma `venta.precio` directamente, respetando la regla de negocio donde el usuario ingresa el **precio total** de la venta (out-of-scope de #437).
- `EditarCosechaConVentaUseCase`: **[NUEVO]** Caso de uso que actualiza en una sola operación la tabla `Cosecha` y `CosechaNoAlmacenada`. Soluciona que la edición de cosechas tipo Venta solo actualizaba la tabla base.
- `FormularioCosechaViewModel`:
  - Fallback al `UltimaSeleccionManager`: si el formulario se abre sin `campaniaId` explícito (ej. botón FAB global), ahora pre-carga la última campaña seleccionada (homologa comportamiento con Tareas e Insumos â€” out-of-scope de #441).
  - Sanitización de decimales: `onCantidadChange` y `onPrecioChange` normalizan coma `,` a punto `.` antes de parsear, evitando que valores como `1234,5` se guarden como `0`.
  - `cargarCosecha()` ahora también consulta `CosechaNoAlmacenadaRepository` para pre-cargar `tipo` y `precio` al editar cosechas no almacenadas (out-of-scope de #439).
- `FormularioCosechaScreen`: Label del campo precio cambiado de `"Precio (Opcional)"` a `"Precio Total de Venta ($)"` para claridad del usuario.
- `VincularInsumoScreen`: Agrega `SelectorCampania` en la parte superior para mostrar y permitir cambiar la campaña destino. Listado de insumos refactorizado a `LazyColumn` para scroll nativo (out-of-scope de #439).
- `docs/bugs_identificados.md`: Registrada DT [PENDIENTE-ID] para la edición de insumos vinculados (feature no implementada originalmente).
- `docs/plan_de_pruebas.md`: Agregados casos GWT FC-1 a FC-6 y DR-1, DR-2.

**[2026-09-11] - Formulario dedicado de vinculación de insumos (Pantalla Unificada) [#439]**
- `VincularInsumoScreen`: Se creó una pantalla nueva independiente para vincular insumos a campañas, unificando la lógica.
- `InsumosScreen`: Se eliminó el `ModalBottomSheet` interno que tenía duplicada la funcionalidad de vinculación. Ahora se redirige a `VincularInsumoScreen`.
- `DetalleCampaniaScreen`: En `CardModuloInsumos`, el botón "+" (Nuevo Insumo) ahora navega correctamente al formulario de vinculación `VincularInsumoScreen` de esa campaña (antes navegaba al formulario de crear insumo al catálogo, lo que era un flujo incorrecto).
- `InsumoVinculacionViewModel`: Actualizado el `init` para que priorice el `campaniaId` de navegación y aplique sincronización segura del manager (evitando posibles race conditions).
- `InsumoVinculacionViewModelTest`: Agregadas pruebas para verificar que `asignarInsumo` y los UseCases funcionan correctamente con el `campaniaId`.

**[2026-09-11] - Fix balance en Dashboard y overflow de tarjetas [#437]**
- `DashboardOperacionesScreen`: Se implementó la utilidad `formatearMoneda` para manejar correctamente balances negativos con el signo por delante, utilizando `Locale.US` para el control manual del separador de miles/decimales y formato abreviado (K/M) según magnitud.
- `CardResumen`: Se aplicó una altura fija de `72.dp` y `TextOverflow.Ellipsis` para garantizar la uniformidad del grid.
- Se agregaron las pruebas unitarias correspondientes en `FormatearMonedaTest` con 9 casos GWT.

**[2026-09-11] - Fix race condition en contadores de Tareas y Cosechas en DetalleCampaniaScreen [#441]**
- `TareaViewModel`: el `init{}` ahora prioriza el `campaniaId` del `SavedStateHandle`. Si hay ID explícito, notifica al `UltimaSeleccionManager` pero no suscribe su flow, eliminando la race condition donde un ID obsoleto sobreescribía el correcto.
- `CosechaViewModel`: mismo patrón de prioridad aplicado. Se agrega `sincronizarCampania(id)` para uso desde `DetalleCampaniaScreen` sin contaminar el manager global.
- `DetalleCampaniaScreen`: `CardModuloCosechas` migra de `seleccionarCampania()` a `sincronizarCampania()`, consistente con `CardModuloTareas`.
- Se crean tests unitarios GWT en `CosechaViewModelTest` y se amplía `TareaViewModelTest` con casos de la race condition.

**[2026-09-04] - Persistir seleccion de campania en BottomNav (Issue #416)**
- Se agregó UltimaSeleccionManager (Singleton inyectado por Hilt) para mantener en memoria el ID de la campaña seleccionada.
- Se refactorizaron InsumoVinculacionViewModel, ObservacionViewModel, CosechaViewModel y TareaViewModel para inyectar UltimaSeleccionManager.
- Se crearon y modificaron las pruebas unitarias para mockear UltimaSeleccionManager.

**[2026-09-03] - Fix metricas financieras Dashboard (Issue #402)**
- Se agregó getAllNoAlmacenadas al CosechaNoAlmacenadaRepository.
- Se modificó ObtenerResumenRendimientoUseCase para calcular ingresos con precio x cantidad de ventas.
- Se actualizó el modelo ResumenRendimiento incluyendo ingresosBrutos y balance.
- Se modificó DashboardOperacionesScreen para visualizar Capital Invertido, Ingresos Brutos y Balance con colores dinámicos.
- Se creó ObtenerResumenRendimientoUseCaseTest para validar las reglas matemáticas en Domain.

**[2026-09-03] - Fix teclado bloquea scroll en formularios (Issue #409)**
- Se agregó el modificador .verticalScroll(rememberScrollState()) y .imePadding() a los contenedores Column principales en Insumo, Campaña, Tarea, Observación, y Cosecha.
- Se reemplazó .weight(1f) por .height(32.dp) en espaciadores dentro de Columns con scroll para evitar crashes de UI.

**[2026-09-03] - Fix nombre de usuario tras registro (Issue #413)**
- Se abstrajo el guardado de sesión en un nuevo GuardarSesionUseCase.
- Se actualizó el LoginViewModel para invocar este caso de uso tras un registro y login de invitado exitosos.
- Se añadieron y ajustaron pruebas unitarias en LoginViewModelTest.

**[2026-09-08] - Fix DTs pre-merge: preservar confirmar en edicion, callback observaciones, tests (#403, #410, #415)**

**[2026-09-01] - [#398, #401, #402, #405, #406, #407, #408, #409, #412, #413, #414, #416] Iteración 4: Reportes, Auth, Dashboard y UX**
- **#416 (feat/ux):** UltimaSeleccionManager para persistir campaña seleccionada al navegar desde BottomNav.
- **#402 (fix/dashboard):** Reglas de negocio de métricas financieras (ingresos, balance) movidas a Domain. Tarjetas actualizadas con colores dinámicos.
- **#409 (fix/ux):** Scroll vertical y imePadding en formularios para que el teclado no tape campos.
- **#413 (fix/auth):** GuardarSesionUseCase para persistir nombre de usuario tras registro.
- **#401 (fix/dashboard):** Se conectó el botón 'Ver detalle' de rendimiento en el Dashboard con la ruta de Reportes.
- **#414 (fix/dashboard):** Se corrigió el cálculo del umbral de inicio del día para que las tareas de hoy no aparezcan en rojo.
- **#408 (fix/reportes):** Se corrigió el recorte vertical del PieChart y el desborde de leyendas, forzando aspectRatio(1f).
- **#407 (fix/pdf):** Solucionado el problema de renderizado de caracteres especiales (Ã‘, tildes) en la exportación a PDF.
- **#412 (feat/reportes):** Se agregaron etiquetas para el eje X en el gráfico Canvas de Evolución Histórica.
- **#405 (feat/reportes):** Implementados filtros de tiempo avanzados en Reportes con DateRangePicker y accesos rápidos.
- **#406 (feat/reportes):** Rediseño del comparador de campañas con dos tarjetas lado a lado y métricas de Cosecha (Tn), Rendimiento (Tn/Ha) y Costo por Tonelada ($/Tn).
- **#398 (refactor/reportes):** Se refactorizó ReportesViewModel para usar Use Cases en lugar de repositorios directos.
- **DT/confirmar (#410):** `NuevaTareaViewModel` â€” Agrega campo `confirmar` a `NuevaTareaFormState` y lo carga en `cargarTarea()`. `guardar()` en modo edicion usa `current.confirmar` en lugar del literal `false`, evitando que una tarea completada vuelva a pendiente al ser editada.
- **DT/observaciones (#415):** `DetalleCampaniaScreen` â€” `CardModuloObservaciones` recibe ahora un callback separado `onGoToNuevaObservacion` para el boton `+`, siendo semanticamente consistente con los otros modulos del grid 2xN.
- **test(insumos) (#403):** `FormularioInsumoViewModelTest` [NUEVO] â€” 5 casos GWT cubriendo los AC del Issue #403: estado inicial false, solo nombre/categoria no habilitan, nombre+categoria validos habilitan, borrar nombre deshabilita.
- **test(tareas) (#410):** `NuevaTareaViewModelTest` â€” Actualizado para inyectar `editarTareaUseCase` y `obtenerTareaPorIdUseCase`. Agrega VM-T-E1 (precarga datos incluyendo confirmar) y VM-T-E2 (confirmar se preserva al guardar).
- **docs(pruebas):** `plan_de_pruebas.md` â€” Casos GWT VM-I-1 a VM-I-5 (#403) y VM-T-E1/E2 (#410) documentados.
- **docs(roadmap):** `roadmap_iteracion_4.md` â€” Todos los issues de la Iteracion 4 marcados `[x]` reflejando el estado real de `main`.
- **docs(bugs):** `bugs_identificados.md` â€” Conflicto de merge resuelto. Nuevas DTs de las PRs #435 y #436 registradas.

**[2026-08-28] - Fix pre-testing: correcciones de UX y validación (#335, #336, #339)**
- **#335 (fix/cosecha):** Se corrigió el flujo de edición de cosechas. `FormularioCosechaScreen` ahora recibe el parámetro `cosechaId` desde la navegación y muestra el título dinámico \"Editar Cosecha\" cuando corresponde. `screens.kt` actualizado para pasar `cosechaId` al composable.
**[2026-09-01] - Iteración 4 / Bloque 3: ABM de Tareas, Edición de Foto, Fix Insumo y Rediseño Campaña (#403, #404, #410, #415)**

- **#403 (fix/insumos):** `FormularioInsumoViewModel` â€” `evaluarValidaciones()` ahora se llama dentro de `onNombreChange()` y `onCategoriaChange()`. El estado `isGuardarHabilitado` se actualiza en tiempo real al tipear, habilitando el botón "Guardar Insumo" en cuanto los campos son válidos.
- **#404 (fix/observaciones):** `ObservacionesScreen` â€” El `AlertDialog` inline (solo texto) fue reemplazado por el composable `DialogEditarObservacion` existente, que ya soporta reemplazar y eliminar la foto desde cámara/galería. El callback `onGuardar` conecta directamente con `listViewModel.editarObservacion`.
- **#410 (feat/tareas):** ABM completo de Tareas implementado:
  - `TareasScreen`: Iconos âœï¸ (Editar) y ðŸ—‘ï¸ (Eliminar) en cada `TarjetaTareaItem` para tareas no completadas. Diálogo de confirmación antes de eliminar.
  - `NavRoutes.kt`: Ruta `NuevaTarea` extendida con parámetro opcional `tareaId`.
  - `NuevaTareaViewModel`: Lee `tareaId` desde `SavedStateHandle`, pre-carga el formulario con los datos existentes y bifurca el guardado entre `CrearTareaUseCase` y `EditarTareaUseCase`.
  - `NuevaTareaScreen`: Título ("Nueva Tarea" / "Editar Tarea") y texto del botón ("Guardar Tarea" / "Guardar Cambios") dinámicos según el modo.
  - `ObtenerTareaPorIdUseCase` [NUEVO]: Caso de uso para recuperar una tarea por su ID.
- **#415 (ux/campanias):** `DetalleCampaniaScreen` rediseñada como Dashboard Grid 2xN:
  - Eliminado el `ScrollableTabRow` y la variable `selectedTab`.
  - Reemplazado por `LazyVerticalGrid(GridCells.Fixed(2))` con 4 tarjetas de módulo (Tareas, Insumos, Cosechas, Observaciones).
  - Cada tarjeta (`ModuloCardBase`) muestra: contador de elementos, métrica principal y botón `+` verde para acceso rápido al formulario de Alta precargado con `campaniaId`.
  - El toque en el cuerpo de la tarjeta navega al listado completo del módulo.
  - Nuevos callbacks `onGoToNuevaTarea`, `onGoToNuevoInsumo` y `onGoToNuevaCosecha` conectados en `screens.kt`.


- **#335 (fix/cosecha):** Se corrigió el flujo de edición de cosechas. `FormularioCosechaScreen` ahora recibe el parámetro `cosechaId` desde la navegación y muestra el título dinámico "Editar Cosecha" cuando corresponde. `screens.kt` actualizado para pasar `cosechaId` al composable.
- **#336 (fix/cosecha):** Se agregó `errorFecha` al estado `FormularioCosechaState`. El mapeo de errores en `guardar()` ahora distingue el campo correcto (`errorCantidad` vs `errorFecha` vs `errorGeneral`) según el mensaje del `ValidarDatosCosechaUseCase`. La UI muestra el error en el campo Fecha correspondiente. Se agregaron 4 nuevos casos de test unitario (Tests 6â€“9).
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


**[2026-08-25] - [#351] feat(cultivos): ABM de Cultivos (CatÃƒÆ’Ã‚Â¡logo estandarizado)**
- **Data/Domain:** Se creÃƒÆ’Ã‚Â³ la entidad `CultivoEntity` y `Cultivo` (modelo de dominio). Se implementÃƒÆ’Ã‚Â³ `CultivoDao` con soporte para soft-delete, y se expuso `CultivoRepository` y su implementaciÃƒÆ’Ã‚Â³n. Se actualizÃƒÆ’Ã‚Â³ la versiÃƒÆ’Ã‚Â³n de la base de datos a 7.
- **CampaÃƒÆ’Ã‚Â±as:** Se reemplazÃƒÆ’Ã‚Â³ el campo de texto libre `cultivo` en `CampaniaEntity` y `Campania` por `id_cultivo` / `cultivoId` (FK) y `cultivoNombre`, realizando un `INNER JOIN` en todas las consultas de lectura para obtener su descripciÃƒÆ’Ã‚Â³n del catÃƒÆ’Ã‚Â¡logo de forma reactiva.
- **UI:** Se implementÃƒÆ’Ã‚Â³ `CatalogoCultivosScreen` y su `CultivoCatalogoViewModel` para ABM con diÃƒÆ’Ã‚Â¡logos inline. El formulario de campaÃƒÆ’Ã‚Â±a ahora utiliza un `ExposedDropdownMenuBox` para seleccionar cultivos de forma estricta, con una opciÃƒÆ’Ã‚Â³n de inserciÃƒÆ’Ã‚Â³n rÃƒÆ’Ã‚Â¡pida para nuevos cultivos en el mismo formulario.
- **Testing:** Se actualizaron todos los tests unitarios e instrumentados afectados, y se aÃƒÆ’Ã‚Â±adieron pruebas unitarias para `CultivoCatalogoViewModel`.
- **Rama:** `Issue351`

**[2026-08-25] - [#349] feat(db): HectÃƒÆ’Ã‚Â¡reas por campaÃƒÆ’Ã‚Â±a y mÃƒÆ’Ã‚Â©tricas Tn/Ha**
- **Data/Domain:** Se agregÃƒÆ’Ã‚Â³ el campo `hectareas` (Double) a `CampaniaEntity` y `Campania`. Se incrementÃƒÆ’Ã‚Â³ la versiÃƒÆ’Ã‚Â³n de la base de datos Room a 6 implementando la migraciÃƒÆ’Ã‚Â³n correspondiente.
- **UI:** El `FormularioCampaniaScreen` incluye validaciÃƒÆ’Ã‚Â³n de este nuevo campo. Se actualizÃƒÆ’Ã‚Â³ la vista de Reportes para mostrar la mÃƒÆ’Ã‚Â©trica `Rendimiento: X Tn/Ha`.
- **Rama:** `fix/issue-349-refactor-db`

**[2026-08-25] - [#348] feat(reportes): Top 3 insumos de mayor gasto**
- **UI:** Se agregÃƒÆ’Ã‚Â³ una nueva tarjeta en la pantalla de Reportes mostrando los 3 insumos con mayor porcentaje de gasto en la campaÃƒÆ’Ã‚Â±a actual.
- **Rama:** `fix/issue-348-top-insumos`

**[2026-08-25] - [#347] feat(dashboard): Tasa de Cumplimiento de Tareas**
- **Domain:** Se creÃƒÆ’Ã‚Â³ `ObtenerCumplimientoTareasUseCase` y el modelo `CumplimientoTareas` para calcular la relaciÃƒÆ’Ã‚Â³n entre tareas confirmadas y tareas totales en el periodo de las campaÃƒÆ’Ã‚Â±as activas.
- **UI:** Se integrÃƒÆ’Ã‚Â³ al `HomeViewModel` y se visualiza la tasa de cumplimiento en el `DashboardOperacionesScreen`.
- **Rama:** `fix/issue-347-tasa-cumplimiento`

**[2026-08-25] - [#346] feat(dashboard): Resumen financiero rÃƒÆ’Ã‚Â¡pido**
- **Domain:** Se creÃƒÆ’Ã‚Â³ `ObtenerResumenRendimientoUseCase` y el modelo `ResumenRendimiento` para calcular capital invertido (insumos) y total cosechado del mes actual.
- **UI:** Se agregÃƒÆ’Ã‚Â³ una tarjeta en el `DashboardOperacionesScreen` para mostrar estos indicadores financieros.
- **Rama:** `fix/issue-346-resumen-dashboard`

**[2026-08-25] - [#345] feat(tareas): RediseÃƒÆ’Ã‚Â±o de pantalla de tareas y filtros**
- **Domain:** Se creÃƒÆ’Ã‚Â³ `ObtenerTareasFiltradasUseCase` para unificar la bÃƒÆ’Ã‚Âºsqueda de tareas por campaÃƒÆ’Ã‚Â±a y fecha.
- **UI:** Se implementÃƒÆ’Ã‚Â³ `SelectorRangoFechas` interactivo (DateRangePicker). La pantalla de Tareas ahora usa este componente para permitir el filtrado de tareas en un rango especÃƒÆ’Ã‚Â­fico o mostrar pendientes por defecto.
- **Rama:** `fix/issue-345-redisenio-tareas`

**[2026-08-25] - [#344] feat(reportes): Leyenda de insumos con valores absolutos**
- **UI:** Se reemplazÃƒÆ’Ã‚Â³ el `FlowRow` en `ReportesRendimientoScreen` por un `Column` ordenado, mostrando el porcentaje y el valor absoluto en pesos de cada insumo.
- **Rama:** `fix/issue-344-orden-insumos`

**[2026-08-25] - [#343] feat(reportes): ExportaciÃƒÆ’Ã‚Â³n de datos de cosechas**
- **Domain:** Se incluyÃƒÆ’Ã‚Â³ la lista de `cosechas` como parte del modelo enviado al `ReportExporter`.
- **Core:** Se actualizaron las funciones `exportToCsv` y `exportToPdf` para anexar el listado de las cosechas de la campaÃƒÆ’Ã‚Â±a seleccionada en ambos formatos.
- **Rama:** `fix/issue-343-exportar-cosechas`

**[2026-08-25] - [#341] feat(auth): Persistencia de SesiÃƒÆ’Ã‚Â³n**
- **Core:** `SessionManager` ahora guarda `isLoggedIn`. Se aÃƒÆ’Ã‚Â±adiÃƒÆ’Ã‚Â³ `MainViewModel` para controlar el estado inicial de `MainActivity` mientras se carga el `DataStore`.
- **UI:** El flujo de navegaciÃƒÆ’Ã‚Â³n dirige al Dashboard (Home) si la sesiÃƒÆ’Ã‚Â³n estÃƒÆ’Ã‚Â¡ activa o al Login en caso contrario. El Login fue modificado para persistir tambiÃƒÆ’Ã‚Â©n a los usuarios Invitados. Se agregÃƒÆ’Ã‚Â³ funcionalidad de "Cerrar sesiÃƒÆ’Ã‚Â³n" en el Dashboard.
- **Rama:** `fix/issue-341-persistencia-sesion`

**[2026-08-25] - [#342] feat(campaÃƒÆ’Ã‚Â±as): Borrado y estilo visual de campaÃƒÆ’Ã‚Â±as inactivas**
- **Data/Domain:** Se integrÃƒÆ’Ã‚Â³ `EliminarCampaniaUseCase` en `GestionCampaniasViewModel`. Se confirmÃƒÆ’Ã‚Â³ que Room maneja la eliminaciÃƒÆ’Ã‚Â³n en cascada.
- **UI:** Las tarjetas de campaÃƒÆ’Ã‚Â±as inactivas en `GestionCampaniasScreen` tienen un color atenuado. Se agregÃƒÆ’Ã‚Â³ un botÃƒÆ’Ã‚Â³n de papelera y diÃƒÆ’Ã‚Â¡logo de confirmaciÃƒÆ’Ã‚Â³n para eliminaciÃƒÆ’Ã‚Â³n definitiva.
- **Rama:** `fix/issue-342-campanias-inactivas`

**[2026-08-25] - [#338] fix(ux): Teclado y Scroll en Formularios**
- **UI:** Se ajustÃƒÆ’Ã‚Â³ el manejo de insets en `MainActivity` y se aplicÃƒÆ’Ã‚Â³ `consumeWindowInsets` en `screens.kt` para evitar el bloqueo de scroll y el bloque blanco superior al abrir el teclado virtual.
- **Rama:** `fix/issue-338-teclado`

**[2026-08-25] - [#337] feat(observaciones): EdiciÃƒÆ’Ã‚Â³n de fotos en observaciones**
- **Dominio:** Se implementÃƒÆ’Ã‚Â³ `ValidarObservacionUseCase` y se ajustÃƒÆ’Ã‚Â³ `EditarObservacionUseCase` para manejar fotos.
- **UI:** El diÃƒÆ’Ã‚Â¡logo de ediciÃƒÆ’Ã‚Â³n de observaciones ahora permite modificar o eliminar fotos utilizando cÃƒÆ’Ã‚Â¡mara y galerÃƒÆ’Ã‚Â­a con permisos dinÃƒÆ’Ã‚Â¡micos.
- **Rama:** `fix/issue-337-editar-foto-observacion`

**[2026-08-25] - [#334] fix(insumos): CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de insumos en el catÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡logo**
- **ViewModel:** Se corrigiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³ la lectura del `insumoId` en `FormularioInsumoViewModel` para que un valor de `-1` no se trate como ediciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n, habilitando correctamente el flujo de creaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n.
- **Rama:** `fix/issue-334-creacion-insumos`

**[2026-08-12] - [#304] fix(ux): Pantalla no se desplaza al escribir (IME padding global)**
- **UI:** En `screens.kt`, se aplicÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³ el modificador `imePadding()` al contenedor principal dentro del `Scaffold` para que el espaciado reaccione al teclado virtual de forma automÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡tica.
- **UI:** Este ajuste resuelve globalmente el solapamiento del teclado en todos los formularios de la app.
- **Rama:** `fix/ime-padding-formularios` (stacked sobre `fix/bloquear-modo-oscuro`)

**[2026-08-12] - [#303] fix(ux): Bloquear Modo Oscuro (Forzar Tema Claro)**
- **UI:** En `Theme.kt`, se modificÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³ `DonElioTheme` para que el parÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡metro `darkTheme` siempre sea `false` por defecto, ignorando el setting del sistema.
- **UI:** Se forzÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³ `isAppearanceLightStatusBars = true` para asegurar que los iconos de la barra de estado siempre sean oscuros.
- **Rama:** `fix/bloquear-modo-oscuro`

**[2026-08-11] - [#294] feat(observaciones): EdiciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n y eliminaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de observaciones**
- **Dominio:** Se crearon `EditarObservacionUseCase` y `EliminarObservacionUseCase`.
- **ViewModels:** Se inyectaron los nuevos casos de uso en `ObservacionViewModel` para gestionar las acciones y los errores, exponiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©ndolos como estado.
- **UI:** Se agregaron ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­conos de editar y eliminar a cada `ObservacionCard` en `ObservacionesScreen`.
- **UI:** Se implementaron diÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡logos modales (AlertDialog) para confirmar la eliminaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n y para editar el texto de la observaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n in-place.
- **Rama:** `feat/issue-294-edicion-observaciones`

**[2026-08-11] - [#291] fix(tareas): Selector de hora usa TimeInput en vez de texto libre**
- **ViewModels:** `NuevaTareaViewModel` ahora valida que la hora no estÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â© vacÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a y que cumpla el formato regex (HH:mm), exponiendo `errorHora`.
- **UI:** En `NuevaTareaScreen` se reemplazÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³ el `OutlinedTextField` genÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©rico por un `TimeInput` nativo de Material 3 contenido dentro de un `AlertDialog`, previniendo el ingreso de texto arbitrario.
- **Rama:** `fix/issue-291-timepicker-hora`

**[2026-08-11] - [#285] fix(dashboard): Tareas interactivas y filtradas por vencimiento**
- **DAO/Dominio:** Actualizada la consulta `getTareasPendientesGlobales` para recibir `fechaLimite` y omitir tareas vencidas hace mÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡s de 7 dÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­as.
- **ViewModels:** `HomeViewModel` ahora calcula dinÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡micamente la `fechaLimite` y la pasa al `ObtenerTareasPendientesUseCase`.
- **UI:** Las tarjetas de "Tareas PrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³ximas" ahora son clickeables (navegan al detalle de la campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a asociada).
- **UI:** Tratamiento visual condicional: tareas recientes vencidas se muestran con color rojo tenue.
- **UI:** Se agregÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³ el botÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n "Ver todas" que redirige a la lista completa de tareas de la app.
- **Rama:** `fix/issue-285-dashboard-tareas`

**[2026-08-11] - [#287] fix(login): Saludo muestra nombre de usuario en vez de Invitado**
- **ViewModels:** `LoginViewModel` inyecta ahora `SessionManager` y luego del inicio de sesiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n persistirÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ en DataStore el nombre real del usuario recibido del backend.
- **Rama:** `fix/issue-287-saludo-usuario`

**[2026-08-11] - [#290] fix(campanias): ValidaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n estricta de fechas pasadas en creaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n**
- **Dominio:** 
  - Creado `ValidarDatosCampaniaUseCase` para concentrar la lÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³gica de validaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n (nombre, cultivo y control estricto de no permitir fechas anteriores a hoy, ignorando la regla en modo ediciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n).
  - AÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±adida capa extra de defensa en `CrearCampaniaUseCase` para lanzar excepciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n si la fecha es menor a hoy (medianoche).
- **ViewModels:** `CampaniaFormViewModel` limpiado completamente. Toda su lÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³gica condicional fue delegada al nuevo caso de uso, dedicÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ndose exclusivamente a actualizar la UI.
- **UI:** En `FormularioCampaniaScreen`, se configurÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³ `selectableDates` en el `rememberDatePickerState` para deshabilitar visualmente fechas anteriores a hoy, mejorando sustancialmente la UX.
- **Rama:** `fix/campanias-validacion-fechas`

**[2026-08-11] - [#289] fix(insumos): ValidaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Formulario y DelegaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n a Dominio**
- **Dominio:** Creado `ValidarInsumoUseCase` para evaluar la obligatoriedad de `nombre` y `categorÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a`. Nota: El campo `unidad` no fue incluido en la validaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n porque no existe en la arquitectura actual del proyecto.
- **ViewModels:** 
  - `FormularioInsumoViewModel` modificado para consumir el caso de uso y exponer un estado ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Âºnico `isGuardarHabilitado`.
  - `InsumoCatalogoViewModel` modificado para inyectar el caso de uso y exponer una funciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de delegaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de validaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n.
- **UI:** 
  - `FormularioInsumoScreen` muestra mensajes de error en los campos basÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ndose enteramente en el estado unificado, eliminando lÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³gica de negocio visual.
  - `CatalogoInsumosScreen` refactorizado para el diÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡logo inline y agregado un `SnackbarHost` para observar errores del ViewModel.
- **Rama:** `fix/insumos-validacion-formulario`

**[2026-08-02] - [#302] feat(reportes): Implementar ComparaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n Real entre CampaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±as**
- **Dominio:** `ReportesViewModel` ahora expone `cosechasA` y `cosechasB` asociadas a las campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±as seleccionadas en el comparador.
- **UI:** En `ReportesRendimientoScreen`, la secciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de "MÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©tricas Comparativas" ahora muestra los verdaderos totales de Costo de Insumos y Rendimiento (Cosechas) para la CampaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a A y la CampaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a B.
- **UI:** Se reemplazÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³ el `GraficoEvolucionPlaceholder` por un `DoubleBarIndicator`, que consiste en barras de progreso compuestas (Jetpack Compose) para representar visual y proporcionalmente la diferencia de Costos y Rendimiento entre ambas campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±as seleccionadas.
- **Rama:** `feat/comparacion-campanias` (stacked sobre `feat/grafico-desglose-cosechas`)
- **Dominio y UI:** Agregado el estado `desgloseCosechasData` al `ReportesViewModel` que filtra y agrupa dinÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡micamente el listado de cosechas en base a su destino (Almacenada vs Vendida/Reservada).
- **UI:** AÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±adido un nuevo grÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡fico `PieChart` en `ReportesRendimientoScreen` para visualizar visualmente las proporciones del destino de las cosechas de la campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a activa.
- **Tests:** Creado caso de prueba en `ReportesViewModelTest` para asegurar la correcta agrupaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n matemÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡tica de las cosechas.
- **Rama:** `feat/grafico-desglose-cosechas` (stacked sobre `feat/reporte-insumos-mejorado`)
- **ExportaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n:** El exportador (`ReportExporter`) ahora recibe y pinta el nombre de la campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a en los archivos CSV y PDF generados. El nombre del archivo sugerido en el `FilePicker` ahora incluye el nombre de la campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a.
- **ValidaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n UI:** Se agregÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³ una guardia en `ReportesRendimientoScreen` que verifica si hay una campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a seleccionada antes de abrir el `FilePicker`, mostrando un `Toast` si es `null`.
- **Rama:** `feat/reporte-insumos-mejorado` (stacked sobre `feat/migracion-db-insumos`)
- **Base de Datos:** MigraciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n a versiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n 5 (`MIGRATION_4_5`) usando copias de tabla temporales para eliminar la columna `unidad` de Insumos y Cosechas (limitaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de SQLite).
- **Dominio y UI:** EliminaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n del campo `unidad` explÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­cito en todo el cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³digo; se asume Kg/L de manera implÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­cita para simplificar el modelo y la UI.
- **Tests actualizados** para no requerir o asertar por el campo `unidad`.
- **Rama:** `feat/migracion-db-insumos` (stacked sobre `feat/campanas-historial`)

**[2026-07-29] - [#299] fix(reportes): Eliminar datos mockeados en Dashboard y reestructurar pantalla Reportes**
- **Dashboard (`DashboardOperacionesScreen.kt`):** Eliminadas las tarjetas hardcodeadas "Clima 24ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â°C" y "Salud Lotes 90% ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…â€œptimo". El contenido restante sube automÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ticamente.
- **`ReportesViewModel.kt` reescrito:** Se reemplaza `ObtenerTodosLosInsumosVinculadosUseCase` por `ObtenerInsumosVinculadosUseCase(campaniaId)` contextual. Se inyectan `ObtenerCampaniasUseCase` y `ObtenerCosechasPorCampaniaUseCase`. Nuevos StateFlows: `campanias`, `campaniaIndividual`, `insumosIndividual`, `cosechasIndividual`, `campaniaA/B`, `insumosA/B`. `pieChartData` y `exportableData` ahora son contextuales a la campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a seleccionada.
- **`ReportesRendimientoScreen.kt` reestructurada en dos secciones:**
  - *SecciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n 1 ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã¯Â¿Â½ EstadÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­sticas individuales:* Dropdown con campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±as reales de BD, tarjetas de costo de insumos y total cosechado, PieChart contextual (por campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a seleccionada).
  - *SecciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n 2 ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã¯Â¿Â½ Comparador:* Dos dropdowns con campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±as reales, `CardMetricaComparativa` con costo real de insumos A vs B, placeholder para grÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡fico de evoluciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n (scope #302).
- **ExportaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n CSV/PDF:** Ahora exporta los insumos de la campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a seleccionada en SecciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n 1 (en lugar de todos los insumos globales).
- **Tests creados:** `ReportesViewModelTest.kt` con 5 casos Given-When-Then (JUnit 4 + MockK + Turbine).
- **`docs/plan_de_pruebas.md` actualizado** con subsecciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n `ReportesViewModel ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã¯Â¿Â½ StateFlows contextuales [#299]`.
- **Nota de scope:** La lÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³gica de `campaniaA/B` e `insumosA/B` es un paso preparatorio del Issue #302. Documentado en la PR con `Partial-scope: #302`.
- **Rama:** `fix/datos-mock-dashboard-reportes` (stacked sobre `fix/tab-tareas-no-actualiza`)

**[2026-07-22] - [#292] fix(campania): PestaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a Tareas no actualiza datos al cambiar de campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a**
- **Causa raÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­z doble resuelta:**
  - `TabTareas` usaba `hiltViewModel(key = "tab_tareas")` con key estÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡tica, haciendo que Hilt reutilizara la misma instancia del `TareaViewModel` sin importar la campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a activa.
  - El `campaniaId` recibido como parÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡metro en `TabTareas` nunca se propagaba al ViewModel (que iniciaba con `null` desde `SavedStateHandle`).
- **`TareaViewModel.kt` modificado:** Se agrega el mÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©todo pÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Âºblico `sincronizarCampania(id: Int)` que actualiza `_campaniaIdSeleccionada` solo si el valor difiere del actual (idempotente, evita emisiones innecesarias en el StateFlow).
- **`DetalleCampaniaScreen.kt` modificado:**
  - `TabTareas`: key cambiada a `"tab_tareas_$campaniaId"` + `LaunchedEffect(campaniaId)` que invoca `sincronizarCampania()` como segunda lÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­nea de defensa.
  - `TabInsumos`: key corregida de `"tab_insumos"` a `"tab_insumos_$campaniaId"` (mismo patrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de bug identificado).
- **Tests creados:** `TareaViewModelTest.kt` con 5 casos Given-When-Then (JUnit 4 + MockK + Turbine).
- **`docs/plan_de_pruebas.md` actualizado** con subsecciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n `TareaViewModel ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã¯Â¿Â½ sincronizarCampania() [#292]`.
- **Rama:** `fix/tab-tareas-no-actualiza` (stacked sobre `fix/permiso-camara-observaciones`)

**[2026-06-30] - [#283] fix: Crash al Abrir la CÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡mara ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã¯Â¿Â½ Permiso CAMERA no Solicitado**
- **Causa raÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­z resuelta:** La app lanzaba `cameraLauncher.launch(uri)` directamente sin verificar ni solicitar el permiso `CAMERA` en runtime, causando un `SecurityException` en Android 6.0+ (API 23).
- **Nuevo mÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³dulo creado:** `presentation/util/CameraUtils.kt` con tres responsabilidades separadas:
  - `EstadoPermisoCamara`: State holder observable con `mutableStateOf` para `permisoConcedido`, `mostrarRazon` y `denegadoPermanente`.
  - `recordarPermisoCamara()`: Composable que gestiona el ciclo completo del permiso usando `ActivityResultContracts.RequestPermission()` y `ActivityCompat.shouldShowRequestPermissionRationale()` para distinguir denegaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n temporal vs. permanente.
  - `DialogoRazonPermisoCamara()`: `AlertDialog` de rationale que se muestra en primera denegaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n.
  - `abrirAjustesPermiso()`: Helper que lanza `Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)` cuando el permiso es denegado permanentemente.
- **`ObservacionesScreen.kt` actualizado:** BotÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n "Tomar foto" ahora verifica `controlPermiso.permisoConcedido` antes de lanzar la cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡mara. Si no estÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ concedido, guarda la acciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n pendiente y llama a `controlPermiso.solicitar()`. `SnackbarHost` aÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±adido al `Box` para feedback visual.
- **Flujos cubiertos:** Permiso ya concedido (directo a cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡mara) ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â· Primera denegaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n (muestra rationale) ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â· DenegaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n permanente (Snackbar con botÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n "Abrir Ajustes").
- **Rama:** `fix/permiso-camara-observaciones`

**[2026-06-10] - Cosechas: Fix Crash FK [#284] y ValidaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n del Formulario [#293]**
- **Issue 7 (#284):** Eliminado el crash `FOREIGN KEY constraint failed` al guardar una cosecha con `campaniaId = -1`. `FormularioCosechaViewModel` ahora inicializa `campaniaId` como `null` cuando `SavedStateHandle` no recibe un id vÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lido (`takeIf { it != -1 }`), inyecta `ObtenerCampaniasUseCase` para exponer `campanias` y `onCampaniaChange()`, y `guardar()` valida `campaniaId == null` emitiendo `errorCampania = "Debe seleccionar una campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a"` antes de intentar la inserciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n.
- **Issue 7 (UI):** `FormularioCosechaScreen` ahora muestra el componente `SelectorCampania` (etiqueta "CampaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a vinculada") con texto de error debajo cuando falta seleccionar campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a. El botÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n "Guardar" queda deshabilitado mientras `campaniaId == null`.
- **Issue 12 (#293):** `guardar()` ya no hace retorno silencioso con campos vacÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­os: setea `errorCantidad = "La cantidad es obligatoria"`. Adaptado a migraciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n DB v5 (campo `unidad` eliminado del modelo).
- **Issue 12 (UI):** BotÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n "Guardar" deshabilitado si hay errores o campos obligatorios vacÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­os (`cantidad`, `campaniaId`).
- **Testing:** Creado `FormularioCosechaViewModelTest` con 5 casos (MockK + coroutines-test): sin campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a, cantidad vacÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a, almacenado vÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lido y venta vÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lida.
- **DocumentaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n:** Marcados como completos Issues #284 y #293 en `.context/roadmap_iteracion_2.md`; agregados escenarios Given-When-Then en `docs/plan_de_pruebas.md`.
- **Rama:** `fix-cosechas-estabilizacion`

**[2026-06-23] - DocumentaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Entrega y Casos de Uso**
- ActualizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `docs/FLOW.md` incorporando diagramas de flujo interactivos Mermaid para cada una de las 8 ramas principales del sistema.
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `docs/diferencias_casos_de_uso_2025_2026.md` contrastando la propuesta teÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³rica original (2025) con la implementaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n final en Clean Architecture (2026), aplicando el formato tabular de casos de uso requerido en la cursada.

**[2026-06-09] - PlanificaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n y DivisiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de IteraciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n 2**
- ActualizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `docs/bugs_identificados.md` refinando Issues 8, 15, 18, 19, 20 en relaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n al rediseÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±o lineal, validaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de insumos con Flow y unificaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n a Toneladas.
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `.context/iteracion_2.md` con el roadmap maestro priorizado de L1 a L5.
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `docs/roadmap_desarrolladores.md` organizando las tareas para ejecuciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n en paralelo por 3 desarrolladores, con un desglose granular de ramas Git y orden de ejecuciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n.

**[2026-06-09] - SesiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Pruebas Manuales APK Debug ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã¯Â¿Â½ DocumentaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de 23 Issues**
- Reescritura completa de `docs/bugs_identificados.md` con 23 issues organizados por severidad (L1-L5).
- **L1 (Crashes):** Crash por permisos de cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡mara no solicitados (Issue 6), crash por FK constraint al registrar cosecha sin campaniaId (Issue 7).
- **L2 (Bugs Funcionales):** Saludo siempre muestra "Invitado" (Issue 3 actualizado), catÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡logo de insumos sin validaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n completa (Issue 8), campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±as permiten fechas pasadas (Issue 9), campo hora de tareas sin restricciones (Issue 10), tabs tareas/insumos no se actualizan al cambiar de campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a (Issue 11), validaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n faltante en formulario cosechas (Issue 12).
- **L3 (Features Faltantes):** EdiciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n/eliminaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de observaciones (Issue 13) y cosechas (Issue 14), separaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±as activas/inactivas (Issue 15), navegaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n lateral entre campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±as (Issue 16), campo hectÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡reas en cosecha (Issue 17).
- **L4 (Reportes):** Selector de campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a en grÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡fico de insumos (Issue 18), grÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡fico desglose cosechas (Issue 19), comparaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n real entre campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±as (Issue 20).
- **L5 (UX):** Bloquear modo oscuro (Issue 21), teclado cubre campos al escribir (Issue 22), tarjetas mock del dashboard (Issue 23).

**[2026-06-09] - GeneraciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de APK de Debug para Pruebas**
- Se generÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³ el archivo APK en versiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de depuraciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n (debug) mediante Gradle para facilitar las pruebas manuales en dispositivos fÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­sicos.

**[2026-06-04] - Fase 12: SincronizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n, Tests y Refactor (Issue 12.2)**
- **Roadmap:** Sincronizados y marcados como completos los Issues silentes de permisos, exportaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n/importaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de base de datos, BottomNav y Use Cases.
- **Tests Instrumentados:** Diagnosticados y programados para soluciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n los errores de compilaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de DAOs (`CampaniaDaoTest` y `CampaniaInsumoDaoTest`) que fallaban por nomenclaturas antiguas.
- **Refactor:** AÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±adida la tarea para limpiar las importaciones comodÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­n (`*`) a lo largo del proyecto para apegarse a las mejores prÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡cticas de Kotlin.

**[2026-06-04] - Hotfix: CorrecciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de compilaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n y rebase de PR**
- **Fix:** Corregido error de compilaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n en `ReportesViewModel.kt` causado por una importaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n faltante de la funciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de extensiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n `map` de `StateFlow`.
- **Git:** Desecho un commit de merge local y rebasada la rama `feature/171` sobre `main` resolviendo los conflictos en `CHANGELOG.md` para permitir un "Rebase and merge" limpio en GitHub.

**[2026-06-02] - Fase 7: ImplementaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Testing y CI/CD (Issue 1 Completo)**
- **Testing Unitario (Dominio):** Refactor de aserciones para corrutinas (cambio de `assertThrows` por `try-catch`) para arreglar fallos silenciosos. Ampliada la cobertura aÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±adiendo pruebas a Casos de Uso faltantes (`RegistroUseCaseTest`, `EditarCampaniaUseCaseTest`, `EditarTareaUseCaseTest`, `ObtenerCampaniasUseCaseTest`), subiendo la cobertura del paquete de 26% a 36.2%.
- **Testing Unitario (PresentaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n):** Implementado `LoginViewModelTest` usando Turbine para testear la emisiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n asÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­ncrona de `StateFlow`.
- **Testing Instrumentado (Datos):** Creados tests en memoria para los DAOs (`UsuarioDaoTest`, `CampaniaDaoTest`, `CampaniaInsumoDaoTest`) simulando un entorno de base de datos Android real con SQLite.
- **Cobertura y CI/CD:** Corregida la tarea de GitHub Actions (`pr_tests.yml`) para invocar la variante correcta de Android (`koverHtmlReportDebug`), permitiendo la correcta lectura de reportes de cobertura en PRs.
- **DocumentaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n:** Actualizado `plan_de_pruebas.md` documentando el correcto uso de excepciones en corrutinas y el comando especÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­fico de Kover.

**[2026-06-02] - Fase 7: PlanificaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Estrategia de Testing (Issue 1)**
- **Testing:** DefiniciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n del stack tecnolÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³gico (MockK, Turbine, Kover, AndroidX Test, Compose Rule).
- **DocumentaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n Viva:** CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n del documento `docs/plan_de_pruebas.md` que incluye:
  - AnÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lisis detallado de discrepancias entre el diseÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±o original (2025) y la arquitectura final implementada.
  - Escenarios BDD (Behavior-Driven Development) `Given-When-Then` para todos los mÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³dulos de la aplicaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n (CampaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±as, Insumos, Tareas, Cosechas, Observaciones, Auth y Backup).
  - IntegraciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n exhaustiva de Edge Cases (Casos de Borde).
  - Estrategias de comandos de ejecuciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n local y metas de cobertura estricta (Kover 80% en domain, 70% en data).
- **Roadmap:** Actualizado `.context/RoadmapOP.md` con el progreso en el Issue 1 de la Fase 7.

**[2026-06-02] - Fase 6: ExportaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Reportes a Archivos (Issue 2)**
- **Dominio:** Creado modelo `InsumoResumen` para abstraer la informaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n exportable.
- **Utilidad:** Creada clase `ReportExporter` que utiliza SAF y el ContentResolver para escribir los archivos.
- **ExportaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n CSV:** Implementada conversiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de datos de gastos por insumo en formato CSV.
- **ExportaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n PDF:** Implementada generaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de documento PDF usando la API nativa de Android `PdfDocument`, dibujando tablas en `Canvas`.
- **UI & ViewModel:** Integrados los launchers `ActivityResultContracts.CreateDocument` en `ReportesRendimientoScreen` y conectados a `ReportesViewModel`.
**[2026-06-01] - Fase 9: RefactorizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Arquitectura DB y DocumentaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Bugs**
- **Base de Datos:** Eliminado el soporte de borrado lÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³gico (soft-delete) de la tabla intermedia `CampaniaInsumoEntity`, aplicando borrado fÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­sico estricto (`DELETE`) en `CampaniaInsumoDao` para mantener la integridad referencial limpia.
- **KSP Fix:** Solucionados conflictos de compilaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Room (KSP) causados por colisiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de anotaciones `@Delete` y `@Query`.
- **Limpieza de CÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³digo:** Removida la propiedad `activo` del dominio, mappers y datos semilla de insumos. Se incrementÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³ la base de datos a la versiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n 4 forzando `fallbackToDestructiveMigration()`.
- **Limpieza de Repositorio:** AÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±adidos archivos de configuraciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n locales de Android Studio (`.idea/misc.xml`, `.idea/deploymentTargetSelector.xml`) al `.gitignore` y eliminados del rastreo de git.
- **DocumentaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n:** Creado el archivo `docs/bugs_identificados.md` documentando 4 problemas conocidos listos para la prÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³xima iteraciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n.

**[2026-06-01] - Optimizaciones de Entorno y Datos de Prueba**
- Migradas rutas locales del JDK (`org.gradle.java.home`) y cachÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â© (`gradle.user.home`) desde `gradle.properties` hacia `local.properties` para prevenir sobreescrituras en repositorio compartido.
- Restaurado botÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n condicional de "Cargar datos de prueba" (`BuildConfig.DEBUG`) en `ConfiguracionDBScreen` manteniendo compatibilidad con el nuevo soft-delete (`activo`) de Insumos en el `DataSeederImpl`.
**[2026-05-31] - ImplementaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Backup y CorrecciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de RegresiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n**
- Implementadas funcionalidades de exportaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n e importaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de base de datos (CU12, CU13) en `ConfiguracionDBScreen`.
- Creados Casos de Uso `CrearBackupUseCase` y `RestaurarBackupUseCase`.
- **Hotfix:** Revertida sobreescritura accidental del archivo `screens.kt` que habÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a eliminado la navegaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n moderna con `NavHost`.
- Restaurados `CosechaDao.kt`, `gradle.properties` y `.idea/misc.xml` para eliminar cambios locales subidos por error en la PR.


**[2026-05-25] - ActualizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Roadmap y BotÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n Invitado**
- ActualizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `.context/RoadmapOP.md` con issues finalizados de fase 8, 10 y 11.
- AÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±adido botÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n "Invitado" para debug en la pantalla de login (F8/Issue 1.8).

**[2026-05-25] - FinalizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de requerimientos fase 2**
- Implementado swipe semanal para gestiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n visual de Tareas.
- Implementado catÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡logo de Insumos con ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­conos e integraciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n a base de datos.
- Integrado YCharts para grÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ficos de pie en Dashboard de Reportes.
- AÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±adido soporte de Soft-Delete (activo) en vinculaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Insumos.
- Forzada versiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Room DB a 2 con migraciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n destructiva (entorno dev).
- AÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±adida DataSeed con iconos e items eliminados para pruebas de UI.
- Solucionados errores WorkerDaemon configurando gradle.user.home en entorno local.
- Actualizados Roadmap y documentaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Arquitectura.

**[2026-05-20] - IntegraciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de 20 issues de auditorÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a en RoadmapOP.md**
- Fusionados los 20 issues detectados en auditorÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a de cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³digo dentro del `RoadmapOP.md` como Fases 8-12, organizados por criticidad.
- Agregadas notas de referencia cruzada y de dependencia entre issues.
- Eliminado `.context/IssuesPendientes.md` (contenido migrado a RoadmapOP.md).

**[2026-05-19] - Implementar autenticaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n, refactor Clean Arch y conectar Use Cases muertos**
- **Issue 1 (Login completo):** CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `UsuarioDao`, modelo de dominio `Usuario`, mappers, `LoginUseCase` (SHA-256), `RegistroUseCase` y `LoginViewModel`. ConexiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `LoginScreen` y `RegistroScreen`.
- **Issue 12 (Refactor Clean Arch):** CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de 6 UseCases contenedores para queries reactivas. RefactorizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de 6 ViewModels para inyectar UseCases en lugar de repositorios (`CampaniaFormViewModel`, `CampaniaDetailViewModel`, `TareaViewModel`, `CosechaViewModel`, `InsumoVinculacionViewModel` y `ObservacionViewModel`).
- **Issue 13 (Use Cases muertos):** ConexiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `EditarTareaUseCase`, `EliminarTareaUseCase`, `EditarInsumoCatalogoUseCase` y creaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `EliminarInsumoCatalogoUseCase`. DiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡logo de ediciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n inline en `CatalogoInsumosScreen`.

**[2026-05-18] - Refactor de GestiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de CampaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±as (F4/Issue9)**
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `GestionCampaniasViewModel` con carga reactiva de campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±as desde `ObtenerCampaniasUseCase`.
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `GestionCampaniasScreen` reemplazando `GestionParcelasScreen` (mock) con lista real desde BD.
- CorrecciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de navegaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n: `onGoToDetail` ahora recibe `campaniaId` real del item clickeado.
- Estado vacÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­o con icono e indicaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n visual para crear campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a.

**[2026-05-18] - ImplementaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de MÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³dulo de Observaciones (F4/Issue8)**
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `ObservacionViewModel` con carga reactiva de observaciones por campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a desde BD.
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `FormularioObservacionViewModel` con formulario reactivo, validaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n y conexiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n a `GuardarObservacionUseCase`.
- RediseÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±o de `ObservacionesScreen` con formulario para guardar + listado reactivo de observaciones registradas.
- ActualizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `TabObservaciones` en `DetalleCampaniaScreen` con ViewModel por campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a y ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Âºltimas 3 observaciones.

**[2026-05-18] - ImplementaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n completa CosechaNoAlmacenada (Venta/Reserva)**
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `CosechaNoAlmacenadaDao`, modelo de dominio `CosechaNoAlmacenada`, repositorio e implementaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n.
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `RegistrarCosechaConVentaUseCase` que inserta cosecha base + detalle de venta/reserva.
- ExposiciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n del DAO en `DonElioDatabase` y DI en `DatabaseModule`/`RepositoryModule`.
- Mappers `toDomain()`/`toEntity()` para `CosechaNoAlmacenadaEntity`.
- `CosechaRepository.insertCosecha()` ahora retorna `Long` (ID generado).
- `CosechaViewModel` ampliado: `almacenadas` (filtrado) y `noAlmacenadasDetalle` (mapa idÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢detalle).
- `FormularioCosechaViewModel.guardar()` bifurca entre `RegistrarCosechaUseCase` y `RegistrarCosechaConVentaUseCase` segÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Âºn checkbox.
- `CosechasScreen` muestra tipo y precio en cards de venta/reserva.
- `TabCosechas` en `DetalleCampaniaScreen` con key ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Âºnica por campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a y resumen real de ventas.

**[2026-05-18] - ImplementaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de MÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³dulo de Cosechas (F4/Issue7)**
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `CosechaViewModel` con carga reactiva de cosechas por campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a desde BD.
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `FormularioCosechaViewModel` con formulario reactivo, validaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n y conexiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n a `RegistrarCosechaUseCase`.
- RefactorizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `CosechasScreen` con datos reales, separaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n visual almacenadas/no-almacenadas.
- RefactorizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `FormularioCosechaScreen` con ViewModel, DatePicker, validaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de cantidad y spinner de guardado.
- Agregado parÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡metro `campaniaId` opcional a `NavRoute.FormularioCosecha`.
- ActualizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `TabCosechas` en `DetalleCampaniaScreen` con datos reales desde BD.

**[2026-05-15] - Seed data para testing (debug source set)**
- ConfiguraciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `sourceSets { debug { java.srcDir("src/debug/java") } }` en `app/build.gradle.kts`.
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de interfaz `DataSeeder` en `src/main/` con `@BindsOptionalOf` para inyecciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n opcional en Hilt.
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `DataSeederImpl` en `src/debug/` con 4 campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±as, 8 insumos, 8 tareas, 3 cosechas, 5 vinculaciones y 4 observaciones con fechas fijas mediante `Calendar`.
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `SeedModule` en `src/debug/` proveyendo `DataSeederImpl` vÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a Hilt.
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `ConfiguracionDBViewModel` con estado `SeedState` (Idle/Cargando/Exito/Error) y mÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©todo `cargarDatosPrueba()`.
- BotÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n "Cargar datos de prueba" en `ConfiguracionDBScreen` visible solo en builds debug, con spinner y Snackbar de feedback.
- ActualizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `.context/RoadmapOP.md` con Issue 10 de Fase 4.

**[2026-05-15] - ImplementaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de MÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³dulo de Insumos (F4/Issue6)**
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `InsumoCatalogoViewModel` e `InsumoVinculacionViewModel` con carga reactiva desde BD.
- ConexiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `CatalogoInsumosScreen` al catÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡logo real con `ObtenerCatalogoInsumosUseCase`.
- ConexiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `FormularioInsumoScreen` a `CrearInsumoCatalogoUseCase` con validaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n y spinner.
- RefactorizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `InsumosScreen` (vinculaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n) con datos reales, cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lculo `cantidad ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬Â precio` formateado y atajo "Crear nuevo insumo" si no existe en catÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡logo.
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `FormularioInsumoViewModel` con estado reactivo.
- ActualizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `TabInsumos` en `DetalleCampaniaScreen` con conteo real y total estimado.

**[2026-05-15] - ImplementaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de MÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³dulo de Tareas (F4/Issue5)**
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `TareaViewModel` con carga reactiva de tareas por campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a desde BD.
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `NuevaTareaViewModel` con formulario reactivo, validaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n y conexiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n a `CrearTareaUseCase`.
- RefactorizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `TareasScreen` con datos reales, checkbox de confirmaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n con `ConfirmarTareaUseCase`, feedback visual (tachado + atenuado).
- RefactorizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `NuevaTareaScreen` con `DatePickerDialog` M3, validaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de nombre y spinner de guardado.
- ActualizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `TabTareas` en `DetalleCampaniaScreen` con lista real de pendientes y resumen.
- ActualizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `NavRoute.NuevaTarea` con `campaniaId` opcional.

**[2026-05-14] - Correcciones de bugs y navegaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n (F4/Issue4)**
- Bugfix: `CrearCampaniaUseCase` ahora acepta parÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡metro `cultivo` ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã¯Â¿Â½ el campo ya no se pierde al crear campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±as nuevas.
- Bugfix: `CampaniaFormViewModel` pasa `cultivo` al `crearCampaniaUseCase`.
- Bugfix: `GestionParcelasScreen`, `TareasScreen`, `InsumosScreen`, `CosechasScreen`, `ObservacionesScreen` ya no hardcodean `campaniaId=1` ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã¯Â¿Â½ todas las rutas aceptan `campaniaId` opcional y lo propagan correctamente.
- Limpieza: eliminado parÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡metro `onEditar` no usado en `HeaderCampania`.

**[2026-05-14] - Pantalla Detalle de CampaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a con Tabs y encabezado fijo (F4/Issue4)**
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `CampaniaDetailViewModel` con `SavedStateHandle` para carga de campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a por ID + eliminaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n.
- RediseÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±o de `DetalleCampaniaScreen` con TopAppBar dinÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡mico, encabezado fijo (nombre, cultivo, fechas, estado) y TabRow con 5 tabs: Info, Tareas, Insumos, Cosechas, Observaciones.
- Cada tab muestra resumen informativo y botÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de navegaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n a su pantalla completa, pasando `campaniaId`.
- ActualizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `screens.kt` con `navArgument("campaniaId")` extraÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­do y pasado al ViewModel.
- NavegaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n desde detalle a ediciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a (`onGoToEditar`) con el ID correcto.

**[2026-05-14] - ImplementaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Formulario ABM CampaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±as con validaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n y DatePicker (F4/Issue3)**
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `CampaniaFormViewModel` con `SavedStateHandle` para modo ediciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n/creaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n.
- RefactorizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `FormularioCampaniaScreen` con campos nombre/cultivo validados, DatePicker M3, botÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n guardar con spinner.
- ActualizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `NavRoute.FormularioCampania` con `campaniaId` opcional vÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a query param.
- IntegraciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `CrearCampaniaUseCase` (creaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n) y `EditarCampaniaUseCase` (ediciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n) con `LaunchedEffect` para navegaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n post-guardado.

**[2026-05-14] - Refactor: divisiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de screens.kt en archivos individuales**
- SeparaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de 15 pantallas en archivos por feature (login, home, campania, tarea, cosecha, insumo, observacion, reportes, config).
- ExtracciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de colores a `theme/AgriCoreColors.kt`.
- Componentes compartidos movidos a `components/` (6 archivos).
- NavegaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n migrada a `navigation/NavRoutes.kt` con sealed class `NavRoute`.
- SimplificaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de la ruta `FormularioCampania` (sin parÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡metro opcional).

**[2026-05-14] - ImplementaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de HomeViewModel y Dashboard reactivo (F4/Issue2)**
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `HomeViewModel` con inyecciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `ObtenerCampaniasUseCase`.
- RefactorizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `DashboardOperacionesScreen` para consumir datos reales desde BD.
- Lista reactiva de campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±as con navegaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n al detalle por ID.
- Estado vacÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­o con indicaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n visual para crear una nueva campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a.

**[2026-05-14] - MigraciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n a Navigation Compose y Scaffold global (F4/Issue1)**
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `NavRoute` (sealed class) reemplazando enum `Destino`.
- MigraciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de navegaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n manual (lista/pila) a `NavHost` + `NavController`.
- ConfiguraciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de BottomNavigationBar con preservaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de estado por pestaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a.
- EliminaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `BackHandler` manual (delegado al NavController).
- DefiniciciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de rutas con parÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡metros (`DetalleCampania`, `FormularioCampania`).

**[2026-05-12] - ImplementaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Casos de Uso (CampaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±as y Tareas) - F3/Issue4**
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `CrearCampaniaUseCase`, `EditarCampaniaUseCase`, `EliminarCampaniaUseCase` y `ObtenerCampaniasUseCase`.
- Cada Use Case con `@Inject constructor` y validaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de nombre no vacÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­o.
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `CrearTareaUseCase`, `EditarTareaUseCase`, `EliminarTareaUseCase` y `ConfirmarTareaUseCase`.

**[2026-05-14] - ImplementaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Resource<T> y manejo de errores en Use Cases**
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `Resource<T>` en `domain/model/` con extensiones `onSuccess`, `onError`, `isSuccess`, `isError`.
- RefactorizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de 7 Use Cases para retornar `Flow<Resource<Unit>>` con emisiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Loading, Success y Error.
- Manejo de excepciones con try/catch y ejecuciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n en `Dispatchers.IO` mediante `flowOn`.

**[2026-05-14] - CorrecciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de mapeo Campania, unificaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de nomenclatura e implementaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Use Cases faltantes**
- Corregido mapeo bidireccional `Campania` ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â ÃƒÂ¢Ã¢â€šÂ¬Ã¯Â¿Â½ `CampaniaEntity`: agregado `cultivo` al modelo de dominio y `estaActiva` a la entidad; eliminados hardcodeos en `Mappers.kt`.
- Renombrado `campaniaId` ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢ `idCampania` en `TareaRepository`, `CosechaRepository` y sus implementaciones.
- Creados modelos de dominio `Observacion` y `CampaniaInsumo` para mantener la pureza de la capa domain.
- Creados `CampaniaInsumoRepository` y `ObservacionRepository` con sus implementaciones y bindings de Hilt.
- Agregados mappers para `ObservacionEntity` ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â ÃƒÂ¢Ã¢â€šÂ¬Ã¯Â¿Â½ `Observacion` y `CampaniaInsumoEntity` ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â ÃƒÂ¢Ã¢â€šÂ¬Ã¯Â¿Â½ `CampaniaInsumo`.
- Implementados 6 casos de uso: `RegistrarCosechaUseCase`, `CrearInsumoCatalogoUseCase`, `EditarInsumoCatalogoUseCase`, `ObtenerCatalogoInsumosUseCase`, `AsignarInsumoACampaniaUseCase`, `GuardarObservacionUseCase`.

**[2026-05-12] - Card campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a activa en Tareas/Cosechas/Observaciones + botÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n exportar en Reportes + diagrama de flujo**
- TareasScreen, CosechasScreen y ObservacionesScreen: aÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±adida `CampanaSeleccionadaCard` de la campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±a activa.
- ReportesRendimientoScreen: aÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±adido botÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de exportar (Excel/PDF) en TopAppBar con `DropdownMenu`.
- Creado `docs/FLOW.md` con diagrama Mermaid de navegaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n y tabla de cobertura de Casos de Uso.

**[2026-05-12] - Refactor de navegaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n global, mÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³dulo de insumos y reportes**
- BottomNav: aÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±adido acceso directo a `Destino.Insumos`; renombrado "Agenda" ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢ "Tareas" y "Parcelas" ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢ "CampaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±as".
- Home: `CampaniaSeleccionadaCard` ahora navega a `DetalleCampania`; botÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n + navega a `FormularioCampania`.
- InsumosScreen: reemplazado formulario inline por `ModalBottomSheet` con buscador, selector cantidad/precio y botÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n "Agregar al catÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡logo".
- FormularioInsumoScreen: simplificado a solo campos Nombre, CategorÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a y Unidad.
- ReportesRendimientoScreen: aÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±adidas tarjetas de mÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©tricas comparativas (Rendimiento, Ganancias, Costos, Insumos); selector dropdown para comparar dos campaÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â±as; grÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ficos Canvas de evoluciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n mensual (Costos/Insumos) con leyenda bicolor.

**[2026-05-12] - InicializaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de documentaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de seguimiento**
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `CHANGELOG.md` en la raÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­z para el seguimiento de tareas.
- Ajuste de `donelioOP.md` para referenciar `.context/RoadmapOP.md`.

**[2026-05-11] - Avance en Fase 3 (Capa de Dominio)**
- DefiniciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de modelos de dominio (`data class` puros).
- ImplementaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de `Mappers.kt`.
- CreaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de interfaces de repositorios (`CampaniaRepository`, `TareaRepository`, etc.).
- ImplementaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n base de los repositorios en la capa `data`.

**[2026-05-10] - FinalizaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Fase 1 y Fase 2**
- ConfiguraciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n inicial del proyecto, dependencias y estructura de Clean Architecture.
- ImplementaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n completa de la capa de datos: Entidades Room, TypeConverters y DAOs.
- ConfiguraciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de Dagger-Hilt para inyecciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de dependencias.

**[2026-08-21] - Fix InserciÃƒÆ’Ã‚Â³n de Insumos al CatÃƒÆ’Ã‚Â¡logo [#334]**
- Se corrigiÃƒÆ’Ã‚Â³ un error donde FormularioInsumoViewModel leÃƒÆ’Ã‚Â­a un insumoId = -1 por defecto y causaba que se ejecutara el flujo de actualizaciÃƒÆ’Ã‚Â³n silenciosamente en lugar de crear uno nuevo.

**[2026-08-21] - Fix EdiciÃƒÆ’Ã‚Â³n de Cosechas [#335]**
- Se agregÃƒÆ’Ã‚Â³ el parÃƒÆ’Ã‚Â¡metro cosechaId a la ruta de navegaciÃƒÆ’Ã‚Â³n de FormularioCosecha y se vinculÃƒÆ’Ã‚Â³ el evento onEditarCosecha para permitir la ediciÃƒÆ’Ã‚Â³n correcta de las cosechas.

**[2026-08-21] - Fix ValidaciÃƒÆ’Ã‚Â³n de Formulario de Cosechas [#336]**
- Se aÃƒÆ’Ã‚Â±adiÃƒÆ’Ã‚Â³ una propiedad errorGeneral para evitar que todos los errores del formulario de cosecha se agruparan errÃƒÆ’Ã‚Â³neamente en el campo cantidad, mostrando en cambio un Snackbar universal.
**[2026-08-21] - Fix Reportes ExportaciÃƒÆ’Ã‚Â³n vacÃƒÆ’Ã‚Â­a y Comparador [#355] [#356]**
- Se agregÃƒÆ’Ã‚Â³ una guardia en ReportesViewModel para evitar exportar PDFs o CSVs vacÃƒÆ’Ã‚Â­os cuando no hay datos en la campaÃƒÆ’Ã‚Â±a seleccionada.
- Se implementÃƒÆ’Ã‚Â³ una tarjeta de advertencia en ReportesRendimientoScreen para prevenir que el usuario seleccione la misma campaÃƒÆ’Ã‚Â±a en ambos selectores del comparador, documentando el caso en el plan de pruebas.
**[2026-08-21] - Fix UI Detalles y Reportes [#339] [#340]**
- Se migrÃƒÆ’Ã‚Â³ el TabRow a ScrollableTabRow en DetalleCampaniaScreen para evitar que los nombres de las pestaÃƒÆ’Ã‚Â±as se corten o dividan en varias lÃƒÆ’Ã‚Â­neas.
- Se ocultÃƒÆ’Ã‚Â³ la leyenda por defecto de los grÃƒÆ’Ã‚Â¡ficos PieChart en ReportesRendimientoScreen y se creÃƒÆ’Ã‚Â³ una leyenda manual debajo utilizando FlowRow, solucionando el problema de solapamiento de etiquetas en el grÃƒÆ’Ã‚Â¡fico.






**[2026-09-15] - Fix Error de compilación en pruebas**
- Se corrigió el acceso a las propiedades 'nombre' y 'estaActiva' en CampaniaDaoTest.kt que impedían compilar el proyecto y generar el APK debido a referencias desactualizadas tras refactorizar CampaniaConCultivoSchema.
