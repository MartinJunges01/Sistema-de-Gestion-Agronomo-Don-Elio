# Casos de Uso - Sistema de Gestión Agrónomo Don Elio

### CU-01 Iniciar Sesión

**Descripción:** El usuario (Propietario/Agrónomo) ingresa al sistema validando sus credenciales para acceder a la funcionalidad principal.
**Actores:** Usuario
**Pre condiciones:** El usuario debe tener una cuenta previamente registrada en el sistema.
**Post condiciones:** El sistema valida las credenciales y guarda la sesión iniciada del usuario.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Ingresa su nombre de usuario y contraseña | Valida que los campos no estén vacíos |
| 2 | Solicita iniciar sesión | Cifra la contraseña (SHA-256), busca al usuario en la base de datos y compara el hash |
| 3 | | Inicia sesión, guarda el nombre de usuario localmente (GuardarSesionUseCase) y redirige a la pantalla principal |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Deja el nombre de usuario o contraseña vacíos | Muestra mensaje de error: "El nombre de usuario/contraseña no puede estar vacío" |
| 2 | Ingresa credenciales incorrectas | Muestra error de inicio de sesión indicando que no se encontró coincidencia |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Cada vez que el usuario ingresa a la aplicación sin sesión activa.
**Importancia:** Vital
**Urgencia:** Inmediatamente
**Comentarios:** Utiliza SHA-256 para el hasheo de la contraseña.


### CU-02 Registrar Usuario

**Descripción:** Un nuevo usuario se registra en la aplicación proporcionando su nombre completo, nombre de usuario y una contraseña.
**Actores:** Usuario nuevo
**Pre condiciones:** El nombre de usuario elegido no debe existir en la base de datos.
**Post condiciones:** Se crea una nueva cuenta de usuario en el sistema.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Ingresa nombre completo, nombre de usuario y contraseña | Valida que los campos no estén vacíos y que la contraseña tenga al menos 4 caracteres |
| 2 | Solicita registrarse | Verifica que el nombre de usuario no exista. Cifra la contraseña (SHA-256) |
| 3 | | Crea la cuenta guardándola en la base de datos local y notifica el éxito |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Deja algún campo vacío | Lanza excepción y muestra: "El nombre completo/usuario no puede estar vacío" |
| 2 | Ingresa una contraseña menor a 4 caracteres | Muestra error: "La contraseña debe tener al menos 4 caracteres" |
| 3 | Ingresa un nombre de usuario ya existente | Muestra error: "El nombre de usuario ya existe" |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Una vez por cada usuario nuevo.
**Importancia:** Vital
**Urgencia:** Inmediatamente
**Comentarios:** Guarda la contraseña de forma segura usando SHA-256.


### CU-03 Cerrar Sesión

**Descripción:** El usuario cierra su sesión actual por seguridad o para cambiar de cuenta.
**Actores:** Usuario
**Pre condiciones:** Debe haber una sesión de usuario activa.
**Post condiciones:** La sesión se elimina localmente y el usuario debe iniciar sesión nuevamente para acceder.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Selecciona la opción de cerrar sesión | Elimina los datos de la sesión actual del almacenamiento local (SessionManager) |
| 2 | | Redirige a la pantalla de Inicio de Sesión |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Falla al acceder a las preferencias de sesión | Muestra error genérico |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** A discreción del usuario.
**Importancia:** Importante
**Urgencia:** Puede esperar
**Comentarios:** Se borra el registro de sesión del SessionManager.



**Relaciones:** <<extend>> a CU-01 Iniciar Sesión
### CU-10 Crear Cultivo

**Descripción:** El usuario registra un nuevo tipo de cultivo en el sistema.
**Actores:** Usuario
**Pre condiciones:** El nombre del cultivo no debe estar vacío.
**Post condiciones:** El nuevo cultivo es almacenado con estado activo (`activo = true`).

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Ingresa el nombre del nuevo cultivo | Valida que el nombre no esté en blanco |
| 2 | Solicita guardar el cultivo | Crea la entidad Cultivo, establece activo=true y la guarda en la base de datos |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Deja el nombre en blanco | Lanza excepción: "El nombre del cultivo no puede estar vacío" y muestra error |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Ocasional.
**Importancia:** Importante
**Urgencia:** Puede esperar
**Comentarios:** Los cultivos se crean activos por defecto.



