# Roadmap: Iteración 5 — Estabilización Post-Testing, Fixes de UI y Arquitectura de Insumos

> **Fuente:** Verificación manual en emulador — 2026-09-09 (post-merge Iteración 4).
>
> **Iteración anterior:** Todos los issues del oadmap_iteracion_4.md fueron completados. Este roadmap cubre los hallazgos del testing manual y la deuda técnica pendiente relevada.

---

## Checklist de Progreso

### 🟠 NIVEL L2 — BUGS FUNCIONALES
- [ ] **[#437] Issue 437:** Balance del Dashboard muestra valor incorrecto y tarjetas tienen overflow de texto
- [ ] **[#438] Issue 438:** Grafico de Evolucion Historica falla visualmente con un solo punto de datos (1 campania)
- [ ] **[#441] Issue 441:** Contadores de Tareas completadas y Cosechas en grid DetalleCampania no se actualizan

### ⚪ NIVEL L3 — FIX UI / UX
- [ ] **[#440] Issue 440:** Emojis/iconos se ven rotos al ingresarlos en el formulario de nuevo insumo

### 🔵 NIVEL L5 — MEJORAS Y NUEVOS DESARROLLOS
- [ ] **[#439] Issue 439:** Formulario dedicado de Vinculacion de Insumos a Campania desde grid DetalleCampania

---

## Orden de Ejecucion Sugerido

### Fase 1 — Bugs Funcionales (Sprint 1)
1. **[#441]** Contadores del grid DetalleCampania desactualizados (fix rapido en ViewModels)
2. **[#437]** Balance incorrecto + overflow tarjetas Dashboard (fix formateador + altura fija + logica de calculo)
3. **[#438]** Grafico Evolucion falla con 1 punto (fix matematico en Canvas)

### Fase 2 — Fix UX (Sprint 1-2)
4. **[#440]** Emojis rotos en Formulario Insumo (revisar input de teclado y maxLength del TextField)

### Fase 3 — Feature (Sprint 2)
5. **[#439]** Formulario dedicado de Vinculacion de Insumos (feature nueva - requiere nueva ruta + pantalla)

---

## Detalles de Issues

### [#437] Balance del Dashboard y overflow de tarjetas
**Archivos:** ObtenerResumenRendimientoUseCase.kt, DashboardOperacionesScreen.kt
**Causa:** NumberFormat AR pone el signo - al final; maxLines=1 trunca antes del signo. Tambien hay un error en el calculo / truncamiento de limites de numeros grandes. Tarjetas sin altura minima fija.
**AC:** Balance calculado y mostrado correctamente, tarjetas de altura uniforme, formato abreviado para valores grandes ($6.3M), test unitario.

### [#438] Grafico Evolucion con 1 punto
**Archivo:** ReportesRendimientoScreen.kt (Canvas)
**Causa:** Con size==1, x = paddingLeft + 0*stepX = paddingLeft (extremo izquierdo). paddingBottom=120f excesivo.
**AC:** Punto centrado con 1 dataset, etiquetas Eje X dentro del Card, sin regresion con 2+ campanias.

### [#439] Formulario Vinculacion Insumos
**Archivos:** InsumosScreen.kt, DetalleCampaniaScreen.kt, NavRoutes.kt, screens.kt
**Causa:** Boton + navega al catalogo global en vez de al formulario de vinculacion. El BottomSheet de InsumosScreen ya tiene la logica pero no es accesible por ruta.
**AC:** Nueva ruta VincularInsumo(campaniaId), buscador de catalogo + campos Cantidad/Precio, campaniaId precargado.

### [#440] Emojis corruptos en Formulario Insumo
**Archivos:** FormularioInsumoScreen.kt, FormularioInsumoViewModel.kt
**Causa:** El DataSeeder los guarda bien, pero al tipear desde el teclado en el TextField se rompen (posible limite de maxLength=1 que corta los surrogates UTF-16, o filtrado de caracteres).
**AC:** Permitir ingresar emojis complejos por teclado, validacion correcta de longitud de caracteres Unicode.

### [#441] Contadores desactualizados en grid
**Archivos:** DetalleCampaniaScreen.kt (CardModuloTareas, CardModuloCosechas), TareaViewModel.kt, CosechaViewModel.kt
**Causa:** Condicion de carrera - StateFlow emite emptyList() antes de que seleccionarCampania actualice el ID.
**AC:** Tareas muestra N pendientes + M completadas, Cosechas muestra total + Kg, actualizacion en tiempo real.
