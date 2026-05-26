package com.itec.donelio.domain.use_case

import android.app.Application
import android.net.Uri
import com.itec.donelio.data.local.DonElioDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import javax.inject.Inject

class RestaurarBackupUseCase @Inject constructor(
    private val database: DonElioDatabase,
    private val application: Application
) {
    suspend operator fun invoke(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val headerBytes = application.contentResolver.openInputStream(uri)?.use { stream ->
                val header = ByteArray(16)
                val bytesRead = stream.read(header)
                if (bytesRead < 16) {
                    return@use null
                }
                header
            } ?: return@withContext Result.failure(
                Exception("No se pudo leer el archivo seleccionado")
            )

            val expectedHeader = "SQLite format 3\u0000".toByteArray(Charsets.UTF_8)
            if (!headerBytes.contentEquals(expectedHeader)) {
                return@withContext Result.failure(
                    Exception("Archivo inválido: no es una base de datos SQLite")
                )
            }

            database.close()

            val destFile = application.getDatabasePath("don_elio_db")
            application.contentResolver.openInputStream(uri)?.use { inp ->
                FileOutputStream(destFile).use { out ->
                    inp.copyTo(out)
                }
            } ?: return@withContext Result.failure(
                Exception("No se pudo leer el archivo de respaldo")
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
