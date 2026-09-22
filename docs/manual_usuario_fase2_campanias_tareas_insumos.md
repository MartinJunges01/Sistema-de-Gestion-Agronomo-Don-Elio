# Manual de Usuario — Fase 2: Campañas + Tareas + Insumos

> **Destino en el documento principal:** `docs/documentacion_tpi_2da_entrega.md` → Sección **8.2 (continuación)**
>
> **Instrucción para el equipo:** Copiar el contenido de este archivo a continuación del [FIN_FASE_MANUAL_1], respetando los subtítulos indicados.

---

#### 8.2.4 Gestión de Campañas

Una campaña es el eje central de la aplicación. Representa un ciclo agrícola completo (siembra, insumos, cosecha) asociado a un cultivo y a un período de tiempo. Todas las tareas, insumos, cosechas y observaciones se registran siempre dentro de una campaña.

---

##### 8.2.4.1 Pantalla: Listado de Campañas

[INSERTAR CAPTURA PANTALLA GESTION CAMPANIAS]

**¿Qué hace esta pantalla?**

Muestra todas las campañas organizadas en dos secciones: las campañas **activas** (en curso) y el **historial** de campañas finalizadas. Desde aquí se accede al detalle de cualquier campaña y se eliminan las finalizadas.

**Elementos de la pantalla:**

| Elemento | Descripción |
|---|---|
| Sección "Activas" | Lista las campañas en curso. Fondo blanco, etiqueta verde "Activa" |
| Sección "Historial (N)" | Lista las campañas finalizadas. Fondo grisado. Se expande/colapsa tocando el encabezado |
| Ícono ▼ / ▲ | Indica si el historial está colapsado o expandido |
| Ícono 🗑️ (Eliminar) | Solo aparece en campañas del historial. Permite eliminarlas permanentemente |
| Mensaje "No hay campañas" | Se muestra cuando no existe ninguna campaña aún |

**Cómo ver el detalle de una campaña:**

1. Tocar la tarjeta de cualquier campaña activa.
2. La aplicación navegará al **Detalle de Campaña**.

**Cómo ver el historial:**

1. Tocar el encabezado **"Historial (N)"** para expandirlo.
2. Tocar nuevamente para colapsarlo.

**Cómo eliminar una campaña del historial:**

1. Expandir la sección **"Historial"**.
2. Tocar el ícono de 🗑️ en la campaña que se desea eliminar.
3. Leer la advertencia en el diálogo de confirmación: *"Esta acción eliminará todos los datos asociados (cosechas, insumos, observaciones, tareas) y no se puede deshacer."*
4. Tocar **"Eliminar"** para confirmar, o **"Cancelar"** para abortar.

> **Advertencia:** La eliminación es permanente. Los insumos, cosechas, tareas y observaciones vinculadas a esa campaña se eliminan también. No hay forma de recuperarlos.

---

##### 8.2.4.2 Pantalla: Crear / Editar Campaña

[INSERTAR CAPTURA PANTALLA FORMULARIO CAMPANIA]

**¿Qué hace esta pantalla?**

Permite crear una nueva campaña o editar los datos de una existente. El título de la pantalla cambia entre "Crear Campaña" y "Editar Campaña" según el contexto.

**Elementos del formulario:**

| Campo | Descripción |
|---|---|
| **Nombre de la Campaña** | Nombre identificador (obligatorio) |
| **Cultivo** | Selector desplegable del tipo de cultivo (obligatorio). Incluye la opción "+ Agregar nuevo cultivo..." |
| Ícono de configuración ⚙️ junto al cultivo | Navega al Catálogo de Cultivos para gestionar los tipos disponibles |
| **Hectáreas** | Número de hectáreas (obligatorio). Acepta valores decimales |
| **Fecha de Inicio** | Campo de solo lectura. Se abre un calendario al tocar el ícono 📅 |
| Botón **"Guardar Campaña"** / **"Actualizar Campaña"** | Guarda los datos. Se deshabilita durante la operación de guardado |

**Pasos para crear una campaña:**

