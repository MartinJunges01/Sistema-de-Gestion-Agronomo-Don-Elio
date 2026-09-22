# Manual de Usuario — Fase 3: Cosechas + Catálogo de Cultivos + Observaciones + Reportes + Configuración

> **Destino en el documento principal:** `docs/documentacion_tpi_2da_entrega.md` → Sección **8.2 (continuación) y 8.3**
>
> **Instrucción para el equipo:** Copiar el contenido de este archivo a continuación del [FIN_FASE_MANUAL_2].

---

#### 8.2.7 Gestión de Cosechas

El módulo de Cosechas permite registrar la producción obtenida en cada campaña, distinguiendo entre dos destinos: la cosecha que se **almacena en el establecimiento** (silo, silobolsa) y la que se **destina directamente a venta o consumo**.

---

##### 8.2.7.1 Pantalla: Listado de Cosechas

[INSERTAR CAPTURA PANTALLA COSECHAS SCREEN]

**¿Qué hace esta pantalla?**

Muestra todos los registros de cosechas de la campaña seleccionada, organizados en dos grupos según su destino.

**Elementos de la pantalla:**

| Elemento | Descripción |
|---|---|
| Selector de Campaña | Desplegable en la parte superior para elegir la campaña activa |
| Sección **"Cosechas Almacenadas"** | Registros con borde verde. Indica cantidad en Tn y nombre del almacén (silo/silobolsa) |
| Sección **"Cosechas No Almacenadas (Venta/Reserva)"** | Registros con borde naranja. Indica cantidad, tipo (Venta, Alimento Vacuno, Reserva) y precio total |
| Fecha de registro | Mostrada en el extremo derecho de cada tarjeta |
| Ícono ✏️ (Editar) | Abre el formulario de edición de esa cosecha |
| Ícono 🗑️ (Eliminar) | Solicita confirmación antes de eliminar el registro |
| Mensaje "Sin cosechas registradas" | Aparece cuando no hay registros para la campaña seleccionada |

---

##### 8.2.7.2 Pantalla: Registrar / Editar Cosecha

[INSERTAR CAPTURA PANTALLA FORMULARIO COSECHA]

**¿Qué hace esta pantalla?**

Permite registrar una nueva cosecha o editar una existente. El título cambia entre "Registrar Cosecha" y "Editar Cosecha".

**Elementos del formulario:**

| Campo | Descripción |
|---|---|
| **Campaña vinculada** | Selector de la campaña a la que pertenece la cosecha (obligatorio) |
| **Cantidad (Tn)** | Toneladas cosechadas. Acepta valores decimales (obligatorio) |
| **Fecha** | Fecha de la cosecha. Se selecciona con el calendario 📅 |
| **Checkbox "Almacenar en el establecimiento"** | Define el destino de la cosecha |
| **Almacén** | Solo visible si el checkbox está activo. Nombre del silo o silobolsa |
| **Tipo** | Solo visible si el checkbox está inactivo. Destino: *Venta*, *Alimento Vacuno*, *Reserva*, etc. |
| **Precio Total de Venta (\$)** | Solo visible si el checkbox está inactivo. Monto total recibido por la venta |
| Botón **"Guardar Registro"** | Guarda la cosecha. Se habilita cuando hay campaña y cantidad |

**Pasos para registrar una cosecha almacenada:**

1. Seleccionar la **campaña** en el selector.
2. Ingresar la **cantidad** en toneladas (ej: *25*).
3. Seleccionar la **fecha** tocando el ícono 📅.
4. Activar el checkbox **"Almacenar en el establecimiento"**.
5. Ingresar el nombre del **almacén** (ej: *Silo N°1*, *Silobolsa Lote 3*).
6. Tocar **"Guardar Registro"**.

**Pasos para registrar una cosecha destinada a venta:**

1. Seleccionar la **campaña** en el selector.
2. Ingresar la **cantidad** en toneladas.
3. Seleccionar la **fecha**.
4. Dejar el checkbox **desactivado** (fondo blanco).
5. Ingresar el **tipo** (ej: *Venta*).
6. Ingresar el **precio total de venta** recibido.
7. Tocar **"Guardar Registro"**.

> **Nota:** El precio registrado aquí es el que se utiliza para calcular los **Ingresos Brutos** que aparecen en el Dashboard y en los Reportes.

---

#### 8.2.8 Catálogo de Cultivos

El Catálogo de Cultivos es la lista maestra de tipos de cultivos disponibles para asignar a las campañas. Es una pantalla administrativa que permite crear, renombrar y eliminar tipos de cultivo.

