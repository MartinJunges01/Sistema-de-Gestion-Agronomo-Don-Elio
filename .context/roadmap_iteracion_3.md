# Roadmap: Iteración 3 — Nuevas Funcionalidades, Estabilización y Reportes Avanzados

> **Fuente:** Revisión manual de APK en dispositivo — 2026-08-18 + Deuda técnica pendiente de `docs/bugs_identificados.md`.
>
> **Iteración anterior:** Todos los issues del `roadmap_iteracion_2.md` fueron completados ✅.

---

## Checklist de Progreso

### 🔴 NIVEL L1 — CRASHES Y ERRORES CRÍTICOS
- [x] **[#334] Issue 334:** No se añaden insumos al catálogo
- [ ] **[#335] Issue 335:** El botón de edición de cosechas no funciona

### 🟠 NIVEL L2 — BUGS FUNCIONALES
- [ ] **[#336] Issue 336:** Validación del formulario de cosecha marca el campo incorrecto
- [x] **[#337] Issue 337:** El botón de edición en observaciones solo funciona para texto (no para foto)

### ⚪ NIVEL L3 — FIX UI / UX
- [x] **[#338] Issue 338:** El teclado bloquea el movimiento de la pantalla al escribir (bloque blanco superior)
- [ ] **[#339] Issue 339:** Header de columnas cortado en el panel de detalle de campaña
- [x] **[#340] Issue 340:** Etiquetas del gráfico de reportes desacomodadas

### 🟡 NIVEL L4 — FEATURES NUEVAS
- [x] **[#341] Issue 341:** Persistencia de sesión (login una sola vez)
- [x] **[#342] Issue 342:** Color y botón de borrado para campañas inactivas
- [x] **[#343] Issue 343:** Exportación de reportes con datos de cosechas
- [x] **[#344] Issue 344:** Referencias y valores absolutos de gasto por insumos ordenados por costo
- [x] **[#345] Issue 345:** Rediseño de la Pantalla de Tareas (filtros + calendario con rango)

### 🔵 NIVEL L5 — NUEVOS DESARROLLOS (Dashboard y Reportes Avanzados)
- [x] **[#346] Issue 346:** Resumen Financiero Rápido en Dashboard (vistazo mensual)
- [x] **[#347] Issue 347:** Tasa de Cumplimiento de Tareas (gráfico circular en Dashboard)
- [x] **[#348] Issue 348:** Top 3 Insumos de Mayor Gasto (en pestaña Reportes)
- [x] **[#349] Issue 349:** Refactorizar DB — Hectáreas en campaña + métrica Tn/Ha en reportes
- [x] **[#350] Issue 350:** Costo por Hectárea ($/Ha) en reportes
- [x] **[#351] Issue 351:** ABM de Cultivos (Catálogo estandarizado + nueva entidad DB)
- [x] **[#352] Issue 352:** Evolución Histórica por Cultivo (gráfico de tendencia)
- [x] **[#353] Issue 353:** Filtros Avanzados en Reportes (rango de fechas + selector de campañas + datos financieros)

### 📋 NIVEL L6 — DEUDA TÉCNICA (Pendientes de Iteración 2)
- [x] **[#354] Issue 354:** InsumoVinculacionViewModel sin método sincronizarInsumos()
- [x] **[#355] Issue 355:** Exportación CSV/PDF sin identificación de campaña en el encabezado
- [ ] **[#356] Issue 356:** Comparador permite seleccionar la misma campaña para A y B
- [x] **[#357] Issue 357:** ReportExporter no soporta PDF de múltiples páginas
- [x] **[#358] Issue 358:** DoubleBarIndicator no tiene tests unitarios
- [x] **[#359] Issue 359:** Tests VM-R8 y VM-R9 (guardia de exportación) sin implementación
- [x] **[#360] Issue 360:** UX Inconsistente de Validación entre Formulario de Insumos y Campañas

---
---

# 🔴 NIVEL L1 — CRASHES Y ERRORES CRÍTICOS

Estos issues impiden el uso de funcionalidades core de la aplicación.

---

## [#334] Issue 334: No se añaden insumos al catálogo

**Severidad:** 🔴 Bug Bloqueante
**Módulo:** Insumos / Catálogo
**Archivos afectados:**
- `presentation/viewmodel/insumo/FormularioInsumoViewModel.kt`
- `presentation/viewmodel/insumo/InsumoCatalogoViewModel.kt`
- `domain/use_case/CrearInsumoUseCase.kt`
- `data/local/dao/InsumoDao.kt`

**Descripción**
Al intentar agregar un nuevo insumo desde la pantalla del catálogo de insumos, el insumo no se persiste en la base de datos. La operación falla silenciosamente — el usuario no recibe ningún mensaje de error y el insumo simplemente no aparece en la lista.

> **Nota:** Este issue fue reportado en la iteración 2 como parte del Issue 341 (validación de formulario), pero el flujo de inserción completo sigue sin funcionar correctamente tras la revisión manual en dispositivo.

**Acceptance Criteria**
- Al completar todos los campos obligatorios (nombre, categoría, unidad) y presionar "Guardar", el insumo debe persistirse en la base de datos.
- El insumo creado debe aparecer inmediatamente en la lista del catálogo (observado vía `Flow`).
- Si ocurre un error en la inserción, debe mostrarse un `Snackbar` con el mensaje de error.

**Sub-issues / Tareas Técnicas**
- Verificar que `CrearInsumoUseCase` invoque correctamente al `InsumoRepository.insertar()`.
- Verificar que `InsumoDao.insertarInsumo()` esté implementado con `@Insert(onConflict = OnConflictStrategy.REPLACE)` o equivalente.
- Verificar que `FormularioInsumoViewModel.guardar()` llame al UseCase y maneje la respuesta.
- Agregar logging en el ViewModel para diagnosticar fallos silenciosos.
- Verificar que la lista se actualice reactivamente vía `Flow<List<InsumoEntity>>`.

---

## [#335] Issue 335: El botón de edición de cosechas no funciona

**Severidad:** 🔴 Bug Bloqueante
**Módulo:** Cosechas / Edición
**Archivos afectados:**
- `presentation/ui/screen/cosecha/CosechasScreen.kt`
- `presentation/viewmodel/cosecha/CosechaViewModel.kt`
- `presentation/viewmodel/cosecha/FormularioCosechaViewModel.kt`

**Descripción**
Al presionar el botón de edición (ícono lápiz) en una tarjeta de cosecha existente, no ocurre ninguna acción. El botón no navega al formulario de edición ni abre un diálogo. La funcionalidad de edición fue implementada en la iteración 2 (Issue #295 / Issue 347), pero no está funcional en el dispositivo.

**Acceptance Criteria**
- Al presionar el botón de edición en una cosecha, debe abrirse el formulario de edición pre-rellenado con los datos actuales.
- El usuario debe poder modificar cantidad, fecha, hectáreas y tipo de almacenamiento.
- Al guardar los cambios, la cosecha debe actualizarse en la BD y la lista debe refrescarse.

**Sub-issues / Tareas Técnicas**
- Verificar que el `onClick` del botón de edición esté conectado a la navegación correcta o a la apertura del diálogo.
- Verificar que `FormularioCosechaViewModel.cargarCosecha(id)` funcione correctamente en modo edición.
- Verificar que el `EditarCosechaUseCase` esté inyectado y funcional.
- Testear el flujo completo: click editar → formulario cargado → guardar → lista actualizada.

---
---

# 🟠 NIVEL L2 — BUGS FUNCIONALES

Comportamiento incorrecto que no crashea la app pero produce resultados erróneos.

---

## [#336] Issue 336: Validación del formulario de cosecha marca el campo incorrecto

**Severidad:** 🟠 Bug Funcional
**Módulo:** Cosechas / Formulario / Validación
**Archivos afectados:**
- `presentation/viewmodel/cosecha/FormularioCosechaViewModel.kt`
- `presentation/ui/screen/cosecha/FormularioCosechaScreen.kt`

**Descripción**
Al intentar guardar una cosecha con un campo faltante o inválido, el indicador de error visual (borde rojo + mensaje de soporte) se muestra en un campo diferente al que tiene el problema. Por ejemplo, si falta la cantidad, el error podría marcarse en el campo de fecha o en otro campo no relacionado.

**Causa Raíz (Código)**
Probable desincronización entre los nombres de los estados de error (`errorCantidad`, `errorPrecio`, etc.) y su binding en la UI. Los `isError` y `supportingText` de los `OutlinedTextField` podrían estar apuntando al estado de error equivocado.

**Acceptance Criteria**
- Cada campo del formulario de cosecha debe mostrar su error correspondiente y no el de otro campo.
- Validar la correspondencia 1:1 entre: campo de estado de error en el ViewModel ↔ `isError` y `supportingText` en el `OutlinedTextField` de la UI.
- Los errores deben limpiarse al corregir el valor del campo correspondiente.

**Sub-issues / Tareas Técnicas**
- Auditar el binding de cada campo en `FormularioCosechaScreen.kt` y verificar que `isError = state.errorX != null` apunte al campo correcto.
- Verificar que cada `supportingText` muestre `state.errorX` del campo correspondiente y no de otro.
- Agregar test unitario en `FormularioCosechaViewModelTest` para validar que cada error se emite para el campo correcto.

---

## [#337] Issue 337: El botón de edición en observaciones solo funciona para texto (no permite editar/eliminar foto)

**Severidad:** 🟠 Bug Funcional
**Módulo:** Observaciones / Edición
**Archivos afectados:**
- `presentation/ui/screen/observacion/ObservacionesScreen.kt`
- `presentation/viewmodel/observacion/ObservacionViewModel.kt`
- `domain/use_case/EditarObservacionUseCase.kt`

**Descripción**
Al editar una observación existente que tiene una foto adjunta, el diálogo de edición solo permite modificar el texto descriptivo. No hay opción para:
1. Reemplazar la foto existente por una nueva (desde cámara o galería).
2. Eliminar la foto manteniendo solo el texto.

**Acceptance Criteria**
- El diálogo de edición de observaciones debe mostrar la foto actual (si existe) con opciones para:
  - **Reemplazar foto:** Botón que abra el selector de cámara/galería.
  - **Eliminar foto:** Botón (ícono ✕ sobre la imagen) que limpie el campo `imagenUri`.
- Si la observación no tiene foto, debe permitir agregar una.
- Los cambios de foto deben persistirse correctamente al guardar.

**Sub-issues / Tareas Técnicas**
- En el diálogo de edición de `ObservacionesScreen.kt`: Agregar vista previa de la imagen actual con botones de acción (reemplazar/eliminar).
- Integrar `cameraLauncher` y `galleryLauncher` en el diálogo de edición.
- En `ObservacionViewModel.editarObservacion()`: Aceptar el nuevo `imagenUri` (nullable) y propagarlo al UseCase.
- En `EditarObservacionUseCase`: Actualizar el campo `imagenUri` de la entidad.

---
---

# ⚪ NIVEL L3 — FIX UI / UX

Problemas visuales y de experiencia de usuario que afectan la usabilidad.

---

## [#338] Issue 338: El teclado bloquea el movimiento de la pantalla al escribir (con bloque blanco superior)

**Severidad:** ⚪ UX / Calidad
**Módulo:** Global / Todos los formularios
**Archivos afectados:**
- `presentation/MainActivity.kt`
- Todas las pantallas con formularios

**Descripción**
A pesar de que el Issue 355 de la iteración 2 aplicó `imePadding()` globalmente, persisten dos problemas relacionados al teclado:
1. **Bloqueo de scroll:** En algunas pantallas, el teclado sigue bloqueando el movimiento/scroll al escribir.
2. **Bloque blanco superior:** Al aparecer el teclado, se genera un bloque o franja blanca en la parte superior de la pantalla que reduce el área visible.

> **Nota:** Esto puede ser un efecto secundario de la combinación de `enableEdgeToEdge()` + `imePadding()` + `WindowInsets` mal configurados.

**Acceptance Criteria**
- Al abrir el teclado en cualquier formulario, el campo activo debe permanecer visible.
- No debe aparecer ninguna franja/bloque blanco en la parte superior.
- El scroll debe funcionar normalmente con el teclado abierto en todas las pantallas.

**Sub-issues / Tareas Técnicas**
- Revisar la configuración de `WindowInsets` en `MainActivity.kt` y el `Scaffold` principal.
- Verificar que `imePadding()` se aplique en el nivel correcto del árbol de composición.
- Testear con `Modifier.imeNestedScroll()` como alternativa para el scroll.
- Investigar si `consumeWindowInsets = false` en el `Scaffold` resuelve el bloque blanco.
- Testear en múltiples dispositivos/resoluciones.

---

## [#339] Issue 339: Header de columnas cortado en panel de detalle de campaña

**Severidad:** ⚪ UX / Calidad
**Módulo:** Detalle Campaña / UI
**Archivos afectados:**
- `presentation/ui/screen/campania/DetalleCampaniaScreen.kt` (o la tabla de detalle correspondiente)

**Descripción**
En la pantalla de detalle de campaña, al hacer click en una campaña, los encabezados de las columnas de la tabla de datos se muestran con palabras cortadas. El texto no se ajusta al ancho disponible ni hace wrap, resultando en etiquetas ilegibles como "Rendi..." o "Cant...".

**Acceptance Criteria**
- Todos los encabezados de columna deben ser legibles sin cortes.
- Opciones de resolución:
  - Reducir el tamaño de fuente de los headers.
  - Usar abreviaciones claras (ej: "Cant." en vez de "Cantidad").
  - Hacer las columnas horizontalmente scrolleables.
  - Usar `softWrap = true` con `maxLines = 2`.
- El contenido de las celdas también debe ser legible.

**Sub-issues / Tareas Técnicas**
- Identificar el Composable que renderiza la tabla de detalle.
- Ajustar los `Text()` de los headers con `overflow = TextOverflow.Visible`, `softWrap = true`.
- Alternativamente, envolver la tabla en un `horizontalScroll()`.
- Verificar en diferentes tamaños de pantalla.

---

## [#340] Issue 340: Etiquetas del gráfico de reportes desacomodadas

**Severidad:** ⚪ UX / Calidad
**Módulo:** Reportes / Gráficos
**Archivos afectados:**
- `presentation/ui/screen/reportes/ReportesRendimientoScreen.kt`

**Descripción**
Las etiquetas y referencias del gráfico de reportes (PieChart o Canvas de evolución) se superponen entre sí o se salen del área visible del gráfico. Las leyendas de referencia tampoco están correctamente alineadas con sus colores correspondientes.

**Acceptance Criteria**
- Las etiquetas del gráfico no deben superponerse entre sí.
- Las referencias/leyendas deben estar alineadas correctamente con sus colores.
- En pantallas pequeñas, las leyendas deben adaptarse (wrapping o scroll horizontal).
- Los valores numéricos deben ser legibles y no cortarse.

**Sub-issues / Tareas Técnicas**
- Revisar el componente de leyenda en `ReportesRendimientoScreen.kt`.
- Ajustar el espaciado entre elementos de la leyenda.
- Considerar usar `FlowRow` o `LazyRow` para las referencias.
- Ajustar los offsets de las etiquetas del PieChart para evitar superposición.

---
---

# 🟡 NIVEL L4 — FEATURES NUEVAS

Funcionalidades nuevas identificadas durante la revisión que mejoran la experiencia del usuario.

---

## [#341] Issue 341: Persistencia de sesión (login una sola vez)

**Severidad:** 🟡 Feature
**Módulo:** Login / Session / Navegación
**Archivos afectados:**
- `core/SessionManager.kt`
- `presentation/viewmodel/login/LoginViewModel.kt`
- `presentation/navigation/NavGraph.kt`
- `presentation/MainActivity.kt`

**Descripción**
Actualmente el usuario debe iniciar sesión cada vez que abre la aplicación. No hay persistencia de la sesión entre cierres de app. Se requiere que al hacer login una vez, la sesión quede guardada y la próxima vez que se abra la app, se salte directamente al Dashboard.

**Acceptance Criteria**
- Tras un login exitoso, la sesión debe persistirse en `DataStore`.
- Al abrir la app, si existe una sesión válida, navegar directamente al Dashboard sin mostrar la pantalla de login.
- Debe existir un botón "Cerrar sesión" en algún lugar accesible (drawer, menú, o settings) que limpie la sesión y vuelva al login.
- El flujo de "Invitado" también debe persistirse.

**Sub-issues / Tareas Técnicas**
- En `SessionManager`: Agregar `val isLoggedIn: Flow<Boolean>` basado en DataStore.
- En `LoginViewModel.login()`: Tras éxito, llamar `sessionManager.saveSession(userId, userName)`.
- En `NavGraph.kt` o `MainActivity`: Leer `isLoggedIn` al arranque y decidir la ruta de inicio (`Login` vs `Dashboard`).
- Implementar `fun cerrarSesion()` en `SessionManager` que limpie el DataStore.
- Agregar UI para "Cerrar sesión" (preferiblemente en el header del Dashboard o en un menú).

---

## [#342] Issue 342: Color y botón de borrado para campañas inactivas

**Severidad:** 🟡 Feature / UX
**Módulo:** Gestión de Campañas
**Archivos afectados:**
- `presentation/ui/screen/campania/GestionCampaniasScreen.kt`
- `presentation/viewmodel/campania/GestionCampaniasViewModel.kt`

**Descripción**
Las campañas que están marcadas como inactivas (finalizadas) necesitan diferenciarse visualmente con mayor claridad y tener la opción de ser eliminadas permanentemente. Actualmente se ven iguales a las activas salvo por un badge.

**Acceptance Criteria**
- Las campañas inactivas deben mostrarse con un color atenuado/gris distinguible (fondo `Color.Gray.copy(alpha = 0.1f)` o similar).
- Cada campaña inactiva debe tener un botón "Eliminar" (ícono papelera) que ejecute un hard delete tras confirmación.
- El diálogo de confirmación debe decir: "¿Eliminar permanentemente la campaña [nombre]? Esta acción eliminará todos los datos asociados (cosechas, insumos, observaciones, tareas) y no se puede deshacer."
- La eliminación debe ser en cascada (verificar FK con `onDelete = CASCADE`).

**Sub-issues / Tareas Técnicas**
- En `GestionCampaniasScreen.kt`: Aplicar estilo atenuado condicional a las tarjetas de campañas inactivas.
- Agregar botón de eliminar con `IconButton` + ícono de papelera solo en campañas inactivas.
- En `GestionCampaniasViewModel`: Implementar `eliminarCampaniaPermanente(id: Int)` con hard delete.
- Crear `EliminarCampaniaPermanenteUseCase` o usar el existente.
- Agregar diálogo de confirmación con texto descriptivo.

---

## [#343] Issue 343: Agregar datos de cosechas a la exportación de reportes

**Severidad:** 🟡 Feature
**Módulo:** Reportes / Exportación
**Archivos afectados:**
- `core/utils/ReportExporter.kt`
- `presentation/viewmodel/reportes/ReportesViewModel.kt`

**Descripción**
Actualmente la exportación de reportes (CSV/PDF) solo incluye los datos de insumos y sus gastos. No se incluyen los datos de cosechas de la campaña seleccionada: cantidades cosechadas, tipos (almacenada/vendida/reservada), fechas y valores de venta.

**Acceptance Criteria**
- El reporte exportado (CSV y PDF) debe incluir una sección de cosechas con: fecha, cantidad (Tn), hectáreas, tipo de almacenamiento y precio de venta (si aplica).
- El PDF debe tener una sección diferenciada "Cosechas" debajo de la sección "Insumos".
- El CSV debe incluir columnas adicionales para los datos de cosechas o una segunda hoja/sección.
- El total de rendimiento bruto y Tn/Ha debe aparecer como resumen al final.

**Sub-issues / Tareas Técnicas**
- En `ReportesViewModel`: Exponer `cosechasPorCampania: StateFlow<List<Cosecha>>` filtrado por la campaña seleccionada.
- En `ReportExporter.exportToPdf()`: Agregar sección "Cosechas" con tabla y totales.
- En `ReportExporter.exportToCsv()`: Agregar filas de cosechas con separador de sección.
- Considerar la paginación del PDF (ver Issue 355 de deuda técnica).

---

## [#344] Issue 344: Referencias y valores absolutos de gasto por insumos ordenados por costo total

**Severidad:** 🟡 Feature
**Módulo:** Reportes / Gráficos
**Archivos afectados:**
- `presentation/ui/screen/reportes/ReportesRendimientoScreen.kt`
- `presentation/viewmodel/reportes/ReportesViewModel.kt`

**Descripción**
El gráfico de insumos necesita incluir valores absolutos en pesos ($) junto a los porcentajes, y la lista de referencia/leyenda debe estar ordenada de mayor a menor gasto total. Actualmente los valores solo se muestran como porcentaje en el PieChart sin una leyenda con montos.

**Acceptance Criteria**
- La leyenda del gráfico de insumos debe mostrar para cada insumo: nombre, color, monto total ($X.XXX) y porcentaje (XX%).
- La lista debe estar ordenada de mayor a menor gasto total.
- Los montos deben formatearse con separador de miles (locale `es-AR`).
- Debe mostrarse el total general sumado al final de la leyenda.

**Sub-issues / Tareas Técnicas**
- En `ReportesViewModel`: Ordenar `exportableData` por `costoTotal` descendente.
- En `ReportesRendimientoScreen.kt`: Mejorar el componente de leyenda para incluir monto y porcentaje.
- Formatear valores con `NumberFormat.getCurrencyInstance(Locale("es", "AR"))`.

---

## [#345] Issue 345: Rediseño de la Pantalla de Tareas (Filtros + Calendario con Rango)

**Severidad:** 🟡 Feature / Refactor
**Módulo:** Tareas / UI
**Archivos afectados:**
- `presentation/ui/screen/tarea/TareasScreen.kt` (o pantalla principal de tareas)
- `presentation/viewmodel/tarea/TareaViewModel.kt`
- `domain/use_case/ObtenerTareasPorRangoUseCase.kt` (nuevo o parametrizado)

**Descripción**
La pantalla de tareas actual no satisface las necesidades. Se requiere un rediseño completo:

1. **Vista por defecto (sin filtros):** Listar todas las tareas pendientes/próximas de todas las campañas, ordenadas por fecha, sin ningún filtro aplicado.
2. **Filtro por campaña:** Agregar selector para filtrar por una campaña específica.
3. **Filtro por fecha con calendario:** Agregar un calendario (similar al `DatePickerDialog` del formulario) que permita seleccionar un **rango de fechas** (desde–hasta). Con este filtro el usuario puede ver tareas antiguas (vencidas o finalizadas) que por defecto no se muestran.

> **Nota:** El componente de calendario con rango de fechas es reutilizable y se usará también en el Issue 353 (Filtros Avanzados en Reportes).

**Acceptance Criteria**
- Al abrir la pantalla de tareas, se muestran **todas las tareas próximas/pendientes** sin filtros.
- Selector de campaña (dropdown o chips) para filtrar por campaña específica o "Todas".
- Botón/ícono de calendario que abre un `DateRangePicker` de Material 3 para seleccionar rango de fechas.
- Al aplicar un rango de fechas, se muestran las tareas de ese período (incluyendo vencidas y completadas).
- Chip o badge que indique los filtros activos con opción de "limpiar filtros".
- Las tareas vencidas se muestran con estilo visual diferente (tinte rojo suave).
- Las tareas completadas se muestran con estilo tachado o atenuado.

**Sub-issues / Tareas Técnicas**
- Rediseñar la UI de `TareasScreen.kt` con barra de filtros en la parte superior.
- En `TareaViewModel`: Agregar estados `filtrosCampania: StateFlow<Int?>` y `filtroFechas: StateFlow<Pair<Long, Long>?>`.
- Crear o parametrizar `ObtenerTareasPorRangoUseCase(campaniaId: Int?, fechaDesde: Long?, fechaHasta: Long?)`.
- Implementar `DateRangePicker` de Material 3 (`rememberDateRangePickerState()`).
- Crear componente reutilizable `SelectorRangoFechas.kt` para compartir con Reportes.
- Agregar lógica de limpieza de filtros.

---
---

# 🔵 NIVEL L5 — NUEVOS DESARROLLOS (Dashboard y Reportes Avanzados)

Nuevas funcionalidades de análisis financiero y productivo.

---

## [#346] Issue 346: Resumen Financiero Rápido en Dashboard (Vistazo Mensual)

**Severidad:** 🔵 Feature Nueva
**Módulo:** Dashboard / Home
**Archivos afectados:**
- `presentation/ui/screen/home/DashboardOperacionesScreen.kt`
- `presentation/viewmodel/home/HomeViewModel.kt`
- Nuevos UseCases de cálculo financiero

**Descripción**
El Dashboard debe mostrar un "vistazo" financiero rápido y simple de las operaciones del **mes actual** de todas las campañas activas. La idea es información de un solo vistazo, sin filtros complejos. Para análisis más profundo, un link "Ver detalle →" lleva a la pestaña de Reportes (Issue 353) donde sí se aplican filtros avanzados.

Las métricas del vistazo:

1. **Capital Invertido:** Sumatoria del costo total de insumos vinculados a las campañas activas.
2. **Ingresos Brutos:** Sumatoria del dinero generado por las cosechas vendidas de las campañas activas.
3. **Balance Actual:** Ingresos − Inversión. Verde si positivo, rojo si negativo.

**Acceptance Criteria**
- Tres tarjetas compactas en el Dashboard: Capital Invertido ($), Ingresos Brutos ($), Balance ($ con color semáforo).
- Los datos se calculan sobre el mes en curso a partir de campañas donde `estaActiva = true`.
- Capital Invertido = `Σ (cantidad × precio)` de insumos vinculados a campañas activas.
- Ingresos Brutos = `Σ (precio × cantidad)` de cosechas vendidas de campañas activas.
- Balance = Ingresos Brutos − Capital Invertido.
- El balance se muestra en **verde** (positivo) o **rojo** (negativo).
- Texto pequeño indicando "Este mes" debajo de las tarjetas.
- Botón `TextButton("Ver detalle →")` que navegue a la pestaña de Reportes con los filtros financieros (Issue 353).

**Sub-issues / Tareas Técnicas**
- Crear `ObtenerResumenFinancieroUseCase` en `domain/use_case/`.
- Crear data class `ResumenFinanciero(capitalInvertido: Double, ingresosBrutos: Double, balance: Double)`.
- En `HomeViewModel`: Inyectar el UseCase y exponer `resumenFinanciero: StateFlow<ResumenFinanciero>`.
- En `DashboardOperacionesScreen.kt`: Agregar sección con 3 `Card` compactas de métricas financieras + botón "Ver detalle".
- Formatear montos con `NumberFormat.getCurrencyInstance()`.

---

## [#347] Issue 347: Tasa de Cumplimiento de Tareas (Gráfico Circular)

**Severidad:** 🔵 Feature Nueva
**Módulo:** Dashboard / Home
**Archivos afectados:**
- `presentation/ui/screen/home/DashboardOperacionesScreen.kt`
- `presentation/viewmodel/home/HomeViewModel.kt`

**Descripción**
Agregar al Dashboard un gráfico circular pequeño que muestre la tasa de cumplimiento de tareas semanal. Por ejemplo: "80% de tareas completadas esta semana". Esto agrega más valor que solo ver la lista de tareas pendientes.

**Acceptance Criteria**
- Gráfico circular (donut) pequeño en el Dashboard que muestre el % de tareas completadas de la semana actual.
- Texto central o inferior: "X% completadas" o "X de Y tareas".
- Colores: verde para completadas, gris para pendientes.
- La semana se calcula de lunes a domingo.
- Si no hay tareas en la semana, mostrar "Sin tareas esta semana".

**Sub-issues / Tareas Técnicas**
- Crear `ObtenerCumplimientoTareasUseCase` que calcule tareas completadas vs totales en un rango de fechas.
- En `HomeViewModel`: Exponer `cumplimientoSemanal: StateFlow<CumplimientoTareas>`.
- Crear data class `CumplimientoTareas(completadas: Int, total: Int, porcentaje: Float)`.
- En `DashboardOperacionesScreen.kt`: Implementar mini donut chart usando `Canvas` con `drawArc()`.
- Agregar texto de porcentaje al centro o debajo del gráfico.

---

## [#348] Issue 348: Top 3 Insumos de Mayor Gasto (en pestaña Reportes)

**Severidad:** 🔵 Feature Nueva
**Módulo:** Reportes
**Archivos afectados:**
- `presentation/ui/screen/reportes/ReportesRendimientoScreen.kt`
- `presentation/viewmodel/reportes/ReportesViewModel.kt`

**Descripción**
Una lista rápida en la pestaña de Reportes que muestre los 3 insumos en los que se está gastando más presupuesto. Se muestra contextual a la campaña seleccionada en el selector de reportes.

**Acceptance Criteria**
- Sección "Top 3 Insumos" visible al seleccionar una campaña en Reportes.
- Cada ítem debe mostrar: posición (#1, #2, #3), nombre del insumo, costo total ($), y porcentaje sobre el gasto total.
- Barra de progreso horizontal proporcional al gasto.
- Si hay menos de 3 insumos, mostrar los que haya.

**Sub-issues / Tareas Técnicas**
- En `ReportesViewModel`: Calcular top 3 a partir de `exportableData` ya existente (ordenar por costo total descendente, tomar los primeros 3).
- Exponer `top3Insumos: StateFlow<List<InsumoGasto>>`.
- En `ReportesRendimientoScreen.kt`: Agregar sección con `Card` para cada insumo del top 3.
- Incluir `LinearProgressIndicator` proporcional.

---

## [#349] Issue 349: Refactorizar DB — Hectáreas en Campaña + Métrica Tn/Ha

**Severidad:** 🔵 Refactor / Feature
**Módulo:** Data / Campañas / Cosechas / Reportes
**Archivos afectados:**
- `data/local/entity/CampaniaEntity.kt`
- `data/local/entity/CosechaEntity.kt`
- `domain/model/Campania.kt`
- `domain/model/Cosecha.kt`
- `presentation/ui/screen/campania/FormularioCampaniaScreen.kt`
- `presentation/viewmodel/campania/CampaniaFormViewModel.kt`
- `presentation/viewmodel/reportes/ReportesViewModel.kt`
- `DonElioDatabase.kt`

**Descripción**
Refactorizar la base de datos para que las hectáreas se registren en la **campaña** en lugar de en la cosecha. Esto simplifica el modelo: una campaña tiene un campo fijo con el total de hectáreas trabajadas. Adicionalmente, agregar la métrica de **toneladas totales cosechadas / hectáreas del campo** a los reportes, manteniendo también el rendimiento total.

> **Nota:** Esto revierte la decisión de la iteración 2 (Issue 350 / #305) donde las hectáreas se ubicaron en la cosecha. La nueva decisión se basa en que las hectáreas del campo son un dato fijo de la campaña, no variable por cosecha.

**Acceptance Criteria**
- Mover el campo `hectareas: Double` de `CosechaEntity` a `CampaniaEntity`.
- Actualizar `Campania` (modelo de dominio) con el nuevo campo.
- Eliminar `hectareas` de `CosechaEntity` y `Cosecha`.
- El formulario de campaña debe incluir campo "Hectáreas" obligatorio.
- El formulario de cosecha ya no necesita el campo de hectáreas.
- En Reportes: Agregar métrica `Rendimiento (Tn/Ha) = Σ cosechas.cantidad / campaña.hectareas`.
- Mantener el rendimiento total (`Σ cosechas.cantidad`) también visible.
- Incrementar versión de DB con `fallbackToDestructiveMigration()`.

**Sub-issues / Tareas Técnicas**
- Migrar campo `hectareas` de `CosechaEntity` a `CampaniaEntity`.
- Actualizar mappers `toDomain()` / `toEntity()` de ambas entidades.
- Actualizar `FormularioCampaniaScreen` y `CampaniaFormViewModel` para incluir el campo.
- Actualizar `FormularioCosechaScreen` y `FormularioCosechaViewModel` para eliminar el campo.
- En `ReportesViewModel`: Calcular `rendimientoTnHa = totalCosechado / campania.hectareas`.
- Actualizar `ReportesRendimientoScreen.kt` para mostrar ambas métricas.
- Incrementar versión de DB.
- Actualizar tests afectados.

---

## [#350] Issue 350: Costo por Hectárea ($/Ha) en Reportes

**Severidad:** 🔵 Feature Nueva
**Módulo:** Reportes
**Archivos afectados:**
- `presentation/viewmodel/reportes/ReportesViewModel.kt`
- `presentation/ui/screen/reportes/ReportesRendimientoScreen.kt`

**Descripción**
Cruzar el total gastado en insumos con el total cosechado y las hectáreas para mostrar el **Costo por Hectárea ($/Ha)**: cuánta plata se invirtió por cada hectárea trabajada. Esta es una métrica clave para evaluar la rentabilidad del campo.

> **Dependencia:** Requiere Issue 349 (hectáreas en campaña).

**Acceptance Criteria**
- Nueva tarjeta/métrica en la sección individual de Reportes: "Costo por Hectárea: $X.XXX/Ha".
- Cálculo: `Costo/Ha = Σ insumos.costoTotal / campaña.hectareas`.
- Si `hectareas = 0` o no definida, mostrar "N/A" en vez de dividir por cero.
- Incluir esta métrica también en la comparación de campañas.

**Sub-issues / Tareas Técnicas**
- En `ReportesViewModel`: Calcular `costoPorHectarea = totalGastoInsumos / campania.hectareas` con guardia para hectáreas 0.
- Exponer la métrica en el estado de métricas individuales y comparativas.
- En `ReportesRendimientoScreen.kt`: Agregar `Card` de "Costo/Ha" junto a las métricas existentes.
- Formatear con `NumberFormat.getCurrencyInstance()`.

---

## [#351] Issue 351: ABM de Cultivos (Catálogo Estandarizado + Nueva Entidad DB)

**Severidad:** 🔵 Feature Nueva / Refactor
**Módulo:** Data / Campañas / Catálogo
**Archivos afectados:**
- `data/local/entity/CultivoEntity.kt` (NUEVO)
- `data/local/dao/CultivoDao.kt` (NUEVO)
- `domain/model/Cultivo.kt` (NUEVO)
- `domain/repository/CultivoRepository.kt` (NUEVO)
- `data/repository/CultivoRepositoryImpl.kt` (NUEVO)
- `domain/use_case/` (nuevos UseCases CRUD)
- `presentation/ui/screen/cultivo/CatalogoCultivosScreen.kt` (NUEVO)
- `presentation/viewmodel/cultivo/CultivoCatalogoViewModel.kt` (NUEVO)
- `presentation/ui/screen/campania/FormularioCampaniaScreen.kt` (modificar selector)
- `DonElioDatabase.kt` (agregar entidad)

**Descripción**
El campo `cultivo` en las campañas es actualmente texto libre, lo que genera inconsistencias (ej: "Soja", "soja", "SOJA", "Soja 1era") que imposibilitan agrupar campañas por cultivo para gráficos de evolución (Issue 352). Se requiere estandarizar con una nueva entidad `CultivoEntity` y un ABM (Alta/Baja/Modificación) completo.

> **Nota:** Este issue es **prerequisito** del Issue 352 (Evolución Histórica por Cultivo).

**Acceptance Criteria**
- Nueva entidad `CultivoEntity` con campos: `id: Int`, `nombre: String` (único), `activo: Boolean = true`.
- CRUD completo: crear, listar, editar nombre, soft-delete.
- Pantalla de catálogo de cultivos accesible desde un menú de configuración o desde el formulario de campaña.
- En el formulario de campaña: reemplazar el `OutlinedTextField` libre de cultivo por un `ExposedDropdownMenu` que liste los cultivos del catálogo.
- Opción rápida de "Agregar nuevo cultivo" desde el selector del formulario si el cultivo no existe.
- Incluir cultivos predeterminados vía `DataSeeder`: Soja, Trigo, Maíz, Girasol, Cebada, Sorgo, Algodón.
- El campo `cultivo: String` de `CampaniaEntity` se reemplaza por `cultivoId: Int` (FK a `CultivoEntity`).
- Incrementar versión de DB.

**Sub-issues / Tareas Técnicas**
- Crear `CultivoEntity`, `CultivoDao`, `CultivoRepository`, `CultivoRepositoryImpl`.
- Crear UseCases: `CrearCultivoUseCase`, `ObtenerCultivosUseCase`, `EditarCultivoUseCase`, `EliminarCultivoUseCase`.
- Crear `CatalogoCultivosScreen.kt` y `CultivoCatalogoViewModel.kt` (similar al catálogo de insumos).
- Migrar `CampaniaEntity.cultivo: String` → `CampaniaEntity.cultivoId: Int` con FK.
- Actualizar `FormularioCampaniaScreen.kt`: Reemplazar TextField por dropdown con lista de cultivos.
- Agregar cultivos predeterminados en `DataSeeder.kt`.
- Actualizar mappers y modelos de dominio.
- Proveer módulo Hilt para inyección de los nuevos componentes.
- Incrementar versión de DB con `fallbackToDestructiveMigration()`.

---

## [#352] Issue 352: Evolución Histórica por Cultivo (Gráfico de Tendencia)

**Severidad:** 🔵 Feature Nueva
**Módulo:** Reportes / Gráficos
**Archivos afectados:**
- `presentation/ui/screen/reportes/ReportesRendimientoScreen.kt`
- `presentation/viewmodel/reportes/ReportesViewModel.kt`

**Descripción**
Un gráfico de líneas o barras que permita visualizar la evolución de un cultivo específico (ej: Soja) a través de los años/campañas. Por ejemplo, ver cómo evolucionó el rendimiento (Tn/Ha) de la Soja en las campañas 2023, 2024, 2025 y 2026 para analizar si el campo está perdiendo fertilidad o mejorando.

> **Dependencias:** Requiere Issue 349 (hectáreas en campaña) para la métrica Tn/Ha y Issue 351 (ABM de Cultivos) para agrupar campañas por cultivo estandarizado.

**Acceptance Criteria**
- Selector de cultivo (dropdown alimentado desde `CultivoEntity`) en la sección de Reportes.
- Gráfico de líneas que muestre el rendimiento (Tn/Ha) de ese cultivo a lo largo de las campañas históricas.
- Eje X: Campañas (por nombre o año). Eje Y: Rendimiento (Tn/Ha).
- Opcionalmente: poder alternar entre Tn/Ha, Costo/Ha, e Ingresos/Ha.
- Si solo hay una campaña del cultivo, mostrar un punto con su valor.
- Si no hay campañas del cultivo seleccionado, mostrar mensaje "Sin datos históricos".

**Sub-issues / Tareas Técnicas**
- En `ReportesViewModel`: Crear `ObtenerEvolucionCultivoUseCase` que agrupe campañas por `cultivoId`.
- Exponer `evolucionCultivo: StateFlow<List<PuntoCultivo>>` con data class `PuntoCultivo(campaniaNombre: String, rendimiento: Double, anio: Int)`.
- Agregar selector de cultivo (dropdown alimentado por `ObtenerCultivosUseCase`).
- Implementar gráfico de líneas usando `Canvas` con `drawLine()` / `Path`.
- Agregar puntos interactivos (al tocar un punto, mostrar el valor).

---

## [#353] Issue 353: Filtros Avanzados en Reportes (Rango de Fechas + Selector de Campañas + Datos Financieros)

**Severidad:** 🔵 Feature Nueva
**Módulo:** Reportes
**Archivos afectados:**
- `presentation/ui/screen/reportes/ReportesRendimientoScreen.kt`
- `presentation/viewmodel/reportes/ReportesViewModel.kt`
- `presentation/ui/components/SelectorRangoFechas.kt` (reutilizable del Issue 345)

**Descripción**
La pestaña de Reportes necesita un sistema de filtros avanzados que permita al usuario personalizar el análisis. Esta es la pantalla destino del "Ver detalle →" del Dashboard (Issue 346). Los filtros permiten ver datos financieros (capital invertido, ingresos, balance) con mayor granularidad que el vistazo del Dashboard.

**Acceptance Criteria**

### Filtro de Campañas
- Selector que permita elegir:
  - "Todas las campañas activas" (por defecto).
  - Una campaña individual.
  - Múltiples campañas seleccionadas (multi-select con checkboxes, si no es muy complejo).

### Filtro de Tiempo
- Reutilizar el componente `SelectorRangoFechas` del Issue 345 (`DateRangePicker` de Material 3).
- Opciones rápidas de acceso directo:
  - "Este mes"
  - "Último mes"
  - "Este año"
  - "Lo que va del año"
  - "Personalizado" (abre el calendario de rango).
- Al seleccionar un rango, todos los datos de la pestaña se filtran por ese período.

### Datos Financieros en Reportes
- Sección de **Resumen Financiero** con las mismas 3 métricas del Dashboard pero filtradas:
  - Capital Invertido ($), Ingresos Brutos ($), Balance ($).
- Los datos se recalculan al cambiar campaña o rango de fechas.
- Estos datos complementan (no reemplazan) los gráficos existentes de insumos, cosechas y comparación.

**Sub-issues / Tareas Técnicas**
- Reutilizar `SelectorRangoFechas.kt` creado en el Issue 345.
- En `ReportesViewModel`: Agregar estados `filtroCampanias: StateFlow<List<Int>>` (IDs) y `filtroFechas: StateFlow<Pair<Long, Long>?>`.
- Crear opciones rápidas de tiempo como `enum class FiltroTiempo { ESTE_MES, ULTIMO_MES, ESTE_ANIO, LO_QUE_VA, PERSONALIZADO }`.
- Implementar selector de campaña multi-select (lista de checkboxes en un `DropdownMenu` o `BottomSheet`).
- Agregar sección de resumen financiero en `ReportesRendimientoScreen.kt`.
- Conectar todos los gráficos y métricas existentes con los filtros aplicados.
- Agregar chip bar o indicadores de filtros activos con opción "Limpiar".

---
---

# 📋 NIVEL L6 — DEUDA TÉCNICA (Pendientes de Iteración 2)

Issues identificados durante la iteración 2 que quedaron sin resolverse.

---

## [#354] Issue 354: InsumoVinculacionViewModel sin método sincronizarInsumos()

**Severidad:** ⚪ UX / Deuda Técnica
**Módulo:** Insumos / Detalle Campaña / Tabs
**Archivo afectado:** `presentation/viewmodel/insumo/InsumoVinculacionViewModel.kt`

**Descripción**
`InsumoVinculacionViewModel` tiene el mismo patrón de bug que `TareaViewModel`: lee `campaniaId` desde `SavedStateHandle` y no expone un método para sincronizarlo externamente. La key dinámica `"tab_insumos_$campaniaId"` funciona como solución, pero falta `LaunchedEffect` con `sincronizarInsumos()` como segunda línea de defensa.

**Acceptance Criteria**
- Agregar `fun sincronizarInsumos(id: Int)` en `InsumoVinculacionViewModel`.
- Agregar `LaunchedEffect(campaniaId) { vm.sincronizarInsumos(campaniaId) }` en `TabInsumos`.
- Crear `InsumoVinculacionViewModelTest` con casos Given-When-Then.

**Origen:** Detectado durante fix del Issue [#292] — Iteración 2

---

## [#355] Issue 355: Exportación CSV/PDF sin identificación de campaña en el encabezado

**Severidad:** 🔵 Mejora de Reportes
**Módulo:** Reportes / Exportación
**Archivo afectado:** `core/utils/ReportExporter.kt`

**Descripción**
El título del PDF sigue siendo "Reporte de Gastos por Insumo" sin mencionar el nombre de la campaña exportada. Si se exportan múltiples campañas, los archivos son indistinguibles. Además, si se exporta sin campaña seleccionada, el reporte se genera vacío sin advertencia.

**Acceptance Criteria**
- Agregar nombre de la campaña como subtítulo en el PDF.
- Propagar el nombre como parámetro a `exportToPdf()` y `exportToCsv()`.
- Si `exportableData` está vacío, mostrar `Snackbar`: "Seleccioná una campaña antes de exportar".

**Origen:** Detectado durante fix del Issue [#299] — Iteración 2

---

## [#356] Issue 356: Comparador permite seleccionar la misma campaña para A y B

**Severidad:** ⚪ UX / Deuda Técnica
**Módulo:** Reportes / Comparador
**Archivo afectado:** `presentation/ui/screen/reportes/ReportesRendimientoScreen.kt`, `presentation/viewmodel/reportes/ReportesViewModel.kt`

**Descripción**
El usuario puede seleccionar la misma campaña en dropdown A y B, obteniendo una comparación sin valor informativo.

**Acceptance Criteria**
- El dropdown B debe excluir la campaña seleccionada en A (y viceversa).
- **O bien:** Mostrar `Card` de advertencia "Las campañas seleccionadas son iguales" si `campaniaA.id == campaniaB.id`.
- Agregar test VM-R12 al plan de pruebas.

**Origen:** Detectado durante fix del Issue [#299] / auditoría del Issue [#302] — Iteración 2

---

## [#357] Issue 357: ReportExporter no soporta PDF de múltiples páginas

**Severidad:** 🔵 Mejora de Reportes
**Módulo:** Reportes / Exportación
**Archivo afectado:** `core/utils/ReportExporter.kt`

**Descripción**
El PDF tiene una sola página fija (A4). Si la lista supera ~22 ítems, el contenido se trunca silenciosamente.

**Acceptance Criteria**
- Implementar paginación: al superar `yPosition > 800f`, crear nueva página.
- Repetir encabezado de columnas en cada nueva página.
- Total Final solo en la última página.

**Origen:** Detectado durante fix del Issue [#299] — Iteración 2

---

## [#358] Issue 358: DoubleBarIndicator no tiene tests unitarios

**Severidad:** ⚪ Cobertura / Deuda Técnica
**Módulo:** Reportes / Comparador
**Archivo afectado:** `presentation/ui/screen/reportes/ReportesRendimientoScreen.kt`

**Acceptance Criteria**
- Crear prueba instrumentada que valide que `DoubleBarIndicator` no crashea con `maxA = 0f` o `maxB = 0f`.
- Verificar que las barras tengan la proporción correcta.

**Origen:** Auditoría del Issue [#302] — Iteración 2

---

## [#359] Issue 359: Tests VM-R8 y VM-R9 (guardia de exportación) sin implementación

**Severidad:** ⚪ Cobertura / Deuda Técnica
**Módulo:** Reportes / Exportación
**Archivo afectado:** `test/.../ReportesViewModelTest.kt`

**Acceptance Criteria**
- Implementar test: `exportarReporteCsv emite error si no hay campania seleccionada()`.
- Implementar test: `exportarReportePdf emite error si no hay campania seleccionada()`.
- Verificar que `exportStatus` emite el mensaje correcto.

**Origen:** Auditoría del Issue [#300] — Iteración 2

---

## [#360] Issue 360: UX Inconsistente de Validación entre Formulario de Insumos y Campañas

**Severidad:** ⚪ UX / Deuda Técnica
**Módulo:** Presentation / Formularios

**Descripción**
El formulario de insumos valida en tiempo real. El formulario de campañas valida solo al presionar "Guardar". Debería estandarizarse.

**Acceptance Criteria**
- Definir un estándar de UX para validación.
- Aplicar el mismo patrón en todos los formularios.

**Origen:** Detectado en Iteración 2

---
---

## 🗓️ Orden de Ejecución Sugerido

### Fase 1 — Bugs Críticos y Funcionales (Sprint 1)
1. Issue 334: No se añaden insumos (**bloqueante**)
2. Issue 335: Botón edición cosechas (**bloqueante**)
3. Issue 336: Validación cosecha campo incorrecto
4. Issue 337: Edición foto observaciones

### Fase 2 — Fix UI/UX (Sprint 1-2)
5. Issue 338: Teclado bloquea scroll + bloque blanco
6. Issue 339: Header columnas cortado
7. Issue 340: Etiquetas gráfico desacomodadas

### Fase 3 — Features Core + Refactors de DB (Sprint 2-3)
8. [x] Issue 341: Persistencia de sesión
9. [x] Issue 349: Refactor DB hectáreas → campaña (**bloqueante para Issues 350, 352**)
10. Issue 351: ABM de Cultivos (**bloqueante para Issue 352**)
11. [x] Issue 342: Color y borrado campañas inactivas
12. [x] Issue 345: Rediseño pantalla de tareas (crea `SelectorRangoFechas` reutilizable, **bloqueante para Issue 353**)

### Fase 4 — Reportes Avanzados (Sprint 3-4)
13. [x] Issue 343: Exportación con cosechas
14. [x] Issue 344: Referencias y valores absolutos insumos
15. [x] Issue 348: Top 3 insumos mayor gasto
16. Issue 350: Costo por Hectárea ($/Ha)
17. Issue 352: Evolución histórica por cultivo (requiere Issues 349 + 351)

### Fase 5 — Dashboard Financiero + Filtros Reportes (Sprint 4-5)
18. Issue 353: Filtros avanzados en Reportes (requiere `SelectorRangoFechas` del Issue 345)
19. [x] Issue 346: Resumen financiero Dashboard (vistazo mensual + link a Issue 353)
20. [x] Issue 347: Tasa cumplimiento tareas (donut chart)

### Fase 6 — Deuda Técnica (Continuo)
21-27. Issues de deuda técnica (priorizar según impacto)

---

## 📊 Grafo de Dependencias

```
Issue 349 (Hectáreas en Campaña) ──→ Issue 350 ($/Ha)
         │                          
         └──→ Issue 352 (Evolución Histórica) ←── Issue 351 (ABM Cultivos)
         
Issue 345 (Rediseño Tareas) ──→ Issue 353 (Filtros Reportes) ←── Issue 346 (Dashboard)
         └── crea SelectorRangoFechas reutilizable
```

---

## 🛠️ Reglas de Desarrollo

1. **Ramas:** Cada issue se trabaja en su propia rama (`feature/`, `fix/`, `refactor/`).
2. **Commits:** Convención de Conventional Commits (ej: `fix(cosechas): corregir binding de errores`).
3. **PRs:** Cada rama se integra vía Pull Request a `develop`.
4. **Tests:** Cada cambio en UseCase o ViewModel requiere test unitario.
5. **Documentación:** Actualizar `CHANGELOG.md` tras cada issue completado.
6. **Dependencias:** Respetar el grafo de dependencias. Issues 349 y 351 son bloqueantes.

