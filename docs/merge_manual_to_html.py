import markdown
import os

# Archivos a leer
archivos_md = [
    "docs/manual_usuario_fase1_instalacion_login_dashboard.md",
    "docs/manual_usuario_fase2_campanias_tareas_insumos.md",
    "docs/manual_usuario_fase3_cosechas_observaciones_reportes_config.md"
]

contenido_completo = ""

for archivo in archivos_md:
    with open(archivo, "r", encoding="utf-8") as f:
        contenido_completo += f.read() + "\n\n"

# Escribir el markdown combinado (opcional, por si el usuario lo quiere)
with open("docs/manual_usuario_completo.md", "w", encoding="utf-8") as f:
    f.write(contenido_completo)

# Convertir a HTML
html_content = markdown.markdown(contenido_completo, extensions=['fenced_code', 'tables'])

# CSS para que se vea bien al copiar a Google Docs
html_template = f"""
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<style>
    body {{
        font-family: Arial, sans-serif;
        line-height: 1.6;
        color: #333;
        max-width: 800px;
        margin: 0 auto;
        padding: 20px;
    }}
    h1, h2, h3, h4, h5, h6 {{
        color: #2c3e50;
        margin-top: 24px;
        margin-bottom: 16px;
    }}
    table {{
        border-collapse: collapse;
        width: 100%;
        margin-bottom: 20px;
    }}
    th, td {{
        border: 1px solid #ddd;
        padding: 8px;
        text-align: left;
    }}
    th {{
        background-color: #f2f2f2;
        font-weight: bold;
    }}
    code {{
        background-color: #f4f4f4;
        padding: 2px 4px;
        border-radius: 4px;
        font-family: Consolas, monospace;
    }}
    pre {{
        background-color: #f4f4f4;
        padding: 10px;
        border-radius: 4px;
        overflow-x: auto;
    }}
    blockquote {{
        border-left: 4px solid #4CAF50;
        margin: 0;
        padding-left: 16px;
        color: #555;
    }}
</style>
</head>
<body>
{html_content}
</body>
</html>
"""

with open("docs/manual_usuario_completo.html", "w", encoding="utf-8") as f:
    f.write(html_template)

print("¡Archivos combinados y convertidos a HTML con éxito!")
