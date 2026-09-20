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
