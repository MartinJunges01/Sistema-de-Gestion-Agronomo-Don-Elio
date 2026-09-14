# Bugs Identificados

> Los issues con ID oficial se encuentran en el Roadmap (`.context/roadmap_iteracion_5.md`).
> Este archivo registra **deuda tÃ©cnica nueva** detectada durante las sesiones de desarrollo de la IteraciÃ³n 5, pendiente de subir a GitHub para obtener su ID.

---

<!-- Plantilla para nuevos bugs:
## [PENDIENTE-ID] TÃ­tulo descriptivo del bug

**Severidad:** ðŸ”´ Bug Bloqueante | ðŸŸ  Bug Funcional | ðŸ”µ UX / Deuda TÃ©cnica
**MÃ³dulo:** [Ej: Insumos / Tareas / SincronizaciÃ³n]
**Archivo afectado:** \ruta/del/archivo.kt

**DescripciÃ³n**
Breve descripciÃ³n del problema encontrado...

**Causa RaÃ­z (CÃ³digo)**
```kotlin
// Snippet del cÃ³digo problemÃ¡tico si se conoce
```

**Criterios de AceptaciÃ³n**
- [ ] Criterio 1
- [ ] Criterio 2
-->

## ðŸ”´ DEUDA TÃ‰CNICA PENDIENTE â€” IteraciÃ³n 5

## [#453] refactor(reportes): extender FormatUtils a pestaña de reportes y exportación PDF/Excel
**Severidad:** ðŸŸ¢ Baja / Consistencia Visual
**MÃ³dulo:** Reportes
**DescripciÃ³n:** Se implementÃ³ `FormatUtils` para estandarizar los separadores de miles (punto) y decimales (coma). Sin embargo, aÃºn falta aplicar esta utilidad en las visualizaciones de reportes y en las funciones de exportaciÃ³n (PDF/Excel) para que respeten la misma configuraciÃ³n regional.

## [#454] fix(ui): errores de codificación (caracteres especiales/ñ/tildes) en formularios
**Severidad:** ðŸŸ¡ Media / UX
**MÃ³dulo:** UI Global
**DescripciÃ³n:** En algunos formularios e inputs del sistema los caracteres especiales del espaÃ±ol (como tildes y la letra 'Ã±') se guardan o se visualizan incorrectamente (aparecen como `?` o corrompidos). Es necesario revisar la configuraciÃ³n de encoding (`UTF-8`) tanto en los TextFields como en la persistencia local de Room.

## [#455] feat(insumos): acumular cantidad al vincular un insumo repetido en lugar de reemplazarlo
**Severidad:** ðŸ”µ Feature Faltante / UX
**MÃ³dulo:** Insumos / CampaniaInsumo
**DescripciÃ³n:** Actualmente, si se intenta vincular un insumo que ya estÃ¡ asociado a la misma campaÃ±a, el sistema simplemente sobreescribe el registro anterior. El comportamiento esperado deberÃ­a ser que se acumule/sume la nueva cantidad ingresada a la cantidad preexistente.

## [#456] feat(insumos): edición de insumos vinculados a campañaÃ±a

**Severidad:** ðŸ”µ UX / Feature Faltante
**MÃ³dulo:** Insumos / CampaniaInsumo
**Archivo afectado:** `presentation/ui/screen/insumo/InsumosScreen.kt`

**DescripciÃ³n**
El listado de insumos vinculados a una campaÃ±a solo permite eliminar una vinculaciÃ³n pero no editarla. Si el usuario cometiÃ³ un error de cantidad o precio debe borrar y volver a vincular. No existe UseCase ni pantalla de ediciÃ³n para la entidad `CampaniaInsumo`.

**Criterios de AceptaciÃ³n**
- [ ] Al presionar el Ã­cono de ediciÃ³n en la card de un insumo vinculado, navegar a un formulario pre-cargado con los datos actuales (cantidad, precio).
- [ ] El formulario reutiliza (o adapta) `VincularInsumoScreen` con modo ediciÃ³n.
- [ ] Se crea `EditarCampaniaInsumoUseCase` que llama a `CampaniaInsumoRepository.update(...)`.
- [ ] Tests unitarios del UseCase y ViewModel para el caso de ediciÃ³n.
- [ ] Test GWT documentado en `docs/plan_de_pruebas.md`.

---


---

