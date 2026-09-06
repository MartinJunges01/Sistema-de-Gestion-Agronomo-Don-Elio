# Plan Estratégico y Casos de Prueba (Living Documentation)

Este documento centraliza la estrategia de testing del proyecto "Don Elio" y actúa como fuente de la verdad para escribir las pruebas automatizadas (Test Cases). Es un **Living Document** (Documento Vivo), lo que significa que **deberemos mantenerlo actualizado obligatoriamente** cada vez que modifiquemos el código o agreguemos nuevas funcionalidades, asegurando que las pruebas y la documentación no se desfasen.

## 1. Stack Tecnológico de Testing
*   **Unit Testing (Casos de Uso, ViewModels, Mappers):** `JUnit 4`, `MockK` (Mocks nativos Kotlin) y `Turbine` (Pruebas de flujos/Flows). 
    *   *Importante:* Para validar excepciones dentro de corrutinas (`runTest`), no se debe usar `assertThrows` de JUnit (ya que pierde el contexto suspendido), sino bloques nativos `try-catch` o `runCatching`.
*   **Pruebas de Integración/Base de Datos (DAOs):** `AndroidX Test`, `Room Testing` (con `inMemoryDatabaseBuilder`) ejecutado en Emulador (Pruebas Instrumentadas).
*   **Pruebas de Interfaz de Usuario (UI):** `Compose Test Rule` nativo.

---

## 2. Análisis de Discrepancias (Documento 2025 vs Realidad 2026)

Al contrastar la propuesta del año 2025 con la arquitectura real implementada en la App, detectamos e implementamos mejoras significativas que impactan la forma en que escribiremos los tests:

1.  **Redundancia de Edición (Campañas - CU2 y CU4):**
    *   *En 2025:* Se separaba "Entrar al menú" (CU2) de "Editar los campos" (CU4).
    *   *Realidad:* La arquitectura moderna expone un solo `EditarCampaniaUseCase`. Además, se agregó el campo **`estaActiva`** a la entidad `Campania` para controlar estados (por ejemplo, si está terminada o en curso). Testearemos directamente la actualización de este estado en BD.
2.  **Arquitectura de Notificaciones (Tareas - CU5):**
    *   *En 2025:* Dependía de un "Actor Externo".
    *   *Realidad:* Reemplazado internamente por `WorkManagerTaskReminderScheduler`. Los tests de tareas deberán validar (vía `MockK`) que el scheduler se mande a llamar o se cancele (ej. al completar o borrar una tarea).
3.  **Unificación de Módulo de Cosechas (CU6 y CU7):**
    *   *En 2025:* "Cosecha" (CU6) y "Datos no almacenados" (CU7) corrían por caminos distintos.
    *   *Realidad:* Bifurcamos la lógica limpiamente en `RegistrarCosechaUseCase` (para silos) y `RegistrarCosechaConVentaUseCase` (Venta o Reserva como alimento). Los tests cubrirán ambas variantes de inserción.
4.  **Refactor Total del Módulo de Insumos (CU9):**
    *   *En 2025:* Los insumos se creaban directamente vinculados a una campaña.
    *   *Realidad:* **Un cambio vital.** Ahora existe un Catálogo Global (`CrearInsumoCatalogoUseCase`) y posteriormente una vinculación a la campaña (`AsignarInsumoACampaniaUseCase`). Además, el catálogo tiene la columna **`activo`**. Si el usuario elimina un insumo del catálogo (`EliminarInsumoCatalogoUseCase`), el test deberá corroborar que **NO se hace un `DELETE` en la DB**, sino un `UPDATE activo = false` (Soft-Delete) para no corromper los históricos de campañas pasadas.
5.  **Módulos Nuevos (No previstos en 2025):**
    *   *Autenticación:* `LoginUseCase` (SHA-256) y `RegistroUseCase`.
    *   *Backups:* `CrearBackupUseCase` y `RestaurarBackupUseCase` usando SAF de Android.

---

