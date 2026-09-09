# Bugs Identificados

> Los issues con ID oficial se encuentran en el Roadmap (`.context/roadmap_iteracion_4.md`).
> Este archivo registra **deuda técnica nueva** detectada durante sesiones de desarrollo, pendiente de subir a GitHub para obtener su ID.

---

<!-- Plantilla para nuevos bugs:
## [PENDIENTE-ID] Título descriptivo del bug

**Severidad:** 🔴 Bug Bloqueante | 🟠 Bug Funcional | 🔵 UX / Deuda Técnica
**Módulo:** [Ej: Insumos / Tareas / Sincronización]
**Archivo afectado:** \ruta/del/archivo.kt

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

## ✅ RESUELTOS EN ITERACIÓN 4

---

## [#413] fix(auth): nombre de usuario muestra Invitado tras primer registro

**Severidad:** 🟠 Bug Funcional
**Módulo:** Autenticación / Sesión
**Archivo afectado:** presentation/viewmodel/login/LoginViewModel.kt

**Criterios de Aceptación**
- [x] Al completar el registro por primera vez, el Dashboard muestra el nombre real del usuario.
- [x] El HomeViewModel.userName refleja el nombre sin necesidad de logout/login.
- [x] Test unitario: registro() exitoso -> sessionManager.saveUserName() es llamado con el nombre correcto.

✅ **Resuelto en PR #430** — `GuardarSesionUseCase` creado e integrado en `LoginViewModel`.

---

## [#414] fix(dashboard): tareas del dia actual se marcan en rojo en el Dashboard

**Severidad:** 🟠 Bug Funcional
**Módulo:** Dashboard / Tareas
**Archivo afectado:** presentation/ui/screen/home/DashboardOperacionesScreen.kt

**Criterios de Aceptación**
- [x] Tarea creada para hoy (cualquier hora) -> NO aparece en rojo en el Dashboard.
- [x] Tarea creada para ayer o antes -> SI aparece en rojo.
- [x] Tarea creada para mañana -> aparece en blanco.
- [x] Test unitario que valide los 3 casos anteriores contra la función de comparación.

✅ **Resuelto** — `DashboardOperacionesScreen` usa `getStartOfDay()` como umbral de comparación.

---

## [#415] ux(campanias): rediseño de DetalleCampaniaScreen con grid 2xN y botones de accion rapida

**Severidad:** 🔵 Mejora UX
**Módulo:** Campañas / Detalle

**Criterios de Aceptación**
- [x] El ScrollableTabRow y el contenido embebido de tabs son eliminados.
- [x] Grid 2xN con botones visibles en pantalla.
- [x] Cada botón muestra un subtexto con el contador correcto.
- [x] El botón + navega directamente al formulario con campaniaId.
- [x] El tap en el card navega a la pantalla de listado.

✅ **Resuelto en PR #436** — `DetalleCampaniaScreen` rediseñada con `LazyVerticalGrid` 2 columnas.

---

## [#416] ux(formularios): conservar campaña seleccionada al acceder desde BottomNav

**Severidad:** 🔵 Mejora UX
**Módulo:** Formularios / Sesión

**Criterios de Aceptación**
- [x] Formularios desde BottomNav muestran preseleccionada la última campaña usada.
- [x] Cambio manual de campaña se persiste como la última.
- [x] Sin interferir con la navegación desde DetalleCampania (campaniaId explícito).

✅ **Resuelto en PR #433** — `UltimaSeleccionManager` implementado e inyectado en ViewModels relevantes.

---

## [#417] ux(navegacion): planteamiento para reducir clics de acceso a cosechas, observaciones y tareas

**Severidad:** 🔵 Mejora UX
**Módulo:** Navegación / UX Global

**Criterios de Aceptación**
- [x] Flujo de creación desde Detalle de Campaña no supera 3 clics.
- [x] Flujo desde BottomNav no requiere re-seleccionar campaña si ya fue usada.
- [x] Documentar en docs/plan_de_pruebas.md los flujos GWT de los 3 escenarios.

✅ **Resuelto** — Implementado con el rediseño de #415 y la persistencia de #416.

---

## [PENDIENTE-ID] fix(tareas): campo `confirmar` se resetea a `false` al editar tarea completada

