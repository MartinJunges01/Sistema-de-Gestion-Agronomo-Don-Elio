# Bugs Identificados

> Los issues con ID oficial se encuentran en el Roadmap (`roadmap_iteracion_3.md`).
> Este archivo registra **deuda técnica nueva** detectada durante sesiones de desarrollo de la Iteración 3, pendiente de subir a GitHub para obtener su ID.

---

> **Sesión:** Revisión de código de PRs abiertas #362, #375, #376, #377, #378 — 2026-08-25

---

## [DT-001] Test unitario de creación de insumo ausente en PR #362

**Severidad:** 🔵 UX / Deuda Técnica
**Módulo:** Insumos / Catálogo / Tests
**Archivo afectado:** `app/src/test/java/com/itec/donelio/presentation/viewmodel/insumo/FormularioInsumoViewModelTest.kt`

**Descripción**
El fix de Issue #334 (PR #362) corrige el bug de creación de insumos cambiando la condición `takeIf { it != -1 }`, pero no hay test que cubra el path de **creación** (cuando `insumoId == null`). Solo existe cobertura implícita para el path de edición.

**Criterios de Aceptación**
- [ ] Agregar test: `Given insumoId es null → When guardar() → Then se llama crearInsumoCatalogoUseCase y NO editarInsumoCatalogoUseCase`.
- [ ] Agregar test: `Given insumoId es -1 desde SavedStateHandle → Then insumoId interno es null`.
- [ ] Documentar en `docs/plan_de_pruebas.md`.

---

## [DT-002] ObservacionViewModel.validarEdicion() es un wrapper innecesario

**Severidad:** 🔵 UX / Deuda Técnica
**Módulo:** Observaciones / ViewModel / Clean Architecture
**Archivo afectado:** `app/src/main/java/com/itec/donelio/presentation/viewmodel/observacion/ObservacionViewModel.kt`

**Descripción**
El método `validarEdicion(texto, imagenUri)` en `ObservacionViewModel` es un wrapper de una línea sobre `ValidarObservacionUseCase`. La UI llama al ViewModel para ejecutar lógica que podría estar directamente en el UseCase o en estado reactivo. No viola Clean Architecture pero agrega una capa de indirección innecesaria.

**Causa Raíz (Código)**
```kotlin
// En ObservacionViewModel — wrapper sin valor agregado
fun validarEdicion(texto: String, imagenUri: String?): Boolean {
    return validarObservacionUseCase(texto, imagenUri)
}
```

**Criterios de Aceptación**
- [ ] Evaluar si `DialogEditarObservacion` puede llamar `ValidarObservacionUseCase` directamente via inyección (no recomendado en Compose) o si se expone como `StateFlow<Boolean>` reactivo en el ViewModel.
- [ ] Si se mantiene el wrapper, agregar KDoc explicando la razón.

---

## [DT-003] ObservacionViewModelTest sin caso de edición con foto (PR #375)

**Severidad:** 🔵 UX / Deuda Técnica
**Módulo:** Observaciones / Tests
**Archivo afectado:** `app/src/test/java/com/itec/donelio/presentation/viewmodel/observacion/ObservacionViewModelTest.kt`

**Descripción**
La PR #375 agrega `ValidarObservacionUseCase` al constructor del ViewModel y actualiza los mocks en el test (+4 líneas), pero no agrega nuevos casos `Given-When-Then` para el flujo de edición de fotos (reemplazar foto, eliminar foto).

**Criterios de Aceptación**
- [ ] Agregar test: `Given observación con foto → When editarObservacion con imagenUri = null → Then se guarda sin foto`.
- [ ] Agregar test: `Given observación sin foto → When editarObservacion con nuevo imagenUri → Then se guarda con foto`.
- [ ] Documentar ambos casos en `docs/plan_de_pruebas.md` con formato Given-When-Then.

---

## [DT-004] Fix de teclado (Issue #338) sin test instrumentado

**Severidad:** 🔵 UX / Deuda Técnica
**Módulo:** Global / WindowInsets / Tests
**Archivos afectados:** `presentation/MainActivity.kt`, `presentation/ui/screens/screens.kt`

**Descripción**
El fix de `consumeWindowInsets` + eliminación de `WindowCompat.setDecorFitsSystemWindows` en PR #376 no puede ser cubierto por tests unitarios estándar. Requiere validación manual obligatoria en dispositivo físico (no solo emulador) dado que el comportamiento varía por fabricante y versión de API.

**Criterios de Aceptación**
- [ ] Testear manualmente en al menos 2 dispositivos con diferente API (recomendado: API 29 + API 34).
- [ ] Testear en todos los formularios con campos de texto: Insumos, Cosechas, Campañas, Observaciones.
- [ ] Documentar resultado del test en `docs/plan_de_pruebas.md`.

