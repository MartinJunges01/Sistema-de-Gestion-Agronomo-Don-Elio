# Plan EstratÃƒÂ©gico y Casos de Prueba (Living Documentation)

Este documento centraliza la estrategia de testing del proyecto "Don Elio" y actÃƒÂºa como fuente de la verdad para escribir las pruebas automatizadas (Test Cases). Es un **Living Document** (Documento Vivo), lo que significa que **deberemos mantenerlo actualizado obligatoriamente** cada vez que modifiquemos el cÃƒÂ³digo o agreguemos nuevas funcionalidades, asegurando que las pruebas y la documentaciÃƒÂ³n no se desfasen.

## 1. Stack TecnolÃƒÂ³gico de Testing
*   **Unit Testing (Casos de Uso, ViewModels, Mappers):** `JUnit 4`, `MockK` (Mocks nativos Kotlin) y `Turbine` (Pruebas de flujos/Flows). 
    *   *Importante:* Para validar excepciones dentro de corrutinas (`runTest`), no se debe usar `assertThrows` de JUnit (ya que pierde el contexto suspendido), sino bloques nativos `try-catch` o `runCatching`.
*   **Pruebas de IntegraciÃƒÂ³n/Base de Datos (DAOs):** `AndroidX Test`, `Room Testing` (con `inMemoryDatabaseBuilder`) ejecutado en Emulador (Pruebas Instrumentadas).
# Plan EstratÃƒÂƒÃ‚Â©gico y Casos de Prueba (Living Documentation)

Este documento centraliza la estrategia de testing del proyecto "Don Elio" y actÃƒÂƒÃ‚Âºa como fuente de la verdad para escribir las pruebas automatizadas (Test Cases). Es un **Living Document** (Documento Vivo), lo que significa que **deberemos mantenerlo actualizado obligatoriamente** cada vez que modifiquemos el cÃƒÂƒÃ‚Â³digo o agreguemos nuevas funcionalidades, asegurando que las pruebas y la documentaciÃƒÂƒÃ‚Â³n no se desfasen.

## 1. Stack TecnolÃƒÂƒÃ‚Â³gico de Testing
*   **Unit Testing (Casos de Uso, ViewModels, Mappers):** `JUnit 4`, `MockK` (Mocks nativos Kotlin) y `Turbine` (Pruebas de flujos/Flows). 
    *   *Importante:* Para validar excepciones dentro de corrutinas (`runTest`), no se debe usar `assertThrows` de JUnit (ya que pierde el contexto suspendido), sino bloques nativos `try-catch` o `runCatching`.
*   **Pruebas de IntegraciÃƒÂƒÃ‚Â³n/Base de Datos (DAOs):** `AndroidX Test`, `Room Testing` (con `inMemoryDatabaseBuilder`) ejecutado en Emulador (Pruebas Instrumentadas).
*   **Pruebas de Interfaz de Usuario (UI):** `Compose Test Rule` nativo.

---

## 2. AnÃƒÂ¡lisis de Discrepancias (Documento 2025 vs Realidad 2026)

Al contrastar la propuesta del aÃƒÂ±o 2025 con la arquitectura real implementada en la App, detectamos e implementamos mejoras significativas que impactan la forma en que escribiremos los tests:

1.  **Redundancia de EdiciÃƒÂ³n (CampaÃƒÂ±as - CU2 y CU4):**
    *   *En 2025:* Se separaba "Entrar al menÃƒÂº" (CU2) de "Editar los campos" (CU4).
    *   *Realidad:* La arquitectura moderna expone un solo `EditarCampaniaUseCase`. AdemÃƒÂ¡s, se agregÃƒÂ³ el campo **`estaActiva`** a la entidad `Campania` para controlar estados (por ejemplo, si estÃƒÂ¡ terminada o en curso). Testearemos directamente la actualizaciÃƒÂ³n de este estado en BD.
2.  **Arquitectura de Notificaciones (Tareas - CU5):**
    *   *En 2025:* DependÃƒÂ­a de un "Actor Externo".
    *   *Realidad:* Reemplazado internamente por `WorkManagerTaskReminderScheduler`. Los tests de tareas deberÃƒÂ¡n validar (vÃƒÂ­a `MockK`) que el scheduler se mande a llamar o se cancele (ej. al completar o borrar una tarea).
3.  **UnificaciÃƒÂ³n de MÃƒÂ³dulo de Cosechas (CU6 y CU7):**
    *   *En 2025:* "Cosecha" (CU6) y "Datos no almacenados" (CU7) corrÃƒÂ­an por caminos distintos.
    *   *Realidad:* Bifurcamos la lÃƒÂ³gica limpiamente en `RegistrarCosechaUseCase` (para silos) y `RegistrarCosechaConVentaUseCase` (Venta o Reserva como alimento). Los tests cubrirÃƒÂ¡n ambas variantes de inserciÃƒÂ³n.
4.  **Refactor Total del MÃƒÂ³dulo de Insumos (CU9):**
    *   *En 2025:* Los insumos se creaban directamente vinculados a una campaÃƒÂ±a.
    *   *Realidad:* **Un cambio vital.** Ahora existe un CatÃƒÂ¡logo Global (`CrearInsumoCatalogoUseCase`) y posteriormente una vinculaciÃƒÂ³n a la campaÃƒÂ±a (`AsignarInsumoACampaniaUseCase`). AdemÃƒÂ¡s, el catÃƒÂ¡logo tiene la columna **`activo`**. Si el usuario elimina un insumo del catÃƒÂ¡logo (`EliminarInsumoCatalogoUseCase`), el test deberÃƒÂ¡ corroborar que **NO se hace un `DELETE` en la DB**, sino un `UPDATE activo = false` (Soft-Delete) para no corromper los histÃƒÂ³ricos de campaÃƒÂ±as pasadas.
5.  **MÃƒÂ³dulos Nuevos (No previstos en 2025):**
    *   *AutenticaciÃƒÂ³n:* `LoginUseCase` (SHA-256) y `RegistroUseCase`.
## 2. AnÃƒÂƒÃ‚Â¡lisis de Discrepancias (Documento 2025 vs Realidad 2026)

Al contrastar la propuesta del aÃƒÂƒÃ‚Â±o 2025 con la arquitectura real implementada en la App, detectamos e implementamos mejoras significativas que impactan la forma en que escribiremos los tests:

1.  **Redundancia de EdiciÃƒÂƒÃ‚Â³n (CampaÃƒÂƒÃ‚Â±as - CU2 y CU4):**
    *   *En 2025:* Se separaba "Entrar al menÃƒÂƒÃ‚Âº" (CU2) de "Editar los campos" (CU4).
    *   *Realidad:* La arquitectura moderna expone un solo `EditarCampaniaUseCase`. AdemÃƒÂƒÃ‚Â¡s, se agregÃƒÂƒÃ‚Â³ el campo **`estaActiva`** a la entidad `Campania` para controlar estados (por ejemplo, si estÃƒÂƒÃ‚Â¡ terminada o en curso). Testearemos directamente la actualizaciÃƒÂƒÃ‚Â³n de este estado en BD.
2.  **Arquitectura de Notificaciones (Tareas - CU5):**
    *   *En 2025:* DependÃƒÂƒÃ‚Â­a de un "Actor Externo".
    *   *Realidad:* Reemplazado internamente por `WorkManagerTaskReminderScheduler`. Los tests de tareas deberÃƒÂƒÃ‚Â¡n validar (vÃƒÂƒÃ‚Â­a `MockK`) que el scheduler se mande a llamar o se cancele (ej. al completar o borrar una tarea).
3.  **UnificaciÃƒÂƒÃ‚Â³n de MÃƒÂƒÃ‚Â³dulo de Cosechas (CU6 y CU7):**
    *   *En 2025:* "Cosecha" (CU6) y "Datos no almacenados" (CU7) corrÃƒÂƒÃ‚Â­an por caminos distintos.
    *   *Realidad:* Bifurcamos la lÃƒÂƒÃ‚Â³gica limpiamente en `RegistrarCosechaUseCase` (para silos) y `RegistrarCosechaConVentaUseCase` (Venta o Reserva como alimento). Los tests cubrirÃƒÂƒÃ‚Â¡n ambas variantes de inserciÃƒÂƒÃ‚Â³n.
4.  **Refactor Total del MÃƒÂƒÃ‚Â³dulo de Insumos (CU9):**
    *   *En 2025:* Los insumos se creaban directamente vinculados a una campaÃƒÂƒÃ‚Â±a.
    *   *Realidad:* **Un cambio vital.** Ahora existe un CatÃƒÂƒÃ‚Â¡logo Global (`CrearInsumoCatalogoUseCase`) y posteriormente una vinculaciÃƒÂƒÃ‚Â³n a la campaÃƒÂƒÃ‚Â±a (`AsignarInsumoACampaniaUseCase`). AdemÃƒÂƒÃ‚Â¡s, el catÃƒÂƒÃ‚Â¡logo tiene la columna **`activo`**. Si el usuario elimina un insumo del catÃƒÂƒÃ‚Â¡logo (`EliminarInsumoCatalogoUseCase`), el test deberÃƒÂƒÃ‚Â¡ corroborar que **NO se hace un `DELETE` en la DB**, sino un `UPDATE activo = false` (Soft-Delete) para no corromper los histÃƒÂƒÃ‚Â³ricos de campaÃƒÂƒÃ‚Â±as pasadas.
5.  **MÃƒÂƒÃ‚Â³dulos Nuevos (No previstos en 2025):**
    *   *AutenticaciÃƒÂƒÃ‚Â³n:* `LoginUseCase` (SHA-256) y `RegistroUseCase`.
    *   *Backups:* `CrearBackupUseCase` y `RestaurarBackupUseCase` usando SAF de Android.

---

## 3. Pruebas Fuera de los Casos de Uso (Out of Scope Tests)

No toda la app es Casos de Uso. Existen componentes de bajo nivel y de infraestructura que testearemos independientemente:
*   **DAOs (Data Access Objects):** 
    Pruebas instrumentadas sobre `UsuarioDao`, `CampaniaDao`, `CampaniaInsumoDao` (validando foreign keys, borrados en cascada fÃƒÂ­sicos, y los queries filtrados por `activo = 1`).
*   **Mappers (Data <-> Domain):** 
    Pruebas unitarias para validar que al pasar de Entity a Domain Model no se pierda informaciÃƒÂ³n y viceversa.
*   **ViewModels (Presentation):** 
    Validar la emisiÃƒÂ³n correcta de los estados (`Loading`, `Success`, `Error`) hacia Jetpack Compose usando `Turbine` (ej: `LoginViewModelTest` verifica la transiciÃƒÂ³n a `isLoading = true` y luego `loginExitoso = true` o la asignaciÃƒÂ³n de mensajes de error).
    Pruebas instrumentadas sobre `UsuarioDao`, `CampaniaDao`, `CampaniaInsumoDao` (validando foreign keys, borrados en cascada fÃƒÂƒÃ‚Â­sicos, y los queries filtrados por `activo = 1`).
*   **Mappers (Data <-> Domain):** 
    Pruebas unitarias para validar que al pasar de Entity a Domain Model no se pierda informaciÃƒÂƒÃ‚Â³n y viceversa.
*   **ViewModels (Presentation):** 
    Validar la emisiÃƒÂƒÃ‚Â³n correcta de los estados (`Loading`, `Success`, `Error`) hacia Jetpack Compose usando `Turbine` (ej: `LoginViewModelTest` verifica la transiciÃƒÂƒÃ‚Â³n a `isLoading = true` y luego `loginExitoso = true` o la asignaciÃƒÂƒÃ‚Â³n de mensajes de error).

---

## 4. Escenarios de Pruebas (Behavior-Driven Development - BDD)

A continuaciÃƒÂ³n, estructuramos los tests en formato `Given-When-Then` por mÃƒÂ³dulo, respetando el orden lÃƒÂ³gico de los Casos de Uso.

### MÃƒÂ³dulo de CampaÃƒÂ±as (CU1 - CU4)

**Test 1: Crear CampaÃƒÂ±a Exitosa**
*   **Given:** Un nombre vÃƒÂ¡lido "Trigo de Invierno", cultivo "Trigo" y una fecha correcta.
*   **When:** Invoco `CrearCampaniaUseCase`.
*   **Then:** El sistema debe insertar el registro en el repositorio y emitir el estado `Resource.Success`.

**Test 2: Crear CampaÃƒÂ±a con Errores**
*   **Given:** Un nombre vacÃƒÂ­o "".
*   **When:** Invoco `CrearCampaniaUseCase`.
*   **Then:** El sistema debe emitir `Resource.Error` con mensaje "El nombre no puede estar vacÃƒÂ­o" y NO llamar al repositorio.

**Test UC-V1: ValidarDatosCampaniaUseCase Ã¢Â€Â” Nombre vacÃƒÂ­o**
A continuaciÃƒÂƒÃ‚Â³n, estructuramos los tests en formato `Given-When-Then` por mÃƒÂƒÃ‚Â³dulo, respetando el orden lÃƒÂƒÃ‚Â³gico de los Casos de Uso.

### MÃƒÂƒÃ‚Â³dulo de CampaÃƒÂƒÃ‚Â±as (CU1 - CU4)

**Test 1: Crear CampaÃƒÂƒÃ‚Â±a Exitosa**
*   **Given:** Un nombre vÃƒÂƒÃ‚Â¡lido "Trigo de Invierno", cultivo "Trigo" y una fecha correcta.
*   **When:** Invoco `CrearCampaniaUseCase`.
*   **Then:** El sistema debe insertar el registro en el repositorio y emitir el estado `Resource.Success`.

**Test 2: Crear CampaÃƒÂƒÃ‚Â±a con Errores**
*   **Given:** Un nombre vacÃƒÂƒÃ‚Â­o "".
*   **When:** Invoco `CrearCampaniaUseCase`.
*   **Then:** El sistema debe emitir `Resource.Error` con mensaje "El nombre no puede estar vacÃƒÂƒÃ‚Â­o" y NO llamar al repositorio.

**Test UC-V1: ValidarDatosCampaniaUseCase ÃƒÂ¢Ã‚Â€Ã‚Â” Nombre vacÃƒÂƒÃ‚Â­o**
*   **Given:** nombre = "", cultivo = "Soja", fechaInicio = <fecha futura>, isEditMode = false
*   **When:** invoke(nombre, cultivo, fechaInicio, isEditMode)
*   **Then:** esValido = false, errorNombre = "El nombre es obligatorio"

**Test UC-V2: ValidarDatosCampaniaUseCase Ã¢Â€Â” Fecha pasada en creaciÃƒÂ³n**
*   **Given:** nombre = "CampaÃƒÂ±a", cultivo = "MaÃƒÂ­z", fechaInicio = <ayer en millis>, isEditMode = false
*   **When:** invoke(...)
*   **Then:** esValido = false, errorFecha = "La fecha no puede ser anterior a hoy"

**Test UC-V3: ValidarDatosCampaniaUseCase Ã¢Â€Â” Fecha pasada permitida en ediciÃƒÂ³n**
*   **Given:** nombre = "CampaÃƒÂ±a", cultivo = "MaÃƒÂ­z", fechaInicio = <ayer en millis>, isEditMode = true
*   **When:** invoke(...)
*   **Then:** esValido = true, errorFecha = null

**Test UC-V4: ValidarDatosCampaniaUseCase Ã¢Â€Â” Todos los campos vÃƒÂ¡lidos**
**Test UC-V2: ValidarDatosCampaniaUseCase ÃƒÂ¢Ã‚Â€Ã‚Â” Fecha pasada en creaciÃƒÂƒÃ‚Â³n**
*   **Given:** nombre = "CampaÃƒÂƒÃ‚Â±a", cultivo = "MaÃƒÂƒÃ‚Â­z", fechaInicio = <ayer en millis>, isEditMode = false
*   **When:** invoke(...)
*   **Then:** esValido = false, errorFecha = "La fecha no puede ser anterior a hoy"

**Test UC-V3: ValidarDatosCampaniaUseCase ÃƒÂ¢Ã‚Â€Ã‚Â” Fecha pasada permitida en ediciÃƒÂƒÃ‚Â³n**
*   **Given:** nombre = "CampaÃƒÂƒÃ‚Â±a", cultivo = "MaÃƒÂƒÃ‚Â­z", fechaInicio = <ayer en millis>, isEditMode = true
*   **When:** invoke(...)
*   **Then:** esValido = true, errorFecha = null

**Test UC-V4: ValidarDatosCampaniaUseCase ÃƒÂ¢Ã‚Â€Ã‚Â” Todos los campos vÃƒÂƒÃ‚Â¡lidos**
*   **Given:** Todos los campos correctos, isEditMode = false
*   **When:** invoke(...)
*   **Then:** esValido = true, todos los errores = null

### MÃƒÂ³dulo de Insumos (CU9 - CU9.4)

**Test 3: EliminaciÃƒÂ³n LÃƒÂ³gica (Soft-Delete) de Insumo del CatÃƒÂ¡logo**
*   **Given:** Que el insumo "Glifosato" existe en el catÃƒÂ¡logo con `activo = true` y ya fue utilizado en 2 campaÃƒÂ±as.
*   **When:** Invoco `EliminarInsumoCatalogoUseCase` pasando ese insumo.
*   **Then:** El repositorio debe realizar un `UPDATE` (cambiando `activo` a `false`) y NO un `DELETE` fÃƒÂ­sico. Las llamadas a `ObtenerCatalogoInsumosUseCase` ya no deben retornarlo.

**Test 4: AsignaciÃƒÂ³n de Insumo a CampaÃƒÂ±a**
*   **Given:** El "Glifosato" (activo en el catÃƒÂ¡logo) y la campaÃƒÂ±a "Trigo de Invierno".
*   **When:** Invoco `AsignarInsumoACampaniaUseCase` pasando `cantidad = 5` y `precio = 100`.
*   **Then:** Se crea un registro en `CampaniaInsumoEntity` relacionando los IDs y estableciendo el coste.

