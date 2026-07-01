package com.itec.donelio.presentation.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * State holder que encapsula el estado mutable del permiso de cámara y las
 * acciones para solicitarlo o reiniciarlo. Diseñado para ser consumido desde
 * [recordarPermisoCamara] en la capa de UI.
 *
 * Todos los campos son observables por Compose gracias a [mutableStateOf].
 *
 * @property permisoConcedido `true` si el permiso [Manifest.permission.CAMERA] fue otorgado.
 * @property mostrarRazon `true` cuando el sistema indica que se debe mostrar una
 *   justificación (rationale) antes de volver a solicitar el permiso.
 * @property denegadoPermanente `true` cuando el usuario denegó el permiso de forma
 *   permanente. En este caso se debe redirigir al usuario a Ajustes del sistema.
 */
@Stable
class EstadoPermisoCamara {
    var permisoConcedido by mutableStateOf(false)
    var mostrarRazon by mutableStateOf(false)
    var denegadoPermanente by mutableStateOf(false)

    internal var onSolicitarPermiso: () -> Unit = {}

    fun solicitar() = onSolicitarPermiso()

    fun restaurar() {
        mostrarRazon = false
        denegadoPermanente = false
    }
}

/**
 * Composable que recuerda y gestiona el ciclo de vida completo del permiso de
 * cámara ([Manifest.permission.CAMERA]) en tiempo de ejecución (runtime permission).
 *
 * Implementa el flujo recomendado por Android para permisos peligrosos (API 23+):
 * 1. Si ya está concedido → [EstadoPermisoCamara.permisoConcedido] = `true` de inmediato.
 * 2. Si se deniega por primera vez → activa [EstadoPermisoCamara.mostrarRazon].
 * 3. Si se deniega permanentemente → activa [EstadoPermisoCamara.denegadoPermanente].
 *
 * Uso típico en un Composable:
 * ```kotlin
 * val controlPermiso = recordarPermisoCamara()
 *
 * OutlinedButton(onClick = {
 *     if (controlPermiso.permisoConcedido) {
 *         lanzarCamara()
 *     } else {
 *         accionPendiente = { lanzarCamara() }
 *         controlPermiso.solicitar()
 *     }
 * }) { Text("Tomar foto") }
 *
 * if (controlPermiso.mostrarRazon) {
 *     DialogoRazonPermisoCamara(
 *         enConfirmar = { controlPermiso.restaurar(); controlPermiso.solicitar() },
 *         enDescartar = { controlPermiso.restaurar() }
 *     )
 * }
 * ```
 *
 * @return Un [EstadoPermisoCamara] estable que representa el estado actual del permiso.
 */
@Composable
fun recordarPermisoCamara(): EstadoPermisoCamara {
    val context = LocalContext.current
    val estado = remember { EstadoPermisoCamara() }

    val estaYaConcedido = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    if (estaYaConcedido) {
        estado.permisoConcedido = true
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { esConcedido ->
        if (esConcedido) {
            estado.permisoConcedido = true
            estado.mostrarRazon = false
            estado.denegadoPermanente = false
        } else {
            val debeJustificar = ActivityCompat.shouldShowRequestPermissionRationale(
                context as androidx.activity.ComponentActivity,
                Manifest.permission.CAMERA
            )
            if (debeJustificar) {
                estado.mostrarRazon = true
            } else {
                estado.denegadoPermanente = true
            }
        }
    }

    estado.onSolicitarPermiso = { launcher.launch(Manifest.permission.CAMERA) }

    return estado
}

/**
 * Diálogo informativo que explica al usuario por qué la aplicación necesita
 * acceso a la cámara. Debe mostrarse cuando [EstadoPermisoCamara.mostrarRazon]
 * es `true`, es decir, cuando el usuario denegó el permiso por primera vez y
 * el sistema indica que se debe justificar el pedido antes de reintentar.
 *
 * @param enConfirmar Callback invocado cuando el usuario acepta la justificación
 *   y desea conceder el permiso.
 * @param enDescartar Callback invocado cuando el usuario descarta el diálogo
 *   sin conceder el permiso.
 */
@Composable
fun DialogoRazonPermisoCamara(
    enConfirmar: () -> Unit,
    enDescartar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = enDescartar,
        title = { Text("Permiso de cámara requerido") },
        text = {
            Text(
                "Para tomar fotos de las observaciones necesitamos acceso a " +
                        "tu cámara. Este permiso solo se usa para adjuntar imágenes a " +
                        "tus registros de campo."
            )
        },
        confirmButton = {
            TextButton(onClick = enConfirmar) {
                Text("Conceder permiso")
            }
        },
        dismissButton = {
            TextButton(onClick = enDescartar) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Abre la pantalla de configuración de la aplicación en los Ajustes del sistema.
 * Útil cuando el permiso fue denegado permanentemente ([EstadoPermisoCamara.denegadoPermanente]
 * = `true`) y el usuario debe habilitarlo manualmente desde
 * [Settings.ACTION_APPLICATION_DETAILS_SETTINGS].
 *
 * @param context Contexto de Android necesario para lanzar el [Intent].
 */
fun abrirAjustesPermiso(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
