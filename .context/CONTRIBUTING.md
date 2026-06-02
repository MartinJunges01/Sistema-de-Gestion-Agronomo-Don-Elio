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

## 6. Actualización con Rebase (Historial Lineal)
Para evitar ensuciar el historial con múltiples commits de fusión automáticos, recomendamos actualizar tu rama local usando *Rebase* en lugar de *Merge*:
1. `git fetch origin` (para traer las últimas novedades)
2. `git rebase origin/main` (para poner tus commits por encima de los cambios de `main`)
3. Si el rebase reescribió la historia de tu rama y necesitas subirla al repositorio remoto, utiliza **siempre** el push seguro: `git push origin nombre-de-tu-rama --force-with-lease`. *Nunca uses solo `--force`*.

## 7. Commits Atómicos y Descriptivos
* Crea commits pequeños y enfocados en una única tarea ("atómicos"). Esto facilita encontrar problemas y revertir cambios si algo sale mal.
* Usa la convención de mensajes de commit (Conventional Commits):
  * `feat(modulo):` para nuevas funcionalidades.
  * `fix(modulo):` para arreglar bugs.
  * `refactor(modulo):` para reestructuración de código sin alterar su comportamiento.
  * `chore:` para tareas de mantenimiento o ajustes de entorno (ej. `.gitignore`).

## 8. Estrategias de Fusión en GitHub (Merge)
Para mantener la rama `main` como una línea recta y limpia, cuando vayas a aceptar la Pull Request elige entre:
* **Rebase and merge:** Úsalo si tu rama tiene commits atómicos y descriptivos de alta calidad que valga la pena conservar en el historial de `main`.
* **Squash and merge:** Úsalo si tu rama tiene muchos commits de prueba o correcciones rápidas (ej. "fix", "prueba", "ahora sí"). Esto agrupará todos tus cambios en un único y limpio commit.
* *Evita usar "Create a merge commit"* para no generar un historial enredado ("vías de tren").

## 9. Limpieza Segura de Ramas Locales
Cuando hayas terminado tu feature y haya sido fusionada, borra tu rama de trabajo local de manera segura:
* Usa `git branch -d nombre-rama` (con `d` minúscula). Git verificará si la rama ya fue fusionada en `main` antes de permitir el borrado.
* Solo usa `-D` (mayúscula) si estás absolutamente seguro de querer descartar código fallido o experimentos.
