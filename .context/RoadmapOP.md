# Roadmap de Desarrollo - Estado Actual

Este archivo refleja el progreso del proyecto "Don Elio".

---

## Fase 1: Configuración Inicial y Arquitectura Base
- [x] **Issue 1: Configuración Inicial del Proyecto**
  - [x] Crear nuevo proyecto seleccionando "Empty Compose Activity".
  - [x] Configurar `build.gradle` (nivel app y nivel proyecto).
  - [x] Configurar el archivo `strings.xml` y limpiar recursos.
- [x] **Issue 2: Implementación de Dependencias Base (Gradle)**
  - [x] Añadir dependencias de Room.
  - [x] Añadir dependencias de Corrutinas.
  - [x] Añadir dependencias del inyector de dependencias (Dagger-Hilt).
  - [x] Añadir dependencias de Navigation Compose.
  - [x] Añadir dependencias de testing.
- [x] **Issue 3: Creación de Estructura de Directorios (Clean Architecture)**
  - [x] Crear paquete `data` con subpaquetes.
  - [x] Crear paquete `domain` con subpaquetes.
  - [x] Crear paquete `presentation` con subpaquetes.
  - [x] Crear paquete `di`.
  - [x] Crear paquete `core` o `common`.
- [x] **Issue 4: Inicialización de Inyección de Dependencias (Hilt)**
  - [x] Crear la clase principal `DonElioApplication`.
  - [x] Anotar la clase con `@HiltAndroidApp`.
  - [x] Registrar la clase `DonElioApplication` en `AndroidManifest.xml`.
  - [x] Crear un módulo base vacío en el paquete `di`.
  - [x] Anotar la `MainActivity` con `@AndroidEntryPoint`.

## Fase 2: Capa de Datos (Data Layer) - Modelado en Room
- [x] **Issue 1: Modelado de Entidades Principales (Room Entities)**
  - [x] Crear CampaniaEntity.
  - [x] Crear TareaEntity.
  - [x] Crear CosechaEntity.
  - [x] Crear CosechaNoAlmacenadaEntity.
  - [x] Crear InsumoEntity.
  - [x] Crear ObservacionEntity.
- [x] **Issue 2: Modelado de Relaciones Muchos a Muchos (N:M)**
  - [x] Crear CampaniaInsumoEntity.
  - [x] Configurar las claves foráneas.
- [x] **Issue 3: Implementación de Type Converters**
  - [x] Crear clase Converters.kt.
  - [x] Implementar @TypeConverter para Date.
  - [x] Implementar @TypeConverter para Time.
- [x] **Issue 4: Creación de los DAOs (Data Access Objects)**
  - [x] Crear CampaniaDao.
  - [x] Crear TareaDao.
  - [x] Crear CosechaDao.
  - [x] Crear InsumoDao.
  - [x] Crear CampaniaInsumoDao.
- [x] **Issue 5: Configuración de la Database e Inyección (Hilt)**
  - [x] Crear la clase abstracta DonElioDatabase.
  - [x] Añadir todas las Entities al decorador @Database.
  - [x] Anotar la base de datos con @TypeConverters.
  - [x] Crear un módulo en el paquete `di`.
  - [x] Crear métodos con @Provides y @Singleton.
  - [x] Crear métodos con @Provides para cada uno de los DAOs.

## Fase 3: Capa de Dominio (Domain Layer)
- [x] **Issue 1: Definición de Modelos de Dominio (Domain Models)**
  - [x] Crear las `data class` de dominio.
  - [x] Crear archivos de extensión (`Mappers.kt`).
- [x] **Issue 2: Definición de Interfaces de Repositorios**
  - [x] Crear `CampaniaRepository`.
  - [x] Crear `TareaRepository`.
  - [x] Crear `CosechaRepository` e `InsumoRepository`.
