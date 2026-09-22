import base64
import urllib.request

mermaid_code = """graph TD
    VM["📱 GestionCampaniasViewModel<br/><i>@HiltViewModel @Inject</i>"]
    UC["⚙️ ObtenerCampaniasActivasUseCase<br/><i>@Inject</i>"]
    RepoInterface[["🎯 CampaniaRepository<br/><i>(Interfaz)</i>"]]
    
    RepoImpl["🏗️ CampaniaRepositoryImpl<br/><i>@Inject</i>"]
    Dao["💾 CampaniaDao<br/><i>@Singleton</i>"]
    DB[("🗄️ DonElioDatabase<br/><i>@Singleton</i>")]

    RepoModule{{"📦 RepositoryModule<br/><i>@Binds</i>"}}
    DbModule{{"📦 DatabaseModule<br/><i>@Provides</i>"}}

    VM -->|Requiere por constructor| UC
    UC -->|Requiere por constructor| RepoInterface
    
    RepoInterface -.->|Hilt busca cómo proveerlo en| RepoModule
    RepoModule ==>|Bindea y retorna| RepoImpl
    
    RepoImpl -->|Requiere| Dao
    RepoImpl -->|Requiere| DB
    
    Dao -.->|Hilt busca cómo proveerlo en| DbModule
    DB -.->|Hilt busca cómo proveerlo en| DbModule
    
    DbModule ==>|Retorna instancia única| Dao
    DbModule ==>|Retorna instancia única| DB
"""

encoded = base64.urlsafe_b64encode(mermaid_code.encode('utf-8')).decode('utf-8')
url = f'https://mermaid.ink/img/{encoded}?type=png&bgColor=white'

print('Descargando imagen...')
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
try:
    with urllib.request.urlopen(req) as response:
        with open('docs/diagrama_hilt_campanias.png', 'wb') as f:
            f.write(response.read())
    print('Imagen guardada exitosamente en docs/diagrama_hilt_campanias.png')
except Exception as e:
    print(f'Error: {e}')
