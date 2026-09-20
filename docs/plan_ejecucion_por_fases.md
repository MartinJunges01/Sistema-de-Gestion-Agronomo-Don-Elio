# Plan de Ejecución por Fases: Generación de Documentación IA

> **Referencia:** Este documento complementa [`plan_generacion_documentacion.md`](./plan_generacion_documentacion.md) y divide su ejecución en sesiones independientes para evitar el agotamiento de contexto de la IA.

---

## Contexto y Estrategia

El proyecto tiene **18 pantallas** distribuidas en **9 módulos funcionales** y más de **60 casos de uso**. Dado que el archivo de destino (`docs/documentacion_tpi_2da_entrega.md`) ya tiene completas las secciones 1–7.4, el trabajo pendiente se divide en dos grandes bloques:

- **Bloque A — Sección 7.5:** Descripción Técnica de Funcionalidades (código → documentación técnica).
- **Bloque B — Sección 8:** Manual de Usuario (código → guía paso a paso).

Cada fase es **autónoma**: la IA puede ejecutarla en una conversación separada sin necesidad de recordar el contexto de la anterior.

---

## Mapa de Módulos

| Módulo | ViewModels | Pantallas | Casos de Uso clave |
|---|---|---|---|
| **Login / Auth** | `LoginViewModel` | `LoginScreen`, `RegistroScreen` | `LoginUseCase`, `GuardarSesionUseCase`, `CerrarSesionUseCase` |
| **Home / Dashboard** | `HomeViewModel` | `DashboardOperacionesScreen` | `ObtenerTareasDelDiaUseCase`, `ObtenerCampaniasActivasUseCase` |
| **Campañas** | `GestionCampaniasViewModel`, `CampaniaDetailViewModel`, `CampaniaFormViewModel` | `GestionCampaniasScreen`, `DetalleCampaniaScreen`, `FormularioCampaniaScreen` | `CrearCampaniaUseCase`, `EditarCampaniaUseCase`, `FinalizarCampaniaUseCase`, `EliminarCampaniaUseCase` |
| **Tareas** | `TareaViewModel` | `TareasScreen`, `NuevaTareaScreen` | `CrearTareaUseCase`, `EditarTareaUseCase`, `ConfirmarTareaUseCase`, `ObtenerTareasFiltradasUseCase` |
| **Insumos** | `InsumoViewModel` | `CatalogoInsumosScreen`, `FormularioInsumoScreen`, `InsumosScreen`, `VincularInsumoScreen` | `CrearInsumoCatalogoUseCase`, `AsignarInsumoACampaniaUseCase`, `DesvincularInsumoUseCase`, `EditarCampaniaInsumoUseCase` |
| **Cosechas** | `CosechaViewModel` | `CosechasScreen`, `FormularioCosechaScreen` | `EditarCosechaUseCase`, `EditarCosechaConVentaUseCase`, `EliminarCosechaUseCase`, `ObtenerCosechasPorCampaniaUseCase` |
| **Cultivos** | `CultivoViewModel` | `CatalogoCultivosScreen` | `CrearCultivoUseCase`, `EditarCultivoUseCase`, `EliminarCultivoUseCase` |
| **Observaciones** | `ObservacionViewModel` | `ObservacionesScreen` | `GuardarObservacionUseCase`, `EditarObservacionUseCase`, `EliminarObservacionUseCase` |
| **Reportes** | `ReportesViewModel` | `ReportesRendimientoScreen` | `ObtenerResumenRendimientoUseCase`, `ObtenerEvolucionCultivoUseCase`, `ObtenerCumplimientoTareasUseCase`, `CalcularCostoPorHectareaUseCase` |

---

## FASE 0 — Preparación (Humano) ⚙️

> **Responsable:** Equipo humano  
> **Duración estimada:** 5 minutos

**Pasos:**
1. Verificar que `docs/documentacion_tpi_2da_entrega.md` esté actualizado en `main`.
2. Confirmar que las secciones 7.5 y 8 están **vacías** (solo tienen el título/encabezado).
3. Abrir una nueva conversación con la IA para cada fase (no acumular contexto entre fases).

---

## FASE 1 — Módulo Login + Dashboard (Sección 7.5, parte 1)

> **Prompt a enviar a la IA:**

```text
Lee el plan en 'docs/plan_generacion_documentacion.md'. Tu tarea de esta sesión es SOLO documentar
los módulos Login/Auth y Home/Dashboard en la sección 7.5 de 'docs/documentacion_tpi_2da_entrega.md'.

Archivos a analizar:
- presentation/ui/screen/login/LoginScreen.kt
- presentation/ui/screen/login/RegistroScreen.kt
- presentation/ui/screen/home/DashboardOperacionesScreen.kt
- presentation/viewmodel/login/ (todos los .kt)
- presentation/viewmodel/home/ (todos los .kt)
- domain/use_case/LoginUseCase.kt
- domain/use_case/GuardarSesionUseCase.kt
- domain/use_case/CerrarSesionUseCase.kt
- domain/use_case/ObtenerTareasDelDiaUseCase.kt
- domain/use_case/ObtenerCampaniasActivasUseCase.kt

RESTRICCIONES:
- NO modifiques las secciones 1 a 7.4.
- Solo escribe dentro de la sección 7.5, bajo los subtítulos "Login/Autenticación" y "Dashboard".
- Deja un marcador [FIN_FASE_1] al terminar.
```

