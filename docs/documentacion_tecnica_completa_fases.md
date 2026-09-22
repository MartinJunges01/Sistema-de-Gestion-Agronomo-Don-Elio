# Documentación Técnica (Fases 1 a 5)

# FASE 1 — Documentación Técnica: Login / Autenticación + Dashboard

> **Destino en el documento principal:** `docs/documentacion_tpi_2da_entrega.md` → Sección **7.5 Descripción Técnica de Funcionalidades**
>
> **Instrucción para el equipo:** Copiar el contenido de este archivo dentro de la sección 7.5, respetando los subtítulos indicados.

---

## 7.5 Descripción Técnica de Funcionalidades

### 7.5.1 Módulo: Login / Autenticación

#### Descripción General

El módulo de autenticación es el punto de entrada a la aplicación. Implementa un flujo de **inicio de sesión local** y **registro de usuario** sin dependencia de servicios externos. Las credenciales se almacenan en la base de datos SQLite local de Room, con la contraseña protegida mediante hash SHA-256.

Existen dos pantallas que componen este módulo:

- **`LoginScreen`**: Pantalla de ingreso con usuario y contraseña.
- **`RegistroScreen`**: Pantalla para crear una cuenta nueva (nombre completo, nombre de usuario y contraseña).

#### Interacciones de Clean Architecture

**Capa Presentation**

El `LoginViewModel` es compartido por ambas pantallas (`LoginScreen` y `RegistroScreen`), inyectado por Hilt mediante `hiltViewModel()`. Expone un único estado reactivo de tipo `LoginUiState` a través de un `StateFlow`:

```kotlin
data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginExitoso: Boolean = false,
    val registroExitoso: Boolean = false
)
```

- Cuando `loginExitoso` o `registroExitoso` cambian a `true`, un `LaunchedEffect` en la pantalla navega automáticamente al Dashboard, sin lógica de navegación dentro del ViewModel.
- El botón "Ingresar" / "Registrarse" se deshabilita mientras `isLoading` es `true`, mostrando un `CircularProgressIndicator` en su lugar.
- Cualquier error de credenciales o validación se muestra en pantalla a través del campo `error` del estado.
- En compilaciones de **debug**, se expone un botón "Entrar como Invitado" que omite la validación de credenciales, útil para pruebas rápidas del equipo.

**Capa Domain**

Tres casos de uso orquestan la lógica de negocio:

| Caso de Uso | Responsabilidad |
|---|---|
| `LoginUseCase` | Valida que los campos no estén vacíos, genera el hash SHA-256 de la contraseña ingresada y la compara contra el hash almacenado en la base de datos. Retorna el objeto `Usuario` de dominio si las credenciales coinciden, o `null` si no. |
| `RegistroUseCase` | Crea un nuevo registro de usuario. Hashea la contraseña antes de persistirla. |
| `GuardarSesionUseCase` | Persiste el nombre del usuario autenticado en `SessionManager` (DataStore Preferences). Esto permite que el Dashboard muestre el saludo personalizado sin consultar la base de datos. |

El hasheo se realiza en la capa Domain (no en Data) para garantizar la independencia del algoritmo respecto al mecanismo de almacenamiento:

```kotlin
fun hashContrasena(contrasena: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val bytes = digest.digest(contrasena.toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
}
```

**Capa Data**

- `UsuarioDao` (Room) expone `getUsuarioByNombre(nombre: String): UsuarioEntity?`, operación de lectura de tipo `suspend` que se ejecuta en el hilo de IO.
- El mapper `toDomain()` convierte la entidad Room (`UsuarioEntity`) al modelo de dominio (`Usuario`), separando la representación de persistencia de la lógica de negocio.
- `SessionManager` es una clase singleton basada en **DataStore Preferences** que persiste el nombre de sesión entre reinicios de la app.

#### Diagrama de flujo de Login

```
LoginScreen
   │
   ├─[usuario, password]──► LoginViewModel.login()
   │                              │
   │                         LoginUseCase.invoke()
   │                              │
   │                         UsuarioDao.getUsuarioByNombre()
   │                              │
   │                    ┌─────────┴────────────┐
   │                    │ Hash coincide         │ No coincide
   │                    ▼                       ▼
   │              toDomain()           state.error = "Credenciales inválidas"
   │                    │
   │              GuardarSesionUseCase
   │                    │
   │              state.loginExitoso = true
   │                    │
   └──────────── LaunchedEffect → navegar al Dashboard
```

---

### 7.5.2 Módulo: Home / Dashboard de Operaciones

#### Descripción General

El `DashboardOperacionesScreen` es la pantalla central de la aplicación, visible inmediatamente después de autenticarse. Actúa como **panel de control** en tiempo real: muestra el resumen financiero del mes en curso, el indicador de cumplimiento de tareas semanales, las tareas pendientes próximas y la lista de campañas activas. Todo el contenido se actualiza automáticamente mediante flujos reactivos (`StateFlow`) sin necesidad de que el usuario refresque la pantalla manualmente.

#### Composición visual

La pantalla implementa un `LazyColumn` con las siguientes secciones en orden vertical:

| Sección | Componente | Datos mostrados |
|---|---|---|
| Encabezado | `HeaderSectionAgriCore` | Nombre del usuario, acceso a Configuración y botón Cerrar Sesión |
| Resumen Financiero | `SeccionResumenRendimiento` | Capital Invertido, Ingresos Brutos y Balance del mes actual (campaña activa) |
| Cumplimiento Semanal | `GraficoCumplimientoCircular` | Gráfico circular Canvas + porcentaje de tareas completadas en la semana |
| Tareas Próximas | Cards (`LazyColumn items`) | Hasta 5 tareas pendientes; las vencidas se resaltan en rojo |
| Campañas Activas | `CampaniaCard` | Lista de campañas con nombre, cultivo, fecha de inicio y estado (Activa/Inactiva) |

#### Interacciones de Clean Architecture

**Capa Presentation**

`HomeViewModel` orquesta cuatro flujos de datos reactivos, todos convertidos a `StateFlow` con `stateIn(SharingStarted.WhileSubscribed(5_000))`. Esto significa que el flujo upstream (Room) se activa cuando al menos un colector está suscripto, y se cancela 5 segundos después de que el último colector desaparece (por ejemplo, al salir de la pantalla):

```kotlin
val campanias: StateFlow<List<Campania>>
val tareasPendientes: StateFlow<List<Tarea>>
val resumenMensual: StateFlow<ResumenRendimiento?>
val cumplimientoSemanal: StateFlow<CumplimientoTareas?>
val userName: StateFlow<String>  // Proviene del SessionManager
```

La función `cerrarSesion()` invoca `CerrarSesionUseCase` en una corrutina del `viewModelScope`, limpiando los datos de sesión de DataStore, y la pantalla reacciona navegando a `LoginScreen`.

**Capa Domain**

| Caso de Uso | Responsabilidad |
|---|---|
| `ObtenerCampaniasActivasUseCase` | Retorna un `Flow<List<Campania>>` con campañas cuyo campo `estaActiva` es `true`. |
| `ObtenerTareasPendientesUseCase` | Retorna un `Flow<List<Tarea>>` con hasta `limite` tareas pendientes cuya fecha sea mayor a `fechaLimite` (últimos 7 días), ordenadas cronológicamente. |
| `ObtenerResumenRendimientoUseCase` | Combina cuatro flujos (campañas activas, insumos utilizados, cosechas y ventas) con el operador `combine`. Calcula el **Capital Invertido** (suma de `cantidad × precio` de todos los insumos de campañas activas), los **Ingresos Brutos** (ventas del mes actual) y el **Balance** (ingresos − capital). Si no hay campañas activas, emite `null` y el widget no se renderiza. |
| `ObtenerCumplimientoTareasUseCase` | Filtra todas las tareas cuya fecha cae dentro de la semana actual (lunes a domingo) y calcula el porcentaje de completadas (`confirmar == true`). Emite `null` si no hay tareas en la semana. |
| `CerrarSesionUseCase` | Delega en `SessionManager.logout()` para limpiar el nombre de usuario persistido en DataStore. |

**Capa Data**

- `CampaniaRepository`, `TareaRepository`, `CampaniaInsumoRepository`, `CosechaRepository` y `CosechaNoAlmacenadaRepository` exponen sus datos como `Flow<List<...>>` respaldados por Room. Cada vez que se escribe en las tablas subyacentes, Room re-emite la lista actualizada automáticamente, propagando el cambio hasta la UI.
- `SessionManager` (DataStore) provee el nombre del usuario autenticado como un `Flow<String>`, el cual es observado directamente por el ViewModel.

