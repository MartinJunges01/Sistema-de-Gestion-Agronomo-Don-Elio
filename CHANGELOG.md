# Changelog

**[2026-05-12] - Implementación de Casos de Uso (Campañas y Tareas) - F3/Issue4**
- Creación de `CrearCampaniaUseCase`, `EditarCampaniaUseCase`, `EliminarCampaniaUseCase` y `ObtenerCampaniasUseCase`.
- Cada Use Case con `@Inject constructor` y validación de nombre no vacío.
- Creación de `CrearTareaUseCase`, `EditarTareaUseCase`, `EliminarTareaUseCase` y `ConfirmarTareaUseCase`.

**[2026-05-14] - Implementación de Resource<T> y manejo de errores en Use Cases**
- Creación de `Resource<T>` en `domain/model/` con extensiones `onSuccess`, `onError`, `isSuccess`, `isError`.
- Refactorización de 7 Use Cases para retornar `Flow<Resource<Unit>>` con emisión de Loading, Success y Error.
- Manejo de excepciones con try/catch y ejecución en `Dispatchers.IO` mediante `flowOn`.

**[2026-05-14] - Corrección de mapeo Campania, unificación de nomenclatura e implementación de Use Cases faltantes**
- Corregido mapeo bidireccional `Campania` ↔ `CampaniaEntity`: agregado `cultivo` al modelo de dominio y `estaActiva` a la entidad; eliminados hardcodeos en `Mappers.kt`.
- Renombrado `campaniaId` → `idCampania` en `TareaRepository`, `CosechaRepository` y sus implementaciones.
- Creados modelos de dominio `Observacion` y `CampaniaInsumo` para mantener la pureza de la capa domain.
- Creados `CampaniaInsumoRepository` y `ObservacionRepository` con sus implementaciones y bindings de Hilt.
- Agregados mappers para `ObservacionEntity` ↔ `Observacion` y `CampaniaInsumoEntity` ↔ `CampaniaInsumo`.
- Implementados 6 casos de uso: `RegistrarCosechaUseCase`, `CrearInsumoCatalogoUseCase`, `EditarInsumoCatalogoUseCase`, `ObtenerCatalogoInsumosUseCase`, `AsignarInsumoACampaniaUseCase`, `GuardarObservacionUseCase`.

**[2026-05-12] - Card campaña activa en Tareas/Cosechas/Observaciones + botón exportar en Reportes + diagrama de flujo**
- TareasScreen, CosechasScreen y ObservacionesScreen: añadida `CampanaSeleccionadaCard` de la campaña activa.
- ReportesRendimientoScreen: añadido botón de exportar (Excel/PDF) en TopAppBar con `DropdownMenu`.
- Creado `docs/FLOW.md` con diagrama Mermaid de navegación y tabla de cobertura de Casos de Uso.

**[2026-05-12] - Refactor de navegación global, módulo de insumos y reportes**
- BottomNav: añadido acceso directo a `Destino.Insumos`; renombrado "Agenda" → "Tareas" y "Parcelas" → "Campañas".
- Home: `CampanaSeleccionadaCard` ahora navega a `DetalleCampania`; botón + navega a `FormularioCampania`.
- InsumosScreen: reemplazado formulario inline por `ModalBottomSheet` con buscador, selector cantidad/precio y botón "Agregar al catálogo".
- FormularioInsumoScreen: simplificado a solo campos Nombre, Categoría y Unidad.
- ReportesRendimientoScreen: añadidas tarjetas de métricas comparativas (Rendimiento, Ganancias, Costos, Insumos); selector dropdown para comparar dos campañas; gráficos Canvas de evolución mensual (Costos/Insumos) con leyenda bicolor.

**[2026-05-12] - Inicialización de documentación de seguimiento**
- Creación de `CHANGELOG.md` en la raíz para el seguimiento de tareas.
- Ajuste de `donelioOP.md` para referenciar `.context/RoadmapOP.md`.

**[2026-05-11] - Avance en Fase 3 (Capa de Dominio)**
- Definición de modelos de dominio (`data class` puros).
- Implementación de `Mappers.kt`.
- Creación de interfaces de repositorios (`CampaniaRepository`, `TareaRepository`, etc.).
- Implementación base de los repositorios en la capa `data`.

**[2026-05-10] - Finalización de Fase 1 y Fase 2**
- Configuración inicial del proyecto, dependencias y estructura de Clean Architecture.
- Implementación completa de la capa de datos: Entidades Room, TypeConverters y DAOs.
- Configuración de Dagger-Hilt para inyección de dependencias.