**Relaciones:** <<extend>> a CU-13 Consultar Cultivos
### CU-11 Editar Cultivo

**Descripción:** El usuario modifica el nombre de un cultivo existente.
**Actores:** Usuario
**Pre condiciones:** El cultivo debe existir en el sistema.
**Post condiciones:** El cultivo se actualiza con el nuevo nombre.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Selecciona un cultivo y edita su nombre | Valida que el nombre modificado no esté en blanco |
| 2 | Solicita guardar cambios | Actualiza el registro en la base de datos con el nuevo nombre |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Deja el nombre en blanco | Lanza excepción: "El nombre del cultivo no puede estar vacío" |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Baja.
**Importancia:** Importante
**Urgencia:** Puede esperar
**Comentarios:** Solo permite modificar el nombre del cultivo.



**Relaciones:** <<extend>> a CU-13 Consultar Cultivos
### CU-12 Eliminar Cultivo

**Descripción:** El usuario elimina un cultivo del sistema.
**Actores:** Usuario
**Pre condiciones:** El cultivo debe existir.
**Post condiciones:** El cultivo es eliminado.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Solicita eliminar un cultivo específico | Invoca el borrado del cultivo en la base de datos |
| 2 | Confirma eliminación | Elimina el registro físico o lógico (según implementación DAO) |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Intenta eliminar un cultivo asociado a campañas | Muestra error de clave foránea o dependencia en la interfaz |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Baja.
**Importancia:** Importante
**Urgencia:** Puede esperar
**Comentarios:** Elimina el registro utilizando el repositorio. Puede ser Hard o Soft delete dependiendo del backend/DAO, pero se invoca explícitamente `deleteCultivo`.



**Relaciones:** <<extend>> a CU-13 Consultar Cultivos
### CU-13 Consultar Cultivos

**Descripción:** El usuario visualiza la lista de cultivos disponibles.
**Actores:** Usuario
**Pre condiciones:** Ninguna.
**Post condiciones:** Se retorna el listado de cultivos.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Accede a la pantalla de cultivos | Consulta la base de datos según el filtro (activos o todos) |
| 2 | | Retorna un flujo (Flow) con la lista y la muestra en pantalla |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | No hay cultivos registrados | Muestra la lista vacía o un mensaje indicando que no hay datos |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Alta.
**Importancia:** Importante
**Urgencia:** Inmediatamente
**Comentarios:** Permite filtrar entre cultivos activos (por defecto) o todos (incluyendo inactivos lógicos).



**Relaciones:** <<include>> a CU-01 Iniciar Sesión
### CU-20 Crear Campaña

**Descripción:** El usuario registra una nueva campaña agrícola, asignando nombre, hectáreas, fecha de inicio y cultivo asociado.
**Actores:** Usuario
**Pre condiciones:** El nombre no debe estar vacío.
**Post condiciones:** La campaña se guarda en estado activa y se notifica el resultado.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Ingresa nombre, hectáreas, fecha y selecciona el cultivo | Emite estado de carga (Loading). Valida que el nombre no sea vacío |
| 2 | Confirma creación | Persiste la campaña con `estaActiva = true` y emite estado de éxito |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Deja el nombre en blanco | Lanza excepción y emite error: "El nombre de la campaña no puede estar vacío" |
| 2 | Falla la inserción | Emite estado de Error con el mensaje de la excepción |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Media.
**Importancia:** Vital
**Urgencia:** Inmediatamente
**Comentarios:** Devuelve un `Flow<Resource<Unit>>` para el manejo reactivo.



**Relaciones:** <<extend>> a CU-24 Consultar Campañas
### CU-21 Editar Campaña

**Descripción:** El usuario modifica los datos de una campaña existente.
**Actores:** Usuario
**Pre condiciones:** El nombre no debe estar vacío.
**Post condiciones:** Se actualiza el registro en la base de datos.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Modifica los datos de la campaña | Emite estado Loading. Valida el nombre |
| 2 | Guarda los cambios | Actualiza la campaña en la base de datos y emite Success |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | El nuevo nombre está vacío | Emite estado de Error: "El nombre de la campaña no puede estar vacío" |
| 2 | Error de persistencia | Emite estado de Error con el mensaje de excepción |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Media.
**Importancia:** Importante
**Urgencia:** Puede esperar
**Comentarios:** Emite estados reactivos hacia la UI.