**Severidad:** 🟠 Bug Funcional
**Módulo:** Tareas / ABM
**Archivo afectado:** `presentation/viewmodel/tarea/NuevaTareaViewModel.kt`

**Criterios de Aceptación**
- [x] El `NuevaTareaFormState` incluye un campo `confirmar: Boolean` que se carga en `cargarTarea()`.
- [x] Al guardar en modo edición, `confirmar` toma el valor del state (no un literal `false`).
- [x] Test unitario: editar tarea completada → `confirmar` permanece `true` tras guardar.

✅ **Resuelto en commit `b7ac1c6`** — rama `fix/436-dts-pre-merge`, sesión 2026-09-08.

---

## [PENDIENTE-ID] ux(observaciones): botón `+` en card de Observaciones navega al listado en vez de al formulario de alta

**Severidad:** 🔵 UX / Deuda Técnica
**Módulo:** Campañas / DetalleCampaniaScreen

**Criterios de Aceptación**
- [x] El botón `+` en la card de Observaciones recibe un callback semánticamente separado (`onGoToNuevaObservacion`).
- [x] El comportamiento es consistente con Tareas, Insumos y Cosechas.

✅ **Resuelto parcialmente en commit `b7ac1c6`** — `CardModuloObservaciones` tiene callbacks separados. Destino de navegación es el listado con formulario inline (diseño actual).

---

## [PENDIENTE-ID] test(insumos/tareas): tests unitarios faltantes para AC de issues #403 y #410

**Severidad:** 🔵 Deuda Técnica (Testing)
**Módulo:** Insumos / Tareas

**Criterios de Aceptación**
- [x] Crear `FormularioInsumoViewModelTest` con caso Given-When-Then para modo creación.
- [x] Agregar casos de edición y eliminación a `NuevaTareaViewModelTest` / `TareaViewModelTest`.
- [x] Documentar los nuevos casos GWT en `docs/plan_de_pruebas.md`.
- [x] Todos los tests pasan con `./gradlew test`.

✅ **Resuelto en commit `b7ac1c6`** — sesión 2026-09-08. `BUILD SUCCESSFUL`.

---

## 🔴 DEUDA TÉCNICA PENDIENTE — Iteración 5

> Detectada durante verificación manual en emulador — 2026-09-08/09.

---

## [PENDIENTE-ID] fix(dashboard): cálculo de Balance incorrecto — posible overflow de tipo numérico

**Severidad:** 🟠 Bug Funcional
**Módulo:** Dashboard / Resumen Financiero
**Archivos afectados:**
- `domain/use_case/ObtenerResumenRendimientoUseCase.kt`
- `presentation/ui/screen/home/DashboardOperacionesScreen.kt`

**Descripción**
En el Dashboard, la tarjeta "Balance" muestra un valor incorrecto cuando el balance es negativo. Con Capital Invertido de $6.383.500 e Ingresos Brutos de $250.000, el Balance debería ser aproximadamente -$6.133.500 (negativo), pero en pantalla se muestra $249.999.99 (positivo). El cálculo `balance = ingresosBrutos - capitalInvertido` en el UseCase usa `Double`, pero el formateo con `NumberFormat.getCurrencyInstance(Locale("es", "AR"))` puede estar produciendo desbordamiento o truncamiento al convertir el resultado negativo a la representación de moneda argentina.

**Causa Raíz Probable (Código)**
```kotlin
// ObtenerResumenRendimientoUseCase.kt
val balance = ingresosBrutos - capitalInvertido // ← correcto en lógica

// DashboardOperacionesScreen.kt
val formatMoneda = java.text.NumberFormat.getCurrencyInstance(Locale("es", "AR"))
Text(formatMoneda.format(resumen.balance), ..., maxLines = 1) // ← maxLines=1 trunca texto largo
```
El problema probable es que `NumberFormat` de Argentina puede formatear valores negativos con el símbolo `-` al final (ej: `$ 6.133.500,00-`) y el `maxLines = 1` junto al `fontSize = 14.sp` dentro de la tarjeta pequeña hace que el texto largo se trunque antes de mostrar el signo, resultando visualmente en un número positivo incompleto.

