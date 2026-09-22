# Manual de Usuario — Fase 1: Instalación + Autenticación + Dashboard

> **Destino en el documento principal:** `docs/documentacion_tpi_2da_entrega.md` → Sección **8. Manual de Usuario**
>
> **Instrucción para el equipo:** Copiar el contenido de este archivo dentro de la sección 8, respetando los subtítulos indicados.

---

## 8. Manual de Usuario

### 8.1 Guía de Instalación y Ejecución

La aplicación **Don Elio** es una app Android que funciona completamente sin conexión a internet. Todos los datos se guardan de forma local en el dispositivo. A continuación se detallan los dos métodos de instalación disponibles.

---

#### 8.1.1 Instalación desde el archivo APK (Usuarios finales)

Este es el método recomendado para usuarios que sólo quieren usar la aplicación, sin necesidad de modificar el código.

**Requisitos del dispositivo:**
- Teléfono o tablet con sistema operativo **Android 8.0 (Oreo) o superior**.
- Al menos **50 MB de espacio de almacenamiento libre**.

**Pasos:**

1. Recibir el archivo `don-elio.apk` (puede enviarse por correo electrónico, WhatsApp, o copiarse desde una PC mediante un cable USB).
2. En el dispositivo Android, abrir el explorador de archivos y ubicar el archivo `.apk` descargado.
3. Tocar el archivo para iniciar la instalación. Si el sistema pide permiso para **"instalar aplicaciones de fuentes desconocidas"**, aceptar la advertencia (es normal para aplicaciones que no provienen de la Play Store).
4. Tocar **"Instalar"** en la pantalla de confirmación.
5. Una vez instalada, tocar **"Abrir"** o buscar el ícono de Don Elio en la lista de aplicaciones.

> **Nota:** La primera vez que la aplicación se ejecuta, crea automáticamente la base de datos local con las tablas vacías. Esto puede tomar 1 o 2 segundos adicionales en el primer arranque.

---

#### 8.1.2 Ejecución desde Android Studio (Desarrolladores)

Para ejecutar la aplicación desde el entorno de desarrollo:

**Requisitos previos:**
- **Android Studio** (versión "Iguana" o superior).
- **JDK 17** (Java Development Kit).
- Un dispositivo físico Android con **depuración USB activada**, o un emulador configurado.

**Pasos:**

1. Clonar el repositorio del proyecto y abrirlo en Android Studio.
2. Esperar a que finalice la **sincronización de Gradle** (la barra de progreso en la parte inferior del IDE).
3. Conectar el dispositivo físico por USB o iniciar un emulador desde el **AVD Manager**.
4. Presionar el botón **"Run"** (triángulo verde) o usar el atajo `Shift + F10`.
5. La aplicación se compilará e instalará automáticamente en el dispositivo seleccionado.

---

### 8.2 Guía de Uso de la Aplicación

---

#### 8.2.1 Pantalla de Inicio de Sesión

Al abrir la aplicación por primera vez (o luego de cerrar sesión), se muestra la pantalla de acceso al sistema.

[INSERTAR CAPTURA PANTALLA LOGIN]

**¿Qué hace esta pantalla?**

Permite ingresar al sistema con las credenciales de un usuario ya registrado. La aplicación no requiere internet: el nombre de usuario y la contraseña se verifican contra la base de datos local del dispositivo.

**Elementos de la pantalla:**

| Elemento | Descripción |
|---|---|
| Ícono de agricultura y título "Don Elio" | Identifica la aplicación |
| Campo "Nombre de usuario" | Ingresar el nombre de usuario registrado |
| Campo "Contraseña" | Ingresar la contraseña (los caracteres se muestran ocultos con puntos) |
| Botón **"Ingresar"** | Valida las credenciales e ingresa al sistema |
| Enlace **"¿No tienes cuenta? Regístrate aquí"** | Navega a la pantalla de registro |

**Pasos para iniciar sesión:**

1. Ingresar el **nombre de usuario** en el primer campo.
2. Ingresar la **contraseña** en el segundo campo.
3. Tocar el botón **"Ingresar"**.
4. Si las credenciales son correctas, la aplicación navegará automáticamente al **Dashboard principal**.

**Resolución de problemas comunes:**

- *"Credenciales inválidas"* → Verificar que el nombre de usuario y la contraseña sean correctos. Recordar que distinguen entre mayúsculas y minúsculas.
- *El botón "Ingresar" no responde* → Esperar a que desaparezca el indicador de carga circular; la app está verificando las credenciales.
- *No recuerdo mi contraseña* → La aplicación no tiene recuperación de contraseña por correo electrónico dado que funciona sin internet. Si no se recuerda la contraseña, contactar al administrador del sistema para que genere un nuevo usuario.

---

#### 8.2.2 Pantalla de Registro de Usuario

Permite crear una cuenta nueva para acceder a la aplicación. Esta pantalla se usa únicamente la primera vez, o cuando se quiere agregar un nuevo usuario al sistema.

[INSERTAR CAPTURA PANTALLA REGISTRO]

**¿Qué hace esta pantalla?**

Registra un nuevo usuario en la base de datos local del dispositivo. Una vez registrado, el usuario puede ingresar desde la pantalla de inicio de sesión.

**Elementos de la pantalla:**

| Elemento | Descripción |
|---|---|
| Campo "Nombre Completo" | Nombre para el saludo personalizado en el Dashboard |
| Campo "Nombre de usuario" | Identificador único para iniciar sesión |
| Campo "Contraseña" | Clave de acceso personal |
| Botón **"Registrarse"** | Crea la cuenta y accede automáticamente al sistema |
| Enlace **"¿Ya tienes cuenta? Inicia sesión"** | Regresa a la pantalla de inicio de sesión |