**Relaciones:** <<extend>> a CU-24 Consultar Campañas
### CU-22 Eliminar Campaña (Física)

**Descripción:** El usuario elimina por completo una campaña del sistema (Hard Delete).
**Actores:** Usuario
**Pre condiciones:** La campaña debe existir.
**Post condiciones:** La campaña y potencialmente todos sus registros asociados son eliminados físicamente.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Solicita eliminar la campaña | Emite estado Loading |
| 2 | Confirma la eliminación | Ejecuta borrado físico (`deleteCampania`) y emite Success |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Error de base de datos | Emite estado de Error detallando el fallo |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Baja.
**Importancia:** Vital
**Urgencia:** Puede esperar
**Comentarios:** Esta es una acción destructiva de borrado duro.



**Relaciones:** <<extend>> a CU-24 Consultar Campañas
### CU-23 Finalizar Campaña (Lógica)

**Descripción:** El usuario finaliza una campaña. Esta deja de estar activa pero su historial se conserva (Soft Delete).
**Actores:** Usuario
**Pre condiciones:** La campaña debe estar en curso.
**Post condiciones:** La campaña pasa a estado inactivo (`estaActiva = false`).

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Solicita finalizar la campaña | Emite Loading |
| 2 | Confirma la acción | Cambia la propiedad `estaActiva` a `false`, actualiza la campaña en BD y emite Success |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Falla al guardar | Emite Error indicando el problema |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Media.
**Importancia:** Importante
**Urgencia:** Puede esperar
**Comentarios:** Esta acción representa un Soft Delete o archivado; mantiene el registro para consultas.



**Relaciones:** <<extend>> a CU-24 Consultar Campañas
### CU-24 Consultar Campañas

**Descripción:** El usuario consulta el listado de campañas (todas, activas o inactivas) o el detalle de una en particular.
**Actores:** Usuario
**Pre condiciones:** Ninguna.
**Post condiciones:** El sistema retorna los datos solicitados en tiempo real.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Accede a la vista de campañas | El sistema invoca el caso de uso correspondiente |
| 2 | | Retorna un Flow con la información y actualiza la UI |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Sin coincidencias | Muestra una vista vacía o mensaje informativo |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Alta.
**Importancia:** Vital
**Urgencia:** Inmediatamente
**Comentarios:** Agrupa UseCases de lectura.



**Relaciones:** <<include>> a CU-01 Iniciar Sesión
### CU-30 Crear Tarea

**Descripción:** El usuario crea una tarea o recordatorio asociado a una campaña agrícola.
**Actores:** Usuario
**Pre condiciones:** El nombre no debe estar vacío.
**Post condiciones:** La tarea es guardada y se programa una alarma si fue solicitada.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Ingresa nombre, fecha, hora, flag de notificación y selecciona campaña | Emite Loading. Valida nombre no vacío. Normaliza la fecha a 00:00:00 |
| 2 | Guarda la tarea | Persiste, obtiene nuevo ID. Si notificar=true, agenda alarma y emite Success |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Nombre vacío | Emite Error: "El nombre de la tarea no puede estar vacío" |
| 2 | Falla de BD/Scheduler | Emite Error |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Alta.
**Importancia:** Vital
**Urgencia:** Inmediatamente
**Comentarios:** Las fechas se normalizan para eliminar la hora y facilitar las comparaciones.



**Relaciones:** <<extend>> a CU-34 Consultar Tareas
### CU-31 Editar Tarea

**Descripción:** El usuario modifica la información de una tarea existente.
**Actores:** Usuario
**Pre condiciones:** El nombre no debe estar vacío.
**Post condiciones:** La tarea se actualiza y la alarma se reprograma o cancela.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Modifica los datos de la tarea | Emite Loading. Valida nombre y normaliza fecha |
| 2 | Guarda los cambios | Actualiza en BD. Reprograma o cancela alarma. Emite Success |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Nombre vacío | Emite Error |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Media.
**Importancia:** Importante
**Urgencia:** Puede esperar
**Comentarios:** Administra dinámicamente el estado del scheduler de alarmas al editar.



**Relaciones:** <<extend>> a CU-34 Consultar Tareas
### CU-32 Eliminar Tarea

