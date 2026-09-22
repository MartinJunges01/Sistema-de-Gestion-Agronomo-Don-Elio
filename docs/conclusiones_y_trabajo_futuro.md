## 9. Conclusiones y Trabajo Futuro

---

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
- El módulo de **proyecciones** no genera estimaciones automáticas de necesidades para campañas futuras. Si bien se exploró la implementación de un gráfico de evolución histórica de rendimiento por cultivo —incluyendo el modelo de datos necesario—, la solución fue descartada por no alcanzar el nivel de calidad visual requerido, quedando pendiente para una iteración futura con mejor enfoque de presentación.
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

**Historial de precios de insumos**

Registrar el precio de mercado de cada insumo en distintas fechas, independientemente de su uso en una campaña específica. Esto permitiría analizar la variación de costos en el tiempo y mejorar la planificación financiera de la campaña siguiente.

**Gráfico de evolución histórica por cultivo**

Durante el desarrollo se exploró la implementación de un gráfico que mostrara la evolución del rendimiento (Tn/Ha) a lo largo de las campañas de un mismo cultivo. La infraestructura de datos necesaria fue incorporada —incluyendo el catálogo de cultivos como entidad independiente y el caso de uso `ObtenerEvolucionCultivoUseCase`—, pero la visualización fue descartada en esta etapa por no alcanzar el nivel de legibilidad y calidad visual esperado. Como trabajo futuro se propone retomar esta funcionalidad con una biblioteca de gráficos más expresiva o con un diseño de Canvas más cuidado, ya que aporta valor analítico directo al productor para comparar el desempeño de sus cultivos entre campañas.

---

#### 9.2.3 Mejoras técnicas y de calidad

**Ampliación de cobertura de testing**

El proyecto cuenta con una suite de tests que cubre casos de uso, ViewModels y DAOs. Como línea de trabajo futuro, se propone ampliar la cobertura hacia los módulos de menor cobertura actual —en particular los ViewModels de Reportes y el componente `ReportExporter`— e incorporar tests de integración que verifiquen el flujo completo desde el caso de uso hasta el repositorio.

**Exportación de esquema de base de datos**

Habilitar `exportSchema = true` en la anotación `@Database` y versionar los archivos de esquema generados junto al código fuente. Esto permitirá detectar cambios accidentales en el modelo de datos durante el proceso de revisión de código (pull requests), complementando las migraciones Room ya implementadas.

**Soporte multilenguaje**

Externalizar todas las cadenas de texto de la interfaz al archivo `strings.xml` y preparar traducciones al inglés, como primer paso hacia una eventual internacionalización del producto para otros contextos productivos fuera de Argentina.

**Actualización de dependencias experimentales**

Reemplazar el uso de `ExperimentalLayoutApi` (`FlowRow`) por la API estable equivalente una vez que Jetpack Compose la promueva a estable, para evitar incompatibilidades en futuras actualizaciones del SDK.

---

*Fin de la sección 9.*