[INSERTAR CAPTURA PANTALLA CATALOGO CULTIVOS]

**Elementos de la pantalla:**

| Elemento | Descripción |
|---|---|
| Lista de cultivos | Cada cultivo muestra su nombre con ícono de hoja 🌿 |
| Ícono ✏️ (Editar) | Abre un diálogo para renombrar el cultivo |
| Ícono 🗑️ (Eliminar) | Elimina el cultivo del catálogo |
| Botón **"Agregar Nuevo Cultivo"** | Al pie de la pantalla. Abre un diálogo para ingresar el nombre |

**Cómo acceder al catálogo:**

- Desde el formulario de **Crear/Editar Campaña**, tocando el ícono ⚙️ junto al selector de cultivo.
- Desde la pantalla de **Insumos**, tocando **"Catálogo"** en la barra superior.

**Cómo agregar un cultivo:**

1. Tocar **"Agregar Nuevo Cultivo"**.
2. Ingresar el nombre en el diálogo (ej: *Trigo*, *Soja*, *Maíz*).
3. Tocar **"Guardar"**.

> **Nota:** Si se elimina un cultivo que ya está asignado a campañas existentes, las campañas no pierden su información. El cultivo eliminado deja de estar disponible para nuevas campañas.

**Resolución de problemas comunes:**

- *No se puede guardar un cultivo* → El nombre del cultivo no puede estar vacío ni repetirse en el catálogo.

---

#### 8.2.9 Observaciones de Campo

El módulo de Observaciones permite registrar notas de campo asociadas a una campaña: anotaciones sobre el estado del cultivo, condiciones climáticas, eventos inesperados, etc. Cada observación puede incluir texto, una foto tomada con la cámara o elegida desde la galería.

[INSERTAR CAPTURA PANTALLA OBSERVACIONES SCREEN]

**¿Qué hace esta pantalla?**

Combina en una sola vista el formulario para crear nuevas observaciones y el historial de las observaciones ya registradas.

**Sección superior: Formulario de nueva observación**

| Elemento | Descripción |
|---|---|
| Selector de Campaña | Desplegable para elegir la campaña activa |
| Campo de texto "Escribe una nota..." | Área de texto libre. Mínimo una línea, máximo 5 líneas visibles |
| Vista previa de imagen | Aparece cuando se adjunta una foto. Tiene botón ✕ para quitarla |
| Botón **"Cámara"** | Abre la cámara del dispositivo para tomar una foto nueva |
| Botón **"Galería"** | Abre la galería de fotos del dispositivo para seleccionar una imagen existente |
| Botón **"Guardar observación"** | Guarda la nota. Se habilita cuando hay campaña seleccionada y texto o imagen |

**Pasos para agregar una observación con texto:**

1. Seleccionar la **campaña** en el selector.
2. Escribir el texto de la nota en el campo "Escribe una nota...".
3. Opcionalmente, adjuntar una imagen tocando **"Cámara"** o **"Galería"**.
4. Tocar **"Guardar observación"**.

**Gestión de permisos de cámara:**

La primera vez que se use el botón **"Cámara"**, el sistema solicitará permiso para acceder a la cámara del dispositivo. Aceptar para poder tomar fotos. Si el permiso fue rechazado permanentemente, aparecerá un mensaje con un botón **"Abrir Ajustes"** para habilitarlo manualmente.

**Sección inferior: Historial de observaciones**

Muestra todas las observaciones guardadas para la campaña seleccionada, de más reciente a más antigua.

Cada tarjeta muestra:
- El **texto** de la nota.
- La **imagen adjunta** (si tiene), en miniatura dentro de la tarjeta.
- Ícono ✏️ para editar el texto o cambiar la foto.
- Ícono 🗑️ para eliminar la observación (solicita confirmación).

**Resolución de problemas comunes:**

- *El botón "Guardar observación" está deshabilitado* → Seleccionar una campaña y escribir al menos algo en el campo de texto, o adjuntar una imagen.
- *La cámara no abre* → Verificar que la app tenga permiso de cámara en Ajustes del dispositivo.

---

#### 8.2.10 Reportes y Análisis

La pantalla de Reportes es la herramienta de análisis financiero y productivo del establecimiento. Permite visualizar el rendimiento de las campañas de múltiples formas y exportar la información a archivo.

[INSERTAR CAPTURA PANTALLA REPORTES]

**¿Qué hace esta pantalla?**

Ofrece cuatro tipos de análisis:

