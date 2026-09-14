# Plan EstratÃ©gico y Casos de Prueba (Living Documentation)

Este documento centraliza la estrategia de testing del proyecto "Don Elio" y actÃºa como fuente de la verdad para escribir las pruebas automatizadas (Test Cases). Es un **Living Document** (Documento Vivo), lo que significa que **deberemos mantenerlo actualizado obligatoriamente** cada vez que modifiquemos el cÃ³digo o agreguemos nuevas funcionalidades, asegurando que las pruebas y la documentaciÃ³n no se desfasen.

## 1. Stack TecnolÃ³gico de Testing
*   **Unit Testing (Casos de Uso, ViewModels, Mappers):** `JUnit 4`, `MockK` (Mocks nativos Kotlin) y `Turbine` (Pruebas de flujos/Flows). 
    *   *Importante:* Para validar excepciones dentro de corrutinas (`runTest`), no se debe usar `assertThrows` de JUnit (ya que pierde el contexto suspendido), sino bloques nativos `try-catch` o `runCatching`.
*   **Pruebas de IntegraciÃ³n/Base de Datos (DAOs):** `AndroidX Test`, `Room Testing` (con `inMemoryDatabaseBuilder`) ejecutado en Emulador (Pruebas Instrumentadas).
*   **Pruebas de Interfaz de Usuario (UI):** `Compose Test Rule` nativo.

---

## 2. AnÃ¡lisis de Discrepancias (Documento 2025 vs Realidad 2026)

Al contrastar la propuesta del aÃ±o 2025 con la arquitectura real implementada en la App, detectamos e implementamos mejoras significativas que impactan la forma en que escribiremos los tests:

1.  **Redundancia de EdiciÃ³n (CampaÃ±as - CU2 y CU4):**
    *   *En 2025:* Se separaba "Entrar al menÃº" (CU2) de "Editar los campos" (CU4).
    *   *Realidad:* La arquitectura moderna expone un solo `EditarCampaniaUseCase`. AdemÃ¡s, se agregÃ³ el campo **`estaActiva`** a la entidad `Campania` para controlar estados (por ejemplo, si estÃ¡ terminada o en curso). Testearemos directamente la actualizaciÃ³n de este estado en BD.
2.  **Arquitectura de Notificaciones (Tareas - CU5):**
    *   *En 2025:* DependÃ­a de un "Actor Externo".
    *   *Realidad:* Reemplazado internamente por `WorkManagerTaskReminderScheduler`. Los tests de tareas deberÃ¡n validar (vÃ­a `MockK`) que el scheduler se mande a llamar o se cancele (ej. al completar o borrar una tarea).
3.  **UnificaciÃ³n de MÃ³dulo de Cosechas (CU6 y CU7):**
    *   *En 2025:* "Cosecha" (CU6) y "Datos no almacenados" (CU7) corrÃ­an por caminos distintos.
    *   *Realidad:* Bifurcamos la lÃ³gica limpiamente en `RegistrarCosechaUseCase` (para silos) y `RegistrarCosechaConVentaUseCase` (Venta o Reserva como alimento). Los tests cubrirÃ¡n ambas variantes de inserciÃ³n.
4.  **Refactor Total del MÃ³dulo de Insumos (CU9):**
    *   *En 2025:* Los insumos se creaban directamente vinculados a una campaÃ±a.
    *   *Realidad:* **Un cambio vital.** Ahora existe un CatÃ¡logo Global (`CrearInsumoCatalogoUseCase`) y posteriormente una vinculaciÃ³n a la campaÃ±a (`AsignarInsumoACampaniaUseCase`). AdemÃ¡s, el catÃ¡logo tiene la columna **`activo`**. Si el usuario elimina un insumo del catÃ¡logo (`EliminarInsumoCatalogoUseCase`), el test deberÃ¡ corroborar que **NO se hace un `DELETE` en la DB**, sino un `UPDATE activo = false` (Soft-Delete) para no corromper los histÃ³ricos de campaÃ±as pasadas.
5.  **MÃ³dulos Nuevos (No previstos en 2025):**
    *   *AutenticaciÃ³n:* `LoginUseCase` (SHA-256) y `RegistroUseCase`.
    *   *Backups:* `CrearBackupUseCase` y `RestaurarBackupUseCase` usando SAF de Android.

---

## 3. Pruebas Fuera de los Casos de Uso (Out of Scope Tests)

No toda la app es Casos de Uso. Existen componentes de bajo nivel y de infraestructura que testearemos independientemente:
*   **DAOs (Data Access Objects):** 
    Pruebas instrumentadas sobre `UsuarioDao`, `CampaniaDao`, `CampaniaInsumoDao` (validando foreign keys, borrados en cascada fÃ­sicos, y los queries filtrados por `activo = 1`).
*   **Mappers (Data <-> Domain):** 
    Pruebas unitarias para validar que al pasar de Entity a Domain Model no se pierda informaciÃ³n y viceversa.
*   **ViewModels (Presentation):** 
    Validar la emisiÃ³n correcta de los estados (`Loading`, `Success`, `Error`) hacia Jetpack Compose usando `Turbine` (ej: `LoginViewModelTest` verifica la transiciÃ³n a `isLoading = true` y luego `loginExitoso = true` o la asignaciÃ³n de mensajes de error).

---

## 4. Escenarios de Pruebas (Behavior-Driven Development - BDD)

A continuaciÃ³n, estructuramos los tests en formato `Given-When-Then` por mÃ³dulo, respetando el orden lÃ³gico de los Casos de Uso.

### MÃ³dulo de CampaÃ±as (CU1 - CU4)

**Test 1: Crear CampaÃ±a Exitosa**
*   **Given:** Un nombre vÃ¡lido "Trigo de Invierno", cultivo "Trigo" y una fecha correcta.
*   **When:** Invoco `CrearCampaniaUseCase`.
*   **Then:** El sistema debe insertar el registro en el repositorio y emitir el estado `Resource.Success`.

**Test 2: Crear CampaÃ±a con Errores**
*   **Given:** Un nombre vacÃ­o "".
*   **When:** Invoco `CrearCampaniaUseCase`.
*   **Then:** El sistema debe emitir `Resource.Error` con mensaje "El nombre no puede estar vacÃ­o" y NO llamar al repositorio.

**Test UC-V1: ValidarDatosCampaniaUseCase â Nombre vacÃ­o**
*   **Given:** nombre = "", cultivo = "Soja", fechaInicio = <fecha futura>, isEditMode = false
*   **When:** invoke(nombre, cultivo, fechaInicio, isEditMode)
*   **Then:** esValido = false, errorNombre = "El nombre es obligatorio"

**Test UC-V2: ValidarDatosCampaniaUseCase â Fecha pasada en creaciÃ³n**
*   **Given:** nombre = "CampaÃ±a", cultivo = "MaÃ­z", fechaInicio = <ayer en millis>, isEditMode = false
*   **When:** invoke(...)
*   **Then:** esValido = false, errorFecha = "La fecha no puede ser anterior a hoy"

