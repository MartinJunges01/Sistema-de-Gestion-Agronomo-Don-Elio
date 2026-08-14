# Don Elio - Gestión Agrónoma

Aplicación Android para la gestión y seguimiento de campañas agrícolas utilizando Clean Architecture, Jetpack Compose y Hilt.

## Stack Tecnológico
- Jetpack Compose (UI)
- Room (Base de Datos)
- Hilt (Inyección de dependencias)
- Corrutinas & Flow (Concurrencia)
- Navigation Compose (Navegación)

## Arquitectura
Este proyecto sigue los principios de Clean Architecture, separando la lógica en tres capas:
- **Domain:** Modelos puros y reglas de negocio.
- **Data:** Implementación de persistencia y repositorios.
- **Presentation:** UI y ViewModel (Jetpack Compose).

## Instrucciones de Ejecución (Para Desarrollo)

**Requisitos Previos:**
- **IDE:** Android Studio (versión "Iguana" o superior).
- **JDK:** Java Development Kit 17.

**Pasos:**
1. Clonar este repositorio y abrir el proyecto en Android Studio.
2. Esperar a que finalice la sincronización de dependencias de Gradle.
3. Conectar un dispositivo físico (con depuración USB activada) o iniciar un Emulador.
4. Presionar "Run" (`Shift + F10`) para compilar e instalar la aplicación.
*Nota: La primera vez que se ejecute, Room autogenerará las tablas de la base de datos local SQLite.*

## Generación de Instalador (APK)
Para generar el archivo ejecutable para el usuario final:
1. En Android Studio, ir a `Build > Build Bundle(s) / APK(s) > Build APK(s)`.
2. Transferir el archivo `.apk` generado al dispositivo Android destino.
