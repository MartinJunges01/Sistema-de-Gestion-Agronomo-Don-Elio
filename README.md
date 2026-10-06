# Don Elio - Sistema de Gestión Agrónoma 🌾

Aplicación Android nativa para la gestión integral de campañas agrícolas. Diseñada para productores agropecuarios, permite planificar tareas, registrar insumos, controlar cosechas y analizar la rentabilidad de cada campaña, todo de forma offline y centralizada.

> **Proyecto académico** — Tecnicatura Superior en Desarrollo de Software · ITEC El Molino · Prácticas Profesionalizantes II · 2026

---

## Stack Tecnológico

| Categoría | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Base de Datos | Room (SQLite) |
| Inyección de Dependencias | Dagger-Hilt |
| Concurrencia | Coroutines & Flow |
| Trabajos en Segundo Plano | WorkManager + AlarmManager |
| Navegación | Navigation Compose |
| Arquitectura | Clean Architecture + MVVM |

---

## 🏗️ Arquitectura y Estructura del Proyecto

El sistema adopta una arquitectura desacoplada basada en **Clean Architecture**, organizando el código en capas con dependencias unidireccionales: la UI nunca accede directamente a la base de datos, y el Dominio no conoce detalles de ninguna capa tecnológica.

```
app/src/main/java/com/itec/donelio/
│
├── core/                   # Servicios transversales y utilidades base
│   ├── alarm/              # Contratos e implementación de recordatorios de tareas
│   ├── util/               # Sembradores de datos iniciales (DataSeeder)
│   ├── utils/              # Formateadores reutilizables (FormatUtils, DateUtils) y exportadores
│   └── worker/             # Trabajos en segundo plano con WorkManager (TareaReminderWorker)
│
├── data/                   # Capa de Datos — Única capa que conoce Room y SQLite
│   ├── local/
│   │   ├── dao/            # Data Access Objects: interfaces con consultas SQL anotadas
│   │   └── entity/         # Entidades Room: representación exacta de tablas en SQLite
│   ├── mapper/             # Conversores entre Entity ↔ Modelo de dominio
│   └── repository/         # Implementaciones concretas de los contratos del dominio
│
├── di/                     # Inyección de Dependencias (módulos Hilt)
│   ├── AppModule.kt        # Conecta interfaces Repository con sus implementaciones
│   └── DatabaseModule.kt   # Provee la instancia de Room Database y los DAOs
│
├── domain/                 # Capa de Dominio — Corazón de la app, puro Kotlin sin dependencias Android
│   ├── model/              # Modelos de negocio inmutables (POJOs): Campania, Tarea, Insumo…
│   ├── repository/         # Interfaces (contratos) que definen qué operaciones de datos existen
│   ├── use_case/           # Casos de Uso atómicos: una clase = una acción de negocio específica
│   └── util/               # Clases de envoltura de estado (Resource) y utilidades de dominio
│
└── presentation/           # Capa de Presentación — Todo lo que el usuario ve e interactúa
    ├── navigation/         # Grafo de navegación y definición de rutas (AppNavigation.kt)
    ├── state/              # Clases de estado de UI (UIState)
    ├── ui/
    │   ├── components/     # Componentes visuales reutilizables (botones, tarjetas, barras)
    │   ├── screen/         # Pantallas completas de Compose, organizadas por módulo funcional
    │   └── theme/          # Configuración global: colores, tipografía, formas (Material 3)
    ├── util/               # Ayudantes de presentación y formateo visual
    └── viewmodel/          # ViewModels por módulo: conectan la UI con los Casos de Uso
```

### Flujo de Datos (Unidireccional)

```
UI (Screen) → ViewModel → UseCase → Repository (interfaz)
                                          ↓
                                   RepositoryImpl → DAO → Room DB
```