**Criterios de Aceptación**
- [ ] El Balance negativo se muestra correctamente como valor negativo en la tarjeta (ej: `- $6.133.500`).
- [ ] Las 3 tarjetas (Capital, Ingresos, Balance) tienen altura uniforme independientemente del largo del número.
- [ ] Para valores grandes, se aplica formato abreviado (ej: `$6,3M`) o se ajusta el `fontSize` automáticamente.
- [ ] Test unitario en `ObtenerResumenRendimientoUseCaseTest` validando balance negativo.

---

## [PENDIENTE-ID] fix(dashboard): tarjetas de resumen financiero con altura inconsistente y overflow de texto

**Severidad:** 🔵 UX / Deuda Técnica (UI)
**Módulo:** Dashboard / Resumen Financiero
**Archivo afectado:** `presentation/ui/screen/home/DashboardOperacionesScreen.kt`

**Descripción**
Las 3 tarjetas del Resumen Financiero (Capital Invertido, Ingresos Brutos, Balance) muestran alturas diferentes cuando los valores numéricos son largos. En pantalla, "Capital Invertido" con valor `$ 6.883.500,00` hace que el texto haga salto de línea dentro de la tarjeta (se muestra `6883500.` en una línea y `00` en la siguiente). Esto provoca que esa tarjeta sea más alta que las otras dos, rompiendo la simetría visual del grid horizontal.

**Causa Raíz (Código)**
```kotlin
// CardResumen en DashboardOperacionesScreen.kt
Text(valor, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = valorColor, maxLines = 1)
// El problema es que fontSize = 14.sp con valores como "$ 6.883.500,00" excede el ancho
// disponible en cada tarjeta (1/3 del ancho de pantalla menos padding)
```
El `maxLines = 1` en teoría debería truncar, pero la combinación con `weight(1f)` en la Row no impone un tamaño mínimo fijo en la Column interna.

**Criterios de Aceptación**
- [ ] Las 3 tarjetas siempre tienen la misma altura, independientemente del largo del valor.
- [ ] Los valores numéricos se muestran completos o en formato abreviado (ej: `$6,3M`).
- [ ] Unificar solución con el issue del cálculo de Balance (issue anterior).

---

## [PENDIENTE-ID] fix(reportes): gráfico de Evolución Histórica por Cultivo falla con dataset de 1 campaña

**Severidad:** 🟠 Bug Funcional
**Módulo:** Reportes / Gráfico de Evolución
**Archivo afectado:** `presentation/ui/screen/reportes/ReportesRendimientoScreen.kt`

**Descripción**
El gráfico Canvas de "Evolución Histórica por Cultivo" tiene un bug matemático cuando el dataset contiene exactamente 1 campaña finalizada para el cultivo seleccionado. En lugar de mostrar un punto centrado en el gráfico, la visualización es incorrecta: solo se ve un punto verde arrinconado en la esquina superior izquierda, una línea vertical cortada, y la etiqueta del nombre de la campaña en el Eje X aparece pegada al margen inferior izquierdo, rotada y visible solo parcialmente.

**Causa Raíz (Código)**
```kotlin
// ReportesRendimientoScreen.kt - dentro del Canvas
val stepX = if (evolucion.size > 1) width / (evolucion.size - 1) else width
// Cuando size == 1, stepX = width, pero el punto se dibuja en:
val x = paddingLeft + index * stepX  // index=0 → x = paddingLeft (extremo izquierdo)

// Y el paddingBottom de 120f es excesivo para el área de etiquetas rotadas,
// haciendo que el texto quede fuera del área visible del Card
```

**Criterios de Aceptación**
- [ ] Con 1 campaña en el dataset, el punto se renderiza centrado horizontal y verticalmente en el área del gráfico.
- [ ] Las etiquetas del Eje X no se desbordan fuera del área visible del Card.
- [ ] Con 0 campañas, se muestra el placeholder "Sin datos históricos" (ya implementado).
- [ ] Con 2+ campañas, el comportamiento actual se mantiene igual.

---

## [PENDIENTE-ID] feat(insumos): formulario dedicado de Vinculación de Insumos a Campaña

**Severidad:** 🔵 Mejora UX / Deuda de Arquitectura
**Módulo:** Insumos / Campañas / Navegación
**Archivos afectados:**
- `presentation/ui/screen/insumo/InsumosScreen.kt`
- `presentation/ui/screen/campania/DetalleCampaniaScreen.kt`
- `presentation/navigation/NavRoutes.kt`
- `presentation/ui/screens/screens.kt`