- [x] **Issue 3: Implementación de los Repositorios (Data Layer)**
  - [x] Crear `CampaniaRepositoryImpl` en la capa `data/repository`.
  - [x] Crear `TareaRepositoryImpl`, `CosechaRepositoryImpl` e `InsumoRepositoryImpl`.
  - [x] Implementar la lógica interna y mapeo.
  - [x] Configurar Hilt (`RepositoryModule.kt`).
- [x] **Issue 4: Casos de Uso (Use Cases) - Módulo de Campañas y Tareas**
  - [x] Crear `CrearCampaniaUseCase`, `EditarCampaniaUseCase`, `EliminarCampaniaUseCase` y `ObtenerCampaniasUseCase`.
  - [x] Crear `CrearTareaUseCase`, `EditarTareaUseCase`, `EliminarTareaUseCase`.
  - [x] Crear `ConfirmarTareaUseCase` (CU5.4).
  - [x] Inyectar los repositorios correspondientes.
- [x] **Issue 5: Casos de Uso (Use Cases) - Módulo de Cosechas, Insumos y Observaciones**
  - [x] Crear `RegistrarCosechaUseCase`.
  - [x] Crear `CrearInsumoCatalogoUseCase`, `EditarInsumoCatalogoUseCase` y `ObtenerCatalogoInsumosUseCase`.
  - [x] Crear `AsignarInsumoACampaniaUseCase`.
  - [x] Crear `GuardarObservacionUseCase`.
- [x] **Issue 6: (Opcional) Wrapper de Resultados y Manejo de Errores**
  - [x] Crear una clase sellada `Resource<T>` o `Result<T>`.
  - [x] Ajustar los Use Cases más complejos para que retornen un `Flow<Resource<T>>`.

## Fase 4: Interfaz de Usuario y ViewModels
- [x] **Issue 1: Configuración de Navegación y Layout Principal**
  - [x] Definir las rutas de navegación.
  - [x] Crear el NavHost principal en la MainActivity.
  - [x] Configurar un Scaffold global.
- [x] **Issue 2: Pantalla Principal (Dashboard / Home de Campañas)**
  - [x] Crear el HomeViewModel.
  - [x] Diseñar lista (LazyColumn) de campañas.
  - [x] Implementar FAB para crear nueva campaña.
- [x] **Issue 3: Formularios de ABM de Campañas (Crear / Editar)**
  - [x] Crear CampaniaFormViewModel.
  - [x] Diseñar la pantalla de formulario.
  - [x] Implementar validación de errores.
  - [x] Conectar botón "Guardar".
- [x] **Issue 4: Pantalla de "Detalle de Campaña"**
  - [x] Crear CampaniaDetailViewModel.
  - [x] Diseñar interfaz con "Tabs" (Tareas, Insumos, Cosechas, Observaciones).
  - [x] Mostrar encabezado fijo.
- [x] **Issue 5: Módulo de Tareas en Campaña (Crear, Listar y Confirmar)**
  - [x] Crear TareaViewModel con carga reactiva de tareas por campaña.
  - [x] Conexión de TareasScreen y NuevaTareaScreen a CrearTareaUseCase y ConfirmarTareaUseCase.
  - [x] Checkbox de confirmación con persistencia en BD y feedback visual (tachado).
  - [x] DatePicker/TimePicker en formulario de nueva tarea.
- [x] **Issue 6: Módulo de Insumos (Catálogo Global y Asignación)**
  - [x] Crear InsumoCatalogoViewModel e InsumoVinculacionViewModel.
  - [x] Conexión de catálogo, formulario y vinculación a Use Cases reales.
  - [x] Cálculo cantidad × precio en lista de insumos vinculados.
  - [x] Atajo visual para crear insumo si no existe en catálogo.
- [x] **Issue 7: Módulo de Cosechas (Listar y Registrar)**
  - [x] Crear CosechaViewModel con carga reactiva de cosechas por campaña.
  - [x] Conectar CosechasScreen a datos reales desde BD.
  - [x] Conectar FormularioCosechaScreen a RegistrarCosechaUseCase.
  - [x] Actualizar TabCosechas en DetalleCampaniaScreen con datos reales.
  - [x] Implementar CosechaNoAlmacenadaDao, modelo, repositorio y use case.
  - [x] Refactorizar FormularioCosechaScreen para bifurcar almacenada/no-almacenada.
  - [x] Mostrar tipo y precio en cards de venta/reserva.