| Sección | Qué muestra |
|---|---|
| **Resumen Productivo-Financiero** | Métricas globales filtradas por campaña y rango de fechas. Muestra capital invertido, ingresos brutos y balance |
| **Análisis Individual por Campaña** | Desglose completo de una sola campaña: distribución de insumos (gráfico de torta), top 3 insumos por costo, rendimiento en Tn/ha, cosechas almacenadas vs. vendidas |
| **Comparativa entre Campañas** | Confronta dos campañas seleccionables: costo por hectárea, insumos y cosechas de cada una |

---

**Filtros del Resumen Productivo-Financiero:**

| Filtro | Opciones |
|---|---|
| **Por Campaña** | Chips seleccionables. Se pueden activar múltiples campañas simultáneamente |
| **Por Fecha** | "Este mes", "Último mes", "Este año", o rango personalizado con selector de calendario |
| **Limpiar** | Chip para quitar el filtro de fecha activo |

**Cómo usar el análisis individual:**

1. Desplazar hacia abajo hasta la sección **"Análisis Individual"**.
2. Seleccionar una campaña en el desplegable.
3. El sistema mostrará automáticamente:
   - Gráfico de torta con la distribución del gasto en insumos por tipo.
   - Los 3 insumos de mayor costo.
   - El rendimiento expresado en toneladas por hectárea (Tn/ha).
   - La proporción de cosecha almacenada vs. cosecha vendida.

**Cómo comparar dos campañas:**

1. Desplazar hasta la sección **"Comparativa"**.
2. Seleccionar **Campaña A** y **Campaña B** en los desplegables.
3. Se mostrarán tarjetas comparativas de costo por hectárea, insumos y cosechas entre las dos campañas seleccionadas.

**Exportar el reporte:**

1. Tocar el ícono de descarga (↓) en la barra superior derecha.
2. Seleccionar el formato de exportación:
   - **"Exportar a Excel"** → Genera un archivo `.csv` con el detalle de insumos de la campaña seleccionada.
   - **"Exportar a PDF"** → Genera un PDF con el reporte completo de la campaña seleccionada.
3. Elegir la carpeta de destino en el explorador de archivos del dispositivo.
4. El archivo se guardará y aparecerá un mensaje de confirmación.

> **Nota:** Para poder exportar, primero debe seleccionarse una campaña en el **Análisis Individual**. Si no hay ninguna seleccionada, aparecerá un aviso solicitando que se elija una.

---

### 8.3 Configuración y Respaldo de Datos

La pantalla de Configuración es accesible desde el ícono de engranaje ⚙️ del encabezado del Dashboard. Permite realizar copias de seguridad de toda la base de datos y restaurarla desde un archivo.

[INSERTAR CAPTURA PANTALLA CONFIGURACION DB]

> **Esta pantalla es crítica.** Las operaciones de respaldo y restauración afectan a todos los datos de la aplicación. Se recomienda realizar respaldos periódicos, especialmente antes de actualizar o cambiar el dispositivo.

---

#### 8.3.1 Exportar Base de Datos (Respaldo)

Crea una copia completa de toda la base de datos local (campañas, insumos, tareas, cosechas, observaciones, usuarios) y la guarda en el dispositivo como un archivo `.db`.

**Pasos:**

1. Tocar el botón **"Exportar Base de Datos (Respaldo)"**.
2. El explorador de archivos se abrirá con el nombre sugerido `don_elio_backup.db`.
3. Elegir la carpeta de destino (se recomienda guardar en el almacenamiento interno o en una unidad USB/nube).
4. Tocar **"Guardar"**.
5. Aparecerá un mensaje de confirmación cuando el archivo se haya guardado correctamente.

> **Recomendación:** Realizar un respaldo antes de cada operación de restauración y periódicamente (por ejemplo, una vez por semana o al finalizar cada campaña importante).

---

#### 8.3.2 Importar Base de Datos (Restaurar)

Reemplaza toda la base de datos actual con los datos de un archivo de respaldo previamente exportado.

**Pasos:**

1. Tocar el botón **"Importar Base de Datos"**.
2. El explorador de archivos se abrirá. Navegar y seleccionar el archivo `.db` del respaldo.
3. Leer el diálogo de advertencia: *"Se sobrescribirán TODOS los datos actuales. Esta acción no se puede deshacer. ¿Desea continuar?"*
4. Tocar **"Restaurar"** para confirmar, o **"Cancelar"** para abortar.
5. La aplicación se **cerrará y reiniciará automáticamente** para cargar los datos restaurados.

> **Advertencia:** Esta operación es **irreversible**. Todos los datos actuales serán reemplazados por los del archivo importado. Si los datos actuales son importantes, exportar un respaldo primero.

---

*[FIN_FASE_MANUAL_3 — FIN MANUAL DE USUARIO]*