## 3. Pruebas Fuera de los Casos de Uso (Out of Scope Tests)

No toda la app es Casos de Uso. Existen componentes de bajo nivel y de infraestructura que testearemos independientemente:
*   **DAOs (Data Access Objects):** 
    Pruebas instrumentadas sobre `UsuarioDao`, `CampaniaDao`, `CampaniaInsumoDao` (validando foreign keys, borrados en cascada físicos, y los queries filtrados por `activo = 1`).
*   **Mappers (Data <-> Domain):** 
    Pruebas unitarias para validar que al pasar de Entity a Domain Model no se pierda información y viceversa.
*   **ViewModels (Presentation):** 
    Validar la emisión correcta de los estados (`Loading`, `Success`, `Error`) hacia Jetpack Compose usando `Turbine` (ej: `LoginViewModelTest` verifica la transición a `isLoading = true` y luego `loginExitoso = true` o la asignación de mensajes de error).

---

## 4. Escenarios de Pruebas (Behavior-Driven Development - BDD)

A continuación, estructuramos los tests en formato `Given-When-Then` por módulo, respetando el orden lógico de los Casos de Uso.

### Módulo de Campañas (CU1 - CU4)

**Test 1: Crear Campaña Exitosa**
*   **Given:** Un nombre válido "Trigo de Invierno", cultivo "Trigo" y una fecha correcta.
*   **When:** Invoco `CrearCampaniaUseCase`.
*   **Then:** El sistema debe insertar el registro en el repositorio y emitir el estado `Resource.Success`.

**Test 2: Crear Campaña con Errores**
*   **Given:** Un nombre vacío "".
*   **When:** Invoco `CrearCampaniaUseCase`.
*   **Then:** El sistema debe emitir `Resource.Error` con mensaje "El nombre no puede estar vacío" y NO llamar al repositorio.

**Test UC-V1: ValidarDatosCampaniaUseCase — Nombre vacío**
*   **Given:** nombre = "", cultivo = "Soja", fechaInicio = <fecha futura>, isEditMode = false
*   **When:** invoke(nombre, cultivo, fechaInicio, isEditMode)
*   **Then:** esValido = false, errorNombre = "El nombre es obligatorio"

**Test UC-V2: ValidarDatosCampaniaUseCase — Fecha pasada en creación**
*   **Given:** nombre = "Campaña", cultivo = "Maíz", fechaInicio = <ayer en millis>, isEditMode = false
*   **When:** invoke(...)
*   **Then:** esValido = false, errorFecha = "La fecha no puede ser anterior a hoy"

**Test UC-V3: ValidarDatosCampaniaUseCase — Fecha pasada permitida en edición**
*   **Given:** nombre = "Campaña", cultivo = "Maíz", fechaInicio = <ayer en millis>, isEditMode = true
*   **When:** invoke(...)
*   **Then:** esValido = true, errorFecha = null

**Test UC-V4: ValidarDatosCampaniaUseCase — Todos los campos válidos**
*   **Given:** Todos los campos correctos, isEditMode = false
*   **When:** invoke(...)
*   **Then:** esValido = true, todos los errores = null

### Módulo de Insumos (CU9 - CU9.4)

**Test 3: Eliminación Lógica (Soft-Delete) de Insumo del Catálogo**
*   **Given:** Que el insumo "Glifosato" existe en el catálogo con `activo = true` y ya fue utilizado en 2 campañas.
*   **When:** Invoco `EliminarInsumoCatalogoUseCase` pasando ese insumo.
*   **Then:** El repositorio debe realizar un `UPDATE` (cambiando `activo` a `false`) y NO un `DELETE` físico. Las llamadas a `ObtenerCatalogoInsumosUseCase` ya no deben retornarlo.

**Test 4: Asignación de Insumo a Campaña**
*   **Given:** El "Glifosato" (activo en el catálogo) y la campaña "Trigo de Invierno".
*   **When:** Invoco `AsignarInsumoACampaniaUseCase` pasando `cantidad = 5` y `precio = 100`.
*   **Then:** Se crea un registro en `CampaniaInsumoEntity` relacionando los IDs y estableciendo el coste.