---

## [DT-005] Eliminar campaña: no se verifica CASCADE en DAO (PR #377)

**Severidad:** 🟡 Bug Funcional
**Módulo:** Campañas / Data / Room
**Archivo afectado:** `data/local/entity/CampaniaEntity.kt` (y entidades relacionadas)

**Descripción**
La funcionalidad de eliminación permanente de campañas inactivas (PR #377) llama a `EliminarCampaniaUseCase` pero no se auditó si las FK de las tablas relacionadas (cosechas, insumos vinculados, observaciones, tareas) tienen `onDelete = CASCADE`. Si no, la eliminación fallará con error de integridad referencial o dejará datos huérfanos.

**Causa Raíz (Código)**
```kotlin
// En las entidades relacionadas — ejemplo:
@ForeignKey(
    entity = CampaniaEntity::class,
    parentColumns = ["id"],
    childColumns = ["campaniaId"],
    onDelete = ForeignKey.CASCADE  // ← ¿Está esto presente en TODAS las entidades?
)
```

**Criterios de Aceptación**
- [ ] Auditar `CosechaEntity`, `InsumoVinculadoEntity`, `ObservacionEntity`, `TareaEntity`: confirmar `onDelete = ForeignKey.CASCADE` en las FK de `campaniaId`.
- [ ] Agregar test instrumentado Room: `Given campaña con cosechas y tareas → When eliminarCampania → Then todas las entidades relacionadas son eliminadas`.
- [ ] Si falta CASCADE, corregir y agregar migración de DB (o `fallbackToDestructiveMigration()`).

---

## [DT-006] GestionCampaniasViewModelTest sin caso de error en eliminación

**Severidad:** 🔵 UX / Deuda Técnica
**Módulo:** Campañas / Tests
**Archivo afectado:** `app/src/test/java/com/itec/donelio/presentation/viewmodel/campania/GestionCampaniasViewModelTest.kt`

**Descripción**
La PR #377 agrega +8 líneas al test del ViewModel, pero no está claro si cubre el path de error de `eliminarCampaniaPermanente()`. El `SharedFlow<String>` de errores (`_errorMessage`) necesita cobertura para asegurar que el error se propaga correctamente a la UI.

**Criterios de Aceptación**
- [ ] Agregar test: `Given EliminarCampaniaUseCase emite Resource.Error → When eliminarCampaniaPermanente → Then errorMessage emite el mensaje correcto`.
- [ ] Agregar test: `Given EliminarCampaniaUseCase emite Resource.Success → When eliminarCampaniaPermanente → Then errorMessage no emite nada`.

---

## [DT-007] Archivo .context/plan_issue_338.md commiteado accidentalmente

**Severidad:** 🔵 UX / Deuda Técnica
**Módulo:** Repositorio / Estructura
**Archivo afectado:** `.context/plan_issue_338.md`

**Descripción**
La PR #377 incluye el archivo `.context/plan_issue_338.md` (artefacto de planificación temporal). Este archivo no debería formar parte del historial del repositorio ya que contamina el árbol de archivos de `.context/` con notas de trabajo internas.

**Criterios de Aceptación**
- [ ] Eliminar `.context/plan_issue_338.md` de la rama `fix/issue-342-campanias-inactivas` antes del merge, o revertir el archivo en el commit.
- [ ] Evaluar agregar `.context/plan_*.md` al `.gitignore` para evitar que vuelva a ocurrir.

---

## [DT-008] Persistencia de sesión no cubre el flujo de usuario Invitado (PR #378)

**Severidad:** 🟡 Bug Funcional
**Módulo:** Login / Session / Invitado
**Archivos afectados:** `presentation/viewmodel/login/LoginViewModel.kt`, `core/SessionManager.kt`

**Descripción**
La PR #378 persiste la sesión via `SessionManager.saveUserName()` que internamente setea `IS_LOGGED_IN_KEY = true`. Sin embargo, `LoginViewModel` no fue modificado para llamar `saveUserName()` al hacer login exitoso (ni en el flujo de usuario normal ni en el flujo de "Invitado"). El guardado de `userName` solo ocurre cuando el usuario edita su nombre desde configuración, no al loguear. Resultado: al cerrar y reabrir la app, `isLoggedIn` sigue siendo `false` y el usuario debe loguear nuevamente.

**Causa Raíz (Código)**
```kotlin
// LoginViewModel.login() — falta la llamada a sessionManager.saveUserName()
fun login(nombre: String) {
    // ... validaciones ...
    viewModelScope.launch {
        // ← Aquí debería llamarse sessionManager.saveUserName(nombre)
        _loginExitoso.value = true
    }
}
```

**Criterios de Aceptación**
- [ ] En `LoginViewModel.login()`: después del login exitoso, llamar `sessionManager.saveUserName(nombre)`.
- [ ] En el flujo de Invitado: llamar `sessionManager.saveUserName("Invitado")` para persistir la sesión.
- [ ] Agregar test: `Given login exitoso → Then isLoggedIn en DataStore es true`.
- [ ] Agregar test: `Given logout → Then isLoggedIn en DataStore es false`.

---

## [DT-009] Pantalla de splash es un Box vacío (pantalla en blanco)

**Severidad:** 🔵 UX / Deuda Técnica
**Módulo:** Login / Splash / UX
**Archivo afectado:** `presentation/MainActivity.kt`

**Descripción**
Mientras `MainViewModel.isLoggedIn` es `null` (estado de carga del DataStore), `MainActivity` muestra un `Box(modifier = Modifier.fillMaxSize())` completamente vacío. El usuario ve una pantalla en blanco durante ~100-300ms al iniciar la app.

**Causa Raíz (Código)**
```kotlin
if (isLoggedIn == null) {
    Box(modifier = Modifier.fillMaxSize())  // ← Pantalla en blanco
} else {
    DonElioApp(isLoggedIn = isLoggedIn!!)
}
```

**Criterios de Aceptación**
- [ ] Reemplazar el `Box` vacío por una pantalla de splash mínima con el logo de Don Elio y fondo `AgriFondo`.
- [ ] Opcionalmente: usar la API de Splash Screen de Android 12+ (`core-splashscreen`) para una transición más nativa.

---

## [DT-010] MainViewModel y CerrarSesionUseCase sin cobertura de tests (PR #378)

**Severidad:** 🔵 UX / Deuda Técnica
**Módulo:** Session / Tests
**Archivos afectados:**
- `presentation/viewmodel/MainViewModel.kt` (nuevo)
- `domain/use_case/CerrarSesionUseCase.kt` (nuevo)

**Descripción**
Dos nuevos artefactos introducidos en PR #378 no tienen tests unitarios. `MainViewModel` contiene lógica de decisión de ruta inicial (Login vs Dashboard), que es crítica para la UX del arranque de la app.

**Criterios de Aceptación**
- [ ] Crear `MainViewModelTest`: casos `isLoggedIn emite null → luego true` y `isLoggedIn emite null → luego false`.
- [ ] Crear `CerrarSesionUseCaseTest`: caso `Given sessionManager → When invoke() → Then sessionManager.logout() fue llamado`.
- [ ] Agregar los casos en `docs/plan_de_pruebas.md`.

## [PENDIENTE] DT-011: ReportesViewModelTest incompleto
**Severidad:** 🟡 UX / Deuda Técnica
**Descripción:** `ReportesViewModelTest` no cubre exportación con cosechas (nuevos paths de Issue #343).

## [PENDIENTE] DT-012: ObtenerTareasFiltradasUseCase sin tests
**Severidad:** 🟡 UX / Deuda Técnica
**Descripción:** `ObtenerTareasFiltradasUseCase` sin tests unitarios propios (Issue #345).

## [PENDIENTE] DT-013: ObtenerResumenRendimientoUseCase sin tests
**Severidad:** 🟡 UX / Deuda Técnica
**Descripción:** `ObtenerResumenRendimientoUseCase` sin tests (la lógica de filtro por mes requiere cobertura). (Issue #346)

## [PENDIENTE] DT-014: ObtenerCumplimientoTareasUseCase sin tests
**Severidad:** 🟡 UX / Deuda Técnica
**Descripción:** `ObtenerCumplimientoTareasUseCase` sin tests unitarios propios (Issue #347).

## [Resuelto] DT-015: Archivo residual build_error.txt
**Severidad:** 🔴 Bug Bloqueante
**Descripción:** `build_error.txt` commiteado en `fix/issue-349-refactor-db` — archivo de error de CI que no debe estar en el repositorio.

## [Resuelto] DT-016: Migración de BD faltante MIGRATION_5_6
**Severidad:** 🔴 Bug Bloqueante
**Descripción:** Verificar si `DonElioDatabase` en PR #349 incluye `MIGRATION_5_6`. Sin ella, Room hace crash al actualizar la app.

<!-- Plantilla para nuevos bugs:
## [PENDIENTE-ID] Título descriptivo del bug

**Severidad:** 🔴 Bug Bloqueante | 🟡 Bug Funcional | 🔵 UX / Deuda Técnica
**Módulo:** [Ej: Insumos / Tareas / Sincronización]
**Archivo afectado:** `ruta/del/archivo.kt`

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
