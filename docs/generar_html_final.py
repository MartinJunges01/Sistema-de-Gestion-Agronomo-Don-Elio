import markdown

with open('docs/manual_usuario_completo.md', 'r', encoding='utf-8') as f:
    md_text = f.read()

# Render HTML using python-markdown
html_content = markdown.markdown(md_text, extensions=['fenced_code', 'tables'])

# HTML Template with nice styling
html_template = f"""<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<style>
    body {{ font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 900px; margin: 0 auto; padding: 20px; }}
    h1, h2, h3, h4, h5, h6 {{ color: #2c3e50; margin-top: 24px; margin-bottom: 16px; border-bottom: 1px solid #eee; padding-bottom: 5px; }}
    table {{ border-collapse: collapse; width: 100%; margin-bottom: 20px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }}
    th, td {{ border: 1px solid #ddd; padding: 12px; text-align: left; }}
    th {{ background-color: #f8f9fa; font-weight: bold; }}
    img {{ max-width: 100%; height: auto; border: 1px solid #ddd; border-radius: 4px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); margin: 20px 0; }}
    code {{ background-color: #f4f4f4; padding: 2px 4px; border-radius: 4px; font-family: Consolas, monospace; }}
    blockquote {{ border-left: 4px solid #4CAF50; margin: 0; padding-left: 16px; color: #555; background-color: #f9f9f9; padding: 10px; }}
</style>
</head>
<body>
{html_content}
</body>
</html>"""

with open('docs/manual_usuario_completo.html', 'w', encoding='utf-8') as f:
    f.write(html_template)

print("HTML final generado.")