**Test UC-V5: ValidarInsumoUseCase Ã¢Â€Â” CategorÃƒÂ­a vacÃƒÂ­a**
*   **Given:** nombre = "Herbicida", categoria = ""
*   **When:** invoke(nombre, categoria)
*   **Then:** esValido = false, errorCategoria = "La categorÃƒÂ­a es obligatoria"

**Test UC-V6: ValidarInsumoUseCase Ã¢Â€Â” Ambos campos vÃƒÂ¡lidos**
*   **Given:** nombre = "Herbicida", categoria = "QuÃƒÂ­mico"
*   **When:** invoke(nombre, categoria)
*   **Then:** esValido = true, errorNombre = null, errorCategoria = null

### MÃƒÂ³dulo de Tareas (CU5 - CU5.4)

#### TareaViewModel Ã¢Â€Â” sincronizarCampania() [#292]
### MÃƒÂƒÃ‚Â³dulo de Insumos (CU9 - CU9.4)

**Test 3: EliminaciÃƒÂƒÃ‚Â³n LÃƒÂƒÃ‚Â³gica (Soft-Delete) de Insumo del CatÃƒÂƒÃ‚Â¡logo**
*   **Given:** Que el insumo "Glifosato" existe en el catÃƒÂƒÃ‚Â¡logo con `activo = true` y ya fue utilizado en 2 campaÃƒÂƒÃ‚Â±as.
*   **When:** Invoco `EliminarInsumoCatalogoUseCase` pasando ese insumo.
*   **Then:** El repositorio debe realizar un `UPDATE` (cambiando `activo` a `false`) y NO un `DELETE` fÃƒÂƒÃ‚Â­sico. Las llamadas a `ObtenerCatalogoInsumosUseCase` ya no deben retornarlo.

**Test 4: AsignaciÃƒÂƒÃ‚Â³n de Insumo a CampaÃƒÂƒÃ‚Â±a**
*   **Given:** El "Glifosato" (activo en el catÃƒÂƒÃ‚Â¡logo) y la campaÃƒÂƒÃ‚Â±a "Trigo de Invierno".
*   **When:** Invoco `AsignarInsumoACampaniaUseCase` pasando `cantidad = 5` y `precio = 100`.
*   **Then:** Se crea un registro en `CampaniaInsumoEntity` relacionando los IDs y estableciendo el coste.

**Test UC-V5: ValidarInsumoUseCase ÃƒÂ¢Ã‚Â€Ã‚Â” CategorÃƒÂƒÃ‚Â­a vacÃƒÂƒÃ‚Â­a**
*   **Given:** nombre = "Herbicida", categoria = ""
*   **When:** invoke(nombre, categoria)
*   **Then:** esValido = false, errorCategoria = "La categorÃƒÂƒÃ‚Â­a es obligatoria"

**Test UC-V6: ValidarInsumoUseCase ÃƒÂ¢Ã‚Â€Ã‚Â” Ambos campos vÃƒÂƒÃ‚Â¡lidos**
*   **Given:** nombre = "Herbicida", categoria = "QuÃƒÂƒÃ‚Â­mico"
*   **When:** invoke(nombre, categoria)
*   **Then:** esValido = true, errorNombre = null, errorCategoria = null

### MÃƒÂƒÃ‚Â³dulo de Tareas (CU5 - CU5.4)

#### TareaViewModel ÃƒÂ¢Ã‚Â€Ã‚Â” sincronizarCampania() [#292]

**Test VM-T1: sincronizarCampania actualiza el id cuando difiere del actual**
*   **Given:** El `TareaViewModel` inicia sin `campaniaId` en el `SavedStateHandle` (estado inicial `null`).
*   **When:** Se llama a `sincronizarCampania(5)`.
*   **Then:** El StateFlow `campaniaIdSeleccionada` debe emitir el valor `5`.

**Test VM-T2: sincronizarCampania no emite si el id es igual al actual**
*   **Given:** El `TareaViewModel` ya tiene `campaniaIdSeleccionada = 5`.
*   **When:** Se llama a `sincronizarCampania(5)` con el mismo valor.
*   **Then:** El StateFlow **no** debe emitir un nuevo evento (idempotencia garantizada).

**Test VM-T3: tareas emite lista vacÃƒÂ­a si no hay campaniaId vÃƒÂ¡lido**
*   **Given:** El `TareaViewModel` inicia sin `campaniaId` vÃƒÂ¡lido.
*   **When:** Se observa el StateFlow `tareas`.
*   **Then:** Debe emitir inmediatamente una lista vacÃƒÂ­a, sin llamar al repositorio.
**Test VM-T3: tareas emite lista vacÃƒÂƒÃ‚Â­a si no hay campaniaId vÃƒÂƒÃ‚Â¡lido**
*   **Given:** El `TareaViewModel` inicia sin `campaniaId` vÃƒÂƒÃ‚Â¡lido.
*   **When:** Se observa el StateFlow `tareas`.
*   **Then:** Debe emitir inmediatamente una lista vacÃƒÂƒÃ‚Â­a, sin llamar al repositorio.

**Test VM-T4: isCampaniaValid emite false cuando campaniaId es nulo**
*   **Given:** `campaniaIdSeleccionada` es `null`.
*   **When:** Se observa `isCampaniaValid`.
*   **Then:** Debe emitir `false`.

**Test VM-T5: isCampaniaValid emite true tras sincronizarCampania con id vÃƒÂ¡lido**
**Test VM-T5: isCampaniaValid emite true tras sincronizarCampania con id vÃƒÂƒÃ‚Â¡lido**
*   **Given:** El ViewModel inicia con `campaniaId = null`.
*   **When:** Se llama a `sincronizarCampania(3)`.
*   **Then:** `isCampaniaValid` debe emitir `true`.


**Test 5: Agendar tarea con recordatorio activado**
*   **Given:** Una nueva tarea "Revisar fertilizante" con el switch `notificar = true`.
*   **When:** Invoco `CrearTareaUseCase`.
*   **Then:** El sistema guarda la tarea en BD y, posteriormente, invoca `taskReminderScheduler.schedule(tarea)`.

**Test 6: Completar tarea programada (CancelaciÃƒÂ³n de Alerta)**
**Test 6: Completar tarea programada (CancelaciÃƒÂƒÃ‚Â³n de Alerta)**
*   **Given:** La tarea anterior, que actualmente tiene notificaciones encoladas.
*   **When:** Invoco `ConfirmarTareaUseCase` seteando la tarea como `completada = true`.
*   **Then:** El estado de la tarea cambia en BD, y obligatoriamente se invoca `taskReminderScheduler.cancel(tarea.id)` para evitar alertas fantasma.

### MÃƒÂ³dulo de Cosechas (CU6 - CU7)
### MÃƒÂƒÃ‚Â³dulo de Cosechas (CU6 - CU7)

**Test 7: Registrar Cosecha No Almacenada (Venta/Reserva)**
*   **Given:** Una cosecha de "Soja" que no va al silo, sino que se vende (`venta = true`) a $100.
*   **When:** Invoco `RegistrarCosechaConVentaUseCase`.
*   **Then:** El sistema inserta el registro base en la tabla Cosechas, toma el ID generado, e inserta un segundo registro en `CosechaNoAlmacenadaEntity` vinculando la venta y el precio.

**Test 8: Listar Cosechas de una CampaÃƒÂ±a**
*   **Given:** Una campaÃƒÂ±a con cosechas mixtas (en silo y vendidas).
*   **When:** Invoco `ObtenerCosechasPorCampaniaUseCase` y `ObtenerCosechasNoAlmacenadasUseCase`.
*   **Then:** El repositorio debe devolver dos flujos distintos. El ViewModel debe ser capaz de fusionarlos para mostrar quÃƒÂ© fracciÃƒÂ³n de la cosecha total fue vendida.

**Test 8.1: Formulario de Cosecha - Sin CampaÃƒÂ±a Seleccionada (Issue 7)**
*   **Given:** Un `FormularioCosechaViewModel` creado sin `campaniaId` en el `SavedStateHandle` (acceso vÃƒÂ­a navegaciÃƒÂ³n global).
*   **When:** El usuario ingresa una cantidad vÃƒÂ¡lida y presiona "Guardar Registro".
*   **Then:** Se setea `errorCampania = "Debe seleccionar una campaÃƒÂ±a"` y no se llama a ningÃƒÂºn use case de registro.

**Test 8.2: Formulario de Cosecha - Cantidad Obligatoria (Issue 12)**
*   **Given:** Una campaÃƒÂ±a seleccionada y el campo `cantidad` vacÃƒÂ­o.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se setea `errorCantidad = "La cantidad es obligatoria"` y no se llama a ningÃƒÂºn use case de registro.

**Test 8.3: Formulario de Cosecha - Precio InvÃƒÂ¡lido**
*   **Given:** Una campaÃƒÂ±a y una `cantidad` vÃƒÂ¡lidas, con `almacenado = false`, `tipo = "Venta"` y un precio no numÃƒÂ©rico (ej. "abc").
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se setea `errorPrecio = "Precio invÃƒÂ¡lido"` y no se llama a ningÃƒÂºn use case de registro.

**Test 8.4: Formulario de Cosecha - Registro Exitoso (Almacenado)**
*   **Given:** Una campaÃƒÂ±a, `cantidad = 100`, y `almacen = "Silo 1"` vÃƒÂ¡lidos.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se llama a `RegistrarCosechaUseCase` con los parÃƒÂ¡metros correctos y se emite `guardadoExitoso = true`.

**Test 8.5: Formulario de Cosecha - Registro Exitoso (Venta)**
*   **Given:** Una campaÃƒÂ±a, `cantidad = 100`, `almacenado = false`, `tipo = "Venta"` y `precio = 500` vÃƒÂ¡lidos.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se llama a `RegistrarCosechaConVentaUseCase` con los parÃƒÂ¡metros correctos y se emite `guardadoExitoso = true`.

### MÃƒÂ³dulo de Observaciones (CU8)

**Test 9: Guardar ObservaciÃƒÂ³n con Imagen Adjunta**
**Test 8: Listar Cosechas de una CampaÃƒÂƒÃ‚Â±a**
*   **Given:** Una campaÃƒÂƒÃ‚Â±a con cosechas mixtas (en silo y vendidas).
*   **When:** Invoco `ObtenerCosechasPorCampaniaUseCase` y `ObtenerCosechasNoAlmacenadasUseCase`.
*   **Then:** El repositorio debe devolver dos flujos distintos. El ViewModel debe ser capaz de fusionarlos para mostrar quÃƒÂƒÃ‚Â© fracciÃƒÂƒÃ‚Â³n de la cosecha total fue vendida.

**Test 8.1: Formulario de Cosecha - Sin CampaÃƒÂƒÃ‚Â±a Seleccionada (Issue 7)**
*   **Given:** Un `FormularioCosechaViewModel` creado sin `campaniaId` en el `SavedStateHandle` (acceso vÃƒÂƒÃ‚Â­a navegaciÃƒÂƒÃ‚Â³n global).
*   **When:** El usuario ingresa una cantidad vÃƒÂƒÃ‚Â¡lida y presiona "Guardar Registro".
*   **Then:** Se setea `errorCampania = "Debe seleccionar una campaÃƒÂƒÃ‚Â±a"` y no se llama a ningÃƒÂƒÃ‚Âºn use case de registro.

**Test 8.2: Formulario de Cosecha - Cantidad Obligatoria (Issue 12)**
*   **Given:** Una campaÃƒÂƒÃ‚Â±a seleccionada y el campo `cantidad` vacÃƒÂƒÃ‚Â­o.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se setea `errorCantidad = "La cantidad es obligatoria"` y no se llama a ningÃƒÂƒÃ‚Âºn use case de registro.

**Test 8.3: Formulario de Cosecha - Precio InvÃƒÂƒÃ‚Â¡lido**
*   **Given:** Una campaÃƒÂƒÃ‚Â±a y una `cantidad` vÃƒÂƒÃ‚Â¡lidas, con `almacenado = false`, `tipo = "Venta"` y un precio no numÃƒÂƒÃ‚Â©rico (ej. "abc").
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se setea `errorPrecio = "Precio invÃƒÂƒÃ‚Â¡lido"` y no se llama a ningÃƒÂƒÃ‚Âºn use case de registro.

**Test 8.4: Formulario de Cosecha - Registro Exitoso (Almacenado)**
*   **Given:** Una campaÃƒÂƒÃ‚Â±a, `cantidad = 100`, y `almacen = "Silo 1"` vÃƒÂƒÃ‚Â¡lidos.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se llama a `RegistrarCosechaUseCase` con los parÃƒÂƒÃ‚Â¡metros correctos y se emite `guardadoExitoso = true`.

**Test 8.5: Formulario de Cosecha - Registro Exitoso (Venta)**
*   **Given:** Una campaÃƒÂƒÃ‚Â±a, `cantidad = 100`, `almacenado = false`, `tipo = "Venta"` y `precio = 500` vÃƒÂƒÃ‚Â¡lidos.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se llama a `RegistrarCosechaConVentaUseCase` con los parÃƒÂƒÃ‚Â¡metros correctos y se emite `guardadoExitoso = true`.

### MÃƒÂƒÃ‚Â³dulo de Observaciones (CU8)

**Test 9: Guardar ObservaciÃƒÂƒÃ‚Â³n con Imagen Adjunta**
*   **Given:** Una nota de texto y una URI local que apunta a una foto en el dispositivo.
*   **When:** Invoco `GuardarObservacionUseCase`.
*   **Then:** El sistema guarda correctamente el string de la URI en la entidad para que luego Coil pueda renderizarla en la UI.

### MÃƒÂ³dulo de AutenticaciÃƒÂ³n (Extra 1)

**Test 10: Login Exitoso con Hash SHA-256**
*   **Given:** Un usuario "DonElio" registrado en la base de datos con contraseÃƒÂ±a hasheada.
*   **When:** El usuario ingresa la contraseÃƒÂ±a en texto plano y se invoca `LoginUseCase`.
### MÃƒÂƒÃ‚Â³dulo de AutenticaciÃƒÂƒÃ‚Â³n (Extra 1)

**Test 10: Login Exitoso con Hash SHA-256**
*   **Given:** Un usuario "DonElio" registrado en la base de datos con contraseÃƒÂƒÃ‚Â±a hasheada.
*   **When:** El usuario ingresa la contraseÃƒÂƒÃ‚Â±a en texto plano y se invoca `LoginUseCase`.
*   **Then:** El Use Case encripta el texto plano ingresado, lo compara con la BD, coincide, y emite `Resource.Success`.

**Test 11: Login Fallido (Usuario no existe)**
*   **Given:** Un intento de acceso con el nombre "Intruso".
*   **When:** Invoco `LoginUseCase`.
*   **Then:** Retorna `Resource.Error("Usuario no encontrado")`.

### MÃƒÂ³dulo de Backups (Extra 2)

**Test 12: GeneraciÃƒÂ³n de Backup Exitoso**
### MÃƒÂƒÃ‚Â³dulo de Backups (Extra 2)

**Test 12: GeneraciÃƒÂƒÃ‚Â³n de Backup Exitoso**
*   **Given:** Una ruta URI proporcionada por el SAF (Storage Access Framework) donde el usuario tiene permisos de escritura.
*   **When:** Invoco `CrearBackupUseCase`.
*   **Then:** El archivo `.db` se copia exitosamente al destino y emite `Resource.Success`.

---

## 5. Casos de Borde (Edge Cases) a Testear
*   **CampaÃƒÂ±as:** Intentar crear una campaÃƒÂ±a con nombre vacÃƒÂ­o (DeberÃƒÂ­a fallar con `Resource.Error`).
*   **Insumos:** Intentar vincular una cantidad nula o negativa de insumos a una campaÃƒÂ±a (Lanza `IllegalArgumentException`).
*   **Tareas:** Programar una tarea en el pasado con el switch de notificar en `true`. El `WorkManagerTaskReminderScheduler` no deberÃƒÂ­a encolar notificaciones retroactivas (debe validar que el delay calculado sea > 0).
*   **Observaciones:** Intentar guardar una observaciÃƒÂ³n con el campo de texto vacÃƒÂ­o (Lanza `IllegalArgumentException`).
*   **Cosechas (Formulario):** Guardar sin campaÃƒÂ±a seleccionada (Issue 7) Ã¢Â€Â” Debe emitir `errorCampania` y NO crashear por FK constraint; guardar con `cantidad` o `unidad` vacÃƒÂ­as (Issue 12) Ã¢Â€Â” Debe emitir el error visual correspondiente y deshabilitar el botÃƒÂ³n "Guardar".
*   **AutenticaciÃƒÂ³n:** Iniciar sesiÃƒÂ³n con un usuario inexistente o con credenciales vacÃƒÂ­as (El ViewModel debe capturar la excepciÃƒÂ³n o el `null` y emitir el estado de `error` correspondiente).

---

## 6. Cobertura y EjecuciÃƒÂ³n de Tests

Para garantizar que nuestros tests efectivamente cubren la lÃƒÂ³gica de negocio, implementaremos las siguientes estrategias:

### A. EjecuciÃƒÂ³n de Pruebas (Comandos)
1.  **Pruebas Unitarias (JVM Locales):**
    *   Comando: `./gradlew testDebugUnitTest`
    *   *PropÃƒÂ³sito:* Ejecutar todas las pruebas de Use Cases y ViewModels de manera ultra rÃƒÂ¡pida sin necesidad de un emulador.