**Test UC-V3: ValidarDatosCampaniaUseCase â Fecha pasada permitida en ediciÃ³n**
*   **Given:** nombre = "CampaÃ±a", cultivo = "MaÃ­z", fechaInicio = <ayer en millis>, isEditMode = true
*   **When:** invoke(...)
*   **Then:** esValido = true, errorFecha = null

**Test UC-V4: ValidarDatosCampaniaUseCase â Todos los campos vÃ¡lidos**
*   **Given:** Todos los campos correctos, isEditMode = false
*   **When:** invoke(...)
*   **Then:** esValido = true, todos los errores = null

### MÃ³dulo de Insumos (CU9 - CU9.4)

**Test 3: EliminaciÃ³n LÃ³gica (Soft-Delete) de Insumo del CatÃ¡logo**
*   **Given:** Que el insumo "Glifosato" existe en el catÃ¡logo con `activo = true` y ya fue utilizado en 2 campaÃ±as.
*   **When:** Invoco `EliminarInsumoCatalogoUseCase` pasando ese insumo.
*   **Then:** El repositorio debe realizar un `UPDATE` (cambiando `activo` a `false`) y NO un `DELETE` fÃ­sico. Las llamadas a `ObtenerCatalogoInsumosUseCase` ya no deben retornarlo.

**Test 4: AsignaciÃ³n de Insumo a CampaÃ±a**
*   **Given:** El "Glifosato" (activo en el catÃ¡logo) y la campaÃ±a "Trigo de Invierno".
*   **When:** Invoco `AsignarInsumoACampaniaUseCase` pasando `cantidad = 5` y `precio = 100`.
*   **Then:** Se crea un registro en `CampaniaInsumoEntity` relacionando los IDs y estableciendo el coste.

**Test UC-V5: ValidarInsumoUseCase â CategorÃ­a vacÃ­a**
*   **Given:** nombre = "Herbicida", categoria = ""
*   **When:** invoke(nombre, categoria)
*   **Then:** esValido = false, errorCategoria = "La categorÃ­a es obligatoria"

**Test UC-V6: ValidarInsumoUseCase â Ambos campos vÃ¡lidos**
*   **Given:** nombre = "Herbicida", categoria = "QuÃ­mico"
*   **When:** invoke(nombre, categoria)
*   **Then:** esValido = true, errorNombre = null, errorCategoria = null

### MÃ³dulo de Tareas (CU5 - CU5.4)

#### TareaViewModel â sincronizarCampania() [#292]

**Test VM-T1: sincronizarCampania actualiza el id cuando difiere del actual**
*   **Given:** El `TareaViewModel` inicia sin `campaniaId` en el `SavedStateHandle` (estado inicial `null`).
*   **When:** Se llama a `sincronizarCampania(5)`.
*   **Then:** El StateFlow `campaniaIdSeleccionada` debe emitir el valor `5`.

**Test VM-T2: sincronizarCampania no emite si el id es igual al actual**
*   **Given:** El `TareaViewModel` ya tiene `campaniaIdSeleccionada = 5`.
*   **When:** Se llama a `sincronizarCampania(5)` con el mismo valor.
*   **Then:** El StateFlow **no** debe emitir un nuevo evento (idempotencia garantizada).

**Test VM-T3: tareas emite lista vacÃ­a si no hay campaniaId vÃ¡lido**
*   **Given:** El `TareaViewModel` inicia sin `campaniaId` vÃ¡lido.
*   **When:** Se observa el StateFlow `tareas`.
*   **Then:** Debe emitir inmediatamente una lista vacÃ­a, sin llamar al repositorio.

**Test VM-T4: isCampaniaValid emite false cuando campaniaId es nulo**
*   **Given:** `campaniaIdSeleccionada` es `null`.
*   **When:** Se observa `isCampaniaValid`.
*   **Then:** Debe emitir `false`.

**Test VM-T5: isCampaniaValid emite true tras sincronizarCampania con id vÃ¡lido**
*   **Given:** El ViewModel inicia con `campaniaId = null`.
*   **When:** Se llama a `sincronizarCampania(3)`.
*   **Then:** `isCampaniaValid` debe emitir `true`.


**Test 5: Agendar tarea con recordatorio activado**
*   **Given:** Una nueva tarea "Revisar fertilizante" con el switch `notificar = true`.
*   **When:** Invoco `CrearTareaUseCase`.
*   **Then:** El sistema guarda la tarea en BD y, posteriormente, invoca `taskReminderScheduler.schedule(tarea)`.

**Test 6: Completar tarea programada (CancelaciÃ³n de Alerta)**
*   **Given:** La tarea anterior, que actualmente tiene notificaciones encoladas.
*   **When:** Invoco `ConfirmarTareaUseCase` seteando la tarea como `completada = true`.
*   **Then:** El estado de la tarea cambia en BD, y obligatoriamente se invoca `taskReminderScheduler.cancel(tarea.id)` para evitar alertas fantasma.

### MÃ³dulo de Cosechas (CU6 - CU7)

**Test 7: Registrar Cosecha No Almacenada (Venta/Reserva)**
*   **Given:** Una cosecha de "Soja" que no va al silo, sino que se vende (`venta = true`) a $100.
*   **When:** Invoco `RegistrarCosechaConVentaUseCase`.
*   **Then:** El sistema inserta el registro base en la tabla Cosechas, toma el ID generado, e inserta un segundo registro en `CosechaNoAlmacenadaEntity` vinculando la venta y el precio.

**Test 8: Listar Cosechas de una CampaÃ±a**
*   **Given:** Una campaÃ±a con cosechas mixtas (en silo y vendidas).
*   **When:** Invoco `ObtenerCosechasPorCampaniaUseCase` y `ObtenerCosechasNoAlmacenadasUseCase`.
*   **Then:** El repositorio debe devolver dos flujos distintos. El ViewModel debe ser capaz de fusionarlos para mostrar quÃ© fracciÃ³n de la cosecha total fue vendida.

**Test 8.1: Formulario de Cosecha - Sin CampaÃ±a Seleccionada (Issue 7)**
*   **Given:** Un `FormularioCosechaViewModel` creado sin `campaniaId` en el `SavedStateHandle` (acceso vÃ­a navegaciÃ³n global).
*   **When:** El usuario ingresa una cantidad vÃ¡lida y presiona "Guardar Registro".
*   **Then:** Se setea `errorCampania = "Debe seleccionar una campaÃ±a"` y no se llama a ningÃºn use case de registro.

**Test 8.2: Formulario de Cosecha - Cantidad Obligatoria (Issue 12)**
*   **Given:** Una campaÃ±a seleccionada y el campo `cantidad` vacÃ­o.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se setea `errorCantidad = "La cantidad es obligatoria"` y no se llama a ningÃºn use case de registro.

**Test 8.3: Formulario de Cosecha - Precio InvÃ¡lido**
*   **Given:** Una campaÃ±a y una `cantidad` vÃ¡lidas, con `almacenado = false`, `tipo = "Venta"` y un precio no numÃ©rico (ej. "abc").
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se setea `errorPrecio = "Precio invÃ¡lido"` y no se llama a ningÃºn use case de registro.