**Descripción:** El usuario elimina una tarea del sistema (Hard Delete).
**Actores:** Usuario
**Pre condiciones:** La tarea debe existir.
**Post condiciones:** La tarea es eliminada físicamente y su alarma es cancelada.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Solicita eliminar la tarea | Emite Loading |
| 2 | Confirma eliminación | Borra el registro en BD, cancela su alarma agendada y emite Success |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Error de borrado | Emite Error |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Media.
**Importancia:** Importante
**Urgencia:** Puede esperar
**Comentarios:** Es un borrado físico y es crucial cancelar la alarma en el SO.



**Relaciones:** <<extend>> a CU-34 Consultar Tareas
### CU-33 Confirmar Tarea

**Descripción:** El usuario marca una tarea como completada o revierte su estado a pendiente.
**Actores:** Usuario
**Pre condiciones:** La tarea debe existir.
**Post condiciones:** La tarea cambia su estado y si es completada, su alarma se cancela.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Marca/desmarca la casilla de completado | Emite Loading |
| 2 | | Llama a BD actualizando el booleano. Si completada=true, cancela alarma. Emite Success |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Error de conexión | Emite Error |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Alta.
**Importancia:** Vital
**Urgencia:** Inmediatamente
**Comentarios:** Las alarmas se cancelan de manera definitiva al completar la tarea.



**Relaciones:** <<extend>> a CU-34 Consultar Tareas
### CU-34 Consultar Tareas

**Descripción:** El usuario consulta la lista de tareas aplicando diferentes filtros (del día, filtradas, pendientes, por campaña).
**Actores:** Usuario
**Pre condiciones:** Ninguna.
**Post condiciones:** Se despliegan las tareas correspondientes.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Accede a las vistas de agenda o detalle de campaña | Llama al UseCase correspondiente |
| 2 | | Retorna un Flow de Tareas y las despliega en la UI |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Sin resultados | Muestra lista vacía |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Muy Alta.
**Importancia:** Vital
**Urgencia:** Inmediatamente
**Comentarios:** Agrupa varios casos de uso de consulta.


# Casos de Uso - Módulos: Cosechas, Insumos, Observaciones, Reportes y Backups

## Cosechas (CU-40 - CU-49)


**Relaciones:** <<include>> a CU-01 Iniciar Sesión
### CU-40 Registrar Cosecha

**Descripción:** El usuario registra una nueva cosecha (cantidad, fecha, almacén) asociada a una campaña para mantener el control del rendimiento.
**Actores:** Propietario / Administrador
**Pre condiciones:** Debe existir una campaña y los datos de cantidad deben ser mayores a cero.
**Post condiciones:** Se guarda un nuevo registro de Cosecha en la base de datos.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Ingresa cantidad, fecha, almacén y campaña, y solicita guardar. | Valida datos. Guarda la cosecha en BD y muestra mensaje de éxito. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Ingresa cantidad <= 0 o vacía. | Muestra error indicando que la cantidad debe ser mayor a 0. |
| 2 | No ingresa nombre del almacén (si es almacenada). | Muestra error de almacén obligatorio. |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Frecuente durante la temporada de cosecha.
**Importancia:** Vital
**Urgencia:** Inmediatamente
**Comentarios:** No aplica.


**Relaciones:** <<extend>> a CU-45 Consultar Cosechas
### CU-41 Registrar Cosecha con Venta/Reserva

**Descripción:** El usuario registra una cosecha e inmediatamente asocia una venta o reserva (sin almacenar).
**Actores:** Propietario / Administrador
**Pre condiciones:** Debe existir una campaña, cantidad > 0, y el tipo de venta no puede estar vacío.
**Post condiciones:** Se guarda un registro de Cosecha (sin almacén) y un registro de CosechaNoAlmacenada con tipo y precio.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Ingresa cantidad, fecha, campaña, tipo (venta/reserva) y precio. | Valida. Guarda Cosecha y luego CosechaNoAlmacenada. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Tipo de venta vacío. | Muestra error: "El tipo de venta no puede estar vacío". |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Ocasional
**Importancia:** Importante
**Urgencia:** Inmediatamente
**Comentarios:** Este flujo crea dos registros vinculados lógicamente.


**Relaciones:** <<extend>> a CU-40 Registrar Cosecha
### CU-42 Editar Cosecha

