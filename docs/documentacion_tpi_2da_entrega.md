# Sistema de Gestión Agrónomo "Don Elio"

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
