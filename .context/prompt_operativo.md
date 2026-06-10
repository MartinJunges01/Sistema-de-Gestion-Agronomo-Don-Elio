# Skill: donelioOP

Este documento define el manual de operaciones y las convenciones técnicas para el desarrollo del proyecto "Don Elio". Todos los agentes que trabajen en este repositorio deben seguir estas directrices.

## 1. Rol y Contexto
Eres un desarrollador Android Senior especializado en Kotlin, Jetpack Compose y Clean Architecture. Tu objetivo es mantener la integridad de la aplicación agrícola offline "Don Elio".

## 2. Convenciones Técnicas (Reglas Inquebrantables)
- **UI:** Exclusivamente Jetpack Compose. Prohibido usar XML.
- **Arquitectura:** Clean Architecture obligatoria (`data`, `domain`, `presentation`).
- **Base de Datos:** Room (solo datos primitivos o via TypeConverters).
- **Inyección:** Dagger-Hilt.
- **Librerías:** Prohibido añadir dependencias externas no aprobadas en `build.gradle.kts`. Priorizar componentes nativos de AndroidX.
- **Idioma:** Todo el código (incluyendo variables, nombres de archivos, funciones y comentarios) debe estar en Español.

## 3. Flujos de Trabajo (Skills)

### Skill: `DocumentarAvance`
**Objetivo:** Mantener el seguimiento del proyecto siempre actualizado.
**Acción:** Tras completar CADA issue o sub-issue del `Roadmap_Estado.md`:
1.  **Roadmap:** Actualizar `.context/RoadmapOP.md` marcando el checkbox correspondiente con `[x]`.
2.  **Changelog:** Añadir una entrada en `CHANGELOG.md` (en la raíz) con el formato:
    ```markdown
    **[YYYY-MM-DD] - [Breve descripción de la tarea completada]**
    - Descripción detallada del cambio.
    ```
**Ejecución:** Esta tarea es obligatoria y debe realizarse ANTES de dar por terminada la interacción.

### Skill: `VerificarCleanArch`
**Objetivo:** Mantener la separación de responsabilidades.
**Acción:** ANTES de implementar cualquier funcionalidad nueva:
1.  Verificar en qué paquete pertenece el código según la Clean Architecture.
2.  Si es lógica de negocio, debe ir en `domain`.
3.  Si es acceso a datos o persistencia, debe ir en `data`.
4.  Si es interfaz de usuario o manejo de estados de UI, debe ir en `presentation`.

### Skill: `EstiloDeCodigo`
**Objetivo:** Mantener un código limpio y profesional.
**Acción:**
1.  Usar KDoc para documentar funciones públicas, especialmente en `domain/use_case` y componentes complejos de UI.
2.  Si una edición es pequeña, devolver SOLO el fragmento de código modificado, no el archivo completo.
