# Sistema de Gestión Agrónomo "Don Elio"

## Índice General

1. [Introducción](#introducción)
2. [Fundamentación del Proyecto](#fundamentación-del-proyecto)
3. [Alcance y Límites](#alcance-y-límites)
4. [Objetivos](#objetivos-general-y-objetivos-específicos)
    - 4.1. [Objetivo General](#objetivo-general)
    - 4.2. [Objetivos Específicos](#objetivos-específicos)
5. [Relevamiento y Análisis de Requerimientos](#relevamiento-y-análisis-de-requerimientos)
    - 5.1. [Requerimientos Funcionales](#requerimientos-funcionales)
    - 5.2. [Requerimientos No Funcionales](#requerimientos-no-funcionales)
6. [Diseño y Prototipado (Primera Entrega 2026)](#diseño-y-prototipado-primera-entrega-2026)
    - 6.1. [Interfaces de Usuario (Vistas)](#interfaces-de-usuario-vistas)
    - 6.2. [Modelo Lógico de la Base de Datos](#modelo-lógico-de-la-base-de-datos)
    - 6.3. [Modelo Físico de la Base de Datos (DDL)](#modelo-físico-de-la-base-de-datos-ddl)
7. [Documentación Técnica (Entrega Completa)](#documentación-técnica-entrega-completa)
    - 7.1. [Arquitectura del Sistema](#arquitectura-del-sistema)
    - 7.2. [Stack Tecnológico](#stack-tecnológico)
    - 7.3. [Estructura del Proyecto (Módulos)](#estructura-del-proyecto-módulos)
    - 7.4. [Diagramas UML](#diagramas-uml)
        - 7.4.1. Diagrama de Contexto
        - 7.4.2. Diagrama de Casos de Uso
        - 7.4.3. Diagrama de Clases
        - 7.4.4. Diagrama Entidad-Relación (E/R)
    - 7.5. [Descripción Técnica de Funcionalidades](#descripción-técnica-de-funcionalidades)
8. [Manual de Usuario](#manual-de-usuario)
    - 8.1. [Guía de Instalación y Ejecución](#guía-de-instalación-y-ejecución)
    - 8.2. [Guía de Uso Paso a Paso (Pantallas)](#guía-de-uso-paso-a-paso-pantallas)
    - 8.3. [Resolución de Errores Comunes](#resolución-de-errores-comunes)
9. [Conclusiones y Trabajo Futuro](#conclusiones-y-trabajo-futuro)
10. [Anexos](#anexos)

---

## Introducción

El establecimiento “Don Elio” se dedica actualmente a la producción agrícola-ganadera. Está ubicado en la localidad de Tabossi, provincia de Entre Ríos, y desarrolla sus actividades también en zonas aledañas. 

En el área agrícola, se enfoca en la siembra, cosecha y posterior comercialización de los cultivos obtenidos. Parte de la producción se almacena para futuras campañas, ya sea en silobolsas o en silos convencionales, según las necesidades operativas. 

La empresa cuenta con un equipo de aproximadamente ocho empleados que se desempeñan en distintas áreas productivas. 

En el sector pecuario, el establecimiento dispone de un sistema de engorde a corral (feedlot), además de un depósito principal y varios espacios destinados al almacenamiento de maquinaria e insumos. 

Actualmente, “Don Elio” opera sobre una superficie total de aproximadamente 2.000 hectáreas, de las cuales 500 son propias y unas 1.500 se encuentran alquiladas en distintas zonas de la provincia de Entre Ríos. 

En contacto con la empresa pudimos dialogar sobre las distintas áreas de mejora y profundizar en el conocimiento del funcionamiento de la misma. Con las conclusiones de la entrevista, creemos que aportamos más valor a la empresa creando un sistema de gestión que permita registrar los ingresos y egresos de cada campaña, ya que actualmente no cuenta con un sistema informático dedicado a tales fines. 

## Fundamentación del Proyecto

En la actualidad, el establecimiento “Don Elio” gestiona gran parte de su información agrícola mediante registros en papel o planillas de Excel. Esta metodología, aunque funcional en el corto plazo, presenta múltiples limitaciones: la información puede extraviarse, resulta difícil de organizar y consultar en el tiempo, y carece de mecanismos automatizados para generar reportes o alertas que faciliten la toma de decisiones. 

Durante el relevamiento realizado con el personal del establecimiento, se identificó como una de las principales necesidades la implementación de una herramienta que permita centralizar y proteger los datos de cada campaña agrícola. La digitalización de estos procesos no solo evitaría la pérdida de información clave, sino que también optimizaría significativamente el tiempo y el esfuerzo invertido en la gestión operativa. 

El desarrollo de un sistema informático de gestión agrícola permitirá registrar de forma ordenada y segura los ingresos y egresos de cada campaña, las tareas programadas, el uso de insumos, el rendimiento de las cosechas, entre otros aspectos esenciales para la planificación productiva. Además, la posibilidad de generar reportes automáticos, establecer recordatorios, y visualizar estadísticas a través de un panel de control, aportará un valor estratégico a la toma de decisiones. 

En resumen, la implementación de este sistema representa una mejora sustancial en la eficiencia operativa y en el control administrativo del establecimiento, brindando una herramienta tecnológica adaptada a las necesidades específicas del rubro agropecuario. 

## Alcance y Límites

Este proyecto tiene como objetivo el desarrollo integral de un sistema de gestión, en formato de aplicación móvil nativa (Android), que integre funcionalidades clave tales como la carga eficiente de datos, almacenamiento seguro, generación automatizada de reportes y gestión de recordatorios. Se asegurará que la aplicación esté operativa y cumpla con los objetivos definidos inicialmente. 

Si bien la aplicación está destinada a un único perfil de usuario administrador (el agrónomo/productor), se incluirá un sistema de autenticación local para restringir el acceso y proteger la sensibilidad de los datos productivos y económicos almacenados. No se contempla en esta fase el desarrollo de múltiples roles de usuario (ej. peones, contadores) con distintos niveles de permisos.

Asimismo, no se contemplan futuras actualizaciones o mejoras que el cliente pudiera considerar útiles una vez puesta en marcha la aplicación, especialmente aquellas que no hayan sido incluidas dentro de los objetivos establecidos al inicio del desarrollo. Cualquier posible ampliación podrá ser evaluada en el marco de nuevos proyectos o etapas posteriores. 

## Objetivo General y Objetivos Específicos

### Objetivo General 
Desarrollar un sistema informático de gestión agrícola que permita registrar, organizar y analizar de manera eficiente la información relacionada con campañas de cultivo, insumos, tareas y producción, con el fin de optimizar la planificación, seguimiento y toma de decisiones en actividades agrícolas. 

### Objetivos Específicos 
* Diseñar un módulo para registrar y gestionar campañas de cultivo. 
* Incorporar herramientas para registrar el rendimiento de las cosechas y almacenar productos obtenidos. 
* Permitir la carga de notas y fotos como observaciones asociadas a campañas o tareas específicas. 
* Desarrollar un historial detallado de cultivos y consumos por campaña. 
* Registrar y controlar los egresos de insumos utilizados (semillas, fertilizantes, etc.) como parte de los gastos por campaña. 
* Generar reportes automáticos sobre consumo de insumos, producción por cultivo, y costos estimados por campaña. 
* Proveer al usuario de un panel de control con estadísticas e indicadores clave para la toma de decisiones. 
* Establecer recordatorios asociados a fechas relevantes dentro de cada campaña, como próximas siembras o tareas programadas. 
* Analizar datos históricos para proyectar necesidades y planificar recursos a corto, mediano y largo plazo.

## Relevamiento y Análisis de Requerimientos 

### Requerimientos Funcionales 
Los siguientes requerimientos funcionales describen las acciones y funcionalidades que el sistema de gestión deberá permitir al usuario realizar: 

**Gestión de campañas de cultivo:** 
* RF1: Crear, editar y eliminar campañas. 
* RF2: Registrar qué se siembra y cuándo. 
* RF3: Consultar el historial de cultivos por campaña. 

**Gestión de tareas agrícolas:** 
* RF4: Agendar tareas y su fecha de realización. 
* RF5: Generar notificaciones a partir de las tareas. 
* RF6: Registrar la realización efectiva de las tareas. 

**Registro de cosechas:** 
* RF7: Registrar fechas y rendimiento de cosechas. 
* RF8: Asociar cada cosecha a una campaña. 

**Gestión de observaciones:** 
* RF9: Adjuntar notas y fotografías a campañas. 

**Gestión de insumos:** 
* RF10: Registrar egresos de insumos como semillas, fertilizantes, agroquímicos. 
* RF11: Asociar estos gastos a campañas específicas. 

**Gestión de productos almacenados:** 
* RF12: Registrar productos cosechados y su almacenamiento (silo convencional/silobolsa). 

**Generación de reportes:**
* RF13: Reportes de consumo por tipo de insumo. 
* RF14: Reportes de producción por cultivo. 
* RF15: Historial de tareas realizadas por campaña. 
* RF16: Visualización de los insumos más utilizados. 
* RF17: Costos estimados por campaña. 
* RF18: Informes periódicos de historial de compras. 

**Proyecciones y estadísticas:** 
* RF19: Visualización de estadísticas mediante paneles de control. 
* RF20: Visualización de consumos históricos para proyectar necesidades a corto, mediano y largo plazo. 

**Importación y Exportación de datos:** 
* RF21: Exportar la base de datos del sistema para su respaldo o transferencia. 
* RF22: Importar una base de datos previamente respaldada para restaurar la información del sistema. 
* RF23: Exportar reportes generados en formato PDF o Excel, según preferencia del usuario. 

### Requerimientos No Funcionales 
Los requerimientos no funcionales determinan aspectos técnicos, operativos y de calidad del sistema: 

* **Usabilidad:** La interfaz deberá ser clara, intuitiva y accesible para usuarios con conocimientos técnicos básicos. El sistema debe permitir una navegación simple, con menús y botones identificables.
* **Disponibilidad:** El sistema debe estar disponible para uso diario y continuo, permitiendo su uso tanto en campo como en oficina. 
* **Portabilidad:** La aplicación deberá ser accesible desde dispositivos móviles, sin necesidad de conexión a internet (offline-first). 
* **Escalabilidad:** El sistema deberá estar preparado para futuras ampliaciones o incorporación de nuevas funcionalidades sin afectar su rendimiento.
* **Almacenamiento y respaldo:** Los datos deben almacenarse de forma segura en una base de datos local (SQLite) que permita la exportación de los mismos para prevenir pérdidas de información. 
* **Rendimiento:** El sistema debe operar con tiempos de respuesta adecuados para la carga, consulta y generación de reportes, incluso ante grandes volúmenes de datos.

## Diseño y Prototipado (Primera Entrega 2026)

### Interfaces de Usuario (Vistas)
Se definieron las siguientes vistas y flujos para la aplicación:
1. Login/registro
2. Home / Dashboard
3. Configuración (CU12/13)
4. Reportes (CU10)
5. Export (CU11)
6. Tareas (CU5) (CU 5.4)
7. Nueva Tarea (CU 5.1)
8. Edición y Eliminación de Tareas
9. Insumos (CU9 - 9.3)
10. Catálogo (CU 9.4 - CU 9.7)
11. Formulario Creación/Edición Catálogo (CU 9.5)
12. Campañas
13. Crear / Editar Campaña
14. Resumen Campaña
15. Cosechas
16. Formulario Cosecha (CU6 y CU7)
17. Observaciones
18. Adjuntar Foto

*(Nota para el equipo: Asegúrense de enlazar aquí el prototipo de Figma o pegar las capturas de las vistas solicitadas en la primera entrega).*

### Modelo Lógico de la Base de Datos

**1. Tabla: Usuario**
- **Campos y Tipos:**
  - `id_usuario`: Entero (PK)
  - `nombre`: Cadena de texto
  - `contrasena`: Cadena de texto (Hash)
  - `ultimo_acceso`: Entero Largo (timestamp)
- **Relaciones:** Ninguna (Entidad aislada para control de acceso local).

**2. Tabla: Campania**
- **Campos y Tipos:**
  - `id_campania`: Entero (PK)
  - `nombre`: Cadena de texto
  - `fecha`: Entero Largo (timestamp)
  - `cultivo`: Cadena de texto
  - `estaActiva`: Booleano
- **Relaciones:** Tabla central. Relacionada con Tarea, Observacion, Cosecha e InsumosUtilizados.

**3. Tabla: Tarea**
- **Campos y Tipos:**
  - `id_tarea`: Entero (PK)
  - `nombre`: Cadena de texto
  - `fecha`: Entero largo (timestamp)
  - `hora`: Cadena de Texto
  - `notificar`: Booleano
  - `confirmar`: Booleano
  - `id_campania`: Entero (FK) -> Referencia a la tabla Campania
- **Relaciones:** Pertenece a una Campania (1:N).

**4. Tabla: Observacion**
- **Campos y Tipos:**
  - `id_observacion`: Entero (PK)
  - `texto`: Cadena de texto larga
  - `imagenurl`: Cadena de texto (Ruta del archivo local, Opcional/Nulo)
  - `id_campania`: Entero (FK) -> Referencia a la tabla Campania
- **Relaciones:** Pertenece a una Campania (1:N).

**5. Tabla: Cosecha**
- **Campos y Tipos:**
  - `id_cosecha`: Entero (PK)
  - `cantidad`: Decimal real
  - `fecha`: Entero Largo (timestamp)
  - `unidad`: Cadena de texto
  - `almacen`: Cadena de texto
  - `id_campania`: Entero (FK) -> Referencia a la tabla Campania
- **Relaciones:** Pertenece a una Campania (1:N). Tiene muchas CosechaNoAlmacenada.

**6. Tabla: CosechaNoAlmacenada**
- **Campos y Tipos:**
  - `id_cosecha_no_alm`: Entero (PK)
  - `tipo`: Cadena de texto
  - `precio`: Decimal real (Opcional/Nulo)
  - `id_cosecha`: Entero (FK) -> Referencia a la tabla Cosecha
- **Relaciones:** Pertenece a una Cosecha (1:N).

**7. Tabla: Insumo**
- **Campos y Tipos:**
  - `id_insumo`: Entero (PK)
  - `nombre`: Cadena de texto
  - `categoria`: Cadena de texto
  - `unidad`: Cadena de texto
  - `icono`: Cadena de Texto (opcional/nulo)
  - `activo`: Booleano
- **Relaciones:** Catálogo general. Se relaciona con campania_insumo.

**8. Tabla: campania_insumo**
- **Campos y Tipos:**
  - `id_insumo_utilizado`: Entero (PK)
  - `cantidad`: Decimal real
  - `precio`: Decimal real
  - `id_campania`: Entero (FK) -> Referencia a la tabla Campania
  - `id_insumo`: Entero (FK) -> Referencia a la tabla Insumo
- **Relaciones:** Tabla intermedia/asociativa que une Campania (1:N) e Insumo (1:N).

### Modelo Físico de la Base de Datos (DDL)

```sql
-- =========================================================    
-- CREACIÓN DE TABLAS    
-- =========================================================

CREATE TABLE usuarios (    
    id_usuario INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,    
    nombre TEXT NOT NULL,    
    nombreUsuario TEXT NOT NULL,  
    contrasena TEXT NOT NULL,    
    ultimo_acceso INTEGER NOT NULL    
);

CREATE TABLE campanias (    
    id_campania INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,    
    nombre TEXT NOT NULL,    
    fecha INTEGER NOT NULL,    
    cultivo TEXT NOT NULL,  
    estaActiva INTEGER NOT NULL DEFAULT 1  
);

CREATE TABLE tareas (    
    id_tarea INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,    
    nombre TEXT NOT NULL,    
    fecha INTEGER NOT NULL,    
    hora TEXT NOT NULL,    
    notificar INTEGER NOT NULL DEFAULT 0,    
    confirmar INTEGER NOT NULL DEFAULT 0,    
    id_campania INTEGER NOT NULL,    
    FOREIGN KEY (id_campania) REFERENCES campanias(id_campania) ON DELETE CASCADE    
);

CREATE TABLE observaciones (    
    id_observacion INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,    
    texto TEXT NOT NULL,    
    imagenUri TEXT,    
    id_campania INTEGER NOT NULL,    
    FOREIGN KEY (id_campania) REFERENCES campanias(id_campania) ON DELETE CASCADE    
);

CREATE TABLE cosechas (    
    id_cosecha INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,    
    cantidad REAL NOT NULL,    
    fecha INTEGER NOT NULL,    
    unidad TEXT NOT NULL,    
    almacen TEXT NOT NULL,    
    id_campania INTEGER NOT NULL,    
    FOREIGN KEY (id_campania) REFERENCES campanias(id_campania) ON DELETE CASCADE    
);

CREATE TABLE cosechas_no_almacenadas (    
    id_cosecha_no_alm INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,    
    tipo TEXT NOT NULL,    
    precio REAL NOT NULL,    
    id_cosecha INTEGER NOT NULL,    
    FOREIGN KEY (id_cosecha) REFERENCES cosechas(id_cosecha) ON DELETE CASCADE    
);

CREATE TABLE insumos (    
    id_insumo INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,    
    nombre TEXT NOT NULL,    
    categoria TEXT NOT NULL,    
    unidad TEXT NOT NULL,  
    icono TEXT,  
    activo INTEGER NOT NULL DEFAULT 1  
);

CREATE TABLE campania_insumo (    
    id_campania_insumo INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,    
    id_campania INTEGER NOT NULL,    
    id_insumo INTEGER NOT NULL,    
    cantidad REAL NOT NULL,    
    precio REAL NOT NULL,    
    FOREIGN KEY (id_campania) REFERENCES campanias(id_campania) ON DELETE CASCADE,    
    FOREIGN KEY (id_insumo) REFERENCES insumos(id_insumo) ON DELETE CASCADE    
);

-- =========================================================    
-- ÍNDICES (Para optimizar consultas frecuentes)    
-- =========================================================

CREATE INDEX index_tareas_id_campania ON tareas(id_campania);    
CREATE INDEX index_observaciones_id_campania ON observaciones(id_campania);    
CREATE INDEX index_cosechas_id_campania ON cosechas(id_campania);    
CREATE INDEX index_cosechas_no_almacenadas_id_cosecha ON cosechas_no_almacenadas(id_cosecha);    
CREATE INDEX index_campania_insumo_id_campania ON campania_insumo(id_campania);    
CREATE INDEX index_campania_insumo_id_insumo ON campania_insumo(id_insumo);  
CREATE UNIQUE INDEX index_campania_insumo_id_campania_id_insumo ON campania_insumo(id_campania, id_insumo);
```

## Documentación Técnica (Entrega Completa)

### Arquitectura del Sistema
*(A completar con la arquitectura Clean Architecture de la app).*

### Stack Tecnológico
*(A completar con Kotlin, Jetpack Compose, Room, Hilt, etc).*

### Estructura del Proyecto (Módulos)
*(A completar).*

### Diagramas UML

#### Diagrama de Contexto
*[INSERTAR DIAGRAMA UML AQUÍ]*

#### Diagrama de Casos de Uso
*[INSERTAR DIAGRAMA UML AQUÍ]*

#### Diagrama de Clases
*[INSERTAR DIAGRAMA UML AQUÍ]*

#### Diagrama Entidad-Relación (E/R)
*[INSERTAR DIAGRAMA UML AQUÍ]*

### Descripción Técnica de Funcionalidades
*(A completar).*

## Manual de Usuario

### Guía de Instalación y Ejecución
*(A completar).*

### Guía de Uso Paso a Paso (Pantallas)
*(A completar con capturas).*

### Resolución de Errores Comunes
*(A completar).*

## Conclusiones y Trabajo Futuro

### 9.1 Conclusiones

#### 9.1.1 Síntesis del proyecto y cumplimiento de objetivos

El presente trabajo integrador tuvo como punto de partida una necesidad concreta y real: el establecimiento "Don Elio" gestionaba su actividad agrícola a través de registros en papel y planillas de Excel, lo que generaba pérdida de información, dificultades para consultar el historial productivo y ausencia total de herramientas para la toma de decisiones. Frente a esa realidad, nos propusimos desarrollar un sistema de gestión móvil nativo para Android, orientado exclusivamente a las necesidades del agrónomo-productor, que centralizara y digitalizara la operatoria cotidiana del campo.

El resultado del desarrollo es una aplicación funcional, completa y operativa, que cubre el ciclo productivo agrícola de punta a punta: desde el alta de una campaña con su cultivo asociado, pasando por el registro de insumos aplicados, tareas programadas y observaciones de campo, hasta el registro de cosechas, la generación de reportes analíticos y la exportación de datos para respaldo.

A continuación se verifica el grado de cumplimiento de cada objetivo específico planteado en la sección 4.2:

| Objetivo | Estado | Módulo/Fase |
|---|---|---|
| Diseñar un módulo para registrar y gestionar campañas de cultivo | ✅ Cumplido | Gestión de Campañas (Fase 2) |
| Registrar el rendimiento de las cosechas y almacenar productos obtenidos | ✅ Cumplido | Gestión de Cosechas (Fase 3) |
| Permitir la carga de notas y fotos como observaciones | ✅ Cumplido | Observaciones (Fase 4) |
| Desarrollar un historial detallado de cultivos y consumos por campaña | ✅ Cumplido | Campañas + Insumos (Fases 2–3) |
| Registrar y controlar los egresos de insumos por campaña | ✅ Cumplido | Gestión de Insumos (Fase 3) |
| Generar reportes automáticos de producción, insumos y costos | ✅ Cumplido | Reportes y Análisis (Fase 4) |
| Proveer un panel de control con estadísticas e indicadores clave | ✅ Cumplido | Dashboard (Fase 1) |
| Establecer recordatorios asociados a tareas y fechas de campaña | ✅ Cumplido | Tareas + WorkManager (Fases 2 y 4) |
| Analizar datos históricos para proyectar necesidades a futuro | ⚠️ Parcialmente cumplido | Reportes — visualización histórica sin motor predictivo |

El único objetivo que no fue implementado en su totalidad corresponde a la **proyección automatizada de necesidades** (RF20): si bien la pantalla de Reportes permite visualizar la evolución histórica de insumos y rendimientos por campaña —información suficiente para que el productor realice sus propias estimaciones—, no se desarrolló un algoritmo que calcule automáticamente las cantidades proyectadas para ciclos futuros. Esta decisión estuvo condicionada por el tiempo disponible de desarrollo y se retoma en la sección de Trabajo Futuro.

---

#### 9.1.2 Decisiones técnicas y aprendizajes

El proceso de desarrollo nos permitió consolidar conocimientos y tomar decisiones de diseño que consideramos valiosas de documentar, tanto por su impacto en la calidad del producto final como por los aprendizajes que dejaron.

**Arquitectura Clean Architecture**

La adopción de la arquitectura de tres capas (Presentation → Domain → Data) fue sin dudas la decisión técnica de mayor impacto positivo. Al aislar la lógica de negocio en casos de uso independientes del framework de Android, cada módulo pudo ser desarrollado, modificado y verificado de forma autónoma. Esta separación también facilitó que múltiples pantallas reutilizaran los mismos casos de uso sin duplicación de lógica: `ObtenerCampaniasActivasUseCase`, por ejemplo, es consumido tanto por el Dashboard como por el módulo de Gestión de Campañas.

**Programación reactiva con Kotlin Flows y StateFlow**

La integración de Room con Kotlin Flows creó una cadena reactiva completa, desde la base de datos hasta la interfaz de usuario. Cualquier escritura en la base de datos (nueva tarea, nuevo insumo, cosecha editada) se propaga automáticamente a todas las pantallas suscritas sin necesidad de notificaciones manuales ni actualizaciones explícitas. Esto redujo significativamente la superficie de bugs relacionados con estados desactualizados en la UI.

**Modelo de datos centrado en Campaña**

El diseño del esquema relacional con `Campania` como entidad raíz, y todas las entidades relacionadas (Tarea, Cosecha, Observacion, CampaniaInsumo) vinculadas mediante claves foráneas con restricción `ON DELETE CASCADE`, garantizó la integridad referencial de forma declarativa. La eliminación de una campaña limpia automáticamente todos sus datos asociados, sin lógica adicional en la capa de aplicación.

**Enfoque offline-first**

La decisión de operar exclusivamente sobre una base de datos SQLite local, sin dependencia de internet, fue determinante para satisfacer el requerimiento de portabilidad del sistema. El productor puede utilizar la aplicación en el campo, sin cobertura de señal, con la misma funcionalidad que en la oficina. La exportación de la base de datos como mecanismo de respaldo manual cubre la necesidad de protección ante pérdida del dispositivo dentro del alcance definido.

**Gestión de sesión y contexto de navegación**

Dos soluciones de diseño resolvieron problemas concretos de experiencia de usuario. El `SessionManager` basado en DataStore Preferences permite que, al reabrir la aplicación, el usuario autenticado acceda directamente al Dashboard sin repetir el proceso de login. El `UltimaSeleccionManager` asegura que al navegar desde el detalle de una campaña hacia sus tareas, insumos o cosechas, la pantalla destino muestre automáticamente los datos de esa campaña, sin requerir una nueva selección por parte del usuario.

**Seguridad de credenciales**

La decisión de implementar el hasheo SHA-256 de contraseñas en la capa Domain —y no en la capa Data— garantiza que el algoritmo de seguridad sea independiente del mecanismo de almacenamiento. Si en el futuro se cambia el motor de base de datos, la lógica de hasheo no se ve afectada.

---

#### 9.1.3 Limitaciones y desafíos encontrados

Durante el desarrollo identificamos un conjunto de limitaciones, algunas previstas desde el inicio del proyecto en la sección de Alcance y Límites, y otras surgidas durante la implementación:

**Limitaciones previstas en el alcance**

- El sistema opera con un **único perfil de usuario administrador**, sin soporte para múltiples roles con distintos niveles de acceso. Esta decisión fue deliberada: la incorporación de roles (peón, contador) habría incrementado significativamente la complejidad del sistema, excediendo los tiempos disponibles para el proyecto.
- No se contempló la **sincronización entre dispositivos** ni el almacenamiento en la nube. El respaldo de datos depende de la exportación manual de la base de datos por parte del usuario.
- Las funcionalidades exclusivas del **sector pecuario** (feedlot) del establecimiento quedaron fuera del alcance, priorizando el área agrícola como eje del sistema.

**Limitaciones identificadas durante el desarrollo**

- La **limpieza de imágenes huérfanas** almacenadas en el directorio interno de la aplicación (`filesDir`) no se realiza automáticamente al eliminar una observación. Los archivos de imagen permanecen en el sistema de archivos del dispositivo hasta que el usuario desinstale la aplicación o se implemente una tarea de limpieza periódica.
- El módulo de **proyecciones** implementado ofrece visualización histórica (gráfico de evolución por campaña) pero no genera estimaciones automáticas para campañas futuras.
- El módulo de Reportes hace uso de una API experimental de Jetpack Compose (`ExperimentalLayoutApi` para `FlowRow`), lo que podría implicar cambios de API en futuras versiones del framework.

---

### 9.2 Trabajo Futuro

El sistema desarrollado sienta las bases de una plataforma de gestión agrícola extensible. A continuación se detallan las mejoras y ampliaciones identificadas como más relevantes, organizadas por prioridad e impacto para el establecimiento.

---

#### 9.2.1 Nuevas funcionalidades (alta prioridad)

**Módulo de actividad pecuaria (feedlot)**

El establecimiento "Don Elio" opera tanto en el área agrícola como en el sector de engorde a corral. Incorporar un módulo que permita registrar ingresos y egresos de hacienda, consumo de alimento balanceado, evolución de peso por lote y resultados de cada ciclo de engorde completaría la visión integral de gestión del establecimiento.

**Múltiples roles de usuario**

Extender el sistema de autenticación para soportar, al menos, dos roles diferenciados:
- **Peón / Operario**: acceso a tareas asignadas y registro de novedades de campo, sin acceso a datos económicos.
- **Contador / Administrador externo**: acceso de solo lectura a reportes de costos e ingresos, sin posibilidad de modificar datos productivos.

**Sincronización y respaldo automático en la nube**

Implementar un mecanismo de backup automático hacia Google Drive o un servidor propio, que se ejecute periódicamente mediante WorkManager cuando el dispositivo tenga conexión a internet. Esto eliminaría la dependencia del usuario de recordar realizar el respaldo manual y protegería ante la pérdida o rotura del dispositivo.

**Motor de proyecciones y planificación**

Desarrollar un algoritmo que, a partir del historial de insumos utilizados, rendimientos obtenidos y superficie trabajada por cultivo, genere estimaciones de necesidades para la campaña siguiente. Por ejemplo: dado que en los últimos tres ciclos de soja se utilizaron en promedio X kg/ha de herbicida Y, el sistema podría sugerir la cantidad total necesaria para la superficie planificada.

---

#### 9.2.2 Mejoras sobre funcionalidades existentes

**Limpieza automática de imágenes huérfanas**

Implementar una tarea periódica con WorkManager que cruce los archivos de imagen almacenados en `filesDir` contra las rutas registradas en la base de datos, y elimine automáticamente aquellos archivos sin referencia activa. Esta mejora resuelve la limitación de limpieza diferida documentada en la fase de Observaciones.

**Notificaciones enriquecidas con acciones directas**

Extender las notificaciones de recordatorio de tareas para incluir botones de acción directa: "Confirmar como realizada" y "Posponer 1 hora", permitiendo al usuario interactuar con la tarea sin necesidad de abrir la aplicación.

**Exportación de reportes en formato PDF con diseño profesional**

Mejorar la generación de reportes en PDF incorporando una plantilla con el membrete del establecimiento, tablas formateadas y gráficos embebidos, produciendo un documento apto para presentar ante asesores, bancos o entidades de crédito agrario.

**Historial de precios de insumos**

Registrar el precio de mercado de cada insumo en distintas fechas, independientemente de su uso en una campaña específica. Esto permitiría analizar la variación de costos en el tiempo y mejorar la planificación financiera de la campaña siguiente.

**Gráficos comparativos inter-anuales**

Incorporar en el módulo de Reportes una vista que compare el rendimiento (Tn/Ha), el costo total y el balance neto de campañas del mismo cultivo a lo largo de múltiples años, facilitando la identificación de tendencias productivas.

---

#### 9.2.3 Mejoras técnicas y de calidad

**Cobertura de testing automatizado**

Ampliar la suite de pruebas automatizadas para incluir Unit Tests sobre todos los casos de uso críticos (validaciones de formularios, cálculos de rendimiento y balance financiero) e Instrumented Tests sobre los DAOs de Room, verificando el comportamiento de las consultas con datos reales en una base de datos de prueba en memoria.

**Estrategia formal de migraciones de base de datos**

La base de datos se encuentra actualmente en versión 1. Al incorporar nuevas funcionalidades que modifiquen el esquema (nuevas tablas, columnas o relaciones), será necesario implementar migraciones de Room (`Migration(from, to)`) para garantizar que los usuarios existentes no pierdan sus datos al actualizar la aplicación.

**Soporte multilenguaje**

Externalizar todas las cadenas de texto de la interfaz al archivo `strings.xml` y preparar traducciones al inglés, como primer paso hacia una eventual internacionalización del producto para otros contextos productivos fuera de Argentina.

**Actualización de dependencias experimentales**

Reemplazar el uso de `ExperimentalLayoutApi` (`FlowRow`) por la API estable equivalente una vez que Jetpack Compose la promueva a estable, para evitar incompatibilidades en futuras actualizaciones del SDK.

## Anexos
*(A completar).*
