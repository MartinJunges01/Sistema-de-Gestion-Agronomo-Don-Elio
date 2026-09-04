# Bugs Identificados

> Los issues con ID oficial se encuentran en los Roadmaps activos (`.context/roadmap_iteracion_4.md` y `.context/roadmap_iteracion_5.md`).
> Este archivo registra **deuda técnica nueva** detectada durante sesiones de desarrollo (Iteraciones 4 y 5), pendiente de subir a GitHub para obtener su ID.

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

## [PENDIENTE-ID] NuevaTareaScreen no soporta modo edición

**Severidad:** 🟡 Deuda Técnica / Feature incompleta
**Módulo:** Tareas / UI
**Archivo afectado:** `presentation/ui/screen/tarea/NuevaTareaScreen.kt`

**Descripción**
Al implementar el Issue #410 (ABM completo de Tareas), se identificó que `NuevaTareaScreen` no acepta un `tareaId: Int?` para operar en modo edición. Actualmente solo soporta el flujo de alta. El formulario necesita pre-cargarse con los datos de la tarea existente cuando se navega desde el botón "editar" en `TareasScreen`.

**Causa Raíz (Código)**
```kotlin
// NuevaTareaScreen.kt — firma actual (aproximada)
@Composable
fun NuevaTareaScreen(
    campaniaId: Int,
    onBack: () -> Unit
) { ... }
// Falta: tareaId: Int? = null
```

**Criterios de Aceptación**
- [ ] El composable acepta `tareaId: Int? = null` como parámetro opcional.
- [ ] Si `tareaId != null`, el ViewModel carga la tarea existente y pre-rellena todos los campos.
- [ ] El título de la pantalla cambia: "Nueva Tarea" vs "Editar Tarea".
- [ ] Test unitario que verifique la carga de datos en modo edición.

---

## [PENDIENTE-ID] Cobertura de tests de ObservacionViewModel insuficiente

**Severidad:** 🔵 Deuda Técnica / Testing
**Módulo:** Observaciones / ViewModel
**Archivo afectado:** `presentation/viewmodel/observacion/ObservacionViewModel.kt`

**Descripción**
Al revisar el módulo de Observaciones para el Issue #404, no se pudo confirmar la existencia de `ObservacionViewModelTest`. La funcionalidad de edición/eliminación de foto agregada en la sesión actual no tiene cobertura de test unitario verificada.

**Criterios de Aceptación**
- [ ] Crear/completar `ObservacionViewModelTest` con MockK.
- [ ] Cubrir: `onFotoActualizada()` persiste la nueva URI correctamente.
- [ ] Cubrir: `onFotoEliminada()` establece `fotoUri = null` en el estado.
- [ ] Cubrir: guardar observación sin foto no lanza excepción.
