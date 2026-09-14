# Bugs Identificados

> Los issues con ID oficial se encuentran en el Roadmap (`.context/roadmap_iteracion_5.md`).
> Este archivo registra **deuda tÃƒÂ©cnica nueva** detectada durante las sesiones de desarrollo de la IteraciÃƒÂ³n 5, pendiente de subir a GitHub para obtener su ID.

---

<!-- Plantilla para nuevos bugs:
## [PENDIENTE-ID] TÃƒÂ­tulo descriptivo del bug

**Severidad:** Ã°Å¸â€Â´ Bug Bloqueante | Ã°Å¸Å¸Â  Bug Funcional | Ã°Å¸â€Âµ UX / Deuda TÃƒÂ©cnica
**MÃƒÂ³dulo:** [Ej: Insumos / Tareas / SincronizaciÃƒÂ³n]
**Archivo afectado:** \ruta/del/archivo.kt

**DescripciÃƒÂ³n**
Breve descripciÃƒÂ³n del problema encontrado...

**Causa RaÃƒÂ­z (CÃƒÂ³digo)**
```kotlin
// Snippet del cÃƒÂ³digo problemÃƒÂ¡tico si se conoce
```

**Criterios de AceptaciÃƒÂ³n**
- [ ] Criterio 1
- [ ] Criterio 2
-->

## Ã°Å¸â€Â´ DEUDA TÃƒâ€°CNICA PENDIENTE Ã¢â‚¬â€ IteraciÃƒÂ³n 5

## [#453] refactor(reportes): extender FormatUtils a pestaÃ±a de reportes y exportaciÃ³n PDF/Excel
**Severidad:** Ã°Å¸Å¸Â¢ Baja / Consistencia Visual
**MÃƒÂ³dulo:** Reportes
**DescripciÃƒÂ³n:** Se implementÃƒÂ³ `FormatUtils` para estandarizar los separadores de miles (punto) y decimales (coma). Sin embargo, aÃƒÂºn falta aplicar esta utilidad en las visualizaciones de reportes y en las funciones de exportaciÃƒÂ³n (PDF/Excel) para que respeten la misma configuraciÃƒÂ³n regional.

## [#454] fix(ui): errores de codificaciÃ³n (caracteres especiales/Ã±/tildes) en formularios
**Severidad:** Ã°Å¸Å¸Â¡ Media / UX
**MÃƒÂ³dulo:** UI Global
**DescripciÃƒÂ³n:** En algunos formularios e inputs del sistema los caracteres especiales del espaÃƒÂ±ol (como tildes y la letra 'ÃƒÂ±') se guardan o se visualizan incorrectamente (aparecen como `?` o corrompidos). Es necesario revisar la configuraciÃƒÂ³n de encoding (`UTF-8`) tanto en los TextFields como en la persistencia local de Room.

## [#455] feat(insumos): acumular cantidad al vincular un insumo repetido en lugar de reemplazarlo
**Severidad:** Ã°Å¸â€Âµ Feature Faltante / UX
**MÃƒÂ³dulo:** Insumos / CampaniaInsumo
**DescripciÃƒÂ³n:** Actualmente, si se intenta vincular un insumo que ya estÃƒÂ¡ asociado a la misma campaÃƒÂ±a, el sistema simplemente sobreescribe el registro anterior. El comportamiento esperado deberÃƒÂ­a ser que se acumule/sume la nueva cantidad ingresada a la cantidad preexistente.

## [#456] feat(insumos): ediciÃ³n de insumos vinculados a campaÃ±aÃƒÂ±a

**Severidad:** Ã°Å¸â€Âµ UX / Feature Faltante
**MÃƒÂ³dulo:** Insumos / CampaniaInsumo
**Archivo afectado:** `presentation/ui/screen/insumo/InsumosScreen.kt`

**DescripciÃƒÂ³n**
El listado de insumos vinculados a una campaÃƒÂ±a solo permite eliminar una vinculaciÃƒÂ³n pero no editarla. Si el usuario cometiÃƒÂ³ un error de cantidad o precio debe borrar y volver a vincular. No existe UseCase ni pantalla de ediciÃƒÂ³n para la entidad `CampaniaInsumo`.

