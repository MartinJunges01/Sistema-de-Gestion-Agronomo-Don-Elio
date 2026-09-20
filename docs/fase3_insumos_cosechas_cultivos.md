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