2.  **Pruebas Instrumentadas (Base de Datos):**
    *   Comando: `./gradlew connectedDebugAndroidTest`
    *   *PropÃƒÂ³sito:* Ejecutar las pruebas sobre los DAOs. Requiere que un dispositivo fÃƒÂ­sico o emulador estÃƒÂ© encendido y conectado.

### B. MediciÃƒÂ³n de Cobertura (Code Coverage)
Utilizaremos **KoverX** (o JaCoCo configurado para Kotlin) para generar reportes HTML visuales sobre quÃƒÂ© porcentaje de nuestro cÃƒÂ³digo estÃƒÂ¡ siendo probado.
*   **Comando de Cobertura (Android):** `./gradlew koverHtmlReportDebug` (Es fundamental usar la variante `Debug` para que Kover analice correctamente las clases instrumentadas de Android).
*   **Meta de Cobertura:**
    *   `domain` (Reglas de negocio y Use Cases): **MÃƒÂ­nimo 80%**. Esta capa es crÃƒÂ­tica.
    *   `data` (DAOs y Repositorios): **MÃƒÂ­nimo 70%**.
    *   `presentation` (UI): No requerirÃƒÂ¡ cobertura estricta en la fase inicial para priorizar velocidad.

### C. AutomatizaciÃƒÂ³n Continua (CI/CD) con GitHub Actions
Para asegurar que no se introduzcan regresiones al proyecto, hemos configurado un flujo de trabajo (Workflow) en GitHub Actions (`.github/workflows/pr_tests.yml`). 

**Ã‚Â¿QuÃƒÂ© hace automÃƒÂ¡ticamente?**
*   **CampaÃƒÂƒÃ‚Â±as:** Intentar crear una campaÃƒÂƒÃ‚Â±a con nombre vacÃƒÂƒÃ‚Â­o (DeberÃƒÂƒÃ‚Â­a fallar con `Resource.Error`).
*   **Insumos:** Intentar vincular una cantidad nula o negativa de insumos a una campaÃƒÂƒÃ‚Â±a (Lanza `IllegalArgumentException`).
*   **Tareas:** Programar una tarea en el pasado con el switch de notificar en `true`. El `WorkManagerTaskReminderScheduler` no deberÃƒÂƒÃ‚Â­a encolar notificaciones retroactivas (debe validar que el delay calculado sea > 0).
*   **Observaciones:** Intentar guardar una observaciÃƒÂƒÃ‚Â³n con el campo de texto vacÃƒÂƒÃ‚Â­o (Lanza `IllegalArgumentException`).
*   **Cosechas (Formulario):** Guardar sin campaÃƒÂƒÃ‚Â±a seleccionada (Issue 7) ÃƒÂ¢Ã‚Â€Ã‚Â” Debe emitir `errorCampania` y NO crashear por FK constraint; guardar con `cantidad` o `unidad` vacÃƒÂƒÃ‚Â­as (Issue 12) ÃƒÂ¢Ã‚Â€Ã‚Â” Debe emitir el error visual correspondiente y deshabilitar el botÃƒÂƒÃ‚Â³n "Guardar".
*   **AutenticaciÃƒÂƒÃ‚Â³n:** Iniciar sesiÃƒÂƒÃ‚Â³n con un usuario inexistente o con credenciales vacÃƒÂƒÃ‚Â­as (El ViewModel debe capturar la excepciÃƒÂƒÃ‚Â³n o el `null` y emitir el estado de `error` correspondiente).

---

## 6. Cobertura y EjecuciÃƒÂƒÃ‚Â³n de Tests

Para garantizar que nuestros tests efectivamente cubren la lÃƒÂƒÃ‚Â³gica de negocio, implementaremos las siguientes estrategias:

### A. EjecuciÃƒÂƒÃ‚Â³n de Pruebas (Comandos)
1.  **Pruebas Unitarias (JVM Locales):**
    *   Comando: `./gradlew testDebugUnitTest`
    *   *PropÃƒÂƒÃ‚Â³sito:* Ejecutar todas las pruebas de Use Cases y ViewModels de manera ultra rÃƒÂƒÃ‚Â¡pida sin necesidad de un emulador.
2.  **Pruebas Instrumentadas (Base de Datos):**
    *   Comando: `./gradlew connectedDebugAndroidTest`
    *   *PropÃƒÂƒÃ‚Â³sito:* Ejecutar las pruebas sobre los DAOs. Requiere que un dispositivo fÃƒÂƒÃ‚Â­sico o emulador estÃƒÂƒÃ‚Â© encendido y conectado.

### B. MediciÃƒÂƒÃ‚Â³n de Cobertura (Code Coverage)
Utilizaremos **KoverX** (o JaCoCo configurado para Kotlin) para generar reportes HTML visuales sobre quÃƒÂƒÃ‚Â© porcentaje de nuestro cÃƒÂƒÃ‚Â³digo estÃƒÂƒÃ‚Â¡ siendo probado.
*   **Comando de Cobertura (Android):** `./gradlew koverHtmlReportDebug` (Es fundamental usar la variante `Debug` para que Kover analice correctamente las clases instrumentadas de Android).
*   **Meta de Cobertura:**
    *   `domain` (Reglas de negocio y Use Cases): **MÃƒÂƒÃ‚Â­nimo 80%**. Esta capa es crÃƒÂƒÃ‚Â­tica.
    *   `data` (DAOs y Repositorios): **MÃƒÂƒÃ‚Â­nimo 70%**.
    *   `presentation` (UI): No requerirÃƒÂƒÃ‚Â¡ cobertura estricta en la fase inicial para priorizar velocidad.

### C. AutomatizaciÃƒÂƒÃ‚Â³n Continua (CI/CD) con GitHub Actions
Para asegurar que no se introduzcan regresiones al proyecto, hemos configurado un flujo de trabajo (Workflow) en GitHub Actions (`.github/workflows/pr_tests.yml`). 

**ÃƒÂ‚Ã‚Â¿QuÃƒÂƒÃ‚Â© hace automÃƒÂƒÃ‚Â¡ticamente?**
Cada vez que un desarrollador hace un *Push* o crea un *Pull Request* hacia las ramas `main` o `develop`:
1. El servidor de GitHub arranca un entorno virtual Linux con Java 17.
2. Ejecuta `./gradlew testDebugUnitTest` para validar todas nuestras pruebas de Use Cases y ViewModels.
3. Genera y sube el reporte de cobertura HTML (`koverHtmlReportDebug`) como un artefacto descargable.

**Nota sobre Tests Instrumentados:**
Los tests que requieren emulador (`connectedDebugAndroidTest`) no estÃƒÂ¡n incluidos de momento en el flujo bÃƒÂ¡sico para evitar tiempos muertos en la validaciÃƒÂ³n rÃƒÂ¡pida del PR, pero deben ejecutarse localmente antes de solicitar el PR.

---
*(Este documento se mantendrÃƒÂ¡ sincronizado con el cÃƒÂ³digo. Cualquier bug detectado en producciÃƒÂ³n en el futuro se traducirÃƒÂ¡ en un nuevo escenario "Given-When-Then" aquÃƒÂ­ antes de escribir el parche).*

---

## MÃƒÂ³dulo de Reportes

#### ReportesViewModel Ã¢Â€Â” StateFlows contextuales [#299]

**Test VM-R1: campanias emite lista vacÃƒÂ­a cuando la BD estÃƒÂ¡ vacÃƒÂ­a**
*   **Given:** El `ReportesViewModel` inicia con BD sin campaÃƒÂ±as.
*   **When:** Se observa el StateFlow `campanias`.
*   **Then:** Debe emitir una lista vacÃƒÂ­a.

**Test VM-R2: campanias emite la lista real cuando la BD tiene registros**
*   **Given:** La BD tiene 2 campaÃƒÂ±as registradas.
*   **When:** Se observa el StateFlow `campanias`.
*   **Then:** Debe emitir exactamente esas 2 campaÃƒÂ±as.

**Test VM-R3: seleccionarCampaniaIndividual actualiza campaniaIndividual**
*   **Given:** El ViewModel estÃƒÂ¡ inicializado sin selecciÃƒÂ³n (campaniaIndividual = null).
*   **When:** Se llama a `seleccionarCampaniaIndividual(campania)`.
*   **Then:** `campaniaIndividual` debe emitir la campaÃƒÂ±a elegida.

**Test VM-R4: insumosIndividual emite lista vacÃƒÂ­a cuando no hay campaÃƒÂ±a seleccionada**
*   **Given:** No hay campaÃƒÂ±a seleccionada.
*   **When:** Se observa `insumosIndividual`.
*   **Then:** Debe emitir lista vacÃƒÂ­a sin consultar la BD.

**Test VM-R5: pieChartData emite null cuando no hay campaÃƒÂ±a seleccionada**
*   **Given:** No hay campaÃƒÂ±a seleccionada (insumosIndividual vacÃƒÂ­o).
*   **When:** Se observa `pieChartData`.
*   **Then:** Debe emitir `null` (el grÃƒÂ¡fico no debe mostrarse).

#### ReportesViewModel Ã¢Â€Â” desglose cosechas por destino [#301]

**Test VM-R6: desgloseCosechasData agrupa por almacÃƒÂ©n y venta correctamente**
*   **Given:** Una campaÃƒÂ±a con cosechas mixtas (algunas con `almacen` no vacÃƒÂ­o, otras con `almacen` en blanco).
*   **When:** Se selecciona esa campaÃƒÂ±a con `seleccionarCampaniaIndividual()`.
*   **Then:** `desgloseCosechasData` debe emitir un `PieChartData` con 2 slices:
    - Slice "Almacenada": suma de cantidades con `almacen.isNotBlank()`.
    - Slice "Vendida": suma de cantidades con `almacen.isBlank()`.

**Test VM-R7: desgloseCosechasData emite null cuando no hay cosechas**
*   **Given:** Una campaÃƒÂ±a seleccionada pero sin cosechas en la BD.
*   **When:** Se observa `desgloseCosechasData`.
*   **Then:** Debe emitir `null` (sin grÃƒÂ¡fico).

#### ReportesViewModel Ã¢Â€Â” guardia de exportaciÃƒÂ³n [#300]

**Test VM-R8: exportarReporteCsv emite error si no hay campaÃƒÂ±a seleccionada**
*   **Given:** No hay campaÃƒÂ±a seleccionada (`campaniaIndividual = null`).
*   **When:** Se llama a `exportarReporteCsv(uri, context)`.
*   **Then:** `exportStatus` debe emitir `"Seleccione una campaÃƒÂ±a para exportar"` y no debe invocarse `ReportExporter`.

**Test VM-R9: exportarReportePdf emite error si no hay campaÃƒÂ±a seleccionada**
*   **Given:** No hay campaÃƒÂ±a seleccionada.
*   **When:** Se llama a `exportarReportePdf(uri, context)`.
*   **Then:** `exportStatus` debe emitir `"Seleccione una campaÃƒÂ±a para exportar"`.

#### ReportesViewModel Ã¢Â€Â” comparaciÃƒÂ³n real entre campaÃƒÂ±as [#302]

**Test VM-R10: cosechasA emite la lista de cosechas de la campaÃƒÂ±a A seleccionada**
*   **Given:** La BD tiene cosechas asociadas a la campaÃƒÂ±a con `id = 1`.
*   **When:** Se llama a `seleccionarCampaniaA(campaniaSoja)` donde `campaniaSoja.id = 1`.
*   **Then:** `cosechasA` debe emitir la lista real de cosechas de esa campaÃƒÂ±a.

**Test VM-R11: cosechasA emite lista vacÃƒÂ­a cuando no hay campaÃƒÂ±a A seleccionada**
*   **Given:** No hay campaÃƒÂ±a seleccionada en el comparador (campaniaA = null).
*   **When:** Se observa `cosechasA`.
*   **Then:** Debe emitir una lista vacÃƒÂ­a.


### ReportesViewModel
- **VM-R12:** Given misma campaÃ±a en A y B / When comparar / Then se emite estado de advertencia (UI lo maneja con condicional de igualdad de IDs).

## ValidarDatosCosechaUseCase
- **Dado** cantidad = null -> **Cuando** invoke() -> **Entonces** retorna Error("La cantidad debe ser mayor a 0.")
- **Dado** fecha = null -> **Cuando** invoke() -> **Entonces** retorna Error("La fecha es obligatoria.")
- **Dado** isAlmacenada=true y almacen en blanco -> **Cuando** invoke() -> **Entonces** retorna Error("El nombre del almacen o silo es obligatorio.")
- **Dado** todos los campos son vÃ¡lidos -> **Cuando** invoke() -> **Entonces** retorna Success

## FormularioInsumoViewModel Â— ValidaciÃ³n al guardar
- **Dado** nombre vacÃ­o y se llama guardar() -> **Cuando** validarInsumoUseCase devuelve error -> **Entonces** state.errorNombre != null y NO se llama al UseCase de inserciÃ³n
- **Dado** nombre vÃ¡lido, categoria vÃ¡lida -> **Cuando** guardar() -> **Entonces** se invoca el UseCase de inserciÃ³n
- **Dado** el usuario escribe en el campo nombre -> **Cuando** onNombreChange() -> **Entonces** errorNombre se limpia (sin validar aÃºn)


## FormularioCosechaViewModel - EdiciÃ³n y validaciÃ³n por campo (#335 / #336)

**Test VM-C6: Init con cosechaId vÃ¡lido carga la cosecha en el estado**
*   **Given:** SavedStateHandle contiene cosechaId = 7 y obtenerCosechaPorIdUseCase(7) retorna una cosecha con cantidad 55.0 y almacÃ©n "Silo A".
*   **When:** Se inicializa el ViewModel.
*   **Then:** state.cosechaId == 7, state.cantidad == "55.0", state.almacen == "Silo A", state.almacenado == true.

**Test VM-C7: Error de cantidad va a errorCantidad, no a errorFecha**
*   **Given:** CampaÃ±a seleccionada. ValidarDatosCosechaUseCase retorna Error("La cantidad debe ser mayor a 0.").
*   **When:** Se llama a guardar().
*   **Then:** errorCantidad != null, errorFecha == null.

**Test VM-C8: Error de fecha va a errorFecha, no a errorCantidad**
*   **Given:** CampaÃ±a y cantidad vÃ¡lidas. ValidarDatosCosechaUseCase retorna Error("La fecha es obligatoria.").
*   **When:** Se llama a guardar().
*   **Then:** errorFecha != null, errorCantidad == null.

**Test VM-C9: onFechaChange limpia errorFecha**
*   **Given:** Existe errorFecha en el state (provocado por un guardar fallido).
*   **When:** Se llama a onFechaChange(timestamp).
*   **Then:** errorFecha == null.
## FormularioInsumoViewModel - Habilitacion de boton guardar en tiempo real (#403)

**VM-I-1: Estado inicial en modo Alta tiene isGuardarHabilitado = false**
* **Dado** el ViewModel se inicializa sin insumoId (modo Alta).
* **Cuando** se observa el state.isGuardarHabilitado.
* **Entonces** debe ser false.

**VM-I-2: Tipear solo nombre no habilita el boton guardar**
* **Dado** el formulario esta en modo Alta.
* **Cuando** se llama a onNombreChange("Herbicida") y categoria esta vacia.
* **Entonces** isGuardarHabilitado debe seguir siendo false.

**VM-I-3: Tipear solo categoria no habilita el boton guardar**
* **Dado** el formulario esta en modo Alta.
* **Cuando** se llama a onCategoriaChange("Pesticidas") y nombre esta vacio.
* **Entonces** isGuardarHabilitado debe seguir siendo false.

**VM-I-4: Tipear nombre y categoria validos habilita el boton guardar**
* **Dado** el formulario esta en modo Alta.
* **Cuando** se llama a onNombreChange("Herbicida Total") y luego onCategoriaChange("Pesticidas").
* **Entonces** isGuardarHabilitado = true, errorNombre = null, errorCategoria = null.

**VM-I-5: Borrar nombre deshabilita el boton guardar**
* **Dado** el formulario tiene nombre y categoria validos (isGuardarHabilitado = true).
* **Cuando** se llama a onNombreChange("") vaciando el nombre.
* **Entonces** isGuardarHabilitado vuelve a false.

## NuevaTareaViewModel - Modo Edicion y preservacion de confirmar (#410)

**VM-T-E1: Modo edicion precarga datos de la tarea existente incluyendo confirmar**
* **Dado** existe una Tarea con id=5, nombre="Tarea Completada", confirmar=true en la BD.
* **Cuando** el ViewModel inicia con tareaId=5 en el SavedStateHandle.
* **Entonces** state.nombre = "Tarea Completada", state.confirmar = true, state.hora = "09:00".

**VM-T-E2: Editar tarea completada preserva confirmar=true al guardar**
* **Dado** una Tarea con confirmar=true esta cargada en modo edicion.
* **Cuando** se modifica el nombre y se llama a guardar().
* **Entonces** editarTareaUseCase recibe una Tarea con confirmar=true (no reseteado a false).



# Plan EstratÃƒÂ©gico y Casos de Prueba (Living Documentation)

Este documento centraliza la estrategia de testing del proyecto "Don Elio" y actÃƒÂºa como fuente de la verdad para escribir las pruebas automatizadas (Test Cases). Es un **Living Document** (Documento Vivo), lo que significa que **deberemos mantenerlo actualizado obligatoriamente** cada vez que modifiquemos el cÃƒÂ³digo o agreguemos nuevas funcionalidades, asegurando que las pruebas y la documentaciÃƒÂ³n no se desfasen.

## 1. Stack TecnolÃƒÂ³gico de Testing
*   **Unit Testing (Casos de Uso, ViewModels, Mappers):** `JUnit 4`, `MockK` (Mocks nativos Kotlin) y `Turbine` (Pruebas de flujos/Flows). 
    *   *Importante:* Para validar excepciones dentro de corrutinas (`runTest`), no se debe usar `assertThrows` de JUnit (ya que pierde el contexto suspendido), sino bloques nativos `try-catch` o `runCatching`.
