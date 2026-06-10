# Skills y Plugins Disponibles

Este documento detalla las capacidades y plugins (skills) instalados en el entorno de desarrollo que pueden ser utilizados por los agentes de IA durante el trabajo en "Don Elio".

## 1. `don-elio-plugin`
- **Skill principal:** `don-elio-workflow`
- **Uso:** Reglas estrictas de desarrollo, arquitectura, Git y documentación para el proyecto Android "Don Elio".
- **Cuándo usarlo:** Debe invocarse (o leerse las convenciones) al inicio de cualquier sesión trabajando en este repositorio.
- **Skill secundaria:** `don-elio-testing`
- **Uso:** Reglas estrictas de TDD, actualización de `plan_de_pruebas.md` y creación de Unit / Instrumented Tests.
- **Cuándo usarlo:** Cada vez que se cree o modifique un Caso de Uso, ViewModel o DAO, o cuando se pida asegurar la calidad del código.

## 2. `android-cli-plugin`
- **Skill principal:** `android-cli`
- **Uso:** Orquestar tareas de desarrollo Android.
- **Cuándo usarlo:** Para la creación de proyectos, despliegue, gestión del SDK, ejecución de comandos ADB o diagnósticos del entorno mediante la herramienta de línea de comandos `android`.

## 3. `modern-web-guidance-plugin`
- **Uso:** Guías modernas para desarrollo web.
- **Cuándo usarlo:** Si en algún momento la aplicación requiere un dashboard web o servicios frontend (por defecto, no aplica a la app nativa en Compose, pero está disponible en el entorno).

## 4. `firebase`
- **Uso:** Integración con Firebase.
- **Cuándo usarlo:** Si se planea agregar Analytics, Crashlytics, o sincronización en la nube (Cloud Firestore) al proyecto.

## 5. `chrome-devtools-plugin`
- **Uso:** Herramientas de depuración web.
- **Cuándo usarlo:** Útil para analizar tráfico web o WebView en caso de integrarse.

*(Nota: El entorno también cuenta con múltiples plugins científicos orientados a genómica y biología como AlphaFold, OpenTargets, Ensembl, etc., los cuales han sido omitidos de este listado por no ser relevantes para el desarrollo de la aplicación agrícola "Don Elio").*
