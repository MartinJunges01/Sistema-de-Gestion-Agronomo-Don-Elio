# Diagrama de Flujo de Navegación

```mermaid
graph TD
    Login[Login / Registro<br/>F8/Issue 1 - Acceso] --> Home[Home / Dashboard<br/>CU10 - Operaciones y Resumen]

    Home -->|BottomNav: Campañas| Campanias[Lista de Campañas<br/>Vista General]
    Home -->|BottomNav: Tareas| Tareas[Agenda y Tareas<br/>CU5 - Gestión de Tareas]
    Home -->|BottomNav: Insumos| Insumos[Vincular Insumos<br/>CU9 - Gestión de Insumos]
    Home -->|BottomNav: Reportes| Reportes[Reportes y Análisis<br/>CU10/CU11 - Gráficos y Exportación]
    Home -->|Card Campaña Activa| DetalleCampania

    Campanias -->|FAB +| FormCampania[Formulario Campaña<br/>CU1 - Crear/Editar]
    Campanias -->|Click item| DetalleCampania

    DetalleCampania[Detalle Campaña<br/>CU1-CU8] -->|Tab Info| InfoCampania[Resumen Campaña]
    DetalleCampania -->|Tab Tareas| Tareas
    DetalleCampania -->|Tab Insumos| Insumos
    DetalleCampania -->|Tab Cosechas| Cosechas[Gestión de Cosechas<br/>CU6/CU7]
    DetalleCampania -->|Tab Observaciones| Observaciones[Observaciones<br/>CU8]
    DetalleCampania -->|Botón Editar| FormCampania

    Tareas -->|Checkbox| ConfTarea[Confirmar Tarea<br/>CU5.4]
    Tareas -->|Botón + / FAB| NvaTarea[Nueva Tarea<br/>CU5.1 - Crear con recordatorio]

    Cosechas -->|FAB +| FormCosecha[Formulario Cosecha]
    FormCosecha -->|Almacenada| DB_Almacen[Registro Cosecha Base<br/>CU6]
    FormCosecha -->|No Almacenada| DB_Venta[Registro + Venta/Reserva<br/>CU7]

    Insumos -->|Catálogo| Catalogo[Catálogo de Insumos<br/>CU9.4 - Maestro]
    Insumos -->|ModalBottomSheet| Vincular[Vincular Insumo a Campaña<br/>CU9.1]
    
    Catalogo -->|FAB +| FormInsumo[Formulario Insumo<br/>CU9.5]
    Catalogo -->|Inline| EditDelInsumo[Editar/Eliminar Insumo<br/>CU9.6/CU9.7]

    Observaciones -->|Adjuntar Foto| Camara[Integración Cámara<br/>CU8.1]

    Reportes -->|Exportar| ExportarExcel[Exportar a Excel<br/>CU11]
    Reportes -->|Exportar| ExportarPDF[Exportar a PDF<br/>CU11]

    subgraph Configuracion
        ConfigDB[Configuración DB<br/>CU12/CU13 - Importar/Exportar DB]
        DataSeed[Data Seed<br/>Cargar Datos de Prueba Debug]
    end

    Home -->|Header DB| ConfigDB
    ConfigDB --> DataSeed
```

## Leyenda de Casos de Uso (CU)

| Código | Descripción | Estado |
|--------|-------------|--------|
| CU0 / F8.1 | Acceso y Registro de Usuario (Login) | ✅ Implementado (Fase 8) |
| CU1 a CU4 | Gestión de Campañas (ABM) | ✅ Implementado |
| CU5 | Gestión de Tareas | ✅ Implementado |
| CU5.1 a CU5.4 | Crear, Editar, Eliminar y Confirmar Tarea | ✅ Implementado (Con DatePicker y Checkbox persistente) |
| CU6 y CU7 | Registrar Cosecha (Almacenada/No Almacenada) | ✅ Implementado (ViewModel y DAO) |
| CU8 | Observaciones | ✅ Implementado (A nivel de datos e UI) |
| CU8.1 | Observaciones con Imágenes (Cámara) | ✅ Implementado (Guardado URI local) |
| CU9 | Gestión de Insumos (Catálogo y Asignación) | ✅ Implementado (Vinculación y edición inline de catálogo) |
| CU10 | Dashboard de Reportes y Estadísticas | ✅ Implementado (YCharts y datos reales) |
| CU11 | Exportación de Reportes (Excel/PDF) | 🟡 Parcial (Generación pendiente) |
| CU12 | Importar Base de Datos (Restauración) | ✅ Implementado (RestaurarBackupUseCase con SAF) |
| CU13 | Exportar Base de Datos (Backup) | ✅ Implementado (CrearBackupUseCase con SAF) |