**Test UC-V5: ValidarInsumoUseCase — Categoría vacía**
*   **Given:** nombre = "Herbicida", categoria = ""
*   **When:** invoke(nombre, categoria)
*   **Then:** esValido = false, errorCategoria = "La categoría es obligatoria"

**Test UC-V6: ValidarInsumoUseCase — Ambos campos válidos**
*   **Given:** nombre = "Herbicida", categoria = "Químico"
*   **When:** invoke(nombre, categoria)
*   **Then:** esValido = true, errorNombre = null, errorCategoria = null

### Módulo de Tareas (CU5 - CU5.4)

#### TareaViewModel — sincronizarCampania() [#292]

**Test VM-T1: sincronizarCampania actualiza el id cuando difiere del actual**
*   **Given:** El `TareaViewModel` inicia sin `campaniaId` en el `SavedStateHandle` (estado inicial `null`).
*   **When:** Se llama a `sincronizarCampania(5)`.
*   **Then:** El StateFlow `campaniaIdSeleccionada` debe emitir el valor `5`.

**Test VM-T2: sincronizarCampania no emite si el id es igual al actual**
*   **Given:** El `TareaViewModel` ya tiene `campaniaIdSeleccionada = 5`.
*   **When:** Se llama a `sincronizarCampania(5)` con el mismo valor.
*   **Then:** El StateFlow **no** debe emitir un nuevo evento (idempotencia garantizada).

**Test VM-T3: tareas emite lista vacía si no hay campaniaId válido**
*   **Given:** El `TareaViewModel` inicia sin `campaniaId` válido.
*   **When:** Se observa el StateFlow `tareas`.
*   **Then:** Debe emitir inmediatamente una lista vacía, sin llamar al repositorio.

**Test VM-T4: isCampaniaValid emite false cuando campaniaId es nulo**
*   **Given:** `campaniaIdSeleccionada` es `null`.
*   **When:** Se observa `isCampaniaValid`.
*   **Then:** Debe emitir `false`.

**Test VM-T5: isCampaniaValid emite true tras sincronizarCampania con id válido**
*   **Given:** El ViewModel inicia con `campaniaId = null`.
*   **When:** Se llama a `sincronizarCampania(3)`.
*   **Then:** `isCampaniaValid` debe emitir `true`.


**Test 5: Agendar tarea con recordatorio activado**
*   **Given:** Una nueva tarea "Revisar fertilizante" con el switch `notificar = true`.
*   **When:** Invoco `CrearTareaUseCase`.
*   **Then:** El sistema guarda la tarea en BD y, posteriormente, invoca `taskReminderScheduler.schedule(tarea)`.

**Test 6: Completar tarea programada (Cancelación de Alerta)**
*   **Given:** La tarea anterior, que actualmente tiene notificaciones encoladas.
*   **When:** Invoco `ConfirmarTareaUseCase` seteando la tarea como `completada = true`.
*   **Then:** El estado de la tarea cambia en BD, y obligatoriamente se invoca `taskReminderScheduler.cancel(tarea.id)` para evitar alertas fantasma.

### Módulo de Cosechas (CU6 - CU7)

**Test 7: Registrar Cosecha No Almacenada (Venta/Reserva)**
*   **Given:** Una cosecha de "Soja" que no va al silo, sino que se vende (`venta = true`) a $100.
*   **When:** Invoco `RegistrarCosechaConVentaUseCase`.
*   **Then:** El sistema inserta el registro base en la tabla Cosechas, toma el ID generado, e inserta un segundo registro en `CosechaNoAlmacenadaEntity` vinculando la venta y el precio.