#### Formateo de moneda

El Dashboard implementa una función utilitaria `formatearMoneda(valor: Double): String` que adapta el formato para la configuración regional argentina:
- Valores ≥ 1.000.000 → abreviados como `$X,XM` (ej: `$6,1M`)
- Valores ≥ 1.000 → abreviados como `$XXXk` (ej: `$250K`)
- Valores negativos → el signo `-` siempre precede al símbolo `$` (ej: `-$1.500`), corrigiendo el comportamiento incorrecto de `NumberFormat.getCurrencyInstance` con la locale `es_AR`.

#### Navegación saliente

Desde el Dashboard el usuario puede navegar a:
- **Detalle de Campaña** (`onGoToDetalle(campaniaId)`) — al tocar una card de campaña o una tarea.
- **Tareas** (`onGoToTareas()`) — al tocar "Ver todas →".
- **Reportes** (`onGoToReportes()`) — al tocar "Ver detalle →" en la sección de rendimiento.
- **Configuración** (`onGoToConfig()`) — desde el encabezado.
- **Login** (`onLogoutSuccess()`) — al confirmar el cierre de sesión.

---

*[FIN_FASE_1]*


# FASE 2 — Documentación Técnica: Campañas + Tareas

> **Destino en el documento principal:** `docs/documentacion_tpi_2da_entrega.md` → Sección **7.5 Descripción Técnica de Funcionalidades**
>
> **Instrucción para el equipo:** Pegar el contenido de este archivo a continuación del [FIN_FASE_1], respetando los subtítulos indicados.

---

### 7.5.3 Módulo: Gestión de Campañas

#### Descripción General

El módulo de Campañas es el eje central del sistema. Una **campaña** representa un ciclo agrícola completo (siembra, insumos, cosecha) asociado a un cultivo y a un período de tiempo. Todos los demás módulos (Tareas, Insumos, Cosechas, Observaciones) se vinculan siempre a una campaña.

El módulo está compuesto por tres pantallas y tres ViewModels especializados:

| Pantalla | ViewModel | Propósito |
|---|---|---|
| `GestionCampaniasScreen` | `GestionCampaniasViewModel` | Listado de campañas activas e historial de inactivas |
| `DetalleCampaniaScreen` | `CampaniaDetailViewModel` | Hub de navegación hacia los sub-módulos de la campaña |
| `FormularioCampaniaScreen` | `CampaniaFormViewModel` | Alta y edición de una campaña |

#### Interacciones de Clean Architecture — Listado (`GestionCampaniasScreen`)

**Capa Presentation**

`GestionCampaniasViewModel` expone dos `StateFlow` reactivos separados: `campaniasActivas` y `campaniasInactivas`. La pantalla los observa de forma independiente para renderizar dos secciones diferenciadas:

- **Activas:** siempre visibles, sin opción de eliminación (solo se accede al detalle).
- **Historial:** colapsable mediante un estado local `showHistorial`. Solo las campañas inactivas pueden eliminarse, operación que requiere confirmación del usuario a través de un `AlertDialog`.

Los errores de operaciones asíncronas (por ejemplo, fallo al eliminar) se emiten a través de un `MutableSharedFlow<String>` llamado `errorMessage`. La pantalla lo recolecta con un `LaunchedEffect` y lo muestra en un `SnackbarHost`, sin necesidad de bloquear el estado principal de la UI.

**Capa Domain**

| Caso de Uso | Responsabilidad |
|---|---|
| `ObtenerCampaniasActivasUseCase` | Retorna un `Flow<List<Campania>>` filtrado por `estaActiva == true`. Room lo re-emite automáticamente ante cualquier cambio en la tabla. |
| `ObtenerCampaniasInactivasUseCase` | Ídem para `estaActiva == false`. Representa el historial completo de campañas finalizadas. |
| `EliminarCampaniaUseCase` | Elimina permanentemente una campaña de la base de datos. Retorna un `Flow<Resource<Unit>>` para informar el resultado (`Loading`, `Success`, `Error`) a la capa Presentation. La eliminación tiene efecto cascada: Room elimina automáticamente todas las entidades asociadas (tareas, insumos, cosechas, observaciones) gracias a la restricción `CASCADE` definida en las entidades. |

#### Interacciones de Clean Architecture — Detalle (`DetalleCampaniaScreen`)

**Capa Presentation**

`CampaniaDetailViewModel` implementa un patrón de **estado unificado** con `CampaniaDetailState`:

```kotlin
data class CampaniaDetailState(
    val campania: Campania? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val finishSuccess: Boolean = false,
    val idAnterior: Int? = null,
    val idSiguiente: Int? = null
)
```

El ID de la campaña a mostrar se extrae de `SavedStateHandle`, garantizando que sobreviva a la recreación del proceso de la app (rotación de pantalla, memoria baja). El ViewModel calcula los IDs de navegación anterior/siguiente (`idAnterior`, `idSiguiente`) comparando la campaña actual contra el listado completo de campañas, habilitando botones de navegación directa en la TopAppBar.

La pantalla usa un `LazyVerticalGrid` de 2 columnas donde cada celda es una `ModuloCardBase` —componente reutilizable—  que muestra un contador en tiempo real de los ítems de ese sub-módulo. Cada tarjeta instancia su propio ViewModel (con una clave `hiltViewModel(key = "card_<modulo>_$campaniaId")`) para evitar colisiones entre campañas distintas, y lanza un `LaunchedEffect` para sincronizar el ID de campaña correcto al momento de la composición.

**Capa Domain**

| Caso de Uso | Responsabilidad |
|---|---|
| `ObtenerCampaniaPorIdUseCase` | Retorna un `Flow<Campania?>` para un ID específico. El ViewModel lo recolecta en una corrutina del `viewModelScope`. Si la campaña no existe, emite `null` y el ViewModel actualiza el estado de error. |
| `FinalizarCampaniaUseCase` | Implementa un **soft-delete**: en lugar de eliminar el registro, realiza una copia de la campaña con `estaActiva = false` y la actualiza en el repositorio. Esto preserva todo el historial vinculado (cosechas, insumos) en la base de datos. El flujo emite `Resource.Loading` → `Resource.Success` y la UI reacciona navegando hacia atrás cuando `finishSuccess` es `true`. |

#### Interacciones de Clean Architecture — Formulario (`FormularioCampaniaScreen`)

**Capa Presentation**

`CampaniaFormViewModel` es un ViewModel de doble modo: **creación** y **edición**. El modo se detecta automáticamente en el `init`: si `SavedStateHandle` contiene un `campaniaId` válido, carga los datos existentes y activa `isEditMode = true`. La pantalla adapta su título y el texto del botón de acuerdo a este flag.

El formulario implementa validación **en tiempo real** mediante `ValidarDatosCampaniaUseCase`: cada campo limpia su propio error cuando el usuario comienza a escribir (`errorNombre = null`, etc.) y el ViewModel ejecuta la validación completa antes de intentar persistir.

Incluye una funcionalidad especial de **creación rápida de cultivo**: desde el mismo formulario de campaña, el usuario puede crear un cultivo nuevo mediante un `AlertDialog` sin salir de la pantalla. El método `crearYSeleccionarNuevoCultivo()` persiste el cultivo, obtiene su ID generado por Room y lo selecciona automáticamente en el formulario, todo en una sola acción del usuario.

**Capa Domain**

| Caso de Uso | Responsabilidad |
|---|---|
| `ValidarDatosCampaniaUseCase` | Centraliza todas las reglas de negocio de validación (nombre no vacío, hectáreas como número positivo, cultivo seleccionado, fecha no pasada en modo creación). Devuelve un objeto `ResultadoValidacion` con los errores específicos de cada campo, en lugar de lanzar excepciones. |
| `CrearCampaniaUseCase` | Persiste una nueva campaña con `estaActiva = true`. Retorna `Flow<Resource<Unit>>`. |
| `EditarCampaniaUseCase` | Actualiza una campaña existente en Room. Retorna `Flow<Resource<Unit>>`. |

---

### 7.5.4 Módulo: Gestión de Tareas

#### Descripción General