## Arquitectura Modular (Refactor Finalizado)

La aplicación sigue los principios de Clean Architecture. Los ViewModels acceden a los repositorios a través de UseCases y la UI está modularizada.

### Estructura de Pantallas y ViewModels

| Pantalla | Archivo de UI (`screen/`) | ViewModel asociado |
|----------|---------------------------|--------------------|
| Login / Registro | `login/LoginScreen.kt`, `RegistroScreen.kt` | `LoginViewModel` |
| Dashboard | `home/DashboardOperacionesScreen.kt` | `HomeViewModel` |
| Campañas | `campania/GestionCampaniasScreen.kt` | `GestionCampaniasViewModel` |
| Detalle Campaña | `campania/DetalleCampaniaScreen.kt` | `CampaniaDetailViewModel` |
| Formulario Campaña | `campania/FormularioCampaniaScreen.kt` | `CampaniaFormViewModel` |
| Tareas | `tarea/TareasScreen.kt` | `TareaViewModel` |
| Nueva Tarea | `tarea/NuevaTareaScreen.kt` | `NuevaTareaViewModel` |
| Cosechas | `cosecha/CosechasScreen.kt` | `CosechaViewModel` |
| Formulario Cosecha | `cosecha/FormularioCosechaScreen.kt` | `FormularioCosechaViewModel` |
| Insumos (Asignación) | `insumo/InsumosScreen.kt` | `InsumoVinculacionViewModel` |
| Catálogo Insumos | `insumo/CatalogoInsumosScreen.kt` | `InsumoCatalogoViewModel` |
| Formulario Insumo | `insumo/FormularioInsumoScreen.kt` | `FormularioInsumoViewModel` |
| Observaciones | `observacion/ObservacionesScreen.kt` | `ObservacionViewModel` |
| Reportes | `reportes/ReportesRendimientoScreen.kt` | `ReportesViewModel` / `EstadisticasViewModel` |
| Configuración DB | `config/ConfiguracionDBScreen.kt` | `ConfiguracionDBViewModel` (para DataSeed) |

### Componentes extraídos (`components/`)
- `AgriCoreBottomNav.kt`: Navegación inferior (5 tabs)
- `HeaderSectionAgriCore.kt`: Header verde con fecha dinámica
- `CampaniaSeleccionadaCard.kt`: Card de campaña activa reutilizable
- `CardMetricaComparativa.kt`: Comparativa de valores
- `SelectorCampania.kt`: Dropdown reutilizable (F10/Issue 8)

*Nota: Los componentes `ModuleCard.kt` y `TarjetaTarea.kt` fueron declarados obsoletos (Dead Code - Fase 12).*

## Navegación con Parámetros

Todas las rutas que requieren contexto de campaña aceptan el parámetro `campaniaId` de manera robusta:

| Ruta | Parámetro | Tipo |
|------|-----------|------|
| `DetalleCampania` | `{campaniaId}` | Requerido (path) |
| `FormularioCampania` | Ninguno | Simplificado (Sin parámetro) |
| `Tareas` | `?campaniaId=` | Opcional (query) |
| `NuevaTarea` | `?campaniaId=` | Opcional (query) |
| `Insumos` | `?campaniaId=` | Opcional (query) |
| `Cosechas` | `?campaniaId=` | Opcional (query) |
| `Observaciones` | `?campaniaId=` | Opcional (query) |
| `Campanias` | `?campaniaId=` | Opcional (query) |

## Ramas de Flujos (Para Capturas de Pantalla)