<!-- Plantilla para nuevos bugs:
## [PENDIENTE-ID] TÃ­tulo descriptivo del bug

**Severidad:** ðŸ”´ Bug Bloqueante | ðŸŸ¡ Bug Funcional | ðŸ”µ UX / Deuda TÃ©cnica
**MÃ³dulo:** [Ej: Insumos / Tareas / SincronizaciÃ³n]
**Archivo afectado:** \ruta/del/archivo.kt

**DescripciÃ³n**
Breve descripciÃ³n del problema encontrado...

**Causa RaÃ­z (CÃ³digo)**
```kotlin
// Snippet del cÃ³digo problemÃ¡tico si se conoce
```

**Criterios de AceptaciÃ³n**
- [ ] Criterio 1
- [ ] Criterio 2
-->

## [#413] fix(auth): nombre de usuario muestra Invitado tras primer registro

**Severidad:** ðŸŸ  Bug Funcional
**MÃ³dulo:** AutenticaciÃ³n / SesiÃ³n
**Archivo afectado:** presentation/viewmodel/login/LoginViewModel.kt

**DescripciÃ³n**
En LoginViewModel.registro(), el flujo llama a registroUseCase() y emite registroExitoso = true, pero nunca persiste el nombre en sesiÃ³n usando sessionManager.saveUserName(nombre). Al ingresar por primera vez, el Dashboard muestra "Invitado".

**Causa RaÃ­z (CÃ³digo)**
`kotlin
fun registro(nombre: String, nombreUsuario: String, contrasena: String) {
    viewModelScope.launch {
        registroUseCase(nombre, nombreUsuario, contrasena)
        _state.update { it.copy(isLoading = false, registroExitoso = true) }
        // sessionManager.saveUserName(nombre) <-- FALTA
    }
}
`

**Criterios de AceptaciÃ³n**
- [ ] Al completar el registro por primera vez, el Dashboard muestra el nombre real del usuario.
- [ ] El HomeViewModel.userName refleja el nombre sin necesidad de logout/login.
- [ ] Test unitario: registro() exitoso -> sessionManager.saveUserName() es llamado con el nombre correcto.

## [#414] fix(dashboard): tareas del dia actual se marcan en rojo en el Dashboard

**Severidad:** ðŸŸ  Bug Funcional
**MÃ³dulo:** Dashboard / Tareas
**Archivo afectado:** presentation/ui/screen/home/DashboardOperacionesScreen.kt

**DescripciÃ³n**
La comparaciÃ³n usa timestamps exactos en vez de comparar por dÃ­a calendario. Una tarea de "hoy" que ya pasÃ³ en hora pero no en fecha se considera vencida y se marca en rojo.

**Causa RaÃ­z (CÃ³digo)**
`kotlin
// DashboardOperacionesScreen.kt
val hoy = System.currentTimeMillis() // Timestamp exacto
val isVencida = tarea.fecha < hoy    // â�Œ Tarea de hoy a las 15:30 -> true
`

**Criterios de AceptaciÃ³n**
- [ ] Tarea creada para hoy (cualquier hora) -> NO aparece en rojo en el Dashboard.
- [ ] Tarea creada para ayer o antes -> SI aparece en rojo.
- [ ] Tarea creada para maÃ±ana -> aparece en blanco.
- [ ] Test unitario que valide los 3 casos anteriores contra la funciÃ³n de comparaciÃ³n.

## [#415] ux(campanias): rediseÃ±o de DetalleCampaniaScreen con grid 2xN y botones de accion rapida

**Severidad:** ðŸ”µ Mejora UX
**MÃ³dulo:** CampaÃ±as / Detalle
**Archivo afectado:** presentation/ui/screen/campania/DetalleCampaniaScreen.kt

**DescripciÃ³n**
Reemplazar el ScrollableTabRow por un grid de 2 columnas x N filas de botones rectangulares. Cada botÃ³n incluye un botÃ³n + secundario visible que navega directamente al formulario de esa entidad (pantalla separada) pre-cargado con el campaniaId. Al presionar el botÃ³n principal navega a la pantalla de listado.

**Criterios de AceptaciÃ³n**
- [ ] El ScrollableTabRow y el contenido embebido de tabs son eliminados.
- [ ] Grid 2xN con botones visibles en pantalla.
- [ ] Cada botÃ³n muestra un subtexto con el contador correcto.
- [ ] El botÃ³n + navega directamente al formulario con campaniaId.
- [ ] El tap en el card navega a la pantalla de listado.

