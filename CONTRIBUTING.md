# Guía de Contribución y Flujo de Trabajo (Git / GitHub)

Para mantener el orden en el repositorio, evitar regresiones y asegurar que el trabajo de todos fluya de forma eficiente, el equipo ha acordado el siguiente flujo de trabajo.

## 1. La Rama `main` está Protegida
* **Prohibido el push directo:** Nadie puede subir código directamente a `main`. Todo nuevo código, sin excepción, debe ingresar a través de una **Pull Request (PR)**.
* **Revisión requerida:** Toda PR debe tener al menos **1 revisión** aprobada antes de poder fusionarse.

## 2. Un Issue = Una Rama = Una PR
* Para cada nueva tarea, mejora o corrección, se debe crear una rama independiente partiendo de `main` (por ejemplo: `feature/nombre-de-tarea` o `fix/nombre-del-bug`).
* Nunca reutilices una rama vieja para un nuevo propósito.

## 3. Vinculación Automática a Issues
* Para mantener la trazabilidad, **toda PR debe estar vinculada al Issue que resuelve**.
* **Cómo hacerlo:** En la descripción de tu Pull Request en GitHub, debes usar una palabra clave seguida del número del Issue. Por ejemplo, escribe:
  > `Fixes #45` o `Closes #45`
* *Resultado:* Al fusionarse la PR, GitHub cerrará automáticamente el Issue, ahorrando trabajo manual de gestión.

## 4. Mantén tu Rama Actualizada
* Está activada la opción *"Always suggest updating pull request branches"*.
* Si tus compañeros han fusionado código nuevo a `main` mientras tú estabas trabajando en tu PR, GitHub te mostrará un botón de **Update branch**. 
* **Regla:** Siempre presiona ese botón para traer lo último de `main` a tu rama y asegurarte de que tu código no rompa nada con los nuevos cambios antes de la fusión final.

## 5. Limpieza Automática de Ramas
* El repositorio está configurado para **eliminar automáticamente** las ramas una vez que su Pull Request ha sido fusionada con éxito.
* Esto garantiza que el repositorio no se llene de ramas obsoletas o "zombies".
* *Tip local:* Recuerda limpiar periódicamente las ramas eliminadas en tu computadora ejecutando `git fetch -p`.