**Test 8: Listar Cosechas de una Campaña**
*   **Given:** Una campaña con cosechas mixtas (en silo y vendidas).
*   **When:** Invoco `ObtenerCosechasPorCampaniaUseCase` y `ObtenerCosechasNoAlmacenadasUseCase`.
*   **Then:** El repositorio debe devolver dos flujos distintos. El ViewModel debe ser capaz de fusionarlos para mostrar qué fracción de la cosecha total fue vendida.

**Test 8.1: Formulario de Cosecha - Sin Campaña Seleccionada (Issue 7)**
*   **Given:** Un `FormularioCosechaViewModel` creado sin `campaniaId` en el `SavedStateHandle` (acceso vía navegación global).
*   **When:** El usuario ingresa una cantidad válida y presiona "Guardar Registro".
*   **Then:** Se setea `errorCampania = "Debe seleccionar una campaña"` y no se llama a ningún use case de registro.

**Test 8.2: Formulario de Cosecha - Cantidad Obligatoria (Issue 12)**
*   **Given:** Una campaña seleccionada y el campo `cantidad` vacío.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se setea `errorCantidad = "La cantidad es obligatoria"` y no se llama a ningún use case de registro.

**Test 8.3: Formulario de Cosecha - Precio Inválido**
*   **Given:** Una campaña y una `cantidad` válidas, con `almacenado = false`, `tipo = "Venta"` y un precio no numérico (ej. "abc").
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se setea `errorPrecio = "Precio inválido"` y no se llama a ningún use case de registro.

**Test 8.4: Formulario de Cosecha - Registro Exitoso (Almacenado)**
*   **Given:** Una campaña, `cantidad = 100`, y `almacen = "Silo 1"` válidos.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se llama a `RegistrarCosechaUseCase` con los parámetros correctos y se emite `guardadoExitoso = true`.

**Test 8.5: Formulario de Cosecha - Registro Exitoso (Venta)**
*   **Given:** Una campaña, `cantidad = 100`, `almacenado = false`, `tipo = "Venta"` y `precio = 500` válidos.
*   **When:** El usuario presiona "Guardar Registro".
*   **Then:** Se llama a `RegistrarCosechaConVentaUseCase` con los parámetros correctos y se emite `guardadoExitoso = true`.

### Módulo de Observaciones (CU8)

**Test 9: Guardar Observación con Imagen Adjunta**
*   **Given:** Una nota de texto y una URI local que apunta a una foto en el dispositivo.
*   **When:** Invoco `GuardarObservacionUseCase`.
*   **Then:** El sistema guarda correctamente el string de la URI en la entidad para que luego Coil pueda renderizarla en la UI.

### Módulo de Autenticación (Extra 1)

**Test 10: Login Exitoso con Hash SHA-256**
*   **Given:** Un usuario "DonElio" registrado en la base de datos con contraseña hasheada.
*   **When:** El usuario ingresa la contraseña en texto plano y se invoca `LoginUseCase`.
*   **Then:** El Use Case encripta el texto plano ingresado, lo compara con la BD, coincide, y emite `Resource.Success`.

**Test 11: Login Fallido (Usuario no existe)**
*   **Given:** Un intento de acceso con el nombre "Intruso".
*   **When:** Invoco `LoginUseCase`.
*   **Then:** Retorna `Resource.Error("Usuario no encontrado")`.

### Módulo de Backups (Extra 2)

**Test 12: Generación de Backup Exitoso**
*   **Given:** Una ruta URI proporcionada por el SAF (Storage Access Framework) donde el usuario tiene permisos de escritura.
*   **When:** Invoco `CrearBackupUseCase`.
*   **Then:** El archivo `.db` se copia exitosamente al destino y emite `Resource.Success`.

---

