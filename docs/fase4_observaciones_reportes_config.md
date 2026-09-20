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