**Descripción:** El usuario edita los detalles de una cosecha previamente registrada.
**Actores:** Propietario / Administrador
**Pre condiciones:** La cosecha debe existir.
**Post condiciones:** Los datos de la cosecha quedan actualizados en BD.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Modifica los datos de la cosecha y guarda. | Actualiza la entidad Cosecha en BD. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Datos inválidos o vacíos. | Evita el guardado y muestra error según la validación. |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Poco frecuente
**Importancia:** Importante
**Urgencia:** Puede esperar
**Comentarios:** Se sobreescriben los campos del registro.


**Relaciones:** <<extend>> a CU-45 Consultar Cosechas
### CU-43 Editar Cosecha con Venta/Reserva

**Descripción:** El usuario edita una cosecha modificando si es almacenada o vendida, afectando los registros relacionados.
**Actores:** Propietario / Administrador
**Pre condiciones:** La cosecha debe existir.
**Post condiciones:** La cosecha se actualiza. Se elimina o crea/actualiza el registro en CosechaNoAlmacenada según corresponda.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Cambia el estado de venta y los datos, solicita guardar. | Actualiza Cosecha. Si ahora es almacenada, elimina la venta previa (si existe). Si es venta, actualiza o crea el detalle. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Datos de venta incompletos. | Rechaza la actualización y muestra error. |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Poco frecuente
**Importancia:** Importante
**Urgencia:** Puede esperar
**Comentarios:** Combina lógica de actualización y posible borrado/creación condicional de detalles.


**Relaciones:** <<extend>> a CU-42 Editar Cosecha
### CU-44 Eliminar Cosecha

**Descripción:** El usuario elimina una cosecha registrada por error.
**Actores:** Propietario / Administrador
**Pre condiciones:** La cosecha debe existir.
**Post condiciones:** La cosecha y sus detalles vinculados desaparecen.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Solicita eliminar una cosecha. | Ejecuta la eliminación (Hard Delete) de Cosecha y cascada FK. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Falla en BD. | Muestra mensaje de error. |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Rara vez
**Importancia:** Importante
**Urgencia:** Puede esperar
**Comentarios:** Hard Delete. Elimina en cascada (CASCADE via FK).


**Relaciones:** <<extend>> a CU-45 Consultar Cosechas
### CU-45 Consultar Cosechas

**Descripción:** El usuario lista y visualiza las cosechas registradas (por campaña, o globales, o ventas).
**Actores:** Propietario / Administrador
**Pre condiciones:** El sistema debe estar inicializado.
**Post condiciones:** Ninguna (solo lectura).

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Ingresa a la sección de cosechas o al detalle de campaña. | Recupera las cosechas desde BD y las muestra en la vista. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | No hay registros. | Muestra lista vacía o mensaje "Sin cosechas". |

**Rendimiento:** El sistema deberá realizar la acción de forma inmediata.
**Frecuencia:** Muy frecuente
**Importancia:** Vital
**Urgencia:** Inmediatamente
**Comentarios:** Incluye múltiples variantes (Todas, Por Campaña, No Almacenadas).

---

## Insumos (CU-50 - CU-69)


**Relaciones:** <<include>> a CU-01 Iniciar Sesión
### CU-50 Crear Insumo en Catálogo

**Descripción:** El usuario crea un nuevo insumo genérico en el catálogo para luego utilizarlo en campañas.
**Actores:** Propietario
**Pre condiciones:** El nombre no debe estar vacío.
**Post condiciones:** El insumo queda registrado en el catálogo (BD).

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Ingresa nombre, categoría e ícono, y guarda. | Valida y guarda el Insumo. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Nombre vacío. | Muestra error "El nombre no puede estar vacío". |

**Rendimiento:** Inmediato
**Frecuencia:** Ocasional (al adquirir nuevos productos)
**Importancia:** Importante
**Urgencia:** Inmediatamente
**Comentarios:** -


**Relaciones:** <<extend>> a CU-53 Consultar Catálogo de Insumos
### CU-51 Editar Insumo en Catálogo

**Descripción:** El usuario modifica un insumo existente del catálogo.
**Actores:** Propietario
**Pre condiciones:** El insumo debe existir.
**Post condiciones:** Insumo actualizado en el catálogo.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Modifica datos del insumo y guarda. | Actualiza el Insumo en BD. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Nombre en blanco. | Rechaza la edición con mensaje de error. |

