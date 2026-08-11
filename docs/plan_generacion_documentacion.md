# Plan de Trabajo: Generación de Documentación Asistida por IA

Este documento describe el flujo de trabajo acordado para redactar automáticamente la documentación requerida para la **Entrega Completa del TPI (06/10/2026)** utilizando a la IA como asistente integrado en el entorno de desarrollo.

## Objetivo
Traducir el código existente en la aplicación Android (arquitectura Clean Architecture + Jetpack Compose) a documentos formales (`manual_de_usuario.md` y `documentacion_tecnica.md`) según las rúbricas de la materia, evitando el doble esfuerzo y manteniendo la documentación siempre sincronizada con la realidad del código.

## Flujo de Trabajo (Docs as Code)
1. **Desarrollo**: El equipo continúa programando funcionalidades en el repositorio sin preocuparse por la documentación en papel.
2. **Análisis a Demanda**: Días antes de la entrega, el equipo le solicita a la IA que ejecute este plan mediante el "Prompt de Ejecución" detallado al final de este archivo.
3. **Escaneo de Código**: La IA analizará la arquitectura y los módulos de forma exhaustiva (ver instrucciones al final).
4. **Diagramas UML**: El equipo se encarga de los diagramas, solicitando ayuda puntual a la IA si requieren entender cómo interactúan ciertas clases o entidades.
5. **Volcado a Markdown**: La IA redactará o actualizará los archivos `.md` en la carpeta `docs/`.
6. **Exportación**: El equipo revisa el Markdown, agrega capturas de pantalla reales (si aplica) y lo exporta a PDF para entregar a la cátedra.

---

## EJEMPLOS: ¿Cómo se verá la documentación generada?

### 1. Documentación Técnica (Arquitectura General y Tecnologías)
> **Arquitectura del Sistema:** 
> La aplicación utiliza el patrón **Clean Architecture** (Arquitectura Limpia) dividida en tres capas principales: `presentation` (UI y ViewModels), `domain` (Modelos y Casos de Uso) y `data` (Repositorios y base de datos local).
> 
> **Tecnologías Utilizadas:**
> - **Lenguaje:** Kotlin
> - **Interfaz de Usuario:** Jetpack Compose (Declarativa)
> - **Base de Datos:** SQLite gestionado a través de Room Database.
> - **Inyección de Dependencias:** Hilt/Dagger (según aplique en el módulo `di`).
> 
> **Estructura de Carpetas:**
> - `app/src/main/java/com/itec/donelio/presentation/`: Contiene la lógica de la vista y componentes Compose.
> - `app/src/main/java/com/itec/donelio/domain/`: Reglas de negocio puras (Casos de uso como `CrearTareaUseCase`).
> - `app/src/main/java/com/itec/donelio/data/`: Implementaciones de repositorios.

### 2. Documentación Técnica (Descripción de Módulos)
> **Módulo:** Dashboard de Operaciones (`DashboardOperacionesScreen`)
> 
> **Descripción:** Actúa como la pantalla principal tras la autenticación. Muestra un resumen en tiempo real del estado de los lotes y el clima.
> 
> **Interacciones de Clean Architecture:**
> - **Presentation:** `HomeViewModel` gestiona el estado de la UI y los eventos del usuario.
> - **Domain:** Consume los casos de uso `ObtenerTareasDelDiaUseCase` y `ObtenerCampaniasActivasUseCase` para poblar los widgets de resumen.
> - **Data:** Los repositorios se comunican con SQLite (vía Room) devolviendo flujos reactivos (Flow/LiveData).

### 3. Fragmento del Manual de Usuario
> ### Pantalla Principal (Dashboard)
> Al ingresar al sistema, te encontrarás con el panel de control principal.
> 1. **Resumen Rápido:** En la parte superior verás tarjetas con el Clima actual y la "Salud" general de los lotes.
> 2. **Tareas Próximas:** Debajo del resumen, hay una lista con las actividades que debes realizar hoy (por ejemplo, "Aplicar Urea"). Puedes tocar el círculo de verificación a la izquierda de cada tarea para marcarla como completada.
> 3. **Botón (+):** Ubicado en la esquina inferior derecha. Al presionarlo, se abrirá un menú para crear rápidamente una nueva Tarea o registrar una Cosecha sin tener que navegar por los menús internos.
> 
> **Resolución de problemas comunes:** Si la lista de tareas no carga, verifica que hayas seleccionado una Campaña Activa en el menú principal.

---

## 🤖 GUÍA DE EJECUCIÓN PARA LA IA (Prompt)

Para que la IA comience a documentar automáticamente, el equipo debe enviarle el siguiente mensaje (prompt):

```text
Por favor, lee el archivo 'docs/plan_generacion_documentacion.md' y ejecuta la generación de documentación técnica y manual de usuario basándote en el código actual del repositorio.

INSTRUCCIONES CRÍTICAS PARA LA IA:
1. **Investigación Exhaustiva:** Revisa TODOS los archivos relevantes dentro de 'app/src/main/java/com/itec/donelio/'. No te quedes solo con los nombres de los archivos; lee el contenido de las clases, ViewModels, UseCases y Entities.
2. **Manejo de Contexto Largo:** El proyecto es grande. Si sientes que te vas a quedar sin contexto, divídelo en múltiples pasos o flujos de ejecución.
3. **Archivos Scratch (Memoria):** Si necesitas mantener contexto entre los distintos módulos que investigas, crea archivos temporales (scratchpad) en tu directorio de artefactos para guardar resúmenes intermedios de lo que vas leyendo.
4. **Enfoque de Salida:** Genera o actualiza 'docs/documentacion_tecnica.md' y 'docs/manual_de_usuario.md' siguiendo los ejemplos provistos en este plan.
5. **No diagramas UML:** Deja espacios marcados como "[INSERTAR DIAGRAMA UML AQUÍ]" donde corresponda; el equipo humano se encargará de ellos.
```
