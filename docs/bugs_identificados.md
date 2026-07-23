# Bugs Identificados

> Los issues con ID oficial se encuentran en el Roadmap (`roadmap_iteracion_2.md`).
> Este archivo registra **deuda técnica nueva** detectada durante sesiones de desarrollo, pendiente de subir a GitHub para obtener su ID.

---

## [PENDIENTE-ID] InsumoVinculacionViewModel sin método sincronizarInsumos()

**Severidad:** ⚪ UX / Deuda Técnica
**Módulo:** Insumos / Detalle Campaña / Tabs
**Archivo afectado:** `presentation/viewmodel/insumo/InsumoVinculacionViewModel.kt`

**Descripción**
Durante la resolución del Issue [#292], se identificó que `InsumoVinculacionViewModel` tiene exactamente el mismo patrón de bug que `TareaViewModel`: lee `campaniaId` desde `SavedStateHandle` y no expone un método para sincronizarlo externamente. El fix actual en `DetalleCampaniaScreen.kt` usa la key dinámica `"tab_insumos_$campaniaId"` como solución, pero **no agrega un `LaunchedEffect` con `sincronizarInsumos()`** como segunda línea de defensa (al contrario de `TabTareas`).

Esto podría causar que `TabInsumos` muestre insumos desactualizados en ciertos escenarios de recomposición edge-case.

**Causa Raíz (Código)**
```kotlin
// InsumoVinculacionViewModel.kt — mismo patrón que TareaViewModel:
private val _campaniaIdSeleccionada = MutableStateFlow<Int?>(
    savedStateHandle.get<Int>("campaniaId").takeIf { it != -1 }
)
// No existe método público sincronizarInsumos(id: Int)
```

**Criterios de Aceptación**
- Agregar `fun sincronizarInsumos(id: Int)` en `InsumoVinculacionViewModel` con la misma lógica idempotente de `sincronizarCampania()`.
- Agregar `LaunchedEffect(campaniaId) { vm.sincronizarInsumos(campaniaId) }` en `TabInsumos` dentro de `DetalleCampaniaScreen.kt`.
- Crear `InsumoVinculacionViewModelTest` con casos Given-When-Then equivalentes.

**Origen:** Detectado durante fix del Issue [#292] — 2026-07-22


## Convención de Registro
Cada vez que identifiques un bug, usa la siguiente plantilla. Luego súbelo a GitHub para obtener su ID oficial y reemplaza `[#ID]` con el número correspondiente. Evita usar numeraciones locales arbitrarias (como "Issue 1", "Issue 2").

---
## [#ID] Título Corto del Bug

**Severidad:** 🔴 CRASH | 🟠 Bug Funcional | 🟡 Feature Faltante | 🔵 Mejora de Reportes | ⚪ UX
**Módulo:** Nombre del módulo
**Archivo afectado:** `ruta/al/archivo.kt`

**Descripción**
Descripción detallada de qué sucede y por qué.

**Causa Raíz (Código)**
```kotlin
// Fragmento de código problemático si es conocido
```

**Acceptance Criteria**
- [ ] Criterio de negocio/usuario 1
- [ ] Criterio de negocio/usuario 2

**Sub-issues / Tareas Técnicas**
- [ ] Tarea técnica 1 (ej. Modificar UseCase)
- [ ] Tarea técnica 2 (ej. Actualizar ViewModel)
---