El módulo de Tareas funciona como una **agenda de actividades** asociadas a campañas. Permite programar, filtrar, confirmar y eliminar tareas. Cada tarea tiene nombre, fecha, hora, estado de completitud y un flag de notificación que dispara recordatorios del sistema operativo.

El módulo comprende dos pantallas y dos ViewModels:

| Pantalla | ViewModel | Propósito |
|---|---|---|
| `TareasScreen` | `TareaViewModel` | Listado filtrable de tareas pendientes y completadas |
| `NuevaTareaScreen` | `NuevaTareaViewModel` | Alta y edición de una tarea |

#### Interacciones de Clean Architecture — Listado (`TareasScreen`)

**Capa Presentation**

`TareaViewModel` es el ViewModel más sofisticado del proyecto. Su estado principal, `tareasUi: StateFlow<List<TareaUiModel>>`, se construye mediante una **cadena de operadores reactivos**:

```kotlin
val tareasUi = combine(_filtroCampania, _filtroFechas, campanias) { id, fechas, campaniasList ->
    Triple(id, fechas, campaniasList)
}.flatMapLatest { (id, fechas, campaniasList) ->
    obtenerTareasFiltradasUseCase(id, fechas).map { tareas ->
        tareas.map { tarea ->
            val nombreCampania = campaniasList.find { it.id == tarea.idCampania }?.nombre ?: "Sin Campaña"
            val isVencida = tarea.fecha < hoy
            TareaUiModel(tarea, isVencida, nombreCampania)
        }
    }
}.stateIn(...)
```

El operador `combine` fusiona los dos filtros activos (campaña y rango de fechas) con el listado de campañas disponibles. Al cambiar cualquiera de ellos, `flatMapLatest` cancela la suscripción al `Flow` anterior y abre una nueva con los parámetros actualizados, garantizando que nunca se procesen resultados de una búsqueda obsoleta. El resultado final es una lista de `TareaUiModel`, un modelo de UI enriquecido que incluye:

- `tarea`: el objeto de dominio.
- `isVencida`: calculado comparando `tarea.fecha` contra el inicio del día actual.
- `campaniaNombre`: nombre de la campaña asociada, resuelto localmente sin una consulta extra a la base de datos.

La pantalla renderiza dos sublistas diferenciadas: **Pendientes** (resaltando en rojo las vencidas) y **Completadas** (con texto tachado y fondo grisado). El botón "Programar Nueva Tarea" aplica un **guardián de navegación**: invoca `viewModel.onNuevaTareaClick()`, que verifica si hay una campaña seleccionada antes de emitir el evento de navegación; si no la hay, emite un mensaje de error reactivo en lugar de navegar.

El ViewModel también integra `UltimaSeleccionManager`, un singleton que recuerda la última campaña seleccionada por el usuario. Cuando `TareasScreen` se abre desde la barra de navegación inferior (sin `campaniaId` explícito), el ViewModel usa el manager como fuente de verdad; si se abre desde `DetalleCampaniaScreen` (con `campaniaId` explícito en `SavedStateHandle`), ese ID tiene precedencia.

**Capa Domain**

| Caso de Uso | Responsabilidad |
|---|---|
| `ObtenerTareasFiltradasUseCase` | Retorna un `Flow<List<Tarea>>` cuya consulta SQL varía según los filtros activos: sin filtros (todas las tareas), solo campaña, solo rango de fechas, o ambos combinados. La lógica de construcción de la consulta está aislada en esta capa, no en el DAO ni en el ViewModel. |
| `ConfirmarTareaUseCase` | Actualiza el campo `confirmar` de la tarea en Room. Si la tarea se marca como completada, invoca `TaskReminderScheduler.cancel(tareaId)` para cancelar la alarma del sistema operativo asociada, evitando notificaciones innecesarias. |
| `EliminarTareaUseCase` | Elimina permanentemente la tarea de la base de datos. |

#### Interacciones de Clean Architecture — Formulario (`NuevaTareaScreen`)

**Capa Presentation**

`NuevaTareaViewModel` opera en modo **creación** o **edición** según si recibe un `tareaId` en `SavedStateHandle`. En modo edición, carga los datos existentes usando `ObtenerTareaPorIdUseCase`. El campo `confirmar` de la tarea se **preserva** en la edición: si el usuario modifica la hora de una tarea que ya estaba completada, no se revierte accidentalmente su estado de completitud.

La pantalla ofrece:
- **Selector de campaña** (`SelectorCampania`): componente reutilizable compartido con otros formularios del proyecto.
- **DatePicker de Material3**: para seleccionar la fecha en formato de calendario nativo.
- **TimePicker de Material3**: para la hora en formato `HH:mm` con formato 24 horas. El ViewModel valida el formato con una expresión regular (`^([01]\d|2[0-3]):[0-5]\d$`) antes de persistir.
- **Checkbox de notificación**: cuando está activo, el sistema programa una alarma vía `AlarmManager` al guardar la tarea.

**Capa Domain**

| Caso de Uso | Responsabilidad |
|---|---|
| `CrearTareaUseCase` | Persiste una nueva tarea. Si `notificar == true`, programa una alarma del sistema operativo vía `TaskReminderScheduler` en el momento del guardado. |
| `EditarTareaUseCase` | Actualiza una tarea existente. Reprograma la alarma si corresponde. |

**Capa Data**

- `TareaRepository` (Room) expone `getTareasByCampania(campaniaId)` y `getAllTareas()` como `Flow<List<TareaEntity>>`, habilitando la reactividad automática ante inserciones, actualizaciones o eliminaciones.
- El mapper `toDomain()` traduce `TareaEntity` (con todos los campos de SQLite) al modelo limpio `Tarea` (dominio), separando el modelo de persistencia del modelo de negocio.
- `TaskReminderScheduler` (capa Core) abstrae la interacción con `AlarmManager` del sistema operativo. Esta clase es inyectada en los casos de uso mediante Hilt, manteniendo la capa Domain libre de dependencias de Android.

---

*[FIN_FASE_2]*


# FASE 3 — Documentación Técnica: Insumos + Cosechas + Cultivos

> **Destino en el documento principal:** `docs/documentacion_tpi_2da_entrega.md` → Sección **7.5 Descripción Técnica de Funcionalidades**
>
> **Instrucción para el equipo:** Pegar el contenido de este archivo a continuación del [FIN_FASE_2], respetando los subtítulos indicados.

---

### 7.5.5 Módulo: Gestión de Insumos

#### Descripción General

El módulo de Insumos maneja dos capas bien diferenciadas que trabajan en conjunto:

- **Catálogo de Insumos** (`CatalogoInsumosScreen`): Repositorio global de tipos de insumos disponibles en la empresa (semillas, herbicidas, combustibles, etc.), independiente de cualquier campaña.
- **Insumos Vinculados** (`InsumosScreen` + `VincularInsumoScreen`): Registro de qué insumos, en qué cantidades y a qué precio se aplicaron en una campaña específica. Un mismo insumo del catálogo puede vincularse múltiples veces a la misma campaña (por ejemplo, aplicaciones en diferentes fechas).

El módulo utiliza dos ViewModels especializados:

| Pantalla | ViewModel | Propósito |
|---|---|---|
| `CatalogoInsumosScreen` | `InsumoCatalogoViewModel` | CRUD del catálogo global de tipos de insumos |
| `InsumosScreen` + `VincularInsumoScreen` | `InsumoVinculacionViewModel` | Vinculación, edición y desvinculación de insumos por campaña |

#### Interacciones de Clean Architecture — Catálogo (`CatalogoInsumosScreen`)

**Capa Presentation**

`InsumoCatalogoViewModel` expone el catálogo completo de insumos como un `StateFlow<List<Insumo>>` alimentado reactivamente por Room. La pantalla lista cada insumo con su nombre, categoría e ícono emoji seleccionable.

La edición se realiza a través de un `AlertDialog` inline (`DialogEditarInsumo`) que incluye una cuadrícula (`LazyVerticalGrid`) para seleccionar visualmente uno de los diez íconos emoji predefinidos (🌱, 💧, 💊, ⛽, etc.). El botón "Guardar" del diálogo se activa únicamente si `validarEdicion(nombre, categoria)` retorna `true`, delegando la validación al `ValidarInsumoUseCase` de la capa Domain antes de permitir el guardado.

Los errores de operaciones (editar, eliminar) se exponen como `StateFlow<String?>` y se recolectan con un `LaunchedEffect` para mostrarse en un `SnackbarHost`, desapareciendo automáticamente sin intervención adicional del usuario.

