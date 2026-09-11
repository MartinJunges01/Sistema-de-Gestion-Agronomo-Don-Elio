# Roadmap: Iteración 5 — Estabilización Post-Testing, Fixes de UI y Arquitectura de Insumos

> **Fuente:** Verificación manual en emulador — 2026-09-09 (post-merge Iteración 4).
>
> **Iteración anterior:** Todos los issues del oadmap_iteracion_4.md fueron completados. Este roadmap cubre los hallazgos del testing manual y la deuda técnica pendiente relevada.

---

## Checklist de Progreso

### 🟠 NIVEL L2 — BUGS FUNCIONALES
- [x] **[#437] Issue 437:** Balance del Dashboard muestra valor incorrecto y tarjetas tienen overflow de texto
- [ ] **[#438] Issue 438:** Gráfico de Evolución Histórica falla visualmente con un solo punto de datos (1 campaña)
- [x] **[#441] Issue 441:** Contadores de Tareas completadas y Cosechas en grid DetalleCampania no se actualizan

### ⚪ NIVEL L3 — FIX UI / UX
- [ ] **[#434] Issue 434:** Truncamiento de nombres largos en eje X del gráfico de evolución
- [ ] **[#440] Issue 440:** Emojis/íconos se ven rotos al ingresarlos en el formulario de nuevo insumo

### 🔵 NIVEL L5 — MEJORAS Y NUEVOS DESARROLLOS
- [ ] **[#439] Issue 439:** Formulario dedicado de Vinculación de Insumos a Campaña desde grid DetalleCampania

---
---

# 🟠 NIVEL L2 — BUGS FUNCIONALES

---

## [#437] Issue 437: Balance del Dashboard muestra valor incorrecto y tarjetas tienen overflow de texto

**Severidad:** 🟠 Bug Funcional / 🔵 Mejora UX
**Módulo:** Dashboard / Resumen Financiero
**Archivos afectados:**
- domain/use_case/ObtenerResumenRendimientoUseCase.kt
- presentation/ui/screen/home/DashboardOperacionesScreen.kt

**Descripción**
El formato NumberFormat.getCurrencyInstance(Locale("es", "AR")) posiciona el signo negativo al final del string (ej. $ 6.133.500,00-). Al combinarse con maxLines = 1 en la tarjeta de resumen, el signo se trunca, mostrando visualmente un número positivo incorrecto. Adicionalmente, valores numéricos grandes generan saltos de línea imprevistos, provocando que las tarjetas tengan alturas irregulares y rompan la estética del grid.

**Acceptance Criteria**
- [ ] El Balance se calcula y muestra correctamente con el signo negativo visible (ej: -.133.500).
- [ ] Las 3 tarjetas de resumen mantienen siempre la misma altura uniforme.
- [ ] Para valores iguales o superiores a .000.000, se aplica formato abreviado (ej: $6,3M) o AutoSizeText.
- [ ] Incluye test unitario verificando cálculos con balance negativo.

---

## [#438] Issue 438: Gráfico de Evolución Histórica falla visualmente con un solo punto de datos

**Severidad:** 🟠 Bug Funcional
**Módulo:** Reportes / Gráficos Canvas
**Archivo afectado:** presentation/ui/screen/reportes/ReportesRendimientoScreen.kt

**Descripción**
Cuando un cultivo tiene exactamente una (1) campaña finalizada, la matemática del Canvas divide por (evolucion.size - 1), asignando un stepX igual al ancho total (width). Esto ocasiona que el único punto de datos se dibuje arrinconado en el extremo izquierdo. Además, existe un paddingBottom de 120f que resulta excesivo y desplaza las etiquetas fuera de los límites del Card.

**Acceptance Criteria**
- [ ] Cuando existe solo 1 campaña, el punto verde se dibuja perfectamente centrado de forma horizontal y vertical.
- [ ] Las etiquetas del Eje X se mantienen dentro de los bordes visibles del Card.
- [ ] El comportamiento actual con 2 o más campañas se mantiene sin regresiones.

---

## [#441] Issue 441: Contadores de Tareas completadas y Cosechas en grid DetalleCampania no se actualizan

**Severidad:** 🟠 Bug Funcional
**Módulo:** Campañas / Detalle
**Archivos afectados:**
- presentation/ui/screen/campania/DetalleCampaniaScreen.kt
- presentation/viewmodel/tarea/TareaViewModel.kt
- presentation/viewmodel/cosecha/CosechaViewModel.kt

**Descripción**
En el grid de 2 columnas de Detalles de la Campaña, las tarjetas de "Tareas" y "Cosechas" muestran 0 completadas o 0 registradas a pesar de existir datos reales en Room. Esto se debe a una condición de carrera: el StateFlow emite una lista vacía antes de que seleccionarCampania() termine de ejecutar y recupere los datos por campaniaId.

**Acceptance Criteria**
- [ ] La tarjeta de Tareas muestra dinámicamente N pendientes y M completadas.
- [ ] La tarjeta de Cosechas totaliza correctamente la cantidad de registros y la suma de kilogramos netos.
- [ ] La UI reacciona en tiempo real si ocurren cambios (altas/bajas) en tareas o cosechas.

---
---

# ⚪ NIVEL L3 — FIX UI / UX

---

## [#434] Issue 434: Truncamiento de nombres largos en eje X del gráfico de evolución

**Severidad:** ⚪ UX / Deuda Técnica
**Módulo:** Reportes / Gráficos Canvas
**Archivo afectado:** presentation/ui/screen/reportes/ReportesRendimientoScreen.kt

**Descripción**
Introducido en el PR #424, el nombre de la campaña se pinta en el eje X usando drawContext.canvas.nativeCanvas con una rotación de -45 grados. Al carecer de lógica de control de longitud, los nombres de campañas muy extensos se superponen entre sí (especialmente con múltiples puntos en la gráfica) y son recortados por el borde inferior del Canvas.

**Acceptance Criteria**
- [ ] Nombres de campaña que superen los 12 caracteres son truncados con puntos suspensivos ("...").
- [ ] Se verifica que al mostrar 5+ campañas, las etiquetas no se solapan.
- [ ] El padding inferior del Canvas se escala inteligentemente según la longitud final del texto a mostrar.

---

## [#440] Issue 440: Emojis/íconos se ven rotos al ingresarlos en el formulario de nuevo insumo

**Severidad:** ⚪ UX / Calidad
**Módulo:** Insumos / Formularios
**Archivos afectados:**
- presentation/ui/screen/insumo/FormularioInsumoScreen.kt
- presentation/viewmodel/insumo/FormularioInsumoViewModel.kt

**Descripción**
Room y la Base de Datos nativa soportan emojis, tal como se verifica con los registros del DataSeeder. Sin embargo, al tipear un emoji (especialmente caracteres SMP de Unicode) en el campo "Ícono" del formulario de Nuevo Insumo, este se "rompe" (muestra cuadros vacíos o ??). Esto indica un problema en la captura del estado en el OutlinedTextField, un filtro de expresión regular muy restrictivo, o un maxLength = 1 que recorta a la mitad el Surrogate Pair del emoji.

**Acceptance Criteria**
- [ ] El teclado permite ingresar caracteres Unicode (emojis) complejos.
- [ ] Se ajusta la validación de longitud para considerar los Code Points Unicode, permitiendo que un emoji ocupe "1" espacio visual sin importar su peso en bytes.
- [ ] Al guardar, el emoji se muestra correctamente en el catálogo global.

---
---

# 🔵 NIVEL L5 — MEJORAS Y NUEVOS DESARROLLOS

---

## [#439] Issue 439: Formulario dedicado de Vinculación de Insumos a Campaña desde grid DetalleCampania

**Severidad:** 🔵 Mejora Funcional
**Módulo:** Insumos / Navegación
**Archivos afectados:**
- presentation/ui/screen/insumo/InsumosScreen.kt
- presentation/ui/screen/campania/DetalleCampaniaScreen.kt
- presentation/navigation/NavRoutes.kt
- presentation/ui/screens/screens.kt

**Descripción**
Actualmente, el botón + en el módulo Insumos (dentro de Detalle de Campaña) navega erróneamente a FormularioInsumoScreen, un formulario diseñado para crear nuevos agroquímicos en el catálogo base. El flujo correcto debe invocar una interfaz exclusiva para *vincular* (asignar dosis y costos) un insumo existente a la campaña seleccionada. Esta vista de "vinculación" ya reside como un BottomSheet en InsumosScreen, pero carece de un acceso directo vía URL/NavRoute.

**Acceptance Criteria**
- [ ] Incorporación de la ruta NavRoutes.VincularInsumo(campaniaId: Int) al NavHost general.
- [ ] El botón + en DetalleCampaniaScreen redirecciona hacia la vista de Vinculación con el campaniaId pre-cargado.
- [ ] El formulario despliega el catálogo disponible e incluye los campos "Cantidad Aplicada" y "Costo Unitario".
- [ ] Se mantiene inalterado el botón flotante (o engranaje) de gestión del catálogo global.

---
---

## 🗓️ Orden de Ejecución Sugerido

### Fase 1 — Bugs Funcionales (Sprint 1)
1. **[#441]** Contadores del grid DetalleCampania desactualizados
2. **[#437]** Balance incorrecto + overflow tarjetas Dashboard
3. **[#438]** Gráfico Evolución falla con 1 punto

### Fase 2 — Fix UX (Sprint 1-2)
4. **[#434]** Truncamiento de nombres largos en eje X
5. **[#440]** Emojis rotos en Formulario Insumo

### Fase 3 — Feature (Sprint 2)
6. **[#439]** Formulario dedicado de Vinculación de Insumos

---

## 🛠️ Reglas de Desarrollo

1. **Ramas:** Cada issue se trabaja en su propia rama (eature/, ix/, efactor/).
2. **Commits:** Convención de Conventional Commits (ej: ix(dashboard): formato de moneda en balance).
3. **PRs:** Cada rama se integra vía Pull Request a main.
4. **Tests:** Cada cambio en UseCase o ViewModel requiere actualizar su test unitario correspondiente.
5. **Documentación:** Actualizar CHANGELOG.md tras la conclusión de cada issue.
6. **Referencia:** Mantener estricto uso de la etiqueta de Issue (ej: [#437]) en todos los commits generados.
