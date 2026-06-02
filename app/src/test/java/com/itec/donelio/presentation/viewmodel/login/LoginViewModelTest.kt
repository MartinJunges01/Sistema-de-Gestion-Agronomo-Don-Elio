package com.itec.donelio.presentation.viewmodel.login

import app.cash.turbine.test
import com.itec.donelio.domain.model.Usuario
import com.itec.donelio.domain.use_case.LoginUseCase
import com.itec.donelio.domain.use_case.RegistroUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var registroUseCase: RegistroUseCase
    private lateinit var loginViewModel: LoginViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        loginUseCase = mockk()
        registroUseCase = mockk()
        loginViewModel = LoginViewModel(loginUseCase, registroUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with valid credentials updates state to loginExitoso`() = runTest {
        // Given
        val nombreUsuario = "DonElio"
        val contrasena = "123456"
        val usuario = Usuario(1, "Don Elio", nombreUsuario, contrasena, 1L)
        
        coEvery { loginUseCase(nombreUsuario, contrasena) } returns usuario

        // When
        loginViewModel.state.test {
            // Initial state
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)
            assertFalse(initialState.loginExitoso)

            // Trigger action
            loginViewModel.login(nombreUsuario, contrasena)

            // Loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            // Success state
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertTrue(successState.loginExitoso)
            assertEquals(null, successState.error)
            
            // Should not receive any more items
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login with invalid credentials updates state with error`() = runTest {
        // Given
        val nombreUsuario = "Intruso"
        val contrasena = "123456"
        
        coEvery { loginUseCase(nombreUsuario, contrasena) } returns null

        // When
        loginViewModel.state.test {
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)

            loginViewModel.login(nombreUsuario, contrasena)

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertFalse(errorState.loginExitoso)
            assertEquals("Credenciales inválidas", errorState.error)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login throwing exception updates state with error`() = runTest {
        // Given
        val nombreUsuario = "  "
        val contrasena = "123456"
        
        coEvery { loginUseCase(any(), any()) } throws IllegalArgumentException("El nombre de usuario no puede estar vacío")

        // When
        loginViewModel.state.test {
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)

            loginViewModel.login(nombreUsuario, contrasena)

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertFalse(errorState.loginExitoso)
            assertEquals("El nombre de usuario no puede estar vacío", errorState.error)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}
