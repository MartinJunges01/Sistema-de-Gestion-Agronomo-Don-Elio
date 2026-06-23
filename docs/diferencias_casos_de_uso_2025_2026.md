# Evolución del Sistema: Diferencias Casos de Uso (2025 vs 2026)

Este documento detalla la evolución arquitectónica y funcional del Sistema de Gestión "Don Elio", contrastando la propuesta teórica original del año 2025 con la realidad técnica y práctica implementada en 2026.

## 1. Evolución Arquitectónica
* **2025 (Propuesta Original):** Concepción de un sistema de gestión general sin una arquitectura de software estrictamente definida.
* **2026 (Realidad):** El sistema fue refactorizado y construido bajo los principios de **Clean Architecture** (Capas `data`, `domain`, `presentation`). Se adoptó **Jetpack Compose** para una UI reactiva y moderna, y **Room** como base de datos local.

## 2. Diferencias Conceptuales en los Casos de Uso (CU)

A lo largo del desarrollo, las necesidades operativas dictaron mejoras sobre los Casos de Uso originales. Las principales discrepancias y evoluciones son:

### Campañas (CU1 - CU4)
* **2025:** Se trataba la edición (CU4) y el acceso al menú de la campaña (CU2) como flujos aislados.
* **2026:** Se consolidó la lógica en un solo `EditarCampaniaUseCase`. Además, se introdujo el concepto de estado mediante el atributo `estaActiva`, lo que permite archivar campañas finalizadas y mantener el dashboard enfocado en el trabajo en curso.

### Tareas y Notificaciones (CU5)
* **2025:** La responsabilidad de las notificaciones se delegaba teóricamente a un "Actor Externo".
* **2026:** El sistema es ahora totalmente autónomo mediante la implementación de `WorkManagerTaskReminderScheduler`. Cuando una tarea se crea (`CrearTareaUseCase`) con el recordatorio activo, el sistema programa alarmas nativas. Al confirmar una tarea (`ConfirmarTareaUseCase`), el sistema cancela inteligentemente el recordatorio para evitar "alertas fantasma".

### Registro de Cosechas (CU6 y CU7)
* **2025:** El registro de cosecha a silo (CU6) y el destino no almacenado (venta/reserva animal - CU7) carecían de una separación estricta en el diseño lógico. No se contemplaba la superficie recolectada ni la unificación de métricas.
* **2026:** Se introdujeron cambios estructurales clave:
  * **Bifurcación:** `RegistrarCosechaUseCase` maneja el flujo de silo, mientras que `RegistrarCosechaConVentaUseCase` inserta la cosecha base y un registro hijo automático (`CosechaNoAlmacenadaEntity`) con ventas y precio.
  * **Hectáreas y Rendimiento:** Se añadió el campo "Hectáreas" obligatorio para posibilitar el cálculo del rendimiento real por área (Tn/ha).
  * **Unificación a Toneladas:** Se eliminó la multiplicidad de unidades de medida (Kg, gr, etc.). El sistema ahora estandariza toda la cosecha a Toneladas (Tn).

### Gestión de Insumos (CU9)
* **2025:** Los insumos eran simples registros atados directamente a la campaña donde se creaban.
* **2026:** **Un cambio arquitectónico vital.** Se introdujo el concepto de **Catálogo Global de Insumos**.
  * Ahora el usuario administra un maestro de insumos (`CrearInsumoCatalogoUseCase`).
  * En lugar de borrar insumos que ya no se usan (`EliminarInsumoCatalogoUseCase`), se implementó **Soft-Delete** (`activo = false`) para evitar corromper el historial económico de campañas anteriores.
  * Para utilizar un insumo, este se vincula desde el catálogo hacia la campaña activa (`AsignarInsumoACampaniaUseCase`).

### Observaciones y Multimedia (CU8 y CU8.1)
* **2025:** Concepto de texto simple.
* **2026:** Se materializó el soporte multimedia (`GuardarObservacionUseCase`). Ahora el sistema almacena de forma persistente URIs locales de imágenes adjuntas desde la cámara o galería, permitiendo un seguimiento visual del estado del cultivo.

## 3. Nuevos Módulos (No contemplados en 2025)

* **Autenticación (CU0 / F8.1):** Acceso local seguro (`LoginUseCase` y `RegistroUseCase`) con encriptación **hash SHA-256**.
* **Copias de Seguridad (CU12 y CU13):** Importación/Exportación usando **Storage Access Framework (SAF)**.
* **Dashboard Analítico y Reportes (CU10 / CU11):** Integración de **YCharts** para cuadros visuales interactivos. Ahora los gráficos incluyen **selectores de campaña específicos**, PieCharts de gastos por insumos y desgloses de cosecha total (Almacenada vs Vendida), reemplazando los antiguos mockups por inteligencia de negocio real.

---

## 4. Casos de Uso Actualizados (Formato 2025 aplicado a 2026)

A continuación se presenta la tabla formal de los Casos de Uso que han evolucionado o que son nuevos, utilizando la estructura solicitada en la entrega de 2025.

### CU0 / F8.1 - Autenticación y Acceso (Nuevo)

| CU0 / F8.1 | Login / Registro de Usuario | | |
| --- | --- | --- | --- |
| **Descripción** | Autenticación del usuario al iniciar la app usando un sistema de hash local (SHA-256). | | |
| **Actores** | Propietario. | | |
| **Pre condiciones** | Aplicación instalada y base de datos inicializada. | | |
| **Post condiciones** | Usuario logueado, con acceso a las funciones del Dashboard. | | |
| **Secuencia Normal** | **#** | **Acción (actor)** | **Reacción (sistema)** |
| | 1 | Usuario ingresa credenciales (o se registra) | Sistema procesa datos e invoca el `LoginUseCase` |
| | 2 | Usuario presiona "Ingresar" | Sistema compara el hash SHA-256 con el guardado en Room |
| | 3 | | Autenticación exitosa, el sistema redirige a Home |
| **Excepciones** | **#** | **Acción (actor)** | **Reacción (sistema)** |
| | 1 | Credenciales inválidas | Muestra un error "Usuario no encontrado / Credenciales inválidas" |
| **Importancia** | **Vital** | | |

