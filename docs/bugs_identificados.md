# Bugs Identificados

> Los issues con ID oficial se encuentran en el Roadmap (`roadmap_iteracion_2.md`).
> Este archivo registra **deuda técnica nueva** detectada durante sesiones de desarrollo, pendiente de subir a GitHub para obtener su ID.

---

## [PENDIENTE-ID] InsumoVinculacionViewModel sin método sincronizarInsumos()

**Severidad:** ⚪ UX / Deuda Técnica
**Módulo:** Insumos / Detalle Campaña / Tabs
**Archivo afectado:** `presentation/viewmodel/insumo/InsumoVinculacionViewModel.kt`

**Descripción**
Durante la resolución del Issue [#292], se identificó que `InsumoVinculacionViewModel` tiene exactamente el mismo patrón de bug que `TareaViewModel`: lee `campaniaId` desde `SavedStateHandle` y no expone un método para sincronizarlo externamente. El fix actual en `DetalleCampaniaScreen.kt` usa la key dinámica `"tab_insumos_$campaniaId"` como solución, pero **no agrega un `LaunchedEffect` con `sincronizarInsumos()`** como segunda línea de defensa (al contrario de `TabTareas`).

Esto podría causar que `TabInsumos` muestre insumos desactualizados en ciertos escenarios de recomposición edge-case.

**Causa Raíz (Código)**
```kotlin
// InsumoVinculacionViewModel.kt — mismo patrón que TareaViewModel:
private val _campaniaIdSeleccionada = MutableStateFlow<Int?>(
    savedStateHandle.get<Int>("campaniaId").takeIf { it != -1 }
)
// No existe método público sincronizarInsumos(id: Int)
```

**Criterios de Aceptación**
- Agregar `fun sincronizarInsumos(id: Int)` en `InsumoVinculacionViewModel` con la misma lógica idempotente de `sincronizarCampania()`.
- Agregar `LaunchedEffect(campaniaId) { vm.sincronizarInsumos(campaniaId) }` en `TabInsumos` dentro de `DetalleCampaniaScreen.kt`.
- Crear `InsumoVinculacionViewModelTest` con casos Given-When-Then equivalentes.

**Origen:** Detectado durante fix del Issue [#292] — 2026-07-22


---

## [PENDIENTE-ID] Exportación CSV/PDF sin identificación de campaña en el encabezado

**Severidad:** 🔵 Mejora de Reportes
**Módulo:** Reportes / Exportación
**Archivo afectado:** `core/utils/ReportExporter.kt`

**Descripción**
Tras el Issue #299, la exportación CSV/PDF es contextual a la campaña seleccionada en la Sección 1 de Reportes. Sin embargo, el título del PDF sigue siendo el genérico "Reporte de Gastos por Insumo" sin mencionar el nombre de la campaña exportada. Si el usuario exporta múltiples campañas, los archivos generados son indistinguibles por su contenido.

Adicionalmente, si el usuario pulsa "Exportar" sin haber seleccionado ninguna campaña, `exportableData` estará vacío y el CSV/PDF se generará vacío sin ninguna advertencia en la UI.

**Causa Raíz (Código)**
```kotlin
// ReportExporter.kt línea 53 — título hardcodeado sin campaña:
canvas.drawText("Reporte de Gastos por Insumo", 50f, 80f, paint)

// ReportesViewModel.kt — sin guardia ante exportableData vacío:
fun exportarReporteCsv(uri: Uri, context: Context) {
    viewModelScope.launch {
        val data = exportableData.value  // puede ser emptyList() sin aviso
        ...
    }
}
```

**Criterios de Aceptación**
- Agregar el nombre de la campaña seleccionada como subtítulo en el PDF (ej. "Campaña: Soja 2026").
- Propagar el nombre de la campaña como parámetro a `ReportExporter.exportToPdf()` y `exportToCsv()`.
- Si `exportableData` está vacío (no hay campaña seleccionada), mostrar un `Snackbar` o `Toast` indicando "Seleccioná una campaña antes de exportar" y NO abrir el selector de archivo.

**Origen:** Detectado durante fix del Issue [#299] — 2026-07-29

---

## [PENDIENTE-ID] Comparador permite seleccionar la misma campaña para A y B

**Severidad:** ⚪ UX / Deuda Técnica
**Módulo:** Reportes / Comparador
**Archivo afectado:** `presentation/ui/screen/reportes/ReportesRendimientoScreen.kt`

**Descripción**
En la Sección 2 (Comparador de Campañas), el usuario puede seleccionar la misma campaña en el dropdown A y en el dropdown B. El resultado es una comparación de una campaña consigo misma, lo que carece de valor informativo y puede confundir al usuario.

**Causa Raíz (Código)**
```kotlin
// DropdownCampania no filtra la campaña ya seleccionada en el otro dropdown.
// ReportesViewModel no valida que campaniaA != campaniaB.
campanias.forEach { campania ->
    DropdownMenuItem(text = { Text(campania.nombre) }, onClick = { ... })
}
```

**Criterios de Aceptación**
- El dropdown de Campaña B debe excluir de su lista la campaña ya seleccionada en A (y viceversa).
- Alternativa: mostrar un `Card` de advertencia "Las campañas seleccionadas son iguales" en lugar de las métricas si `campaniaA.id == campaniaB.id`.

**Origen:** Detectado durante fix del Issue [#299] — 2026-07-29

---

## [PENDIENTE-ID] ReportExporter no soporta PDF de múltiples páginas

**Severidad:** 🔵 Mejora de Reportes
**Módulo:** Reportes / Exportación
**Archivo afectado:** `core/utils/ReportExporter.kt`

**Descripción**
El `ReportExporter.exportToPdf()` tiene una sola página fija (A4, 595×842pt). Si la lista de insumos de una campaña supera los ≈22 ítems, el contenido se trunca silenciosamente sin generar nuevas páginas ni advertir al usuario. El propio código tiene un comentario interno reconociendo esta limitación.

**Causa Raíz (Código)**
```kotlin
// ReportExporter.kt líneas 81-85:
if (yPosition > 800f) {
    pdfDocument.finishPage(page)
    // Para simplificar, omitiremos el multi-page en esta versión inicial básica,
    // asumiendo que los insumos entran en una sola página.
}
```
El bloque finaliza la página pero no crea una nueva, por lo que los ítems restantes no se dibujan.

**Criterios de Aceptación**
- Implementar lógica de paginación: cuando `yPosition > 800f`, finalizar la página actual, crear una nueva con `pdfDocument.startPage()` y continuar desde la coordenada Y inicial.
- La nueva página debe repetir el encabezado de columnas (Insumo, Cantidad, Total).
- El Total Final debe aparecer únicamente en la última página.

**Origen:** Detectado durante fix del Issue [#299] — 2026-07-29

---

---

## [PENDIENTE-ID] DoubleBarIndicator no tiene tests unitarios

**Severidad:** ⚪ UX / Deuda Técnica
**Módulo:** Reportes / Comparador
**Archivo afectado:** `presentation/ui/screen/reportes/ReportesRendimientoScreen.kt`

**Descripción**
El componente `DoubleBarIndicator` (añadido en #302) no tiene ninguna prueba unitaria ni instrumentada que valide su comportamiento. Los casos de borde más críticos son: `maxA = 0f` (división por cero, actualmente cubierta por el guard `if (maxA > 0f)`) y el caso donde `valueA > valueB`.

**Criterios de Aceptación**
- Crear una prueba instrumentada o test de Compose que valide que el `DoubleBarIndicator` no crashea cuando `maxA = 0f` o `maxB = 0f`.
- Verificar que las barras tienen la proporción correcta (la de mayor valor llega al 100%).

**Origen:** Detectado durante auditoría de implementación del Issue [#302] — 2026-08-02

---

## [PENDIENTE-ID] ReportesViewModel no valida que campaña A ≠ campaña B

**Severidad:** ⚪ UX / Deuda Técnica
**Módulo:** Reportes / Comparador
**Archivo afectado:** `presentation/viewmodel/reportes/ReportesViewModel.kt`

**Descripción**
Según el plan de implementación del Issue #302, se debía agregar una validación `campaniaA?.id != campaniaB?.id` que emitiera un estado `comparacionInvalida: Boolean`. Esta validación **no fue implementada**. El usuario puede seleccionar la misma campaña en A y en B, obteniendo una comparación carente de valor (una campaña contra sí misma). Esta deuda estaba también registrada como "PEND-3" antes de la sesión pero no se resolvió.

**Criterios de Aceptación**
- El dropdown de Campaña B debe excluir de su lista la campaña ya seleccionada en A (y viceversa). **O bien:**
- Agregar un `val comparacionInvalida: StateFlow<Boolean>` al ViewModel que emita `true` cuando `_campaniaA.value?.id == _campaniaB.value?.id && ambas != null`.
- En la UI, mostrar un `Card` de advertencia (en lugar de las métricas) cuando `comparacionInvalida = true`.
- Agregar test VM-R12 al plan de pruebas.

**Origen:** Detectado durante auditoría de implementación del Issue [#302] — 2026-08-02

---

## [PENDIENTE-ID] Tests VM-R8 y VM-R9 (guardia de exportación #300) no tienen implementación

**Severidad:** ⚪ Cobertura / Deuda Técnica
**Módulo:** Reportes / Exportación
**Archivo afectado:** `test/.../ReportesViewModelTest.kt`

**Descripción**
Los casos de prueba VM-R8 y VM-R9 (guardia de exportación sin campaña seleccionada) están documentados en `plan_de_pruebas.md` pero **no tienen su implementación correspondiente** en `ReportesViewModelTest.kt`. La lógica de guardia sí está implementada en producción, pero no tiene cobertura automatizada.

**Criterios de Aceptación**
- Implementar `exportarReporteCsv emite error si no hay campania seleccionada()` en `ReportesViewModelTest`.
- Implementar `exportarReportePdf emite error si no hay campania seleccionada()` en `ReportesViewModelTest`.
- Ambos tests deben verificar que `exportStatus` emite el mensaje correcto.

**Origen:** Detectado durante auditoría de implementación del Issue [#300] — 2026-08-02

---

## Convención de Registro
Cada vez que identifiques un bug, usa la siguiente plantilla. Luego súbelo a GitHub para obtener su ID oficial y reemplaza `[#ID]` con el número correspondiente. Evita usar numeraciones locales arbitrarias (como "Issue 1", "Issue 2").

---
## [#ID] Título Corto del Bug

**Severidad:** 🔴 CRASH | 🟠 Bug Funcional | 🟡 Feature Faltante | 🔵 Mejora de Reportes | ⚪ UX
**Módulo:** Nombre del módulo
**Archivo afectado:** `ruta/al/archivo.kt`

**Descripción**
Descripción detallada de qué sucede y por qué.

**Causa Raíz (Código)**
```kotlin
// Fragmento de código problemático si es conocido
```

**Acceptance Criteria**
- [ ] Criterio de negocio/usuario 1
- [ ] Criterio de negocio/usuario 2

**Sub-issues / Tareas Técnicas**
- [ ] Tarea técnica 1 (ej. Modificar UseCase)
- [ ] Tarea técnica 2 (ej. Actualizar ViewModel)
---
## [RESUELTO] Doble Validación de Fecha en CrearCampaniaUseCase

**Estado:** Resuelto en PR #321

**Severidad:** 🟠 Bug Funcional (violación DRY)
**Módulo:** Campañas / Domain
**Archivo afectado:** `domain/use_case/CrearCampaniaUseCase.kt`

**Descripción**
La validación de fecha no puede ser anterior a hoy está duplicada en `CrearCampaniaUseCase` y en el nuevo `ValidarDatosCampaniaUseCase`. Si los mensajes divergen, el usuario verá un error diferente según el path de ejecución.

**Causa Raíz (Código)**
```kotlin
// DUPLICADO — la misma validación también existe en ValidarDatosCampaniaUseCase
```

**Acceptance Criteria**
- [x] Eliminar el bloque de validación de fecha de `CrearCampaniaUseCase`.
- [x] El UseCase de creación solo debe validar que el nombre no esté en blanco (validación mínima de integridad).
- [x] La pre-validación completa queda exclusivamente en `ValidarDatosCampaniaUseCase`.

---

## [RESUELTO] isGuardarHabilitado Bloqueado en Modo Edición de Insumo

**Estado:** Resuelto en PR #321

**Severidad:** 🟠 Bug Funcional (UX bloqueante)
**Módulo:** Insumos / Presentation
**Archivo afectado:** `presentation/viewmodel/insumo/FormularioInsumoViewModel.kt`

**Descripción**
Al editar un insumo existente, `cargarInsumo()` no llama a `evaluarValidaciones()` tras restaurar los campos. El estado `isGuardarHabilitado` permanece `false` y el botón "Guardar" aparece deshabilitado hasta que el usuario modifique manualmente algún campo.

**Acceptance Criteria**
- [x] `cargarInsumo()` debe invocar `evaluarValidaciones(it.nombre, it.categoria)` después de restaurar los campos.

---

## [RESUELTO] KDoc Faltante en ValidarDatosCampaniaUseCase y ValidarInsumoUseCase

**Estado:** Resuelto en PR #321

**Severidad:** ⚪ Deuda Técnica / Calidad
**Módulo:** Domain / Use Cases

**Descripción**
El `prompt_operativo.md` y la skill `don-elio-workflow` exigen KDoc en todas las funciones públicas de los UseCases. Los dos UseCases nuevos no tienen ningún bloque de documentación.

**Acceptance Criteria**
- [x] Agregar KDoc describiendo parámetros, retorno y comportamiento a ambos UseCases.

---

## [PENDIENTE-ID] UX Inconsistente de Validación entre Formulario de Insumos y Campañas

**Severidad:** ⚪ UX / Deuda Técnica
**Módulo:** Presentation / Formularios

**Descripción**
El formulario de insumos valida en tiempo real (el botón se habilita/deshabilita dinámicamente). El formulario de campañas valida solo al presionar "Guardar" (los errores aparecen a posteriori). Debería estandarizarse el comportamiento.

**Acceptance Criteria**
- [ ] Definir un estándar de UX para validación en el equipo.
- [ ] Aplicar el mismo patrón en ambos formularios.

---
