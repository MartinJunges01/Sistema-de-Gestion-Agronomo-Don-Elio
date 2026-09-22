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

 feature/issues-466-467-468
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

## [#398] refactor(reportes): ReportesViewModel inyecta repositorios directamente (DT-022)

**Severidad:** 🔴 Deuda Técnica (Arquitectura)
**Módulo:** Reportes / Domain
**Archivo afectado:** presentation/viewmodel/reportes/ReportesViewModel.kt

**Descripción**
ReportesViewModel inyecta CampaniaInsumoRepository y CosechaRepository directamente, violando el principio de Clean Architecture que exige que la capa de presentación consuma únicamente UseCases.

**Criterios de Aceptación**
- [ ] Crear ObtenerResumenFinancieroPorCampaniasUseCase en domain/use_case/.
- [ ] Refactorizar ReportesViewModel para inyectar exclusivamente UseCases.
- [ ] Actualizar ReportesViewModelTest para mockear el nuevo UseCase.

## [#418] refactor(observaciones): limpieza de archivos de fotos huérfanos al reemplazar o eliminar imagenUri

**Severidad:** 🔵 UX / Deuda Técnica
**Módulo:** Observaciones / Almacenamiento
**Archivos afectados:** domain/use_case/EditarObservacionUseCase.kt, core/utils/FileManager.kt

**Descripción**
Al reemplazar o eliminar una foto asociada a una observación existente, el archivo físico previamente guardado en el almacenamiento local permanece en el dispositivo sin ser borrado, acumulando archivos huérfanos.

**Criterios de Aceptación**
- [ ] Al reemplazar una foto existente por una nueva o por null, eliminar el archivo anterior del almacenamiento interno.
- [ ] Test unitario que valide la invocación al borrado de archivo huérfano.

---

## [DT-466-A] Campo de estado de campaña no confirmado en la entidad Room

**Severidad:** 🟡 Riesgo Medio
**Módulo:** Campañas / Data Layer
**Archivos afectados:** data/local/entity/CampaniaEntity.kt, data/local/dao/CampaniaDao.kt
**Detectado durante:** Análisis de Issue #466 (2026-09-22)

**Descripción**
El Issue #466 asume que existe un campo booleano en la entidad `Campania` que indica si está activa o archivada. Sin embargo, el nombre exacto del campo no fue confirmado antes de diseñar la solución. Puede ser `activa`, `estado`, `habilitada`, o `archivada`. Un nombre incorrecto en las queries SQL de Room resultará en un error de compilación o en datos incorrectos.

**Causa Raíz (Código)**
```kotlin
// Hipótesis del campo — debe verificarse en CampaniaEntity.kt
data class CampaniaEntity(
    val activa: Boolean // ¿Es este el campo correcto?
)
```

**Criterios de Aceptación**
- [ ] Verificar el nombre real del campo en `CampaniaEntity.kt` antes de implementar queries DAO.
- [ ] Usar el nombre exacto del campo en `@Query` de Room para filtrar campañas activas.

---

## [DT-467-A] Lógica de cálculo financiero duplicada entre HomeViewModel y ReportesViewModel

**Severidad:** 🔴 Deuda Técnica (DRY / Clean Architecture)
**Módulo:** Dashboard / Reportes / Domain
**Archivos afectados:** presentation/viewmodel/home/HomeViewModel.kt, presentation/viewmodel/reportes/ReportesViewModel.kt
**Detectado durante:** Análisis de Issue #467 (2026-09-22)

**Descripción**
El Issue #467 requiere que Reportes calcule Capital Invertido, Ingresos Brutos y Balance — los mismos indicadores que el Dashboard. Si no existe un `UseCase` compartido para este cálculo, la lógica estará duplicada en dos ViewModels. Esto viola el principio DRY y dificulta la corrección del bug #402 (métricas incorrectas).

**Causa Raíz (Código)**
```kotlin
// HomeViewModel.kt calcula métricas financieras directamente
// ReportesViewModel.kt calcularía las mismas métricas por su cuenta
// → Misma lógica en dos lugares distintos
```

**Criterios de Aceptación**
- [ ] Crear `ObtenerResumenFinancieroPorFiltrosUseCase` en `domain/use_case/`.
- [ ] Tanto `HomeViewModel` como `ReportesViewModel` deben consumir este UseCase.
- [ ] El UseCase debe aceptar parámetros opcionales de `campaniaId` y `rangoFechas` para soportar el filtrado de Reportes.
- [ ] Actualizar tests de ambos ViewModels.

---

## [DT-467-B] Panel financiero de Reportes puede mostrar valores incorrectos si #402 no está resuelto

**Severidad:** 🟠 Riesgo Funcional
**Módulo:** Reportes / Dashboard
**Archivos afectados:** presentation/viewmodel/reportes/ReportesViewModel.kt
**Detectado durante:** Análisis de Issue #467 (2026-09-22)
**Bloquea:** Issue #467

**Descripción**
El Issue #467 indica que los valores del panel en Reportes "deben coincidir con los del Dashboard cuando los filtros están vacíos". Sin embargo, el Issue #402 (pendiente en Iteración 4) documenta que las métricas del Dashboard son actualmente incorrectas (muestra Inversión/Cosechado/Costo en lugar de Capital/Ingresos/Balance). Si se implementa #467 antes que #402, el panel de Reportes heredará los valores incorrectos o habrá inconsistencia entre ambos.

**Criterios de Aceptación**
- [ ] Coordinar con quien implemente #402 para compartir el UseCase de cálculo financiero.
- [ ] Verificar que los valores de Reportes (sin filtros) coinciden con Dashboard tras completar #402.

## [DT-467-C] `ResumenRendimiento` no tiene campos de Ingresos Brutos ni Balance
**Severidad:** 🟠 Deuda funcional (Bloquea parcialmente #467)
**Módulo/Archivo:** `domain/use_case/ResumenRendimiento.kt`
**Descripción**
El modelo `ResumenRendimiento` actualmente tiene `capitalInvertido`, `totalCosechado` y `costoPorTonelada`, pero le faltan `ingresosBrutos` y `balance` para soportar el nuevo panel financiero.
**Criterios de Aceptación**
- [ ] Agregar `ingresosBrutos` y `balance` al data class (con valores por defecto para retrocompatibilidad).

## [DT-467-D] Cálculo de `resumenFiltrado` en ViewModel viola Clean Architecture
**Severidad:** 🔴 Deuda Técnica (Arquitectura)
**Módulo/Archivo:** `ReportesViewModel.kt`
**Descripción**
El `resumenFiltrado` se calcula directamente en el ViewModel inyectando repositorios, violando Clean Architecture. Relacionado a #398.
**Criterios de Aceptación**
- [ ] Extraer lógica a un UseCase dedicado.

## [DT-468-A] Estados huérfanos de Evolución de Cultivo en `ReportesViewModel`
**Severidad:** 🔵 Deuda Técnica (Código muerto)
**Módulo/Archivo:** `ReportesViewModel.kt`
**Descripción**
Al eliminar el gráfico de Evolución Histórica de la UI (Issue #468), los states correspondientes (`cultivos`, `cultivoSeleccionado`, `evolucionCultivo`) y dependencias de UseCase quedan huérfanos.
**Criterios de Aceptación**
- [ ] Eliminar los states y el UseCase inyectado del ViewModel.

 main