**Criterios de AceptaciÃƒÂ³n**
- [ ] Al presionar el ÃƒÂ­cono de ediciÃƒÂ³n en la card de un insumo vinculado, navegar a un formulario pre-cargado con los datos actuales (cantidad, precio).
- [ ] El formulario reutiliza (o adapta) `VincularInsumoScreen` con modo ediciÃƒÂ³n.
- [ ] Se crea `EditarCampaniaInsumoUseCase` que llama a `CampaniaInsumoRepository.update(...)`.
- [ ] Tests unitarios del UseCase y ViewModel para el caso de ediciÃƒÂ³n.
- [ ] Test GWT documentado en `docs/plan_de_pruebas.md`.

---

<!-- Plantilla para nuevos bugs:
## [PENDIENTE-ID] TÃƒÂ­tulo descriptivo del bug

**Severidad:** Ã°Å¸â€Â´ Bug Bloqueante | Ã°Å¸Å¸Â¡ Bug Funcional | Ã°Å¸â€Âµ UX / Deuda TÃƒÂ©cnica
**MÃƒÂ³dulo:** [Ej: Insumos / Tareas / SincronizaciÃƒÂ³n]
**Archivo afectado:** \ruta/del/archivo.kt

**DescripciÃƒÂ³n**
Breve descripciÃƒÂ³n del problema encontrado...

**Causa RaÃƒÂ­z (CÃƒÂ³digo)**
```kotlin
// Snippet del cÃƒÂ³digo problemÃƒÂ¡tico si se conoce
```

**Criterios de AceptaciÃƒÂ³n**
- [ ] Criterio 1
- [ ] Criterio 2
-->

## [#413] fix(auth): nombre de usuario muestra Invitado tras primer registro

**Severidad:** Ã°Å¸Å¸Â  Bug Funcional
**MÃƒÂ³dulo:** AutenticaciÃƒÂ³n / SesiÃƒÂ³n
**Archivo afectado:** presentation/viewmodel/login/LoginViewModel.kt

**DescripciÃƒÂ³n**
En LoginViewModel.registro(), el flujo llama a registroUseCase() y emite registroExitoso = true, pero nunca persiste el nombre en sesiÃƒÂ³n usando sessionManager.saveUserName(nombre). Al ingresar por primera vez, el Dashboard muestra "Invitado".

**Causa RaÃƒÂ­z (CÃƒÂ³digo)**
`kotlin
fun registro(nombre: String, nombreUsuario: String, contrasena: String) {
    viewModelScope.launch {
        registroUseCase(nombre, nombreUsuario, contrasena)
        _state.update { it.copy(isLoading = false, registroExitoso = true) }
        // sessionManager.saveUserName(nombre) <-- FALTA
    }
}
`

**Criterios de AceptaciÃƒÂ³n**
- [ ] Al completar el registro por primera vez, el Dashboard muestra el nombre real del usuario.
- [ ] El HomeViewModel.userName refleja el nombre sin necesidad de logout/login.
- [ ] Test unitario: registro() exitoso -> sessionManager.saveUserName() es llamado con el nombre correcto.

## [#414] fix(dashboard): tareas del dia actual se marcan en rojo en el Dashboard

**Severidad:** Ã°Å¸Å¸Â  Bug Funcional
**MÃƒÂ³dulo:** Dashboard / Tareas
**Archivo afectado:** presentation/ui/screen/home/DashboardOperacionesScreen.kt

**DescripciÃƒÂ³n**
La comparaciÃƒÂ³n usa timestamps exactos en vez de comparar por dÃƒÂ­a calendario. Una tarea de "hoy" que ya pasÃƒÂ³ en hora pero no en fecha se considera vencida y se marca en rojo.