**Descripción**
El botón `+` de la tarjeta "Insumos" en el grid 2xN de Detalle de Campaña actualmente navega al `FormularioInsumoScreen` (alta en catálogo global). El flujo correcto debería ser navegar a una pantalla/diálogo dedicado para **vincular un insumo del catálogo existente** a la campaña actual, donde el usuario seleccione qué insumo, qué cantidad y a qué precio lo aplicó. Esta funcionalidad de vinculación ya existe parcialmente en `InsumosScreen` como un `BottomSheet` interno, pero no es accesible directamente desde el grid de DetalleCampaniaScreen.

**Criterios de Aceptación**
- [ ] El botón `+` de Insumos en `DetalleCampaniaScreen` navega a un formulario de Vinculación (puede ser pantalla dedicada o BottomSheet accesible por ruta).
- [ ] El formulario de Vinculación muestra un buscador del catálogo de insumos y campos para Cantidad y Precio.
- [ ] El `campaniaId` se pre-carga automáticamente desde la navegación.
- [ ] La pantalla `InsumosScreen` mantiene el acceso al catálogo global (engranaje ⚙️) sin cambios.
- [ ] Agregar ruta `NavRoutes.VincularInsumo(campaniaId)` al NavHost.

---

## [PENDIENTE-ID] fix(encoding): emojis e íconos de insumos se guardan como caracteres rotos en Room

**Severidad:** 🔵 Deuda Técnica (UI / Base de Datos)
**Módulo:** Insumos / Base de Datos / DataSeeder
**Archivos afectados:**
- `app/src/debug/java/com/itec/donelio/data/seed/DataSeederImpl.kt`
- `data/local/entity/InsumoEntity.kt`
- `presentation/ui/screen/insumo/CatalogoInsumosScreen.kt` (renderizado del ícono)

**Descripción**
Los emojis/íconos del campo `icono` en `InsumoEntity` (ej: 🌿, 🌱, 💊) se guardan en Room como caracteres corruptos (`??`) cuando se leen a través de PowerShell o se insertan desde el `DataSeederImpl`. Al renderizarlos en la UI, los íconos se muestran como cuadros vacíos o signos de interrogación. El problema de codificación puede ser de dos tipos: (1) el archivo `.kt` del DataSeeder no está guardado como UTF-8 con BOM, o (2) Room/SQLite no está configurado para manejar caracteres Unicode fuera del plano básico (emoji son caracteres de 4 bytes, superiores a U+FFFF).

**Causa Raíz Probable**
Los emojis son caracteres Unicode `Supplementary Multilingual Plane` (SMP) que requieren pares sustitutos en UTF-16. SQLite de Android los soporta si la codificación de la DB está en UTF-8, pero la lectura/escritura del archivo fuente puede perderlos si el IDE los guarda incorrectamente.

**Criterios de Aceptación**
- [ ] Verificar el encoding del archivo `DataSeederImpl.kt` (debe ser UTF-8 sin BOM).
- [ ] Comprobar si Room puede guardar y recuperar emojis correctamente con una prueba instrumentada (`androidTest`).
- [ ] Si Room no soporta los emojis del plano SMP, reemplazar la estrategia de íconos por `Icons.Default.*` de Material (vector drawables), eliminando el campo `icono: String` del modelo.
- [ ] Los íconos de insumos se ven correctamente en `CatalogoInsumosScreen` y `InsumosScreen`.


---

<!-- Plantilla para nuevos bugs:
## [PENDIENTE-ID] Título descriptivo del bug

**Severidad:** 🔴 Bug Bloqueante | 🟡 Bug Funcional | 🔵 UX / Deuda Técnica
**Módulo:** [Ej: Insumos / Tareas / Sincronización]
**Archivo afectado:** \ruta/del/archivo.kt

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

## [#413] fix(auth): nombre de usuario muestra Invitado tras primer registro

**Severidad:** 🟠 Bug Funcional
**Módulo:** Autenticación / Sesión
**Archivo afectado:** presentation/viewmodel/login/LoginViewModel.kt