**Rendimiento:** Inmediato
**Frecuencia:** Rara vez
**Importancia:** Quedaría bien
**Urgencia:** Puede esperar
**Comentarios:** -


**Relaciones:** <<extend>> a CU-53 Consultar Catálogo de Insumos
### CU-52 Eliminar Insumo del Catálogo

**Descripción:** El usuario elimina un insumo del catálogo.
**Actores:** Propietario
**Pre condiciones:** El insumo existe.
**Post condiciones:** El insumo se elimina del sistema.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Solicita eliminar el insumo. | Ejecuta Hard Delete del insumo. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Insumo vinculado a campañas. | Falla por FK (o elimina en cascada según configuración BD). |

**Rendimiento:** Inmediato
**Frecuencia:** Rara vez
**Importancia:** Quedaría bien
**Urgencia:** Puede esperar
**Comentarios:** Se realiza Hard Delete desde InsumoRepository.


**Relaciones:** <<extend>> a CU-53 Consultar Catálogo de Insumos
### CU-53 Consultar Catálogo de Insumos

**Descripción:** Listar los insumos disponibles en el catálogo.
**Actores:** Propietario
**Pre condiciones:** -
**Post condiciones:** -

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Accede al catálogo de insumos. | Muestra la lista de insumos. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Catálogo vacío. | Muestra mensaje pertinente. |

**Rendimiento:** Inmediato
**Frecuencia:** Frecuente
**Importancia:** Importante
**Urgencia:** Inmediatamente
**Comentarios:** -


**Relaciones:** <<include>> a CU-01 Iniciar Sesión
### CU-54 Asignar Insumo a Campaña

**Descripción:** El usuario vincula un insumo del catálogo a una campaña en curso, indicando cantidad y precio.
**Actores:** Propietario / Encargado
**Pre condiciones:** Cantidad > 0. El insumo y la campaña deben existir.
**Post condiciones:** Se genera un registro de CampaniaInsumo.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Selecciona insumo, ingresa cantidad y precio, y asigna. | Valida cantidad y persiste la asignación en BD. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Cantidad <= 0. | Muestra error "La cantidad debe ser mayor a cero". |

**Rendimiento:** Inmediato
**Frecuencia:** Muy frecuente (durante aplicaciones agronómicas)
**Importancia:** Vital
**Urgencia:** Inmediatamente
**Comentarios:** Impacta directamente en el cálculo de costos de la campaña.


**Relaciones:** <<extend>> a CU-57 Consultar Insumos de Campaña
### CU-55 Editar Insumo Asignado a Campaña

**Descripción:** El usuario corrige la cantidad o precio de un insumo ya aplicado a una campaña.
**Actores:** Propietario
**Pre condiciones:** ID válido, cantidad > 0, precio >= 0.
**Post condiciones:** Se actualizan los valores de la asignación.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Modifica los valores y guarda. | Valida y actualiza CampaniaInsumo. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Precio negativo o cantidad <= 0. | Rechaza con mensaje descriptivo de la validación. |

**Rendimiento:** Inmediato
**Frecuencia:** Ocasional
**Importancia:** Importante
**Urgencia:** Puede esperar
**Comentarios:** -


**Relaciones:** <<extend>> a CU-57 Consultar Insumos de Campaña
### CU-56 Desvincular Insumo de Campaña

**Descripción:** El usuario retira un insumo que se asignó por error a una campaña.
**Actores:** Propietario
**Pre condiciones:** La asignación existe.
**Post condiciones:** Se elimina el registro de CampaniaInsumo.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Solicita desvincular insumo. | Ejecuta Hard Delete en CampaniaInsumoRepository. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Error de BD. | Informa al usuario. |

**Rendimiento:** Inmediato
**Frecuencia:** Ocasional
**Importancia:** Importante
**Urgencia:** Puede esperar
**Comentarios:** Hard Delete de la vinculación.


**Relaciones:** <<extend>> a CU-57 Consultar Insumos de Campaña
### CU-57 Consultar Insumos de Campaña

**Descripción:** El usuario visualiza los insumos aplicados a una campaña o todos los vinculados globalmente.
**Actores:** Propietario
**Pre condiciones:** -
**Post condiciones:** -

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Visualiza detalles de una campaña. | El sistema lista los insumos (Cantidades, Precios). |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Sin insumos. | Muestra estado vacío. |