> Para una descripción técnica exhaustiva del flujo y ejemplos prácticos, ver [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

---

## 📦 Módulos Funcionales del Sistema

El sistema está organizado en **10 módulos funcionales**, cada uno con sus propias pantallas, ViewModel y Casos de Uso:

| Módulo | Carpeta | Descripción |
|--------|---------|-------------|
| 🏠 **Dashboard Operativo** | `home/` | Pantalla central con indicadores clave de la campaña activa: tareas del día, balance financiero rápido y accesos directos a los módulos principales. |
| 🌾 **Gestión de Campañas** | `campania/` | Ciclo de vida completo del año agronómico: alta, edición de datos (lote, superficie, cultivo, temporada), archivo y reactivación de campañas. |
| 🌱 **Catálogo de Cultivos** | `cultivo/` | Registro y consulta de los tipos de cultivo disponibles (soja, maíz, trigo, etc.) con sus variedades y unidades de rendimiento esperadas. |
| 📋 **Planificación de Tareas** | `tarea/` | Gestión de labores agronómicas: siembra, fumigación, fertilización. Incluye fechas de vencimiento y recordatorios automáticos vía WorkManager. |
| 📦 **Control de Insumos** | `insumo/` | Catálogo maestro de insumos (semillas, agroquímicos, fertilizantes) y registro histórico de aplicaciones por campaña con trazabilidad de fechas y costos. |
| 🚜 **Registro de Cosechas** | `cosecha/` | Rendimientos obtenidos en toneladas por lote, con diferenciación de destino: almacenaje en silo propio o venta directa con precio e ingresos brutos. |
| 📓 **Libro de Campo** | `observacion/` | Bitácora de observaciones de campo: monitoreo de plagas, malezas, condiciones climáticas y notas fenológicas durante el ciclo del cultivo. |
| 📊 **Reportes y Análisis** | `reportes/` | Panel financiero con capital invertido en insumos, ingresos brutos por ventas de cosecha, balance neto y costo por hectárea por campaña. |
| 🔐 **Autenticación** | `login/` | Registro e inicio de sesión de operarios con validación local. Gestión de sesión activa y persistencia segura de credenciales offline. |
| ⚙️ **Configuración** | `config/` | Generación y restauración de backups de la base de datos SQLite, sembrado de datos de ejemplo (DataSeeder) y preferencias del sistema. |

---

## ⚙️ Instrucciones de Ejecución (Para Desarrollo)

**Requisitos Previos:**
- **IDE:** Android Studio (versión "Iguana" o superior recomendada)
- **JDK:** Java Development Kit 17
- **Android SDK:** API 26 (Android 8.0) como mínimo · API 34 como objetivo

**Pasos:**
1. Clonar el repositorio y abrir el proyecto en Android Studio.
2. Esperar a que finalice la sincronización de dependencias de Gradle.
3. Conectar un dispositivo físico (con depuración USB activada) o iniciar un emulador (API 26+).
4. Presionar **Run** (`Shift + F10`) para compilar e instalar la aplicación.

> *La primera vez que se ejecute, Room autogenerará automáticamente las tablas de la base de datos local SQLite.*

---

## 📦 Generación del Instalador (APK)

Para generar el archivo ejecutable para el usuario final:

**Desde Android Studio:**
1. Ir a `Build > Build Bundle(s) / APK(s) > Build APK(s)`.
2. Transferir el archivo `.apk` generado al dispositivo Android destino.

**Desde la línea de comandos:**
```bash
# APK de depuración (sin firma de release)
./gradlew assembleDebug

# APK de release (requiere keystore configurado)
./gradlew assembleRelease
```

---

## 📁 Documentación Adicional

| Documento | Descripción |
|-----------|-------------|
| [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) | Arquitectura detallada con flujo de datos y ejemplos prácticos |
| [`docs/FLOW.md`](docs/FLOW.md) | Diagramas de flujo y navegación entre pantallas |
| [`docs/plan_de_pruebas.md`](docs/plan_de_pruebas.md) | Plan de pruebas funcionales con criterios Given-When-Then |
| [`docs/roadmap_desarrolladores.md`](docs/roadmap_desarrolladores.md) | Historial de iteraciones y estado del roadmap |
| [`CHANGELOG.md`](CHANGELOG.md) | Registro cronológico de todos los cambios del sistema |