- [x] **Issue 8: Módulo de Observaciones (Guardar y Listar)**
  - [x] Crear ObservacionViewModel con carga reactiva de observaciones por campaña.
  - [x] Conectar ObservacionesScreen a GuardarObservacionUseCase.
  - [x] Actualizar TabObservaciones en DetalleCampaniaScreen con datos reales.
- [x] **Issue 9: Gestión de Campañas (Listado y Navegación)**
  - [x] Refactorizar GestionParcelasScreen a GestionCampaniasScreen.
  - [x] Conectar a ObtenerCampaniasUseCase para lista real de campañas.
  - [x] Navegación al detalle con campaniaId correcto.
- [x] **Issue 10: Seed Data para testing (debug source set)**
  - [x] Configurar sourceSet debug en build.gradle.kts.
  - [x] Crear DataSeeder con datos de prueba realistas en src/debug/.
  - [x] Crear SeedModule en src/debug/ con @Provides para Hilt.
  - [x] Agregar botón "Cargar datos de prueba" en ConfiguracionDB visible solo en debug.

## Fase 5: Integración con Hardware (Cámara y Notificaciones)
- [ ] **Issue 1: Configuración de Permisos (Cámara y Almacenamiento)**
  - [ ] Añadir permisos en `AndroidManifest.xml`.
  - [ ] Implementar gestor de permisos en Compose.
  - [ ] Crear `FileProvider`.
- [x] **Issue 2: Módulo de Observaciones con Imágenes (CU8)**
  - [x] Crear `ObservacionViewModel`.
  - [x] Diseñar pantalla "Nueva Observación".
  - [x] Implementar botones de cámara/galería.
  - [x] Implementar Coil para vista previa.
  - [x] Lógica de guardado de URI.
- [x] **Issue 3: Configuración de Permisos de Notificaciones y Alarmas**
  - [x] Añadir permisos (`POST_NOTIFICATIONS`).
  - [x] Implementar solicitud de permiso en Compose al entrar al Dashboard.
- [x] **Issue 4: Sistema de Recordatorios de Tareas (CU5)**
  - [x] Crear Notification Channel.
  - [x] Implementar motor de notificaciones (WorkManager).
  - [x] Modificar `CrearTareaUseCase` y `EditarTareaUseCase` para encolar notificaciones múltiples (2 días antes y día de la tarea).
  - [x] Crear caso de uso/lógica de cancelación al completar o eliminar tarea.

## Fase 6: Análisis de Datos y Respaldos
- [x] **Issue 1: Dashboard de Reportes y Estadísticas (CU10)**
  - [x] Crear `EstadisticasViewModel` (ReportesViewModel).
  - [x] Integrar librería de gráficos (YCharts).
  - [x] Diseñar pantalla "Reportes y Estadísticas" (ReportesRendimientoScreen).
- [ ] **Issue 2: Exportación de Reportes a Archivos (CU11)**
  - [ ] Implementar `Storage Access Framework` (SAF).
  - [ ] Escribir utilidad CSV/Excel o PDF.
  - [ ] Conectar botón "Exportar Reporte".
- [ ] **Issue 3: Exportar Base de Datos (Backup) (CU13)**
  *(Ver también F8/Issue 3.4 — conectar botón "Exportar BD" en ConfiguracionDBScreen)*
  - [ ] Crear `BackupViewModel`.
  - [ ] Obtener ruta de `.db` y usar SAF para guardar.
