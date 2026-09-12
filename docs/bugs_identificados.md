# Bugs Identificados

> Los issues con ID oficial se encuentran en el Roadmap (`.context/roadmap_iteracion_4.md`).
> Este archivo registra **deuda técnica nueva** detectada durante sesiones de desarrollo de la Iteración 4, pendiente de subir a GitHub para obtener su ID.

---

<!-- Plantilla para nuevos bugs:
## [PENDIENTE-ID] Título descriptivo del bug

**Severidad:** 🔴 Bug Bloqueante | 🟡 Bug Funcional | 🔵 UX / Deuda Técnica
**Módulo:** [Ej: Insumos / Tareas / Sincronización]
**Archivo afectado:** \ruta/del/archivo.kt

**Descripción**
Breve descripción del problema encontrado...

**Causa Raíz (Código)**
```kotlin
// Snippet del código problemático si se conoce
```

**Criterios de Aceptación**
- [ ] Criterio 1
- [ ] Criterio 2
-->

## [#413] fix(auth): nombre de usuario muestra Invitado tras primer registro

**Severidad:** 🟠 Bug Funcional
**Módulo:** Autenticación / Sesión
**Archivo afectado:** presentation/viewmodel/login/LoginViewModel.kt

**Descripción**
En LoginViewModel.registro(), el flujo llama a registroUseCase() y emite registroExitoso = true, pero nunca persiste el nombre en sesión usando sessionManager.saveUserName(nombre). Al ingresar por primera vez, el Dashboard muestra "Invitado".

**Causa Raíz (Código)**
`kotlin
fun registro(nombre: String, nombreUsuario: String, contrasena: String) {
    viewModelScope.launch {
        registroUseCase(nombre, nombreUsuario, contrasena)
        _state.update { it.copy(isLoading = false, registroExitoso = true) }
        // sessionManager.saveUserName(nombre) <-- FALTA
    }
}
`

**Criterios de Aceptación**
- [ ] Al completar el registro por primera vez, el Dashboard muestra el nombre real del usuario.
- [ ] El HomeViewModel.userName refleja el nombre sin necesidad de logout/login.
- [ ] Test unitario: registro() exitoso -> sessionManager.saveUserName() es llamado con el nombre correcto.

## [#414] fix(dashboard): tareas del dia actual se marcan en rojo en el Dashboard

**Severidad:** 🟠 Bug Funcional
**Módulo:** Dashboard / Tareas
**Archivo afectado:** presentation/ui/screen/home/DashboardOperacionesScreen.kt

**Descripción**
La comparación usa timestamps exactos en vez de comparar por día calendario. Una tarea de "hoy" que ya pasó en hora pero no en fecha se considera vencida y se marca en rojo.

**Causa Raíz (Código)**
`kotlin
// DashboardOperacionesScreen.kt
val hoy = System.currentTimeMillis() // Timestamp exacto
val isVencida = tarea.fecha < hoy    // ❌ Tarea de hoy a las 15:30 -> true
`

**Criterios de Aceptación**
- [ ] Tarea creada para hoy (cualquier hora) -> NO aparece en rojo en el Dashboard.
- [ ] Tarea creada para ayer o antes -> SI aparece en rojo.
- [ ] Tarea creada para mañana -> aparece en blanco.
- [ ] Test unitario que valide los 3 casos anteriores contra la función de comparación.

## [#415] ux(campanias): rediseño de DetalleCampaniaScreen con grid 2xN y botones de accion rapida

**Severidad:** 🔵 Mejora UX
**Módulo:** Campañas / Detalle
**Archivo afectado:** presentation/ui/screen/campania/DetalleCampaniaScreen.kt

**Descripción**
Reemplazar el ScrollableTabRow por un grid de 2 columnas x N filas de botones rectangulares. Cada botón incluye un botón + secundario visible que navega directamente al formulario de esa entidad (pantalla separada) pre-cargado con el campaniaId. Al presionar el botón principal navega a la pantalla de listado.

**Criterios de Aceptación**
- [ ] El ScrollableTabRow y el contenido embebido de tabs son eliminados.
- [ ] Grid 2xN con botones visibles en pantalla.
- [ ] Cada botón muestra un subtexto con el contador correcto.
- [ ] El botón + navega directamente al formulario con campaniaId.
- [ ] El tap en el card navega a la pantalla de listado.

## [#416] ux(formularios): conservar campaña seleccionada al acceder desde BottomNav

**Severidad:** 🔵 Mejora UX
**Módulo:** Formularios / Sesión
**Archivos afectados:** Formularios de Tarea, Cosecha y Observacion. core/UltimaSeleccionManager.kt

**Descripción**
Cuando el usuario navega desde el BottomNav, no se pasa campaniaId en la ruta. Crear un UltimaSeleccionManager para persistir el campaniaId de la última campaña interactuada para usarla como fallback al navegar desde BottomNav.

