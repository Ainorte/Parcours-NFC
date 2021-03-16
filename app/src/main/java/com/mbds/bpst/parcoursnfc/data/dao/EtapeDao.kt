package com.mbds.bpst.parcoursnfc.data.dao

import androidx.room.*
import com.mbds.bpst.parcoursnfc.data.entities.Etape

@Dao
interface EtapeDao {

    @Query("SELECT * FROM etape WHERE status LIKE :read ORDER BY id")
    suspend fun getAllEtapeByRead(read: Boolean): List<Etape>

    @Query("SELECT * FROM etape")
    suspend fun getAllEtape(): List<Etape>

    @Insert
    suspend fun insertEtape(vararg etape: Etape)

    @Delete
    suspend fun deleteEtape(etape: Etape)


}