**Test 8.4: Formulario de Cosecha - Registro Exitoso (Almacenado)**
*   **Given:** Una campaÃ±a, `cantidad = 100`, y `almacen = "Silo 1"` vÃ¡lidos.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se llama a `RegistrarCosechaUseCase` con los parÃ¡metros correctos y se emite `guardadoExitoso = true`.

**Test 8.5: Formulario de Cosecha - Registro Exitoso (Venta)**
*   **Given:** Una campaÃ±a, `cantidad = 100`, `almacenado = false`, `tipo = "Venta"` y `precio = 500` vÃ¡lidos.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se llama a `RegistrarCosechaConVentaUseCase` con los parÃ¡metros correctos y se emite `guardadoExitoso = true`.

### MÃ³dulo de Observaciones (CU8)

**Test 9: Guardar ObservaciÃ³n con Imagen Adjunta**
*   **Given:** Una nota de texto y una URI local que apunta a una foto en el dispositivo.
*   **When:** Invoco `GuardarObservacionUseCase`.
*   **Then:** El sistema guarda correctamente el string de la URI en la entidad para que luego Coil pueda renderizarla en la UI.

### MÃ³dulo de AutenticaciÃ³n (Extra 1)

**Test 10: Login Exitoso con Hash SHA-256**
*   **Given:** Un usuario "DonElio" registrado en la base de datos con contraseÃ±a hasheada.
*   **When:** El usuario ingresa la contraseÃ±a en texto plano y se invoca `LoginUseCase`.
*   **Then:** El Use Case encripta el texto plano ingresado, lo compara con la BD, coincide, y emite `Resource.Success`.

**Test 11: Login Fallido (Usuario no existe)**
*   **Given:** Un intento de acceso con el nombre "Intruso".
*   **When:** Invoco `LoginUseCase`.
*   **Then:** Retorna `Resource.Error("Usuario no encontrado")`.

### MÃ³dulo de Backups (Extra 2)

**Test 12: GeneraciÃ³n de Backup Exitoso**
*   **Given:** Una ruta URI proporcionada por el SAF (Storage Access Framework) donde el usuario tiene permisos de escritura.
*   **When:** Invoco `CrearBackupUseCase`.
*   **Then:** El archivo `.db` se copia exitosamente al destino y emite `Resource.Success`.

---

## 5. Casos de Borde (Edge Cases) a Testear
*   **CampaÃ±as:** Intentar crear una campaÃ±a con nombre vacÃ­o (DeberÃ­a fallar con `Resource.Error`).
*   **Insumos:** Intentar vincular una cantidad nula o negativa de insumos a una campaÃ±a (Lanza `IllegalArgumentException`).
*   **Tareas:** Programar una tarea en el pasado con el switch de notificar en `true`. El `WorkManagerTaskReminderScheduler` no deberÃ­a encolar notificaciones retroactivas (debe validar que el delay calculado sea > 0).
*   **Observaciones:** Intentar guardar una observaciÃ³n con el campo de texto vacÃ­o (Lanza `IllegalArgumentException`).
*   **Cosechas (Formulario):** Guardar sin campaÃ±a seleccionada (Issue 7) â Debe emitir `errorCampania` y NO crashear por FK constraint; guardar con `cantidad` o `unidad` vacÃ­as (Issue 12) â Debe emitir el error visual correspondiente y deshabilitar el botÃ³n "Guardar".
*   **AutenticaciÃ³n:** Iniciar sesiÃ³n con un usuario inexistente o con credenciales vacÃ­as (El ViewModel debe capturar la excepciÃ³n o el `null` y emitir el estado de `error` correspondiente).

---

## 6. Cobertura y EjecuciÃ³n de Tests

Para garantizar que nuestros tests efectivamente cubren la lÃ³gica de negocio, implementaremos las siguientes estrategias:

### A. EjecuciÃ³n de Pruebas (Comandos)
1.  **Pruebas Unitarias (JVM Locales):**
    *   Comando: `./gradlew testDebugUnitTest`
    *   *PropÃ³sito:* Ejecutar todas las pruebas de Use Cases y ViewModels de manera ultra rÃ¡pida sin necesidad de un emulador.
2.  **Pruebas Instrumentadas (Base de Datos):**
    *   Comando: `./gradlew connectedDebugAndroidTest`
    *   *PropÃ³sito:* Ejecutar las pruebas sobre los DAOs. Requiere que un dispositivo fÃ­sico o emulador estÃ© encendido y conectado.

### B. MediciÃ³n de Cobertura (Code Coverage)
Utilizaremos **KoverX** (o JaCoCo configurado para Kotlin) para generar reportes HTML visuales sobre quÃ© porcentaje de nuestro cÃ³digo estÃ¡ siendo probado.
*   **Comando de Cobertura (Android):** `./gradlew koverHtmlReportDebug` (Es fundamental usar la variante `Debug` para que Kover analice correctamente las clases instrumentadas de Android).
*   **Meta de Cobertura:**
    *   `domain` (Reglas de negocio y Use Cases): **MÃ­nimo 80%**. Esta capa es crÃ­tica.
    *   `data` (DAOs y Repositorios): **MÃ­nimo 70%**.
    *   `presentation` (UI): No requerirÃ¡ cobertura estricta en la fase inicial para priorizar velocidad.

### C. AutomatizaciÃ³n Continua (CI/CD) con GitHub Actions
Para asegurar que no se introduzcan regresiones al proyecto, hemos configurado un flujo de trabajo (Workflow) en GitHub Actions (`.github/workflows/pr_tests.yml`). 

**Â¿QuÃ© hace automÃ¡ticamente?**
Cada vez que un desarrollador hace un *Push* o crea un *Pull Request* hacia las ramas `main` o `develop`:
1. El servidor de GitHub arranca un entorno virtual Linux con Java 17.
2. Ejecuta `./gradlew testDebugUnitTest` para validar todas nuestras pruebas de Use Cases y ViewModels.
3. Genera y sube el reporte de cobertura HTML (`koverHtmlReportDebug`) como un artefacto descargable.

**Nota sobre Tests Instrumentados:**
Los tests que requieren emulador (`connectedDebugAndroidTest`) no estÃ¡n incluidos de momento en el flujo bÃ¡sico para evitar tiempos muertos en la validaciÃ³n rÃ¡pida del PR, pero deben ejecutarse localmente antes de solicitar el PR.

---
*(Este documento se mantendrÃ¡ sincronizado con el cÃ³digo. Cualquier bug detectado en producciÃ³n en el futuro se traducirÃ¡ en un nuevo escenario "Given-When-Then" aquÃ­ antes de escribir el parche).*

---

## MÃ³dulo de Reportes

#### ReportesViewModel â StateFlows contextuales [#299]

**Test VM-R1: campanias emite lista vacÃ­a cuando la BD estÃ¡ vacÃ­a**
*   **Given:** El `ReportesViewModel` inicia con BD sin campaÃ±as.
*   **When:** Se observa el StateFlow `campanias`.
*   **Then:** Debe emitir una lista vacÃ­a.