**Capa Domain**

| Caso de Uso | Responsabilidad |
|---|---|
| `ObtenerCatalogoInsumosUseCase` | Retorna un `Flow<List<Insumo>>` con todos los insumos activos del catálogo. Room re-emite el listado ante cualquier cambio. |
| `EditarInsumoCatalogoUseCase` | Actualiza nombre, categoría e ícono de un insumo. Los registros de `CampaniaInsumo` que ya usan este insumo quedan automáticamente actualizados gracias a la relación de Room. |
| `EliminarInsumoCatalogoUseCase` | Elimina el insumo del catálogo. Los registros vinculados en campañas **no se borran** (no hay cascada), pero pasan a mostrarse como "Eliminado" en la UI mediante el campo `insumoActivo`. |
| `ValidarInsumoUseCase` | Valida que nombre y categoría no estén vacíos. Retorna un resultado booleano usado directamente por la UI para habilitar/deshabilitar el botón de guardado. |

#### Interacciones de Clean Architecture — Vinculación (`InsumosScreen` + `VincularInsumoScreen`)

**Capa Presentation**

`InsumoVinculacionViewModel` implementa el mismo patrón de `UltimaSeleccionManager` visto en Tareas y Cosechas: prioriza el `campaniaId` de `SavedStateHandle` (navegación desde Detalle de Campaña) y hace fallback al manager global cuando se accede desde la barra de navegación inferior.

El flujo reactivo principal usa `flatMapLatest` sobre `_campaniaIdSeleccionada`: cuando cambia la campaña seleccionada, cancela la suscripción anterior y abre una nueva consulta a `ObtenerInsumosVinculadosUseCase(id)`, garantizando que no queden datos cacheados de una campaña anterior en pantalla.

`InsumosScreen` implementa una vista de **acordeón agrupado** (`InsumoAgrupado`): los registros de `CampaniaInsumo` se agrupan por `idInsumo` usando `groupBy` sobre la lista reactiva. Cada grupo muestra:
- Totales acumulados (cantidad y costo) de todas las aplicaciones de ese insumo.
- Un `Badge` con la cantidad de aplicaciones si hay más de una.
- Expansión animada (`AnimatedVisibility` con `expandVertically`/`shrinkVertically`) para ver y operar sobre cada aplicación individual.

La edición de un registro vinculado se realiza mediante `DialogEditarCampaniaInsumo`, un diálogo inline que permite modificar cantidad y precio sin salir de la pantalla.

`VincularInsumoScreen` implementa una **búsqueda en tiempo real** sobre el catálogo: el campo de texto filtra la lista localmente (`catalogo.filter { it.nombre.contains(busqueda, ignoreCase = true) }`) sin necesidad de consultas adicionales a la base de datos. Si el insumo buscado no existe, la pantalla ofrece un botón directo para crear uno nuevo en el catálogo. El botón "Vincular a Campaña" se activa solo cuando hay campaña válida, insumo seleccionado y cantidad ingresada.

**Capa Domain**

| Caso de Uso | Responsabilidad |
|---|---|
| `ObtenerInsumosVinculadosUseCase` | Retorna `Flow<List<CampaniaInsumo>>` con todos los registros de aplicación de insumos para la campaña indicada, enriquecidos con el nombre e ícono del insumo correspondiente. |
| `AsignarInsumoACampaniaUseCase` | Crea un nuevo registro en la tabla `CampaniaInsumo` (relación campaña ↔ insumo) con cantidad, precio y timestamp de aplicación automático. |
| `EditarCampaniaInsumoUseCase` | Actualiza cantidad y precio de un registro de vinculación específico sin alterar los demás registros del mismo insumo. |
| `DesvincularInsumoUseCase` | Elimina un registro individual de `CampaniaInsumo`. No afecta al insumo en el catálogo global. |

**Capa Data**

- `CampaniaInsumo` es un modelo de dominio enriquecido que combina datos de la tabla `campania_insumo` (cantidad, precio, fechaAplicación) con datos del catálogo (nombreInsumo, iconoInsumo, insumoActivo). Esta unión se realiza mediante un `JOIN` en el DAO de Room, evitando múltiples consultas separadas.
- La desvinculación no tiene efecto cascada sobre el catálogo: si se elimina el insumo del catálogo después, los registros históricos de `CampaniaInsumo` quedan con `insumoActivo = false`, permitiendo que la UI muestre el nombre histórico con la etiqueta "(Eliminado)" para preservar la trazabilidad del gasto.

---

### 7.5.6 Módulo: Gestión de Cosechas

#### Descripción General

El módulo de Cosechas registra la producción obtenida en cada campaña. Maneja una distinción de negocio fundamental: una cosecha puede ser **almacenada** (queda en un silo o silobolsa del establecimiento) o **no almacenada** (se vende, se destina a alimento vacuno u otro uso). Esta distinción determina qué datos adicionales se guardan y cómo se calcula el balance en el Dashboard.

| Pantalla | ViewModel | Propósito |
|---|---|---|
| `CosechasScreen` | `CosechaViewModel` | Listado de cosechas por campaña, separadas por tipo |
| `FormularioCosechaScreen` | `FormularioCosechaViewModel` | Alta y edición de una cosecha (almacenada o con venta) |

#### Interacciones de Clean Architecture — Listado (`CosechasScreen`)

**Capa Presentation**

`CosechaViewModel` expone dos flujos reactivos derivados del mismo `_campaniaIdSeleccionada`:

```kotlin
val cosechas: StateFlow<List<Cosecha>>  // Todas las cosechas de la campaña
val noAlmacenadasDetalle: StateFlow<Map<Int, CosechaNoAlmacenada>>  // Detalles de ventas
```

El segundo flujo (`noAlmacenadasDetalle`) provee un `Map<cosechaId, CosechaNoAlmacenada>` que la pantalla usa para enriquecer las tarjetas de cosechas no almacenadas sin hacer consultas adicionales: `val detalle = noAlmacenadasDetalle[cosecha.id]`.

La eliminación implementa un **patrón de confirmación en dos pasos** mediante estado reactivo:
1. `solicitarEliminacion(cosecha)` → emite la cosecha en `cosechaAEliminar` y la pantalla muestra un `AlertDialog`.
2. `confirmarEliminacion()` → ejecuta la eliminación real y limpia `cosechaAEliminar`.
3. `cancelarEliminacion()` → limpia el estado sin eliminar nada.

Esto mantiene la lógica de confirmación completamente en el ViewModel, sin estado local en la UI.

**Capa Domain**

| Caso de Uso | Responsabilidad |
|---|---|
| `ObtenerCosechasPorCampaniaUseCase` | Retorna `Flow<List<Cosecha>>` filtrado por campaña. Reactivo ante nuevas inserciones. |
| `ObtenerCosechasNoAlmacenadasUseCase` | Retorna `Flow<Map<Int, CosechaNoAlmacenada>>` con los detalles de venta indexados por `cosechaId`. |
| `EliminarCosechaUseCase` | Elimina la cosecha. Gracias a la restricción `CASCADE` de Room, el registro en `CosechaNoAlmacenada` se elimina automáticamente si existía. |

#### Interacciones de Clean Architecture — Formulario (`FormularioCosechaScreen`)

**Capa Presentation**

`FormularioCosechaViewModel` es el ViewModel de mayor complejidad de persistencia del proyecto porque debe coordinar **dos tablas** de Room en una sola operación:
- `Cosecha`: tabla principal con cantidad, fecha y nombre de almacén.
- `CosechaNoAlmacenada`: tabla secundaria con tipo de destino y precio de venta (solo cuando `almacenado == false`).

El formulario adapta su interfaz dinámicamente según el `Checkbox` de almacenamiento:
- `almacenado = true` → muestra campo "Almacén (Silo, Silobolsa)".
- `almacenado = false` → muestra campos "Tipo (Venta, Alimento Vacuno, Reserva)" y "Precio Total de Venta".

En modo **edición**, el ViewModel carga simultáneamente los datos de ambas tablas:

```kotlin
val cosecha = obtenerCosechaPorIdUseCase(id)
val detalle = if (!esAlmacenada) cosechaNoAlmacenadaRepository.getPorCosechaId(cosecha.id) else null
```

El campo de cantidad acepta tanto punto (`.`) como coma (`,`) como separador decimal, normalizando el input en `onCantidadChange`: `value.replace(",", ".")`. Lo mismo aplica al campo precio. Esto garantiza compatibilidad con el teclado numérico argentino.