- [ ] **Issue 4: Importar Base de Datos (Restauración) (CU12)**
  *(Ver también F8/Issue 3.5 — conectar botón "Importar BD" en ConfiguracionDBScreen)*
  - [ ] Utilizar SAF (`OpenDocument`).
  - [ ] Mostrar cuadro de advertencia.
  - [ ] Implementar lógica de sobrescritura de `.db`.

## Fase 7: Calidad, Testing y Producción
- [x] **Issue 1: Pruebas Unitarias de Lógica y Base de Datos (Testing)**
  *(Requiere F11/Issue 12 — refactor ViewModels a Use Cases — antes de testear ViewModels)*
  - [x] Redactar Plan Estratégico de Pruebas y Casos de Uso (BDD) en `docs/plan_de_pruebas.md`.
  - [x] Pruebas unitarias para Casos de Uso.
  - [x] Pruebas unitarias para ViewModels.
  - [x] Pruebas instrumentadas para DAOs.
- [ ] **Issue 2: Refinamiento de Usabilidad y UI/UX**
  - [ ] Revisar navegación.
  - [ ] Implementar Feedback Visual (Snackbar).
  - [ ] Revisar accesibilidad.
  - [ ] Implementar "Empty States".
- [ ] **Issue 3: Pruebas de Rendimiento y Entorno Offline**
  - [ ] Probar sin conexión.
  - [ ] Cargar datos masivos de prueba.
  - [ ] Optimizar listas.
- [ ] **Issue 4: Preparación para Producción y Generación de APK**
  - [ ] Habilitar R8/ProGuard.
  - [ ] Crear Keystore.
  - [ ] Actualizar versionCode/versionName.
  - [ ] Generar APK firmado.

## Fase 8: Correcciones de Bugs Críticos (L1 — Alta Prioridad)
- [x] **Issue 1: Sistema de Autenticación (Login y Registro)**
  - [x] **1.1** Crear `UsuarioDao` en `data/local/dao/` con `insert`, `getByNombre`, `getAll`
  - [x] **1.2** Exponer `usuarioDao` en `DonElioDatabase` y proveerlo en `DatabaseModule`
  - [x] **1.3** Crear `LoginUseCase` que valide nombre + contraseña contra la DB
  - [x] **1.4** Crear `RegistroUseCase` que valide y persista nuevo usuario
  - [x] **1.5** Crear `LoginViewModel` con estado `Loading/Success/Error` y navegación
  - [x] **1.6** Conectar `LoginScreen` al `LoginViewModel`
  - [x] **1.7** Conectar `RegistroScreen` al `RegistroUseCase`
  - [x] **1.8** Agregar botón "Invitado" para debug (oculto en release)
- [ ] **Issue 2: Navegación BottomNav — Rutas Inválidas**
  - [ ] **2.1** Cambiar `AgriCoreBottomNav` para usar `NavRoute.X.createRoute()` en vez de `NavRoute.X.route`
  - [ ] **2.2** Verificar que `NavRoute.Campanias.createRoute()` sin parámetros genera `"campanias"` (sin query string)
  - [ ] **2.3** Verificar que `NavRoute.Tareas.createRoute()` y `NavRoute.Insumos.createRoute()` también generan rutas limpias
  - [ ] **2.4** Confirmar que los `composable` registrados en `screens.kt` matchean correctamente las rutas generadas
- [ ] **Issue 3: Botones con onClick Vacío (Funcionalidad Faltante)**
  - [ ] **3.1** Conectar "Desvincular" a `AsignarInsumoACampaniaUseCase.desvincularInsumo()`
  - [ ] **3.2** Conectar "Editar" en catálogo a `EditarInsumoCatalogoUseCase` con navegación a `FormularioInsumo` con datos precargados
  - [ ] **3.3** Crear `EliminarInsumoCatalogoUseCase` y conectar botón "Eliminar"
  - [ ] **3.4** Implementar exportación de DB con SAF *(ver también F6/Issue 3 — unificar implementación)*
  - [ ] **3.5** Implementar importación de DB con SAF + diálogo de advertencia *(ver también F6/Issue 4 — unificar implementación)*

