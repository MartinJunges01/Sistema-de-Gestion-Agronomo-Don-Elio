package com.itec.donelio.domain.use_case

import com.itec.donelio.core.SessionManager
import javax.inject.Inject

class GuardarSesionUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(nombre: String) {
        sessionManager.saveUserName(nombre)
    }
}