**Descripción**
En LoginViewModel.registro(), el flujo llama a registroUseCase() y emite registroExitoso = true, pero nunca persiste el nombre en sesión usando sessionManager.saveUserName(nombre). Al ingresar por primera vez, el Dashboard muestra "Invitado".

**Causa Raíz (Código)**
`kotlin
fun registro(nombre: String, nombreUsuario: String, contrasena: String) {
    viewModelScope.launch {
        registroUseCase(nombre, nombreUsuario, contrasena)
        _state.update { it.copy(isLoading = false, registroExitoso = true) }
        // sessionManager.saveUserName(nombre) <-- FALTA
    }
}
`

**Criterios de Aceptación**
- [ ] Al completar el registro por primera vez, el Dashboard muestra el nombre real del usuario.
- [ ] El HomeViewModel.userName refleja el nombre sin necesidad de logout/login.
- [ ] Test unitario: registro() exitoso -> sessionManager.saveUserName() es llamado con el nombre correcto.

## [#414] fix(dashboard): tareas del dia actual se marcan en rojo en el Dashboard

**Severidad:** 🟠 Bug Funcional
**Módulo:** Dashboard / Tareas
**Archivo afectado:** presentation/ui/screen/home/DashboardOperacionesScreen.kt

**Descripción**
La comparación usa timestamps exactos en vez de comparar por día calendario. Una tarea de "hoy" que ya pasó en hora pero no en fecha se considera vencida y se marca en rojo.

**Causa Raíz (Código)**
`kotlin
// DashboardOperacionesScreen.kt
val hoy = System.currentTimeMillis() // Timestamp exacto
val isVencida = tarea.fecha < hoy    // ❌ Tarea de hoy a las 15:30 -> true
`

**Criterios de Aceptación**
- [ ] Tarea creada para hoy (cualquier hora) -> NO aparece en rojo en el Dashboard.
- [ ] Tarea creada para ayer o antes -> SI aparece en rojo.
- [ ] Tarea creada para mañana -> aparece en blanco.
- [ ] Test unitario que valide los 3 casos anteriores contra la función de comparación.

## [#415] ux(campanias): rediseño de DetalleCampaniaScreen con grid 2xN y botones de accion rapida

**Severidad:** 🔵 Mejora UX
**Módulo:** Campañas / Detalle
**Archivo afectado:** presentation/ui/screen/campania/DetalleCampaniaScreen.kt

**Descripción**
Reemplazar el ScrollableTabRow por un grid de 2 columnas x N filas de botones rectangulares. Cada botón incluye un botón + secundario visible que navega directamente al formulario de esa entidad (pantalla separada) pre-cargado con el campaniaId. Al presionar el botón principal navega a la pantalla de listado.

**Criterios de Aceptación**
- [ ] El ScrollableTabRow y el contenido embebido de tabs son eliminados.
- [ ] Grid 2xN con botones visibles en pantalla.
- [ ] Cada botón muestra un subtexto con el contador correcto.
- [ ] El botón + navega directamente al formulario con campaniaId.
- [ ] El tap en el card navega a la pantalla de listado.

## [#416] ux(formularios): conservar campaña seleccionada al acceder desde BottomNav

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

---

## [PENDIENTE-ID] fix(tareas): campo `confirmar` se resetea a `false` al editar tarea completada

**Severidad:** 🟠 Bug Funcional
**Módulo:** Tareas / ABM
**Archivo afectado:** `presentation/viewmodel/tarea/NuevaTareaViewModel.kt`

**Descripción**
En `NuevaTareaViewModel.guardar()`, modo edición, el campo `confirmar` del objeto `Tarea` se construye con el valor literal `false` en lugar de preservar el estado actual de la tarea cargada. Si el usuario edita el nombre de una tarea ya marcada como completada, el guardado la regresará al estado pendiente.

**Causa Raíz (Código)**
```kotlin
// NuevaTareaViewModel.kt — modo edición
val tareaEditada = Tarea(
    id = tareaId,
    nombre = current.nombre.trim(),
    fecha = current.fecha,
    hora = current.hora,
    notificar = current.notificar,
    confirmar = false, // ❌ Hardcodeado — debería preservar el estado original
    idCampania = current.campaniaId
)
```