### CU2 / CU4 - Edición de Campaña (Refactorizado)

| CU2 / CU4 | Edición y Estado de Campaña | | |
| --- | --- | --- | --- |
| **Descripción** | Edición de la información de la campaña. Se unificaron los casos de uso 2 y 4 y se añadió el control de estado activo/inactivo (`estaActiva`). | | |
| **Actores** | Propietario. | | |
| **Pre condiciones** | Existencia de la campaña en la base de datos. | | |
| **Post condiciones** | Campaña editada, pudiendo quedar en estado Inactivo (Archivada). | | |
| **Secuencia Normal** | **#** | **Acción (actor)** | **Reacción (sistema)** |
| | 1 | Propietario selecciona editar campaña | Muestra el formulario con los datos cargados |
| | 2 | Modifica los datos o marca como "Finalizada" | `EditarCampaniaUseCase` actualiza BD |
| | 3 | Confirma operación | Sistema notifica éxito y actualiza lista de Home |

### CU5.1 / CU5.4 - Tareas y Notificaciones (Refactorizado)

| CU5 | Tareas con Recordatorios Autónomos | | |
| --- | --- | --- | --- |
| **Descripción** | Gestión de tareas con uso de WorkManager interno en lugar de actores externos para las notificaciones. | | |
| **Actores** | Propietario, WorkManagerTaskReminderScheduler (Sistema Interno). | | |
| **Pre condiciones** | Campaña activa, permisos de notificación concedidos. | | |
| **Post condiciones** | Tarea guardada y alarma programada / cancelada si se completa. | | |
| **Secuencia Normal** | **#** | **Acción (actor)** | **Reacción (sistema)** |
| | 1 | Crea tarea con switch `notificar = true` | `CrearTareaUseCase` guarda en Room |
| | 2 | | Sistema encola alarma nativa con `WorkManager` |
| | 3 | Marca tarea como "Completada" (CU5.4) | `ConfirmarTareaUseCase` actualiza BD y cancela el scheduler |

### CU6 / CU7 - Cosechas y Ventas (Refactorizado)

| CU6 / CU7 | Registro Bifurcado de Cosechas (Hectáreas y Toneladas) | | |
| --- | --- | --- | --- |
| **Descripción** | Separación limpia de la lógica de almacenamiento vs ventas bajo Clean Architecture. Incorpora toma obligatoria de Hectáreas para cálculos de rendimiento, asumiendo Toneladas (Tn) por defecto. | | |
| **Actores** | Propietario. | | |
| **Pre condiciones** | Campaña Activa. | | |
| **Post condiciones** | Registros de cosecha en Tn mapeados a hectáreas para reportes. | | |
| **Secuencia Normal** | **#** | **Acción (actor)** | **Reacción (sistema)** |
| | 1 | Carga cantidad (Tn), Hectáreas y destino | Sistema evalúa si es silo o venta |
| | 2 | Confirma operación | Si es silo, `RegistrarCosechaUseCase` inserta entidad |
| | 3 | | Si es venta, `RegistrarCosechaConVentaUseCase` inserta cosecha y registro de Venta/Precio |

### CU9 - Catálogo de Insumos (Refactorizado)

| CU9 | Catálogo Global y Soft-Delete de Insumos | | |
| --- | --- | --- | --- |
| **Descripción** | Implementación de un catálogo global en lugar de insumos atados exclusivamente a una campaña, introduciendo eliminación lógica (`activo = false`). | | |
| **Actores** | Propietario. | | |
| **Pre condiciones** | - | | |
| **Post condiciones** | Insumo disponible en el catálogo para ser vinculado a múltiples campañas. | | |
| **Secuencia Normal** | **#** | **Acción (actor)** | **Reacción (sistema)** |
| | 1 | Crea Insumo en el catálogo | `CrearInsumoCatalogoUseCase` inserta entidad con `activo=true` |
| | 2 | Vincula insumo a la campaña activa | `AsignarInsumoACampaniaUseCase` genera la relación de cantidades y precio |
| | 3 | Elimina insumo del catálogo | `EliminarInsumoCatalogoUseCase` actualiza a `activo=false` (sin hacer DELETE SQL) |

### CU12 / CU13 - Backups y Restauración (Nuevo)

| CU12 / CU13 | Backup con Storage Access Framework (SAF) | | |
| --- | --- | --- | --- |
| **Descripción** | Exportación e importación completa de la base de datos local usando SAF, garantizando persistencia externa y mitigando la pérdida de datos. | | |
| **Actores** | Propietario, Sistema de Archivos Android (SAF). | | |
| **Pre condiciones** | Interfaz SAF disponible, BD existente. | | |
| **Post condiciones** | Archivo `.db` generado en memoria externa o base de datos local reemplazada. | | |
| **Secuencia Normal** | **#** | **Acción (actor)** | **Reacción (sistema)** |
| | 1 | Selecciona Exportar DB (CU13) | Abre el selector SAF de Android |
| | 2 | Confirma directorio destino | `CrearBackupUseCase` copia el archivo SQLite al destino |
| | 3 | Selecciona Importar DB (CU12) y elige archivo | `RestaurarBackupUseCase` reemplaza la BD local y reinicia instancia de Room |