*   **Pruebas de IntegraciÃƒÂ³n/Base de Datos (DAOs):** `AndroidX Test`, `Room Testing` (con `inMemoryDatabaseBuilder`) ejecutado en Emulador (Pruebas Instrumentadas).
*   **Pruebas de Interfaz de Usuario (UI):** `Compose Test Rule` nativo.

---

## 2. AnÃƒÂ¡lisis de Discrepancias (Documento 2025 vs Realidad 2026)

Al contrastar la propuesta del aÃƒÂ±o 2025 con la arquitectura real implementada en la App, detectamos e implementamos mejoras significativas que impactan la forma en que escribiremos los tests:

1.  **Redundancia de EdiciÃƒÂ³n (CampaÃƒÂ±as - CU2 y CU4):**
    *   *En 2025:* Se separaba "Entrar al menÃƒÂº" (CU2) de "Editar los campos" (CU4).
    *   *Realidad:* La arquitectura moderna expone un solo `EditarCampaniaUseCase`. AdemÃƒÂ¡s, se agregÃƒÂ³ el campo **`estaActiva`** a la entidad `Campania` para controlar estados (por ejemplo, si estÃƒÂ¡ terminada o en curso). Testearemos directamente la actualizaciÃƒÂ³n de este estado en BD.
2.  **Arquitectura de Notificaciones (Tareas - CU5):**
    *   *En 2025:* DependÃƒÂ­a de un "Actor Externo".
    *   *Realidad:* Reemplazado internamente por `WorkManagerTaskReminderScheduler`. Los tests de tareas deberÃƒÂ¡n validar (vÃƒÂ­a `MockK`) que el scheduler se mande a llamar o se cancele (ej. al completar o borrar una tarea).
3.  **UnificaciÃƒÂ³n de MÃƒÂ³dulo de Cosechas (CU6 y CU7):**
    *   *En 2025:* "Cosecha" (CU6) y "Datos no almacenados" (CU7) corrÃƒÂ­an por caminos distintos.
    *   *Realidad:* Bifurcamos la lÃƒÂ³gica limpiamente en `RegistrarCosechaUseCase` (para silos) y `RegistrarCosechaConVentaUseCase` (Venta o Reserva como alimento). Los tests cubrirÃƒÂ¡n ambas variantes de inserciÃƒÂ³n.
4.  **Refactor Total del MÃƒÂ³dulo de Insumos (CU9):**
    *   *En 2025:* Los insumos se creaban directamente vinculados a una campaÃƒÂ±a.
    *   *Realidad:* **Un cambio vital.** Ahora existe un CatÃƒÂ¡logo Global (`CrearInsumoCatalogoUseCase`) y posteriormente una vinculaciÃƒÂ³n a la campaÃƒÂ±a (`AsignarInsumoACampaniaUseCase`). AdemÃƒÂ¡s, el catÃƒÂ¡logo tiene la columna **`activo`**. Si el usuario elimina un insumo del catÃƒÂ¡logo (`EliminarInsumoCatalogoUseCase`), el test deberÃƒÂ¡ corroborar que **NO se hace un `DELETE` en la DB**, sino un `UPDATE activo = false` (Soft-Delete) para no corromper los histÃƒÂ³ricos de campaÃƒÂ±as pasadas.
5.  **MÃƒÂ³dulos Nuevos (No previstos en 2025):**
    *   *AutenticaciÃƒÂ³n:* `LoginUseCase` (SHA-256) y `RegistroUseCase`.
    *   *Backups:* `CrearBackupUseCase` y `RestaurarBackupUseCase` usando SAF de Android.

---

## 3. Pruebas Fuera de los Casos de Uso (Out of Scope Tests)

No toda la app es Casos de Uso. Existen componentes de bajo nivel y de infraestructura que testearemos independientemente:
*   **DAOs (Data Access Objects):** 
    Pruebas instrumentadas sobre `UsuarioDao`, `CampaniaDao`, `CampaniaInsumoDao` (validando foreign keys, borrados en cascada fÃƒÂ­sicos, y los queries filtrados por `activo = 1`).
*   **Mappers (Data <-> Domain):** 
    Pruebas unitarias para validar que al pasar de Entity a Domain Model no se pierda informaciÃƒÂ³n y viceversa.
*   **ViewModels (Presentation):** 
    Validar la emisiÃƒÂ³n correcta de los estados (`Loading`, `Success`, `Error`) hacia Jetpack Compose usando `Turbine` (ej: `LoginViewModelTest` verifica la transiciÃƒÂ³n a `isLoading = true` y luego `loginExitoso = true` o la asignaciÃƒÂ³n de mensajes de error).

---

## 4. Escenarios de Pruebas (Behavior-Driven Development - BDD)

A continuaciÃƒÂ³n, estructuramos los tests en formato `Given-When-Then` por mÃƒÂ³dulo, respetando el orden lÃƒÂ³gico de los Casos de Uso.

### MÃƒÂ³dulo de CampaÃƒÂ±as (CU1 - CU4)

**Test 1: Crear CampaÃƒÂ±a Exitosa**
*   **Given:** Un nombre vÃƒÂ¡lido "Trigo de Invierno", cultivo "Trigo" y una fecha correcta.
*   **When:** Invoco `CrearCampaniaUseCase`.
*   **Then:** El sistema debe insertar el registro en el repositorio y emitir el estado `Resource.Success`.

**Test 2: Crear CampaÃƒÂ±a con Errores**
*   **Given:** Un nombre vacÃƒÂ­o "".
*   **When:** Invoco `CrearCampaniaUseCase`.
*   **Then:** El sistema debe emitir `Resource.Error` con mensaje "El nombre no puede estar vacÃƒÂ­o" y NO llamar al repositorio.

**Test UC-V1: ValidarDatosCampaniaUseCase Ã¢Â€Â” Nombre vacÃƒÂ­o**
*   **Given:** nombre = "", cultivo = "Soja", fechaInicio = <fecha futura>, isEditMode = false
*   **When:** invoke(nombre, cultivo, fechaInicio, isEditMode)
*   **Then:** esValido = false, errorNombre = "El nombre es obligatorio"

**Test UC-V2: ValidarDatosCampaniaUseCase Ã¢Â€Â” Fecha pasada en creaciÃƒÂ³n**
*   **Given:** nombre = "CampaÃƒÂ±a", cultivo = "MaÃƒÂ­z", fechaInicio = <ayer en millis>, isEditMode = false
*   **When:** invoke(...)
*   **Then:** esValido = false, errorFecha = "La fecha no puede ser anterior a hoy"

**Test UC-V3: ValidarDatosCampaniaUseCase Ã¢Â€Â” Fecha pasada permitida en ediciÃƒÂ³n**
*   **Given:** nombre = "CampaÃƒÂ±a", cultivo = "MaÃƒÂ­z", fechaInicio = <ayer en millis>, isEditMode = true
*   **When:** invoke(...)
*   **Then:** esValido = true, errorFecha = null

**Test UC-V4: ValidarDatosCampaniaUseCase Ã¢Â€Â” Todos los campos vÃƒÂ¡lidos**
*   **Given:** Todos los campos correctos, isEditMode = false
*   **When:** invoke(...)
*   **Then:** esValido = true, todos los errores = null

### MÃƒÂ³dulo de Insumos (CU9 - CU9.4)

**Test 3: EliminaciÃƒÂ³n LÃƒÂ³gica (Soft-Delete) de Insumo del CatÃƒÂ¡logo**
*   **Given:** Que el insumo "Glifosato" existe en el catÃƒÂ¡logo con `activo = true` y ya fue utilizado en 2 campaÃƒÂ±as.
*   **When:** Invoco `EliminarInsumoCatalogoUseCase` pasando ese insumo.
*   **Then:** El repositorio debe realizar un `UPDATE` (cambiando `activo` a `false`) y NO un `DELETE` fÃƒÂ­sico. Las llamadas a `ObtenerCatalogoInsumosUseCase` ya no deben retornarlo.

**Test 4: AsignaciÃƒÂ³n de Insumo a CampaÃƒÂ±a**
*   **Given:** El "Glifosato" (activo en el catÃƒÂ¡logo) y la campaÃƒÂ±a "Trigo de Invierno".
*   **When:** Invoco `AsignarInsumoACampaniaUseCase` pasando `cantidad = 5` y `precio = 100`.
*   **Then:** Se crea un registro en `CampaniaInsumoEntity` relacionando los IDs y estableciendo el coste.

**Test UC-V5: ValidarInsumoUseCase Ã¢Â€Â” CategorÃƒÂ­a vacÃƒÂ­a**
*   **Given:** nombre = "Herbicida", categoria = ""
*   **When:** invoke(nombre, categoria)
*   **Then:** esValido = false, errorCategoria = "La categorÃƒÂ­a es obligatoria"

**Test UC-V6: ValidarInsumoUseCase Ã¢Â€Â” Ambos campos vÃƒÂ¡lidos**
*   **Given:** nombre = "Herbicida", categoria = "QuÃƒÂ­mico"
*   **When:** invoke(nombre, categoria)
*   **Then:** esValido = true, errorNombre = null, errorCategoria = null

### MÃƒÂ³dulo de Tareas (CU5 - CU5.4)

#### TareaViewModel Ã¢Â€Â” sincronizarCampania() [#292]

**Test VM-T1: sincronizarCampania actualiza el id cuando difiere del actual**
*   **Given:** El `TareaViewModel` inicia sin `campaniaId` en el `SavedStateHandle` (estado inicial `null`).
*   **When:** Se llama a `sincronizarCampania(5)`.
*   **Then:** El StateFlow `campaniaIdSeleccionada` debe emitir el valor `5`.

**Test VM-T2: sincronizarCampania no emite si el id es igual al actual**
*   **Given:** El `TareaViewModel` ya tiene `campaniaIdSeleccionada = 5`.
*   **When:** Se llama a `sincronizarCampania(5)` con el mismo valor.
*   **Then:** El StateFlow **no** debe emitir un nuevo evento (idempotencia garantizada).

**Test VM-T3: tareas emite lista vacÃƒÂ­a si no hay campaniaId vÃƒÂ¡lido**
*   **Given:** El `TareaViewModel` inicia sin `campaniaId` vÃƒÂ¡lido.
*   **When:** Se observa el StateFlow `tareas`.
*   **Then:** Debe emitir inmediatamente una lista vacÃƒÂ­a, sin llamar al repositorio.

**Test VM-T4: isCampaniaValid emite false cuando campaniaId es nulo**
*   **Given:** `campaniaIdSeleccionada` es `null`.
*   **When:** Se observa `isCampaniaValid`.
*   **Then:** Debe emitir `false`.

**Test VM-T5: isCampaniaValid emite true tras sincronizarCampania con id vÃƒÂ¡lido**
*   **Given:** El ViewModel inicia con `campaniaId = null`.
*   **When:** Se llama a `sincronizarCampania(3)`.
*   **Then:** `isCampaniaValid` debe emitir `true`.


**Test 5: Agendar tarea con recordatorio activado**
*   **Given:** Una nueva tarea "Revisar fertilizante" con el switch `notificar = true`.
*   **When:** Invoco `CrearTareaUseCase`.
*   **Then:** El sistema guarda la tarea en BD y, posteriormente, invoca `taskReminderScheduler.schedule(tarea)`.

**Test 6: Completar tarea programada (CancelaciÃƒÂ³n de Alerta)**
*   **Given:** La tarea anterior, que actualmente tiene notificaciones encoladas.
*   **When:** Invoco `ConfirmarTareaUseCase` seteando la tarea como `completada = true`.
*   **Then:** El estado de la tarea cambia en BD, y obligatoriamente se invoca `taskReminderScheduler.cancel(tarea.id)` para evitar alertas fantasma.

### MÃƒÂ³dulo de Cosechas (CU6 - CU7)

**Test 7: Registrar Cosecha No Almacenada (Venta/Reserva)**
*   **Given:** Una cosecha de "Soja" que no va al silo, sino que se vende (`venta = true`) a $100.
*   **When:** Invoco `RegistrarCosechaConVentaUseCase`.
*   **Then:** El sistema inserta el registro base en la tabla Cosechas, toma el ID generado, e inserta un segundo registro en `CosechaNoAlmacenadaEntity` vinculando la venta y el precio.

**Test 8: Listar Cosechas de una CampaÃƒÂ±a**
*   **Given:** Una campaÃƒÂ±a con cosechas mixtas (en silo y vendidas).
*   **When:** Invoco `ObtenerCosechasPorCampaniaUseCase` y `ObtenerCosechasNoAlmacenadasUseCase`.
*   **Then:** El repositorio debe devolver dos flujos distintos. El ViewModel debe ser capaz de fusionarlos para mostrar quÃƒÂ© fracciÃƒÂ³n de la cosecha total fue vendida.

**Test 8.1: Formulario de Cosecha - Sin CampaÃƒÂ±a Seleccionada (Issue 7)**
*   **Given:** Un `FormularioCosechaViewModel` creado sin `campaniaId` en el `SavedStateHandle` (acceso vÃƒÂ­a navegaciÃƒÂ³n global).
*   **When:** El usuario ingresa una cantidad vÃƒÂ¡lida y presiona "Guardar Registro".
*   **Then:** Se setea `errorCampania = "Debe seleccionar una campaÃƒÂ±a"` y no se llama a ningÃƒÂºn use case de registro.

**Test 8.2: Formulario de Cosecha - Cantidad Obligatoria (Issue 12)**
*   **Given:** Una campaÃƒÂ±a seleccionada y el campo `cantidad` vacÃƒÂ­o.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se setea `errorCantidad = "La cantidad es obligatoria"` y no se llama a ningÃƒÂºn use case de registro.

**Test 8.3: Formulario de Cosecha - Precio InvÃƒÂ¡lido**
*   **Given:** Una campaÃƒÂ±a y una `cantidad` vÃƒÂ¡lidas, con `almacenado = false`, `tipo = "Venta"` y un precio no numÃƒÂ©rico (ej. "abc").
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se setea `errorPrecio = "Precio invÃƒÂ¡lido"` y no se llama a ningÃƒÂºn use case de registro.

**Test 8.4: Formulario de Cosecha - Registro Exitoso (Almacenado)**
*   **Given:** Una campaÃƒÂ±a, `cantidad = 100`, y `almacen = "Silo 1"` vÃƒÂ¡lidos.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se llama a `RegistrarCosechaUseCase` con los parÃƒÂ¡metros correctos y se emite `guardadoExitoso = true`.

**Test 8.5: Formulario de Cosecha - Registro Exitoso (Venta)**
*   **Given:** Una campaÃƒÂ±a, `cantidad = 100`, `almacenado = false`, `tipo = "Venta"` y `precio = 500` vÃƒÂ¡lidos.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se llama a `RegistrarCosechaConVentaUseCase` con los parÃƒÂ¡metros correctos y se emite `guardadoExitoso = true`.

### MÃƒÂ³dulo de Observaciones (CU8)

**Test 9: Guardar ObservaciÃƒÂ³n con Imagen Adjunta**
*   **Given:** Una nota de texto y una URI local que apunta a una foto en el dispositivo.
*   **When:** Invoco `GuardarObservacionUseCase`.
*   **Then:** El sistema guarda correctamente el string de la URI en la entidad para que luego Coil pueda renderizarla en la UI.

### MÃƒÂ³dulo de AutenticaciÃƒÂ³n (Extra 1)

**Test 10: Login Exitoso con Hash SHA-256**
*   **Given:** Un usuario "DonElio" registrado en la base de datos con contraseÃƒÂ±a hasheada.
*   **When:** El usuario ingresa la contraseÃƒÂ±a en texto plano y se invoca `LoginUseCase`.
*   **Then:** El Use Case encripta el texto plano ingresado, lo compara con la BD, coincide, y emite `Resource.Success`.

**Test 11: Login Fallido (Usuario no existe)**
*   **Given:** Un intento de acceso con el nombre "Intruso".
*   **When:** Invoco `LoginUseCase`.
*   **Then:** Retorna `Resource.Error("Usuario no encontrado")`.

### MÃƒÂ³dulo de Backups (Extra 2)

**Test 12: GeneraciÃƒÂ³n de Backup Exitoso**
*   **Given:** Una ruta URI proporcionada por el SAF (Storage Access Framework) donde el usuario tiene permisos de escritura.
*   **When:** Invoco `CrearBackupUseCase`.
*   **Then:** El archivo `.db` se copia exitosamente al destino y emite `Resource.Success`.

---

## 5. Casos de Borde (Edge Cases) a Testear
*   **CampaÃƒÂ±as:** Intentar crear una campaÃƒÂ±a con nombre vacÃƒÂ­o (DeberÃƒÂ­a fallar con `Resource.Error`).
*   **Insumos:** Intentar vincular una cantidad nula o negativa de insumos a una campaÃƒÂ±a (Lanza `IllegalArgumentException`).
*   **Tareas:** Programar una tarea en el pasado con el switch de notificar en `true`. El `WorkManagerTaskReminderScheduler` no deberÃƒÂ­a encolar notificaciones retroactivas (debe validar que el delay calculado sea > 0).
*   **Observaciones:** Intentar guardar una observaciÃƒÂ³n con el campo de texto vacÃƒÂ­o (Lanza `IllegalArgumentException`).
*   **Cosechas (Formulario):** Guardar sin campaÃƒÂ±a seleccionada (Issue 7) Ã¢Â€Â” Debe emitir `errorCampania` y NO crashear por FK constraint; guardar con `cantidad` o `unidad` vacÃƒÂ­as (Issue 12) Ã¢Â€Â” Debe emitir el error visual correspondiente y deshabilitar el botÃƒÂ³n "Guardar".
*   **AutenticaciÃƒÂ³n:** Iniciar sesiÃƒÂ³n con un usuario inexistente o con credenciales vacÃƒÂ­as (El ViewModel debe capturar la excepciÃƒÂ³n o el `null` y emitir el estado de `error` correspondiente).