## 5. Casos de Borde (Edge Cases) a Testear
*   **Campañas:** Intentar crear una campaña con nombre vacío (Debería fallar con `Resource.Error`).
*   **Insumos:** Intentar vincular una cantidad nula o negativa de insumos a una campaña (Lanza `IllegalArgumentException`).
*   **Tareas:** Programar una tarea en el pasado con el switch de notificar en `true`. El `WorkManagerTaskReminderScheduler` no debería encolar notificaciones retroactivas (debe validar que el delay calculado sea > 0).
*   **Observaciones:** Intentar guardar una observación con el campo de texto vacío (Lanza `IllegalArgumentException`).
*   **Cosechas (Formulario):** Guardar sin campaña seleccionada (Issue 7) — Debe emitir `errorCampania` y NO crashear por FK constraint; guardar con `cantidad` o `unidad` vacías (Issue 12) — Debe emitir el error visual correspondiente y deshabilitar el botón "Guardar".
*   **Autenticación:** Iniciar sesión con un usuario inexistente o con credenciales vacías (El ViewModel debe capturar la excepción o el `null` y emitir el estado de `error` correspondiente).

---

## 6. Cobertura y Ejecución de Tests

Para garantizar que nuestros tests efectivamente cubren la lógica de negocio, implementaremos las siguientes estrategias:

### A. Ejecución de Pruebas (Comandos)
1.  **Pruebas Unitarias (JVM Locales):**
    *   Comando: `./gradlew testDebugUnitTest`
    *   *Propósito:* Ejecutar todas las pruebas de Use Cases y ViewModels de manera ultra rápida sin necesidad de un emulador.
2.  **Pruebas Instrumentadas (Base de Datos):**
    *   Comando: `./gradlew connectedDebugAndroidTest`
    *   *Propósito:* Ejecutar las pruebas sobre los DAOs. Requiere que un dispositivo físico o emulador esté encendido y conectado.

### B. Medición de Cobertura (Code Coverage)
Utilizaremos **KoverX** (o JaCoCo configurado para Kotlin) para generar reportes HTML visuales sobre qué porcentaje de nuestro código está siendo probado.
*   **Comando de Cobertura (Android):** `./gradlew koverHtmlReportDebug` (Es fundamental usar la variante `Debug` para que Kover analice correctamente las clases instrumentadas de Android).
*   **Meta de Cobertura:**
    *   `domain` (Reglas de negocio y Use Cases): **Mínimo 80%**. Esta capa es crítica.
    *   `data` (DAOs y Repositorios): **Mínimo 70%**.
    *   `presentation` (UI): No requerirá cobertura estricta en la fase inicial para priorizar velocidad.

### C. Automatización Continua (CI/CD) con GitHub Actions
Para asegurar que no se introduzcan regresiones al proyecto, hemos configurado un flujo de trabajo (Workflow) en GitHub Actions (`.github/workflows/pr_tests.yml`). 

**¿Qué hace automáticamente?**
Cada vez que un desarrollador hace un *Push* o crea un *Pull Request* hacia las ramas `main` o `develop`:
1. El servidor de GitHub arranca un entorno virtual Linux con Java 17.
2. Ejecuta `./gradlew testDebugUnitTest` para validar todas nuestras pruebas de Use Cases y ViewModels.
3. Genera y sube el reporte de cobertura HTML (`koverHtmlReportDebug`) como un artefacto descargable.

**Nota sobre Tests Instrumentados:**
Los tests que requieren emulador (`connectedDebugAndroidTest`) no están incluidos de momento en el flujo básico para evitar tiempos muertos en la validación rápida del PR, pero deben ejecutarse localmente antes de solicitar el PR.

---
*(Este documento se mantendrá sincronizado con el código. Cualquier bug detectado en producción en el futuro se traducirá en un nuevo escenario "Given-When-Then" aquí antes de escribir el parche).*

---

## Módulo de Reportes

#### ReportesViewModel — StateFlows contextuales [#299]

**Test VM-R1: campanias emite lista vacía cuando la BD está vacía**
*   **Given:** El `ReportesViewModel` inicia con BD sin campañas.
*   **When:** Se observa el StateFlow `campanias`.
*   **Then:** Debe emitir una lista vacía.

**Test VM-R2: campanias emite la lista real cuando la BD tiene registros**
*   **Given:** La BD tiene 2 campañas registradas.
*   **When:** Se observa el StateFlow `campanias`.
*   **Then:** Debe emitir exactamente esas 2 campañas.

