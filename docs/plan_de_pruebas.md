# Plan Estratégico y Casos de Prueba (Living Documentation)

Este documento centraliza la estrategia de testing del proyecto "Don Elio" y actúa como fuente de la verdad para escribir las pruebas automatizadas (Test Cases). Es un **Living Document** (Documento Vivo), lo que significa que **deberemos mantenerlo actualizado obligatoriamente** cada vez que modifiquemos el código o agreguemos nuevas funcionalidades, asegurando que las pruebas y la documentación no se desfasen.

## 1. Stack Tecnológico de Testing
*   **Unit Testing (Casos de Uso, ViewModels, Mappers):** `JUnit 4`, `MockK` (Mocks nativos Kotlin) y `Turbine` (Pruebas de flujos/Flows).
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
    Validar la emisión correcta de los estados (`Loading`, `Success`, `Error`) hacia Jetpack Compose cuando reciben datos de los Use Cases.

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

### Módulo de Insumos (CU9 - CU9.4)

**Test 3: Eliminación Lógica (Soft-Delete) de Insumo del Catálogo**
*   **Given:** Que el insumo "Glifosato" existe en el catálogo con `activo = true` y ya fue utilizado en 2 campañas.
*   **When:** Invoco `EliminarInsumoCatalogoUseCase` pasando ese insumo.
*   **Then:** El repositorio debe realizar un `UPDATE` (cambiando `activo` a `false`) y NO un `DELETE` físico. Las llamadas a `ObtenerCatalogoInsumosUseCase` ya no deben retornarlo.

**Test 4: Asignación de Insumo a Campaña**
*   **Given:** El "Glifosato" (activo en el catálogo) y la campaña "Trigo de Invierno".
*   **When:** Invoco `AsignarInsumoACampaniaUseCase` pasando `cantidad = 5` y `precio = 100`.
*   **Then:** Se crea un registro en `CampaniaInsumoEntity` relacionando los IDs y estableciendo el coste.

### Módulo de Tareas (CU5 - CU5.4)

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
*   **Campañas:** Intentar editar una campaña pasándole un ID que ya fue eliminado (Debería fallar amablemente).
*   **Insumos:** Intentar vincular una cantidad negativa de insumos a una campaña (Debería lanzar `IllegalArgumentException`).
*   **Tareas:** Programar una tarea en el pasado con el switch de notificar en `true`. El `WorkManagerTaskReminderScheduler` no debería encolar notificaciones retroactivas (debe validar que el delay calculado sea > 0).

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
*   **Meta de Cobertura:**
    *   `domain` (Reglas de negocio y Use Cases): **Mínimo 80%**. Esta capa es crítica.
    *   `data` (DAOs y Repositorios): **Mínimo 70%**.
    *   `presentation` (UI): No requerirá cobertura estricta en la fase inicial para priorizar velocidad.

### C. Automatización (CI/CD) - Opcional
En un futuro, se puede configurar **GitHub Actions** o **GitLab CI** para que ejecute automáticamente `./gradlew testDebugUnitTest` cada vez que se haga un *Push* o *Pull Request* hacia la rama principal, bloqueando código que rompa las reglas de negocio descritas en este documento.

---
*(Este documento se mantendrá sincronizado con el código. Cualquier bug detectado en producción en el futuro se traducirá en un nuevo escenario "Given-When-Then" aquí antes de escribir el parche).*