A continuación se dividen las ramas de flujo de la aplicación. Esta estructura se utiliza en el documento de primera entrega para organizar las capturas de pantalla de cada módulo:

### Rama 1: Autenticación y Acceso
* **Login / Registro** (CU0 / F8.1)

```mermaid
graph TD
    Start((Inicio)) --> Login[Pantalla de Login]
    Login -->|Registrarse| Registro[Pantalla de Registro]
    Registro -->|Crear Cuenta| Login
    Login -->|Ingresar| Home[Home / Dashboard]
```

### Rama 2: Navegación Principal y Dashboard
* **Home / Dashboard** (CU10)

```mermaid
graph TD
    Home[Pantalla de Home] -->|Nav| Campanias[Campañas]
    Home -->|Nav| Tareas[Tareas]
    Home -->|Nav| Insumos[Insumos]
    Home -->|Nav| Reportes[Reportes]
```

### Rama 3: Gestión de Campañas
* **Campañas** (CU1 a CU4)

```mermaid
graph TD
    Campanias[Lista de Campañas] -->|FAB +| FormCampania[Formulario Crear Campaña]
    Campanias -->|Click item| DetalleCampania[Detalle de Campaña]
    DetalleCampania -->|Tab Info| Resumen[Resumen Campaña]
    DetalleCampania -->|Botón Editar| FormEditCampania[Formulario Editar Campaña]
    FormCampania -->|Guardar| Campanias
    FormEditCampania -->|Guardar| DetalleCampania
```

### Rama 4: Gestión de Tareas
* **Tareas** (CU5)

```mermaid
graph TD
    Tareas[Lista de Tareas / Agenda] -->|FAB +| FormTarea[Formulario Nueva Tarea]
    FormTarea -->|Guardar| Tareas
    Tareas -->|Checkbox| ConfirmarTarea[Confirmar Tarea]
```

### Rama 5: Gestión de Cosechas
* **Cosechas** (CU6 y CU7)

```mermaid
graph TD
    Cosechas[Lista de Cosechas] -->|FAB +| FormCosecha[Formulario Cosecha]
    FormCosecha -->|Destino: Silo| CosechaSilo[Guardar Cosecha Almacenada]
    FormCosecha -->|Destino: Venta/Reserva| CosechaVenta[Guardar Cosecha No Almacenada]
    CosechaSilo -->|Actualizar| Cosechas
    CosechaVenta -->|Actualizar| Cosechas
```

### Rama 6: Gestión de Insumos
* **Insumos** (CU9)

```mermaid
graph TD
    Insumos[Lista de Insumos Vinculados] -->|Asignar Insumo| Catalogo[Catálogo Global de Insumos]
    Catalogo -->|FAB +| FormInsumo[Formulario Nuevo Insumo]
    Catalogo -->|Click item| FormEditInsumo[Editar Insumo Existente]
    FormInsumo -->|Guardar| Catalogo
    FormEditInsumo -->|Guardar| Catalogo
    Catalogo -->|Seleccionar y Vincular| Insumos
```

### Rama 7: Observaciones
* **Observaciones** (CU8)

```mermaid
graph TD
    Observaciones[Lista y Bloc de Notas] -->|Adjuntar Imagen| OpcionesFoto[Seleccionar Cámara / Galería]
    OpcionesFoto -->|Cámara| Camara[Tomar Foto]
    OpcionesFoto -->|Galería| Galeria[Elegir Imagen]
    Camara -->|Guardar URI| Observaciones
    Galeria -->|Guardar URI| Observaciones
```

### Rama 8: Reportes y Configuración
* **Reportes y Config** (CU11, CU12, CU13)

```mermaid
graph TD
    Reportes[Pantalla de Reportes y Gráficos] -->|Exportar| ExportarPDF[Exportar a PDF]
    Reportes -->|Exportar| ExportarExcel[Exportar a Excel]
    
    ConfigDB[Pantalla de Configuración DB] -->|Backup| CrearBackup[Crear Backup SAF]
    ConfigDB -->|Restaurar| RestaurarBackup[Restaurar Backup SAF]
```