**Entregable:** Subsecciones 7.5.1 (Login) y 7.5.2 (Dashboard) completadas.

---

## FASE 2 — Módulos Campañas + Tareas (Sección 7.5, parte 2)

> **Prompt a enviar a la IA:**

```text
Lee el plan en 'docs/plan_generacion_documentacion.md'. Tu tarea de esta sesión es SOLO documentar
los módulos Campañas y Tareas en la sección 7.5 de 'docs/documentacion_tpi_2da_entrega.md'.
Las subsecciones anteriores (Login y Dashboard) ya están escritas: NO las toques.

Archivos a analizar:
- presentation/ui/screen/campania/ (todos los .kt)
- presentation/ui/screen/tarea/ (todos los .kt)
- presentation/viewmodel/campania/ (todos los .kt)
- presentation/viewmodel/tarea/ (todos los .kt)
- domain/use_case/CrearCampaniaUseCase.kt, EditarCampaniaUseCase.kt, FinalizarCampaniaUseCase.kt,
  EliminarCampaniaUseCase.kt, CrearTareaUseCase.kt, EditarTareaUseCase.kt,
  ConfirmarTareaUseCase.kt, ObtenerTareasFiltradasUseCase.kt

RESTRICCIONES:
- Solo escribe bajo los subtítulos "Campañas" y "Tareas" dentro de la sección 7.5.
- Deja un marcador [FIN_FASE_2] al terminar.
```

**Entregable:** Subsecciones 7.5.3 (Campañas) y 7.5.4 (Tareas) completadas.

---

## FASE 3 — Módulos Insumos + Cosechas + Cultivos (Sección 7.5, parte 3)

> **Prompt a enviar a la IA:**

```text
Lee el plan en 'docs/plan_generacion_documentacion.md'. Tu tarea de esta sesión es documentar
los módulos Insumos, Cosechas y Cultivos en la sección 7.5 de 'docs/documentacion_tpi_2da_entrega.md'.
Las subsecciones anteriores ya están escritas: NO las toques.

Archivos a analizar:
- presentation/ui/screen/insumo/ (todos los .kt)
- presentation/ui/screen/cosecha/ (todos los .kt)
- presentation/ui/screen/cultivo/ (todos los .kt)
- presentation/viewmodel/insumo/ (todos los .kt)
- presentation/viewmodel/cosecha/ (todos los .kt)
- presentation/viewmodel/cultivo/ (todos los .kt)
- domain/use_case/ → casos de uso de Insumo, Cosecha y Cultivo

RESTRICCIONES:
- Solo escribe bajo los subtítulos "Insumos", "Cosechas" y "Cultivos" dentro de la sección 7.5.
- Deja un marcador [FIN_FASE_3] al terminar.
```

**Entregable:** Subsecciones 7.5.5 (Insumos), 7.5.6 (Cosechas) y 7.5.7 (Cultivos) completadas.

---

## FASE 4 — Módulos Observaciones + Reportes + Config (Sección 7.5, parte 4)

> **Prompt a enviar a la IA:**

```text
Lee el plan en 'docs/plan_generacion_documentacion.md'. Tu tarea de esta sesión es documentar
los módulos Observaciones, Reportes y Configuración en la sección 7.5 de
'docs/documentacion_tpi_2da_entrega.md'. Las subsecciones anteriores ya están escritas: NO las toques.

Archivos a analizar:
- presentation/ui/screen/observacion/ObservacionesScreen.kt
- presentation/ui/screen/reportes/ReportesRendimientoScreen.kt
- presentation/ui/screen/config/ConfiguracionDBScreen.kt
- presentation/viewmodel/observacion/ (todos los .kt)
- presentation/viewmodel/reportes/ (todos los .kt)
- presentation/viewmodel/config/ (todos los .kt)
- domain/use_case/ → casos de uso de Observacion, Reportes (Rendimiento, Evolución, Cumplimiento, CostoPorHa)

RESTRICCIONES:
- Solo escribe bajo los subtítulos "Observaciones", "Reportes de Rendimiento" y "Configuración/Backup"
  dentro de la sección 7.5.
- Al terminar esta fase, la sección 7.5 debe estar completamente redactada.
- Deja un marcador [FIN_FASE_4] al terminar.
```

**Entregable:** Subsecciones 7.5.8 (Observaciones), 7.5.9 (Reportes) y 7.5.10 (Config/Backup) completadas.  
🎯 **La sección 7.5 queda 100% completa.**

---

## FASE 5 — Manual de Usuario, Parte 1: Instalación + Auth + Dashboard (Sección 8)

> **Prompt a enviar a la IA:**

