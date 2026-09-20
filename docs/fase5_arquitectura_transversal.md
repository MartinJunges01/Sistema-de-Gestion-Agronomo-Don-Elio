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
