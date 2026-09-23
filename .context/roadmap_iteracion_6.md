# Roadmap: Iteración 6 — Revisión Manual Fin Roadmap 5

> **Fuente:** Planificación de bugs y mejoras detectadas tras la revisión manual del final de la Iteración 5.
>
> **Iteración anterior:** Todos los issues del roadmap_iteracion_5.md fueron completados o trasladados. Este roadmap cubre los hallazgos de bugs funcionales y deuda técnica pendientes.

---

## Checklist de Progreso

### 🟠 NIVEL L2 — BUGS FUNCIONALES
- [x] **[#462] Issue 462:** Pantalla Tareas: botón "Programar nueva tarea" siempre activo y precarga de campaña
- [ ] **[#464] Issue 464:** Formulario "Vincular Insumo": scroll habilitado y botones inferiores con texto completo
- [ ] **[#465] Issue 465:** Pantalla Detalle Campaña: confirmación al archivar y opción de reactivar campañas del historial
- [x] **[#466] Issue 466:** Campañas deshabilitadas: excluirlas de selectores de campaña pero accesibles desde Reportes
- [x] **[#467] Issue 467:** Pantalla Reportes: reemplazar "Resumen Productivo-Financiero" por panel solo financiero alineado al Dashboard

### 🔵 NIVEL L3 — UX / DEUDA TÉCNICA
- [ ] **[#463] Issue 463:** Pantalla Insumos: carácter mal representado (mojibake) en registros vinculados y en el catálogo
- [x] **[#468] Issue 468:** Pantalla Reportes: eliminar gráfico "Evolución Histórica por Cultivo"

---
---

# 🟠 NIVEL L2 — BUGS FUNCIONALES

---

## [#462] Issue 462: Pantalla Tareas: botón "Programar nueva tarea" siempre activo y precarga de campaña

**Severidad:** 🟡 Bug Funcional
**Módulo:** Tareas
**Archivos afectados:**
- `ui/tareas/TareasScreen.kt`
- `ui/tareas/NuevaTareaForm.kt`

**Descripción**
Al ingresar a la pantalla de Tareas sin tener una campaña seleccionada, el botón de "Programar nueva tarea" está deshabilitado o ausente, impidiendo al usuario abrir el formulario. Adicionalmente, si el usuario tiene una campaña seleccionada (ya sea por sesión previa o por el filtro activo de la pantalla de tareas), el formulario de nueva tarea debería recibirla como valor precargado en el campo correspondiente.

**Acceptance Criteria**
- [x] El botón "Programar nueva tarea" permanece activo e interactuable independientemente de si hay campaña seleccionada.
- [x] Si el filtro de tareas tiene una campaña seleccionada, al abrir el formulario de nueva tarea, el campo de campaña viene precargado con esa campaña.
- [x] Si no hay campaña en el filtro, el campo de campaña del formulario aparece vacío pero seleccionable.

---

## [#464] Issue 464: Formulario "Vincular Insumo": scroll habilitado y botones inferiores con texto completo

**Severidad:** 🟡 Bug Funcional
**Módulo:** Insumos
**Archivo afectado:**
- `ui/insumos/VincularInsumoForm.kt`

**Descripción**
El formulario de vincular insumo no permite hacer scroll cuando el contenido supera la altura de la pantalla, dejando los botones de la parte inferior cortados o inaccesibles. Además, el texto de dichos botones aparece truncado.

**Acceptance Criteria**
- [ ] El formulario permite scroll vertical en toda su extensión.
- [ ] Los botones de la parte inferior son completamente visibles y accesibles al hacer scroll.
- [ ] El texto de los botones no está truncado (sin `maxLines` ni `overflow = Ellipsis` que lo corten inadecuadamente).

---

## [#465] Issue 465: Pantalla Detalle Campaña: confirmación al archivar y opción de reactivar campañas del historial

**Severidad:** 🟡 Bug Funcional
**Módulo:** Campañas
**Archivos afectados:**
- `ui/campanas/DetalleCampanaScreen.kt`
- `ui/campanas/HistorialCampanasScreen.kt`

**Descripción**
El botón "Archivar campaña" ejecuta la acción directamente sin mostrar un diálogo de confirmación, a diferencia del botón de eliminación definitiva que sí lo presenta. Además, las campañas archivadas en el historial no ofrecen ninguna opción para reactivarlas.

**Acceptance Criteria**
- [ ] Al presionar "Archivar campaña" se muestra un diálogo de confirmación con el mismo estilo que el de eliminación definitiva, con opción Confirmar / Cancelar.
- [ ] En la pestaña Historial de campañas, cada campaña archivada muestra una opción (botón o menú) para reactivarla / rehabilitarla.
- [ ] Al confirmar la reactivación, la campaña vuelve al estado activo y desaparece del historial.

---

## [#466] Issue 466: Campañas deshabilitadas: excluirlas de selectores de campaña pero accesibles desde Reportes

**Severidad:** 🟡 Bug Funcional
**Módulo:** Campañas / Reportes
**Archivos afectados:**
- `ui/components/CampanaSelectorDropdown.kt`
- `ui/reportes/ReportesScreen.kt`

**Descripción**
Las campañas archivadas/deshabilitadas actualmente aparecen en todos los dropdowns/selectores de campaña del sistema (formularios de nueva tarea, vincular insumo, etc.), generando confusión. Deben ocultarse de esos selectores, pero mantenerse accesibles desde la pestaña de Reportes para consulta histórica.

**Acceptance Criteria**
- [ ] Los dropdowns de campaña en formularios (tareas, insumos, etc.) solo muestran campañas activas.
- [ ] La pantalla de Reportes permite filtrar por campañas deshabilitadas/archivadas.
- [ ] Una campaña recién archivada desaparece inmediatamente de los selectores sin reiniciar la app.

---

## [#467] Issue 467: Pantalla Reportes: reemplazar "Resumen Productivo-Financiero" por panel solo financiero alineado al Dashboard

**Severidad:** 🟡 Bug Funcional
**Módulo:** Reportes
**Archivos afectados:**
- `ui/reportes/ResumenFinancieroScreen.kt`
- `ui/dashboard/DashboardScreen.kt`

**Descripción**
La sección actualmente llamada "Resumen Productivo-Financiero" en Reportes debe quedar exclusivamente como financiera. Los 3 indicadores que muestre deben ser exactamente los mismos que el Dashboard: **Capital Invertido**, **Ingresos Brutos** y **Balance**. La diferencia respecto al Dashboard es que en Reportes estos indicadores deben poder filtrarse por campaña y por rango de fechas.

**Acceptance Criteria**
- [ ] La sección se llama "Resumen Financiero" (sin "Productivo").
- [ ] Muestra exactamente 3 cards: Capital Invertido, Ingresos Brutos y Balance, con el mismo diseño visual que el Dashboard.
- [ ] Los valores se recalculan al aplicar filtros de campaña y/o rango de fechas.
- [ ] Con filtros vacíos (sin campaña ni fechas), los valores coinciden con los del Dashboard.

---
---

# 🔵 NIVEL L3 — UX / DEUDA TÉCNICA

---

## [#463] Issue 463: Pantalla Insumos: carácter mal representado (mojibake) en registros vinculados y en el catálogo

**Severidad:** 🔵 UX / Deuda Técnica
**Módulo:** Insumos
**Archivos afectados:**
- `ui/insumos/InsumoItem.kt`
- `ui/insumos/RegistrarInsumoForm.kt`

**Descripción**
Dos ocurrencias de mojibake detectadas:
1. En la pantalla de Insumos, a la derecha de la cantidad en los registros de insumos ya vinculados aparece un carácter mal codificado.
2. En el formulario "Registrar insumo al catálogo", el ícono de información (botón `ℹ`) se renderiza incorrectamente.

**Acceptance Criteria**
- [ ] El símbolo a la derecha de la cantidad en los ítems de insumos vinculados se muestra correctamente (verificar la unidad de medida u otro símbolo esperado).
- [ ] El ícono `ℹ` en "Registrar insumo al catálogo" se renderiza como el símbolo de información correcto, sin mojibake.

---

## [#468] Issue 468: Pantalla Reportes: eliminar gráfico "Evolución Histórica por Cultivo"

**Severidad:** 🔵 UX / Deuda Técnica
**Módulo:** Reportes
**Archivos afectados:**
- `ui/reportes/ReportesScreen.kt`
- `ui/reportes/EvolucionHistoricaCultivoChart.kt`

**Descripción**
El gráfico de "Evolución Histórica por Cultivo" debe ser eliminado de la pantalla de Reportes por decisión de producto. Se descarta temporalmente hasta nueva definición.

**Acceptance Criteria**
- [ ] El gráfico de evolución histórica por cultivo no aparece en la pantalla de Reportes.
- [ ] No quedan referencias muertas ni estados colgados en el ViewModel de Reportes.
- [ ] El espacio liberado no genera un layout vacío o con padding excesivo.

---

## 🗓️ Orden de Ejecución Sugerido

### Fase 1 — Bugs Funcionales (Sprint 1)
1. **[#462]** Tareas: botón "Programar nueva tarea" activo
2. **[#464]** Insumos: scroll y texto en "Vincular Insumo"
3. **[#465]** Campañas: Confirmación archivar y reactivar
4. **[#466]** Campañas: Ocultar deshabilitadas en dropdowns

### Fase 2 — UI/UX & Refactor (Sprint 1-2)
5. **[#463]** Insumos: arreglar mojibake en catálogo
6. **[#467]** Reportes: Panel Financiero estilo Dashboard
7. **[#468]** Reportes: Eliminar gráfico de evolución histórica

---

## 🛠️ Reglas de Desarrollo

1. **Ramas:** Cada issue se trabaja en su propia rama (`feature/`, `fix/`, `refactor/`).
2. **Commits:** Convención de Conventional Commits (ej: `fix(tareas): mantener activo el boton de programar tarea`).
3. **PRs:** Cada rama se integra vía Pull Request a `main`.
4. **Tests:** Cada cambio en UseCase o ViewModel requiere actualizar su test unitario correspondiente.
5. **Documentación:** Actualizar `CHANGELOG.md` tras la conclusión de cada issue.
6. **Referencia:** Mantener estricto uso de la etiqueta de Issue (ej: `[#462]`) en todos los commits generados.
