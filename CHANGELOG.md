# Changelog

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
