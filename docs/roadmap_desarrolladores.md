# Roadmap de Desarrollo - Iteración 2 (División de Equipos y Ramas)

Este documento divide los issues identificados en el roadmap (`.context/iteracion_2.md`) agrupando tareas similares y atómicas, diseñadas para ser ejecutadas en paralelo por 3 desarrolladores mediante un esquema granular de ramas de Git.

---

## 👨‍💻 Desarrollador 1: Core, Base de Datos y Módulo de Reportes
**Enfoque:** Modificaciones a la capa de datos, migraciones, lógicas complejas de UseCases y gráficos.

### 🌿 Ramas Internas (Orden de Ejecución Recomendado)

1. **`feature/db-migration-hectareas`**
   - **[Issue 17]** Agregar Campo "Hectáreas" a la Entidad Cosecha (Migración DB Destructiva).
   - *Nota:* Esta rama es bloqueante para algunas vistas de otros desarrolladores, debe ser la primera en integrarse a `develop`.

2. **`feature/reportes-limpieza-datos`**
   - **[Issue 5]** Datos Mockeados Residuales en Dashboard y Reportes.
   - Limpiar estados hardcodeados preparando el terreno para la data real.

3. **`feature/reportes-graficos-avanzados`**
   - **[Issue 18]** Reportes — Mejorar Reporte de Insumos con Selector de Campaña.
   - **[Issue 19]** Reportes — Gráfico de Desglose de Cosechas (Almacenada vs Vendida).
   - *Nota:* Depende de la lógica de negocio subyacente y repositorios de Cosecha/Insumos.

4. **`feature/reportes-comparacion`**
   - **[Issue 20]** Reportes — Implementar Comparación Real entre Campañas.
   - Implementación del selector doble y la lógica matemática para la réplica de las 3 tarjetas de comparación.

---

## 👨‍💻 Desarrollador 2: Gestión de Campañas y Tareas (Lógica de Negocio y Flujos)
**Enfoque:** Lógica de tareas programadas, filtrado de campañas y problemas de sincronización de estado en ViewModels.

### 🌿 Ramas Internas (Orden de Ejecución Recomendado)

1. **`feature/tareas-dashboard-fixes`**
   - **[Issue 1]** Tareas del Dashboard — Interacción, Filtrado y Tratamiento Visual.
   - **[Issue 2]** Tareas Nuevas No Aparecen en la Pestaña Tareas (Desincronización de Timestamps).

2. **`feature/campanias-historial`**
   - **[Issue 15]** Separar Campañas Activas e Inactivas (Historial).
   - *Nota:* Impacta los repositorios de campañas y la vista principal de gestión.

3. **`feature/campanias-navegacion`**
   - **[Issue 16]** Navegación entre Detalles de Campañas (Sin Retroceder).
   - **[Issue 11]** Pestaña Tareas en Detalle de Campaña No Se Actualiza al Cambiar de Campaña.
   - *Nota:* Estas tareas van juntas ya que el Issue 16 agravará el Issue 11 si no se corrigen simultáneamente los keys de los `hiltViewModel`.

4. **`fix/validaciones-fechas-horas`**
   - **[Issue 9]** Campañas Permiten Fechas de Inicio en el Pasado.
   - **[Issue 10]** Campo "Hora" en Nueva Tarea Acepta Cualquier Carácter.

---

## 👨‍💻 Desarrollador 3: CRUD, UI/UX, Hardware y Validaciones Globales
**Enfoque:** Formularios, eliminación y actualización de entidades secundarias, permisos de Android y pulido de UX general.

### 🌿 Ramas Internas (Orden de Ejecución Recomendado)

1. **`fix/crashes-criticos` (Hotfix / Prioridad Máxima)**
   - **[Issue 6]** Crash al Abrir la Cámara en Observaciones (Permiso de Cámara).
   - **[Issue 7]** Crash por Foreign Key al Registrar Cosecha (campaniaId = -1).

2. **`feature/insumos-crud-flow`**
   - **[Issue 8]** Catálogo de Insumos — Validación y Flujo de SQLite con `Flow`.
   - **[Issue 4]** Visualización Genérica de Insumos Vinculados tras Soft-Delete en Catálogo.

3. **`feature/observaciones-cosechas-crud`**
   - **[Issue 13]** Incorporar Edición y Eliminación de Observaciones.
   - **[Issue 14]** Incorporar Edición y Eliminación de Cosechas.
   - **[Issue 12]** Formulario de Cosechas — Validación y Mensajes de Error Faltantes.
   - *Nota:* Para los issues de cosechas (14 y 12), es preferible esperar a que el Desarrollador 1 termine la rama `feature/db-migration-hectareas`.

4. **`fix/ux-globales`**
   - **[Issue 3]** Mensaje de Saludo No Funciona Correctamente (SessionManager).
   - **[Issue 21]** Bloquear Modo Oscuro (Forzar Tema Claro).
   - **[Issue 22]** La Pantalla No Se Desplaza al Escribir (Teclado Cubre los Campos).
   - **[Issue 23]** Datos Mock del Dashboard — Clima y Salud de Lotes.

---

## 🛠️ Reglas de Colaboración
1. **Pull Requests (PR):** Cada rama interna debe convertirse en un Pull Request independiente hacia `develop`.
2. **Dependencias:** El Desarrollador 3 debe esperar a que el Desarrollador 1 haga el merge de la migración de base de datos antes de intervenir los formularios de Cosecha.
3. **Merge de UI:** El Desarrollador 3 implementará `imePadding` (Issue 22) globalmente. Cualquier conflicto de UI con los otros desarrolladores se resolverá tras este merge.
