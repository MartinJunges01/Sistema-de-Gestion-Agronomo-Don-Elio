# Bugs Conocidos e Identificados (Iteración Actual)

Este documento registra los bugs identificados en el sistema que están pendientes de ser resueltos en próximas iteraciones. Están estructurados como Issues listos para ser incorporados al backlog del proyecto.

---

## Issue 1: Tareas no clickeables en el Dashboard

**Descripción**
En la pantalla de inicio (`DashboardOperacionesScreen`), la sección de "Tareas Próximas" renderiza una lista de tareas pendientes. Sin embargo, el componente visual `Card` que envuelve cada tarea no tiene configurado ningún evento de clic, lo que impide que el usuario pueda navegar a los detalles de la campaña o interactuar con la tarea directamente desde el inicio.

**Acceptance Criteria (Criterios de Aceptación)**
- Al tocar cualquier tarea de la lista "Tareas Próximas" en el Dashboard, la aplicación debe navegar a la pantalla de detalle correspondiente a la campaña de esa tarea.
- El componente debe proveer feedback visual (ripple effect) al ser presionado.

**Sub-issues / Tareas Técnicas**
- [ ] En `DashboardOperacionesScreen.kt`, ubicar el `Card` de la lista `items(tareas)`.
- [ ] Agregar el modificador `.clickable { onGoToDetalle(tarea.idCampania) }` al `Modifier` del `Card`.

---

## Issue 2: Tareas nuevas no aparecen en la pestaña Tareas (Desincronización de Timestamps)

**Descripción**
Las tareas creadas por el usuario no son visibles en la pestaña "Tareas" a pesar de que se seleccionan el día y la campaña correctos en el calendario. 
El calendario y las consultas a la base de datos (`TareaDao`) buscan coincidencias **exactas** truncadas a la medianoche (ej: `2026-06-01 00:00:00.000`). Sin embargo, el caso de uso `CrearTareaUseCase` guarda el timestamp crudo proveniente de la interfaz, el cual incluye las horas, minutos y milisegundos exactos del momento de creación. Al hacer un `WHERE fecha = :fecha`, la base de datos nunca encuentra una coincidencia.

**Acceptance Criteria (Criterios de Aceptación)**
- Al crear una tarea para una fecha determinada, debe aparecer inmediatamente en la pestaña "Tareas" al seleccionar ese día en el `CalendarioSemanal`.
- Las tareas guardadas en la base de datos deben tener su timestamp de fecha normalizado a las 00:00:00.000 (medianoche local/UTC) independientemente de la hora a la que se use el formulario.

**Sub-issues / Tareas Técnicas**
- [ ] Modificar `CrearTareaUseCase.kt`: Normalizar el parámetro `fecha` a medianoche antes de instanciar el modelo `Tarea` y mandarlo al repositorio.
- [ ] Modificar `EditarTareaUseCase.kt`: Aplicar la misma normalización al recibir el objeto `Tarea` antes de hacer el `update`.

---

## Issue 3: Lógica de Límite y Ordenamiento oculta tareas nuevas en el Dashboard

**Descripción**
El Dashboard pide las tareas pendientes utilizando la consulta `getTareasPendientesGlobales(limite: Int)` en el DAO con un `LIMIT 3` y ordenado `ORDER BY fecha ASC`. Como los datos semilla (`DataSeeder`) inyectan tareas pendientes con fecha de **Enero de 2026**, estas son las más "viejas" en el sistema. Cualquier tarea real creada hoy (Ej: Junio 2026) queda en la posición 4 en adelante y nunca se muestra en el Dashboard porque las de prueba "atrasadas" consumen el cupo de 3.

**Acceptance Criteria (Criterios de Aceptación)**
- El Dashboard debe mostrar las 3 tareas pendientes **más próximas desde el día de hoy en adelante** (ignorando tareas muy vencidas, o dándoles un tratamiento visual distinto).
- (Alternativa de negocio) Borrar las tareas del `DataSeeder` de enero para que las tareas actuales asuman las primeras posiciones.

**Sub-issues / Tareas Técnicas**
- [ ] Analizar si se desea modificar la query de `TareaDao` para que haga `WHERE confirmar = 0 AND fecha >= :hoy LIMIT 3`.
- [ ] O simplemente borrar los datos del `DataSeederImpl` de prueba para que no generen ruido en el entorno real del usuario.

---

## Issue 4: Visualización genérica de Insumos Vinculados tras Soft-Delete en Catálogo

**Descripción**
Al borrar un Insumo del Catálogo (Soft-Delete seteando `activo = false`), si ese insumo ya estaba vinculado a una Campaña, la tarjeta de la sección "Insumos Ya Vinculados" no encuentra el nombre original del insumo y renderiza un nombre genérico como `Insumo #6`. Esto se debe a que la consulta que une la tabla intermedia con el catálogo probablemente está ignorando los insumos inactivos, o la UI está recibiendo un valor nulo para el nombre y aplicando un texto por defecto.

**Acceptance Criteria (Criterios de Aceptación)**
- Al visualizar los insumos vinculados a una campaña, si un insumo fue borrado lógicamente del catálogo, la tarjeta debe seguir mostrando el nombre original del insumo (ej: "Semilla Soja") posiblemente con un indicador visual (ej: "(Eliminado del catálogo)").
- La información histórica de la campaña no debe perder integridad visual por modificaciones posteriores en el catálogo.

**Sub-issues / Tareas Técnicas**
- [ ] Revisar la consulta SQL en el DAO encargada de obtener los `InsumosVinculados` para asegurar que el `JOIN` incluya a los `InsumoEntity` independientemente de su estado `activo`.
- [ ] O alternativamente, revisar la UI/ViewModel en `CampaniaInsumoScreen` para ver por qué el nombre falla y cae en el fallback "Insumo #ID".

---

## Issue 5: Datos Mockeados Residuales en UI (Dashboard y Reportes)

**Descripción**
Aunque gran parte del sistema ya se ha conectado a la base de datos real, aún persisten componentes en la interfaz que renderizan datos "mockeados" (duros).
- En `DashboardOperacionesScreen`: Las tarjetas de "Clima" y "Salud Lotes" contienen texto estático.
- En `ReportesRendimientoScreen`: El componente comparativo de campañas tiene la lista de campañas hardcodeada (`listOf("Campaña Soja 2026", ...)`), así como los valores de rendimiento, ganancias, costos, insumos totales y los datos del gráfico de evolución mensual (`Canvas`). El único dato real es el del gráfico de dona (`PieChart`).

**Acceptance Criteria (Criterios de Aceptación)**
- El Dashboard debe alimentar las tarjetas de Clima y Salud de Lotes con datos dinámicos (o bien ocultarlas/reemplazarlas si no se planea integrar una API de clima por el momento).
- La pantalla de Reportes debe alimentar el selector de campañas con la lista real de la base de datos.
- Las métricas comparativas y el gráfico de evolución en Reportes deben calcularse utilizando datos reales de cosechas e insumos.

**Sub-issues / Tareas Técnicas**
- [ ] En `DashboardOperacionesScreen`: Extraer los valores estáticos hacia el ViewModel o esconder los componentes.
- [ ] En `ReportesRendimientoScreen`: Integrar el dropdown con campañas reales.
- [ ] En `ReportesViewModel`: Crear lógica para calcular costos y ganancias de campañas para alimentar las tarjetas comparativas y el Canvas.
