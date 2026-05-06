package com.itec.donelio.domain.repository

interface CampaniaRepository {
    // Lectura reactiva: Devuelve un flujo de la lista de modelos de dominio
    fun getCampanias(): Flow<List<Campania>>

    // Obtener una por ID
    suspend fun getCampaniaById(id: Int): Campania?

    // Operaciones de escritura
    suspend fun insertCampania(campania: Campania)

    suspend fun updateCampania(campania: Campania)

    suspend fun deleteCampania(campania: Campania)
}