
with open('docs/plan_de_pruebas.md', 'r', encoding='utf-8') as f:
    text = f.read()

new_tests = '''
**Test 4.1: Asignación Múltiple del Mismo Insumo a la Misma Campaña [#455]**
*   **Given:** El \"Glifosato\" ya asignado previamente a la campaña \"Trigo de Invierno\".
*   **When:** Invoco \AsignarInsumoACampaniaUseCase\ con el mismo Insumo y Campaña, pero pasando \cantidad = 2\ y \precio = 120\.
*   **Then:** Se inserta un *nuevo* registro independiente en \CampaniaInsumoEntity\ (sin sobrescribir ni dar error), y el sistema ahora le asigna una fecha automática.

**Test 4.2: Actualización Individual de Insumo Asignado Múltiples Veces [#455]**
*   **Given:** Dos registros independientes del mismo insumo \"Glifosato\" en la campaña.
*   **When:** Invoco la edición sobre el registro individual (e.g. vía ID específico).
*   **Then:** Solo se modifican la cantidad y el precio de ese registro individual, manteniendo los demás registros intactos.
'''

text = text.replace('relacionando los IDs y estableciendo el coste.', 'relacionando los IDs y estableciendo el coste.' + '\n' + new_tests)

with open('docs/plan_de_pruebas.md', 'w', encoding='utf-8') as f:
    f.write(text)

