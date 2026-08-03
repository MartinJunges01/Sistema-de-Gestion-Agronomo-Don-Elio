

| Carrera  | Tecnicatura Superior en Desarrollo de Software |  |  |
| :---- | :---- | :---- | :---- |
| Ciclo Lectivo  | 2025  | Año  | 3º |
| Unidad   Curricular | PRÁCTICAS PROFESIONALIZANTES II  | Docente | Nicolás Kappes |
| Nombre y   apellido | Landra Agostina \- Martinez Ezequiel – Junges  Martin | Fecha | 26/5/2025 |
| Calificación |  |  |  |

ENTREGA FINAL TRABAJO PRÁCTICO INTEGRADOR   
Landra Agostina – Martínez Ezequiel – Junges Martín    
Tecnicatura Superior en Desarrollo de Software    
Instituto Tecnológico El Molino (ITEC)   
Nicolás Kappes   
23 de octubre de 2025

# **Interfaces**

1 \- Login/registro  
2 \- Home / Dashboard  
3 \- Configuración (CU12/13)  
4 \- Reportes (CU10)  
5 \- Export (CU11)  
6 \- Tareas (CU5) (CU 5.4)  
7 \- Nueva Tarea (CU 5.1)  
8 \- (editar y eliminar tareas?)  
9 \- Insumos (CU9 \- 9.3)  
10 \- Catálogo (CU 9.4 \- CU 9.7)  
11 \- Formulario Creacion/edición Catalogo (CU 9.5)  
12 \- Campañas  
13 \- Crear / editar Campaña  
14 \- Resumen Campaña  
15 \- Cosechas  
16 \- Formulario Cosecha (CU6 y CU7)  
17 \- Observaciones  
18 \- Adjuntar Foto

Modelo Lógico de la Base de Datos  
1\. Tabla: Usuario  
Campos y Tipos:  
id\_usuario: Entero (PK)  
nombre: Cadena de texto  
contrasena: Cadena de texto (Hash)  
ultimo\_acceso: Entero Largo (timestamp)  
Relaciones: Ninguna (Entidad aislada para control de acceso local).

Tabla: Campania  
Campos y Tipos:  
id\_campania: Entero (PK)  
nombre: Cadena de texto  
fecha: Entero Largo (timestamp)  
cultivo: Cadena de texto  
estaActiva: Booleano  
Relaciones: Tabla central. Relacionada con Tarea, Observacion, Cosecha e InsumosUtilizados.

Tabla: Tarea  
Campos y Tipos:  
id\_tarea: Entero (PK)  
nombre: Cadena de texto  
fecha: Entero largo (timestamp)  
hora: Cadena de Texto  
notificar: Booleano  
confirmar: Booleano  
id\_campania: Entero (FK) \-\> Referencia a la tabla Campania  
Relaciones: Pertenece a una Campania (1:N).

Tabla: Observacion  
Campos y Tipos:  
id\_observacion: Entero (PK)  
texto: Cadena de texto larga  
imagenurl: Cadena de texto (Ruta del archivo local, Opcional/Nulo)  
id\_campania: Entero (FK) \-\> Referencia a la tabla Campania  
Relaciones: Pertenece a una Campania (1:N).

Tabla: Cosecha  
Campos y Tipos:  
id\_cosecha: Entero (PK)  
cantidad: Decimal real  
fecha: Entero Largo (timestamp)  
unidad: Cadena de texto  
almacen: Cadena de texto  
id\_campania: Entero (FK) \-\> Referencia a la tabla Campania  
Relaciones: Pertenece a una Campania (1:N). Tiene muchas CosechaNoAlmacenada.

Tabla: CosechaNoAlmacenada  
Campos y Tipos:  
id\_cosecha\_no\_alm: Entero (PK)  
tipo: Cadena de texto  
precio: Decimal real (Opcional/Nulo)  
id\_cosecha: Entero (FK) \-\> Referencia a la tabla Cosecha  
Relaciones: Pertenece a una Cosecha (1:N).

Tabla: Insumo  
Campos y Tipos:  
id\_insumo: Entero (PK)  
nombre: Cadena de texto  
categoria: Cadena de texto  
unidad: Cadena de texto  
icono: Cadena de Texto (opcional/nulo)  
activo: Booleano  
Relaciones: Catálogo general. Se relaciona con campania\_insumo.

