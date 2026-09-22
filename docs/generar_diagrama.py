import base64
import urllib.request

mermaid_code = """flowchart TD
    UI[LoginScreen] -->|"[usuario, password]"| VM["LoginViewModel.login()"]
    VM --> UC["LoginUseCase.invoke()"]
    UC --> DAO["UsuarioDao.getUsuarioByNombre()"]
    
    DAO --> Cond{"¿Hash coincide?"}
    
    Cond -- "Sí" --> Mapper["toDomain()"]
    Cond -- "No" --> Err["state.error = 'Credenciales inválidas'"]
    
    Mapper --> Save["GuardarSesionUseCase"]
    Save --> Success["state.loginExitoso = true"]
    Success --> Nav["LaunchedEffect → navegar al Dashboard"]
"""

encoded = base64.urlsafe_b64encode(mermaid_code.encode('utf-8')).decode('utf-8')
url = f'https://mermaid.ink/img/{encoded}?type=png&bgColor=white'

print('Descargando imagen...')
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
try:
    with urllib.request.urlopen(req) as response:
        with open('docs/diagrama_login.png', 'wb') as f:
            f.write(response.read())
    print('Imagen guardada exitosamente en docs/diagrama_login.png')
except Exception as e:
    print(f'Error: {e}')