**Capa Domain**

| Caso de Uso | Responsabilidad |
|---|---|
| `ValidarDatosCosechaUseCase` | Centraliza las reglas de negocio: cantidad positiva, fecha no futura, almacén requerido si `isAlmacenada`. Retorna `ValidationResult.Success` o `ValidationResult.Error(message)`. |
| `RegistrarCosechaUseCase` | Inserta una cosecha almacenada (solo tabla `Cosecha`). |
| `RegistrarCosechaConVentaUseCase` | Inserta en tabla `Cosecha` y luego en `CosechaNoAlmacenada` con los datos de venta, en una transacción Room. |
| `EditarCosechaConVentaUseCase` | Actualiza la tabla `Cosecha` y el registro relacionado en `CosechaNoAlmacenada`. Si el tipo cambió de almacenada a venta o viceversa, crea o elimina el registro secundario según corresponda. |

---

### 7.5.7 Módulo: Catálogo de Cultivos

#### Descripción General

El módulo de Cultivos es el más compacto del sistema. Provee un catálogo global de tipos de cultivo (soja, maíz, trigo, etc.) que se referencia desde el formulario de Campaña. A diferencia de los insumos, los cultivos son datos de referencia simples: solo tienen nombre y un ID generado por Room.

Está compuesto por una única pantalla:

| Pantalla | ViewModel | Propósito |
|---|---|---|
| `CatalogoCultivosScreen` | `CultivoCatalogoViewModel` | CRUD completo de tipos de cultivo |

#### Interacciones de Clean Architecture

**Capa Presentation**

`CultivoCatalogoViewModel` es el ViewModel más simple del proyecto. Expone el catálogo como `StateFlow<List<Cultivo>>` con `soloActivos = true` y delega todas las operaciones directamente a sus casos de uso correspondientes.

La pantalla implementa las operaciones de alta y edición mediante `AlertDialog` inline, sin pantalla dedicada. La función `validarCultivo(nombre: String): Boolean` es invocada directamente desde el botón "Guardar" del diálogo para mantenerlo deshabilitado mientras el campo está vacío, sin necesidad de estado adicional en el ViewModel.

Los errores de operaciones (por ejemplo, intentar eliminar un cultivo referenciado en una campaña activa) se capturan con un bloque `try-catch` en el ViewModel y se exponen como `StateFlow<String?>`, recolectándolos en la pantalla con un `LaunchedEffect` que los muestra en `SnackbarHost` y luego los limpia con `viewModel.limpiarError()`.

El botón "Agregar Nuevo Cultivo" se posiciona en la parte **inferior fija** de la pantalla (fuera del `LazyColumn`), garantizando acceso inmediato al usuario independientemente del scroll.

**Capa Domain**

| Caso de Uso | Responsabilidad |
|---|---|
| `ObtenerCultivosUseCase` | Retorna `Flow<List<Cultivo>>` filtrable por `soloActivos`. En el catálogo y en el selector de campañas siempre se usa `soloActivos = true`. |
| `CrearCultivoUseCase` | Inserta un nuevo cultivo en Room y retorna el `Long` del ID generado, permitiendo seleccionarlo inmediatamente después de crearlo (usado también en `CampaniaFormViewModel.crearYSeleccionarNuevoCultivo()`). |
| `EditarCultivoUseCase` | Actualiza el nombre del cultivo. Las campañas que lo referencian no requieren actualización ya que Room une por ID, no por nombre. |
| `EliminarCultivoUseCase` | Elimina el cultivo. Room rechazará la operación (excepción `SQLiteConstraintException`) si hay campañas activas que lo referencian, lo que el ViewModel captura y re-emite como mensaje de error amigable. |

**Capa Data**

- `CultivoDao` expone todas las operaciones con funciones `suspend` para inserciones/actualizaciones/eliminaciones, y `Flow<List<CultivoEntity>>` para la consulta reactiva.
- El modelo de dominio `Cultivo` es idéntico en estructura a `CultivoEntity` (solo `id` y `nombre`), pero el mapper `toDomain()` lo mantiene separado para respetar los límites de capa: si en el futuro se agregan metadatos a la entidad (por ejemplo, fecha de creación), el modelo de dominio no se modifica hasta que la lógica de negocio lo requiera.

---

*[FIN_FASE_3]*


# FASE 4 — Documentación Técnica: Observaciones + Reportes + Configuración y Backup

> **Destino en el documento principal:** `docs/documentacion_tpi_2da_entrega.md` → Sección **7.5 Descripción Técnica de Funcionalidades**
>
> **Instrucción para el equipo:** Pegar el contenido de este archivo a continuación del [FIN_FASE_3], respetando los subtítulos indicados.

---

### 7.5.8 Módulo: Observaciones

#### Descripción General

El módulo de Observaciones permite registrar notas de campo asociadas a campañas, con soporte opcional de imágenes fotográficas tomadas desde la cámara del dispositivo o seleccionadas desde la galería. Es el módulo con mayor interacción con el sistema operativo de Android debido a la gestión de permisos de cámara, URIs de contenido y almacenamiento persistente de archivos.

La pantalla `ObservacionesScreen` integra dos ViewModels simultáneamente:

| ViewModel | Propósito |
|---|---|
| `ObservacionViewModel` | Listado reactivo de observaciones, edición y eliminación |
| `FormularioObservacionViewModel` | Estado del formulario de alta (texto + imagen), guardado |

Esta separación de responsabilidades dentro de una misma pantalla evita que el estado del formulario de alta se reinicie al recargar el listado, y permite que cada ViewModel sea testeado de forma independiente.

#### Interacciones de Clean Architecture — Presentation

**Gestión de permisos de cámara**

La pantalla implementa un flujo completo de tres estados para el permiso `CAMERA` a través de `recordarPermisoCamara()`, una función `@Composable` de la capa de utilidades que encapsula el launcher de permisos:

1. **Permiso ya concedido:** el launcher de cámara (`ActivityResultContracts.TakePicture`) se lanza directamente.
2. **Permiso denegado una vez:** se muestra `DialogoRazonPermisoCamara` explicando por qué se necesita el permiso antes de volver a solicitarlo.
3. **Permiso denegado permanentemente:** se muestra un `Snackbar` con acción "Abrir Ajustes" que invoca `abrirAjustesPermiso(context)`, llevando al usuario a la pantalla de configuración de la app en el sistema operativo.

Para la sincronización entre el resultado del permiso y la apertura de la cámara, se utiliza un patrón de **acción diferida**: cuando el usuario pulsa "Cámara" sin permiso concedido, la acción de lanzar la cámara se almacena en `accionPendiente` (estado local). Un `LaunchedEffect` observa `controlPermiso.permisoConcedido` y, cuando cambia a `true`, ejecuta y limpia la acción pendiente.

```kotlin
var accionPendiente by remember { mutableStateOf<(() -> Unit)?>(null) }

// Al pulsar Cámara sin permiso:
accionPendiente = { cameraLauncher.launch(uri) }
controlPermiso.solicitar()

// Cuando el permiso es concedido:
LaunchedEffect(controlPermiso.permisoConcedido) {
    if (controlPermiso.permisoConcedido) {
        accionPendiente?.invoke()
        accionPendiente = null
    }
}
```

**Persistencia de imágenes**

La galería devuelve URIs de contenido (`content://`) que son válidas solo mientras la app tiene acceso temporal. `FormularioObservacionViewModel.guardar()` invoca `copyImageToInternalStorage(uri)` **antes** de persistir la observación: copia el archivo a `context.filesDir` con un nombre único basado en el timestamp (`obs_<timestamp>.jpg`) y guarda la ruta absoluta interna. Esta ruta es permanente y sobrevive reinicios de la app, a diferencia del URI de contenido original.

**Interacción entre ViewModels en pantalla**

`ObservacionViewModel` (listado) y `FormularioObservacionViewModel` (formulario) comparten la pantalla pero son independientes. El botón "Guardar observación" invoca `formViewModel.guardar()` y un `LaunchedEffect` sobre `formState.guardadoExitoso` resetea el formulario al completarse (`formViewModel.resetGuardadoExitoso()`), sin afectar al listado que se actualiza automáticamente via Room.

#### Interacciones de Clean Architecture — Domain