1. Ingresar el **nombre** de la campaña (ej: *Soja 2026*).
2. Tocar el campo **"Cultivo"** y seleccionar el tipo de cultivo de la lista desplegable.
   - Si el cultivo no existe en la lista, seleccionar **"+ Agregar nuevo cultivo..."**, ingresar el nombre en el diálogo emergente y tocar **"Guardar"**. El nuevo cultivo quedará seleccionado automáticamente.
3. Ingresar la cantidad de **hectáreas**.
4. Tocar el ícono 📅 en el campo **"Fecha de Inicio"** y seleccionar la fecha en el calendario.
5. Tocar **"Guardar Campaña"**.

**Resolución de problemas comunes:**

- *El campo "Cultivo" no tiene opciones* → Ir al Catálogo de Cultivos (ícono ⚙️) y crear al menos un cultivo.
- *Error en "Fecha de Inicio"* → En modo creación, no se permite seleccionar una fecha pasada. Elegir la fecha de hoy o una futura.
- *Error en "Hectáreas"* → El campo acepta únicamente números positivos.

---

##### 8.2.4.3 Pantalla: Detalle de Campaña

[INSERTAR CAPTURA PANTALLA DETALLE CAMPANIA]

**¿Qué hace esta pantalla?**

Es el panel de control de una campaña individual. Muestra un resumen de la campaña y brinda acceso rápido a sus cuatro módulos asociados: **Tareas**, **Insumos**, **Cosechas** y **Observaciones**, cada uno con un contador de registros en tiempo real.

**Elementos de la pantalla:**

| Elemento | Descripción |
|---|---|
| Encabezado (barra verde) | Nombre de la campaña, botón Volver (←) y botón Finalizar (📦) |
| Botones ◄ ► en la barra | Navegan a la campaña anterior o siguiente sin tener que volver al listado |
| Chips de información (Cultivo, Inicio, Estado) | Datos resumidos de la campaña, desplazables horizontalmente |
| Card de datos + botón "Editar Datos" | Muestra nombre, cultivo, fecha y estado. Permite editar la campaña |
| Tarjeta **Tareas** | Cantidad de tareas pendientes y completadas. Botón "+" para crear tarea rápida |
| Tarjeta **Insumos** | Cantidad de registros de insumos y costo total. Botón "+" para vincular insumo |
| Tarjeta **Cosechas** | Cantidad de cosechas y toneladas totales. Botón "+" para registrar cosecha |
| Tarjeta **Observaciones** | Cantidad de notas de campo. Botón "+" para agregar observación |

**Cómo finalizar una campaña:**

1. Tocar el ícono de archivo 📦 en la barra superior derecha.
2. La campaña pasará al estado **"Inactiva"** y se moverá al historial.
3. Todos sus datos (tareas, insumos, cosechas) quedan preservados para consulta futura.

> **Nota:** Finalizar una campaña no elimina ningún dato. Es equivalente a "archivarla". Si se necesita eliminarla permanentemente, hacerlo desde el Listado de Campañas en la sección Historial.

---

#### 8.2.5 Gestión de Tareas

El módulo de Tareas funciona como una **agenda de actividades** del establecimiento. Permite programar, filtrar, completar y eliminar tareas asociadas a campañas, con la opción de recibir notificaciones de recordatorio en el dispositivo.

---

##### 8.2.5.1 Pantalla: Listado de Tareas (Agenda)

[INSERTAR CAPTURA PANTALLA TAREAS SCREEN]

**¿Qué hace esta pantalla?**

Muestra todas las tareas organizadas en dos grupos: **Pendientes** y **Completadas**. Incluye filtros para ver solo las tareas de una campaña específica o de un rango de fechas determinado.

**Elementos de la pantalla:**

| Elemento | Descripción |
|---|---|
| Chip "Todas las Campañas" | Filtra las tareas por campaña. Al tocarlo, aparece el listado de campañas disponibles |
| Chip "Fechas" | Abre el selector de rango de fechas para filtrar por período |
| Ícono ✕ rojo | Limpia todos los filtros activos |
| Sección "Próximas y Pendientes" | Tareas no completadas. Las vencidas tienen fondo rojo y texto rojo |
| Sección "Completadas" | Tareas confirmadas, con texto tachado y fondo grisado |
| Checkbox ○ / ✓ | A la izquierda de cada tarea. Tocarlo marca o desmarca la tarea como completada |
| Ícono ✏️ (Editar) | Solo en tareas pendientes. Abre el formulario de edición |
| Ícono 🗑️ (Eliminar) | Solo en tareas pendientes. Solicita confirmación antes de eliminar |
| Botón **"Programar Nueva Tarea"** | Al pie de la pantalla. Requiere tener una campaña seleccionada |