**Test VM-R3: seleccionarCampaniaIndividual actualiza campaniaIndividual**
*   **Given:** El ViewModel está inicializado sin selección (campaniaIndividual = null).
*   **When:** Se llama a `seleccionarCampaniaIndividual(campania)`.
*   **Then:** `campaniaIndividual` debe emitir la campaña elegida.

**Test VM-R4: insumosIndividual emite lista vacía cuando no hay campaña seleccionada**
*   **Given:** No hay campaña seleccionada.
*   **When:** Se observa `insumosIndividual`.
*   **Then:** Debe emitir lista vacía sin consultar la BD.

**Test VM-R5: pieChartData emite null cuando no hay campaña seleccionada**
*   **Given:** No hay campaña seleccionada (insumosIndividual vacío).
*   **When:** Se observa `pieChartData`.
*   **Then:** Debe emitir `null` (el gráfico no debe mostrarse).

#### ReportesViewModel — desglose cosechas por destino [#301]

**Test VM-R6: desgloseCosechasData agrupa por almacén y venta correctamente**
*   **Given:** Una campaña con cosechas mixtas (algunas con `almacen` no vacío, otras con `almacen` en blanco).
*   **When:** Se selecciona esa campaña con `seleccionarCampaniaIndividual()`.
*   **Then:** `desgloseCosechasData` debe emitir un `PieChartData` con 2 slices:
    - Slice "Almacenada": suma de cantidades con `almacen.isNotBlank()`.
    - Slice "Vendida": suma de cantidades con `almacen.isBlank()`.

**Test VM-R7: desgloseCosechasData emite null cuando no hay cosechas**
*   **Given:** Una campaña seleccionada pero sin cosechas en la BD.
*   **When:** Se observa `desgloseCosechasData`.
*   **Then:** Debe emitir `null` (sin gráfico).

#### ReportesViewModel — guardia de exportación [#300]

**Test VM-R8: exportarReporteCsv emite error si no hay campaña seleccionada**
*   **Given:** No hay campaña seleccionada (`campaniaIndividual = null`).
*   **When:** Se llama a `exportarReporteCsv(uri, context)`.
*   **Then:** `exportStatus` debe emitir `"Seleccione una campaña para exportar"` y no debe invocarse `ReportExporter`.

**Test VM-R9: exportarReportePdf emite error si no hay campaña seleccionada**
*   **Given:** No hay campaña seleccionada.
*   **When:** Se llama a `exportarReportePdf(uri, context)`.
*   **Then:** `exportStatus` debe emitir `"Seleccione una campaña para exportar"`.

#### ReportesViewModel — comparación real entre campañas [#302]

**Test VM-R10: cosechasA emite la lista de cosechas de la campaña A seleccionada**
*   **Given:** La BD tiene cosechas asociadas a la campaña con `id = 1`.
*   **When:** Se llama a `seleccionarCampaniaA(campaniaSoja)` donde `campaniaSoja.id = 1`.
*   **Then:** `cosechasA` debe emitir la lista real de cosechas de esa campaña.

**Test VM-R11: cosechasA emite lista vacía cuando no hay campaña A seleccionada**
*   **Given:** No hay campaña seleccionada en el comparador (campaniaA = null).
*   **When:** Se observa `cosechasA`.
*   **Then:** Debe emitir una lista vacía.


### ReportesViewModel
- **VM-R12:** Given misma campa�a en A y B / When comparar / Then se emite estado de advertencia (UI lo maneja con condicional de igualdad de IDs).

## ValidarDatosCosechaUseCase
- **Dado** cantidad = null -> **Cuando** invoke() -> **Entonces** retorna Error("La cantidad debe ser mayor a 0.")
- **Dado** fecha = null -> **Cuando** invoke() -> **Entonces** retorna Error("La fecha es obligatoria.")
- **Dado** isAlmacenada=true y almacen en blanco -> **Cuando** invoke() -> **Entonces** retorna Error("El nombre del almacen o silo es obligatorio.")
- **Dado** todos los campos son v�lidos -> **Cuando** invoke() -> **Entonces** retorna Success