**Causa RaÃƒÂ­z (CÃƒÂ³digo)**
`kotlin
// DashboardOperacionesScreen.kt
val hoy = System.currentTimeMillis() // Timestamp exacto
val isVencida = tarea.fecha < hoy    // Ã¢ï¿½Å’ Tarea de hoy a las 15:30 -> true
`

**Criterios de AceptaciÃƒÂ³n**
- [ ] Tarea creada para hoy (cualquier hora) -> NO aparece en rojo en el Dashboard.
- [ ] Tarea creada para ayer o antes -> SI aparece en rojo.
- [ ] Tarea creada para maÃƒÂ±ana -> aparece en blanco.
- [ ] Test unitario que valide los 3 casos anteriores contra la funciÃƒÂ³n de comparaciÃƒÂ³n.

## [#415] ux(campanias): rediseÃƒÂ±o de DetalleCampaniaScreen con grid 2xN y botones de accion rapida

**Severidad:** Ã°Å¸â€Âµ Mejora UX
**MÃƒÂ³dulo:** CampaÃƒÂ±as / Detalle
**Archivo afectado:** presentation/ui/screen/campania/DetalleCampaniaScreen.kt

**DescripciÃƒÂ³n**
Reemplazar el ScrollableTabRow por un grid de 2 columnas x N filas de botones rectangulares. Cada botÃƒÂ³n incluye un botÃƒÂ³n + secundario visible que navega directamente al formulario de esa entidad (pantalla separada) pre-cargado con el campaniaId. Al presionar el botÃƒÂ³n principal navega a la pantalla de listado.

**Criterios de AceptaciÃƒÂ³n**
- [ ] El ScrollableTabRow y el contenido embebido de tabs son eliminados.
- [ ] Grid 2xN con botones visibles en pantalla.
- [ ] Cada botÃƒÂ³n muestra un subtexto con el contador correcto.
- [ ] El botÃƒÂ³n + navega directamente al formulario con campaniaId.
- [ ] El tap en el card navega a la pantalla de listado.

## [#416] ux(formularios): conservar campaÃƒÂ±a seleccionada al acceder desde BottomNav

**Severidad:** Ã°Å¸â€Âµ Mejora UX
**MÃƒÂ³dulo:** Formularios / SesiÃƒÂ³n
**Archivos afectados:** Formularios de Tarea, Cosecha y Observacion. core/UltimaSeleccionManager.kt

**DescripciÃƒÂ³n**
Cuando el usuario navega desde el BottomNav, no se pasa campaniaId en la ruta. Crear un UltimaSeleccionManager para persistir el campaniaId de la ÃƒÂºltima campaÃƒÂ±a interactuada para usarla como fallback al navegar desde BottomNav.

**Criterios de AceptaciÃƒÂ³n**
- [ ] Formularios desde BottomNav muestran preseleccionada la ÃƒÂºltima campaÃƒÂ±a usada.
- [ ] Cambio manual de campaÃƒÂ±a se persiste como la ÃƒÂºltima.
- [ ] Un chip visible indica la campaÃƒÂ±a preseleccionada.
- [ ] Sin interferir con la navegaciÃƒÂ³n desde DetalleCampania (campaniaId explÃƒÂ­cito).

## [#417] ux(navegacion): planteamiento para reducir clics de acceso a cosechas, observaciones y tareas

**Severidad:** Ã°Å¸â€Âµ Mejora UX
**MÃƒÂ³dulo:** NavegaciÃƒÂ³n / UX Global

**DescripciÃƒÂ³n**
Planteamiento estratÃƒÂ©gico documentado. Con el rediseÃƒÂ±o del grid 2xN y la persistencia de campaÃƒÂ±a, el flujo de creaciÃƒÂ³n baja de 6 clics a 3.

**Criterios de AceptaciÃƒÂ³n**
- [ ] Flujo de creaciÃƒÂ³n desde Detalle de CampaÃƒÂ±a no supera 3 clics.
- [ ] Flujo desde BottomNav no requiere re-seleccionar campaÃƒÂ±a si ya fue usada.
- [ ] Documentar en docs/plan_de_pruebas.md los flujos GWT de los 3 escenarios.

---




---

