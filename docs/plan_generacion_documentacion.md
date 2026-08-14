# Plan de Trabajo: Generación de Documentación Asistida por IA (Actualizado)

Este documento describe el flujo de trabajo acordado para redactar automáticamente las secciones técnicas finales requeridas para la **Entrega Completa del TPI (06/10/2026)** utilizando a la IA como asistente integrado en el entorno de desarrollo.

**Nota Importante:** Las secciones 1 a 6 (Introducción, Requerimientos, Prototipos) y las secciones 7.1 a 7.3 (Arquitectura General, Stack Tecnológico y Estructura Base) **ya están completadas** en el archivo unificado `docs/documentacion_tpi_2da_entrega.md`. 

## Objetivo
Utilizar la IA para completar las secciones **7.5 (Descripción Técnica de Funcionalidades)** y **8 (Manual de Usuario)** analizando el código final de la aplicación Android en Jetpack Compose, evitando el doble esfuerzo humano y manteniendo la documentación siempre sincronizada con la realidad del código.

## Flujo de Trabajo (Docs as Code)
1. **Desarrollo**: El equipo continúa programando funcionalidades en el repositorio sin preocuparse por la documentación en papel.
2. **Análisis a Demanda**: Días antes de la entrega, el equipo le solicita a la IA que ejecute este plan mediante el "Prompt de Ejecución" detallado al final de este archivo.
3. **Escaneo de Código**: La IA analizará los módulos específicos ('features' de UI, Domain y Data) de forma exhaustiva (ver instrucciones al final).
4. **Diagramas UML**: El equipo se encarga de los diagramas, solicitando ayuda puntual a la IA si requieren entender cómo interactúan ciertas clases o entidades.
5. **Volcado a Markdown**: La IA rellenará las secciones vacías directamente en el archivo `docs/documentacion_tpi_2da_entrega.md`.
6. **Exportación**: El equipo revisa el Markdown, agrega las capturas de pantalla reales (guiadas por la IA) y lo exporta a PDF para entregar a la cátedra.

---

## EJEMPLOS: ¿Cómo se verá la documentación generada?

### 1. Documentación Técnica (Descripción de Módulos - Sección 7.5)
> **Módulo:** Dashboard de Operaciones (`DashboardOperacionesScreen`)
> 
> **Descripción:** Actúa como la pantalla principal tras la autenticación. Muestra un resumen en tiempo real del estado de los lotes y el clima.
> 
> **Interacciones de Clean Architecture:**
> - **Presentation:** `HomeViewModel` gestiona el estado de la UI y los eventos del usuario (ej. refrescar datos).
> - **Domain:** Consume los casos de uso `ObtenerTareasDelDiaUseCase` y `ObtenerCampaniasActivasUseCase` para poblar los widgets de resumen.
> - **Data:** Los repositorios se comunican con SQLite (vía Room) devolviendo flujos reactivos (Flow/LiveData) que actualizan el Dashboard sin intervención del usuario.

### 2. Fragmento del Manual de Usuario (Sección 8.2)
> ### Pantalla Principal (Dashboard)
> Al ingresar al sistema, te encontrarás con el panel de control principal.
> 
> *[INSERTAR CAPTURA PANTALLA DASHBOARD]*
> 
> 1. **Resumen Rápido:** En la parte superior verás tarjetas con el Clima actual y la "Salud" general de los lotes.
> 2. **Tareas Próximas:** Debajo del resumen, hay una lista con las actividades que debes realizar hoy. Puedes tocar el círculo de verificación a la izquierda de cada tarea para marcarla como completada.
> 3. **Botón Flotante (+):** Ubicado en la esquina inferior derecha. Al presionarlo, se abrirá un menú para crear rápidamente una nueva Tarea o registrar una Cosecha.
> 
> **Resolución de problemas comunes:** Si la lista de tareas no carga, verifica que hayas seleccionado una Campaña Activa en el menú principal.

---

## 🤖 GUÍA DE EJECUCIÓN PARA LA IA (Prompt)

Para que la IA comience a documentar automáticamente las secciones finales, el equipo debe enviarle el siguiente mensaje (prompt):

```text
Por favor, lee el archivo 'docs/plan_generacion_documentacion.md' y ejecuta la generación de la documentación faltante basándote en el código actual del repositorio.

INSTRUCCIONES CRÍTICAS PARA LA IA:
1. **Protección del Documento:** Las secciones 1 a 6 y 7.1 a 7.4 del archivo 'docs/documentacion_tpi_2da_entrega.md' ya están redactadas de forma definitiva. **NO las modifiques ni las sobreescribas**.
2. **Tu Objetivo Técnico (Sección 7.5):** Rellena la sección "7.5 Descripción Técnica de Funcionalidades". Para ello, investiga los features dentro de 'app/src/main/java/com/itec/donelio/' (ej. Dashboard, Tareas, Insumos) y explica cómo interactúan las capas Presentation, Domain y Data de cada uno.
3. **Tu Objetivo Funcional (Sección 8):** Rellena la sección "8 Manual de Usuario", describiendo paso a paso cómo usar la app basándote en el código actual de Jetpack Compose. Deja marcadores claros como "[INSERTAR CAPTURA PANTALLA X]" para que el equipo humano sepa dónde pegar las imágenes.
4. **Manejo de Contexto Largo:** El proyecto es grande. Si sientes que te vas a quedar sin contexto, divídelo en múltiples llamadas de modificación, analizando feature por feature.
```