**Criterios de Aceptación**
- [ ] El `NuevaTareaFormState` incluye un campo `confirmar: Boolean` que se carga en `cargarTarea()`.
- [ ] Al guardar en modo edición, `confirmar` toma el valor del state (no un literal `false`).
- [ ] Test unitario: editar tarea completada → `confirmar` permanece `true` tras guardar.

---

## [PENDIENTE-ID] ux(observaciones): botón `+` en card de Observaciones navega al listado en vez de al formulario de alta

**Severidad:** 🔵 UX / Deuda Técnica
**Módulo:** Campañas / DetalleCampaniaScreen
**Archivo afectado:** `presentation/ui/screen/campania/DetalleCampaniaScreen.kt`

**Descripción**
En `CardModuloObservaciones`, el parámetro `onQuickAddClick` recibe el mismo lambda que `onCardClick` (navega al listado de observaciones). Esto rompe la consistencia del grid 2xN del Issue #415, donde el botón `+` debe navegar directamente al formulario de alta precargado con `campaniaId`.

**Causa Raíz (Código)**
```kotlin
// DetalleCampaniaScreen.kt
private fun CardModuloObservaciones(..., onGoToObservaciones: () -> Unit) {
    ModuloCardBase(
        onCardClick = onGoToObservaciones,
        onQuickAddClick = onGoToObservaciones // ❌ Mismo destino que el card principal
    )
}
```

**Criterios de Aceptación**
- [ ] El botón `+` en la card de Observaciones abre el diálogo de nueva observación directamente (o navega a la pantalla correspondiente).
- [ ] El comportamiento es consistente con Tareas, Insumos y Cosechas.

---

## [PENDIENTE-ID] dt(permisos): verificación de permiso de cámara hardcodeada en composable

**Severidad:** 🔵 UX / Deuda Técnica (Arquitectura)
**Módulo:** Observaciones / Edición
**Archivo afectado:** `presentation/ui/screen/observacion/ObservacionesScreen.kt`

**Descripción**
En `DialogEditarObservacion`, el botón "Cámara" verifica el permiso directamente con `ContextCompat.checkSelfPermission()` en el cuerpo del composable. Esta lógica de negocio viola Clean Architecture (la UI no debe contener lógica de permisos) y duplica el manejo que ya existe en el composable `recordarPermisoCamara()`.

**Causa Raíz (Código)**
```kotlin
// ObservacionesScreen.kt — dentro del botón Cámara en DialogEditarObservacion
val isGranted = ContextCompat.checkSelfPermission(
    context, android.Manifest.permission.CAMERA
) == PackageManager.PERMISSION_GRANTED

if (isGranted) {
    accionPendiente?.invoke()  // ❌ Lógica de permisos en composable
} else {
    controlPermiso.solicitar()
}
```

**Criterios de Aceptación**
- [ ] La verificación y solicitud de permiso se centraliza en `recordarPermisoCamara()` o en un ViewModel/Manager dedicado.
- [ ] No hay llamadas directas a `ContextCompat.checkSelfPermission` en composables de pantalla.

---

## [PENDIENTE-ID] test(insumos/tareas): tests unitarios faltantes para AC de issues #403 y #410

**Severidad:** 🔵 Deuda Técnica (Testing)
**Módulo:** Insumos / Tareas
**Archivos afectados:**
- `app/src/test/.../insumo/FormularioInsumoViewModelTest.kt` (no existe)
- `app/src/test/.../tarea/NuevaTareaViewModelTest.kt` (cobertura de edición faltante)

**Descripción**
Los Acceptance Criteria de los Issues #403 y #410 definen tests unitarios obligatorios que no fueron incluidos en el PR #436:
- **#403:** Test: formulario nuevo + tipear nombre y categoría válidos → `isGuardarHabilitado = true`.
- **#410:** Tests: edición de tarea → datos pre-cargados correctamente; eliminación → tarea removida del estado.

**Criterios de Aceptación**
- [ ] Crear `FormularioInsumoViewModelTest` con caso Given-When-Then para modo creación.
- [ ] Agregar casos de edición y eliminación a `NuevaTareaViewModelTest` / `TareaViewModelTest`.
- [ ] Documentar los nuevos casos GWT en `docs/plan_de_pruebas.md`.
- [ ] Todos los tests pasan con `./gradlew test`.