**Test VM-R2: campanias emite la lista real cuando la BD tiene registros**
*   **Given:** La BD tiene 2 campaÃ±as registradas.
*   **When:** Se observa el StateFlow `campanias`.
*   **Then:** Debe emitir exactamente esas 2 campaÃ±as.

**Test VM-R3: seleccionarCampaniaIndividual actualiza campaniaIndividual**
*   **Given:** El ViewModel estÃ¡ inicializado sin selecciÃ³n (campaniaIndividual = null).
*   **When:** Se llama a `seleccionarCampaniaIndividual(campania)`.
*   **Then:** `campaniaIndividual` debe emitir la campaÃ±a elegida.

**Test VM-R4: insumosIndividual emite lista vacÃ­a cuando no hay campaÃ±a seleccionada**
*   **Given:** No hay campaÃ±a seleccionada.
*   **When:** Se observa `insumosIndividual`.
*   **Then:** Debe emitir lista vacÃ­a sin consultar la BD.

**Test VM-R5: pieChartData emite null cuando no hay campaÃ±a seleccionada**
*   **Given:** No hay campaÃ±a seleccionada (insumosIndividual vacÃ­o).
*   **When:** Se observa `pieChartData`.
*   **Then:** Debe emitir `null` (el grÃ¡fico no debe mostrarse).

#### ReportesViewModel â desglose cosechas por destino [#301]

**Test VM-R6: desgloseCosechasData agrupa por almacÃ©n y venta correctamente**
*   **Given:** Una campaÃ±a con cosechas mixtas (algunas con `almacen` no vacÃ­o, otras con `almacen` en blanco).
*   **When:** Se selecciona esa campaÃ±a con `seleccionarCampaniaIndividual()`.
*   **Then:** `desgloseCosechasData` debe emitir un `PieChartData` con 2 slices:
    - Slice "Almacenada": suma de cantidades con `almacen.isNotBlank()`.
    - Slice "Vendida": suma de cantidades con `almacen.isBlank()`.

**Test VM-R7: desgloseCosechasData emite null cuando no hay cosechas**
*   **Given:** Una campaÃ±a seleccionada pero sin cosechas en la BD.
*   **When:** Se observa `desgloseCosechasData`.
*   **Then:** Debe emitir `null` (sin grÃ¡fico).

#### ReportesViewModel â guardia de exportaciÃ³n [#300]

**Test VM-R8: exportarReporteCsv emite error si no hay campaÃ±a seleccionada**
*   **Given:** No hay campaÃ±a seleccionada (`campaniaIndividual = null`).
*   **When:** Se llama a `exportarReporteCsv(uri, context)`.
*   **Then:** `exportStatus` debe emitir `"Seleccione una campaÃ±a para exportar"` y no debe invocarse `ReportExporter`.

**Test VM-R9: exportarReportePdf emite error si no hay campaÃ±a seleccionada**
*   **Given:** No hay campaÃ±a seleccionada.
*   **When:** Se llama a `exportarReportePdf(uri, context)`.
*   **Then:** `exportStatus` debe emitir `"Seleccione una campaÃ±a para exportar"`.

#### ReportesViewModel â comparaciÃ³n real entre campaÃ±as [#302]

**Test VM-R10: cosechasA emite la lista de cosechas de la campaÃ±a A seleccionada**
*   **Given:** La BD tiene cosechas asociadas a la campaÃ±a con `id = 1`.
*   **When:** Se llama a `seleccionarCampaniaA(campaniaSoja)` donde `campaniaSoja.id = 1`.
*   **Then:** `cosechasA` debe emitir la lista real de cosechas de esa campaÃ±a.

**Test VM-R11: cosechasA emite lista vacÃ­a cuando no hay campaÃ±a A seleccionada**
*   **Given:** No hay campaÃ±a seleccionada en el comparador (campaniaA = null).
*   **When:** Se observa `cosechasA`.
*   **Then:** Debe emitir una lista vacÃ­a.


### ReportesViewModel
- **VM-R12:** Given misma campaña en A y B / When comparar / Then se emite estado de advertencia (UI lo maneja con condicional de igualdad de IDs).

## ValidarDatosCosechaUseCase
- **Dado** cantidad = null -> **Cuando** invoke() -> **Entonces** retorna Error("La cantidad debe ser mayor a 0.")
- **Dado** fecha = null -> **Cuando** invoke() -> **Entonces** retorna Error("La fecha es obligatoria.")
- **Dado** isAlmacenada=true y almacen en blanco -> **Cuando** invoke() -> **Entonces** retorna Error("El nombre del almacen o silo es obligatorio.")
- **Dado** todos los campos son válidos -> **Cuando** invoke() -> **Entonces** retorna Success

## FormularioInsumoViewModel  Validación al guardar
- **Dado** nombre vacío y se llama guardar() -> **Cuando** validarInsumoUseCase devuelve error -> **Entonces** state.errorNombre != null y NO se llama al UseCase de inserción
- **Dado** nombre válido, categoria válida -> **Cuando** guardar() -> **Entonces** se invoca el UseCase de inserción
- **Dado** el usuario escribe en el campo nombre -> **Cuando** onNombreChange() -> **Entonces** errorNombre se limpia (sin validar aún)


## FormularioCosechaViewModel - Edición y validación por campo (#335 / #336)

**Test VM-C6: Init con cosechaId válido carga la cosecha en el estado**
*   **Given:** SavedStateHandle contiene cosechaId = 7 y obtenerCosechaPorIdUseCase(7) retorna una cosecha con cantidad 55.0 y almacén "Silo A".
*   **When:** Se inicializa el ViewModel.
*   **Then:** state.cosechaId == 7, state.cantidad == "55.0", state.almacen == "Silo A", state.almacenado == true.

**Test VM-C7: Error de cantidad va a errorCantidad, no a errorFecha**
*   **Given:** Campaña seleccionada. ValidarDatosCosechaUseCase retorna Error("La cantidad debe ser mayor a 0.").
*   **When:** Se llama a guardar().
*   **Then:** errorCantidad != null, errorFecha == null.

**Test VM-C8: Error de fecha va a errorFecha, no a errorCantidad**
*   **Given:** Campaña y cantidad válidas. ValidarDatosCosechaUseCase retorna Error("La fecha es obligatoria.").
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



# Plan EstratÃ©gico y Casos de Prueba (Living Documentation)

Este documento centraliza la estrategia de testing del proyecto "Don Elio" y actÃºa como fuente de la verdad para escribir las pruebas automatizadas (Test Cases). Es un **Living Document** (Documento Vivo), lo que significa que **deberemos mantenerlo actualizado obligatoriamente** cada vez que modifiquemos el cÃ³digo o agreguemos nuevas funcionalidades, asegurando que las pruebas y la documentaciÃ³n no se desfasen.

