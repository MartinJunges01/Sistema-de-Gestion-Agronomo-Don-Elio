# Implementación Issue 338: Teclado y Scroll

## Diagnóstico
El bloque blanco superior y el bloqueo de scroll ocurren por una combinación de factores en el manejo de WindowInsets (Edge-to-Edge) y el imePadding():
1. MainActivity.kt llama a WindowCompat.setDecorFitsSystemWindows junto con enableEdgeToEdge(). Esto puede causar conflictos porque enableEdgeToEdge() ya gestiona la ventana.
2. En screens.kt, se está aplicando .padding(paddingValues) y luego .imePadding() al Box global. Cuando el teclado aparece, el imePadding() empuja el contenido hacia arriba, pero como los insets del teclado no fueron consumidos por el Scaffold, se genera un desfasaje (la franja blanca).

## Cambios Propuestos

### 1. MainActivity.kt
Eliminar la llamada redundante y dejar que enableEdgeToEdge() maneje los insets modernos.

#### [MODIFY] MainActivity.kt
`kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // ELIMINAR: WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        setContent {
`

### 2. screens.kt
Mejorar el Scaffold para que consuma los insets correctamente y permitir que el teclado redimensione el área de contenido de forma natural, sin desfasar la cabecera. Agregaremos consumeWindowInsets.

#### [MODIFY] screens.kt
`kotlin
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .consumeWindowInsets(paddingValues) // <--- NUEVO: Consume los insets del Scaffold
            .imePadding()
        ) {
`

## Validación
- Correr la app y abrir el formulario de campaña o insumo.
- Tocar un campo de texto; el teclado debe empujar el contenido suavemente.
- No debe aparecer el bloque blanco superior.
- Hacer scroll por el formulario con el teclado abierto para asegurar que no se bloquea.
