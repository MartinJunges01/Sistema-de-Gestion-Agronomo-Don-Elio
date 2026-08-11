# Roadmap: Iteración 2 - Estabilización y Mejoras

Basado en el documento de `bugs_identificados.md`, el siguiente es el listado de problemas y mejoras a implementar durante la Iteración 2, organizados por nivel de severidad.

## 🔴 NIVEL L1 — CRASHES Y ERRORES CRÍTICOS
- [x] **[#283] Issue 6:** Crash al Abrir la Cámara en Observaciones (Permiso no Solicitado)
- [x] **[#284] Issue 7:** Crash por Foreign Key al Registrar Cosecha (campaniaId = -1)

## 🟠 NIVEL L2 — BUGS FUNCIONALES
- [x] **[#285] Issue 1:** Tareas del Dashboard — Interacción, Filtrado y Tratamiento Visual
- [ ] **[#286] Issue 2:** Tareas Nuevas No Aparecen en la Pestaña Tareas (Desincronización de Timestamps)
- [x] **[#287] Issue 3:** Mensaje de Saludo No Funciona Correctamente
- [ ] **[#288] Issue 4:** Visualización Genérica de Insumos Vinculados tras Soft-Delete en Catálogo
- [x] **[#289] Issue 8:** Catálogo de Insumos — No Guarda Nuevos Insumos / Validación de Formulario Faltante
- [x] **[#290] Issue 9:** Campañas Permiten Fechas de Inicio en el Pasado
- [x] **[#291] Issue 10:** Campo "Hora" en Nueva Tarea Acepta Cualquier Carácter
- [x] **[#292] Issue 11:** Pestaña Tareas en Detalle de Campaña No Se Actualiza al Cambiar de Campaña
- [x] **[#293] Issue 12:** Formulario de Cosechas — Validación y Mensajes de Error Faltantes

## 🟢 NIVEL L3 — NUEVAS FEATURES (MEJORAS)
- [x] **[#294] Issue 13:** Incorporar Edición y Eliminación de Observaciones
- [ ] **[#295] Issue 14:** Incorporar Edición y Eliminación de Cosechas
- [ ] **[#296] Issue 15:** Separar Campañas Activas e Inactivas (Historial)
- [ ] **[#297] Issue 16:** Navegación entre Detalles de Campañas (Sin Retroceder)
- [x] **[#298] Issue 17: Refactor Unidad Unica**

## 🔵 NIVEL L4 — MEJORAS DE REPORTES
- [x] **[#299] Issue 5:** Datos Mockeados Residuales en Dashboard y Reportes
- [x] **[#300] Issue 18:** Reportes — Mejorar Reporte de Insumos con Selector de Campaña
- [x] **[#301] Issue 19:** Reportes — Gráfico de Desglose de Cosechas (Almacenada vs Vendida)
- [x] **[#302] Issue 20:** Reportes — Implementar Comparación Real entre Campañas

## ⚪ NIVEL L5 — CALIDAD Y UX
- [ ] **[#303] Issue 21:** Bloquear Modo Oscuro (Forzar Tema Claro)
- [ ] **[#304] Issue 22:** La Pantalla No Se Desplaza al Escribir (Teclado Cubre los Campos)
- [ ] **Issue 23:** Datos Mock del Dashboard — Clima y Salud de Lotes
#

# Bugs Conocidos e Identificados (Iteración Actual)

Este documento registra los bugs y mejoras identificados en el sistema, organizados por severidad.

Cada Issue está redactado como especificación lista para ser incorporada al backlog de desarrollo.

>
> \*\*Fuente:\*\* Sesión de pruebas manuales sobre APK Debug — 2026-06-09.
>

---

---

# 🔴 NIVEL L1 — CRASHES Y ERRORES CRÍTICOS

Estos issues provocan cierres inesperados de la aplicación o corrupción de datos. Deben resolverse antes de cualquier release.

---

## Issue 6: Crash al Abrir la Cámara en Observaciones (Permiso no Solicitado)

\*\*Severidad:\*\* 🔴 CRASH

\*\*Módulo:\*\* Observaciones / Hardware

\*\*Archivo afectado:\*\* \`presentation/ui/screen/observacion/ObservacionesScreen.kt\` (línea ~148)

\*\*Descripción\*\*

Al presionar el botón "Tomar Foto" en la pantalla de Observaciones, la aplicación crashea con un \`SecurityException\`. La causa raíz es que el código lanza directamente \`cameraLauncher.launch(uri)\` \*\*sin solicitar el permiso \`CAMERA\` en tiempo de ejecución\*\*. Aunque el permiso \`android.permission.CAMERA\` está declarado en el \`AndroidManifest.xml\`, a partir de Android 6.0 (API 23) este es un permiso "peligroso" que requiere aprobación explícita del usuario en runtime.

La galería (\`GetContent\`) funciona correctamente porque no requiere permisos peligrosos.

\*\*Causa Raíz (Código)\*\*

\`\`\`kotlin

// ObservacionesScreen.kt — línea 148

// Se lanza la cámara SIN verificar/solicitar permiso:

cameraLauncher.launch(it) // ← SecurityException si CAMERA no fue concedido

\`\`\`

No existe ningún \`rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission())\` ni verificación con \`ContextCompat.checkSelfPermission()\` en el archivo.

\*\*Acceptance Criteria\*\*

- Antes de lanzar la cámara, solicitar el permiso \`Manifest.permission.CAMERA\` usando \`ActivityResultContracts.RequestPermission()\`.
- Si el permiso es denegado, mostrar un \`Snackbar\` informativo: "Se necesita permiso de cámara para tomar fotos".
- Si el permiso es concedido, proceder con \`cameraLauncher.launch(uri)\`.
- No debe crashear bajo ningún escenario de permiso.

\*\*Sub-issues / Tareas Técnicas\*\*

- Crear un \`permissionLauncher\` con \`ActivityResultContracts.RequestPermission()\` en \`ObservacionesScreen.kt\`.
- Modificar el \`onClick\` del botón "Tomar Foto" para solicitar permiso primero; si es concedido, crear la URI temporal y lanzar \`cameraLauncher\`.
- Agregar un estado \`permisoCamaraDenegado\` para mostrar feedback visual (Snackbar) si el usuario rechaza el permiso.

---

## Issue 7: Crash por Foreign Key al Registrar Cosecha (campaniaId = -1)

\*\*Severidad:\*\* 🔴 CRASH

\*\*Módulo:\*\* Cosechas

\*\*Archivos afectados:\*\*

- \`presentation/ui/screens/screens.kt\` (línea ~59)
- \`presentation/viewmodel/cosecha/FormularioCosechaViewModel.kt\` (línea ~37)
- \`presentation/navigation/NavRoutes.kt\`

\*\*Descripción\*\*

Al presionar el botón flotante (FAB) estando en la pantalla de Cosechas para agregar una nueva cosecha, la aplicación crashea con el error:

\`\`\`

FOREIGN KEY constraint failed (code 787) SQLITE_CONSTRAINT_FOREIGNKEY

\`\`\`

La causa raíz es una \*\*cadena de errores en la navegación\*\*: El FAB en \`screens.kt\` navega a \`NavRoute.FormularioCosecha.createRoute()\` \*\*sin pasar \`campaniaId\`\*\*. Esto genera la ruta \`"formulario_cosecha"\` sin query param, lo cual hace que \`SavedStateHandle\` no contenga \`"campaniaId"\`. El \`FormularioCosechaViewModel\` lee\`savedStateHandle.get<Int>("campaniaId") ?: -1\`, obteniendo -1. Al guardar la cosecha, se intenta insertar un registro con \`id_campania = -1\` en la tabla \`cosechas\`, lo cual viola la FK constraint ya que no existe ninguna campaña con \`id = -1\`.

\*\*Causa Raíz (Código)\*\*

\`\`\`kotlin

// screens.kt — FAB onClick (línea ~59):

NavRoute.Cosechas.route -> navController.navigate(

NavRoute.FormularioCosecha.createRoute() // ← SIN campaniaId

)

// NavRoutes.kt — createRoute sin args genera ruta sin param:

fun createRoute(campaniaId: Int? = null): String =

if (campaniaId != null) "formulario_cosecha?campaniaId=$campaniaId"

else "formulario_cosecha" // ← Sin campaniaId en la URL

// FormularioCosechaViewModel.kt — fallback a -1:

val campaniaId = savedStateHandle.get<Int>("campaniaId") ?: -1 // ← -1 = inválido

\`\`\`

\*\*Acceptance Criteria\*\*

- El formulario de cosechas NUNCA debe intentar guardar con \`campaniaId = -1\`.
- Si no se recibe un \`campaniaId\` válido, el formulario debe mostrar un \`SelectorCampania\` obligatorio (igual que \`NuevaTareaScreen\`) para que el usuario elija la campaña destino.
- Alternativamente, si se accede desde el FAB, forzar la selección de campaña activa antes de navegar al formulario.
- Validar \`campaniaId > 0\` en \`FormularioCosechaViewModel.guardar()\` antes de intentar la inserción.

\*\*Sub-issues / Tareas Técnicas\*\*

- En \`FormularioCosechaViewModel.guardar()\`: Agregar validación \`if (campaniaId <= 0) { emitir error "Seleccione una campaña" y retornar }\`.
- Agregar un \`SelectorCampania\` en \`FormularioCosechaScreen\` que se muestre cuando \`campaniaId\` no sea válido, permitiendo al usuario seleccionarlo manualmente.
- (Alternativa) En \`screens.kt\`, cambiar la lógica del FAB en ruta Cosechas para que pase el \`campaniaId\` de la campaña seleccionada actualmente.

---

---

# 🟠 NIVEL L2 — BUGS FUNCIONALES

Estos issues no crashean la app pero producen comportamiento incorrecto o datos erróneos.

---

## Issue 1: Tareas del Dashboard — Interacción, Filtrado y Tratamiento Visual (Actualizado)

\*\*Severidad:\*\* 🟠 Bug Funcional + Mejora UX

\*\*Módulo:\*\* Dashboard / Home

\*\*Archivos afectados:\*\*

- \`presentation/ui/screen/home/DashboardOperacionesScreen.kt\`
- \`presentation/viewmodel/home/HomeViewModel.kt\`
- \`data/local/dao/TareaDao.kt\`
- \`domain/use_case/ObtenerTareasPendientesUseCase.kt\`

\*\*Descripción\*\*

La sección "Tareas Próximas" del Dashboard tiene 3 problemas acumulados:

1. \*\*Tareas no clickeables:\*\* Los \`Card\` de cada tarea no tienen \`onClick\` configurado, por lo que el usuario no puede navegar al detalle de la campaña desde aquí.
2. \*\*Tareas vencidas antiguas ocupan el cupo:\*\* La query \`getTareasPendientesGlobales\` usa \`WHERE confirmar = 0 ORDER BY fecha ASC LIMIT :limite\`. Esto trae las tareas más viejas primero (ej: de Enero 2026 del \`DataSeeder\`), ocultando las tareas reales recientes que el usuario acaba de crear.
3. \*\*Sin tratamiento visual de vencimiento:\*\* Todas las tareas se renderizan con el mismo estilo verde, sin distinguir si están vencidas hace poco, vencidas hace mucho, o próximas a vencer.
4. \*\*Sin botón "Ver más":\*\* El límite es de 3 tareas (hardcoded en \`HomeViewModel\`) y no hay forma de ver el resto.


\*\*Causa Raíz (Código)\*\*

\`\`\`kotlin

// TareaDao.kt — Query actual:

@Query("SELECT \* FROM tareas WHERE confirmar = 0 ORDER BY fecha ASC, hora ASC LIMIT :limite")

fun getTareasPendientesGlobales(limite: Int): Flow<List<TareaEntity>>

// ↑ Trae TODAS las pendientes sin importar si vencieron hace meses

// HomeViewModel.kt — Límite fijo:

val tareasPendientes = obtenerTareasPendientesUseCase(limite = 3) // ← Sin "ver más"

\`\`\`

\*\*Acceptance Criteria\*\*

- Al tocar una tarea, navegar al \`DetalleCampania\` de la campaña correspondiente (con ripple effect).
- Las tareas \*\*vencidas hace más de 7 días\*\* NO deben aparecer en el Dashboard (están demasiado atrasadas para ser "próximas").
- Las tareas \*\*vencidas hace 1-7 días\*\* deben aparecer con un tinte rojo suave (indicador de urgencia/atraso reciente).
- Las tareas \*\*futuras\*\* se muestran con el estilo verde normal actual.
- Agregar un botón "Ver más" debajo de la lista que navegue a la pantalla completa de Tareas.

\*\*Sub-issues / Tareas Técnicas\*\*

- En \`DashboardOperacionesScreen.kt\`: Agregar \`.clickable { onGoToDetalle(tarea.idCampania) }\` al Card de cada tarea.
- En \`TareaDao.kt\`: Modificar la query a: \`WHERE confirmar = 0 AND fecha >= :fechaLimite ORDER BY fecha ASC, hora ASC LIMIT :limite\` donde \`fechaLimite\` = hoy - 7 días.
- En \`ObtenerTareasPendientesUseCase\`: Aceptar \`fechaLimite: Long\` como parámetro y pasarlo al repositorio.
- En \`DashboardOperacionesScreen.kt\`: Agregar lógica de color condicional al Card — si \`tarea.fecha < hoy\`, usar fondo \`Red50\` y tint \`Red600\`; si no, mantener \`AgriVerde\`.
- En \`DashboardOperacionesScreen.kt\`: Agregar botón \`TextButton("Ver todas →")\` debajo de la lista que navegue a la ruta \`NavRoute.Tareas\`.

---

## Issue 2: Tareas Nuevas No Aparecen en la Pestaña Tareas (Desincronización de Timestamps)

\*\*Severidad:\*\* 🟠 Bug Funcional

\*\*Módulo:\*\* Tareas / Calendario

\*\*Archivos afectados:\*\* \`domain/use_case/CrearTareaUseCase.kt\`,\`domain/use_case/EditarTareaUseCase.kt\`

\*\*Descripción\*\*

Las tareas creadas por el usuario no son visibles en la pestaña "Tareas" a pesar de seleccionar el día y la campaña correctos. El calendario y las queries buscan coincidencias exactas truncadas a la medianoche (\`2026-06-01 00:00:00.000\`). Sin embargo, \`CrearTareaUseCase\` guarda el timestamp crudo que incluye horas/minutos/milisegundos. Al hacer \`WHERE fecha = :fecha\`, nunca hay match.

\*\*Acceptance Criteria\*\*

- Al crear una tarea, debe aparecer inmediatamente en la pestaña "Tareas" al seleccionar ese día en el \`CalendarioSemanal\`.
- Los timestamps de fecha deben normalizarse a las \`00:00:00.000\` (medianoche) antes de persistir.

\*\*Sub-issues / Tareas Técnicas\*\*

- Modificar \`CrearTareaUseCase.kt\`: Normalizar \`fecha\` a medianoche antes de instanciar \`Tarea\`.
- Modificar \`EditarTareaUseCase.kt\`: Aplicar la misma normalización al recibir la \`Tarea\`.

---

## Issue 3: Mensaje de Saludo No Funciona Correctamente

\*\*Severidad:\*\* 🟠 Bug Funcional

\*\*Módulo:\*\* Login / Dashboard / Session

\*\*Archivos afectados:\*\*

- \`presentation/viewmodel/login/LoginViewModel.kt\`
- \`core/SessionManager.kt\`
- \`presentation/ui/components/HeaderSectionAgriCore.kt\`

\*\*Descripción\*\*

El encabezado del Dashboard muestra "Hola, Invitado" incluso después de iniciar sesión con un usuario registrado. La causa raíz es que el \`LoginViewModel\` \*\*nunca llama a \`sessionManager.saveUserName()\`\*\* tras un login exitoso. El flujo actual:

1. \`LoginViewModel.login()\` llama a \`loginUseCase()\` → recibe un objeto \`Usuario\` con el nombre.
2. Si el login es exitoso, actualiza \`loginExitoso = true\` pero \*\*descarta el objeto \`Usuario\` sin guardar el nombre\*\*.
3. \`HomeViewModel\` lee \`sessionManager.userName\` que nunca fue actualizado → siempre devuelve "Invitado" (valor por defecto del DataStore).


\*\*Causa Raíz (Código)\*\*

\`\`\`kotlin

// LoginViewModel.kt — login exitoso:

val usuario = loginUseCase(nombreUsuario, contrasena)

if (usuario != null) {

_state.update { it.copy(isLoading = false, loginExitoso = true) }

// ← FALTA: sessionManager.saveUserName(usuario.nombre)

}

// SessionManager.kt — valor por defecto:

override val userName: Flow<String> = context.dataStore.data

.map { preferences -> preferences[USER_NAME_KEY] ?: "Invitado" } // ← Siempre "Invitado"

\`\`\`

Búsqueda en todo el paquete \`presentation/\`: \`saveUserName\` no es invocado en ningún lado.

\*\*Acceptance Criteria\*\*

- Tras un login exitoso, el encabezado del Dashboard debe mostrar "Hola, [nombre del usuario]".
- Al usar el botón "Invitado", debe mostrar "Hola, Invitado".
- Al cerrar sesión (\`logout()\`), el nombre debe resetearse a "Invitado".

\*\*Sub-issues / Tareas Técnicas\*\*

- Inyectar \`SessionManager\` en \`LoginViewModel\`.
- En \`LoginViewModel.login()\`: Tras verificar \`usuario != null\`, llamar\`sessionManager.saveUserName(usuario.nombre)\` antes de setear \`loginExitoso = true\`.
- En el flujo de "Invitado" (si existe un botón de acceso directo): Llamar\`sessionManager.saveUserName("Invitado")\`.
- Verificar que \`logout()\` (si se implementa) llame \`sessionManager.logout()\` para limpiar el DataStore.

---

## Issue 4: Visualización Genérica de Insumos Vinculados tras Soft-Delete en Catálogo

\*\*Severidad:\*\* 🟠 Bug Funcional

\*\*Módulo:\*\* Insumos / Vinculación

\*\*Archivos afectados:\*\* DAO de insumos vinculados, \`InsumosScreen\`,\`InsumoVinculacionViewModel\`

\*\*Descripción\*\*

Al borrar un Insumo del Catálogo (Soft-Delete con \`activo = false\`), si ese insumo ya estaba vinculado a una Campaña, la tarjeta muestra un nombre genérico como "Insumo #6" en vez del nombre real. La consulta que une la tabla intermedia con el catálogo ignora los insumos inactivos, o la UI recibe null y aplica un fallback.

\*\*Acceptance Criteria\*\*

- Al visualizar insumos vinculados a una campaña, si un insumo fue eliminado del catálogo, la tarjeta debe seguir mostrando el nombre original, posiblemente con un indicador "(Eliminado del catálogo)".
- La información histórica de la campaña no debe perder integridad visual.

\*\*Sub-issues / Tareas Técnicas\*\*

- Revisar la consulta SQL del DAO para que el \`JOIN\` incluya insumos independientemente de su estado \`activo\`.
- O revisar la UI/ViewModel para corregir el fallback del nombre.

---

## Issue 8: Catálogo de Insumos — No Guarda Nuevos Insumos / Validación de Formulario Faltante

\*\*Severidad:\*\* 🟠 Bug Funcional

\*\*Módulo:\*\* Insumos / Catálogo

\*\*Archivos afectados:\*\*

- \`presentation/ui/screen/insumo/FormularioInsumoScreen.kt\`
- \`presentation/viewmodel/insumo/FormularioInsumoViewModel.kt\`
- \`presentation/ui/screen/insumo/CatalogoInsumosScreen.kt\` (diálogo de edición)
- \`presentation/viewmodel/insumo/InsumoCatalogoViewModel.kt\`

\*\*Descripción\*\*

El formulario de creación de insumos tiene deficiencias de validación que permiten guardar registros incompletos o que fallan silenciosamente:

1. \*\*Validación incompleta:\*\* Solo se valida que \`nombre\` no esté vacío. Los campos \`categoría\` y \`unidad\` \*\*no tienen validación alguna\*\* — pueden guardarse vacíos sin error.
2. \*\*Botón "Guardar" siempre habilitado:\*\* La condición del botón es solo \`!state.isLoading\` (línea 105 de \`FormularioInsumoScreen\`), sin verificar que los campos obligatorios estén completos.
3. \*\*Sin campos de error en el estado:\*\* \`FormularioInsumoState\` solo tiene \`errorNombre\`. No existen \`errorCategoria\` ni \`errorUnidad\`.
4. \*\*Diálogo de edición débil:\*\* El \`DialogEditarInsumo\` en \`CatalogoInsumosScreen\` solo valida \`nombre.isNotBlank()\` para habilitar el botón Guardar. Categoría y Unidad tampoco se validan.
5. \*\*Errores del ViewModel no mostrados:\*\* \`InsumoCatalogoViewModel\` tiene un estado \`_error\` pero \`CatalogoInsumosScreen\` nunca lo observa — los errores se tragan silenciosamente.


\*\*Causa Raíz (Código)\*\*

\`\`\`kotlin

// FormularioInsumoViewModel.kt — guardar():

fun guardar() {

val current = _state.value

if (current.nombre.isBlank()) {

_state.update { it.copy(errorNombre = "El nombre es obligatorio") }

return

}

// ← FALTA validación de categoría y unidad

// Procede a guardar con categoría/unidad potencialmente vacíos

}

// FormularioInsumoScreen.kt — botón:

Button(

onClick = { viewModel.guardar() },

enabled = !state.isLoading // ← No chequea campos vacíos

)

\`\`\`

\*\*Acceptance Criteria\*\*
```

**Acceptance Criteria**

- Los campos `nombre`, `categoría` y `unidad` deben ser obligatorios.
- El formulario debe mostrar mensajes de error individuales (`supportingText`) debajo de cada campo vacío al intentar guardar.
- El botón "Guardar" debe deshabilitarse visualmente si hay errores activos.
- El diálogo de edición inline debe aplicar las mismas validaciones.
- Los errores del `InsumoCatalogoViewModel` deben mostrarse al usuario (Snackbar).
- El flujo de inserción debe llegar hasta SQLite mediante `CrearInsumoUseCase` e `InsumoDao`, y la UI debe observar la lista de insumos mediante un `Flow` para que se refresque automáticamente.

**Sub-issues / Tareas Técnicas**

- Agregar `errorCategoria: String?` y `errorUnidad: String?` a `FormularioInsumoState`.
- En `FormularioInsumoViewModel.guardar()`: Validar `categoría.isBlank()` y `unidad.isBlank()`, seteando los errores correspondientes.
- En `FormularioInsumoScreen.kt`: Agregar `isError` y `supportingText` a los campos `Categoría` y `Unidad`.
- Cambiar `enabled` del botón Guardar a: `!state.isLoading && state.errorNombre == null && state.errorCategoria == null && state.errorUnidad == null`.
- En `CatalogoInsumosScreen.kt` / `DialogEditarInsumo`: Agregar validaciones equivalentes para categoría y unidad.
- En `CatalogoInsumosScreen.kt`: Observar `viewModel.error` y mostrarlo en un `Snackbar`.

---

## Issue 9: Campañas Permiten Fechas de Inicio en el Pasado

**Severidad:** 🟠 Bug Funcional

**Módulo:** Campañas / Formulario

**Archivos afectados:**

- `presentation/ui/screen/campania/FormularioCampaniaScreen.kt`
- `presentation/viewmodel/campania/CampaniaFormViewModel.kt`
- `domain/use_case/CrearCampaniaUseCase.kt`

**Descripción**

El `DatePicker` del formulario de campaña permite seleccionar cualquier fecha, incluyendo fechas pasadas. La única validación existente es `fechaInicio <= 0` (que solo detectaría un timestamp nulo/inválido, nunca una fecha pasada). El `CrearCampaniaUseCase` tampoco valida la fecha.

**Causa Raíz (Código)**

```kotlin

// FormularioCampaniaScreen.kt — DatePicker sin restricción:

rememberDatePickerState(initialSelectedDateMillis = state.fechaInicio)

// ← Sin parámetro 'selectableDates' para restringir fechas pasadas

// CampaniaFormViewModel.kt — validación insuficiente:

if (current.fechaInicio <= 0) {

_state.update { it.copy(errorFecha = "Seleccione una fecha") }

// ← Solo chequea timestamp inválido, NO chequea pasado

}

```

**Acceptance Criteria**

- El `DatePicker` no debe permitir seleccionar fechas anteriores a hoy.
- Si de alguna forma se llega a `guardar()` con una fecha pasada, el ViewModel debe emitir `errorFecha = "La fecha no puede ser anterior a hoy"`.
- **Excepción en modo edición:** Al editar una campaña existente que ya tiene una fecha pasada (porque empezó hace un mes), se debe permitir mantener esa fecha. La restricción solo aplica a campañas nuevas.

**Sub-issues / Tareas Técnicas**

- En `FormularioCampaniaScreen.kt`: Agregar restricción `selectableDates` al `DatePickerState` que deshabilite fechas anteriores a la medianoche de hoy.
- En `CampaniaFormViewModel.guardar()`: Agregar validación `if (!isEditMode && current.fechaInicio < hoyMedianoche)` → error.
- (Opcional) En `CrearCampaniaUseCase`: Agregar validación de fecha como capa de defensa adicional en la capa de dominio.

---

## Issue 10: Campo "Hora" en Nueva Tarea Acepta Cualquier Carácter

**Severidad:** 🟠 Bug Funcional

**Módulo:** Tareas / Formulario

**Archivos afectados:**

- `presentation/ui/screen/tarea/NuevaTareaScreen.kt`
- `presentation/viewmodel/tarea/NuevaTareaViewModel.kt`

**Descripción**

El campo "Hora" de la pantalla Nueva Tarea es un `OutlinedTextField` genérico sin ningún tipo de restricción. El usuario puede escribir letras, símbolos y cualquier texto arbitrario (ej: "abc", "25:99", "holá"). No hay tipo de teclado numérico, no hay máscara de formato (`HH:mm`), no hay validación en `onHoraChange()`, y no hay campo de error en el estado.

**Causa Raíz (Código)**

```kotlin

// NuevaTareaScreen.kt — campo hora sin restricciones:

OutlinedTextField(

value = state.hora,

onValueChange = viewModel::onHoraChange,

placeholder = { Text("HH:mm") }

// ← Sin keyboardOptions, sin visualTransformation, sin filtro

)

// NuevaTareaViewModel.kt — acepta cualquier string:

fun onHoraChange(value: String) {

_state.update { it.copy(hora = value) } // ← Sin filtrado ni validación

}

// NuevaTareaFormState — sin campo de error para hora:

data class NuevaTareaFormState(

...

val hora: String = "",

// ← FALTA: val errorHora: String? = null

)

```

**Acceptance Criteria**

- **Opción recomendada:** Reemplazar el `OutlinedTextField` por un `TimePickerDialog` nativo de Material 3 (igual que se hizo con `DatePickerDialog` para la fecha). Esto elimina la posibilidad de entrada inválida.
- **Opción alternativa (si se mantiene el TextField):**
- `keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)`
- Filtro de entrada: Solo dígitos y `:`, máximo 5 caracteres.
- Validación en `guardar()`: Verificar formato `HH:mm` con regex `^([01]\d|2[0-3]):[0-5]\d$`.
- Agregar `errorHora: String?` al estado y mostrarlo con `supportingText`.

**Sub-issues / Tareas Técnicas**

- **(Recomendado)** Reemplazar el `OutlinedTextField` de hora por un `TimePickerDialog` de Material 3, similar al `DatePickerDialog` ya implementado para la fecha.
- Agregar `errorHora: String?` a `NuevaTareaFormState`.
- En `NuevaTareaViewModel.guardar()`: Validar que `hora` no esté vacía y tenga formato válido antes de crear la tarea.

---

## Issue 11: Pestaña Tareas en Detalle de Campaña No Se Actualiza al Cambiar de Campaña

**Severidad:** 🟠 Bug Funcional

**Módulo:** Detalle Campaña / Tabs

**Archivo afectado:** `presentation/ui/screen/campania/DetalleCampaniaScreen.kt` (líneas ~175 y ~223)

**Descripción**

Al navegar al detalle de una campaña y luego retroceder y entrar al detalle de otra campaña, las pestañas "Tareas" e "Insumos" siguen mostrando los datos de la primera campaña visitada. Esto ocurre porque el `hiltViewModel()` de estas pestañas usa una key estática sin incluir el `campaniaId`:

```kotlin

// TabTareas — key sin campaniaId:

val viewModel: TareaViewModel = hiltViewModel(key = "tab_tareas")

// ↑ Siempre reutiliza la misma instancia de ViewModel

// TabInsumos — misma situación:

val viewModel: InsumoVinculacionViewModel = hiltViewModel(key = "tab_insumos")

```

En contraste, `TabCosechas` y `TabObservaciones` sí incluyen el `campaniaId` en la key y funcionan correctamente:

```kotlin

val viewModel: CosechaViewModel = hiltViewModel(key = "tab_cosechas_$campaniaId") // ✅

val viewModel: ObservacionViewModel = hiltViewModel(key = "tab_observaciones_$campaniaId") // ✅

```

**Acceptance Criteria**

- Al cambiar de campaña, todas las pestañas deben mostrar los datos de la campaña actual.
- No deben quedar datos cacheados de campañas anteriores.

**Sub-issues / Tareas Técnicas**

- En `DetalleCampaniaScreen.kt`, línea ~175: Cambiar `hiltViewModel(key = "tab_tareas")` por `hiltViewModel(key = "tab_tareas_$campaniaId")`.
- En `DetalleCampaniaScreen.kt`, línea ~223: Cambiar `hiltViewModel(key = "tab_insumos")` por `hiltViewModel(key = "tab_insumos_$campaniaId")`.

---

## Issue 12: Formulario de Cosechas — Validación y Mensajes de Error Faltantes

**Severidad:** 🟠 Bug Funcional

**Módulo:** Cosechas / Formulario

**Archivos afectados:**

- `presentation/ui/screen/cosecha/FormularioCosechaScreen.kt`
- `presentation/viewmodel/cosecha/FormularioCosechaViewModel.kt`

**Descripción**

El formulario de cosechas tiene validación incompleta:

1. **Botón "Guardar" habilitado con campos vacíos:** La condición es `!state.isLoading && state.errorPrecio == null && state.errorCantidad == null`. Cuando los campos están vacíos, los errores son `null` (no se han activado aún), así que el botón está habilitado. El usuario puede presionar "Guardar" con campos vacíos y la función `guardar()` hace un `return` silencioso sin feedback visual.
2. **Sin validación visual de campaña:** Cuando `campaniaId = -1` (ver Issue 7), no hay indicación al usuario de que falta seleccionar campaña.
3. **Sin validación de unidad:** Si `unidad` está vacía, se inserta silenciosamente como "Kg" (fallback hardcoded en el VM).


**Acceptance Criteria**

- Los campos `cantidad` y `unidad` deben mostrar error visual (`isError` + `supportingText`) al intentar guardar con valores vacíos/inválidos.
- El botón "Guardar" debe deshabilitarse si hay errores o campos vacíos obligatorios.
- Si `campaniaId` no es válido, mostrar un mensaje claro: "Debe seleccionar una campaña".

**Sub-issues / Tareas Técnicas**

- En `FormularioCosechaViewModel.guardar()`: Setear `errorCantidad = "La cantidad es obligatoria"` cuando `cantidad.isBlank()` (actualmente solo hace `return`).
- Agregar `errorUnidad: String?` al estado y validar que no esté vacía.
- Agregar `errorCampania: String?` al estado y validar que `campaniaId > 0`.
- En `FormularioCosechaScreen.kt`: Agregar `isError` y `supportingText` a los campos correspondientes.
- Ajustar la condición `enabled` del botón para deshabilitar si `cantidad` o `unidad` están vacías.

---

---

# 🟡 NIVEL L3 — MEJORAS FUNCIONALES (Features Faltantes)

Funcionalidades identificadas durante las pruebas que no están implementadas pero son necesarias para una experiencia completa.

---

## Issue 13: Incorporar Edición y Eliminación de Observaciones

**Severidad:** 🟡 Feature Faltante

**Módulo:** Observaciones

**Archivos afectados:**

- `presentation/ui/screen/observacion/ObservacionesScreen.kt`
- `presentation/viewmodel/observacion/ObservacionViewModel.kt`
- `domain/use_case/` (nuevos Use Cases)
- `domain/repository/ObservacionRepository.kt`
- `data/repository/ObservacionRepositoryImpl.kt`

**Descripción**

Actualmente las observaciones solo pueden crearse y listarse. No existe funcionalidad para editar una observación existente ni para eliminarla. El `ObservacionCard` (líneas 201-222) es completamente de solo lectura, sin botones de acción, swipe-to-delete, ni menú contextual.

**Acceptance Criteria**

- Cada tarjeta de observación debe tener un botón de editar (ícono lápiz) y un botón de eliminar (ícono papelera).
- Al presionar "Editar", abrir un diálogo o formulario pre-rellenado con el texto y la imagen actuales, permitiendo modificarlos y guardar.
- Al presionar "Eliminar", mostrar un diálogo de confirmación: "¿Eliminar esta observación? Esta acción no se puede deshacer."
- Tras confirmar la eliminación, la observación debe desaparecer de la lista reactivamente.

**Sub-issues / Tareas Técnicas**

- Crear `EditarObservacionUseCase` en `domain/use_case/`.
- Crear `EliminarObservacionUseCase` en `domain/use_case/`.
- Agregar método `updateObservacion()` y `deleteObservacion()` en `ObservacionRepository` y su implementación.
- Agregar `@Update` y `@Delete` en el DAO de observaciones (verificar si ya existen).
- En `ObservacionViewModel.kt`: Agregar métodos `editarObservacion()` y `eliminarObservacion()`.
- En `ObservacionesScreen.kt`: Agregar botones de acción al `ObservacionCard` y el diálogo de confirmación para eliminación.
- Agregar diálogo o formulario de edición inline (similar a `DialogEditarInsumo`).

---

## Issue 14: Incorporar Edición y Eliminación de Cosechas

**Severidad:** 🟡 Feature Faltante

**Módulo:** Cosechas

**Archivos afectados:**

- `presentation/ui/screen/cosecha/CosechasScreen.kt`
- `presentation/viewmodel/cosecha/CosechaViewModel.kt`
- `domain/use_case/` (nuevos Use Cases)
- `domain/repository/CosechaRepository.kt`
- `data/repository/CosechaRepositoryImpl.kt`

**Descripción**

Actualmente las cosechas solo pueden crearse y listarse. No existe funcionalidad para editar una cosecha existente ni para eliminarla. El `CosechaCard` (líneas 103-130) es de solo lectura.

**Acceptance Criteria**

- Cada tarjeta de cosecha debe tener opciones de editar y eliminar.
- Al editar, permitir modificar cantidad, unidad, fecha, y tipo (almacenada/venta).
- Al eliminar, mostrar diálogo de confirmación.
- Si la cosecha tiene un registro asociado en `CosechaNoAlmacenada`, la eliminación debe ser en cascada (el FK con `onDelete = CASCADE` ya debería manejarlo, pero verificar).

**Sub-issues / Tareas Técnicas**

- Crear `EditarCosechaUseCase` en `domain/use_case/`.
- Crear `EliminarCosechaUseCase` en `domain/use_case/`.
- Agregar métodos `updateCosecha()` y `deleteCosecha()` en `CosechaRepository` y su implementación.
- En `CosechaViewModel.kt`: Agregar métodos `editarCosecha()` y `eliminarCosecha()`.
- En `CosechasScreen.kt`: Agregar botones de acción al `CosechaCard` y diálogo de confirmación.

---

## Issue 15: Separar Campañas Activas e Inactivas (Historial)

**Severidad:** 🟡 Feature Faltante

**Módulo:** Gestión de Campañas

**Archivos afectados:**

- `presentation/ui/screen/campania/GestionCampaniasScreen.kt`
- `presentation/viewmodel/campania/GestionCampaniasViewModel.kt`
- `data/local/dao/CampaniaDao.kt`

**Descripción**

Actualmente todas las campañas (activas e inactivas) se muestran en una única lista plana en `GestionCampaniasScreen`. El campo `estaActiva` existe y se usa visualmente (ícono verde/gris, badge "Activa"), pero no hay separación estructural ni posibilidad de filtrar.

**Acceptance Criteria**

- La pantalla de gestión debe tener dos secciones claramente diferenciadas: **"Campañas Activas"** (arriba) y **"Historial"** o "Campañas Finalizadas" (abajo, colapsable).
- Alternativamente, implementar un `FilterChip` o `Tab` para alternar entre "Activas" e "Historial".
- Las campañas finalizadas deben mostrarse con estilo atenuado.
- Reemplazar el actual botón de "Eliminar" en el TopAppBar de detalle (que ejecuta un *hard delete*) por un botón "Finalizar Campaña" (*soft delete* cambiando `estaActiva = false`).
- Para las campañas que ya se encuentran en el "Historial", habilitar una opción de "Eliminar definitivamente" (*hard delete* real) en caso de que el usuario necesite borrar una campaña permanentemente.
- En el Dashboard, filtrar estrictamente para aceptar solo campañas donde `estaActiva = 1` (true).

**Sub-issues / Tareas Técnicas**

- En `CampaniaDao.kt`: Agregar queries `getCampaniasActivas()` y `getCampaniasInactivas()` que filtren por `estaActiva`.
- En `GestionCampaniasViewModel.kt`: Exponer dos listas (`activas` e `inactivas`) o una lista filtrada con un toggle.
- En `GestionCampaniasScreen.kt`: Implementar la separación visual (secciones con header o tabs).
- En `DetalleCampaniaScreen.kt`: Agregar botón "Finalizar Campaña" que llame a `EditarCampaniaUseCase` seteando `estaActiva = false`.
- En `HomeViewModel` (Dashboard): Modificar la consulta para que filtre estrictamente `WHERE estaActiva = 1`.

---

## Issue 16: Navegación entre Detalles de Campañas (Sin Retroceder)

**Severidad:** 🟡 Feature Faltante / Mejora UX

**Módulo:** Detalle Campaña / Navegación

**Archivos afectados:**

- `presentation/ui/screen/campania/DetalleCampaniaScreen.kt`
- `presentation/viewmodel/campania/CampaniaDetailViewModel.kt`

**Descripción**

Actualmente, si el usuario está viendo el detalle de la campaña "Girasol" y quiere ver "Maíz", debe presionar el botón de retroceder para volver a la lista, y luego seleccionar la campaña correcta. No existe forma de navegar lateralmente entre campañas desde la pantalla de detalle.

**Acceptance Criteria**

- Implementar un mecanismo de navegación lateral entre campañas, como:
- **Opción A (Recomendada):** Un `ExposedDropdownMenu` en el `TopAppBar` del detalle que liste todas las campañas y permita seleccionar otra sin retroceder.
- **Opción B:** Flechas de navegación ← → en el `TopAppBar` para ir a la campaña anterior/siguiente.
- **Opción C:** Swipe horizontal entre campañas (más complejo, menor prioridad).
- Al cambiar de campaña, todos los tabs deben refrescarse con los datos de la nueva campaña.

**Sub-issues / Tareas Técnicas**

- Decidir el mecanismo de navegación (Opción A recomendada por consistencia con `SelectorCampania` usado en otros módulos).
- En `CampaniaDetailViewModel.kt`: Inyectar `ObtenerCampaniasUseCase` para exponer la lista completa de campañas y un método `cambiarCampania(id)`.
- En `DetalleCampaniaScreen.kt`: Agregar el selector/dropdown en el `TopAppBar` o debajo del encabezado.
- Asegurar que al cambiar de campaña, las keys de los ViewModels de las tabs se actualicen (ver Issue 11).

---

## Issue 17: Agregar Campo "Hectáreas" a la Entidad Cosecha

**Severidad:** 🟡 Feature Faltante

**Módulo:** Cosechas / Datos / Reportes

**Archivos afectados:**

- `data/local/entity/CosechaEntity.kt`
- `domain/model/Cosecha.kt`
- `data/mappers/Mappers.kt` (o equivalente para cosecha)
- `presentation/ui/screen/cosecha/FormularioCosechaScreen.kt`
- `presentation/viewmodel/cosecha/FormularioCosechaViewModel.kt`
- `DonElioDatabase.kt` (incrementar versión)

**Descripción**

Actualmente la entidad `CosechaEntity` registra `cantidad`, `fecha`, `unidad` y `almacen`, pero **no registra la superficie cosechada (en hectáreas)**. Este dato es esencial para calcular el rendimiento por hectárea (`Tn/ha`), que es la métrica principal de productividad agrícola. Se decidió ubicar el campo en la cosecha (no en la campaña) para permitir cosechas parciales donde cada pasada puede cubrir un área diferente.

**Modelo actual de `CosechaEntity`:**

```kotlin

data class CosechaEntity(

val id_cosecha: Int = 0,

val cantidad: Double,   // Producción total

val fecha: Long,

val unidad: String,     // "Kg", "Tn", etc.

val almacen: String,

val id_campania: Int

// ← FALTA: val hectareas: Double

)

```

**Acceptance Criteria**

- Agregar campo `hectareas: Double` a `CosechaEntity` y `Cosecha` (modelo de dominio).
- Eliminar el campo `unidad` de la DB y del modelo de dominio, asumiendo por convención que la cantidad SIEMPRE se expresa en Toneladas (Tn).
- El formulario de cosecha debe incluir un campo numérico "Superficie cosechada (ha)" con validación (> 0).
- El campo "Unidad" en la UI debe quedar fijo en "Tn" (no editable por el usuario) para evitar conversiones.
- El campo Hectáreas es **obligatorio** para poder calcular rendimiento/ha en reportes.
- Los reportes deben poder calcular `rendimiento/ha = cantidad / hectareas` por cada cosecha.
- Incrementar la versión de la base de datos (DB v5) con `fallbackToDestructiveMigration()`.

**Sub-issues / Tareas Técnicas**

- Agregar `val hectareas: Double` a `CosechaEntity` y eliminar `val unidad: String`.
- Agregar `val hectareas: Double` a `Cosecha` (modelo de dominio) y eliminar `val unidad: String`.
- Actualizar mappers `toDomain()` / `toEntity()` para `Cosecha`.
- Incrementar la versión de la DB en `DonElioDatabase.kt` (v4 → v5).
- En `FormularioCosechaScreen.kt`: Agregar `OutlinedTextField` para hectáreas. Modificar la UI de unidad para que sea un texto fijo "Tn".
- En `FormularioCosechaViewModel.kt`: Agregar campo `hectareas` al estado, validar > 0 en `guardar()`. Eliminar cualquier estado de `unidad`.
- Actualizar UseCases (`RegistrarCosechaUseCase`, etc.) para propagar estos cambios.

---

---

# 🔵 NIVEL L4 — MEJORAS DE REPORTES

Mejoras específicas al módulo de Reportes y Estadísticas.

---

## Issue 5: Datos Mockeados Residuales en Dashboard y Reportes (Actualizado)

**Severidad:** 🔵 Mejora Funcional

**Módulo:** Dashboard + Reportes

**Archivos afectados:**

- `presentation/ui/screen/home/DashboardOperacionesScreen.kt`
- `presentation/ui/screen/reportes/ReportesRendimientoScreen.kt`
- `presentation/viewmodel/reportes/ReportesViewModel.kt`

**Descripción**

Persisten datos hardcodeados en dos pantallas:

- **Dashboard:** Tarjetas de "Clima 24°C | Humedad 60%" y "Salud Lotes 90% Óptimo".
- **Reportes:** Lista de campañas del comparador (`listOf("Campaña Soja 2026", ...)`), las 4 CardMetricaComparativa (Rendimiento, Ganancias, Costos, Insumos), y los valores del gráfico Canvas de evolución mensual.

El **único dato real** en Reportes es el `PieChart` de distribución de gastos por insumo.

**Acceptance Criteria**

- Dashboard: Eliminar o reemplazar las tarjetas mock (Clima y Salud) por componentes con datos reales o eliminarlas si no se planea integrar APIs externas.
- Reportes: Todo el módulo debe alimentarse de datos reales de la BD (ver Issues 18, 19 y 20 para el desglose detallado).

---

## Issue 18: Reportes — Mejorar Reporte de Insumos con Selector de Campaña

**Severidad:** 🔵 Mejora Funcional

**Módulo:** Reportes

**Archivos afectados:**

- `presentation/ui/screen/reportes/ReportesRendimientoScreen.kt`
- `presentation/viewmodel/reportes/ReportesViewModel.kt`

**Descripción**

Actualmente el `PieChart` de distribución de gastos por insumo muestra los datos **agregados de TODAS las campañas** (`ObtenerTodosLosInsumosVinculadosUseCase()`). No hay forma de filtrar por campaña individual. Además, el gráfico no tiene leyenda con referencias claras (solo el pie chart con colores).

**Acceptance Criteria**

- Agregar un `ExposedDropdownMenu` **encima** del gráfico de insumos que permita seleccionar una campaña específica o "Todas las campañas".
- Al seleccionar una campaña, el PieChart debe filtrar y mostrar solo los insumos vinculados a esa campaña.
- Agregar una leyenda debajo del gráfico con el nombre del insumo, color correspondiente, y monto en pesos (`$X.XXX`).
- El diseño de la pantalla quedará: `seleccionador individual -> 3 cards de datos con {rendimiento, ganancias, costos} -> piechart insumos -> piechart distribucion cosechas -> y debajo la comparacion de campañas replicando las 3 cards`.

**Sub-issues / Tareas Técnicas***

- En `ReportesViewModel`: Inyectar `ObtenerCampaniasUseCase`, exponer lista de campañas y un `campaniaInsumoSeleccionada: StateFlow<Int?>` (null = todas).
- Crear `ObtenerInsumosVinculadosPorCampaniaUseCase` o parametrizar el existente.
- Filtrar `exportableData` por la campaña seleccionada.
- En `ReportesRendimientoScreen.kt`: Agregar `SelectorCampania` antes del PieChart.
- Agregar componente de leyenda debajo del PieChart.
- Reordenar los `item {}` del `LazyColumn` para poner insumos arriba y comparación abajo.

---

## Issue 19: Reportes — Gráfico de Desglose de Cosechas (Almacenada vs Vendida)

**Severidad:** 🔵 Feature Nueva

**Módulo:** Reportes

**Archivos afectados:**

- `presentation/ui/screen/reportes/ReportesRendimientoScreen.kt`
- `presentation/viewmodel/reportes/ReportesViewModel.kt`

**Descripción**

No existe un gráfico que permita visualizar cómo se distribuyó la cosecha total de una campaña: cuánto se almacenó (silo), cuánto se vendió, cuánto se reservó como alimento, y cuánto es el rendimiento bruto total. Este desglose es fundamental para que el productor entienda su operación.

**Acceptance Criteria**

- Agregar un nuevo gráfico (PieChart o BarChart horizontal) titulado "Desglose de Cosechas" debajo del gráfico de insumos.
- Debe mostrar como mínimo:
- **Rendimiento Bruto Total** (suma de todas las cosechas de la campaña en Tn o Kg).
- **Almacenado** (cosechas con `almacen` no vacío).
- **Vendido** (cosechas no almacenadas de tipo "Venta").
- **Reservado** (cosechas no almacenadas de tipo "Reserva").
- Incluir selector de campaña (puede compartirse con el del Issue 18).
- Si se implementa el Issue 17 (hectáreas), mostrar también el rendimiento por hectárea (`Tn/ha`).
- Todo el sistema de cosecha se unificará a Toneladas (Tn). Se eliminará el campo `unidad` de la DB y este apartado de la creación de cosechas.

**Sub-issues / Tareas Técnicas**

- En `ReportesViewModel`: Inyectar `CosechaRepository` y `CosechaNoAlmacenadaRepository` (o sus Use Cases).
- Crear un estado `cosechaDesglose: StateFlow<CosechaDesglose?>` que agrupe los datos por tipo.
- Crear data class `CosechaDesglose(bruto: Double, almacenado: Double, vendido: Double, reservado: Double, hectareasTotales: Double?)`.
- En `ReportesRendimientoScreen.kt`: Agregar sección con el gráfico y las métricas de desglose.

---

## Issue 20: Reportes — Implementar Comparación Real entre Campañas

**Severidad:** 🔵 Feature Pendiente

**Módulo:** Reportes

- Los dropdowns deben alimentarse de la lista real de campañas desde la BD.
- Al seleccionar campañas A y B, la comparación se basará en replicar las 3 tarjetas por campaña (Rendimiento, Ganancias, Costos).
- Se descarta la tarjeta "Insumos Totales" y se elimina el problema de sumar diferentes unidades unificando todo el sistema a Toneladas (Tn).
  - **Rendimiento:** `Σ cosechas.cantidad` de cada campaña (en Tn). Si hay hectáreas (Issue 17): `Tn/ha`.
  - **Ganancias:** `Σ (precio × cantidad)` de cosechas vendidas de cada campaña.
  - **Costos Totales:** `Σ (cantidad × precio)` de insumos vinculados a cada campaña.
- El gráfico de evolución mensual debe mostrar los costos de insumos por mes para cada campaña seleccionada.
- Si solo se selecciona una campaña, mostrar sus datos sin comparación.

\*\*Sub-issues / Tareas Técnicas\*\*

- En \`ReportesViewModel\`: Agregar estados \`campaniaA: StateFlow<Campania?>\` y \`campaniaB: StateFlow<Campania?>\`.
- Inyectar \`CosechaRepository\`, \`CampaniaInsumoRepository\` y\`CosechaNoAlmacenadaRepository\`.
- Crear lógica de cálculo para rendimiento, ganancias, costos e insumos por campaña.
- Crear estado \`MetricasComparativas\` con los 4 valores para cada campaña.
- En \`ReportesRendimientoScreen.kt\`: Conectar los dropdowns a la lista real de campañas y a los métodos \`seleccionarCampaniaA(id)\` / \`seleccionarCampaniaB(id)\` del ViewModel.
- Reemplazar valores hardcodeados de las \`CardMetricaComparativa\` por los datos del ViewModel.
- Reemplazar valores del \`Canvas\` de evolución mensual por datos reales agrupados por mes.

---

---

# ⚪ NIVEL L5 — CALIDAD Y UX

Mejoras de experiencia de usuario, accesibilidad y polish visual.

---

## Issue 21: Bloquear Modo Oscuro (Forzar Tema Claro)

\*\*Severidad:\*\* ⚪ UX / Calidad

\*\*Módulo:\*\* Tema / Configuración

\*\*Archivos afectados:\*\*

- \`presentation/ui/theme/Theme.kt\`
- \`presentation/MainActivity.kt\`

\*\*Descripción\*\*

La app define un \`DarkColorScheme\` completo en \`Theme.kt\` y respeta el setting del sistema (\`isSystemInDarkTheme()\`). Sin embargo, las pantallas usan colores hardcodeados de \`AgriCoreColors.kt\` (\`AgriVerde\`, \`AgriFondo\`, \`TextoPrincipal\`, \`Color.White\`, etc.) en lugar de tokens de Material Theme (\`MaterialTheme.colorScheme.\*\`). Esto significa que en modo oscuro el esquema de colores cambia parcialmente (fondo, surfaces) pero todos los textos y acentos hardcodeados se rompen visualmente (texto oscuro sobre fondo oscuro, Cards blancas sobre superficie oscura, etc.).

\*\*Decisión:\*\* Bloquear modo oscuro completamente. La app siempre se verá en modo claro.

\*\*Acceptance Criteria\*\*

- La app debe renderizarse siempre en modo claro, independientemente del setting del sistema operativo.
- No debe haber cambios visuales al activar/desactivar modo oscuro en el dispositivo.

\*\*Sub-issues / Tareas Técnicas\*\*

- \*\*Opción A (Rápida — Recomendada):\*\* En \`DonElioTheme()\` en \`Theme.kt\`, forzar \`darkTheme = false\` ignorando el parámetro:

\`\`\`kotlin

@Composable

fun DonElioTheme(

darkTheme: Boolean = false, // ← Siempre false

dynamicColor: Boolean = false,

content: @Composable () -> Unit

) {

val colorScheme = LightColorScheme // ← Siempre light

...

}

\`\`\`

- \*\*Opción B (Alternativa):\*\* En \`MainActivity.kt\`, pasar \`darkTheme = false\` explícitamente:

\`\`\`kotlin

DonElioTheme(darkTheme = false) { DonElioApp() }

\`\`\`

- Verificar que \`isAppearanceLightStatusBars\` quede siempre en \`true\`.

---

## Issue 22: La Pantalla No Se Desplaza al Escribir (Teclado Cubre los Campos)

\*\*Severidad:\*\* ⚪ UX / Calidad

\*\*Módulo:\*\* Global / Todos los formularios

\*\*Archivos afectados:\*\*

- \`presentation/MainActivity.kt\`
- Formularios: \`FormularioCampaniaScreen\`, \`FormularioCosechaScreen\`, \`NuevaTareaScreen\`, \`ObservacionesScreen\`, \`FormularioInsumoScreen\`

\*\*Descripción\*\*

Al abrir el teclado virtual en los formularios, los campos de texto inferiores quedan cubiertos por el teclado sin que la pantalla se desplace automáticamente para mantener el campo activo visible. Esto ocurre porque:

1. \`MainActivity\` usa \`enableEdgeToEdge()\` y \`WindowCompat.setDecorFitsSystemWindows(window, false)\`, lo cual desactiva el ajuste automático de ventana por el sistema.
2. No se aplica \`Modifier.imePadding()\` ni \`WindowInsets.ime\` en ningún Composable del proyecto.


Búsqueda en el proyecto: No se encontró ninguna referencia a \`imePadding\`, \`imeNestedScroll\`, \`WindowInsets.ime\`, \`adjustResize\` ni \`adjustPan\`.

\*\*Acceptance Criteria\*\*

- Al enfocar un campo de texto en cualquier formulario, la pantalla debe desplazarse automáticamente para que el campo quede visible por encima del teclado.
- El comportamiento debe ser consistente en todos los formularios de la app.

\*\*Sub-issues / Tareas Técnicas\*\*

- \*\*Opción A (Recomendada — Global):\*\* En \`DonElioApp()\` o en el \`Scaffold\` principal, agregar \`Modifier.imePadding()\` al contenedor principal. Esto aplicará el padding del teclado a toda la app.
- \*\*Opción B (Por formulario):\*\* En cada pantalla con formulario, envolver el contenido en un \`Column(modifier = Modifier.imePadding())\` o agregar \`.imePadding()\` al \`LazyColumn\`.
- Verificar que \`enableEdgeToEdge()\` siga funcionando correctamente con \`imePadding()\` (son compatibles en Compose).
- Testear en formularios con muchos campos (FormularioCosecha, NuevaTarea) que el scroll funcione correctamente.

---

## Issue 23 (Original Issue 3 — Revisado): Datos Mock del Dashboard — Clima y Salud de Lotes

\*\*Severidad:\*\* ⚪ Cosmético

\*\*Módulo:\*\* Dashboard

\*\*Archivo afectado:\*\* \`presentation/ui/screen/home/DashboardOperacionesScreen.kt\`

\*\*Descripción\*\*

Las tarjetas de "Clima 24°C | Humedad 60%" y "Salud Lotes 90% Óptimo" en el Dashboard son completamente estáticas/mock. No se planea integrar API de clima ni análisis de salud de lotes en esta versión.

\*\*Acceptance Criteria\*\*

- Eliminar las tarjetas mock de Clima y Salud de Lotes del Dashboard.
- Reemplazarlas con información útil real, como un resumen de la campaña activa, un conteo de insumos y cosechas, o simplemente eliminarlas para simplificar la pantalla.

\*\*Sub-issues / Tareas Técnicas\*\*

- En \`DashboardOperacionesScreen.kt\`: Eliminar el bloque \`item { }\` que contiene las dos tarjetas mock.
- (Opcional) Reemplazar por un resumen rápido con datos reales: "X campañas activas · Y tareas pendientes · Z cosechas registradas".


 