## [#416] ux(formularios): conservar campaÃ±a seleccionada al acceder desde BottomNav

**Severidad:** ðŸ”µ Mejora UX
**MÃ³dulo:** Formularios / SesiÃ³n
**Archivos afectados:** Formularios de Tarea, Cosecha y Observacion. core/UltimaSeleccionManager.kt

**DescripciÃ³n**
Cuando el usuario navega desde el BottomNav, no se pasa campaniaId en la ruta. Crear un UltimaSeleccionManager para persistir el campaniaId de la Ãºltima campaÃ±a interactuada para usarla como fallback al navegar desde BottomNav.

**Criterios de AceptaciÃ³n**
- [ ] Formularios desde BottomNav muestran preseleccionada la Ãºltima campaÃ±a usada.
- [ ] Cambio manual de campaÃ±a se persiste como la Ãºltima.
- [ ] Un chip visible indica la campaÃ±a preseleccionada.
- [ ] Sin interferir con la navegaciÃ³n desde DetalleCampania (campaniaId explÃ­cito).

## [#417] ux(navegacion): planteamiento para reducir clics de acceso a cosechas, observaciones y tareas

**Severidad:** ðŸ”µ Mejora UX
**MÃ³dulo:** NavegaciÃ³n / UX Global

**DescripciÃ³n**
Planteamiento estratÃ©gico documentado. Con el rediseÃ±o del grid 2xN y la persistencia de campaÃ±a, el flujo de creaciÃ³n baja de 6 clics a 3.

**Criterios de AceptaciÃ³n**
- [ ] Flujo de creaciÃ³n desde Detalle de CampaÃ±a no supera 3 clics.
- [ ] Flujo desde BottomNav no requiere re-seleccionar campaÃ±a si ya fue usada.
- [ ] Documentar en docs/plan_de_pruebas.md los flujos GWT de los 3 escenarios.

---

## [#447] fix(tareas): campo `confirmar` se resetea a `false` al editar tarea completada

**Severidad:** ðŸŸ  Bug Funcional
**MÃ³dulo:** Tareas / ABM
**Archivo afectado:** `presentation/viewmodel/tarea/NuevaTareaViewModel.kt`

**DescripciÃ³n**
En `NuevaTareaViewModel.guardar()`, modo ediciÃ³n, el campo `confirmar` del objeto `Tarea` se construye con el valor literal `false` en lugar de preservar el estado actual de la tarea cargada. Si el usuario edita el nombre de una tarea ya marcada como completada, el guardado la regresarÃ¡ al estado pendiente.

**Causa RaÃ­z (CÃ³digo)**
```kotlin
// NuevaTareaViewModel.kt â€” modo ediciÃ³n
val tareaEditada = Tarea(
    id = tareaId,
    nombre = current.nombre.trim(),
    fecha = current.fecha,
    hora = current.hora,
    notificar = current.notificar,
    confirmar = false, // â�Œ Hardcodeado â€” deberÃ­a preservar el estado original
    idCampania = current.campaniaId
)
```

**Criterios de AceptaciÃ³n**
- [ ] El `NuevaTareaFormState` incluye un campo `confirmar: Boolean` que se carga en `cargarTarea()`.
- [ ] Al guardar en modo ediciÃ³n, `confirmar` toma el valor del state (no un literal `false`).
- [ ] Test unitario: editar tarea completada â†’ `confirmar` permanece `true` tras guardar.

---



## [#449] dt(permisos): verificación de permiso de cámara hardcodeada en composable

**Severidad:** ðŸ”µ UX / Deuda TÃ©cnica (Arquitectura)
**MÃ³dulo:** Observaciones / EdiciÃ³n
**Archivo afectado:** `presentation/ui/screen/observacion/ObservacionesScreen.kt`

**DescripciÃ³n**
En `DialogEditarObservacion`, el botÃ³n "CÃ¡mara" verifica el permiso directamente con `ContextCompat.checkSelfPermission()` en el cuerpo del composable. Esta lÃ³gica de negocio viola Clean Architecture (la UI no debe contener lÃ³gica de permisos) y duplica el manejo que ya existe en el composable `recordarPermisoCamara()`.