---

## 6. Cobertura y EjecuciÃƒÂ³n de Tests

Para garantizar que nuestros tests efectivamente cubren la lÃƒÂ³gica de negocio, implementaremos las siguientes estrategias:

### A. EjecuciÃƒÂ³n de Pruebas (Comandos)
1.  **Pruebas Unitarias (JVM Locales):**
    *   Comando: `./gradlew testDebugUnitTest`
    *   *PropÃƒÂ³sito:* Ejecutar todas las pruebas de Use Cases y ViewModels de manera ultra rÃƒÂ¡pida sin necesidad de un emulador.
2.  **Pruebas Instrumentadas (Base de Datos):**
    *   Comando: `./gradlew connectedDebugAndroidTest`
    *   *PropÃƒÂ³sito:* Ejecutar las pruebas sobre los DAOs. Requiere que un dispositivo fÃƒÂ­sico o emulador estÃƒÂ© encendido y conectado.

### B. MediciÃƒÂ³n de Cobertura (Code Coverage)
Utilizaremos **KoverX** (o JaCoCo configurado para Kotlin) para generar reportes HTML visuales sobre quÃƒÂ© porcentaje de nuestro cÃƒÂ³digo estÃƒÂ¡ siendo probado.
*   **Comando de Cobertura (Android):** `./gradlew koverHtmlReportDebug` (Es fundamental usar la variante `Debug` para que Kover analice correctamente las clases instrumentadas de Android).
*   **Meta de Cobertura:**
    *   `domain` (Reglas de negocio y Use Cases): **MÃƒÂ­nimo 80%**. Esta capa es crÃƒÂ­tica.
    *   `data` (DAOs y Repositorios): **MÃƒÂ­nimo 70%**.
    *   `presentation` (UI): No requerirÃƒÂ¡ cobertura estricta en la fase inicial para priorizar velocidad.

### C. AutomatizaciÃƒÂ³n Continua (CI/CD) con GitHub Actions
Para asegurar que no se introduzcan regresiones al proyecto, hemos configurado un flujo de trabajo (Workflow) en GitHub Actions (`.github/workflows/pr_tests.yml`). 

**Ã‚Â¿QuÃƒÂ© hace automÃƒÂ¡ticamente?**
Cada vez que un desarrollador hace un *Push* o crea un *Pull Request* hacia las ramas `main` o `develop`:
1. El servidor de GitHub arranca un entorno virtual Linux con Java 17.
2. Ejecuta `./gradlew testDebugUnitTest` para validar todas nuestras pruebas de Use Cases y ViewModels.
3. Genera y sube el reporte de cobertura HTML (`koverHtmlReportDebug`) como un artefacto descargable.

**Nota sobre Tests Instrumentados:**
Los tests que requieren emulador (`connectedDebugAndroidTest`) no estÃƒÂ¡n incluidos de momento en el flujo bÃƒÂ¡sico para evitar tiempos muertos en la validaciÃƒÂ³n rÃƒÂ¡pida del PR, pero deben ejecutarse localmente antes de solicitar el PR.

---
*(Este documento se mantendrÃƒÂ¡ sincronizado con el cÃƒÂ³digo. Cualquier bug detectado en producciÃƒÂ³n en el futuro se traducirÃƒÂ¡ en un nuevo escenario "Given-When-Then" aquÃƒÂ­ antes de escribir el parche).*

---

## MÃƒÂ³dulo de Reportes

#### ReportesViewModel Ã¢Â€Â” StateFlows contextuales [#299]

**Test VM-R1: campanias emite lista vacÃƒÂ­a cuando la BD estÃƒÂ¡ vacÃƒÂ­a**
*   **Given:** El `ReportesViewModel` inicia con BD sin campaÃƒÂ±as.
*   **When:** Se observa el StateFlow `campanias`.
*   **Then:** Debe emitir una lista vacÃƒÂ­a.

**Test VM-R2: campanias emite la lista real cuando la BD tiene registros**
*   **Given:** La BD tiene 2 campaÃƒÂ±as registradas.
*   **When:** Se observa el StateFlow `campanias`.
*   **Then:** Debe emitir exactamente esas 2 campaÃƒÂ±as.

**Test VM-R3: seleccionarCampaniaIndividual actualiza campaniaIndividual**
*   **Given:** El ViewModel estÃƒÂ¡ inicializado sin selecciÃƒÂ³n (campaniaIndividual = null).
*   **When:** Se llama a `seleccionarCampaniaIndividual(campania)`.
*   **Then:** `campaniaIndividual` debe emitir la campaÃƒÂ±a elegida.

**Test VM-R4: insumosIndividual emite lista vacÃƒÂ­a cuando no hay campaÃƒÂ±a seleccionada**
*   **Given:** No hay campaÃƒÂ±a seleccionada.
*   **When:** Se observa `insumosIndividual`.
*   **Then:** Debe emitir lista vacÃƒÂ­a sin consultar la BD.

**Test VM-R5: pieChartData emite null cuando no hay campaÃƒÂ±a seleccionada**
*   **Given:** No hay campaÃƒÂ±a seleccionada (insumosIndividual vacÃƒÂ­o).
*   **When:** Se observa `pieChartData`.
*   **Then:** Debe emitir `null` (el grÃƒÂ¡fico no debe mostrarse).

#### ReportesViewModel Ã¢Â€Â” desglose cosechas por destino [#301]

**Test VM-R6: desgloseCosechasData agrupa por almacÃƒÂ©n y venta correctamente**
*   **Given:** Una campaÃƒÂ±a con cosechas mixtas (algunas con `almacen` no vacÃƒÂ­o, otras con `almacen` en blanco).
*   **When:** Se selecciona esa campaÃƒÂ±a con `seleccionarCampaniaIndividual()`.
Los tests que requieren emulador (`connectedDebugAndroidTest`) no estÃƒÂƒÃ‚Â¡n incluidos de momento en el flujo bÃƒÂƒÃ‚Â¡sico para evitar tiempos muertos en la validaciÃƒÂƒÃ‚Â³n rÃƒÂƒÃ‚Â¡pida del PR, pero deben ejecutarse localmente antes de solicitar el PR.

---
*(Este documento se mantendrÃƒÂƒÃ‚Â¡ sincronizado con el cÃƒÂƒÃ‚Â³digo. Cualquier bug detectado en producciÃƒÂƒÃ‚Â³n en el futuro se traducirÃƒÂƒÃ‚Â¡ en un nuevo escenario "Given-When-Then" aquÃƒÂƒÃ‚Â­ antes de escribir el parche).*

---

## MÃƒÂƒÃ‚Â³dulo de Reportes

#### ReportesViewModel ÃƒÂ¢Ã‚Â€Ã‚Â” StateFlows contextuales [#299]

**Test VM-R1: campanias emite lista vacÃƒÂƒÃ‚Â­a cuando la BD estÃƒÂƒÃ‚Â¡ vacÃƒÂƒÃ‚Â­a**
*   **Given:** El `ReportesViewModel` inicia con BD sin campaÃƒÂƒÃ‚Â±as.
*   **When:** Se observa el StateFlow `campanias`.
*   **Then:** Debe emitir una lista vacÃƒÂƒÃ‚Â­a.

**Test VM-R2: campanias emite la lista real cuando la BD tiene registros**
*   **Given:** La BD tiene 2 campaÃƒÂƒÃ‚Â±as registradas.
*   **When:** Se observa el StateFlow `campanias`.
*   **Then:** Debe emitir exactamente esas 2 campaÃƒÂƒÃ‚Â±as.

**Test VM-R3: seleccionarCampaniaIndividual actualiza campaniaIndividual**
*   **Given:** El ViewModel estÃƒÂƒÃ‚Â¡ inicializado sin selecciÃƒÂƒÃ‚Â³n (campaniaIndividual = null).
*   **When:** Se llama a `seleccionarCampaniaIndividual(campania)`.
*   **Then:** `campaniaIndividual` debe emitir la campaÃƒÂƒÃ‚Â±a elegida.

**Test VM-R4: insumosIndividual emite lista vacÃƒÂƒÃ‚Â­a cuando no hay campaÃƒÂƒÃ‚Â±a seleccionada**
*   **Given:** No hay campaÃƒÂƒÃ‚Â±a seleccionada.
*   **When:** Se observa `insumosIndividual`.
*   **Then:** Debe emitir lista vacÃƒÂƒÃ‚Â­a sin consultar la BD.

**Test VM-R5: pieChartData emite null cuando no hay campaÃƒÂƒÃ‚Â±a seleccionada**
*   **Given:** No hay campaÃƒÂƒÃ‚Â±a seleccionada (insumosIndividual vacÃƒÂƒÃ‚Â­o).
*   **When:** Se observa `pieChartData`.
*   **Then:** Debe emitir `null` (el grÃƒÂƒÃ‚Â¡fico no debe mostrarse).

#### ReportesViewModel ÃƒÂ¢Ã‚Â€Ã‚Â” desglose cosechas por destino [#301]

**Test VM-R6: desgloseCosechasData agrupa por almacÃƒÂƒÃ‚Â©n y venta correctamente**
*   **Given:** Una campaÃƒÂƒÃ‚Â±a con cosechas mixtas (algunas con `almacen` no vacÃƒÂƒÃ‚Â­o, otras con `almacen` en blanco).
*   **When:** Se selecciona esa campaÃƒÂƒÃ‚Â±a con `seleccionarCampaniaIndividual()`.
*   **Then:** `desgloseCosechasData` debe emitir un `PieChartData` con 2 slices:
    - Slice "Almacenada": suma de cantidades con `almacen.isNotBlank()`.
    - Slice "Vendida": suma de cantidades con `almacen.isBlank()`.

**Test VM-R7: desgloseCosechasData emite null cuando no hay cosechas**
*   **Given:** Una campaÃƒÂ±a seleccionada pero sin cosechas en la BD.
*   **When:** Se observa `desgloseCosechasData`.
*   **Then:** Debe emitir `null` (sin grÃƒÂ¡fico).

#### ReportesViewModel Ã¢Â€Â” guardia de exportaciÃƒÂ³n [#300]

**Test VM-R8: exportarReporteCsv emite error si no hay campaÃƒÂ±a seleccionada**
*   **Given:** No hay campaÃƒÂ±a seleccionada (`campaniaIndividual = null`).
*   **When:** Se llama a `exportarReporteCsv(uri, context)`.
*   **Then:** `exportStatus` debe emitir `"Seleccione una campaÃƒÂ±a para exportar"` y no debe invocarse `ReportExporter`.

**Test VM-R9: exportarReportePdf emite error si no hay campaÃƒÂ±a seleccionada**
*   **Given:** No hay campaÃƒÂ±a seleccionada.
*   **When:** Se llama a `exportarReportePdf(uri, context)`.
*   **Then:** `exportStatus` debe emitir `"Seleccione una campaÃƒÂ±a para exportar"`.

#### ReportesViewModel Ã¢Â€Â” comparaciÃƒÂ³n real entre campaÃƒÂ±as [#302]

**Test VM-R10: cosechasA emite la lista de cosechas de la campaÃƒÂ±a A seleccionada**
*   **Given:** La BD tiene cosechas asociadas a la campaÃƒÂ±a con `id = 1`.
*   **When:** Se llama a `seleccionarCampaniaA(campaniaSoja)` donde `campaniaSoja.id = 1`.
*   **Then:** `cosechasA` debe emitir la lista real de cosechas de esa campaÃƒÂ±a.

**Test VM-R11: cosechasA emite lista vacÃƒÂ­a cuando no hay campaÃƒÂ±a A seleccionada**
*   **Given:** No hay campaÃƒÂ±a seleccionada en el comparador (campaniaA = null).
*   **When:** Se observa `cosechasA`.
*   **Then:** Debe emitir una lista vacÃƒÂ­a.


### ReportesViewModel
- **VM-R12:** Given misma campaÃ±a en A y B / When comparar / Then se emite estado de advertencia (UI lo maneja con condicional de igualdad de IDs).
*   **Given:** Una campaÃƒÂƒÃ‚Â±a seleccionada pero sin cosechas en la BD.
*   **When:** Se observa `desgloseCosechasData`.
*   **Then:** Debe emitir `null` (sin grÃƒÂƒÃ‚Â¡fico).

#### ReportesViewModel ÃƒÂ¢Ã‚Â€Ã‚Â” guardia de exportaciÃƒÂƒÃ‚Â³n [#300]

**Test VM-R8: exportarReporteCsv emite error si no hay campaÃƒÂƒÃ‚Â±a seleccionada**
*   **Given:** No hay campaÃƒÂƒÃ‚Â±a seleccionada (`campaniaIndividual = null`).
*   **When:** Se llama a `exportarReporteCsv(uri, context)`.
*   **Then:** `exportStatus` debe emitir `"Seleccione una campaÃƒÂƒÃ‚Â±a para exportar"` y no debe invocarse `ReportExporter`.

**Test VM-R9: exportarReportePdf emite error si no hay campaÃƒÂƒÃ‚Â±a seleccionada**
*   **Given:** No hay campaÃƒÂƒÃ‚Â±a seleccionada.
*   **When:** Se llama a `exportarReportePdf(uri, context)`.
*   **Then:** `exportStatus` debe emitir `"Seleccione una campaÃƒÂƒÃ‚Â±a para exportar"`.

#### ReportesViewModel ÃƒÂ¢Ã‚Â€Ã‚Â” comparaciÃƒÂƒÃ‚Â³n real entre campaÃƒÂƒÃ‚Â±as [#302]

**Test VM-R10: cosechasA emite la lista de cosechas de la campaÃƒÂƒÃ‚Â±a A seleccionada**
*   **Given:** La BD tiene cosechas asociadas a la campaÃƒÂƒÃ‚Â±a con `id = 1`.
*   **When:** Se llama a `seleccionarCampaniaA(campaniaSoja)` donde `campaniaSoja.id = 1`.
*   **Then:** `cosechasA` debe emitir la lista real de cosechas de esa campaÃƒÂƒÃ‚Â±a.

**Test VM-R11: cosechasA emite lista vacÃƒÂƒÃ‚Â­a cuando no hay campaÃƒÂƒÃ‚Â±a A seleccionada**
*   **Given:** No hay campaÃƒÂƒÃ‚Â±a seleccionada en el comparador (campaniaA = null).
*   **When:** Se observa `cosechasA`.
*   **Then:** Debe emitir una lista vacÃƒÂƒÃ‚Â­a.


### ReportesViewModel
- **VM-R12:** Given misma campaÃƒÂ±a en A y B / When comparar / Then se emite estado de advertencia (UI lo maneja con condicional de igualdad de IDs).

## ValidarDatosCosechaUseCase
- **Dado** cantidad = null -> **Cuando** invoke() -> **Entonces** retorna Error("La cantidad debe ser mayor a 0.")
- **Dado** fecha = null -> **Cuando** invoke() -> **Entonces** retorna Error("La fecha es obligatoria.")
- **Dado** isAlmacenada=true y almacen en blanco -> **Cuando** invoke() -> **Entonces** retorna Error("El nombre del almacen o silo es obligatorio.")
- **Dado** todos los campos son vÃ¡lidos -> **Cuando** invoke() -> **Entonces** retorna Success

## FormularioInsumoViewModel Â— ValidaciÃ³n al guardar
- **Dado** nombre vacÃ­o y se llama guardar() -> **Cuando** validarInsumoUseCase devuelve error -> **Entonces** state.errorNombre != null y NO se llama al UseCase de inserciÃ³n
- **Dado** nombre vÃ¡lido, categoria vÃ¡lida -> **Cuando** guardar() -> **Entonces** se invoca el UseCase de inserciÃ³n
- **Dado** el usuario escribe en el campo nombre -> **Cuando** onNombreChange() -> **Entonces** errorNombre se limpia (sin validar aÃºn)


## FormularioCosechaViewModel - EdiciÃ³n y validaciÃ³n por campo (#335 / #336)

**Test VM-C6: Init con cosechaId vÃ¡lido carga la cosecha en el estado**
*   **Given:** SavedStateHandle contiene cosechaId = 7 y obtenerCosechaPorIdUseCase(7) retorna una cosecha con cantidad 55.0 y almacÃ©n "Silo A".
- **Dado** todos los campos son vÃƒÂ¡lidos -> **Cuando** invoke() -> **Entonces** retorna Success

## FormularioInsumoViewModel Ã‚Â— ValidaciÃƒÂ³n al guardar
- **Dado** nombre vacÃƒÂ­o y se llama guardar() -> **Cuando** validarInsumoUseCase devuelve error -> **Entonces** state.errorNombre != null y NO se llama al UseCase de inserciÃƒÂ³n
- **Dado** nombre vÃƒÂ¡lido, categoria vÃƒÂ¡lida -> **Cuando** guardar() -> **Entonces** se invoca el UseCase de inserciÃƒÂ³n
- **Dado** el usuario escribe en el campo nombre -> **Cuando** onNombreChange() -> **Entonces** errorNombre se limpia (sin validar aÃƒÂºn)


## FormularioCosechaViewModel - EdiciÃƒÂ³n y validaciÃƒÂ³n por campo (#335 / #336)

**Test VM-C6: Init con cosechaId vÃƒÂ¡lido carga la cosecha en el estado**
*   **Given:** SavedStateHandle contiene cosechaId = 7 y obtenerCosechaPorIdUseCase(7) retorna una cosecha con cantidad 55.0 y almacÃƒÂ©n "Silo A".
*   **When:** Se inicializa el ViewModel.
*   **Then:** state.cosechaId == 7, state.cantidad == "55.0", state.almacen == "Silo A", state.almacenado == true.