**Cómo marcar una tarea como completada:**

1. Tocar el **checkbox** (círculo) a la izquierda del nombre de la tarea.
2. La tarea se moverá a la sección **"Completadas"** con el texto tachado.
3. Si la tarea tenía un recordatorio programado, la notificación se cancela automáticamente.

**Cómo filtrar tareas:**

1. Tocar el chip **"Todas las Campañas"** y seleccionar una campaña de la lista.
2. Opcionalmente, tocar el chip **"Fechas"** para abrir el selector de rango de fechas.
3. Para quitar todos los filtros, tocar el ícono ✕ rojo.

**Resolución de problemas comunes:**

- *El botón "Programar Nueva Tarea" está deshabilitado* → Seleccionar una campaña en el filtro de campañas antes de crear la tarea.
- *No se ven tareas* → Verificar que no haya filtros activos (chip "Campaña" o "Fechas" seleccionados). Tocar el ícono ✕ para limpiarlos.

---

##### 8.2.5.2 Pantalla: Nueva Tarea / Editar Tarea

[INSERTAR CAPTURA PANTALLA NUEVA TAREA]

**¿Qué hace esta pantalla?**

Permite crear una tarea nueva o editar una existente. El título cambia entre "Nueva Tarea" y "Editar Tarea" según el contexto.

**Elementos del formulario:**

| Campo | Descripción |
|---|---|
| **Campaña vinculada** | Selector desplegable de la campaña a la que pertenece la tarea (obligatorio) |
| **Nombre de la Tarea** | Descripción breve de la actividad (obligatorio) |
| **Fecha** | Campo de solo lectura. Se abre calendario al tocar ícono 📅 |
| **Hora** | Campo de solo lectura. Se abre selector de hora al tocar ícono 🕐. Formato 24 horas (HH:mm) |
| **Activar Notificación de Recordatorio** | Checkbox. Si está activo, el sistema enviará una notificación al dispositivo en la fecha y hora indicadas |
| Botón **"Guardar Tarea"** / **"Guardar Cambios"** | Guarda la tarea |

**Pasos para programar una tarea:**

1. Seleccionar la **campaña** en el selector desplegable.
2. Ingresar el **nombre** de la tarea (ej: *Aplicación de herbicida*).
3. Tocar el ícono 📅 y seleccionar la **fecha** en el calendario.
4. Tocar el ícono 🕐 y seleccionar la **hora** en el selector (formato 24 horas).
5. Si se desea recibir una notificación de recordatorio, activar el **checkbox** correspondiente.
6. Tocar **"Guardar Tarea"**.

**Resolución de problemas comunes:**

- *No llega la notificación de recordatorio* → Verificar que la aplicación tenga permiso de notificaciones en los ajustes del dispositivo (Ajustes → Aplicaciones → Don Elio → Notificaciones).
- *Error en el campo "Hora"* → La hora debe tener el formato `HH:mm` con valores válidos (00:00 a 23:59).

---

#### 8.2.6 Gestión de Insumos

El módulo de Insumos maneja dos conceptos separados que trabajan en conjunto: el **catálogo global** de tipos de insumos disponibles y los **insumos vinculados** a cada campaña con sus cantidades y costos.

---

##### 8.2.6.1 Pantalla: Insumos de una Campaña

[INSERTAR CAPTURA PANTALLA INSUMOS SCREEN]

**¿Qué hace esta pantalla?**

Muestra todos los insumos aplicados en la campaña seleccionada, agrupados por tipo. Permite vincular nuevos insumos, editar cantidades y precios, y desvincular registros.

**Elementos de la pantalla:**

