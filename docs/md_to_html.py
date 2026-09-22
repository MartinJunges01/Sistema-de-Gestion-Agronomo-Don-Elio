import markdown

with open('docs/documentacion_tecnica_completa_fases.md', 'r', encoding='utf-8') as f:
    md_text = f.read()

html_body = markdown.markdown(md_text, extensions=['fenced_code', 'tables'])

html_template = f"""<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8">
<style>
    body {{ font-family: Arial, sans-serif; }}
    table {{ border-collapse: collapse; width: 100%; margin-bottom: 20px; }}
    th, td {{ border: 1px solid #dddddd; text-align: left; padding: 8px; }}
    th {{ background-color: #f2f2f2; }}
    pre {{ background-color: #f4f4f4; padding: 15px; border-radius: 5px; overflow-x: auto; font-family: Consolas, monospace; border: 1px solid #e0e0e0; }}
    code {{ font-family: Consolas, monospace; }}
    blockquote {{ border-left: 4px solid #ccc; margin-left: 0; padding-left: 15px; color: #555; }}
</style>
</head>
<body>
{html_body}
</body>
</html>"""

with open('docs/documentacion_tecnica_completa_fases.html', 'w', encoding='utf-8') as f:
    f.write(html_template)
print("Archivo HTML generado correctamente.")