## 1. Stack TecnolÃ³gico de Testing
*   **Unit Testing (Casos de Uso, ViewModels, Mappers):** `JUnit 4`, `MockK` (Mocks nativos Kotlin) y `Turbine` (Pruebas de flujos/Flows). 
    *   *Importante:* Para validar excepciones dentro de corrutinas (`runTest`), no se debe usar `assertThrows` de JUnit (ya que pierde el contexto suspendido), sino bloques nativos `try-catch` o `runCatching`.
*   **Pruebas de IntegraciÃ³n/Base de Datos (DAOs):** `AndroidX Test`, `Room Testing` (con `inMemoryDatabaseBuilder`) ejecutado en Emulador (Pruebas Instrumentadas).
*   **Pruebas de Interfaz de Usuario (UI):** `Compose Test Rule` nativo.

---

## 2. AnÃ¡lisis de Discrepancias (Documento 2025 vs Realidad 2026)

Al contrastar la propuesta del aÃ±o 2025 con la arquitectura real implementada en la App, detectamos e implementamos mejoras significativas que impactan la forma en que escribiremos los tests:

1.  **Redundancia de EdiciÃ³n (CampaÃ±as - CU2 y CU4):**
    *   *En 2025:* Se separaba "Entrar al menÃº" (CU2) de "Editar los campos" (CU4).
    *   *Realidad:* La arquitectura moderna expone un solo `EditarCampaniaUseCase`. AdemÃ¡s, se agregÃ³ el campo **`estaActiva`** a la entidad `Campania` para controlar estados (por ejemplo, si estÃ¡ terminada o en curso). Testearemos directamente la actualizaciÃ³n de este estado en BD.
2.  **Arquitectura de Notificaciones (Tareas - CU5):**
    *   *En 2025:* DependÃ­a de un "Actor Externo".
    *   *Realidad:* Reemplazado internamente por `WorkManagerTaskReminderScheduler`. Los tests de tareas deberÃ¡n validar (vÃ­a `MockK`) que el scheduler se mande a llamar o se cancele (ej. al completar o borrar una tarea).
3.  **UnificaciÃ³n de MÃ³dulo de Cosechas (CU6 y CU7):**
    *   *En 2025:* "Cosecha" (CU6) y "Datos no almacenados" (CU7) corrÃ­an por caminos distintos.
    *   *Realidad:* Bifurcamos la lÃ³gica limpiamente en `RegistrarCosechaUseCase` (para silos) y `RegistrarCosechaConVentaUseCase` (Venta o Reserva como alimento). Los tests cubrirÃ¡n ambas variantes de inserciÃ³n.
4.  **Refactor Total del MÃ³dulo de Insumos (CU9):**
    *   *En 2025:* Los insumos se creaban directamente vinculados a una campaÃ±a.
    *   *Realidad:* **Un cambio vital.** Ahora existe un CatÃ¡logo Global (`CrearInsumoCatalogoUseCase`) y posteriormente una vinculaciÃ³n a la campaÃ±a (`AsignarInsumoACampaniaUseCase`). AdemÃ¡s, el catÃ¡logo tiene la columna **`activo`**. Si el usuario elimina un insumo del catÃ¡logo (`EliminarInsumoCatalogoUseCase`), el test deberÃ¡ corroborar que **NO se hace un `DELETE` en la DB**, sino un `UPDATE activo = false` (Soft-Delete) para no corromper los histÃ³ricos de campaÃ±as pasadas.
5.  **MÃ³dulos Nuevos (No previstos en 2025):**
    *   *AutenticaciÃ³n:* `LoginUseCase` (SHA-256) y `RegistroUseCase`.
    *   *Backups:* `CrearBackupUseCase` y `RestaurarBackupUseCase` usando SAF de Android.

---

## 3. Pruebas Fuera de los Casos de Uso (Out of Scope Tests)

No toda la app es Casos de Uso. Existen componentes de bajo nivel y de infraestructura que testearemos independientemente:
*   **DAOs (Data Access Objects):** 
    Pruebas instrumentadas sobre `UsuarioDao`, `CampaniaDao`, `CampaniaInsumoDao` (validando foreign keys, borrados en cascada fÃ­sicos, y los queries filtrados por `activo = 1`).
*   **Mappers (Data <-> Domain):** 
    Pruebas unitarias para validar que al pasar de Entity a Domain Model no se pierda informaciÃ³n y viceversa.
*   **ViewModels (Presentation):** 
    Validar la emisiÃ³n correcta de los estados (`Loading`, `Success`, `Error`) hacia Jetpack Compose usando `Turbine` (ej: `LoginViewModelTest` verifica la transiciÃ³n a `isLoading = true` y luego `loginExitoso = true` o la asignaciÃ³n de mensajes de error).

---

## 4. Escenarios de Pruebas (Behavior-Driven Development - BDD)

A continuaciÃ³n, estructuramos los tests en formato `Given-When-Then` por mÃ³dulo, respetando el orden lÃ³gico de los Casos de Uso.

### MÃ³dulo de CampaÃ±as (CU1 - CU4)

**Test 1: Crear CampaÃ±a Exitosa**
*   **Given:** Un nombre vÃ¡lido "Trigo de Invierno", cultivo "Trigo" y una fecha correcta.
*   **When:** Invoco `CrearCampaniaUseCase`.
*   **Then:** El sistema debe insertar el registro en el repositorio y emitir el estado `Resource.Success`.

**Test 2: Crear CampaÃ±a con Errores**
*   **Given:** Un nombre vacÃ­o "".
*   **When:** Invoco `CrearCampaniaUseCase`.
*   **Then:** El sistema debe emitir `Resource.Error` con mensaje "El nombre no puede estar vacÃ­o" y NO llamar al repositorio.

**Test UC-V1: ValidarDatosCampaniaUseCase â Nombre vacÃ­o**
*   **Given:** nombre = "", cultivo = "Soja", fechaInicio = <fecha futura>, isEditMode = false
*   **When:** invoke(nombre, cultivo, fechaInicio, isEditMode)
*   **Then:** esValido = false, errorNombre = "El nombre es obligatorio"

**Test UC-V2: ValidarDatosCampaniaUseCase â Fecha pasada en creaciÃ³n**
*   **Given:** nombre = "CampaÃ±a", cultivo = "MaÃ­z", fechaInicio = <ayer en millis>, isEditMode = false
*   **When:** invoke(...)
*   **Then:** esValido = false, errorFecha = "La fecha no puede ser anterior a hoy"

**Test UC-V3: ValidarDatosCampaniaUseCase â Fecha pasada permitida en ediciÃ³n**
*   **Given:** nombre = "CampaÃ±a", cultivo = "MaÃ­z", fechaInicio = <ayer en millis>, isEditMode = true
*   **When:** invoke(...)
*   **Then:** esValido = true, errorFecha = null

**Test UC-V4: ValidarDatosCampaniaUseCase â Todos los campos vÃ¡lidos**
*   **Given:** Todos los campos correctos, isEditMode = false
*   **When:** invoke(...)
*   **Then:** esValido = true, todos los errores = null

### MÃ³dulo de Insumos (CU9 - CU9.4)

