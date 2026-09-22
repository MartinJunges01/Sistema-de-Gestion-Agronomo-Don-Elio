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