**Rendimiento:** Inmediato
**Frecuencia:** Frecuente
**Importancia:** Vital
**Urgencia:** Inmediatamente
**Comentarios:** Usado para revisión de costos y dosis.

---

## Observaciones (CU-70 - CU-79)


**Relaciones:** <<include>> a CU-01 Iniciar Sesión
### CU-70 Guardar Observación

**Descripción:** El usuario añade una nota (y opcionalmente una imagen) sobre el estado del lote en una campaña.
**Actores:** Propietario / Encargado
**Pre condiciones:** El texto no debe estar vacío.
**Post condiciones:** La observación queda guardada.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Escribe texto, añade imagen y guarda. | Valida y guarda en BD. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Texto vacío. | Muestra "El texto no puede estar vacío". |

**Rendimiento:** Inmediato (puede demorar si procesa imagen grande)
**Frecuencia:** Frecuente (durante recorridas de campo)
**Importancia:** Importante
**Urgencia:** Inmediatamente
**Comentarios:** -


**Relaciones:** <<extend>> a CU-73 Listar Observaciones
### CU-71 Editar Observación

**Descripción:** El usuario corrige el texto o imagen de una observación.
**Actores:** Propietario / Encargado
**Pre condiciones:** La observación debe tener texto o una foto.
**Post condiciones:** Observación actualizada.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Cambia texto/foto y guarda. | El sistema limpia el texto, valida y actualiza vía Flow en BD. Emite Resource.Success. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Deja texto y foto vacíos. | Emite Resource.Error("La observación debe tener texto o una foto"). |
| 2 | Error en BD. | Emite Resource.Error con el mensaje del fallo. |

**Rendimiento:** Inmediato
**Frecuencia:** Poco frecuente
**Importancia:** Quedaría bien
**Urgencia:** Puede esperar
**Comentarios:** Esta funcionalidad se agregó como mejora (originalmente no contemplada pero implementada).


**Relaciones:** <<extend>> a CU-73 Listar Observaciones
### CU-72 Eliminar Observación

**Descripción:** El usuario descarta una observación.
**Actores:** Propietario / Encargado
**Pre condiciones:** La observación existe.
**Post condiciones:** Observación removida del sistema.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Solicita eliminación. | El sistema borra el registro (Hard Delete) y emite Success. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Error de borrado. | Emite Resource.Error. |

**Rendimiento:** Inmediato
**Frecuencia:** Ocasional
**Importancia:** Quedaría bien
**Urgencia:** Puede esperar
**Comentarios:** Se realiza Hard Delete.


**Relaciones:** <<extend>> a CU-73 Listar Observaciones
### CU-73 Listar Observaciones

**Descripción:** El usuario ve el historial de anotaciones de una campaña.
**Actores:** Propietario
**Pre condiciones:** -
**Post condiciones:** -

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Abre la pestaña de observaciones de la campaña. | Muestra el listado de notas e imágenes asociadas. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Sin datos. | Muestra aviso correspondiente. |

**Rendimiento:** Inmediato
**Frecuencia:** Frecuente
**Importancia:** Importante
**Urgencia:** Inmediatamente
**Comentarios:** -

---

## Reportes y Backups (CU-80 - CU-89)


**Relaciones:** <<include>> a CU-01 Iniciar Sesión
### CU-80 Calcular Costo por Hectárea

**Descripción:** El sistema calcula cuánto se ha gastado en insumos por cada hectárea de la campaña.
**Actores:** Propietario (Sistema)
**Pre condiciones:** Campaña con hectáreas > 0.
**Post condiciones:** Devuelve el valor numérico (double) del costo/Ha.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Visualiza detalles financieros de la campaña. | Suma (cantidad * precio) de insumos y divide por hectáreas. Muestra el resultado. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Hectáreas <= 0 o nulas. | Devuelve 0.0 sin error. |
| 2 | Sin insumos. | Devuelve 0.0. |

**Rendimiento:** Inmediato
**Frecuencia:** Muy frecuente (consulta en tiempo real)
**Importancia:** Vital
**Urgencia:** Inmediatamente
**Comentarios:** Lógica en memoria a partir de los datos proveídos.


**Relaciones:** <<include>> a CU-01 Iniciar Sesión
### CU-81 Obtener Evolución de Cultivo