**Test 3: EliminaciÃ³n LÃ³gica (Soft-Delete) de Insumo del CatÃ¡logo**
*   **Given:** Que el insumo "Glifosato" existe en el catÃ¡logo con `activo = true` y ya fue utilizado en 2 campaÃ±as.
*   **When:** Invoco `EliminarInsumoCatalogoUseCase` pasando ese insumo.
*   **Then:** El repositorio debe realizar un `UPDATE` (cambiando `activo` a `false`) y NO un `DELETE` fÃ­sico. Las llamadas a `ObtenerCatalogoInsumosUseCase` ya no deben retornarlo.

**Test 4: AsignaciÃ³n de Insumo a CampaÃ±a**
*   **Given:** El "Glifosato" (activo en el catÃ¡logo) y la campaÃ±a "Trigo de Invierno".
*   **When:** Invoco `AsignarInsumoACampaniaUseCase` pasando `cantidad = 5` y `precio = 100`.
*   **Then:** Se crea un registro en `CampaniaInsumoEntity` relacionando los IDs y estableciendo el coste.

**Test UC-V5: ValidarInsumoUseCase â CategorÃ­a vacÃ­a**
*   **Given:** nombre = "Herbicida", categoria = ""
*   **When:** invoke(nombre, categoria)
*   **Then:** esValido = false, errorCategoria = "La categorÃ­a es obligatoria"

**Test UC-V6: ValidarInsumoUseCase â Ambos campos vÃ¡lidos**
*   **Given:** nombre = "Herbicida", categoria = "QuÃ­mico"
*   **When:** invoke(nombre, categoria)
*   **Then:** esValido = true, errorNombre = null, errorCategoria = null

### MÃ³dulo de Tareas (CU5 - CU5.4)

#### TareaViewModel â sincronizarCampania() [#292]

**Test VM-T1: sincronizarCampania actualiza el id cuando difiere del actual**
*   **Given:** El `TareaViewModel` inicia sin `campaniaId` en el `SavedStateHandle` (estado inicial `null`).
*   **When:** Se llama a `sincronizarCampania(5)`.
*   **Then:** El StateFlow `campaniaIdSeleccionada` debe emitir el valor `5`.

**Test VM-T2: sincronizarCampania no emite si el id es igual al actual**
*   **Given:** El `TareaViewModel` ya tiene `campaniaIdSeleccionada = 5`.
*   **When:** Se llama a `sincronizarCampania(5)` con el mismo valor.
*   **Then:** El StateFlow **no** debe emitir un nuevo evento (idempotencia garantizada).

**Test VM-T3: tareas emite lista vacÃ­a si no hay campaniaId vÃ¡lido**
*   **Given:** El `TareaViewModel` inicia sin `campaniaId` vÃ¡lido.
*   **When:** Se observa el StateFlow `tareas`.
*   **Then:** Debe emitir inmediatamente una lista vacÃ­a, sin llamar al repositorio.

**Test VM-T4: isCampaniaValid emite false cuando campaniaId es nulo**
*   **Given:** `campaniaIdSeleccionada` es `null`.
*   **When:** Se observa `isCampaniaValid`.
*   **Then:** Debe emitir `false`.

**Test VM-T5: isCampaniaValid emite true tras sincronizarCampania con id vÃ¡lido**
*   **Given:** El ViewModel inicia con `campaniaId = null`.
*   **When:** Se llama a `sincronizarCampania(3)`.
*   **Then:** `isCampaniaValid` debe emitir `true`.


**Test 5: Agendar tarea con recordatorio activado**
*   **Given:** Una nueva tarea "Revisar fertilizante" con el switch `notificar = true`.
*   **When:** Invoco `CrearTareaUseCase`.
*   **Then:** El sistema guarda la tarea en BD y, posteriormente, invoca `taskReminderScheduler.schedule(tarea)`.

**Test 6: Completar tarea programada (CancelaciÃ³n de Alerta)**
*   **Given:** La tarea anterior, que actualmente tiene notificaciones encoladas.
*   **When:** Invoco `ConfirmarTareaUseCase` seteando la tarea como `completada = true`.
*   **Then:** El estado de la tarea cambia en BD, y obligatoriamente se invoca `taskReminderScheduler.cancel(tarea.id)` para evitar alertas fantasma.

### MÃ³dulo de Cosechas (CU6 - CU7)

**Test 7: Registrar Cosecha No Almacenada (Venta/Reserva)**
*   **Given:** Una cosecha de "Soja" que no va al silo, sino que se vende (`venta = true`) a $100.
*   **When:** Invoco `RegistrarCosechaConVentaUseCase`.
*   **Then:** El sistema inserta el registro base en la tabla Cosechas, toma el ID generado, e inserta un segundo registro en `CosechaNoAlmacenadaEntity` vinculando la venta y el precio.

**Test 8: Listar Cosechas de una CampaÃ±a**
*   **Given:** Una campaÃ±a con cosechas mixtas (en silo y vendidas).
*   **When:** Invoco `ObtenerCosechasPorCampaniaUseCase` y `ObtenerCosechasNoAlmacenadasUseCase`.
*   **Then:** El repositorio debe devolver dos flujos distintos. El ViewModel debe ser capaz de fusionarlos para mostrar quÃ© fracciÃ³n de la cosecha total fue vendida.

**Test 8.1: Formulario de Cosecha - Sin CampaÃ±a Seleccionada (Issue 7)**
*   **Given:** Un `FormularioCosechaViewModel` creado sin `campaniaId` en el `SavedStateHandle` (acceso vÃ­a navegaciÃ³n global).
*   **When:** El usuario ingresa una cantidad vÃ¡lida y presiona "Guardar Registro".
*   **Then:** Se setea `errorCampania = "Debe seleccionar una campaÃ±a"` y no se llama a ningÃºn use case de registro.

**Test 8.2: Formulario de Cosecha - Cantidad Obligatoria (Issue 12)**
*   **Given:** Una campaÃ±a seleccionada y el campo `cantidad` vacÃ­o.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se setea `errorCantidad = "La cantidad es obligatoria"` y no se llama a ningÃºn use case de registro.

**Test 8.3: Formulario de Cosecha - Precio InvÃ¡lido**
*   **Given:** Una campaÃ±a y una `cantidad` vÃ¡lidas, con `almacenado = false`, `tipo = "Venta"` y un precio no numÃ©rico (ej. "abc").
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se setea `errorPrecio = "Precio invÃ¡lido"` y no se llama a ningÃºn use case de registro.

**Test 8.4: Formulario de Cosecha - Registro Exitoso (Almacenado)**
*   **Given:** Una campaÃ±a, `cantidad = 100`, y `almacen = "Silo 1"` vÃ¡lidos.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se llama a `RegistrarCosechaUseCase` con los parÃ¡metros correctos y se emite `guardadoExitoso = true`.

**Test 8.5: Formulario de Cosecha - Registro Exitoso (Venta)**
*   **Given:** Una campaÃ±a, `cantidad = 100`, `almacenado = false`, `tipo = "Venta"` y `precio = 500` vÃ¡lidos.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se llama a `RegistrarCosechaConVentaUseCase` con los parÃ¡metros correctos y se emite `guardadoExitoso = true`.