**Test VM-C7: Error de cantidad va a errorCantidad, no a errorFecha**
*   **Given:** CampaÃ±a seleccionada. ValidarDatosCosechaUseCase retorna Error("La cantidad debe ser mayor a 0.").
*   **Given:** CampaÃƒÂ±a seleccionada. ValidarDatosCosechaUseCase retorna Error("La cantidad debe ser mayor a 0.").
*   **When:** Se llama a guardar().
*   **Then:** errorCantidad != null, errorFecha == null.

**Test VM-C8: Error de fecha va a errorFecha, no a errorCantidad**
*   **Given:** CampaÃ±a y cantidad vÃ¡lidas. ValidarDatosCosechaUseCase retorna Error("La fecha es obligatoria.").
*   **Given:** CampaÃƒÂ±a y cantidad vÃƒÂ¡lidas. ValidarDatosCosechaUseCase retorna Error("La fecha es obligatoria.").
*   **When:** Se llama a guardar().
*   **Then:** errorFecha != null, errorCantidad == null.

**Test VM-C9: onFechaChange limpia errorFecha**
*   **Given:** Existe errorFecha en el state (provocado por un guardar fallido).
*   **When:** Se llama a onFechaChange(timestamp).
*   **Then:** errorFecha == null.
## FormularioInsumoViewModel - Habilitacion de boton guardar en tiempo real (#403)

**VM-I-1: Estado inicial en modo Alta tiene isGuardarHabilitado = false**
* **Dado** el ViewModel se inicializa sin insumoId (modo Alta).
* **Cuando** se observa el state.isGuardarHabilitado.
* **Entonces** debe ser false.

**VM-I-2: Tipear solo nombre no habilita el boton guardar**
* **Dado** el formulario esta en modo Alta.
* **Cuando** se llama a onNombreChange("Herbicida") y categoria esta vacia.
* **Entonces** isGuardarHabilitado debe seguir siendo false.

**VM-I-3: Tipear solo categoria no habilita el boton guardar**
* **Dado** el formulario esta en modo Alta.
* **Cuando** se llama a onCategoriaChange("Pesticidas") y nombre esta vacio.
* **Entonces** isGuardarHabilitado debe seguir siendo false.

**VM-I-4: Tipear nombre y categoria validos habilita el boton guardar**
* **Dado** el formulario esta en modo Alta.
* **Cuando** se llama a onNombreChange("Herbicida Total") y luego onCategoriaChange("Pesticidas").
* **Entonces** isGuardarHabilitado = true, errorNombre = null, errorCategoria = null.

**VM-I-5: Borrar nombre deshabilita el boton guardar**
* **Dado** el formulario tiene nombre y categoria validos (isGuardarHabilitado = true).
* **Cuando** se llama a onNombreChange("") vaciando el nombre.
* **Entonces** isGuardarHabilitado vuelve a false.

## NuevaTareaViewModel - Modo Edicion y preservacion de confirmar (#410)

**VM-T-E1: Modo edicion precarga datos de la tarea existente incluyendo confirmar**
* **Dado** existe una Tarea con id=5, nombre="Tarea Completada", confirmar=true en la BD.
* **Cuando** el ViewModel inicia con tareaId=5 en el SavedStateHandle.
* **Entonces** state.nombre = "Tarea Completada", state.confirmar = true, state.hora = "09:00".

**VM-T-E2: Editar tarea completada preserva confirmar=true al guardar**
* **Dado** una Tarea con confirmar=true esta cargada en modo edicion.
* **Cuando** se modifica el nombre y se llama a guardar().
* **Entonces** editarTareaUseCase recibe una Tarea con confirmar=true (no reseteado a false).




## CosechaViewModel - Fix Race Condition (#441)

**VM-C-S1: campaniaId explÃ­cito en SavedState no se sobreescribe por el manager**
* **Dado** el ViewModel se crea con campaniaId = 5 en SavedStateHandle.
* **Cuando** el UltimaSeleccionManager emite id = 3.
* **Entonces** campaniaIdSeleccionada permanece en 5.

**VM-C-S2: sin campaniaId en SavedState el manager actÃºa como fallback**
* **Dado** el ViewModel se crea sin campaniaId en SavedStateHandle.
* **Cuando** el UltimaSeleccionManager emite id = 7.
* **Entonces** campaniaIdSeleccionada se actualiza a 7.

**VM-C-S3: sincronizarCampania actualiza el id cuando difiere del actual**
* **Dado** el ViewModel inicia sin campaniaId.
* **Cuando** se llama a sincronizarCampania(2).
* **Entonces** campaniaIdSeleccionada emite 2.

**VM-C-S4: sincronizarCampania no emite si el id es igual al actual (idempotente)**
* **Dado** el ViewModel tiene campaniaId = 4 en SavedState.
* **Cuando** se llama a sincronizarCampania(4).
* **Entonces** NO se emite un nuevo evento (expectNoEvents).

**VM-C-S5: isCampaniaValid emite false cuando campaniaId es nulo**
* **Dado** el ViewModel inicia sin campaniaId vÃ¡lido.
* **Cuando** se observa isCampaniaValid.
* **Entonces** isCampaniaValid = false.

**VM-C-S6: isCampaniaValid emite true tras sincronizarCampania con id vÃ¡lido**
* **Dado** el ViewModel inicia sin campaniaId.
* **Cuando** se llama a sincronizarCampania(1).
* **Entonces** isCampaniaValid = true.

## TareaViewModel - Fix Race Condition (#441)

**VM-T-S1: campaniaId explÃ­cito en SavedState no se sobreescribe por el manager**
* **Dado** el ViewModel se crea con campaniaId = 5 en SavedStateHandle.
* **Cuando** el UltimaSeleccionManager emite id = 3.
* **Entonces** filtroCampania permanece en 5.

**VM-T-S2: sin campaniaId en SavedState el manager actÃºa como fallback**
* **Dado** el ViewModel se crea sin campaniaId en SavedStateHandle.
* **Cuando** el UltimaSeleccionManager emite id = 7.
* **Entonces** filtroCampania se actualiza a 7.

## FormatearMoneda - Fix balance Dashboard (#437)

**FM-1: Formatea valor positivo en millones**
* **Dado** un valor de 6.133.500.
* **Cuando** se llama a formatearMoneda(6133500.0).
* **Entonces** devuelve "`$`6,1M".

**FM-2: Formatea valor positivo en miles**
* **Dado** un valor de 250.000.
* **Cuando** se llama a formatearMoneda(250000.0).
* **Entonces** devuelve "`$`250K".

**FM-3: Formatea balance negativo en millones con signo al frente**
* **Dado** un balance negativo de -6.133.500 (caso del bug reportado).
* **Cuando** se llama a formatearMoneda(-6133500.0).
* **Entonces** devuelve "-`$`6,1M" con el signo al frente (no al final como harÃ­a NumberFormat de locale es_AR).

**FM-4: Formatea balance negativo en miles**
* **Dado** un balance de -250.000.
* **Cuando** se llama a formatearMoneda(-250000.0).
* **Entonces** devuelve "-`$`250K".

**FM-5: Formatea cero**
* **Dado** valor = 0.0.
* **Cuando** se llama a formatearMoneda(0.0).
* **Entonces** devuelve "`$` ".

**FM-6: Formatea exactamente 1.000.000**
* **Dado** valor = 1.000.000.
* **Cuando** se llama a formatearMoneda(1000000.0).
* **Entonces** devuelve "`$`1,0M".

## InsumoVinculacionViewModel - Pantalla unificada VincularInsumoScreen (#439)

**VM-I-S1: Asignar insumo utiliza el campaniaId correcto**
* **Dado** un InsumoVinculacionViewModel con una campaÃ±a seleccionada (campaniaId).
* **Cuando** se llama a signarInsumo(idInsumo, cantidad, precio).
* **Entonces** llama a AsignarInsumoACampaniaUseCase pasando correctamente el idCampania desde el estado, junto a idInsumo, cantidad y precio.

## FormularioCosechaViewModel - Fixes verificacion manual (fix/cosechas-dashboard-vinculacion)

**FC-1: Fallback al Singleton cuando no llega campaniaId por navegacion**
* **Dado** un FormularioCosechaViewModel instanciado SIN campaniaId en SavedStateHandle y el UltimaSeleccionManager tiene el id=5.
* **Cuando** se inicializa el ViewModel.
* **Entonces** state.campaniaId es 5 (tomado del Singleton).

**FC-2: campaniaId explicito tiene prioridad sobre el Singleton**
* **Dado** un FormularioCosechaViewModel instanciado con campaniaId=3 en SavedStateHandle y el Singleton tiene id=9.
* **Cuando** se inicializa el ViewModel.
* **Entonces** state.campaniaId es 3 (no 9).

**FC-3: Sanitizacion de coma en onCantidadChange**
* **Dado** el usuario ingresa "1234,56" en el campo cantidad.
* **Cuando** se llama a onCantidadChange("1234,56").
* **Entonces** state.cantidad es "1234.56" (coma reemplazada por punto) y errorCantidad es null.

**FC-4: Sanitizacion de coma en onPrecioChange**
* **Dado** el usuario ingresa "100.000,50" en el campo precio.
* **Cuando** se llama a onPrecioChange("100.000,50").
* **Entonces** state.precio es "100.000.50" y no hay errorPrecio.

**FC-5: Carga de detalle de venta al editar cosecha no almacenada**
* **Dado** una cosecha con almacen="" y un CosechaNoAlmacenada asociado (tipo="venta", precio=150000).
* **Cuando** se inicializa el ViewModel con cosechaId de esa cosecha.
* **Entonces** state.tipo es "venta" y state.precio es "150000.0" y state.almacenado es false.

**FC-6: Guardado edicion de cosecha no almacenada actualiza ambas tablas**
* **Dado** un ViewModel en modo edicion con cosechaId y state valido (no almacenada).
* **Cuando** se llama a guardar().
* **Entonces** se invoca EditarCosechaConVentaUseCase con esAlmacenada=false, tipo y precioTotal correctos.

## ObtenerResumenRendimientoUseCase - Fix calculo Dashboard

**DR-1: Ingresos brutos = suma de precios totales de ventas (no cantidad x precio)**
* **Dado** una venta de 1000 Tn con precio total = 150000.
* **Cuando** se calcula el resumen mensual.
* **Entonces** ingresosBrutos == 150000.0 (NO 150.000.000).

**DR-2: Solo las ventas (tipo="venta") suman a ingresos brutos**
* **Dado** una CosechaNoAlmacenada con tipo="reserva" y precio=50000.
* **Cuando** se calcula el resumen mensual.
* **Entonces** ingresosBrutos == 0 (no incluye reservas).

## Fixes adicionales Verificacion Manual 2 (fix/cosechas-dashboard-vinculacion)

**FC-7: Formateo de cantidades y monedas estandarizado (es_AR)**
* **Dado** la pantalla de Insumos, Cosechas o Dashboard.
* **Cuando** se muestran valores como 1234.56.
* **Entonces** se muestran con el formato "1.234,56".

**DC-1: El menÃº de Detalle de CampaÃ±a muestra todas las tareas**
* **Dado** una campaÃ±a con 1 tarea pendiente y 1 completada.
* **Cuando** se visualiza el CardModuloTareas.
* **Entonces** dice "1 pendientes" y "1 completadas" (antes decÃ­a 0 completadas).

**DC-2: El menÃº de Detalle de CampaÃ±a muestra todas las cosechas**
* **Dado** una campaÃ±a con 1 cosecha almacenada de 5 Tn y 1 venta de 2 Tn.
* **Cuando** se visualiza el CardModuloCosechas.
* **Entonces** el total cosechado muestra 7 Tn (antes solo mostraba las almacenadas) y usa sufijo "Tn" en lugar de "Kg".

**DR-3: El cÃ¡lculo de ingresos detecta precio mayor a 0 en lugar de la palabra "venta"**
* **Dado** una cosecha no almacenada cuyo tipo es "Reserva Especial" y precio = 50000.
* **Cuando** se calcula el resumen mensual.
* **Entonces** ingresosBrutos incluye esos 50000.
# Plan EstratÃƒÂƒÃ‚Â©gico y Casos de Prueba (Living Documentation)

Este documento centraliza la estrategia de testing del proyecto "Don Elio" y actÃƒÂƒÃ‚Âºa como fuente de la verdad para escribir las pruebas automatizadas (Test Cases). Es un **Living Document** (Documento Vivo), lo que significa que **deberemos mantenerlo actualizado obligatoriamente** cada vez que modifiquemos el cÃƒÂƒÃ‚Â³digo o agreguemos nuevas funcionalidades, asegurando que las pruebas y la documentaciÃƒÂƒÃ‚Â³n no se desfasen.

## 1. Stack TecnolÃƒÂƒÃ‚Â³gico de Testing
*   **Unit Testing (Casos de Uso, ViewModels, Mappers):** `JUnit 4`, `MockK` (Mocks nativos Kotlin) y `Turbine` (Pruebas de flujos/Flows). 
    *   *Importante:* Para validar excepciones dentro de corrutinas (`runTest`), no se debe usar `assertThrows` de JUnit (ya que pierde el contexto suspendido), sino bloques nativos `try-catch` o `runCatching`.
*   **Pruebas de IntegraciÃƒÂƒÃ‚Â³n/Base de Datos (DAOs):** `AndroidX Test`, `Room Testing` (con `inMemoryDatabaseBuilder`) ejecutado en Emulador (Pruebas Instrumentadas).
*   **Pruebas de Interfaz de Usuario (UI):** `Compose Test Rule` nativo.

---

## 2. AnÃƒÂƒÃ‚Â¡lisis de Discrepancias (Documento 2025 vs Realidad 2026)

Al contrastar la propuesta del aÃƒÂƒÃ‚Â±o 2025 con la arquitectura real implementada en la App, detectamos e implementamos mejoras significativas que impactan la forma en que escribiremos los tests:

1.  **Redundancia de EdiciÃƒÂƒÃ‚Â³n (CampaÃƒÂƒÃ‚Â±as - CU2 y CU4):**
    *   *En 2025:* Se separaba "Entrar al menÃƒÂƒÃ‚Âº" (CU2) de "Editar los campos" (CU4).
    *   *Realidad:* La arquitectura moderna expone un solo `EditarCampaniaUseCase`. AdemÃƒÂƒÃ‚Â¡s, se agregÃƒÂƒÃ‚Â³ el campo **`estaActiva`** a la entidad `Campania` para controlar estados (por ejemplo, si estÃƒÂƒÃ‚Â¡ terminada o en curso). Testearemos directamente la actualizaciÃƒÂƒÃ‚Â³n de este estado en BD.
2.  **Arquitectura de Notificaciones (Tareas - CU5):**
    *   *En 2025:* DependÃƒÂƒÃ‚Â­a de un "Actor Externo".
    *   *Realidad:* Reemplazado internamente por `WorkManagerTaskReminderScheduler`. Los tests de tareas deberÃƒÂƒÃ‚Â¡n validar (vÃƒÂƒÃ‚Â­a `MockK`) que el scheduler se mande a llamar o se cancele (ej. al completar o borrar una tarea).
3.  **UnificaciÃƒÂƒÃ‚Â³n de MÃƒÂƒÃ‚Â³dulo de Cosechas (CU6 y CU7):**
    *   *En 2025:* "Cosecha" (CU6) y "Datos no almacenados" (CU7) corrÃƒÂƒÃ‚Â­an por caminos distintos.
    *   *Realidad:* Bifurcamos la lÃƒÂƒÃ‚Â³gica limpiamente en `RegistrarCosechaUseCase` (para silos) y `RegistrarCosechaConVentaUseCase` (Venta o Reserva como alimento). Los tests cubrirÃƒÂƒÃ‚Â¡n ambas variantes de inserciÃƒÂƒÃ‚Â³n.
4.  **Refactor Total del MÃƒÂƒÃ‚Â³dulo de Insumos (CU9):**
    *   *En 2025:* Los insumos se creaban directamente vinculados a una campaÃƒÂƒÃ‚Â±a.
    *   *Realidad:* **Un cambio vital.** Ahora existe un CatÃƒÂƒÃ‚Â¡logo Global (`CrearInsumoCatalogoUseCase`) y posteriormente una vinculaciÃƒÂƒÃ‚Â³n a la campaÃƒÂƒÃ‚Â±a (`AsignarInsumoACampaniaUseCase`). AdemÃƒÂƒÃ‚Â¡s, el catÃƒÂƒÃ‚Â¡logo tiene la columna **`activo`**. Si el usuario elimina un insumo del catÃƒÂƒÃ‚Â¡logo (`EliminarInsumoCatalogoUseCase`), el test deberÃƒÂƒÃ‚Â¡ corroborar que **NO se hace un `DELETE` en la DB**, sino un `UPDATE activo = false` (Soft-Delete) para no corromper los histÃƒÂƒÃ‚Â³ricos de campaÃƒÂƒÃ‚Â±as pasadas.
5.  **MÃƒÂƒÃ‚Â³dulos Nuevos (No previstos en 2025):**
    *   *AutenticaciÃƒÂƒÃ‚Â³n:* `LoginUseCase` (SHA-256) y `RegistroUseCase`.
    *   *Backups:* `CrearBackupUseCase` y `RestaurarBackupUseCase` usando SAF de Android.

---

## 3. Pruebas Fuera de los Casos de Uso (Out of Scope Tests)

No toda la app es Casos de Uso. Existen componentes de bajo nivel y de infraestructura que testearemos independientemente:
*   **DAOs (Data Access Objects):** 
    Pruebas instrumentadas sobre `UsuarioDao`, `CampaniaDao`, `CampaniaInsumoDao` (validando foreign keys, borrados en cascada fÃƒÂƒÃ‚Â­sicos, y los queries filtrados por `activo = 1`).
