# Diagrama de Flujo de Navegación

```mermaid
graph TD
    Login[Login / Registro<br/>CU0 - Acceso] --> Home[Home / Dashboard<br/>CU10 - Reportes generales]

    Home -->|BottomNav: Campañas| Campanias[Gestión de Campañas<br/>CU1 - ABM Campaña]
    Home -->|BottomNav: Tareas| Tareas[Agenda y Tareas<br/>CU5 - Gestión de Tareas]
    Home -->|BottomNav: Insumos| Insumos[Vincular Insumos<br/>CU9 - Gestión de Insumos]
    Home -->|BottomNav: Reportes| Reportes[Reportes y Análisis<br/>CU10/CU11 - Reportes y Exportación]
    Home -->|Card Campaña Activa| DetalleCampania

    Campanias -->|FAB +| FormCampania[Formulario Campaña<br/>CU1 - Crear/Editar]
    Campanias -->|Click item| DetalleCampania

    DetalleCampania[Detalle Campaña<br/>CU1-CU8] -->|Tab Tareas| Tareas
    DetalleCampania -->|Tab Insumos| Insumos
    DetalleCampania -->|Tab Cosechas| Cosechas[Gestión de Cosechas<br/>CU6/CU7 - Registrar Cosecha]
    DetalleCampania -->|Tab Observaciones| Observaciones[Observaciones<br/>CU8 - Notas y Fotos]
    DetalleCampania -->|Botón Editar| FormCampania

    Tareas -->|Checkbox| ConfTarea[Confirmar Tarea<br/>CU5.4]
    Tareas -->|Botón + / FAB| NvaTarea[Nueva Tarea<br/>CU5.1 - Crear con recordatorio]

    Cosechas -->|FAB +| FormCosecha[Formulario Cosecha<br/>CU6 - Almacenada / No Almacenada]

    Insumos -->|Catálogo| Catalogo[Catálogo de Insumos<br/>CU9 - CRUD Insumos]
    Insumos -->|ModalBottomSheet| Vincular[Vincular Insumo a Campaña<br/>CU9.4]
    Catalogo -->|FAB +| FormInsumo[Formulario Insumo<br/>CU9.5 - Nombre, Categoría, Unidad]

    Observaciones -->|Adjuntar Foto| Camara[Integración Cámara<br/>CU8.1]

    Reportes -->|Exportar| ExportarExcel[Exportar a Excel<br/>CU11]
    Reportes -->|Exportar| ExportarPDF[Exportar a PDF<br/>CU11]

    subgraph Configuracion
        ConfigDB[Configuración DB<br/>CU12/CU13 - Importar/Exportar DB]
    end

    Home -->|Header DB| ConfigDB
```

## Leyenda de Casos de Uso (CU)

| Código | Descripción | Estado |
|--------|-------------|--------|
| CU0 | Acceso y Registro de Usuario | ✅ Implementado |
| CU1 | Gestión de Campañas (ABM) | 🟡 UI lista, faltan Use Cases |
| CU5 | Gestión de Tareas | 🟡 UI lista, faltan Use Cases |
| CU5.1 | Crear Tarea con Recordatorio | 🟡 UI lista |
| CU5.4 | Confirmar Tarea | 🟡 UI lista |
| CU6 | Registrar Cosecha (Almacenada/No Almacenada) | 🟡 UI lista |
| CU7 | Cosecha No Almacenada (Venta/Reserva) | 🟡 UI lista |
| CU8 | Observaciones con Imágenes | 🟡 UI parcial, falta cámara |
| CU9 | Gestión de Insumos (Catálogo y Asignación) | 🟡 UI lista, faltan Use Cases |
| CU10 | Dashboard de Reportes y Estadísticas | 🟡 UI con datos mock |
| CU11 | Exportación de Reportes (Excel/PDF) | 🟡 UI con botones mock |
| CU12 | Importar Base de Datos (Restauración) | 🔴 Pendiente (SAF) |
| CU13 | Exportar Base de Datos (Backup) | 🔴 Pendiente (SAF) |

## Pantallas por Capa (Clean Architecture)

```
presentation/ui/screens/
├── screens.kt          ← Todas las pantallas (archivo único actual)
│   ├── LoginScreen          (CU0)
│   ├── RegistroScreen       (CU0)
│   ├── DashboardOperacionesScreen  (Home / CU10)
│   ├── GestionParcelasScreen       (Campañas / CU1)
│   ├── DetalleCampaniaScreen       (CU1-CU8)
│   ├── FormularioCampaniaScreen    (CU1)
│   ├── TareasScreen                (CU5)
│   ├── NuevaTareaScreen            (CU5.1)
│   ├── CosechasScreen              (CU6/CU7)
│   ├── FormularioCosechaScreen     (CU6)
│   ├── InsumosScreen               (CU9)
│   ├── CatalogoInsumosScreen       (CU9)
│   ├── FormularioInsumoScreen      (CU9.5)
│   ├── ReportesRendimientoScreen   (CU10/CU11)
│   ├── ObservacionesScreen         (CU8)
│   └── ConfiguracionDBScreen       (CU12/CU13)
```