## FormularioInsumoViewModel � Validaci�n al guardar
- **Dado** nombre vac�o y se llama guardar() -> **Cuando** validarInsumoUseCase devuelve error -> **Entonces** state.errorNombre != null y NO se llama al UseCase de inserci�n
- **Dado** nombre v�lido, categoria v�lida -> **Cuando** guardar() -> **Entonces** se invoca el UseCase de inserci�n
- **Dado** el usuario escribe en el campo nombre -> **Cuando** onNombreChange() -> **Entonces** errorNombre se limpia (sin validar a�n)


## FormularioCosechaViewModel - Edici�n y validaci�n por campo (#335 / #336)

**Test VM-C6: Init con cosechaId v�lido carga la cosecha en el estado**
*   **Given:** SavedStateHandle contiene cosechaId = 7 y obtenerCosechaPorIdUseCase(7) retorna una cosecha con cantidad 55.0 y almac�n "Silo A".
*   **When:** Se inicializa el ViewModel.
*   **Then:** state.cosechaId == 7, state.cantidad == "55.0", state.almacen == "Silo A", state.almacenado == true.

**Test VM-C7: Error de cantidad va a errorCantidad, no a errorFecha**
*   **Given:** Campa�a seleccionada. ValidarDatosCosechaUseCase retorna Error("La cantidad debe ser mayor a 0.").
*   **When:** Se llama a guardar().
*   **Then:** errorCantidad != null, errorFecha == null.

**Test VM-C8: Error de fecha va a errorFecha, no a errorCantidad**
*   **Given:** Campa�a y cantidad v�lidas. ValidarDatosCosechaUseCase retorna Error("La fecha es obligatoria.").
*   **When:** Se llama a guardar().
*   **Then:** errorFecha != null, errorCantidad == null.

**Test VM-C9: onFechaChange limpia errorFecha**
*   **Given:** Existe errorFecha en el state (provocado por un guardar fallido).
*   **When:** Se llama a onFechaChange(timestamp).
*   **Then:** errorFecha == null.

## M�dulo de Navegaci�n Global y UX (Reducci�n de Clics)

**Test UX-N1: Creaci�n de entidad desde Detalle de Campa�a (Grid 2xN)**
*   **Given:** El usuario se encuentra viendo los detalles de una campa�a espec�fica (ej. "Soja 2026").
*   **When:** El usuario presiona el bot�n de acceso r�pido "Agregar Cosecha" (o Tarea/Insumo/Observaci�n) desde el Grid 2xN.
*   **Then:** El sistema navega al formulario correspondiente con la campa�a "Soja 2026" ya seteada, requiriendo un total m�ximo de 3 clics para guardar.

**Test UX-N2: Creaci�n desde BottomNav (Con Cach� de Campa�a Activa)**
*   **Given:** El usuario ha interactuado previamente con la campa�a "Trigo 2025" (la �ltima selecci�n se guard� en cach�/preferencias a trav�s del Repository).
*   **When:** El usuario navega a "Tareas" usando el men� inferior (BottomNav) y presiona "Nueva Tarea".
*   **Then:** El formulario se abre con "Trigo 2025" preseleccionada, permitiendo continuar sin selecci�n manual de contexto.

**Test UX-N3: Creaci�n desde BottomNav (Sin Cach� Previo)**
*   **Given:** Es la primera vez que el usuario usa la app o no hay campa�as guardadas en cach� (ej. acaba de crear su primera campa�a pero no ha interactuado con ella).
*   **When:** Navega a "Tareas" desde BottomNav y presiona "Nueva Tarea".
*   **Then:** El campo de selecci�n de campa�a aparece vac�o o pide expl�citamente seleccionar una, obligando al usuario a establecer el contexto manualmente.