### MÃ³dulo de Observaciones (CU8)

**Test 9: Guardar ObservaciÃ³n con Imagen Adjunta**
*   **Given:** Una nota de texto y una URI local que apunta a una foto en el dispositivo.
*   **When:** Invoco `GuardarObservacionUseCase`.
*   **Then:** El sistema guarda correctamente el string de la URI en la entidad para que luego Coil pueda renderizarla en la UI.

### MÃ³dulo de AutenticaciÃ³n (Extra 1)

**Test 10: Login Exitoso con Hash SHA-256**
*   **Given:** Un usuario "DonElio" registrado en la base de datos con contraseÃ±a hasheada.
*   **When:** El usuario ingresa la contraseÃ±a en texto plano y se invoca `LoginUseCase`.
*   **Then:** El Use Case encripta el texto plano ingresado, lo compara con la BD, coincide, y emite `Resource.Success`.

**Test 11: Login Fallido (Usuario no existe)**
*   **Given:** Un intento de acceso con el nombre "Intruso".
*   **When:** Invoco `LoginUseCase`.
*   **Then:** Retorna `Resource.Error("Usuario no encontrado")`.

### MÃ³dulo de Backups (Extra 2)

**Test 12: GeneraciÃ³n de Backup Exitoso**
*   **Given:** Una ruta URI proporcionada por el SAF (Storage Access Framework) donde el usuario tiene permisos de escritura.
*   **When:** Invoco `CrearBackupUseCase`.
*   **Then:** El archivo `.db` se copia exitosamente al destino y emite `Resource.Success`.

---

## 5. Casos de Borde (Edge Cases) a Testear
*   **CampaÃ±as:** Intentar crear una campaÃ±a con nombre vacÃ­o (DeberÃ­a fallar con `Resource.Error`).
*   **Insumos:** Intentar vincular una cantidad nula o negativa de insumos a una campaÃ±a (Lanza `IllegalArgumentException`).
*   **Tareas:** Programar una tarea en el pasado con el switch de notificar en `true`. El `WorkManagerTaskReminderScheduler` no deberÃ­a encolar notificaciones retroactivas (debe validar que el delay calculado sea > 0).
*   **Observaciones:** Intentar guardar una observaciÃ³n con el campo de texto vacÃ­o (Lanza `IllegalArgumentException`).
*   **Cosechas (Formulario):** Guardar sin campaÃ±a seleccionada (Issue 7) â Debe emitir `errorCampania` y NO crashear por FK constraint; guardar con `cantidad` o `unidad` vacÃ­as (Issue 12) â Debe emitir el error visual correspondiente y deshabilitar el botÃ³n "Guardar".
*   **AutenticaciÃ³n:** Iniciar sesiÃ³n con un usuario inexistente o con credenciales vacÃ­as (El ViewModel debe capturar la excepciÃ³n o el `null` y emitir el estado de `error` correspondiente).

---

## 6. Cobertura y EjecuciÃ³n de Tests

Para garantizar que nuestros tests efectivamente cubren la lÃ³gica de negocio, implementaremos las siguientes estrategias:

### A. EjecuciÃ³n de Pruebas (Comandos)
1.  **Pruebas Unitarias (JVM Locales):**
    *   Comando: `./gradlew testDebugUnitTest`
    *   *PropÃ³sito:* Ejecutar todas las pruebas de Use Cases y ViewModels de manera ultra rÃ¡pida sin necesidad de un emulador.
2.  **Pruebas Instrumentadas (Base de Datos):**
    *   Comando: `./gradlew connectedDebugAndroidTest`
    *   *PropÃ³sito:* Ejecutar las pruebas sobre los DAOs. Requiere que un dispositivo fÃ­sico o emulador estÃ© encendido y conectado.

### B. MediciÃ³n de Cobertura (Code Coverage)
Utilizaremos **KoverX** (o JaCoCo configurado para Kotlin) para generar reportes HTML visuales sobre quÃ© porcentaje de nuestro cÃ³digo estÃ¡ siendo probado.
*   **Comando de Cobertura (Android):** `./gradlew koverHtmlReportDebug` (Es fundamental usar la variante `Debug` para que Kover analice correctamente las clases instrumentadas de Android).
*   **Meta de Cobertura:**
    *   `domain` (Reglas de negocio y Use Cases): **MÃ­nimo 80%**. Esta capa es crÃ­tica.
    *   `data` (DAOs y Repositorios): **MÃ­nimo 70%**.
    *   `presentation` (UI): No requerirÃ¡ cobertura estricta en la fase inicial para priorizar velocidad.

### C. AutomatizaciÃ³n Continua (CI/CD) con GitHub Actions
Para asegurar que no se introduzcan regresiones al proyecto, hemos configurado un flujo de trabajo (Workflow) en GitHub Actions (`.github/workflows/pr_tests.yml`). 

**Â¿QuÃ© hace automÃ¡ticamente?**
Cada vez que un desarrollador hace un *Push* o crea un *Pull Request* hacia las ramas `main` o `develop`:
1. El servidor de GitHub arranca un entorno virtual Linux con Java 17.
2. Ejecuta `./gradlew testDebugUnitTest` para validar todas nuestras pruebas de Use Cases y ViewModels.
3. Genera y sube el reporte de cobertura HTML (`koverHtmlReportDebug`) como un artefacto descargable.

**Nota sobre Tests Instrumentados:**
Los tests que requieren emulador (`connectedDebugAndroidTest`) no estÃ¡n incluidos de momento en el flujo bÃ¡sico para evitar tiempos muertos en la validaciÃ³n rÃ¡pida del PR, pero deben ejecutarse localmente antes de solicitar el PR.

---
*(Este documento se mantendrÃ¡ sincronizado con el cÃ³digo. Cualquier bug detectado en producciÃ³n en el futuro se traducirÃ¡ en un nuevo escenario "Given-When-Then" aquÃ­ antes de escribir el parche).*

---

## MÃ³dulo de Reportes

#### ReportesViewModel â StateFlows contextuales [#299]

**Test VM-R1: campanias emite lista vacÃ­a cuando la BD estÃ¡ vacÃ­a**
*   **Given:** El `ReportesViewModel` inicia con BD sin campaÃ±as.
*   **When:** Se observa el StateFlow `campanias`.
*   **Then:** Debe emitir una lista vacÃ­a.

**Test VM-R2: campanias emite la lista real cuando la BD tiene registros**
*   **Given:** La BD tiene 2 campaÃ±as registradas.
*   **When:** Se observa el StateFlow `campanias`.
*   **Then:** Debe emitir exactamente esas 2 campaÃ±as.

**Test VM-R3: seleccionarCampaniaIndividual actualiza campaniaIndividual**
*   **Given:** El ViewModel estÃ¡ inicializado sin selecciÃ³n (campaniaIndividual = null).
*   **When:** Se llama a `seleccionarCampaniaIndividual(campania)`.
*   **Then:** `campaniaIndividual` debe emitir la campaÃ±a elegida.

