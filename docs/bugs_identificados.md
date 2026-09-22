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

## [PENDIENTE-DT-466] Registros operativos de campañas archivadas visibles en listados generales

**Severidad:** 🟡 UX / Deuda Técnica
**Módulo:** Tareas / Cosechas / Insumos / Observaciones
**Archivos afectados:** `domain/use_case/ObtenerTareasFiltradasUseCase.kt` y similares.

**Descripción**
Al implementar el Issue #466 (ocultar campañas archivadas de los selectores mediante `ObtenerCampaniasActivasUseCase`), los listados generales (cuando no hay filtro de campaña aplicado) siguen obteniendo *todos* los registros de la base de datos (incluyendo los de campañas archivadas). Esto provoca que dichas tareas/cosechas aparezcan en la vista operativa pero con la etiqueta de campaña como "Sin Campaña" o "N/A" (ya que la campaña archivada no se encuentra en el Flow de campañas activas del ViewModel). 
Los módulos operativos deberían aislarse completamente de las campañas archivadas, dejando su visualización exclusiva para Reportes o el Historial.

**Criterios de Aceptación**
- [ ] Refactorizar repositorios o UseCases de listado (ej: `ObtenerTareasFiltradasUseCase`) para que, si el `campaniaId` es nulo, devuelvan únicamente los registros pertenecientes a campañas activas.
## [PENDIENTE-DT-467] Lógica de cálculo financiero duplicada entre HomeViewModel y ReportesViewModel

**Severidad:** 🔴 Deuda Técnica (DRY / Clean Architecture)
**Módulo:** Dashboard / Reportes / Domain
**Archivos afectados:** `presentation/viewmodel/home/HomeViewModel.kt`, `domain/use_case/ObtenerResumenRendimientoUseCase.kt`

**Descripción**
Al implementar el Issue #467, se creó `ObtenerResumenFinancieroPorFiltrosUseCase` para `ReportesViewModel`. Sin embargo, `HomeViewModel` sigue utilizando `ObtenerResumenRendimientoUseCase` (que tiene la misma lógica base pero sin filtros, e incluye métricas productivas). Esto viola el principio DRY. 

**Criterios de Aceptación**
- [ ] Refactorizar `HomeViewModel` para consumir `ObtenerResumenFinancieroPorFiltrosUseCase` con filtros vacíos.
- [ ] Eliminar `ObtenerResumenRendimientoUseCase` si ya no es utilizado por ninguna otra pantalla.

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

