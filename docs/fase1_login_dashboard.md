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

- `CampaniaRepository`, `TareaRepository`, `CampaniaInsumoRepository`, `CosechaRepository` y `CosechaNoAlmacenadaRepository` exponen sus datos como `Flow<List<...>>` backed por Room. Cada vez que se escribe en las tablas subyacentes, Room re-emite la lista actualizada automáticamente, propagando el cambio hasta la UI.
- `SessionManager` (DataStore) provee el nombre del usuario autenticado como un `Flow<String>`, el cual es observado directamente por el ViewModel.

#### Formateo de moneda

El Dashboard implementa una función utilitaria `formatearMoneda(valor: Double): String` que adapta el formato para la locale argentina:
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
