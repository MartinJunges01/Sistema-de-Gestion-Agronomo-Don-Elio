**[2026-09-22] - refactor(reportes): Eliminar gráfico de evolución histórica (Issue #468)**
- Se eliminó el Canvas y el selector de cultivos en `ReportesRendimientoScreen`.
- Se removieron los UseCases `ObtenerCultivosUseCase` y `ObtenerEvolucionCultivoUseCase` del `ReportesViewModel`.
- Se eliminaron los escenarios correspondientes en `docs/plan_de_pruebas.md`.

**[2026-09-22] - feat(campanias): Ocultar campañas deshabilitadas en selectores (Issue #466)**
- Se reemplazó `ObtenerCampaniasUseCase` por `ObtenerCampaniasActivasUseCase` en todos los ViewModels operativos.
- Las campañas archivadas ya no son seleccionables para la creación de tareas, cosechas, observaciones ni insumos.
- Se documentó la deuda técnica en `docs/bugs_identificados.md` respecto a aislar los listados.
- Se añadieron tests de comportamiento en `docs/plan_de_pruebas.md`.

**[2026-09-22] - feat(reportes): Panel Financiero alineado al Dashboard (Closes #467)**
- Se creó `ObtenerResumenFinancieroPorFiltrosUseCase` para orquestar insumos, cosechas y ventas en el módulo de Reportes.
- Se actualizaron los títulos y tarjetas de `ReportesRendimientoScreen` para mostrar Capital Invertido, Ingresos Brutos y Balance.
- Se agregó el caso Given-When-Then respectivo en `docs/plan_de_pruebas.md`.
- Se documentó la deuda técnica respecto a la duplicación de lógica con el Dashboard en `docs/bugs_identificados.md`.

**[2026-09-18] - fix(tareas): always enable fab and use reactive ui events for navigation (Closes #462)**
- Se modificó TareasScreen.kt para que el botón de nueva tarea siempre esté activo.
- Se implementó un flujo reactivo (Channel/Flow) en TareaViewModel para disparar la navegación solo si hay una campaña seleccionada, o mostrar un error en la UI de lo contrario (Issue #462).
- Se inyectó el UltimaSeleccionManager en NuevaTareaViewModel para pre-seleccionar la campaña activa.

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

> ⚠️� Correcciones adicionales derivadas de la segunda ronda de testeo manual sobre la rama de pruebas `test/verificacion-issues-441-437-439`.

- `ObtenerResumenRendimientoUseCase`: Se cambió el filtro de ingresos. Ya no busca la palabra exacta "venta" en el campo `tipo` (ya que es de escritura libre), sino que asume como ingreso toda `CosechaNoAlmacenada` cuyo `precio > 0.0`.
- `TareaViewModel` y `DetalleCampaniaScreen`: El card del menú ahora utiliza `todasLasTareas` para contabilizar y mostrar correctamente la cantidad de tareas "completadas" (antes mostraba 0 porque el flujo principal las ocultaba).
- `DetalleCampaniaScreen` (Cosechas): El card ahora contabiliza **todas** las cosechas (almacenadas + ventas/reservas) en lugar de solo las almacenadas. Además, la unidad de medida se cambió de "Kg" a "Tn" para mantener consistencia.
- `FormatUtils` **[NUEVO]**: Se creó un utilitario centralizado con la configuración `Locale("es", "AR")` para asegurar que en toda la aplicación los miles se separen con punto (`.`) y los decimales con coma (`,`).
- `DashboardOperacionesScreen`, `CosechasScreen`, `InsumosScreen`: Refactorizados para usar `FormatUtils` en lugar de formatos de texto ad-hoc.

> ⚠️� Estos cambios fueron detectados durante la verificación manual post-merge de las ramas de la iteración 5. No estaban contemplados en los issues originales, pero afectaban la correcta funcionalidad del sistema.

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
  - `TareasScreen`: Iconos âœ�ï¸� (Editar) y ðŸ—‘ï¸� (Eliminar) en cada `TarjetaTareaItem` para tareas no completadas. Diálogo de confirmación antes de eliminar.
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


**[2026-08-25] - [#351] feat(cultivos): ABM de Cultivos (CatÃƒÆ’Ã‚¡logo estandarizado)**
- **Data/Domain:** Se creó la entidad `CultivoEntity` y `Cultivo` (modelo de dominio). Se implementó `CultivoDao` con soporte para soft-delete, y se expuso `CultivoRepository` y su implementación. Se actualizó la versión de la base de datos a 7.
- **Campañas:** Se reemplazó el campo de texto libre `cultivo` en `CampaniaEntity` y `Campania` por `id_cultivo` / `cultivoId` (FK) y `cultivoNombre`, realizando un `INNER JOIN` en todas las consultas de lectura para obtener su descripción del catÃƒÆ’Ã‚¡logo de forma reactiva.
- **UI:** Se implementó `CatalogoCultivosScreen` y su `CultivoCatalogoViewModel` para ABM con diÃƒÆ’Ã‚¡logos inline. El formulario de campaña ahora utiliza un `ExposedDropdownMenuBox` para seleccionar cultivos de forma estricta, con una opción de inserción rÃƒÆ’Ã‚¡pida para nuevos cultivos en el mismo formulario.
- **Testing:** Se actualizaron todos los tests unitarios e instrumentados afectados, y se añadieron pruebas unitarias para `CultivoCatalogoViewModel`.
- **Rama:** `Issue351`

**[2026-08-25] - [#349] feat(db): HectÃƒÆ’Ã‚¡reas por campaña y métricas Tn/Ha**
- **Data/Domain:** Se agregó el campo `hectareas` (Double) a `CampaniaEntity` y `Campania`. Se incrementó la versión de la base de datos Room a 6 implementando la migración correspondiente.
- **UI:** El `FormularioCampaniaScreen` incluye validación de este nuevo campo. Se actualizó la vista de Reportes para mostrar la métrica `Rendimiento: X Tn/Ha`.
- **Rama:** `fix/issue-349-refactor-db`

**[2026-08-25] - [#348] feat(reportes): Top 3 insumos de mayor gasto**
- **UI:** Se agregó una nueva tarjeta en la pantalla de Reportes mostrando los 3 insumos con mayor porcentaje de gasto en la campaña actual.
- **Rama:** `fix/issue-348-top-insumos`

**[2026-08-25] - [#347] feat(dashboard): Tasa de Cumplimiento de Tareas**
- **Domain:** Se creó `ObtenerCumplimientoTareasUseCase` y el modelo `CumplimientoTareas` para calcular la relación entre tareas confirmadas y tareas totales en el periodo de las campañas activas.
- **UI:** Se integró al `HomeViewModel` y se visualiza la tasa de cumplimiento en el `DashboardOperacionesScreen`.
- **Rama:** `fix/issue-347-tasa-cumplimiento`

**[2026-08-25] - [#346] feat(dashboard): Resumen financiero rÃƒÆ’Ã‚¡pido**
- **Domain:** Se creó `ObtenerResumenRendimientoUseCase` y el modelo `ResumenRendimiento` para calcular capital invertido (insumos) y total cosechado del mes actual.
- **UI:** Se agregó una tarjeta en el `DashboardOperacionesScreen` para mostrar estos indicadores financieros.
- **Rama:** `fix/issue-346-resumen-dashboard`

**[2026-08-25] - [#345] feat(tareas): Rediseño de pantalla de tareas y filtros**
- **Domain:** Se creó `ObtenerTareasFiltradasUseCase` para unificar la búsqueda de tareas por campaña y fecha.
- **UI:** Se implementó `SelectorRangoFechas` interactivo (DateRangePicker). La pantalla de Tareas ahora usa este componente para permitir el filtrado de tareas en un rango específico o mostrar pendientes por defecto.
- **Rama:** `fix/issue-345-redisenio-tareas`

**[2026-08-25] - [#344] feat(reportes): Leyenda de insumos con valores absolutos**
- **UI:** Se reemplazó el `FlowRow` en `ReportesRendimientoScreen` por un `Column` ordenado, mostrando el porcentaje y el valor absoluto en pesos de cada insumo.
- **Rama:** `fix/issue-344-orden-insumos`

**[2026-08-25] - [#343] feat(reportes): Exportación de datos de cosechas**
- **Domain:** Se incluyó la lista de `cosechas` como parte del modelo enviado al `ReportExporter`.
- **Core:** Se actualizaron las funciones `exportToCsv` y `exportToPdf` para anexar el listado de las cosechas de la campaña seleccionada en ambos formatos.
- **Rama:** `fix/issue-343-exportar-cosechas`

**[2026-08-25] - [#341] feat(auth): Persistencia de Sesión**
- **Core:** `SessionManager` ahora guarda `isLoggedIn`. Se añadió `MainViewModel` para controlar el estado inicial de `MainActivity` mientras se carga el `DataStore`.
- **UI:** El flujo de navegación dirige al Dashboard (Home) si la sesión estÃƒÆ’Ã‚¡ activa o al Login en caso contrario. El Login fue modificado para persistir también a los usuarios Invitados. Se agregó funcionalidad de "Cerrar sesión" en el Dashboard.
- **Rama:** `fix/issue-341-persistencia-sesion`

**[2026-08-25] - [#342] feat(campañas): Borrado y estilo visual de campañas inactivas**
- **Data/Domain:** Se integró `EliminarCampaniaUseCase` en `GestionCampaniasViewModel`. Se confirmó que Room maneja la eliminación en cascada.
- **UI:** Las tarjetas de campañas inactivas en `GestionCampaniasScreen` tienen un color atenuado. Se agregó un botón de papelera y diÃƒÆ’Ã‚¡logo de confirmación para eliminación definitiva.
- **Rama:** `fix/issue-342-campanias-inactivas`

**[2026-08-25] - [#338] fix(ux): Teclado y Scroll en Formularios**
- **UI:** Se ajustó el manejo de insets en `MainActivity` y se aplicó `consumeWindowInsets` en `screens.kt` para evitar el bloqueo de scroll y el bloque blanco superior al abrir el teclado virtual.
- **Rama:** `fix/issue-338-teclado`

**[2026-08-25] - [#337] feat(observaciones): Edición de fotos en observaciones**
- **Dominio:** Se implementó `ValidarObservacionUseCase` y se ajustó `EditarObservacionUseCase` para manejar fotos.
- **UI:** El diÃƒÆ’Ã‚¡logo de edición de observaciones ahora permite modificar o eliminar fotos utilizando cÃƒÆ’Ã‚¡mara y galería con permisos dinÃƒÆ’Ã‚¡micos.
- **Rama:** `fix/issue-337-editar-foto-observacion`

**[2026-08-25] - [#334] fix(insumos): Creación de insumos en el catÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡logo**
- **ViewModel:** Se corrigió la lectura del `insumoId` en `FormularioInsumoViewModel` para que un valor de `-1` no se trate como edición, habilitando correctamente el flujo de creación.
- **Rama:** `fix/issue-334-creacion-insumos`

**[2026-08-12] - [#304] fix(ux): Pantalla no se desplaza al escribir (IME padding global)**
- **UI:** En `screens.kt`, se aplicó el modificador `imePadding()` al contenedor principal dentro del `Scaffold` para que el espaciado reaccione al teclado virtual de forma automÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡tica.
- **UI:** Este ajuste resuelve globalmente el solapamiento del teclado en todos los formularios de la app.
- **Rama:** `fix/ime-padding-formularios` (stacked sobre `fix/bloquear-modo-oscuro`)

**[2026-08-12] - [#303] fix(ux): Bloquear Modo Oscuro (Forzar Tema Claro)**
- **UI:** En `Theme.kt`, se modificó `DonElioTheme` para que el parÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡metro `darkTheme` siempre sea `false` por defecto, ignorando el setting del sistema.
- **UI:** Se forzó `isAppearanceLightStatusBars = true` para asegurar que los iconos de la barra de estado siempre sean oscuros.
- **Rama:** `fix/bloquear-modo-oscuro`

**[2026-08-11] - [#294] feat(observaciones): Edición y eliminación de observaciones**
- **Dominio:** Se crearon `EditarObservacionUseCase` y `EliminarObservacionUseCase`.
- **ViewModels:** Se inyectaron los nuevos casos de uso en `ObservacionViewModel` para gestionar las acciones y los errores, exponiéndolos como estado.
- **UI:** Se agregaron íconos de editar y eliminar a cada `ObservacionCard` en `ObservacionesScreen`.
- **UI:** Se implementaron diÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡logos modales (AlertDialog) para confirmar la eliminación y para editar el texto de la observación in-place.
- **Rama:** `feat/issue-294-edicion-observaciones`

**[2026-08-11] - [#291] fix(tareas): Selector de hora usa TimeInput en vez de texto libre**
- **ViewModels:** `NuevaTareaViewModel` ahora valida que la hora no esté vacía y que cumpla el formato regex (HH:mm), exponiendo `errorHora`.
- **UI:** En `NuevaTareaScreen` se reemplazó el `OutlinedTextField` genérico por un `TimeInput` nativo de Material 3 contenido dentro de un `AlertDialog`, previniendo el ingreso de texto arbitrario.
- **Rama:** `fix/issue-291-timepicker-hora`

**[2026-08-11] - [#285] fix(dashboard): Tareas interactivas y filtradas por vencimiento**
- **DAO/Dominio:** Actualizada la consulta `getTareasPendientesGlobales` para recibir `fechaLimite` y omitir tareas vencidas hace mÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡s de 7 días.
- **ViewModels:** `HomeViewModel` ahora calcula dinÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡micamente la `fechaLimite` y la pasa al `ObtenerTareasPendientesUseCase`.
- **UI:** Las tarjetas de "Tareas Próximas" ahora son clickeables (navegan al detalle de la campaña asociada).
- **UI:** Tratamiento visual condicional: tareas recientes vencidas se muestran con color rojo tenue.
- **UI:** Se agregó el botón "Ver todas" que redirige a la lista completa de tareas de la app.
- **Rama:** `fix/issue-285-dashboard-tareas`

**[2026-08-11] - [#287] fix(login): Saludo muestra nombre de usuario en vez de Invitado**
- **ViewModels:** `LoginViewModel` inyecta ahora `SessionManager` y luego del inicio de sesión persistirÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡ en DataStore el nombre real del usuario recibido del backend.
- **Rama:** `fix/issue-287-saludo-usuario`

**[2026-08-11] - [#290] fix(campanias): Validación estricta de fechas pasadas en creación**
- **Dominio:** 
  - Creado `ValidarDatosCampaniaUseCase` para concentrar la lógica de validación (nombre, cultivo y control estricto de no permitir fechas anteriores a hoy, ignorando la regla en modo edición).
  - Añadida capa extra de defensa en `CrearCampaniaUseCase` para lanzar excepción si la fecha es menor a hoy (medianoche).
- **ViewModels:** `CampaniaFormViewModel` limpiado completamente. Toda su lógica condicional fue delegada al nuevo caso de uso, dedicÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡ndose exclusivamente a actualizar la UI.
- **UI:** En `FormularioCampaniaScreen`, se configuró `selectableDates` en el `rememberDatePickerState` para deshabilitar visualmente fechas anteriores a hoy, mejorando sustancialmente la UX.
- **Rama:** `fix/campanias-validacion-fechas`

**[2026-08-11] - [#289] fix(insumos): Validación de Formulario y Delegación a Dominio**
- **Dominio:** Creado `ValidarInsumoUseCase` para evaluar la obligatoriedad de `nombre` y `categoría`. Nota: El campo `unidad` no fue incluido en la validación porque no existe en la arquitectura actual del proyecto.
- **ViewModels:** 
  - `FormularioInsumoViewModel` modificado para consumir el caso de uso y exponer un estado único `isGuardarHabilitado`.
  - `InsumoCatalogoViewModel` modificado para inyectar el caso de uso y exponer una función de delegación de validación.
- **UI:** 
  - `FormularioInsumoScreen` muestra mensajes de error en los campos basÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡ndose enteramente en el estado unificado, eliminando lógica de negocio visual.
  - `CatalogoInsumosScreen` refactorizado para el diÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡logo inline y agregado un `SnackbarHost` para observar errores del ViewModel.
- **Rama:** `fix/insumos-validacion-formulario`

**[2026-08-02] - [#302] feat(reportes): Implementar Comparación Real entre Campañas**
- **Dominio:** `ReportesViewModel` ahora expone `cosechasA` y `cosechasB` asociadas a las campañas seleccionadas en el comparador.
- **UI:** En `ReportesRendimientoScreen`, la sección de "Métricas Comparativas" ahora muestra los verdaderos totales de Costo de Insumos y Rendimiento (Cosechas) para la Campaña A y la Campaña B.
- **UI:** Se reemplazó el `GraficoEvolucionPlaceholder` por un `DoubleBarIndicator`, que consiste en barras de progreso compuestas (Jetpack Compose) para representar visual y proporcionalmente la diferencia de Costos y Rendimiento entre ambas campañas seleccionadas.
- **Rama:** `feat/comparacion-campanias` (stacked sobre `feat/grafico-desglose-cosechas`)
- **Dominio y UI:** Agregado el estado `desgloseCosechasData` al `ReportesViewModel` que filtra y agrupa dinÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡micamente el listado de cosechas en base a su destino (Almacenada vs Vendida/Reservada).
- **UI:** Añadido un nuevo grÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡fico `PieChart` en `ReportesRendimientoScreen` para visualizar visualmente las proporciones del destino de las cosechas de la campaña activa.
- **Tests:** Creado caso de prueba en `ReportesViewModelTest` para asegurar la correcta agrupación matemÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡tica de las cosechas.
- **Rama:** `feat/grafico-desglose-cosechas` (stacked sobre `feat/reporte-insumos-mejorado`)
- **Exportación:** El exportador (`ReportExporter`) ahora recibe y pinta el nombre de la campaña en los archivos CSV y PDF generados. El nombre del archivo sugerido en el `FilePicker` ahora incluye el nombre de la campaña.
- **Validación UI:** Se agregó una guardia en `ReportesRendimientoScreen` que verifica si hay una campaña seleccionada antes de abrir el `FilePicker`, mostrando un `Toast` si es `null`.
- **Rama:** `feat/reporte-insumos-mejorado` (stacked sobre `feat/migracion-db-insumos`)
- **Base de Datos:** Migración a versión 5 (`MIGRATION_4_5`) usando copias de tabla temporales para eliminar la columna `unidad` de Insumos y Cosechas (limitación de SQLite).
- **Dominio y UI:** Eliminación del campo `unidad` explícito en todo el código; se asume Kg/L de manera implícita para simplificar el modelo y la UI.
- **Tests actualizados** para no requerir o asertar por el campo `unidad`.
- **Rama:** `feat/migracion-db-insumos` (stacked sobre `feat/campanas-historial`)

**[2026-07-29] - [#299] fix(reportes): Eliminar datos mockeados en Dashboard y reestructurar pantalla Reportes**
- **Dashboard (`DashboardOperacionesScreen.kt`):** Eliminadas las tarjetas hardcodeadas "Clima 24ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â°C" y "Salud Lotes 90% ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…â€œptimo". El contenido restante sube automÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡ticamente.
- **`ReportesViewModel.kt` reescrito:** Se reemplaza `ObtenerTodosLosInsumosVinculadosUseCase` por `ObtenerInsumosVinculadosUseCase(campaniaId)` contextual. Se inyectan `ObtenerCampaniasUseCase` y `ObtenerCosechasPorCampaniaUseCase`. Nuevos StateFlows: `campanias`, `campaniaIndividual`, `insumosIndividual`, `cosechasIndividual`, `campaniaA/B`, `insumosA/B`. `pieChartData` y `exportableData` ahora son contextuales a la campaña seleccionada.
- **`ReportesRendimientoScreen.kt` reestructurada en dos secciones:**
  - *Sección 1 ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã¯¿Â½ Estadísticas individuales:* Dropdown con campañas reales de BD, tarjetas de costo de insumos y total cosechado, PieChart contextual (por campaña seleccionada).
  - *Sección 2 ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã¯¿Â½ Comparador:* Dos dropdowns con campañas reales, `CardMetricaComparativa` con costo real de insumos A vs B, placeholder para grÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡fico de evolución (scope #302).
- **Exportación CSV/PDF:** Ahora exporta los insumos de la campaña seleccionada en Sección 1 (en lugar de todos los insumos globales).
- **Tests creados:** `ReportesViewModelTest.kt` con 5 casos Given-When-Then (JUnit 4 + MockK + Turbine).
- **`docs/plan_de_pruebas.md` actualizado** con subsección `ReportesViewModel ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã¯¿Â½ StateFlows contextuales [#299]`.
- **Nota de scope:** La lógica de `campaniaA/B` e `insumosA/B` es un paso preparatorio del Issue #302. Documentado en la PR con `Partial-scope: #302`.
- **Rama:** `fix/datos-mock-dashboard-reportes` (stacked sobre `fix/tab-tareas-no-actualiza`)

**[2026-07-22] - [#292] fix(campania): Pestaña Tareas no actualiza datos al cambiar de campaña**
- **Causa raíz doble resuelta:**
  - `TabTareas` usaba `hiltViewModel(key = "tab_tareas")` con key estÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡tica, haciendo que Hilt reutilizara la misma instancia del `TareaViewModel` sin importar la campaña activa.
  - El `campaniaId` recibido como parÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡metro en `TabTareas` nunca se propagaba al ViewModel (que iniciaba con `null` desde `SavedStateHandle`).
- **`TareaViewModel.kt` modificado:** Se agrega el método público `sincronizarCampania(id: Int)` que actualiza `_campaniaIdSeleccionada` solo si el valor difiere del actual (idempotente, evita emisiones innecesarias en el StateFlow).
- **`DetalleCampaniaScreen.kt` modificado:**
  - `TabTareas`: key cambiada a `"tab_tareas_$campaniaId"` + `LaunchedEffect(campaniaId)` que invoca `sincronizarCampania()` como segunda línea de defensa.
  - `TabInsumos`: key corregida de `"tab_insumos"` a `"tab_insumos_$campaniaId"` (mismo patrón de bug identificado).
- **Tests creados:** `TareaViewModelTest.kt` con 5 casos Given-When-Then (JUnit 4 + MockK + Turbine).
- **`docs/plan_de_pruebas.md` actualizado** con subsección `TareaViewModel ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã¯¿Â½ sincronizarCampania() [#292]`.
- **Rama:** `fix/tab-tareas-no-actualiza` (stacked sobre `fix/permiso-camara-observaciones`)

**[2026-06-30] - [#283] fix: Crash al Abrir la CÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡mara ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã¯¿Â½ Permiso CAMERA no Solicitado**
- **Causa raíz resuelta:** La app lanzaba `cameraLauncher.launch(uri)` directamente sin verificar ni solicitar el permiso `CAMERA` en runtime, causando un `SecurityException` en Android 6.0+ (API 23).
- **Nuevo módulo creado:** `presentation/util/CameraUtils.kt` con tres responsabilidades separadas:
  - `EstadoPermisoCamara`: State holder observable con `mutableStateOf` para `permisoConcedido`, `mostrarRazon` y `denegadoPermanente`.
  - `recordarPermisoCamara()`: Composable que gestiona el ciclo completo del permiso usando `ActivityResultContracts.RequestPermission()` y `ActivityCompat.shouldShowRequestPermissionRationale()` para distinguir denegación temporal vs. permanente.
  - `DialogoRazonPermisoCamara()`: `AlertDialog` de rationale que se muestra en primera denegación.
  - `abrirAjustesPermiso()`: Helper que lanza `Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)` cuando el permiso es denegado permanentemente.
- **`ObservacionesScreen.kt` actualizado:** Botón "Tomar foto" ahora verifica `controlPermiso.permisoConcedido` antes de lanzar la cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡mara. Si no estÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡ concedido, guarda la acción pendiente y llama a `controlPermiso.solicitar()`. `SnackbarHost` añadido al `Box` para feedback visual.
- **Flujos cubiertos:** Permiso ya concedido (directo a cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡mara) ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â· Primera denegación (muestra rationale) ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â· Denegación permanente (Snackbar con botón "Abrir Ajustes").
- **Rama:** `fix/permiso-camara-observaciones`

**[2026-06-10] - Cosechas: Fix Crash FK [#284] y Validación del Formulario [#293]**
- **Issue 7 (#284):** Eliminado el crash `FOREIGN KEY constraint failed` al guardar una cosecha con `campaniaId = -1`. `FormularioCosechaViewModel` ahora inicializa `campaniaId` como `null` cuando `SavedStateHandle` no recibe un id vÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡lido (`takeIf { it != -1 }`), inyecta `ObtenerCampaniasUseCase` para exponer `campanias` y `onCampaniaChange()`, y `guardar()` valida `campaniaId == null` emitiendo `errorCampania = "Debe seleccionar una campaña"` antes de intentar la inserción.
- **Issue 7 (UI):** `FormularioCosechaScreen` ahora muestra el componente `SelectorCampania` (etiqueta "Campaña vinculada") con texto de error debajo cuando falta seleccionar campaña. El botón "Guardar" queda deshabilitado mientras `campaniaId == null`.
- **Issue 12 (#293):** `guardar()` ya no hace retorno silencioso con campos vacíos: setea `errorCantidad = "La cantidad es obligatoria"`. Adaptado a migración DB v5 (campo `unidad` eliminado del modelo).
- **Issue 12 (UI):** Botón "Guardar" deshabilitado si hay errores o campos obligatorios vacíos (`cantidad`, `campaniaId`).
- **Testing:** Creado `FormularioCosechaViewModelTest` con 5 casos (MockK + coroutines-test): sin campaña, cantidad vacía, almacenado vÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡lido y venta vÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡lida.
- **Documentación:** Marcados como completos Issues #284 y #293 en `.context/roadmap_iteracion_2.md`; agregados escenarios Given-When-Then en `docs/plan_de_pruebas.md`.
- **Rama:** `fix-cosechas-estabilizacion`

**[2026-06-23] - Documentación de Entrega y Casos de Uso**
- Actualización de `docs/FLOW.md` incorporando diagramas de flujo interactivos Mermaid para cada una de las 8 ramas principales del sistema.
- Creación de `docs/diferencias_casos_de_uso_2025_2026.md` contrastando la propuesta teórica original (2025) con la implementación final en Clean Architecture (2026), aplicando el formato tabular de casos de uso requerido en la cursada.

**[2026-06-09] - Planificación y División de Iteración 2**
- Actualización de `docs/bugs_identificados.md` refinando Issues 8, 15, 18, 19, 20 en relación al rediseño lineal, validación de insumos con Flow y unificación a Toneladas.
- Creación de `.context/iteracion_2.md` con el roadmap maestro priorizado de L1 a L5.
- Creación de `docs/roadmap_desarrolladores.md` organizando las tareas para ejecución en paralelo por 3 desarrolladores, con un desglose granular de ramas Git y orden de ejecución.

**[2026-06-09] - Sesión de Pruebas Manuales APK Debug ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã¯¿Â½ Documentación de 23 Issues**
- Reescritura completa de `docs/bugs_identificados.md` con 23 issues organizados por severidad (L1-L5).
- **L1 (Crashes):** Crash por permisos de cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡mara no solicitados (Issue 6), crash por FK constraint al registrar cosecha sin campaniaId (Issue 7).
- **L2 (Bugs Funcionales):** Saludo siempre muestra "Invitado" (Issue 3 actualizado), catÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡logo de insumos sin validación completa (Issue 8), campañas permiten fechas pasadas (Issue 9), campo hora de tareas sin restricciones (Issue 10), tabs tareas/insumos no se actualizan al cambiar de campaña (Issue 11), validación faltante en formulario cosechas (Issue 12).
- **L3 (Features Faltantes):** Edición/eliminación de observaciones (Issue 13) y cosechas (Issue 14), separación campañas activas/inactivas (Issue 15), navegación lateral entre campañas (Issue 16), campo hectÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡reas en cosecha (Issue 17).
- **L4 (Reportes):** Selector de campaña en grÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡fico de insumos (Issue 18), grÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡fico desglose cosechas (Issue 19), comparación real entre campañas (Issue 20).
- **L5 (UX):** Bloquear modo oscuro (Issue 21), teclado cubre campos al escribir (Issue 22), tarjetas mock del dashboard (Issue 23).

**[2026-06-09] - Generación de APK de Debug para Pruebas**
- Se generó el archivo APK en versión de depuración (debug) mediante Gradle para facilitar las pruebas manuales en dispositivos físicos.

**[2026-06-04] - Fase 12: Sincronización, Tests y Refactor (Issue 12.2)**
- **Roadmap:** Sincronizados y marcados como completos los Issues silentes de permisos, exportación/importación de base de datos, BottomNav y Use Cases.
- **Tests Instrumentados:** Diagnosticados y programados para solución los errores de compilación de DAOs (`CampaniaDaoTest` y `CampaniaInsumoDaoTest`) que fallaban por nomenclaturas antiguas.
- **Refactor:** Añadida la tarea para limpiar las importaciones comodín (`*`) a lo largo del proyecto para apegarse a las mejores prÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡cticas de Kotlin.

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
  - AnÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡lisis detallado de discrepancias entre el diseño original (2025) y la arquitectura final implementada.
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
- Implementado catÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡logo de Insumos con íconos e integración a base de datos.
- Integrado YCharts para grÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡ficos de pie en Dashboard de Reportes.
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
- **Issue 13 (Use Cases muertos):** Conexión de `EditarTareaUseCase`, `EliminarTareaUseCase`, `EditarInsumoCatalogoUseCase` y creación de `EliminarInsumoCatalogoUseCase`. DiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡logo de edición inline en `CatalogoInsumosScreen`.

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
- Agregado parÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡metro `campaniaId` opcional a `NavRoute.FormularioCosecha`.
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
- Conexión de `CatalogoInsumosScreen` al catÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡logo real con `ObtenerCatalogoInsumosUseCase`.
- Conexión de `FormularioInsumoScreen` a `CrearInsumoCatalogoUseCase` con validación y spinner.
- Refactorización de `InsumosScreen` (vinculación) con datos reales, cÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡lculo `cantidad ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬Â� precio` formateado y atajo "Crear nuevo insumo" si no existe en catÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡logo.
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
- Bugfix: `CrearCampaniaUseCase` ahora acepta parÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡metro `cultivo` ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã¯¿Â½ el campo ya no se pierde al crear campañas nuevas.
- Bugfix: `CampaniaFormViewModel` pasa `cultivo` al `crearCampaniaUseCase`.
- Bugfix: `GestionParcelasScreen`, `TareasScreen`, `InsumosScreen`, `CosechasScreen`, `ObservacionesScreen` ya no hardcodean `campaniaId=1` ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã¯¿Â½ todas las rutas aceptan `campaniaId` opcional y lo propagan correctamente.
- Limpieza: eliminado parÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡metro `onEditar` no usado en `HeaderCampania`.

**[2026-05-14] - Pantalla Detalle de Campaña con Tabs y encabezado fijo (F4/Issue4)**
- Creación de `CampaniaDetailViewModel` con `SavedStateHandle` para carga de campaña por ID + eliminación.
- Rediseño de `DetalleCampaniaScreen` con TopAppBar dinÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡mico, encabezado fijo (nombre, cultivo, fechas, estado) y TabRow con 5 tabs: Info, Tareas, Insumos, Cosechas, Observaciones.
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
- Simplificación de la ruta `FormularioCampania` (sin parÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡metro opcional).

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
- Definicición de rutas con parÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡metros (`DetalleCampania`, `FormularioCampania`).

**[2026-05-12] - Implementación de Casos de Uso (Campañas y Tareas) - F3/Issue4**
- Creación de `CrearCampaniaUseCase`, `EditarCampaniaUseCase`, `EliminarCampaniaUseCase` y `ObtenerCampaniasUseCase`.
- Cada Use Case con `@Inject constructor` y validación de nombre no vacío.
- Creación de `CrearTareaUseCase`, `EditarTareaUseCase`, `EliminarTareaUseCase` y `ConfirmarTareaUseCase`.

**[2026-05-14] - Implementación de Resource<T> y manejo de errores en Use Cases**
- Creación de `Resource<T>` en `domain/model/` con extensiones `onSuccess`, `onError`, `isSuccess`, `isError`.
- Refactorización de 7 Use Cases para retornar `Flow<Resource<Unit>>` con emisión de Loading, Success y Error.
- Manejo de excepciones con try/catch y ejecución en `Dispatchers.IO` mediante `flowOn`.

**[2026-05-14] - Corrección de mapeo Campania, unificación de nomenclatura e implementación de Use Cases faltantes**
- Corregido mapeo bidireccional `Campania` → `CampaniaEntity`: agregado `cultivo` al modelo de dominio y `estaActiva` a la entidad; eliminados hardcodeos en `Mappers.kt`.
- Renombrado `campaniaId` → `idCampania` en `TareaRepository`, `CosechaRepository` y sus implementaciones.
- Creados modelos de dominio `Observacion` y `CampaniaInsumo` para mantener la pureza de la capa domain.
- Creados `CampaniaInsumoRepository` y `ObservacionRepository` con sus implementaciones y bindings de Hilt.
- Agregados mappers para `ObservacionEntity` → `Observacion` y `CampaniaInsumoEntity` → `CampaniaInsumo`.
- Implementados 6 casos de uso: `RegistrarCosechaUseCase`, `CrearInsumoCatalogoUseCase`, `EditarInsumoCatalogoUseCase`, `ObtenerCatalogoInsumosUseCase`, `AsignarInsumoACampaniaUseCase`, `GuardarObservacionUseCase`.

**[2026-05-12] - Card campaña activa en Tareas/Cosechas/Observaciones + botón exportar en Reportes + diagrama de flujo**
- TareasScreen, CosechasScreen y ObservacionesScreen: añadida `CampanaSeleccionadaCard` de la campaña activa.
- ReportesRendimientoScreen: añadido botón de exportar (Excel/PDF) en TopAppBar con `DropdownMenu`.
- Creado `docs/FLOW.md` con diagrama Mermaid de navegación y tabla de cobertura de Casos de Uso.

**[2026-05-12] - Refactor de navegación global, módulo de insumos y reportes**
- BottomNav: añadido acceso directo a `Destino.Insumos`; renombrado "Agenda" → "Tareas" y "Parcelas" → "Campañas".
- Home: `CampaniaSeleccionadaCard` ahora navega a `DetalleCampania`; botón + navega a `FormularioCampania`.
- InsumosScreen: reemplazado formulario inline por `ModalBottomSheet` con buscador, selector cantidad/precio y botón "Agregar al catÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡logo".
- FormularioInsumoScreen: simplificado a solo campos Nombre, Categoría y Unidad.
- ReportesRendimientoScreen: añadidas tarjetas de métricas comparativas (Rendimiento, Ganancias, Costos, Insumos); selector dropdown para comparar dos campañas; grÃƒÆ’Ã†â€™Ãƒâ€šÃ‚¡ficos Canvas de evolución mensual (Costos/Insumos) con leyenda bicolor.

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

**[2026-08-21] - Fix Inserción de Insumos al CatÃƒÆ’Ã‚¡logo [#334]**
- Se corrigió un error donde FormularioInsumoViewModel leía un insumoId = -1 por defecto y causaba que se ejecutara el flujo de actualización silenciosamente en lugar de crear uno nuevo.

**[2026-08-21] - Fix Edición de Cosechas [#335]**
- Se agregó el parÃƒÆ’Ã‚¡metro cosechaId a la ruta de navegación de FormularioCosecha y se vinculó el evento onEditarCosecha para permitir la edición correcta de las cosechas.

**[2026-08-21] - Fix Validación de Formulario de Cosechas [#336]**
- Se añadió una propiedad errorGeneral para evitar que todos los errores del formulario de cosecha se agruparan erróneamente en el campo cantidad, mostrando en cambio un Snackbar universal.
**[2026-08-21] - Fix Reportes Exportación vacía y Comparador [#355] [#356]**
- Se agregó una guardia en ReportesViewModel para evitar exportar PDFs o CSVs vacíos cuando no hay datos en la campaña seleccionada.
- Se implementó una tarjeta de advertencia en ReportesRendimientoScreen para prevenir que el usuario seleccione la misma campaña en ambos selectores del comparador, documentando el caso en el plan de pruebas.
**[2026-08-21] - Fix UI Detalles y Reportes [#339] [#340]**
- Se migró el TabRow a ScrollableTabRow en DetalleCampaniaScreen para evitar que los nombres de las pestañas se corten o dividan en varias líneas.
- Se ocultó la leyenda por defecto de los grÃƒÆ’Ã‚¡ficos PieChart en ReportesRendimientoScreen y se creó una leyenda manual debajo utilizando FlowRow, solucionando el problema de solapamiento de etiquetas en el grÃƒÆ’Ã‚¡fico.






**[2026-09-15] - Fix Error de compilación en pruebas**
- Se corrigió el acceso a las propiedades 'nombre' y 'estaActiva' en CampaniaDaoTest.kt que impedían compilar el proyecto y generar el APK debido a referencias desactualizadas tras refactorizar CampaniaConCultivoSchema.

