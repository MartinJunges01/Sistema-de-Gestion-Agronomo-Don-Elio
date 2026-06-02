package com.itec.donelio.domain.use_case

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File

class CrearBackupUseCaseTest {

    private lateinit var application: Application
    private lateinit var contentResolver: ContentResolver
    private lateinit var crearBackupUseCase: CrearBackupUseCase

    @Before
    fun setUp() {
        application = mockk()
        contentResolver = mockk()
        every { application.contentResolver } returns contentResolver
        crearBackupUseCase = CrearBackupUseCase(application)
    }

    @Test
    fun `invoke fails when local database does not exist`() = runTest {
        // Given
        val mockUri: Uri = mockk()
        val mockFile: File = mockk()
        
        every { application.getDatabasePath("don_elio_db") } returns mockFile
        every { mockFile.exists() } returns false

        // When
        val result = crearBackupUseCase(mockUri)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message == "No se encontró la base de datos local")
    }

    @Test
    fun `invoke fails when output stream cannot be opened`() = runTest {
        // Given
        val mockUri: Uri = mockk()
        val mockFile: File = mockk()
        
        every { application.getDatabasePath("don_elio_db") } returns mockFile
        every { mockFile.exists() } returns true
        every { contentResolver.openOutputStream(mockUri) } returns null

        // When
        val result = crearBackupUseCase(mockUri)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message == "No se pudo abrir el archivo de destino")
    }
}
