import re

file_path = r'f:\Proyectos\Sistema-de-Gestion-Agronomo-Don-Elio\docs\casos_de_uso_2024.md'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

relations = {
    'CU-01': '',
    'CU-02': '',
    'CU-03': '<<extend>> a CU-01 Iniciar Sesión',
    'CU-10': '<<extend>> a CU-13 Consultar Cultivos',
    'CU-11': '<<extend>> a CU-13 Consultar Cultivos',
    'CU-12': '<<extend>> a CU-13 Consultar Cultivos',
    'CU-13': '<<include>> a CU-01 Iniciar Sesión',
    'CU-20': '<<extend>> a CU-24 Consultar Campañas',
    'CU-21': '<<extend>> a CU-24 Consultar Campañas',
    'CU-22': '<<extend>> a CU-24 Consultar Campañas',
    'CU-23': '<<extend>> a CU-24 Consultar Campañas',
    'CU-24': '<<include>> a CU-01 Iniciar Sesión',
    'CU-30': '<<extend>> a CU-34 Consultar Tareas',
    'CU-31': '<<extend>> a CU-34 Consultar Tareas',
    'CU-32': '<<extend>> a CU-34 Consultar Tareas',
    'CU-33': '<<extend>> a CU-34 Consultar Tareas',
    'CU-34': '<<include>> a CU-01 Iniciar Sesión',
    'CU-40': '<<extend>> a CU-45 Consultar Cosechas',
    'CU-41': '<<extend>> a CU-40 Registrar Cosecha',
    'CU-42': '<<extend>> a CU-45 Consultar Cosechas',
    'CU-43': '<<extend>> a CU-42 Editar Cosecha',
    'CU-44': '<<extend>> a CU-45 Consultar Cosechas',
    'CU-45': '<<include>> a CU-01 Iniciar Sesión',
    'CU-50': '<<extend>> a CU-53 Consultar Catálogo de Insumos',
    'CU-51': '<<extend>> a CU-53 Consultar Catálogo de Insumos',
    'CU-52': '<<extend>> a CU-53 Consultar Catálogo de Insumos',
    'CU-53': '<<include>> a CU-01 Iniciar Sesión',
    'CU-54': '<<extend>> a CU-57 Consultar Insumos de Campaña',
    'CU-55': '<<extend>> a CU-57 Consultar Insumos de Campaña',
    'CU-56': '<<extend>> a CU-57 Consultar Insumos de Campaña',
    'CU-57': '<<include>> a CU-01 Iniciar Sesión',
    'CU-70': '<<extend>> a CU-73 Listar Observaciones',
    'CU-71': '<<extend>> a CU-73 Listar Observaciones',
    'CU-72': '<<extend>> a CU-73 Listar Observaciones',
    'CU-73': '<<include>> a CU-01 Iniciar Sesión',
    'CU-80': '<<include>> a CU-01 Iniciar Sesión',
    'CU-81': '<<include>> a CU-01 Iniciar Sesión',
    'CU-82': '<<include>> a CU-01 Iniciar Sesión',
    'CU-83': '<<include>> a CU-01 Iniciar Sesión',
    'CU-84': '<<include>> a CU-01 Iniciar Sesión'
}

def inject_relations(match):
    cu_id = match.group(1)
    if cu_id in relations and relations[cu_id]:
        # Encontrar la línea de comentarios en este bloque
        block = match.group(0)
        # Si ya tiene relaciones, no duplicar
        if '**Relaciones:**' not in block:
            block = re.sub(r'(\*\*Comentarios:\*\*.*?)$', r'\1\n**Relaciones:** ' + relations[cu_id], block, flags=re.MULTILINE)
        return block
    return match.group(0)

# El regex buscará desde un '### CU-XX' hasta el siguiente '### CU-XX' o el final del archivo
new_content = re.sub(r'(### (CU-\d{2}).*?(?=(### CU-\d{2}|\Z)))', inject_relations, content, flags=re.DOTALL)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(new_content)

print("Relaciones inyectadas exitosamente.")
