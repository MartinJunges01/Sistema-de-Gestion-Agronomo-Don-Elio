# Roadmap: Iteración 5 — Mejoras de UX, Fixes de Regresión y Cierre de Features

> **Fuente:** Prueba manual de APK — 2026-09-01
>
> **Iteración anterior:** Los issues de .context/roadmap_iteracion_4.md están pendientes de finalización y se asumen en curso. Este roadmap cubre los nuevos hallazgos.

---

## Checklist de Progreso

### 🔴 NIVEL L1 — CRASHES Y ERRORES CRÍTICOS (BUGS BLOQUEANTES)
*(Sin nuevos bugs reportados)*

### 🟠 NIVEL L2 — BUGS FUNCIONALES
- [ ] **[#413] Issue 413:** Nombre de usuario muestra Invitado tras primer registro
- [ ] **[#414] Issue 414:** Tareas del dia actual se marcan en rojo en el Dashboard

### ⚪ NIVEL L3 — FIX UI / UX
*(Sin nuevos bugs reportados)*

### 🟡 NIVEL L4 — FEATURES NUEVAS
*(Sin nuevas features reportadas)*

### 🔵 NIVEL L5 — MEJORAS Y NUEVOS DESARROLLOS
- [ ] **[#415] Issue 415:** Rediseño de DetalleCampaniaScreen con grid 2xN y botones de accion rapida
- [ ] **[#416] Issue 416:** Conservar campaña seleccionada en formularios al acceder desde BottomNav
- [ ] **[#417] Issue 417:** Planteamiento para reducir clics de acceso a cosechas, observaciones y tareas

### 📋 NIVEL L6 — DEUDA TÉCNICA
*(Sin deuda técnica reportada)*

---
---

# 🟠 NIVEL L2 — BUGS FUNCIONALES

---

## [#413] Issue 413: Nombre de usuario muestra Invitado tras primer registro

**Severidad:** 🟠 Bug Funcional
**Módulo:** Autenticación / Sesión
**Archivo afectado:** presentation/viewmodel/login/LoginViewModel.kt

**Descripción**
En LoginViewModel.registro(), el flujo llama a egistroUseCase() y emite egistroExitoso = true, pero nunca persiste el nombre en sesión usando sessionManager.saveUserName(nombre). Al ingresar por primera vez, el Dashboard muestra "Invitado".

**Acceptance Criteria**
- [ ] Al completar el registro por primera vez, el Dashboard muestra el nombre real del usuario.
- [ ] El HomeViewModel.userName refleja el nombre sin necesidad de logout/login.
- [ ] Test unitario: egistro() exitoso -> sessionManager.saveUserName() es llamado con el nombre correcto.

---

## [#414] Issue 414: Tareas del dia actual se marcan en rojo en el Dashboard

**Severidad:** 🟠 Bug Funcional
**Módulo:** Dashboard / Tareas
**Archivo afectado:** presentation/ui/screen/home/DashboardOperacionesScreen.kt

**Descripción**
La comparación usa timestamps exactos en vez de comparar por día calendario. Una tarea de "hoy" que ya pasó en hora pero no en fecha se considera vencida y se marca en rojo.

**Acceptance Criteria**
- [ ] Tarea creada para hoy (cualquier hora) -> NO aparece en rojo en el Dashboard.
- [ ] Tarea creada para ayer o antes -> SÍ aparece en rojo.
- [ ] Tarea creada para mañana -> aparece en blanco.
- [ ] Test unitario que valide los 3 casos anteriores contra la función de comparación.

---
---

# 🔵 NIVEL L5 — MEJORAS Y NUEVOS DESARROLLOS

---

## [#415] Issue 415: Rediseño de DetalleCampaniaScreen con grid 2xN y botones de accion rapida

**Severidad:** 🔵 Mejora UX
**Módulo:** Campañas / Detalle
**Archivo afectado:** presentation/ui/screen/campania/DetalleCampaniaScreen.kt

**Descripción**
Reemplazar el ScrollableTabRow por un grid de 2 columnas x N filas de botones rectangulares. Cada botón incluye un botón + secundario visible que navega directamente al formulario de esa entidad (pantalla separada) pre-cargado con el campaniaId. Al presionar el botón principal navega a la pantalla de listado.

**Acceptance Criteria**
- [ ] El ScrollableTabRow y el contenido embebido de tabs son eliminados.
- [ ] Grid 2xN con botones visibles en pantalla.
- [ ] Cada botón muestra un subtexto con el contador correcto.
- [ ] El botón + navega directamente al formulario con campaniaId.
- [ ] El tap en el card navega a la pantalla de listado.

---

## [#416] Issue 416: Conservar campaña seleccionada en formularios al acceder desde BottomNav

**Severidad:** 🔵 Mejora UX
**Módulo:** Formularios / Sesión
**Archivos afectados:** 
- Formularios de Tarea, Cosecha y Observacion
- core/UltimaSeleccionManager.kt

**Descripción**
Cuando el usuario navega desde el BottomNav, no se pasa campaniaId en la ruta. Crear un UltimaSeleccionManager para persistir el campaniaId de la última campaña interactuada para usarla como fallback al navegar desde BottomNav.

**Acceptance Criteria**
- [ ] Formularios desde BottomNav muestran preseleccionada la última campaña usada.
- [ ] Cambio manual de campaña se persiste como la última.
- [ ] Un chip visible indica la campaña preseleccionada.
- [ ] Sin interferir con la navegación desde DetalleCampania (campaniaId explícito).

---

## [#417] Issue 417: Planteamiento para reducir clics de acceso a cosechas, observaciones y tareas

**Severidad:** 🔵 Mejora UX
**Módulo:** Navegación / UX Global

**Descripción**
Planteamiento estratégico documentado. Con el rediseño del grid 2xN (Issue #415) y la persistencia de campaña (Issue #416), el flujo de creación baja de 6 clics a 3.

**Acceptance Criteria**
- [ ] Flujo de creación desde Detalle de Campaña no supera 3 clics.
- [ ] Flujo desde BottomNav no requiere re-seleccionar campaña si ya fue usada.
- [ ] Documentar en docs/plan_de_pruebas.md los flujos GWT de los 3 escenarios.

---
---

## 🗓️ Orden de Ejecución Sugerido

### Fase 1 — Bugs Funcionales (Sprint 1)
1. **[#414]** Tareas del dia actual se marcan en rojo (Fix rápido en DashboardOperacionesScreen)
2. **[#413]** Nombre de usuario muestra Invitado (Fix rápido en LoginViewModel)

### Fase 2 — Mejoras de Flujo y UX (Sprint 2)
3. **[#416]** Conservar campaña seleccionada (Requiere nuevo UltimaSeleccionManager)
4. **[#415]** Rediseño de DetalleCampaniaScreen a grid 2xN

### Fase 3 — Evaluación Final
5. **[#417]** Planteamiento para reducir clics (Se valida y cierra al concluir #415 y #416)

---

## 🛠️ Reglas de Desarrollo

1. **Ramas:** Cada issue se trabaja en su propia rama (eature/, ix/, efactor/).
2. **Commits:** Convención de Conventional Commits (ej: ix(insumos): habilitar boton guardar al tipear).
3. **PRs:** Cada rama se integra vía Pull Request a main.
4. **Tests:** Cada cambio en UseCase o ViewModel requiere test unitario.
5. **Documentación:** Actualizar CHANGELOG.md tras cada issue completado.
6. **Referencia interna:** Cada DT se referencia con su ID oficial (ej: [#403]) en commits, PRs y roadmap.