```text
Lee el plan en 'docs/plan_generacion_documentacion.md'. La sección 7.5 ya está completa.
Tu tarea ahora es comenzar la sección 8 (Manual de Usuario) del archivo
'docs/documentacion_tpi_2da_entrega.md'.

Escribe:
- 8.1: Guía de Instalación y Ejecución (basate en el README y en que es una app Android).
- 8.2 (inicio): Cómo iniciar sesión / registrarse (LoginScreen y RegistroScreen).
- 8.2 (continúa): Cómo usar el Dashboard principal (DashboardOperacionesScreen).

Para cada pantalla:
- Descripción de qué hace y para qué sirve.
- Pasos numerados para realizar la acción principal.
- Marcadores [INSERTAR CAPTURA PANTALLA <nombre>] donde corresponda.
- Sección "Resolución de problemas comunes" al final de cada pantalla.

RESTRICCIONES:
- NO modifiques la sección 7 ni ninguna sección anterior.
- Deja un marcador [FIN_FASE_5] al terminar.
```

**Entregable:** Secciones 8.1 y el inicio de 8.2 (Login + Dashboard) completadas.

---

## FASE 6 — Manual de Usuario, Parte 2: Campañas + Tareas + Insumos (Sección 8.2 continúa)

> **Prompt a enviar a la IA:**

```text
Lee el plan en 'docs/plan_generacion_documentacion.md'. Las partes anteriores del Manual (8.1
y el inicio de 8.2) ya están escritas. Continúa la sección 8.2 documentando:

- Gestión de Campañas (GestionCampaniasScreen, DetalleCampaniaScreen, FormularioCampaniaScreen).
- Gestión de Tareas (TareasScreen, NuevaTareaScreen).
- Gestión de Insumos (CatalogoInsumosScreen, FormularioInsumoScreen, InsumosScreen, VincularInsumoScreen).

Para cada pantalla sigue el mismo formato: descripción, pasos numerados, marcadores de captura
y "Resolución de problemas comunes".

RESTRICCIONES:
- No sobreescribas lo que ya está redactado.
- Deja un marcador [FIN_FASE_6] al terminar.
```

**Entregable:** Subsecciones de 8.2 para Campañas, Tareas e Insumos.

---

## FASE 7 — Manual de Usuario, Parte 3: Cosechas + Cultivos + Observaciones + Reportes (Sección 8.2 cierre)

> **Prompt a enviar a la IA:**

```text
Lee el plan en 'docs/plan_generacion_documentacion.md'. Continúa la sección 8.2 documentando
las pantallas restantes:

- Cosechas (CosechasScreen, FormularioCosechaScreen).
- Catálogo de Cultivos (CatalogoCultivosScreen).
- Observaciones (ObservacionesScreen).
- Reportes de Rendimiento (ReportesRendimientoScreen).
- Configuración / Backup (ConfiguracionDBScreen).

Luego escribe:
- 8.3: Resolución de Errores Comunes (errores frecuentes de la app Android que un usuario podría enfrentar).

RESTRICCIONES:
- No sobreescribas lo que ya está redactado.
- Al terminar esta fase, la sección 8 completa estará redactada.
- Deja un marcador [FIN_FASE_7] al terminar.
```

**Entregable:** Cierre de la sección 8.2 y sección 8.3 completas.  
🎯 **La sección 8 (Manual de Usuario) queda 100% completa.**

---

## FASE 8 — Revisión Final y Limpieza (Humano) 🧹

> **Responsable:** Equipo humano  
> **Duración estimada:** 30–60 minutos

**Pasos:**
1. Buscar todos los marcadores `[FIN_FASE_X]` en el documento y eliminarlos.
2. Insertar las capturas de pantalla reales en cada `[INSERTAR CAPTURA PANTALLA <nombre>]`.
3. Revisar coherencia y estilo del documento completo.
4. Exportar a PDF para entregar a la cátedra.

---

## Resumen de Fases

| Fase | Responsable | Sección | Módulos | Desbloqueada por |
|---|---|---|---|---|
| 0 | Humano | — | Preparación | — |
| 1 | IA | 7.5 (parte 1) | Login + Dashboard | Fase 0 |
| 2 | IA | 7.5 (parte 2) | Campañas + Tareas | Fase 1 |
| 3 | IA | 7.5 (parte 3) | Insumos + Cosechas + Cultivos | Fase 2 |
| 4 | IA | 7.5 (parte 4) | Observaciones + Reportes + Config | Fase 3 |
| 5 | IA | 8.1 + 8.2 inicio | Instalación + Login + Dashboard | Fase 4 |
| 6 | IA | 8.2 continúa | Campañas + Tareas + Insumos | Fase 5 |
| 7 | IA | 8.2 cierre + 8.3 | Cosechas + Cultivos + Obs + Reportes | Fase 6 |
| 8 | Humano | Revisión final | Capturas + Limpieza + PDF | Fase 7 |

> **Tiempo estimado total de IA:** ~7 sesiones de ~10–15 minutos cada una.  
> **Fecha límite:** 06/10/2026
