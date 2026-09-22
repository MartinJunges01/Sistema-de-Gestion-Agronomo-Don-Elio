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

<!-- Añadir aquí las nuevas deudas técnicas detectadas durante la Iteración 6 -->

## [PENDIENTE] Falta SnackbarHost en TareasScreen para mostrar errores del ViewModel

**Severidad:** 🔵 UX / Deuda Técnica
**Módulo:** Tareas
**Archivo afectado:** `TareasScreen.kt`

**Descripción**
El `TareaViewModel` gestiona un estado `errorMessage` para emitir mensajes informativos (ej. "Error al actualizar estado de tarea"), pero la vista `TareasScreen` no tiene implementado un `SnackbarHost` ni recolecta este estado, por lo que estos errores fallan silenciosamente.

**Criterios de Aceptación**
- [ ] Implementar un `SnackbarHost` en el componente principal de `TareasScreen`.
- [ ] Mostrar el mensaje recolectando `viewModel.errorMessage`.
- [ ] Limpiar el error llamando a `viewModel.clearError()` luego de mostrarlo.

---

## 🟢 DEUDA TÉCNICA RESUELTA — Iteración 6

<!-- Mover aquí las deudas técnicas resueltas durante la Iteración 6 -->

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