**Test VM-R4: insumosIndividual emite lista vacÃ­a cuando no hay campaÃ±a seleccionada**
*   **Given:** No hay campaÃ±a seleccionada.
*   **When:** Se observa `insumosIndividual`.
*   **Then:** Debe emitir lista vacÃ­a sin consultar la BD.

**Test VM-R5: pieChartData emite null cuando no hay campaÃ±a seleccionada**
*   **Given:** No hay campaÃ±a seleccionada (insumosIndividual vacÃ­o).
*   **When:** Se observa `pieChartData`.
*   **Then:** Debe emitir `null` (el grÃ¡fico no debe mostrarse).

#### ReportesViewModel â desglose cosechas por destino [#301]

**Test VM-R6: desgloseCosechasData agrupa por almacÃ©n y venta correctamente**
*   **Given:** Una campaÃ±a con cosechas mixtas (algunas con `almacen` no vacÃ­o, otras con `almacen` en blanco).
*   **When:** Se selecciona esa campaÃ±a con `seleccionarCampaniaIndividual()`.
*   **Then:** `desgloseCosechasData` debe emitir un `PieChartData` con 2 slices:
    - Slice "Almacenada": suma de cantidades con `almacen.isNotBlank()`.
    - Slice "Vendida": suma de cantidades con `almacen.isBlank()`.

**Test VM-R7: desgloseCosechasData emite null cuando no hay cosechas**
*   **Given:** Una campaÃ±a seleccionada pero sin cosechas en la BD.
*   **When:** Se observa `desgloseCosechasData`.
*   **Then:** Debe emitir `null` (sin grÃ¡fico).

#### ReportesViewModel â guardia de exportaciÃ³n [#300]

**Test VM-R8: exportarReporteCsv emite error si no hay campaÃ±a seleccionada**
*   **Given:** No hay campaÃ±a seleccionada (`campaniaIndividual = null`).
*   **When:** Se llama a `exportarReporteCsv(uri, context)`.
*   **Then:** `exportStatus` debe emitir `"Seleccione una campaÃ±a para exportar"` y no debe invocarse `ReportExporter`.

**Test VM-R9: exportarReportePdf emite error si no hay campaÃ±a seleccionada**
*   **Given:** No hay campaÃ±a seleccionada.
*   **When:** Se llama a `exportarReportePdf(uri, context)`.
*   **Then:** `exportStatus` debe emitir `"Seleccione una campaÃ±a para exportar"`.

#### ReportesViewModel â comparaciÃ³n real entre campaÃ±as [#302]

**Test VM-R10: cosechasA emite la lista de cosechas de la campaÃ±a A seleccionada**
*   **Given:** La BD tiene cosechas asociadas a la campaÃ±a con `id = 1`.
*   **When:** Se llama a `seleccionarCampaniaA(campaniaSoja)` donde `campaniaSoja.id = 1`.
*   **Then:** `cosechasA` debe emitir la lista real de cosechas de esa campaÃ±a.

**Test VM-R11: cosechasA emite lista vacÃ­a cuando no hay campaÃ±a A seleccionada**
*   **Given:** No hay campaÃ±a seleccionada en el comparador (campaniaA = null).
*   **When:** Se observa `cosechasA`.
*   **Then:** Debe emitir una lista vacÃ­a.


### ReportesViewModel
- **VM-R12:** Given misma campaña en A y B / When comparar / Then se emite estado de advertencia (UI lo maneja con condicional de igualdad de IDs).

## ValidarDatosCosechaUseCase
- **Dado** cantidad = null -> **Cuando** invoke() -> **Entonces** retorna Error("La cantidad debe ser mayor a 0.")
- **Dado** fecha = null -> **Cuando** invoke() -> **Entonces** retorna Error("La fecha es obligatoria.")
- **Dado** isAlmacenada=true y almacen en blanco -> **Cuando** invoke() -> **Entonces** retorna Error("El nombre del almacen o silo es obligatorio.")
- **Dado** todos los campos son válidos -> **Cuando** invoke() -> **Entonces** retorna Success

## FormularioInsumoViewModel  Validación al guardar
- **Dado** nombre vacío y se llama guardar() -> **Cuando** validarInsumoUseCase devuelve error -> **Entonces** state.errorNombre != null y NO se llama al UseCase de inserción
- **Dado** nombre válido, categoria válida -> **Cuando** guardar() -> **Entonces** se invoca el UseCase de inserción
- **Dado** el usuario escribe en el campo nombre -> **Cuando** onNombreChange() -> **Entonces** errorNombre se limpia (sin validar aún)


## FormularioCosechaViewModel - Edición y validación por campo (#335 / #336)

**Test VM-C6: Init con cosechaId válido carga la cosecha en el estado**
*   **Given:** SavedStateHandle contiene cosechaId = 7 y obtenerCosechaPorIdUseCase(7) retorna una cosecha con cantidad 55.0 y almacén "Silo A".
*   **When:** Se inicializa el ViewModel.
*   **Then:** state.cosechaId == 7, state.cantidad == "55.0", state.almacen == "Silo A", state.almacenado == true.

**Test VM-C7: Error de cantidad va a errorCantidad, no a errorFecha**
*   **Given:** Campaña seleccionada. ValidarDatosCosechaUseCase retorna Error("La cantidad debe ser mayor a 0.").
*   **When:** Se llama a guardar().
*   **Then:** errorCantidad != null, errorFecha == null.

**Test VM-C8: Error de fecha va a errorFecha, no a errorCantidad**
*   **Given:** Campaña y cantidad válidas. ValidarDatosCosechaUseCase retorna Error("La fecha es obligatoria.").
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

**VM-C-S1: campaniaId explícito en SavedState no se sobreescribe por el manager**
* **Dado** el ViewModel se crea con campaniaId = 5 en SavedStateHandle.
* **Cuando** el UltimaSeleccionManager emite id = 3.
* **Entonces** campaniaIdSeleccionada permanece en 5.

**VM-C-S2: sin campaniaId en SavedState el manager actúa como fallback**
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
* **Dado** el ViewModel inicia sin campaniaId válido.
* **Cuando** se observa isCampaniaValid.
* **Entonces** isCampaniaValid = false.

**VM-C-S6: isCampaniaValid emite true tras sincronizarCampania con id válido**
* **Dado** el ViewModel inicia sin campaniaId.
* **Cuando** se llama a sincronizarCampania(1).
* **Entonces** isCampaniaValid = true.

## TareaViewModel - Fix Race Condition (#441)

**VM-T-S1: campaniaId explícito en SavedState no se sobreescribe por el manager**
* **Dado** el ViewModel se crea con campaniaId = 5 en SavedStateHandle.
* **Cuando** el UltimaSeleccionManager emite id = 3.
* **Entonces** filtroCampania permanece en 5.

**VM-T-S2: sin campaniaId en SavedState el manager actúa como fallback**
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
* **Entonces** devuelve "-`$`6,1M" con el signo al frente (no al final como haría NumberFormat de locale es_AR).

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