## Fase 9: Bugs Funcionales (L2 — Prioridad Media)
- [ ] **Issue 4: String Bug en TabTareas — $pendientes muestra objeto List**
  - [ ] **4.1** Cambiar `"$pendientes"` por `"${pendientes.size}"` en la línea 184
  - [ ] **4.2** Verificar que no haya otros casos similares en el mismo archivo
- [ ] **Issue 5: Parámetro `onGoToDetalle` No Utilizado en TareasScreen**
  - [ ] Eliminar el parámetro `onGoToDetalle` de la firma de `TareasScreen`
  - [ ] Actualizar la llamada a `TareasScreen` en `screens.kt`
- [ ] **Issue 6: Validación de Campaña Activa en Operaciones**
  - [ ] **6.1** Agregar estado `campaniaIdValido` en ViewModels de insumo, tarea, cosecha y observación
  - [ ] **6.2** Deshabilitar botones de acción cuando `campaniaIdValido = false`
  - [ ] **6.3** Agregar estado `errorPrecio` en `FormularioCosechaViewModel`
  - [ ] **6.4** Mostrar `supportingText` de error en campo precio + deshabilitar botón "Guardar"

## Fase 10: Mejoras Post-Testing (L3 — Prioridad Media)
- [ ] **Issue 7: Dashboard — Reemplazar Cards Mock por Datos Reales**
  - [ ] **7.1** Agregar query `getProximaTarea(fecha: Long)` en `TareaDao`
  - [ ] **7.2** Crear `ObtenerProximaTareaUseCase`
  - [ ] **7.3** Modificar `HomeViewModel` para exponer próxima tarea
  - [ ] **7.4** Modificar `DashboardOperacionesScreen` para mostrar card con datos reales
- [x] **Issue 8: Selector de Campaña en Pantallas de Tareas, Insumos, Cosechas y Observaciones**
  - [x] **8.1** Crear componente reutilizable `SelectorCampania` (ExposedDropdownMenu)
  - [x] **8.2** Agregar `campaniaSeleccionada` state compartido
  - [x] **8.3** Modificar `TareasScreen` + `TareaViewModel` para usar selector
  - [x] **8.4** Modificar `InsumosScreen` + `InsumoVinculacionViewModel` para usar selector
  - [x] **8.5** Modificar `CosechasScreen` + `CosechaViewModel` para usar selector
  - [x] **8.6** Modificar `ObservacionesScreen` + `ObservacionViewModel` para usar selector
- [x] **Issue 9: Calendario Funcional en Pantalla de Tareas**
  - [x] **9.1** Agregar query `getTareasPorFecha(fecha: Long)` en `TareaDao`
  - [x] **9.2** Crear `ObtenerTareasDelDiaUseCase`
  - [x] **9.3** Crear `TareasDelDiaViewModel` o extender `TareaViewModel`
  - [x] **9.4** Reemplazar strip mock por calendario funcional con datos reales
- [x] **Issue 10: Formulario Nueva Tarea — Mostrar y Permitir Cambiar Campaña**
  - [x] Mostrar nombre de campaña destino en el formulario
  - [x] Agregar dropdown para cambiar entre campañas activas
  - [x] Preseleccionar última campaña usada en la sesión
- [x] **Issue 11: Reportes — ViewModel con Datos Reales + Gráfico de Torta**
  *(Este issue refina y amplía F6/Issue 1; coordinar implementación)*
  - [x] **11.1** Crear `ReportesViewModel` que inyecte `ObtenerCampaniasUseCase`, `CosechaRepository`, `CampaniaInsumoRepository`
  - [x] **11.2** Implementar cálculos: costo total = Σ(cantidad × precio) de campania_insumo
  - [x] **11.3** Implementar agrupación por categoría de insumo para gráfico de torta
  - [x] **11.4** Reemplazar Canvas mock por datos reales en gráficos de evolución mensual
  - [x] **11.5** Conectar dropdowns de comparación a lista real de campañas