## [#450] test(insumos/tareas): tests unitarios faltantes para AC de issues #403 y #410

**Severidad:** Ã°Å¸â€Âµ Deuda TÃƒÂ©cnica (Testing)
**MÃƒÂ³dulo:** Insumos / Tareas
**Archivos afectados:**
- `app/src/test/.../insumo/FormularioInsumoViewModelTest.kt` (no existe)
- `app/src/test/.../tarea/NuevaTareaViewModelTest.kt` (cobertura de ediciÃƒÂ³n faltante)

**DescripciÃƒÂ³n**
Los Acceptance Criteria de los Issues #403 y #410 definen tests unitarios obligatorios que no fueron incluidos en el PR #436:
- **#403:** Test: formulario nuevo + tipear nombre y categorÃƒÂ­a vÃƒÂ¡lidos Ã¢â€ â€™ `isGuardarHabilitado = true`.
- **#410:** Tests: ediciÃƒÂ³n de tarea Ã¢â€ â€™ datos pre-cargados correctamente; eliminaciÃƒÂ³n Ã¢â€ â€™ tarea removida del estado.

**Criterios de AceptaciÃƒÂ³n**
- [ ] Crear `FormularioInsumoViewModelTest` con caso Given-When-Then para modo creaciÃƒÂ³n.
- [ ] Agregar casos de ediciÃƒÂ³n y eliminaciÃƒÂ³n a `NuevaTareaViewModelTest` / `TareaViewModelTest`.
- [ ] Documentar los nuevos casos GWT en `docs/plan_de_pruebas.md`.
- [ ] Todos los tests pasan con `./gradlew test`.

## ?? DEUDA TÃ‰CNICA RESUELTA â€” IteraciÃ³n 5

## [RESUELTO-EN-PR-446] fix(dashboard): cÃ¡lculo de ingresos dependiente de texto libre
**Severidad:** ?? Bug Funcional
**MÃ³dulo:** Dashboard
**DescripciÃ³n:** El dashboard filtraba ingresos buscando la palabra "venta" exacta. Dado que el campo es libre, causaba que ventas reales no se sumaran.
**SoluciÃ³n:** Se modificÃ³ `ObtenerResumenRendimientoUseCase` para sumar cualquier cosecha no almacenada con `precio > 0.0`.

## [RESUELTO-EN-PR-446] fix(ui): formato numÃ©rico inconsistente y fallas con decimales
**Severidad:** ?? Baja / UX
**MÃ³dulo:** UI Global
**DescripciÃ³n:** El separador de miles aparecÃ­a como coma, y el usuario no podÃ­a ingresar comas decimales sin que se borrara el valor.
**SoluciÃ³n:** Se creÃ³ `FormatUtils` con Locale("es", "AR") para toda la UI, y se aÃ±adiÃ³ lÃ³gica de reemplazo automÃ¡tico de comas por puntos en los TextFields.

## [RESUELTO-EN-PR-446] fix(campania): contadores de DetalleCampania no incluyen tareas completadas ni todas las cosechas
**Severidad:** ?? Media / UX
**MÃ³dulo:** CampaÃ±a
**DescripciÃ³n:** El contador de tareas de la campaÃ±a siempre mostraba 0 tareas completadas porque se alimentaba de un flow filtrado. El contador de cosechas solo sumaba las almacenadas e indicaba "Kg".
**SoluciÃ³n:** Se inyectÃ³ el TareaRepository para tener un flow puro `todasLasTareas` y se corrigiÃ³ el CardModuloCosechas para sumar todas y usar "Tn".

## [RESUELTO-EN-PR-446] feat(cosechas): ediciÃ³n incompleta de ventas
**Severidad:** ?? Media / UX
**MÃ³dulo:** Cosechas
**DescripciÃ³n:** Al editar una cosecha tipo "venta", los campos de tipo y precio no se precargaban ni se guardaban los cambios.
**SoluciÃ³n:** Se creÃ³ `EditarCosechaConVentaUseCase` para recuperar y guardar simultÃ¡neamente en ambas tablas.

