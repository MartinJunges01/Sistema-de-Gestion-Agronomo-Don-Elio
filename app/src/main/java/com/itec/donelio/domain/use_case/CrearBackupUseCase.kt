package com.itec.donelio.domain.use_case

import android.app.Application
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import javax.inject.Inject

class CrearBackupUseCase @Inject constructor(
    private val application: Application
) {
    suspend operator fun invoke(outputUri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val sourceDb = application.getDatabasePath("don_elio_db")
            if (!sourceDb.exists()) {
                return@withContext Result.failure(
                    Exception("No se encontró la base de datos local")
                )
            }

            val outputStream = application.contentResolver.openOutputStream(outputUri)
                ?: return@withContext Result.failure(
                    Exception("No se pudo abrir el archivo de destino")
                )

            outputStream.use { out ->
                FileInputStream(sourceDb).use { inp ->
                    inp.copyTo(out)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