**Pasos para registrarse:**

1. Ingresar el **nombre completo** (por ejemplo: *Juan Pérez*). Este nombre aparecerá como saludo en el Dashboard.
2. Elegir un **nombre de usuario** corto y fácil de recordar (por ejemplo: *juan*).
3. Ingresar una **contraseña**.
4. Tocar el botón **"Registrarse"**.
5. Si el registro es exitoso, la aplicación ingresará automáticamente al Dashboard.

**Resolución de problemas comunes:**

- *Mensaje de error al registrarse* → Verificar que ninguno de los tres campos esté vacío.
- *El nombre de usuario ya existe* → El sistema no permite dos usuarios con el mismo nombre de usuario. Elegir uno diferente.

---

#### 8.2.3 Dashboard Principal (Panel de Control)

Es la pantalla central de la aplicación. Se muestra inmediatamente después de iniciar sesión y presenta un resumen de todas las operaciones activas en tiempo real.

[INSERTAR CAPTURA PANTALLA DASHBOARD COMPLETO]

**¿Qué hace esta pantalla?**

Ofrece una vista de alto nivel del estado del establecimiento: el rendimiento económico del mes, el cumplimiento de tareas de la semana, las tareas próximas y las campañas en curso. Todos los datos se actualizan automáticamente cuando se realizan cambios en cualquier parte de la aplicación.

---

**Sección: Encabezado superior**

[INSERTAR CAPTURA PANTALLA HEADER DASHBOARD]

| Elemento | Descripción |
|---|---|
| Saludo con nombre del usuario | Muestra el nombre completo ingresado al registrarse |
| Ícono de configuración (engranaje) | Navega a la pantalla de Configuración y Backup |
| Ícono de cerrar sesión | Cierra la sesión activa y regresa a la pantalla de Login |

**Cómo cerrar sesión:**

1. Tocar el ícono de salida ubicado en el encabezado superior derecho.
2. Confirmar la acción si se solicita.
3. La aplicación regresará a la pantalla de inicio de sesión.

---

**Sección: Rendimiento Global (Mes Actual)**

[INSERTAR CAPTURA PANTALLA SECCION RENDIMIENTO]

Muestra tres tarjetas con los indicadores financieros del mes en curso, calculados a partir de las campañas activas:

| Tarjeta | Qué muestra |
|---|---|
| **Capital Invertido** | Suma total del costo de todos los insumos aplicados en campañas activas |
| **Ingresos Brutos** | Total de ventas de cosechas registradas en el mes actual |
| **Balance** | Diferencia entre Ingresos y Capital. Se muestra en verde si es positivo, en rojo si es negativo |

> **Nota:** Esta sección solo aparece si hay al menos una campaña activa con datos. Si no hay campañas activas, la sección no se muestra.

- Tocar **"Ver detalle →"** para ir a la pantalla de Reportes con el análisis completo.

---

**Sección: Cumplimiento Semanal**

[INSERTAR CAPTURA PANTALLA CUMPLIMIENTO]

Muestra un gráfico circular que indica qué porcentaje de las tareas programadas para la semana actual (de lunes a domingo) ya fueron completadas.

- El arco **verde** representa las tareas completadas.
- El fondo **gris** representa las tareas pendientes.
- El porcentaje se muestra en el centro del gráfico.
- Debajo del gráfico se indica: *"X de Y tareas completadas"*.

> **Nota:** Si no hay tareas programadas para la semana actual, aparece el mensaje *"Sin tareas esta semana"* en lugar del gráfico.

---

**Sección: Tareas Próximas**

[INSERTAR CAPTURA PANTALLA TAREAS PROXIMAS]

Lista las tareas pendientes más cercanas en el tiempo. Cada tarjeta muestra:
- **Nombre de la tarea** en negrita.
- **Fecha y hora** programada.
- Las tareas cuya fecha ya pasó se resaltan con **fondo rojo** para indicar que están vencidas.

**Acciones disponibles:**

- Tocar una tarjeta de tarea para ir directamente al **Detalle de la Campaña** a la que pertenece.
- Tocar **"Ver todas →"** para ir a la pantalla completa de **Gestión de Tareas**.

---

**Sección: Campañas Activas**

[INSERTAR CAPTURA PANTALLA CAMPANIAS DASHBOARD]

Lista todas las campañas marcadas como activas. Cada tarjeta muestra:
- **Nombre de la campaña**.
- **Tipo de cultivo** (Soja, Maíz, Trigo, etc.).
- **Fecha de inicio**.
- Etiqueta **"Activa"** en verde.

**Acciones disponibles:**

- Tocar una tarjeta de campaña para ir al **Detalle de esa Campaña**, donde se pueden gestionar sus tareas, insumos, cosechas y observaciones.

> **Nota:** Si no hay campañas creadas, la pantalla muestra el mensaje *"No hay campañas — Presiona + para crear una"*. En ese caso, navegar a la sección **Campañas** desde la barra de navegación inferior para crear la primera campaña.

---

**Barra de navegación inferior**

[INSERTAR CAPTURA PANTALLA BARRA NAVEGACION]

La barra fija en la parte inferior de la pantalla permite acceder rápidamente a las secciones principales de la aplicación:

| Ícono | Sección |
|---|---|
| 🏠 Inicio | Dashboard (pantalla actual) |
| 🌾 Campañas | Gestión de Campañas |
| ✅ Tareas | Gestión de Tareas |
| 🧪 Insumos | Gestión de Insumos |
| 📊 Reportes | Reportes de Rendimiento |

---

*[FIN_FASE_MANUAL_1]*


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


