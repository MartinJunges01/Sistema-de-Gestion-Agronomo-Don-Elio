# Bugs Identificados

> Los issues con ID oficial se encuentran en el Roadmap (`.context/roadmap_iteracion_6.md`).
> Este archivo registra **deuda técnica nueva** detectada durante las sesiones de desarrollo de la Iteración 6, pendiente de subir a GitHub para obtener su ID.

---

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

## 🛠 DEUDA TÉCNICA PENDIENTE — Iteración 6

<!-- Sin deudas técnicas pendientes actualmente -->

---

## 🟢 DEUDA TÉCNICA RESUELTA — Iteración 6

<!-- Mover aquí las deudas técnicas resueltas durante la Iteración 6 -->

## [NO-ISSUE — Revisión PR #473] Cambio de comportamiento en búsqueda de insumos con campo vacío

**Severidad:** ✅ No es bug — UX intencional
**Módulo:** Insumos
**Archivo afectado:** `presentation/ui/screen/insumo/VincularInsumoScreen.kt`

**Análisis**
Detectado durante revisión de PR #473. El nuevo comportamiento (mostrar la lista completa de insumos cuando el campo de búsqueda está vacío) es **UX correcto e intencional**: funciona como un modo "browse" que permite al usuario explorar el catálogo sin necesidad de escribir. El comportamiento original (no mostrar nada con búsqueda en blanco) era menos descubrible. **No requiere acción.**

---

## [RESUELTO — Revisión PR #474] Plan de pruebas no actualizado para `reactivarCampania()` (#465)

**Severidad:** 🔵 UX / Deuda Técnica
**Módulo:** Campañas / Documentación
**Archivo afectado:** `docs/plan_de_pruebas.md`

**Descripción**
Durante la revisión del PR #474 (fix/465) se detectó que `reactivarCampania()` en `GestionCampaniasViewModel` no tenía sus casos Given-When-Then documentados en el plan de pruebas, violando el Skill `MantenerTesting`.

**Criterios de Aceptación**
- [x] Agregar en `docs/plan_de_pruebas.md` los casos `VM-GC-1`, `VM-GC-2` y `VM-GC-3` para `reactivarCampania()`.
- [x] Cubrir los escenarios: invocación exitosa, sin error, y con `Resource.Error`.

---

## [RESUELTO - Commit 315b321] Falta documentación (Resuelto) de pruebas para asignación múltiple de insumos (Issue #455)

**Severidad:** 🛠 UX / Deuda Técnica
**Módulo:** Insumos / Documentación
**Archivo afectado:** `docs/plan_de_pruebas.md`

**Descripción**
Durante la revisión del Issue #455 (permitir agregar el mismo insumo múltiples veces a una campaña con fechas independientes), se detectó que el archivo `plan_de_pruebas.md` no fue actualizado con los nuevos casos `Given-When-Then` correspondientes al cambio en la lógica de negocio, violando el prompt operativo.

**Causa Raíz (Código)**
Se modificaron DAOs y el UseCase (`AsignarInsumoACampaniaUseCase`) pero no se documentaron los nuevos escenarios de testing.

**Criterios de Aceptación**
- [x] Escribir los escenarios Given-When-Then para la vinculación múltiple de un insumo a una misma campaña.
- [x] Verificar que los tests unitarios implementados cubran fielmente esos casos documentados.