| Caso de Uso | Responsabilidad |
|---|---|
| `ObtenerObservacionesPorCampaniaUseCase` | Retorna `Flow<List<Observacion>>` para la campaña seleccionada, reactivo ante cambios. |
| `GuardarObservacionUseCase` | Inserta una nueva observación con texto y ruta de imagen (ya copiada al almacenamiento interno). |
| `EditarObservacionUseCase` | Actualiza texto y/o imagen de una observación existente. Retorna `Flow<Resource<Unit>>`. |
| `EliminarObservacionUseCase` | Elimina la observación de Room. La imagen almacenada en `filesDir` **no se elimina automáticamente** (limpieza diferida). |
| `ValidarObservacionUseCase` | Verifica que al menos uno de los dos campos (texto o imagen) esté presente antes de habilitar el guardado. |

---

### 7.5.9 Módulo: Reportes y Análisis

#### Descripción General

`ReportesRendimientoScreen` es la pantalla analítica central del sistema. Está compuesta por tres secciones diferenciadas, todas gestionadas por un único `ReportesViewModel`:

| Sección | Descripción |
|---|---|
| **Resumen Productivo-Financiero** | Filtros multi-campaña y por rango de fechas, con métricas agregadas (capital invertido, toneladas cosechadas, costo por tonelada) y evolución histórica por tipo de cultivo mediante gráfico de línea |
| **Estadísticas de Campaña** | Análisis individual de una campaña seleccionada: costo de insumos, cosecha total, rendimiento (Tn/Ha), costo/Ha, Top 3 insumos de mayor gasto con barra de progreso, y dos gráficos de torta (distribución de gastos por insumo y distribución de destino de cosecha) |
| **Comparador de Campañas** | Comparación lado a lado de dos campañas seleccionadas independientemente (Campaña A vs Campaña B) en costo total de insumos, volumen cosechado y costo/Ha |

#### Interacciones de Clean Architecture — Presentation

**ReportesViewModel** es el ViewModel con mayor cantidad de `StateFlow` del proyecto (más de 20). Su arquitectura interna sigue el principio de **derivación reactiva en cadena**: los datos base (campañas, insumos, cosechas) se obtienen como `Flow` de Room, y las métricas derivadas se calculan mediante `combine` y `map` sobre esos flows, sin cálculos imperativos en funciones separadas.

Ejemplo representativo — cálculo de rendimiento Tn/Ha:
```kotlin
val rendimientoTnHa: StateFlow<String> = combine(
    campaniaIndividual,
    cosechasIndividual
) { campania, cosechas ->
    if (campania == null || campania.hectareas <= 0) return@combine "N/A"
    val totalCosechado = cosechas.sumOf { it.cantidad }
    val rendimiento = totalCosechado / campania.hectareas
    String.format(Locale("es", "AR"), "%.2f Tn/Ha", rendimiento)
}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "N/A")
```

**Gráficos de torta (PieChart)**

Se utilizan dos instancias del componente `PieChart` de la biblioteca `co.yml.charts`:
- **PieChart de insumos:** cada slice representa un tipo de insumo, con su costo total como valor y un color asignado cíclicamente desde una paleta de 7 colores.
- **PieChart de cosechas:** dos slices fijos: "Almacenada" (verde) y "Vendida" (ámbar), calculados sumando toneladas por destino.

Ambos gráficos se alimentan de `StateFlow<PieChartData?>` emitidos como `null` cuando no hay datos, lo que permite que la UI muestre un placeholder en lugar de un gráfico vacío.

**Gráfico de línea de evolución histórica**

El gráfico de línea se implementa **sin biblioteca externa**, usando directamente `Canvas` de Compose con la API de dibujo de bajo nivel. Calcula los ejes, coordenadas y etiquetas (rotadas 45° para evitar solapamiento) manualmente. Esto lo hace completamente personalizable y sin dependencias adicionales.

**Leyenda con drill-down de insumos**

La leyenda del PieChart de insumos implementa un acordeón expansible (mismo patrón que `InsumosScreen`): cada ítem de la leyenda es clickeable, se expande con `AnimatedVisibility` y muestra los registros individuales de ese insumo. Desde esta vista el usuario puede editar o eliminar un registro sin salir de la pantalla de reportes, operaciones delegadas a `viewModel.editarInsumo()` y `viewModel.eliminarInsumo()`.

**Filtro multi-campaña con `FlowRow`**

El filtro de campañas utiliza `FlowRow` (API experimental de `ExperimentalLayoutApi`) para mostrar chips de filtro que se redistribuyen automáticamente en múltiples filas si no caben en una sola. La selección múltiple se maneja con `toggleFiltroCampania(campaniaId)` que agrega o quita el ID de la lista `_filtroCampaniasMulti`.

**Exportación de reportes**

El ViewModel coordina la exportación mediante `ActivityResultContracts.CreateDocument`, que abre el selector de archivos del sistema operativo y devuelve una URI donde escribir. El ViewModel llama a `ReportExporter.exportToCsv()` o `ReportExporter.exportToPdf()` (clase utilitaria en `core/utils`) con los datos actuales de la campaña seleccionada. El resultado se emite como `_exportStatus: StateFlow<String?>` y la pantalla lo muestra con `Toast`.

#### Interacciones de Clean Architecture — Domain

| Caso de Uso | Responsabilidad |
|---|---|
| `ObtenerTodosLosInsumosUtilizadosUseCase` | Retorna `Flow<List<CampaniaInsumo>>` con **todos** los registros de insumos de todas las campañas, para el resumen global. |
| `ObtenerTodasLasCosechasUseCase` | Retorna `Flow<List<Cosecha>>` con todas las cosechas de todas las campañas. |
| `CalcularCostoPorHectareaUseCase` | Recibe una `Campania` y su lista de `CampaniaInsumo`, calcula la suma de gastos y la divide por las hectáreas. Retorna `0.0` si la campaña es nula o tiene `hectareas <= 0`. |
| `ObtenerEvolucionCultivoUseCase` | Retorna `Flow<List<PuntoCultivo>>` con el rendimiento Tn/Ha histórico de cada campaña que utilizó ese tipo de cultivo, ordenado cronológicamente. |

---

### 7.5.10 Módulo: Configuración y Backup

#### Descripción General

`ConfiguracionDBScreen` centraliza las operaciones de mantenimiento de la base de datos SQLite de la aplicación. Gestiona dos funcionalidades críticas de seguridad de datos: **backup** (copia de seguridad) y **restauración** de la base de datos, con un mecanismo adicional de seed de datos de prueba disponible solo en builds de desarrollo.

La pantalla instancia dos ViewModels:

| ViewModel | Propósito |
|---|---|
| `BackupViewModel` | Exportación e importación del archivo SQLite |
| `ConfiguracionDBViewModel` | Carga de datos de prueba (seed) — solo en builds DEBUG |

#### Interacciones de Clean Architecture — Presentation

**Backup (exportación)**

El botón "Exportar Base de Datos" lanza `ActivityResultContracts.CreateDocument("application/octet-stream")` que abre el selector de archivos del sistema operativo, permitiendo al usuario elegir destino y nombre. Al confirmar, el sistema devuelve una URI y el ViewModel invoca `crearBackupUseCase(uri)`. El estado `BackupUiState` (interfaz sellada con `Idle`, `Loading`, `Success`, `Error`) controla la visibilidad del `CircularProgressIndicator` y el mensaje de resultado en `Snackbar`.

**Restauración (importación)**

La restauración implementa un **flujo de dos pasos** para prevenir la pérdida accidental de datos:
1. El launcher `ActivityResultContracts.OpenDocument()` devuelve la URI del archivo seleccionado.
2. La URI se almacena en `pendingRestoreUri` (estado local) y se muestra un `AlertDialog` de confirmación con advertencia explícita: *"Se sobrescribirán TODOS los datos actuales"*.
3. Solo si el usuario confirma, se invoca `backupViewModel.restaurarBackup(uri)`.

Tras una restauración exitosa, `restoreCompleted` cambia a `true` y el `LaunchedEffect` que lo observa **reinicia la aplicación completa** mediante un intent de relanzamiento seguido de `Runtime.getRuntime().exit(0)`. Este reinicio forzado es necesario porque Room mantiene la base de datos en caché en memoria, y sin reiniciar el proceso la app continuaría usando los datos anteriores.

**Seed de datos de prueba**