*   **Mappers (Data <-> Domain):** 
    Pruebas unitarias para validar que al pasar de Entity a Domain Model no se pierda informaciÃƒÂƒÃ‚Â³n y viceversa.
*   **ViewModels (Presentation):** 
    Validar la emisiÃƒÂƒÃ‚Â³n correcta de los estados (`Loading`, `Success`, `Error`) hacia Jetpack Compose usando `Turbine` (ej: `LoginViewModelTest` verifica la transiciÃƒÂƒÃ‚Â³n a `isLoading = true` y luego `loginExitoso = true` o la asignaciÃƒÂƒÃ‚Â³n de mensajes de error).

---

## 4. Escenarios de Pruebas (Behavior-Driven Development - BDD)

A continuaciÃƒÂƒÃ‚Â³n, estructuramos los tests en formato `Given-When-Then` por mÃƒÂƒÃ‚Â³dulo, respetando el orden lÃƒÂƒÃ‚Â³gico de los Casos de Uso.

### MÃƒÂƒÃ‚Â³dulo de CampaÃƒÂƒÃ‚Â±as (CU1 - CU4)

**Test 1: Crear CampaÃƒÂƒÃ‚Â±a Exitosa**
*   **Given:** Un nombre vÃƒÂƒÃ‚Â¡lido "Trigo de Invierno", cultivo "Trigo" y una fecha correcta.
*   **When:** Invoco `CrearCampaniaUseCase`.
*   **Then:** El sistema debe insertar el registro en el repositorio y emitir el estado `Resource.Success`.

**Test 2: Crear CampaÃƒÂƒÃ‚Â±a con Errores**
*   **Given:** Un nombre vacÃƒÂƒÃ‚Â­o "".
*   **When:** Invoco `CrearCampaniaUseCase`.
*   **Then:** El sistema debe emitir `Resource.Error` con mensaje "El nombre no puede estar vacÃƒÂƒÃ‚Â­o" y NO llamar al repositorio.

**Test UC-V1: ValidarDatosCampaniaUseCase ÃƒÂ¢Ã‚Â€Ã‚Â” Nombre vacÃƒÂƒÃ‚Â­o**
*   **Given:** nombre = "", cultivo = "Soja", fechaInicio = <fecha futura>, isEditMode = false
*   **When:** invoke(nombre, cultivo, fechaInicio, isEditMode)
*   **Then:** esValido = false, errorNombre = "El nombre es obligatorio"

**Test UC-V2: ValidarDatosCampaniaUseCase ÃƒÂ¢Ã‚Â€Ã‚Â” Fecha pasada en creaciÃƒÂƒÃ‚Â³n**
*   **Given:** nombre = "CampaÃƒÂƒÃ‚Â±a", cultivo = "MaÃƒÂƒÃ‚Â­z", fechaInicio = <ayer en millis>, isEditMode = false
*   **When:** invoke(...)
*   **Then:** esValido = false, errorFecha = "La fecha no puede ser anterior a hoy"

**Test UC-V3: ValidarDatosCampaniaUseCase ÃƒÂ¢Ã‚Â€Ã‚Â” Fecha pasada permitida en ediciÃƒÂƒÃ‚Â³n**
*   **Given:** nombre = "CampaÃƒÂƒÃ‚Â±a", cultivo = "MaÃƒÂƒÃ‚Â­z", fechaInicio = <ayer en millis>, isEditMode = true
*   **When:** invoke(...)
*   **Then:** esValido = true, errorFecha = null

**Test UC-V4: ValidarDatosCampaniaUseCase ÃƒÂ¢Ã‚Â€Ã‚Â” Todos los campos vÃƒÂƒÃ‚Â¡lidos**
*   **Given:** Todos los campos correctos, isEditMode = false
*   **When:** invoke(...)
*   **Then:** esValido = true, todos los errores = null

### MÃƒÂƒÃ‚Â³dulo de Insumos (CU9 - CU9.4)

**Test 3: EliminaciÃƒÂƒÃ‚Â³n LÃƒÂƒÃ‚Â³gica (Soft-Delete) de Insumo del CatÃƒÂƒÃ‚Â¡logo**
*   **Given:** Que el insumo "Glifosato" existe en el catÃƒÂƒÃ‚Â¡logo con `activo = true` y ya fue utilizado en 2 campaÃƒÂƒÃ‚Â±as.
*   **When:** Invoco `EliminarInsumoCatalogoUseCase` pasando ese insumo.
*   **Then:** El repositorio debe realizar un `UPDATE` (cambiando `activo` a `false`) y NO un `DELETE` fÃƒÂƒÃ‚Â­sico. Las llamadas a `ObtenerCatalogoInsumosUseCase` ya no deben retornarlo.

**Test 4: AsignaciÃƒÂƒÃ‚Â³n de Insumo a CampaÃƒÂƒÃ‚Â±a**
*   **Given:** El "Glifosato" (activo en el catÃƒÂƒÃ‚Â¡logo) y la campaÃƒÂƒÃ‚Â±a "Trigo de Invierno".
*   **When:** Invoco `AsignarInsumoACampaniaUseCase` pasando `cantidad = 5` y `precio = 100`.
*   **Then:** Se crea un registro en `CampaniaInsumoEntity` relacionando los IDs y estableciendo el coste.

**Test UC-V5: ValidarInsumoUseCase ÃƒÂ¢Ã‚Â€Ã‚Â” CategorÃƒÂƒÃ‚Â­a vacÃƒÂƒÃ‚Â­a**
*   **Given:** nombre = "Herbicida", categoria = ""
*   **When:** invoke(nombre, categoria)
*   **Then:** esValido = false, errorCategoria = "La categorÃƒÂƒÃ‚Â­a es obligatoria"

**Test UC-V6: ValidarInsumoUseCase ÃƒÂ¢Ã‚Â€Ã‚Â” Ambos campos vÃƒÂƒÃ‚Â¡lidos**
*   **Given:** nombre = "Herbicida", categoria = "QuÃƒÂƒÃ‚Â­mico"
*   **When:** invoke(nombre, categoria)
*   **Then:** esValido = true, errorNombre = null, errorCategoria = null

### MÃƒÂƒÃ‚Â³dulo de Tareas (CU5 - CU5.4)

#### TareaViewModel ÃƒÂ¢Ã‚Â€Ã‚Â” sincronizarCampania() [#292]

**Test VM-T1: sincronizarCampania actualiza el id cuando difiere del actual**
*   **Given:** El `TareaViewModel` inicia sin `campaniaId` en el `SavedStateHandle` (estado inicial `null`).
*   **When:** Se llama a `sincronizarCampania(5)`.
*   **Then:** El StateFlow `campaniaIdSeleccionada` debe emitir el valor `5`.

**Test VM-T2: sincronizarCampania no emite si el id es igual al actual**
*   **Given:** El `TareaViewModel` ya tiene `campaniaIdSeleccionada = 5`.
*   **When:** Se llama a `sincronizarCampania(5)` con el mismo valor.
*   **Then:** El StateFlow **no** debe emitir un nuevo evento (idempotencia garantizada).

**Test VM-T3: tareas emite lista vacÃƒÂƒÃ‚Â­a si no hay campaniaId vÃƒÂƒÃ‚Â¡lido**
*   **Given:** El `TareaViewModel` inicia sin `campaniaId` vÃƒÂƒÃ‚Â¡lido.
*   **When:** Se observa el StateFlow `tareas`.
*   **Then:** Debe emitir inmediatamente una lista vacÃƒÂƒÃ‚Â­a, sin llamar al repositorio.

**Test VM-T4: isCampaniaValid emite false cuando campaniaId es nulo**
*   **Given:** `campaniaIdSeleccionada` es `null`.
*   **When:** Se observa `isCampaniaValid`.
*   **Then:** Debe emitir `false`.

**Test VM-T5: isCampaniaValid emite true tras sincronizarCampania con id vÃƒÂƒÃ‚Â¡lido**
*   **Given:** El ViewModel inicia con `campaniaId = null`.
*   **When:** Se llama a `sincronizarCampania(3)`.
*   **Then:** `isCampaniaValid` debe emitir `true`.


**Test 5: Agendar tarea con recordatorio activado**
*   **Given:** Una nueva tarea "Revisar fertilizante" con el switch `notificar = true`.
*   **When:** Invoco `CrearTareaUseCase`.
*   **Then:** El sistema guarda la tarea en BD y, posteriormente, invoca `taskReminderScheduler.schedule(tarea)`.

**Test 6: Completar tarea programada (CancelaciÃƒÂƒÃ‚Â³n de Alerta)**
*   **Given:** La tarea anterior, que actualmente tiene notificaciones encoladas.
*   **When:** Invoco `ConfirmarTareaUseCase` seteando la tarea como `completada = true`.
*   **Then:** El estado de la tarea cambia en BD, y obligatoriamente se invoca `taskReminderScheduler.cancel(tarea.id)` para evitar alertas fantasma.

### MÃƒÂƒÃ‚Â³dulo de Cosechas (CU6 - CU7)

**Test 7: Registrar Cosecha No Almacenada (Venta/Reserva)**
*   **Given:** Una cosecha de "Soja" que no va al silo, sino que se vende (`venta = true`) a $100.
*   **When:** Invoco `RegistrarCosechaConVentaUseCase`.
*   **Then:** El sistema inserta el registro base en la tabla Cosechas, toma el ID generado, e inserta un segundo registro en `CosechaNoAlmacenadaEntity` vinculando la venta y el precio.

**Test 8: Listar Cosechas de una CampaÃƒÂƒÃ‚Â±a**
*   **Given:** Una campaÃƒÂƒÃ‚Â±a con cosechas mixtas (en silo y vendidas).
*   **When:** Invoco `ObtenerCosechasPorCampaniaUseCase` y `ObtenerCosechasNoAlmacenadasUseCase`.
*   **Then:** El repositorio debe devolver dos flujos distintos. El ViewModel debe ser capaz de fusionarlos para mostrar quÃƒÂƒÃ‚Â© fracciÃƒÂƒÃ‚Â³n de la cosecha total fue vendida.

**Test 8.1: Formulario de Cosecha - Sin CampaÃƒÂƒÃ‚Â±a Seleccionada (Issue 7)**
*   **Given:** Un `FormularioCosechaViewModel` creado sin `campaniaId` en el `SavedStateHandle` (acceso vÃƒÂƒÃ‚Â­a navegaciÃƒÂƒÃ‚Â³n global).
*   **When:** El usuario ingresa una cantidad vÃƒÂƒÃ‚Â¡lida y presiona "Guardar Registro".
*   **Then:** Se setea `errorCampania = "Debe seleccionar una campaÃƒÂƒÃ‚Â±a"` y no se llama a ningÃƒÂƒÃ‚Âºn use case de registro.

**Test 8.2: Formulario de Cosecha - Cantidad Obligatoria (Issue 12)**
*   **Given:** Una campaÃƒÂƒÃ‚Â±a seleccionada y el campo `cantidad` vacÃƒÂƒÃ‚Â­o.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se setea `errorCantidad = "La cantidad es obligatoria"` y no se llama a ningÃƒÂƒÃ‚Âºn use case de registro.

**Test 8.3: Formulario de Cosecha - Precio InvÃƒÂƒÃ‚Â¡lido**
*   **Given:** Una campaÃƒÂƒÃ‚Â±a y una `cantidad` vÃƒÂƒÃ‚Â¡lidas, con `almacenado = false`, `tipo = "Venta"` y un precio no numÃƒÂƒÃ‚Â©rico (ej. "abc").
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se setea `errorPrecio = "Precio invÃƒÂƒÃ‚Â¡lido"` y no se llama a ningÃƒÂƒÃ‚Âºn use case de registro.

**Test 8.4: Formulario de Cosecha - Registro Exitoso (Almacenado)**
*   **Given:** Una campaÃƒÂƒÃ‚Â±a, `cantidad = 100`, y `almacen = "Silo 1"` vÃƒÂƒÃ‚Â¡lidos.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se llama a `RegistrarCosechaUseCase` con los parÃƒÂƒÃ‚Â¡metros correctos y se emite `guardadoExitoso = true`.

**Test 8.5: Formulario de Cosecha - Registro Exitoso (Venta)**
*   **Given:** Una campaÃƒÂƒÃ‚Â±a, `cantidad = 100`, `almacenado = false`, `tipo = "Venta"` y `precio = 500` vÃƒÂƒÃ‚Â¡lidos.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se llama a `RegistrarCosechaConVentaUseCase` con los parÃƒÂƒÃ‚Â¡metros correctos y se emite `guardadoExitoso = true`.

### MÃƒÂƒÃ‚Â³dulo de Observaciones (CU8)

**Test 9: Guardar ObservaciÃƒÂƒÃ‚Â³n con Imagen Adjunta**
*   **Given:** Una nota de texto y una URI local que apunta a una foto en el dispositivo.
*   **When:** Invoco `GuardarObservacionUseCase`.
*   **Then:** El sistema guarda correctamente el string de la URI en la entidad para que luego Coil pueda renderizarla en la UI.

### MÃƒÂƒÃ‚Â³dulo de AutenticaciÃƒÂƒÃ‚Â³n (Extra 1)

**Test 10: Login Exitoso con Hash SHA-256**
*   **Given:** Un usuario "DonElio" registrado en la base de datos con contraseÃƒÂƒÃ‚Â±a hasheada.
*   **When:** El usuario ingresa la contraseÃƒÂƒÃ‚Â±a en texto plano y se invoca `LoginUseCase`.
*   **Then:** El Use Case encripta el texto plano ingresado, lo compara con la BD, coincide, y emite `Resource.Success`.

**Test 11: Login Fallido (Usuario no existe)**
*   **Given:** Un intento de acceso con el nombre "Intruso".
*   **When:** Invoco `LoginUseCase`.
*   **Then:** Retorna `Resource.Error("Usuario no encontrado")`.

### MÃƒÂƒÃ‚Â³dulo de Backups (Extra 2)

**Test 12: GeneraciÃƒÂƒÃ‚Â³n de Backup Exitoso**
*   **Given:** Una ruta URI proporcionada por el SAF (Storage Access Framework) donde el usuario tiene permisos de escritura.
*   **When:** Invoco `CrearBackupUseCase`.
*   **Then:** El archivo `.db` se copia exitosamente al destino y emite `Resource.Success`.

---

## 5. Casos de Borde (Edge Cases) a Testear
*   **CampaÃƒÂƒÃ‚Â±as:** Intentar crear una campaÃƒÂƒÃ‚Â±a con nombre vacÃƒÂƒÃ‚Â­o (DeberÃƒÂƒÃ‚Â­a fallar con `Resource.Error`).
*   **Insumos:** Intentar vincular una cantidad nula o negativa de insumos a una campaÃƒÂƒÃ‚Â±a (Lanza `IllegalArgumentException`).
*   **Tareas:** Programar una tarea en el pasado con el switch de notificar en `true`. El `WorkManagerTaskReminderScheduler` no deberÃƒÂƒÃ‚Â­a encolar notificaciones retroactivas (debe validar que el delay calculado sea > 0).
*   **Observaciones:** Intentar guardar una observaciÃƒÂƒÃ‚Â³n con el campo de texto vacÃƒÂƒÃ‚Â­o (Lanza `IllegalArgumentException`).
*   **Cosechas (Formulario):** Guardar sin campaÃƒÂƒÃ‚Â±a seleccionada (Issue 7) ÃƒÂ¢Ã‚Â€Ã‚Â” Debe emitir `errorCampania` y NO crashear por FK constraint; guardar con `cantidad` o `unidad` vacÃƒÂƒÃ‚Â­as (Issue 12) ÃƒÂ¢Ã‚Â€Ã‚Â” Debe emitir el error visual correspondiente y deshabilitar el botÃƒÂƒÃ‚Â³n "Guardar".
*   **AutenticaciÃƒÂƒÃ‚Â³n:** Iniciar sesiÃƒÂƒÃ‚Â³n con un usuario inexistente o con credenciales vacÃƒÂƒÃ‚Â­as (El ViewModel debe capturar la excepciÃƒÂƒÃ‚Â³n o el `null` y emitir el estado de `error` correspondiente).

---

## 6. Cobertura y EjecuciÃƒÂƒÃ‚Â³n de Tests

Para garantizar que nuestros tests efectivamente cubren la lÃƒÂƒÃ‚Â³gica de negocio, implementaremos las siguientes estrategias:

### A. EjecuciÃƒÂƒÃ‚Â³n de Pruebas (Comandos)
1.  **Pruebas Unitarias (JVM Locales):**
    *   Comando: `./gradlew testDebugUnitTest`
    *   *PropÃƒÂƒÃ‚Â³sito:* Ejecutar todas las pruebas de Use Cases y ViewModels de manera ultra rÃƒÂƒÃ‚Â¡pida sin necesidad de un emulador.
2.  **Pruebas Instrumentadas (Base de Datos):**
    *   Comando: `./gradlew connectedDebugAndroidTest`
    *   *PropÃƒÂƒÃ‚Â³sito:* Ejecutar las pruebas sobre los DAOs. Requiere que un dispositivo fÃƒÂƒÃ‚Â­sico o emulador estÃƒÂƒÃ‚Â© encendido y conectado.

### B. MediciÃƒÂƒÃ‚Â³n de Cobertura (Code Coverage)
Utilizaremos **KoverX** (o JaCoCo configurado para Kotlin) para generar reportes HTML visuales sobre quÃƒÂƒÃ‚Â© porcentaje de nuestro cÃƒÂƒÃ‚Â³digo estÃƒÂƒÃ‚Â¡ siendo probado.
*   **Comando de Cobertura (Android):** `./gradlew koverHtmlReportDebug` (Es fundamental usar la variante `Debug` para que Kover analice correctamente las clases instrumentadas de Android).
*   **Meta de Cobertura:**
    *   `domain` (Reglas de negocio y Use Cases): **MÃƒÂƒÃ‚Â­nimo 80%**. Esta capa es crÃƒÂƒÃ‚Â­tica.
    *   `data` (DAOs y Repositorios): **MÃƒÂƒÃ‚Â­nimo 70%**.
    *   `presentation` (UI): No requerirÃƒÂƒÃ‚Â¡ cobertura estricta en la fase inicial para priorizar velocidad.