| Elemento | Descripción |
|---|---|
| Selector de Campaña | Desplegable en la parte superior. Permite cambiar la campaña activa |
| Botón "Catálogo" (esquina superior derecha) | Navega al Catálogo Global de Insumos para agregar o editar tipos |
| Botón **"Vincular Nuevo Insumo"** | Navega a la pantalla de vinculación. Requiere tener una campaña seleccionada |
| Tarjeta agrupada por insumo | Muestra nombre, ícono emoji, cantidad total y costo total de todas las aplicaciones |
| Badge "xN" | Aparece cuando un mismo insumo tiene N aplicaciones registradas |
| Ícono ▼ (Expandir) | Despliega las aplicaciones individuales del insumo |
| Fila individual | Muestra cantidad × precio = costo parcial y fecha de aplicación |
| Ícono ✏️ (Editar fila) | Abre un diálogo para modificar cantidad y precio de esa aplicación |
| Ícono 🗑️ (Eliminar fila) | Elimina esa aplicación específica sin afectar el catálogo |

**Cómo ver los registros individuales de un insumo:**

1. Tocar la tarjeta del insumo agrupado para expandirla.
2. Se mostrarán todas las aplicaciones de ese insumo con su cantidad, precio y fecha.
3. Tocar nuevamente la tarjeta para colapsar.

**Cómo editar la cantidad o precio de una aplicación:**

1. Expandir el insumo correspondiente.
2. Tocar el ícono ✏️ de la fila a modificar.
3. Ingresar los nuevos valores en el diálogo emergente.
4. Tocar **"Guardar"**.

---

##### 8.2.6.2 Pantalla: Vincular Insumo a Campaña

[INSERTAR CAPTURA PANTALLA VINCULAR INSUMO]

**¿Qué hace esta pantalla?**

Permite buscar un insumo del catálogo global y registrar su aplicación en la campaña seleccionada, indicando cantidad y precio.

**Elementos de la pantalla:**

| Elemento | Descripción |
|---|---|
| Selector de Campaña | Permite confirmar o cambiar la campaña de destino |
| Campo "Buscar insumo en catálogo" | Búsqueda en tiempo real sobre el catálogo. Filtra mientras se escribe |
| Lista de resultados | Muestra los insumos coincidentes. Tocar uno para seleccionarlo (se resalta en verde) |
| Mensaje "El insumo no existe en el catálogo" | Aparece cuando la búsqueda no tiene resultados |
| Botón "Crear nuevo insumo" | Solo aparece cuando no hay resultados. Navega al Catálogo para crear el insumo |
| Campo **"Cantidad"** | Cantidad aplicada. Acepta punto (.) o coma (,) como separador decimal |
| Campo **"Precio (opcional)"** | Precio unitario. Opcional si aún no se conoce |
| Botón **"Vincular a Campaña"** | Guarda el registro. Se habilita solo cuando hay campaña, insumo y cantidad |
| Botón "Agregar al catálogo" | Navega al Catálogo para crear un tipo de insumo nuevo |

**Pasos para vincular un insumo:**

1. Confirmar que la **campaña** correcta está seleccionada en el selector.
2. Escribir el nombre del insumo en el campo de búsqueda (ej: *Glifosato*).
3. Tocar el insumo en la lista de resultados para seleccionarlo. El ítem se resaltará en verde.
4. Ingresar la **cantidad** aplicada.
5. Ingresar el **precio** unitario (opcional).
6. Tocar **"Vincular a Campaña"**.

> **Nota:** Un mismo insumo puede vincularse múltiples veces a la misma campaña. Cada vinculación queda como un registro independiente con su propia fecha de aplicación automática. Esto permite registrar, por ejemplo, dos aplicaciones de herbicida en fechas distintas.

**Resolución de problemas comunes:**

- *El insumo buscado no aparece en la lista* → El insumo no existe en el catálogo. Tocar "Crear nuevo insumo" para agregarlo.
- *El botón "Vincular a Campaña" está deshabilitado* → Verificar que haya una campaña seleccionada, un insumo seleccionado (resaltado en verde) y una cantidad ingresada.

---

*[FIN_FASE_MANUAL_2]*