**Causa RaÃ­z (CÃ³digo)**
```kotlin
// ObservacionesScreen.kt â€” dentro del botÃ³n CÃ¡mara en DialogEditarObservacion
val isGranted = ContextCompat.checkSelfPermission(
    context, android.Manifest.permission.CAMERA
) == PackageManager.PERMISSION_GRANTED

if (isGranted) {
    accionPendiente?.invoke()  // â�Œ LÃ³gica de permisos en composable
} else {
    controlPermiso.solicitar()
}
```

**Criterios de AceptaciÃ³n**
- [ ] La verificaciÃ³n y solicitud de permiso se centraliza en `recordarPermisoCamara()` o en un ViewModel/Manager dedicado.
- [ ] No hay llamadas directas a `ContextCompat.checkSelfPermission` en composables de pantalla.

---

## [#450] test(insumos/tareas): tests unitarios faltantes para AC de issues #403 y #410

**Severidad:** ðŸ”µ Deuda TÃ©cnica (Testing)
**MÃ³dulo:** Insumos / Tareas
**Archivos afectados:**
- `app/src/test/.../insumo/FormularioInsumoViewModelTest.kt` (no existe)
- `app/src/test/.../tarea/NuevaTareaViewModelTest.kt` (cobertura de ediciÃ³n faltante)

**DescripciÃ³n**
Los Acceptance Criteria de los Issues #403 y #410 definen tests unitarios obligatorios que no fueron incluidos en el PR #436:
- **#403:** Test: formulario nuevo + tipear nombre y categorÃ­a vÃ¡lidos â†’ `isGuardarHabilitado = true`.
- **#410:** Tests: ediciÃ³n de tarea â†’ datos pre-cargados correctamente; eliminaciÃ³n â†’ tarea removida del estado.

**Criterios de AceptaciÃ³n**
- [ ] Crear `FormularioInsumoViewModelTest` con caso Given-When-Then para modo creaciÃ³n.
- [ ] Agregar casos de ediciÃ³n y eliminaciÃ³n a `NuevaTareaViewModelTest` / `TareaViewModelTest`.
- [ ] Documentar los nuevos casos GWT en `docs/plan_de_pruebas.md`.
- [ ] Todos los tests pasan con `./gradlew test`.

## ?? DEUDA TÉCNICA RESUELTA — Iteración 5

## [RESUELTO-EN-PR-446] fix(dashboard): cálculo de ingresos dependiente de texto libre
**Severidad:** ?? Bug Funcional
**Módulo:** Dashboard
**Descripción:** El dashboard filtraba ingresos buscando la palabra "venta" exacta. Dado que el campo es libre, causaba que ventas reales no se sumaran.
**Solución:** Se modificó `ObtenerResumenRendimientoUseCase` para sumar cualquier cosecha no almacenada con `precio > 0.0`.

## [RESUELTO-EN-PR-446] fix(ui): formato numérico inconsistente y fallas con decimales
**Severidad:** ?? Baja / UX
**Módulo:** UI Global
**Descripción:** El separador de miles aparecía como coma, y el usuario no podía ingresar comas decimales sin que se borrara el valor.
**Solución:** Se creó `FormatUtils` con Locale("es", "AR") para toda la UI, y se añadió lógica de reemplazo automático de comas por puntos en los TextFields.

## [RESUELTO-EN-PR-446] fix(campania): contadores de DetalleCampania no incluyen tareas completadas ni todas las cosechas
**Severidad:** ?? Media / UX
**Módulo:** Campaña
**Descripción:** El contador de tareas de la campaña siempre mostraba 0 tareas completadas porque se alimentaba de un flow filtrado. El contador de cosechas solo sumaba las almacenadas e indicaba "Kg".
**Solución:** Se inyectó el TareaRepository para tener un flow puro `todasLasTareas` y se corrigió el CardModuloCosechas para sumar todas y usar "Tn".

## [RESUELTO-EN-PR-446] feat(cosechas): edición incompleta de ventas
**Severidad:** ?? Media / UX
**Módulo:** Cosechas
**Descripción:** Al editar una cosecha tipo "venta", los campos de tipo y precio no se precargaban ni se guardaban los cambios.
**Solución:** Se creó `EditarCosechaConVentaUseCase` para recuperar y guardar simultáneamente en ambas tablas.