## Fase 11: Refactor Arquitectónico (L4 — Prioridad Baja)
- [x] **Issue 12: ViewModels Violan Clean Architecture — Inyectan Repositorios Directamente**
  *(Bloquea F7/Issue 1 — pruebas unitarias de ViewModels)*
  - [x] **12.1** Identificar qué Use Cases faltan y crearlos
  - [x] **12.2** Refactorizar `CampaniaFormViewModel` y `CampaniaDetailViewModel`
  - [x] **12.3** Refactorizar `TareaViewModel`
  - [x] **12.4** Refactorizar `InsumoVinculacionViewModel`
  - [x] **12.5** Refactorizar `CosechaViewModel`
  - [x] **12.6** Refactorizar `ObservacionViewModel`
- [x] **Issue 13: Use Cases Muertos — Conectar o Eliminar**
  - [x] Conectar `EditarInsumoCatalogoUseCase` al botón "Editar" en catálogo
  - [x] Conectar o eliminar `EditarTareaUseCase`
  - [x] Conectar o eliminar `EliminarTareaUseCase`

## Fase 12: Deuda Técnica y Limpieza (L5 — Prioridad Baja)
- [ ] **Issue 14: Dead Code — Componentes y Archivos No Utilizados**
  - [ ] Eliminar `components/TarjetaTarea.kt` (no usado)
  - [ ] Eliminar `components/ModuleCard.kt` (no usado)
  - [ ] Eliminar componentes no usados en `components/SharedComponent.kt`
  - [ ] Eliminar `data/local/Converters.kt` (TypeConverters no usados)
  - [ ] Eliminar `DonElioExtendedTheme` y `DonElioThemeColors` no referenciados en `theme/Theme.kt`
  - [ ] Eliminar extensiones `onSuccess()`, `isSuccess()` no usadas en `domain/model/Resource.kt`
  - [ ] Mover tests al package correcto `com.itec.donelio`
- [ ] **Issue 15: Títulos con Códigos de Caso de Uso Visibles al Usuario**
  - [ ] Limpiar "CU9" de `InsumosScreen`
  - [ ] Limpiar "CU9.5" de `FormularioInsumoScreen`
  - [ ] Limpiar "CU5.1" de `NuevaTareaScreen`
  - [ ] Limpiar "CU12/CU13" de `ConfiguracionDBScreen`
  - [ ] Limpiar "CU8" de `ObservacionesScreen`
- [ ] **Issue 16: Inconsistencias de Nombrado**
  - [ ] Renombrar `CampanaSeleccionadaCard` → `CampaniaSeleccionadaCard`
  - [ ] Renombrar `campaign` → `campania` en `CampaniaDetailViewModel`
- [ ] **Issue 17: CampanaSeleccionadaCard — Datos Dinámicos**
  - [ ] Aceptar objeto `Campania` como parámetro
  - [ ] Mostrar nombre, cultivo y fecha reales
  - [ ] Estado vacío si no hay campaña
- [ ] **Issue 18: HeaderSectionAgriCore — Nombre de Usuario Dinámico**
  *(Depende de F8/Issue 1 — Login)*
  - [ ] Mostrar "Hola, [nombre del usuario]" después del login
  - [ ] Para invitado: "Hola, Invitado"
- [ ] **Issue 19: BottomNav — Navegación sin Historial**
  - [ ] **19.1** Cambiar flags de navegación: `popUpTo(0)` en vez de `saveState/restoreState`
  - [ ] **19.2** Verificar que no se pierdan datos al cambiar de tab
- [ ] **Issue 20: Actualización Reactiva al Editar Campaña**
  - [ ] **20.1** Cambiar `CampaniaDetailViewModel` para exponer un `Flow<Campania?>` reactivo
  - [ ] **20.2** Usar `ObtenerCampaniasUseCase` + `find` en vez de recarga manual
  - [ ] **20.3** Verificar que `FormularioCampaniaScreen` notifica el cambio al volver