El botón "Cargar datos de prueba" solo es visible cuando `BuildConfig.DEBUG == true`, garantizando que no aparezca en producción. `ConfiguracionDBViewModel.cargarDatosPrueba()` ejecuta un conjunto predefinido de inserciones que poblan todas las entidades del esquema, permitiendo demostrar la aplicación sin datos reales.

#### Interacciones de Clean Architecture — Domain

| Caso de Uso | Responsabilidad |
|---|---|
| `CrearBackupUseCase` | Abre un stream de escritura sobre la URI proporcionada por el selector del sistema, copia el archivo `.db` de Room desde `context.getDatabasePath()` y cierra el stream. Retorna `Result<Unit>` (éxito o excepción). |
| `RestaurarBackupUseCase` | Cierra la conexión de Room a la base de datos, copia el archivo seleccionado sobre el archivo `.db` interno, y delega al ViewModel el reinicio de la aplicación. Opera en `Dispatchers.IO` para no bloquear el hilo principal durante la copia de archivos. |

**Capa Data**

- El archivo de base de datos de Room se ubica en `context.getDatabasePath("don_elio_db.db")`. El backup es una copia binaria directa del archivo SQLite, lo que garantiza compatibilidad con cualquier versión futura que mantenga la misma versión de esquema.
- No hay migración de esquema durante la restauración: si se intenta importar un backup de una versión anterior del esquema, Room lanzará `IllegalStateException` y el ViewModel lo capturará como `BackupUiState.Error`.

---

*[FIN_FASE_4]*


# FASE 5 — Documentación Técnica: Arquitectura Transversal

> **Destino en el documento principal:** `docs/documentacion_tpi_2da_entrega.md` → Sección **7.5 Descripción Técnica de Funcionalidades**
>
> **Instrucción para el equipo:** Pegar el contenido de este archivo a continuación del [FIN_FASE_4], respetando los subtítulos indicados.

---

### 7.5.11 Arquitectura General y Decisiones de Diseño Transversales

Esta sección documenta los elementos de infraestructura que atraviesan todos los módulos del sistema: la arquitectura de capas, el grafo de navegación, la inyección de dependencias, el esquema de base de datos, y los patrones de diseño reutilizados en toda la aplicación.

#### 7.5.11.1 Arquitectura de Capas (Clean Architecture + MVVM)

La aplicación implementa **Clean Architecture** organizada en tres capas con dependencias unidireccionales:

```
Presentation  →  Domain  →  Data
(UI + ViewModels)  (UseCases + Models)  (Room + Repositories)
```

**Regla fundamental:** ninguna capa interna conoce a la externa. El dominio define interfaces de repositorio (`CampaniaRepository`, `TareaRepository`, etc.) que la capa Data implementa (`CampaniaRepositoryImpl`). Hilt inyecta la implementación concreta en tiempo de compilación sin que el dominio dependa de Room directamente.

| Capa | Paquete | Responsabilidad |
|---|---|---|
| **Presentation** | `presentation/` | ViewModels, pantallas Compose, componentes UI, navegación |
| **Domain** | `domain/` | Modelos de negocio, interfaces de repositorio, casos de uso |
| **Data** | `data/` | Entidades Room, DAOs, implementaciones de repositorio, mappers |
| **Core** | `core/` | Utilidades transversales: `SessionManager`, `TaskReminderScheduler`, `ReportExporter` |

#### 7.5.11.2 Esquema de Base de Datos (Room, versión 8)

La base de datos `DonElioDatabase` está declarada en `DonElioDatabase.kt` y contiene **9 entidades**:

| Tabla SQLite | Entidad Room | Descripción |
|---|---|---|
| `campanias` | `CampaniaEntity` | Campañas agrícolas. Columnas clave: `id_campania`, `nombre`, `hectareas`, `fecha`, `esta_activa`, `id_cultivo`. |
| `tareas` | `TareaEntity` | Tareas programadas. FK → `campanias`. Columnas: `fecha` (Long, ms), `hora` (String "HH:mm"), `notificar`, `confirmar`. |
| `cosechas` | `CosechaEntity` | Registros de cosecha. FK → `campanias` con `ON DELETE CASCADE`. Columna `almacen` vacía indica cosecha no almacenada. |
| `cosechas_no_almacenadas` | `CosechaNoAlmacenadaEntity` | Detalles de venta/destino de cosechas. FK → `cosechas` con `ON DELETE CASCADE`. |
| `insumos` | `InsumoEntity` | Catálogo global de tipos de insumos. Campos: `nombre`, `categoria`, `icono` (emoji), `activo`. |
| `campania_insumo` | `CampaniaInsumoEntity` | Tabla de unión campaña ↔ insumo. FK doble con `ON DELETE CASCADE`. Columnas: `cantidad`, `precio`, `fecha_aplicacion`. |
| `observaciones` | `ObservacionEntity` | Notas de campo. FK → `campanias`. Campo `imagen_uri` guarda ruta absoluta en `filesDir`. |
| `usuarios` | `UsuarioEntity` | Usuarios locales. Contraseña almacenada como hash SHA-256. |
| `cultivos` | `CultivoEntity` | Catálogo de tipos de cultivo. Campo `activo` para soft-delete. |

**Historial de migraciones relevantes:**

La base de datos evolucionó de la versión 4 a la 8 durante el desarrollo del proyecto. Las migraciones más significativas fueron:

