# Roadmap: Iteración 4 — Estabilización, Fixes de Regresión y ABM de Tareas

> **Fuente:** Revisión manual de APK en emulador — 2026-08-31 (post-Iteración 3).
>
> **Iteración anterior:** Todos los issues del `roadmap_iteracion_3.md` fueron completados ✅ (salvo DT-022 que queda pendiente en esta iteración).

---

## Checklist de Progreso

### 🔴 NIVEL L1 — CRASHES Y ERRORES CRÍTICOS (BUGS BLOQUEANTES)
- [ ] **[#403] Issue 403:** Botón Guardar permanece deshabilitado al crear insumo nuevo
- [ ] **[#409] Issue 409:** Regresión Issue #338 — teclado virtual sigue bloqueando scroll en formularios

### 🟠 NIVEL L2 — BUGS FUNCIONALES
- [ ] **[#401] Issue 401:** Botón "Ver detalle →" del Dashboard no navega a Reportes
- [x] **[#402] Issue 402:** Métricas del resumen financiero del Dashboard no coinciden con diseño del Issue #346
- [ ] **[#404] Issue 404:** Edición de foto en observaciones sin implementar (Issue #337 sin resolver)

### ⚪ NIVEL L3 — FIX UI / UX
- [ ] **[#407] Issue 407:** Caracteres especiales (Ñ y acentos) no se renderizan en exportación PDF
- [ ] **[#408] Issue 408:** Regresión de Issues #339/#340 — leyendas PieChart se desbordan y gráfico se recorta

### 🟡 NIVEL L4 — FEATURES NUEVAS
- [ ] **[#410] Issue 410:** ABM completo de Tareas — agregar edición y eliminación

### 🔵 NIVEL L5 — MEJORAS Y NUEVOS DESARROLLOS
- [ ] **[#412] Issue 412:** Agregar etiquetas de eje X (nombre de campañas) al gráfico de evolución
- [ ] **[#405] Issue 405:** Completar filtros avanzados de tiempo en Reportes (DateRangePicker + accesos rápidos)
- [ ] **[#406] Issue 406:** Rediseñar UI del comparador de campañas + agregar métricas Cosecha/Ha y Costo/Tn

### 📋 NIVEL L6 — DEUDA TÉCNICA (Pendientes de Iteración 3)
- [ ] **[#398] Issue 398:** ReportesViewModel inyecta repositorios directamente (DT-022)

---
---

# 🔴 NIVEL L1 — CRASHES Y ERRORES CRÍTICOS

---

## [#403] Issue 403: Botón Guardar permanece deshabilitado al crear insumo nuevo

**Severidad:** 🔴 Bug Bloqueante
**Módulo:** Insumos / Formulario
**Archivos afectados:**
- `presentation/viewmodel/insumo/FormularioInsumoViewModel.kt`
- `presentation/ui/screen/insumo/FormularioInsumoScreen.kt`

**Descripción**
Al abrir el formulario de creación de insumo nuevo, el botón "Guardar Insumo" permanece deshabilitado aunque se completen todos los campos obligatorios. El estado `isGuardarHabilitado` comienza en `false` y `evaluarValidaciones()` solo se llama en `cargarInsumo()` (modo edición), pero no desde `onNombreChange()` ni `onCategoriaChange()`.

**Acceptance Criteria**
- Al tipear en Nombre o Categoría, `evaluarValidaciones()` se invoca y `isGuardarHabilitado` refleja el estado real.
- El botón Guardar se habilita en cuanto Nombre y Categoría son válidos.
- Agregar test unitario cubriendo: formulario nuevo + tipear nombre y categoría válidos → `isGuardarHabilitado = true`.

---

## [#409] Issue 409: Regresión Issue #338 — teclado virtual sigue bloqueando scroll en formularios

**Severidad:** 🔴 Bug Bloqueante (Regresión)
**Módulo:** Global / Formularios
**Archivos afectados:**
- `presentation/MainActivity.kt`
- Todos los formularios con campos de texto

**Descripción**
El Issue #338 fue marcado como resuelto (PR #376) pero la revisión manual confirma que el problema persiste de forma inconsistente: en algunos formularios el teclado tapa campos y bloquea el scroll, mientras que en otros (cosechas) funciona correctamente.

**Acceptance Criteria**
- Identificar en qué formularios persiste el problema.
- `imePadding()` o `imeNestedScroll()` se aplica uniformemente en todas las pantallas con campos de texto.
- El teclado no tapa ningún campo en: FormularioCampania, FormularioTarea, FormularioObservacion, FormularioInsumo, FormularioCosecha.

---
---

# 🟠 NIVEL L2 — BUGS FUNCIONALES

---

## [#401] Issue 401: Botón "Ver detalle →" del Dashboard no navega a Reportes

**Severidad:** 🟠 Bug Funcional
**Módulo:** Dashboard / Navegación
**Archivo afectado:** `presentation/ui/screen/home/DashboardOperacionesScreen.kt`

**Descripción**
El botón "Ver detalle →" tiene su lambda hardcodeada como `TODO` (`/* TODO: Navigate to reportes when implemented */`). Tiene animación de clic pero no realiza ninguna navegación.

**Acceptance Criteria**
- Al presionar "Ver detalle →" navegar a la pestaña de Reportes.
- El NavGraph debe proveer el callback correcto a `DashboardOperacionesScreen`.

---

## [#402] Issue 402: Métricas del resumen financiero del Dashboard no coinciden con diseño del Issue #346

**Severidad:** 🟠 Bug Funcional
**Módulo:** Dashboard / Home
**Archivos afectados:**
- `presentation/ui/screen/home/DashboardOperacionesScreen.kt`
- `presentation/viewmodel/home/HomeViewModel.kt`
- `domain/use_case/ResumenRendimiento.kt`

**Descripción**
El Dashboard muestra las métricas "Inversión / Cosechado (Tn) / Costo/Tn" en lugar de las métricas definidas en el Issue #346: "Capital Invertido / Ingresos Brutos / Balance (con semáforo de color verde/rojo)".

| Implementado | Requerido (Issue #346) |
|---|---|
| Inversión | Capital Invertido |
| Cosechado (Tn) | Ingresos Brutos ($) |
| Costo/Tn | Balance ($ con semáforo) |

**Acceptance Criteria**
- Agregar cálculo de Ingresos Brutos (precio × cantidad de cosechas vendidas de campañas activas).
- Calcular Balance = Ingresos Brutos − Capital Invertido.
- Mostrar Balance con color verde (positivo) o rojo (negativo).
- Las métricas actuales (Tn cosechadas, Costo/Tn) pueden mantenerse como métricas secundarias si hay espacio.

---

## [#404] Issue 404: Edición de foto en observaciones sin implementar (Issue #337 sin resolver)

**Severidad:** 🟠 Bug Funcional
**Módulo:** Observaciones / Edición
**Archivos afectados:**
- `presentation/ui/screen/observacion/ObservacionesScreen.kt`
- `presentation/viewmodel/observacion/ObservacionViewModel.kt`

**Descripción**
El Issue #337 fue marcado como completado y mergeado en PR #375, pero la funcionalidad de edición/eliminación de foto en el diálogo de edición de observaciones no está operativa. El diálogo solo permite modificar el texto.

**Acceptance Criteria**
- El diálogo de edición muestra la foto actual (si existe).
- Botón "Reemplazar foto": abre selector de cámara/galería.
- Botón ✕ sobre la imagen: elimina la foto manteniendo solo el texto.
- Si no tiene foto, permitir agregar una desde el diálogo de edición.
- Los cambios de foto se persisten correctamente al guardar.

---
---

# ⚪ NIVEL L3 — FIX UI / UX

---

## [#407] Issue 407: Caracteres especiales (Ñ y acentos) no se renderizan en exportación PDF

**Severidad:** ⚪ UX / Calidad
**Módulo:** Exportación / PDF
**Archivo afectado:** `core/utils/ReportExporter.kt`

**Descripción**
El PDF exportado muestra caracteres incorrectos en lugar de la Ñ y las vocales con tilde (á, é, í, ó, ú). El nombre de la campaña aparece en el encabezado pero los caracteres especiales no se renderizan.

**Acceptance Criteria**
- La Ñ y los caracteres acentuados se renderizan correctamente en el PDF.
- Verificar en: título del reporte, nombre de campaña, nombres de insumos y nombre de cultivo.

---

## [#408] Issue 408: Regresión Issues #339/#340 — leyendas PieChart se desbordan y gráfico se recorta

**Severidad:** ⚪ UX / Calidad (Regresión)
**Módulo:** Reportes / Gráficos
**Archivo afectado:** `presentation/ui/screen/reportes/ReportesRendimientoScreen.kt`

**Descripción**
Con leyendas largas, el contenido se desborda empujando el gráfico fuera del área visible. El círculo del PieChart se recorta arriba y abajo. El layout total no ajusta su altura dinámicamente.

**Acceptance Criteria**
- Las leyendas no se desbordan fuera de su contenedor.
- El círculo del PieChart no se recorta en ninguna dimensión.
- El layout (gráfico + leyendas + total) ajusta su altura de forma dinámica.
- Verificar con 5+ insumos de nombres largos.

---
---

# 🟡 NIVEL L4 — FEATURES NUEVAS

---

## [#410] Issue 410: ABM completo de Tareas — edición y eliminación

**Severidad:** 🟡 Feature
**Módulo:** Tareas / UI
**Archivos afectados:**
- `presentation/ui/screen/tarea/TareasScreen.kt`
- `presentation/viewmodel/tarea/TareaViewModel.kt`
- `domain/use_case/EditarTareaUseCase.kt` (verificar si existe, crear si no)
- `domain/use_case/EliminarTareaUseCase.kt` (verificar si existe, crear si no)

**Descripción**
Actualmente solo existe alta y visualización de tareas. No hay forma de editar ni eliminar una tarea existente.

**Acceptance Criteria**

### Edición
- Botón de edición (lápiz) en cada tarjeta de tarea.
- Al presionar, abre el formulario pre-cargado con los datos actuales.
- Al guardar, la tarea se actualiza en la BD y la lista se refresca.

### Eliminación
- Botón de eliminación (papelera) en cada tarjeta de tarea.
- Diálogo de confirmación: "¿Eliminar tarea [nombre]?"
- Implementar soft-delete (`activa = false`) para preservar historial.

### Tests
- Test unitario en `TareaViewModelTest` para edición y eliminación.
- Actualizar `plan_de_pruebas.md` con los flujos GWT nuevos.

---
---

# 🔵 NIVEL L5 — MEJORAS Y NUEVOS DESARROLLOS

---

## [#405] Issue 405: Completar filtros avanzados de tiempo en Reportes (DateRangePicker + accesos rápidos)

**Severidad:** 🔵 Mejora
**Módulo:** Reportes / Filtros
**Archivos afectados:**
- `presentation/ui/screen/reportes/ReportesRendimientoScreen.kt`
- `presentation/viewmodel/reportes/ReportesViewModel.kt`
- `presentation/ui/components/SelectorRangoFechas.kt` (reutilizar del Issue #345)

**Descripción**
Los filtros avanzados solo tienen operativo el filtro por campaña. Falta el filtro temporal definido en el Issue #353.

**Acceptance Criteria**
- Opciones rápidas: "Este mes", "Último mes", "Este año", "Lo que va del año", "Personalizado".
- La opción "Personalizado" abre el `DateRangePicker` de Material 3.
- Al aplicar un rango, todos los datos de Reportes se filtran por ese período.
- Chip o badge que indique el filtro activo con opción "Limpiar".
- El título de la sección de filtros no debe tener paréntesis innecesarios.

---

## [#406] Issue 406: Rediseñar UI del comparador de campañas + agregar métricas Cosecha/Ha y Costo/Tn

**Severidad:** 🔵 Mejora UX
**Módulo:** Reportes / Comparador
**Archivo afectado:** `presentation/ui/screen/reportes/ReportesRendimientoScreen.kt`

**Descripción**
La interfaz del comparador agrupa los datos por métrica (viñeta) en lugar de por campaña, lo que es confuso. Además faltan las métricas Cosecha/Ha y Costo/Tn en el comparador.

**Acceptance Criteria**
- Mostrar una tarjeta por campaña (A y B) debajo de su selector respectivo, con todas las métricas de esa campaña juntas.
- Agregar métricas Cosecha/Ha y Costo/Tn a cada tarjeta del comparador.
- El gráfico de barras existente se mantiene.

---

## [#412] Issue 412: Agregar etiquetas de eje X (nombre de campañas) al gráfico de evolución

**Severidad:** 🔵 Mejora
**Módulo:** Reportes / Gráficos
**Archivo afectado:** `presentation/ui/screen/reportes/ReportesRendimientoScreen.kt`

**Descripción**
El gráfico de evolución histórica (Canvas) actualmente dibuja los puntos pero carece de etiquetas en el eje X para identificar a qué campaña corresponde cada punto, a pesar de que el backend ya las ordena cronológicamente por campaña.

**Acceptance Criteria**
- Dibujar el nombre de la campaña correspondiente debajo de cada punto en el eje X del Canvas.
- Rotar el texto si es necesario para evitar solapamientos.
- Mantener el orden cronológico actual.

---
---

# 📋 NIVEL L6 — DEUDA TÉCNICA (Pendientes de Iteración 3)

---

## [#398] Issue 398: ReportesViewModel inyecta repositorios directamente (DT-022)

**Severidad:** 🔴 Deuda Técnica (Arquitectura)
**Módulo:** Reportes / Domain
**Archivo afectado:** `presentation/viewmodel/reportes/ReportesViewModel.kt`

**Descripción**
`ReportesViewModel` inyecta `CampaniaInsumoRepository` y `CosechaRepository` directamente, violando Clean Architecture.

**Acceptance Criteria**
- Crear `ObtenerResumenFinancieroPorCampaniasUseCase` en `domain/use_case/`.
- El ViewModel solo inyecta UseCases, nunca repositorios directamente.

---
---

## 🗓️ Orden de Ejecución Sugerido

### Fase 1 — Bugs Bloqueantes (Sprint 1)
1. **[#403]** Botón Guardar insumo deshabilitado (**crítico — regresión del Issue #334**)
2. **[#409]** Teclado bloquea scroll en formularios (**regresión del Issue #338**)

### Fase 2 — Bugs Funcionales (Sprint 1-2)
3. **[#401]** Botón "Ver detalle" Dashboard sin navegación
4. [x] **[#402]** Métricas Dashboard incorrectas
5. **[#404]** Edición de foto en observaciones sin implementar

### Fase 3 — Fix UI/UX (Sprint 2)
6. **[#407]** Encoding PDF caracteres especiales
7. **[#408]** Regresión PieChart leyendas desbordadas

### Fase 4 — Features y Mejoras (Sprint 3)
8. **[#410]** ABM completo de Tareas (edición + eliminación)
9. **[#405]** Filtros de tiempo en Reportes (DateRangePicker)
10. **[#406]** Rediseño comparador de campañas

### Fase 5 — Deuda Técnica (Continuo)
11. **[#398]** Refactor arquitectónico ReportesViewModel

---

## 🛠️ Reglas de Desarrollo

1. **Ramas:** Cada issue se trabaja en su propia rama (`feature/`, `fix/`, `refactor/`).
2. **Commits:** Convención de Conventional Commits (ej: `fix(insumos): habilitar boton guardar al tipear`).
3. **PRs:** Cada rama se integra vía Pull Request a `main`.
4. **Tests:** Cada cambio en UseCase o ViewModel requiere test unitario.
5. **Documentación:** Actualizar `CHANGELOG.md` tras cada issue completado.
6. **Referencia interna:** Cada DT se referencia con su ID oficial (ej: `[#403]`) en commits, PRs y roadmap.
