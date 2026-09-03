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
- [x] **[#416] Issue 416:** Conservar campaña seleccionada en formularios al acceder desde BottomNav
- [ ] **[#415] Issue 415:** Rediseño de DetalleCampaniaScreen con grid 2xN y botones de accion rapida
- [ ] **[#417] Issue 417:** Planteamiento para reducir clics de acceso a cosechas, observaciones y tareas

### 📋 NIVEL L6 — DEUDA TÉCNICA
*(Sin deuda técnica reportada)*

---

## 🗓️ Orden de Ejecución Sugerido

### Fase 1 — Bugs Funcionales (Sprint 1)
1. **[#414]** Tareas del dia actual se marcan en rojo (Fix rápido en DashboardOperacionesScreen)
2. **[#413]** Nombre de usuario muestra Invitado (Fix rápido en LoginViewModel)

### Fase 2 — Mejoras de Flujo y UX (Sprint 2)
3. [x] **[#416]** Conservar campaña seleccionada (Requiere nuevo UltimaSeleccionManager)
4. **[#415]** Rediseño de DetalleCampaniaScreen a grid 2xN

### Fase 3 — Evaluación Final
5. **[#417]** Planteamiento para reducir clics (Se valida y cierra al concluir #415 y #416)
