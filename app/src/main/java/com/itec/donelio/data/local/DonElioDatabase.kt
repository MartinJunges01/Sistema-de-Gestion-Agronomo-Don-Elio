package com.itec.donelio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.itec.donelio.data.local.dao.CampaniaDao
import com.itec.donelio.data.local.dao.CampaniaInsumoDao
import com.itec.donelio.data.local.dao.CosechaDao
import com.itec.donelio.data.local.dao.CosechaNoAlmacenadaDao
import com.itec.donelio.data.local.dao.InsumoDao
import com.itec.donelio.data.local.dao.ObservacionDao
import com.itec.donelio.data.local.dao.TareaDao
import com.itec.donelio.data.local.dao.UsuarioDao
import com.itec.donelio.data.local.dao.CultivoDao
import com.itec.donelio.data.local.entity.CampaniaEntity
import com.itec.donelio.data.local.entity.CampaniaInsumoEntity
import com.itec.donelio.data.local.entity.CosechaEntity
import com.itec.donelio.data.local.entity.CosechaNoAlmacenadaEntity
import com.itec.donelio.data.local.entity.InsumoEntity
import com.itec.donelio.data.local.entity.ObservacionEntity
import com.itec.donelio.data.local.entity.TareaEntity
import com.itec.donelio.data.local.entity.UsuarioEntity
import com.itec.donelio.data.local.entity.CultivoEntity

@Database(
    entities = [
        CampaniaEntity::class,
        TareaEntity::class,
        CosechaEntity::class,
        CosechaNoAlmacenadaEntity::class,
        InsumoEntity::class,
        ObservacionEntity::class,
        CampaniaInsumoEntity::class,
        UsuarioEntity::class,
        CultivoEntity::class
    ],
    version = 7, // Subimos a 7 para el ABM de Cultivos
    exportSchema = false
)
abstract class DonElioDatabase : RoomDatabase() {

    abstract val campaniaDao: CampaniaDao
    abstract val tareaDao: TareaDao
    abstract val cosechaDao: CosechaDao
    abstract val insumoDao: InsumoDao
    abstract val campaniaInsumoDao: CampaniaInsumoDao
    abstract val observacionDao: ObservacionDao
    abstract val cosechaNoAlmacenadaDao: CosechaNoAlmacenadaDao
    abstract val usuarioDao: UsuarioDao
    abstract val cultivoDao: CultivoDao
}