Tabla: campania\_insumo  
Campos y Tipos:  
id\_insumo\_utilizado: Entero (PK)  
cantidad: Decimal real  
precio: Decimal real  
id\_campania: Entero (FK) \-\> Referencia a la tabla Campania  
id\_insumo: Entero (FK) \-\> Referencia a la tabla Insumo  
Relaciones: Tabla intermedia/asociativa que une Campania (1:N) e Insumo (1:N).

Modelo Físico de la Base de datos (SQLite / Room)  
sentencias DDL  
\-- \=========================================================    
\-- CREACIÓN DE TABLAS    
\-- \=========================================================

CREATE TABLE usuarios (    
    id\_usuario INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,    
    nombre TEXT NOT NULL,    
    nombreUsuario TEXT NOT NULL,  
    contrasena TEXT NOT NULL,    
    ultimo\_acceso INTEGER NOT NULL    
);

CREATE TABLE campanias (    
    id\_campania INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,    
    nombre TEXT NOT NULL,    
    fecha INTEGER NOT NULL,    
    cultivo TEXT NOT NULL,  
    estaActiva INTEGER NOT NULL DEFAULT 1  
);

CREATE TABLE tareas (    
    id\_tarea INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,    
    nombre TEXT NOT NULL,    
    fecha INTEGER NOT NULL,    
    hora TEXT NOT NULL,    
    notificar INTEGER NOT NULL DEFAULT 0,    
    confirmar INTEGER NOT NULL DEFAULT 0,    
    id\_campania INTEGER NOT NULL,    
    FOREIGN KEY (id\_campania) REFERENCES campanias(id\_campania) ON DELETE CASCADE    
);

CREATE TABLE observaciones (    
    id\_observacion INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,    
    texto TEXT NOT NULL,    
    imagenUri TEXT,    
    id\_campania INTEGER NOT NULL,    
    FOREIGN KEY (id\_campania) REFERENCES campanias(id\_campania) ON DELETE CASCADE    
);

CREATE TABLE cosechas (    
    id\_cosecha INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,    
    cantidad REAL NOT NULL,    
    fecha INTEGER NOT NULL,    
    unidad TEXT NOT NULL,    
    almacen TEXT NOT NULL,    
    id\_campania INTEGER NOT NULL,    
    FOREIGN KEY (id\_campania) REFERENCES campanias(id\_campania) ON DELETE CASCADE    
);

CREATE TABLE cosechas\_no\_almacenadas (    
    id\_cosecha\_no\_alm INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,    
    tipo TEXT NOT NULL,    
    precio REAL NOT NULL,    
    id\_cosecha INTEGER NOT NULL,    
    FOREIGN KEY (id\_cosecha) REFERENCES cosechas(id\_cosecha) ON DELETE CASCADE    
);

CREATE TABLE insumos (    
    id\_insumo INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,    
    nombre TEXT NOT NULL,    
    categoria TEXT NOT NULL,    
    unidad TEXT NOT NULL,  
    icono TEXT  
    activo INTEGER NOT NULL DEFAULT 1,  
);

CREATE TABLE campania\_insumo (    
    id\_campania\_insumo INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,    
    id\_campania INTEGER NOT NULL,    
    id\_insumo INTEGER NOT NULL,    
    cantidad REAL NOT NULL,    
    precio REAL NOT NULL,    
    FOREIGN KEY (id\_campania) REFERENCES campanias(id\_campania) ON DELETE CASCADE,    
    FOREIGN KEY (id\_insumo) REFERENCES insumos(id\_insumo) ON DELETE CASCADE    
);

\-- \=========================================================    
\-- ÍNDICES (Para optimizar consultas frecuentes generados por Room)    
\-- \=========================================================

CREATE INDEX index\_tareas\_id\_campania ON tareas(id\_campania);    
CREATE INDEX index\_observaciones\_id\_campania ON observaciones(id\_campania);    
CREATE INDEX index\_cosechas\_id\_campania ON cosechas(id\_campania);    
CREATE INDEX index\_cosechas\_no\_almacenadas\_id\_cosecha ON cosechas\_no\_almacenadas(id\_cosecha);    
CREATE INDEX index\_campania\_insumo\_id\_campania ON campania\_insumo(id\_campania);    
CREATE INDEX index\_campania\_insumo\_id\_insumo ON campania\_insumo(id\_insumo);  
CREATE UNIQUE INDEX index\_campania\_insumo\_id\_campania\_id\_insumo ON campania\_insumo(id\_campania, id\_insumo);