**Criterios de Aceptación**
- [ ] Formularios desde BottomNav muestran preseleccionada la última campaña usada.
- [ ] Cambio manual de campaña se persiste como la última.
- [ ] Un chip visible indica la campaña preseleccionada.
- [ ] Sin interferir con la navegación desde DetalleCampania (campaniaId explícito).

## [#417] ux(navegacion): planteamiento para reducir clics de acceso a cosechas, observaciones y tareas

**Severidad:** 🔵 Mejora UX
**Módulo:** Navegación / UX Global

**Descripción**
Planteamiento estratégico documentado. Con el rediseño del grid 2xN y la persistencia de campaña, el flujo de creación baja de 6 clics a 3.

**Criterios de Aceptación**
- [ ] Flujo de creación desde Detalle de Campaña no supera 3 clics.
- [ ] Flujo desde BottomNav no requiere re-seleccionar campaña si ya fue usada.
- [ ] Documentar en docs/plan_de_pruebas.md los flujos GWT de los 3 escenarios.

---

## [PENDIENTE-ID] fix(tareas): campo `confirmar` se resetea a `false` al editar tarea completada

**Severidad:** 🟠 Bug Funcional
**Módulo:** Tareas / ABM
**Archivo afectado:** `presentation/viewmodel/tarea/NuevaTareaViewModel.kt`

**Descripción**
En `NuevaTareaViewModel.guardar()`, modo edición, el campo `confirmar` del objeto `Tarea` se construye con el valor literal `false` en lugar de preservar el estado actual de la tarea cargada. Si el usuario edita el nombre de una tarea ya marcada como completada, el guardado la regresará al estado pendiente.

**Causa Raíz (Código)**
```kotlin
// NuevaTareaViewModel.kt — modo edición
val tareaEditada = Tarea(
    id = tareaId,
    nombre = current.nombre.trim(),
    fecha = current.fecha,
    hora = current.hora,
    notificar = current.notificar,
    confirmar = false, // ❌ Hardcodeado — debería preservar el estado original
    idCampania = current.campaniaId
)
```

**Criterios de Aceptación**
- [ ] El `NuevaTareaFormState` incluye un campo `confirmar: Boolean` que se carga en `cargarTarea()`.
- [ ] Al guardar en modo edición, `confirmar` toma el valor del state (no un literal `false`).
- [ ] Test unitario: editar tarea completada → `confirmar` permanece `true` tras guardar.

---



## [PENDIENTE-ID] dt(permisos): verificación de permiso de cámara hardcodeada en composable

**Severidad:** 🔵 UX / Deuda Técnica (Arquitectura)
**Módulo:** Observaciones / Edición
**Archivo afectado:** `presentation/ui/screen/observacion/ObservacionesScreen.kt`

**Descripción**
En `DialogEditarObservacion`, el botón "Cámara" verifica el permiso directamente con `ContextCompat.checkSelfPermission()` en el cuerpo del composable. Esta lógica de negocio viola Clean Architecture (la UI no debe contener lógica de permisos) y duplica el manejo que ya existe en el composable `recordarPermisoCamara()`.

**Causa Raíz (Código)**
```kotlin
// ObservacionesScreen.kt — dentro del botón Cámara en DialogEditarObservacion
val isGranted = ContextCompat.checkSelfPermission(
    context, android.Manifest.permission.CAMERA
) == PackageManager.PERMISSION_GRANTED

if (isGranted) {
    accionPendiente?.invoke()  // ❌ Lógica de permisos en composable
} else {
    controlPermiso.solicitar()
}
```

**Criterios de Aceptación**
- [ ] La verificación y solicitud de permiso se centraliza en `recordarPermisoCamara()` o en un ViewModel/Manager dedicado.
- [ ] No hay llamadas directas a `ContextCompat.checkSelfPermission` en composables de pantalla.

---

## [PENDIENTE-ID] test(insumos/tareas): tests unitarios faltantes para AC de issues #403 y #410

**Severidad:** 🔵 Deuda Técnica (Testing)
**Módulo:** Insumos / Tareas
**Archivos afectados:**
- `app/src/test/.../insumo/FormularioInsumoViewModelTest.kt` (no existe)
- `app/src/test/.../tarea/NuevaTareaViewModelTest.kt` (cobertura de edición faltante)

**Descripción**
Los Acceptance Criteria de los Issues #403 y #410 definen tests unitarios obligatorios que no fueron incluidos en el PR #436:
- **#403:** Test: formulario nuevo + tipear nombre y categoría válidos → `isGuardarHabilitado = true`.
- **#410:** Tests: edición de tarea → datos pre-cargados correctamente; eliminación → tarea removida del estado.

**Criterios de Aceptación**
- [ ] Crear `FormularioInsumoViewModelTest` con caso Given-When-Then para modo creación.
- [ ] Agregar casos de edición y eliminación a `NuevaTareaViewModelTest` / `TareaViewModelTest`.
- [ ] Documentar los nuevos casos GWT en `docs/plan_de_pruebas.md`.
- [ ] Todos los tests pasan con `./gradlew test`.
