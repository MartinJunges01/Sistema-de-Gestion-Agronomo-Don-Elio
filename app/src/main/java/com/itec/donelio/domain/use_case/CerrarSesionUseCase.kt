package com.itec.donelio.domain.use_case

import com.itec.donelio.core.SessionManager
import javax.inject.Inject

class CerrarSesionUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke() {
        sessionManager.logout()
    }
}