- **v5→v6:** Se agregó la columna `hectareas` a `campanias` para habilitar el cálculo de rendimiento Tn/Ha y costo/Ha.
- **v6→v7:** Se creó la tabla `cultivos` con 4 cultivos predefinidos (Soja, Maíz, Trigo, Girasol) y se vinculó a `campanias` mediante `id_cultivo`.
- **v7→v8 (#455):** Se eliminó el índice único compuesto `(id_campania, id_insumo)` de `campania_insumo` y se agregó `fecha_aplicacion`. Esta migración habilitó que un mismo insumo pueda vincularse múltiples veces a la misma campaña, cada registro representando una aplicación diferente.

Todas las migraciones se declaran en `DatabaseModule.kt` y se registran al construir la base de datos con `.addMigrations(...)`, garantizando que los usuarios con versiones anteriores instaladas no pierdan sus datos al actualizar la app.

#### 7.5.11.3 Inyección de Dependencias (Hilt)

La aplicación usa **Hilt** (Dagger 2 simplificado) para inyección de dependencias. Todos los módulos están instalados en `SingletonComponent`, lo que significa que sus instancias viven durante todo el ciclo de vida de la aplicación.

**Módulos de Hilt:**

| Módulo | Tipo | Provee |
|---|---|---|
| `DatabaseModule` | `@Module @object` | `DonElioDatabase` y los 9 DAOs individuales |
| `RepositoryModule` | `@Module abstract class` | Binding de interfaces de repositorio a sus implementaciones |
| `SessionModule` | `@Module @object` | `SessionManager` (DataStore Preferences) |
| `AlarmModule` | `@Module @object` | `TaskReminderScheduler` (WorkManager) |

`RepositoryModule` utiliza `@Binds` en lugar de `@Provides`. Esta diferencia es importante: `@Binds` le indica a Hilt que, cuando se pida una `CampaniaRepository` (interfaz de dominio), debe entregar una `CampaniaRepositoryImpl` (implementación de la capa Data), sin necesitar código de instanciación manual. Hilt resuelve el constructor de `CampaniaRepositoryImpl` automáticamente porque está anotado con `@Inject constructor`.

**Flujo de inyección en un ViewModel típico:**

```
Hilt → CampaniaRepositoryImpl(@Inject) 
     ← CampaniaDao (@Singleton desde DatabaseModule)
     ← DonElioDatabase (@Singleton desde DatabaseModule)
     
→ CampaniaRepository (binding desde RepositoryModule)
→ ObtenerCampaniasActivasUseCase(@Inject constructor(CampaniaRepository))
→ GestionCampaniasViewModel(@HiltViewModel @Inject constructor(ObtenerCampaniasActivasUseCase, ...))
```

#### 7.5.11.4 Sistema de Navegación (Jetpack Navigation Compose)

La navegación usa **Jetpack Navigation Compose** con rutas tipadas definidas en `NavRoutes.kt`. Todas las rutas son subclases `sealed` de `NavRoute(val route: String)`, lo que garantiza exhaustividad en tiempo de compilación.

**Patrón de rutas con parámetros opcionales:**

Las rutas que pueden recibir un `campaniaId` (o `tareaId`, `cosechaId`) usan query parameters opcionales en lugar de segmentos de ruta obligatorios:

```kotlin
// Ruta con parámetro opcional
data object Tareas : NavRoute("tareas?campaniaId={campaniaId}") {
    fun createRoute(campaniaId: Int? = null): String =
        if (campaniaId != null) "tareas?campaniaId=$campaniaId" else "tareas"
}
```

Este diseño permite que la misma pantalla se abra desde dos contextos:
- Desde la **barra de navegación inferior** (sin ID → usa `UltimaSeleccionManager`).
- Desde **Detalle de Campaña** (con ID explícito → tiene prioridad sobre el manager).

**Rutas del sistema:**

| Ruta | Pantalla | Parámetros |
|---|---|---|
| `login` | LoginScreen | — |
| `registro` | RegistroScreen | — |
| `home` | DashboardOperacionesScreen | — |
| `campanias?campaniaId={id}` | GestionCampaniasScreen | `campaniaId` (opcional) |
| `detalle_campania/{campaniaId}` | DetalleCampaniaScreen | `campaniaId` (obligatorio) |
| `formulario_campania?campaniaId={id}` | FormularioCampaniaScreen | `campaniaId` (opcional, activa modo edición) |
| `tareas?campaniaId={id}` | TareasScreen | `campaniaId` (opcional) |
| `nueva_tarea?campaniaId={id}&tareaId={id}` | NuevaTareaScreen | `campaniaId`, `tareaId` (ambos opcionales) |
| `insumos?campaniaId={id}` | InsumosScreen | `campaniaId` (opcional) |
| `vincular_insumo?campaniaId={id}` | VincularInsumoScreen | `campaniaId` (opcional) |
| `catalogo_insumos` | CatalogoInsumosScreen | — |
| `catalogo_cultivos` | CatalogoCultivosScreen | — |
| `cosechas?campaniaId={id}` | CosechasScreen | `campaniaId` (opcional) |
| `formulario_cosecha?campaniaId={id}&cosechaId={id}` | FormularioCosechaScreen | ambos opcionales |
| `observaciones?campaniaId={id}` | ObservacionesScreen | `campaniaId` (opcional) |
| `reportes` | ReportesRendimientoScreen | — |
| `configuracion_db` | ConfiguracionDBScreen | — |

La barra de navegación inferior (`BottomNavBar`) incluye las 5 rutas definidas en `NavRoute.bottomNavRoutes`: `Home`, `Campanias`, `Tareas`, `Insumos`, `Reportes`.

#### 7.5.11.5 Patrón de Estado: `Resource<T>`

Para las operaciones asíncronas que requieren comunicar estados intermedios (carga, éxito, error) desde los casos de uso hacia los ViewModels, la aplicación define una clase sellada genérica:

```kotlin
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val exception: Throwable? = null) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}
```

Los casos de uso que realizan operaciones de escritura (crear, editar, eliminar) retornan `Flow<Resource<Unit>>` emitiendo la secuencia `Loading → Success` o `Loading → Error`. Los ViewModels reaccionan actualizando su `MutableStateFlow` de estado con `when(resource)`.

La clase además provee extensiones de conveniencia: `onSuccess {}`, `onError {}`, `isSuccess()`, `isError()`, permitiendo un consumo fluido en cadena cuando el ViewModel no necesita manejar el estado de carga.

#### 7.5.11.6 Patrón de Estado Global: `UltimaSeleccionManager`

`UltimaSeleccionManager` es un singleton inyectado por Hilt (`@Singleton`) que mantiene en memoria el ID de la última campaña seleccionada por el usuario:

```kotlin
@Singleton
class UltimaSeleccionManager @Inject constructor() {
    private val _campaniaIdSeleccionada = MutableStateFlow<Int?>(null)
    val campaniaIdSeleccionada: StateFlow<Int?> = _campaniaIdSeleccionada.asStateFlow()

    fun seleccionarCampania(id: Int) {
        _campaniaIdSeleccionada.value = id
    }
}
```

Este singleton resuelve un problema de UX: cuando el usuario navega desde **Detalle de Campaña X** hacia Tareas (o Insumos, Cosechas), la pantalla destino debe mostrar directamente los datos de la Campaña X sin que el usuario tenga que volver a seleccionarla. Al mismo tiempo, si navega desde la barra de navegación inferior, la pantalla debe mostrar la última campaña usada.

La lógica de prioridad implementada en **todos** los ViewModels que lo consumen es idéntica:

```kotlin
// init del ViewModel
val idExplicito = savedStateHandle.get<Int>("campaniaId").takeIf { it != -1 }
if (idExplicito != null) {
    ultimaSeleccionManager.seleccionarCampania(idExplicito)  // notifica pero no suscribe
} else {
    viewModelScope.launch {
        ultimaSeleccionManager.campaniaIdSeleccionada.collect { id ->
            if (id != null && _campaniaIdSeleccionada.value != id) {
                _campaniaIdSeleccionada.value = id  // suscripción al manager global
            }
        }
    }
}
```

#### 7.5.11.7 Capa Data: Repositorios y Mappers

Todos los repositorios siguen la misma estructura. Tomando `CampaniaRepositoryImpl` como representativo:

```kotlin
class CampaniaRepositoryImpl @Inject constructor(
    private val campaniaDao: CampaniaDao
) : CampaniaRepository {

    override fun getCampanias(): Flow<List<Campania>> {
        return campaniaDao.getCampanias().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun updateCampania(campania: Campania) {
        campaniaDao.updateCampania(campania.toEntity())
    }
}
```

**Convención de traducción de tipos:**
- **Lecturas (Flow):** DAO retorna `Flow<List<XEntity>>` → el repositorio lo transforma con `.map { it.toDomain() }` → el caso de uso lo expone a la capa Presentation.
- **Escrituras (suspend):** el caso de uso recibe un modelo de dominio → el repositorio llama a `.toEntity()` → el DAO persiste en SQLite.

Los **mappers** viven en `data/mapper/Mappers.kt` como funciones de extensión, sin clases adicionales. Por ejemplo `CampaniaEntity.toDomain()` y `Campania.toEntity()`. Esto mantiene la lógica de traducción colocada y visible.

El caso especial de `InsumoUtilizadoRelacion.toDomain()` demuestra cómo Room resuelve JOINs: la clase `InsumoUtilizadoRelacion` combina `CampaniaInsumoEntity` (datos de la vinculación) con `InsumoEntity` (datos del catálogo) en una sola proyección de consulta. El mapper extrae ambos y construye el modelo de dominio enriquecido `CampaniaInsumo` con nombre e ícono del insumo incluidos, sin necesidad de una segunda consulta al DAO.

#### 7.5.11.8 Persistencia de Sesión: DataStore Preferences

La sesión del usuario se persiste mediante **Jetpack DataStore Preferences**, no con SharedPreferences, para garantizar acceso asíncrono seguro sin bloquear el hilo principal. `SessionManagerImpl` (provisto por `SessionModule`) expone:

- `usuarioLogueadoFlow: Flow<Int?>` — ID del usuario activo. `null` indica que no hay sesión.
- `guardarSesion(usuarioId: Int)` — graba el ID tras login exitoso.
- `cerrarSesion()` — limpia el ID del DataStore.

`LoginViewModel` lee este flow en su `init` para determinar si el usuario ya tiene sesión activa y navegar directamente al Dashboard sin mostrar la pantalla de login. Esta verificación es asíncrona y no bloquea el splash de la aplicación.

#### 7.5.11.9 Sistema de Notificaciones: `TaskReminderScheduler`

Las notificaciones de tareas se programan mediante **WorkManager**, abstrayendo la interacción con `AlarmManager` detrás de la interfaz `TaskReminderScheduler` (capa Core). Hilt inyecta `WorkManagerTaskReminderScheduler` como implementación concreta (declarado en `AlarmModule`).

La interfaz tiene dos métodos:
- `schedule(tareaId, fechaHora)` — programa un `OneTimeWorkRequest` que disparará la notificación en el momento indicado.
- `cancel(tareaId)` — cancela la alarma usando el `tareaId` como `uniqueWorkName`.

`ConfirmarTareaUseCase` llama a `taskReminderScheduler.cancel(tareaId)` cuando una tarea se marca como completada, evitando que aparezca la notificación de una tarea ya finalizada. `CrearTareaUseCase` llama a `schedule(...)` al crear una tarea con `notificar = true`.

---

*[FIN_FASE_5]*