### C. AutomatizaciÃƒÂƒÃ‚Â³n Continua (CI/CD) con GitHub Actions
Para asegurar que no se introduzcan regresiones al proyecto, hemos configurado un flujo de trabajo (Workflow) en GitHub Actions (`.github/workflows/pr_tests.yml`). 

**ÃƒÂ‚Ã‚Â¿QuÃƒÂƒÃ‚Â© hace automÃƒÂƒÃ‚Â¡ticamente?**
Cada vez que un desarrollador hace un *Push* o crea un *Pull Request* hacia las ramas `main` o `develop`:
1. El servidor de GitHub arranca un entorno virtual Linux con Java 17.
2. Ejecuta `./gradlew testDebugUnitTest` para validar todas nuestras pruebas de Use Cases y ViewModels.
3. Genera y sube el reporte de cobertura HTML (`koverHtmlReportDebug`) como un artefacto descargable.

**Nota sobre Tests Instrumentados:**
Los tests que requieren emulador (`connectedDebugAndroidTest`) no estÃƒÂƒÃ‚Â¡n incluidos de momento en el flujo bÃƒÂƒÃ‚Â¡sico para evitar tiempos muertos en la validaciÃƒÂƒÃ‚Â³n rÃƒÂƒÃ‚Â¡pida del PR, pero deben ejecutarse localmente antes de solicitar el PR.

---
*(Este documento se mantendrÃƒÂƒÃ‚Â¡ sincronizado con el cÃƒÂƒÃ‚Â³digo. Cualquier bug detectado en producciÃƒÂƒÃ‚Â³n en el futuro se traducirÃƒÂƒÃ‚Â¡ en un nuevo escenario "Given-When-Then" aquÃƒÂƒÃ‚Â­ antes de escribir el parche).*

---

## MÃƒÂƒÃ‚Â³dulo de Reportes

#### ReportesViewModel ÃƒÂ¢Ã‚Â€Ã‚Â” StateFlows contextuales [#299]

**Test VM-R1: campanias emite lista vacÃƒÂƒÃ‚Â­a cuando la BD estÃƒÂƒÃ‚Â¡ vacÃƒÂƒÃ‚Â­a**
*   **Given:** El `ReportesViewModel` inicia con BD sin campaÃƒÂƒÃ‚Â±as.
*   **When:** Se observa el StateFlow `campanias`.
*   **Then:** Debe emitir una lista vacÃƒÂƒÃ‚Â­a.

**Test VM-R2: campanias emite la lista real cuando la BD tiene registros**
*   **Given:** La BD tiene 2 campaÃƒÂƒÃ‚Â±as registradas.
*   **When:** Se observa el StateFlow `campanias`.
*   **Then:** Debe emitir exactamente esas 2 campaÃƒÂƒÃ‚Â±as.

**Test VM-R3: seleccionarCampaniaIndividual actualiza campaniaIndividual**
*   **Given:** El ViewModel estÃƒÂƒÃ‚Â¡ inicializado sin selecciÃƒÂƒÃ‚Â³n (campaniaIndividual = null).
*   **When:** Se llama a `seleccionarCampaniaIndividual(campania)`.
*   **Then:** `campaniaIndividual` debe emitir la campaÃƒÂƒÃ‚Â±a elegida.

**Test VM-R4: insumosIndividual emite lista vacÃƒÂƒÃ‚Â­a cuando no hay campaÃƒÂƒÃ‚Â±a seleccionada**
*   **Given:** No hay campaÃƒÂƒÃ‚Â±a seleccionada.
*   **When:** Se observa `insumosIndividual`.
*   **Then:** Debe emitir lista vacÃƒÂƒÃ‚Â­a sin consultar la BD.

**Test VM-R5: pieChartData emite null cuando no hay campaÃƒÂƒÃ‚Â±a seleccionada**
*   **Given:** No hay campaÃƒÂƒÃ‚Â±a seleccionada (insumosIndividual vacÃƒÂƒÃ‚Â­o).
*   **When:** Se observa `pieChartData`.
*   **Then:** Debe emitir `null` (el grÃƒÂƒÃ‚Â¡fico no debe mostrarse).

#### ReportesViewModel ÃƒÂ¢Ã‚Â€Ã‚Â” desglose cosechas por destino [#301]

**Test VM-R6: desgloseCosechasData agrupa por almacÃƒÂƒÃ‚Â©n y venta correctamente**
*   **Given:** Una campaÃƒÂƒÃ‚Â±a con cosechas mixtas (algunas con `almacen` no vacÃƒÂƒÃ‚Â­o, otras con `almacen` en blanco).
*   **When:** Se selecciona esa campaÃƒÂƒÃ‚Â±a con `seleccionarCampaniaIndividual()`.
*   **Then:** `desgloseCosechasData` debe emitir un `PieChartData` con 2 slices:
    - Slice "Almacenada": suma de cantidades con `almacen.isNotBlank()`.
    - Slice "Vendida": suma de cantidades con `almacen.isBlank()`.

**Test VM-R7: desgloseCosechasData emite null cuando no hay cosechas**
*   **Given:** Una campaÃƒÂƒÃ‚Â±a seleccionada pero sin cosechas en la BD.
*   **When:** Se observa `desgloseCosechasData`.
*   **Then:** Debe emitir `null` (sin grÃƒÂƒÃ‚Â¡fico).

#### ReportesViewModel ÃƒÂ¢Ã‚Â€Ã‚Â” guardia de exportaciÃƒÂƒÃ‚Â³n [#300]

**Test VM-R8: exportarReporteCsv emite error si no hay campaÃƒÂƒÃ‚Â±a seleccionada**
*   **Given:** No hay campaÃƒÂƒÃ‚Â±a seleccionada (`campaniaIndividual = null`).
*   **When:** Se llama a `exportarReporteCsv(uri, context)`.
*   **Then:** `exportStatus` debe emitir `"Seleccione una campaÃƒÂƒÃ‚Â±a para exportar"` y no debe invocarse `ReportExporter`.

**Test VM-R9: exportarReportePdf emite error si no hay campaÃƒÂƒÃ‚Â±a seleccionada**
*   **Given:** No hay campaÃƒÂƒÃ‚Â±a seleccionada.
*   **When:** Se llama a `exportarReportePdf(uri, context)`.
*   **Then:** `exportStatus` debe emitir `"Seleccione una campaÃƒÂƒÃ‚Â±a para exportar"`.

#### ReportesViewModel ÃƒÂ¢Ã‚Â€Ã‚Â” comparaciÃƒÂƒÃ‚Â³n real entre campaÃƒÂƒÃ‚Â±as [#302]

**Test VM-R10: cosechasA emite la lista de cosechas de la campaÃƒÂƒÃ‚Â±a A seleccionada**
*   **Given:** La BD tiene cosechas asociadas a la campaÃƒÂƒÃ‚Â±a con `id = 1`.
*   **When:** Se llama a `seleccionarCampaniaA(campaniaSoja)` donde `campaniaSoja.id = 1`.
*   **Then:** `cosechasA` debe emitir la lista real de cosechas de esa campaÃƒÂƒÃ‚Â±a.

**Test VM-R11: cosechasA emite lista vacÃƒÂƒÃ‚Â­a cuando no hay campaÃƒÂƒÃ‚Â±a A seleccionada**
*   **Given:** No hay campaÃƒÂƒÃ‚Â±a seleccionada en el comparador (campaniaA = null).
*   **When:** Se observa `cosechasA`.
*   **Then:** Debe emitir una lista vacÃƒÂƒÃ‚Â­a.


### ReportesViewModel
- **VM-R12:** Given misma campaÃƒÂ±a en A y B / When comparar / Then se emite estado de advertencia (UI lo maneja con condicional de igualdad de IDs).

## ValidarDatosCosechaUseCase
- **Dado** cantidad = null -> **Cuando** invoke() -> **Entonces** retorna Error("La cantidad debe ser mayor a 0.")
- **Dado** fecha = null -> **Cuando** invoke() -> **Entonces** retorna Error("La fecha es obligatoria.")
- **Dado** isAlmacenada=true y almacen en blanco -> **Cuando** invoke() -> **Entonces** retorna Error("El nombre del almacen o silo es obligatorio.")
- **Dado** todos los campos son vÃƒÂ¡lidos -> **Cuando** invoke() -> **Entonces** retorna Success

## FormularioInsumoViewModel Ã‚Â— ValidaciÃƒÂ³n al guardar
- **Dado** nombre vacÃƒÂ­o y se llama guardar() -> **Cuando** validarInsumoUseCase devuelve error -> **Entonces** state.errorNombre != null y NO se llama al UseCase de inserciÃƒÂ³n
- **Dado** nombre vÃƒÂ¡lido, categoria vÃƒÂ¡lida -> **Cuando** guardar() -> **Entonces** se invoca el UseCase de inserciÃƒÂ³n
- **Dado** el usuario escribe en el campo nombre -> **Cuando** onNombreChange() -> **Entonces** errorNombre se limpia (sin validar aÃƒÂºn)


## FormularioCosechaViewModel - EdiciÃƒÂ³n y validaciÃƒÂ³n por campo (#335 / #336)

**Test VM-C6: Init con cosechaId vÃƒÂ¡lido carga la cosecha en el estado**
*   **Given:** SavedStateHandle contiene cosechaId = 7 y obtenerCosechaPorIdUseCase(7) retorna una cosecha con cantidad 55.0 y almacÃƒÂ©n "Silo A".
*   **When:** Se inicializa el ViewModel.
*   **Then:** state.cosechaId == 7, state.cantidad == "55.0", state.almacen == "Silo A", state.almacenado == true.

**Test VM-C7: Error de cantidad va a errorCantidad, no a errorFecha**
*   **Given:** CampaÃƒÂ±a seleccionada. ValidarDatosCosechaUseCase retorna Error("La cantidad debe ser mayor a 0.").
*   **When:** Se llama a guardar().
*   **Then:** errorCantidad != null, errorFecha == null.

**Test VM-C8: Error de fecha va a errorFecha, no a errorCantidad**
*   **Given:** CampaÃƒÂ±a y cantidad vÃƒÂ¡lidas. ValidarDatosCosechaUseCase retorna Error("La fecha es obligatoria.").
*   **When:** Se llama a guardar().
*   **Then:** errorFecha != null, errorCantidad == null.

**Test VM-C9: onFechaChange limpia errorFecha**
*   **Given:** Existe errorFecha en el state (provocado por un guardar fallido).
*   **When:** Se llama a onFechaChange(timestamp).
*   **Then:** errorFecha == null.
## FormularioInsumoViewModel - Habilitacion de boton guardar en tiempo real (#403)

**VM-I-1: Estado inicial en modo Alta tiene isGuardarHabilitado = false**
* **Dado** el ViewModel se inicializa sin insumoId (modo Alta).
* **Cuando** se observa el state.isGuardarHabilitado.
* **Entonces** debe ser false.

**VM-I-2: Tipear solo nombre no habilita el boton guardar**
* **Dado** el formulario esta en modo Alta.
* **Cuando** se llama a onNombreChange("Herbicida") y categoria esta vacia.
* **Entonces** isGuardarHabilitado debe seguir siendo false.

**VM-I-3: Tipear solo categoria no habilita el boton guardar**
* **Dado** el formulario esta en modo Alta.
* **Cuando** se llama a onCategoriaChange("Pesticidas") y nombre esta vacio.
* **Entonces** isGuardarHabilitado debe seguir siendo false.

**VM-I-4: Tipear nombre y categoria validos habilita el boton guardar**
* **Dado** el formulario esta en modo Alta.
* **Cuando** se llama a onNombreChange("Herbicida Total") y luego onCategoriaChange("Pesticidas").
* **Entonces** isGuardarHabilitado = true, errorNombre = null, errorCategoria = null.

**VM-I-5: Borrar nombre deshabilita el boton guardar**
* **Dado** el formulario tiene nombre y categoria validos (isGuardarHabilitado = true).
* **Cuando** se llama a onNombreChange("") vaciando el nombre.
* **Entonces** isGuardarHabilitado vuelve a false.

## NuevaTareaViewModel - Modo Edicion y preservacion de confirmar (#410)

**VM-T-E1: Modo edicion precarga datos de la tarea existente incluyendo confirmar**
* **Dado** existe una Tarea con id=5, nombre="Tarea Completada", confirmar=true en la BD.
* **Cuando** el ViewModel inicia con tareaId=5 en el SavedStateHandle.
* **Entonces** state.nombre = "Tarea Completada", state.confirmar = true, state.hora = "09:00".

**VM-T-E2: Editar tarea completada preserva confirmar=true al guardar**
* **Dado** una Tarea con confirmar=true esta cargada en modo edicion.
* **Cuando** se modifica el nombre y se llama a guardar().
* **Entonces** editarTareaUseCase recibe una Tarea con confirmar=true (no reseteado a false).




## CosechaViewModel - Fix Race Condition (#441)

**VM-C-S1: campaniaId explÃƒÂ­cito en SavedState no se sobreescribe por el manager**
* **Dado** el ViewModel se crea con campaniaId = 5 en SavedStateHandle.
* **Cuando** el UltimaSeleccionManager emite id = 3.
* **Entonces** campaniaIdSeleccionada permanece en 5.

**VM-C-S2: sin campaniaId en SavedState el manager actÃƒÂºa como fallback**
* **Dado** el ViewModel se crea sin campaniaId en SavedStateHandle.
* **Cuando** el UltimaSeleccionManager emite id = 7.
* **Entonces** campaniaIdSeleccionada se actualiza a 7.

**VM-C-S3: sincronizarCampania actualiza el id cuando difiere del actual**
* **Dado** el ViewModel inicia sin campaniaId.
* **Cuando** se llama a sincronizarCampania(2).
* **Entonces** campaniaIdSeleccionada emite 2.

**VM-C-S4: sincronizarCampania no emite si el id es igual al actual (idempotente)**
* **Dado** el ViewModel tiene campaniaId = 4 en SavedState.
* **Cuando** se llama a sincronizarCampania(4).
* **Entonces** NO se emite un nuevo evento (expectNoEvents).

**VM-C-S5: isCampaniaValid emite false cuando campaniaId es nulo**
* **Dado** el ViewModel inicia sin campaniaId vÃƒÂ¡lido.
* **Cuando** se observa isCampaniaValid.
* **Entonces** isCampaniaValid = false.

**VM-C-S6: isCampaniaValid emite true tras sincronizarCampania con id vÃƒÂ¡lido**
* **Dado** el ViewModel inicia sin campaniaId.
* **Cuando** se llama a sincronizarCampania(1).
* **Entonces** isCampaniaValid = true.

## TareaViewModel - Fix Race Condition (#441)

**VM-T-S1: campaniaId explÃƒÂ­cito en SavedState no se sobreescribe por el manager**
* **Dado** el ViewModel se crea con campaniaId = 5 en SavedStateHandle.
* **Cuando** el UltimaSeleccionManager emite id = 3.
* **Entonces** filtroCampania permanece en 5.

**VM-T-S2: sin campaniaId en SavedState el manager actÃƒÂºa como fallback**
* **Dado** el ViewModel se crea sin campaniaId en SavedStateHandle.
* **Cuando** el UltimaSeleccionManager emite id = 7.
* **Entonces** filtroCampania se actualiza a 7.

## FormatearMoneda - Fix balance Dashboard (#437)

**FM-1: Formatea valor positivo en millones**
* **Dado** un valor de 6.133.500.
* **Cuando** se llama a formatearMoneda(6133500.0).
* **Entonces** devuelve "`$`6,1M".

**FM-2: Formatea valor positivo en miles**
* **Dado** un valor de 250.000.
* **Cuando** se llama a formatearMoneda(250000.0).
* **Entonces** devuelve "`$`250K".

**FM-3: Formatea balance negativo en millones con signo al frente**
* **Dado** un balance negativo de -6.133.500 (caso del bug reportado).
* **Cuando** se llama a formatearMoneda(-6133500.0).
* **Entonces** devuelve "-`$`6,1M" con el signo al frente (no al final como harÃƒÂ­a NumberFormat de locale es_AR).

**FM-4: Formatea balance negativo en miles**
* **Dado** un balance de -250.000.
* **Cuando** se llama a formatearMoneda(-250000.0).
* **Entonces** devuelve "-`$`250K".

**FM-5: Formatea cero**
* **Dado** valor = 0.0.
* **Cuando** se llama a formatearMoneda(0.0).
* **Entonces** devuelve "`$` ".

**FM-6: Formatea exactamente 1.000.000**
* **Dado** valor = 1.000.000.
* **Cuando** se llama a formatearMoneda(1000000.0).
* **Entonces** devuelve "`$`1,0M".
* **Entonces** filtroCampania se actualiza a 7.


## Pruebas de Estabilizacion - Iteracion 5 (Issues #434, #438, #440)

### Reportes UI
- **UI-R1 (Issue #438):** Given una unica campania finalizada / When se visualiza la evolucion historica / Then el unico punto se centra horizontal y verticalmente en el Canvas.
- **UI-R2 (Issue #434):** Given un nombre de campania muy largo / When se renderiza el eje X del grafico / Then el texto se trunca a 12 caracteres con '...'.

### Insumos UI
- **UI-I1 (Issue #440):** Given el formulario de Nuevo Insumo / When se tipean caracteres Unicode/Emojis complejos / Then se insertan y guardan correctamente en la BD local.