**Descripción:** El usuario visualiza la evolución del rendimiento (Tn/Ha) histórico de un cultivo a lo largo del tiempo (todas sus campañas).
**Actores:** Propietario
**Pre condiciones:** El cultivo debe tener campañas y cosechas asociadas.
**Post condiciones:** Genera una lista de `PuntoCultivo`.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Selecciona un cultivo para ver su historial. | Cruza datos de Campañas y Cosechas, calcula rendimiento (Tn/Ha) ordenado por fecha de inicio y lo devuelve como flujo. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Hectáreas de campaña = 0. | El rendimiento para esa campaña se calcula como 0.0. |

**Rendimiento:** Inmediato
**Frecuencia:** Ocasional (al planificar o analizar fin de ciclo)
**Importancia:** Importante
**Urgencia:** Puede esperar
**Comentarios:** Toma en cuenta tanto campañas activas como finalizadas.


**Relaciones:** <<include>> a CU-01 Iniciar Sesión
### CU-82 Obtener Resumen de Rendimiento (Mensual)

**Descripción:** El usuario visualiza un panel de resumen con capital invertido, ingresos brutos, balance, total cosechado y costo por tonelada de las campañas activas.
**Actores:** Propietario
**Pre condiciones:** Debe haber campañas activas.
**Post condiciones:** Devuelve objeto `ResumenRendimiento`.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Accede al dashboard de la app. | Suma insumos de campañas activas (total). Suma cosechas y ventas *solo del mes actual*. Calcula balance y costos. Muestra dashboard. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Sin campañas activas. | Devuelve `null`, la UI mostrará estado vacío. |

**Rendimiento:** Rápido (combinación de flujos reactivos)
**Frecuencia:** Muy frecuente (al abrir la app)
**Importancia:** Vital
**Urgencia:** Inmediatamente
**Comentarios:** Tiene la particularidad de cruzar Insumos (todo el historial de la campaña activa) vs Cosechas/Ventas (filtrado por el mes y año actual).


**Relaciones:** <<include>> a CU-01 Iniciar Sesión
### CU-83 Crear Backup de Base de Datos

**Descripción:** El usuario exporta la base de datos local de la aplicación a un archivo seguro en su dispositivo o nube.
**Actores:** Propietario
**Pre condiciones:** La base de datos local debe existir.
**Post condiciones:** Se escribe una copia física del archivo `don_elio_db` en la URI destino.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Elige ubicación en el explorador de archivos (SAF) y confirma. | Lee la BD local y la copia al OutputStream de la URI seleccionada. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | No se encuentra BD local. | Retorna fallo "No se encontró la base de datos local". |
| 2 | No se puede abrir destino. | Retorna fallo indicando que no se pudo abrir el archivo. |

**Rendimiento:** Unos pocos segundos.
**Frecuencia:** Periódica (semanal/mensual recomendada).
**Importancia:** Vital
**Urgencia:** Hay presión (para evitar pérdida de datos).
**Comentarios:** Utiliza el ContentResolver para escribir el archivo.


**Relaciones:** <<include>> a CU-01 Iniciar Sesión
### CU-84 Restaurar Backup de Base de Datos

**Descripción:** El usuario importa un archivo de backup previamente creado para recuperar sus datos.
**Actores:** Propietario
**Pre condiciones:** Archivo seleccionado debe ser válido (SQLite 3).
**Post condiciones:** La base de datos local se sobreescribe con la del backup.

**Secuencia Normal**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Selecciona el archivo de backup a restaurar. | Lee los primeros 16 bytes. Valida la firma "SQLite format 3". Cierra la BD actual. Sobreescribe la BD local. |

**Excepciones**
| # | Acción (actor) | Reacción (sistema) |
|---|---|---|
| 1 | Archivo no es SQLite. | Retorna fallo "Archivo inválido: no es una base de datos SQLite". |
| 2 | No se puede leer. | Retorna fallo por error de lectura. |

**Rendimiento:** Unos pocos segundos.
**Frecuencia:** Rara vez (ante cambio de celular o pérdida de datos).
**Importancia:** Vital
**Urgencia:** Inmediatamente (si se perdieron datos).
**Comentarios:** Es un proceso delicado, sobreescribe totalmente los datos actuales.

**Relaciones:** <<include>> a CU-01 Iniciar Sesión

