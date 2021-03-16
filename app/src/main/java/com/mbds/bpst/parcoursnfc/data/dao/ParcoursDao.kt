package com.mbds.bpst.parcoursnfc.data.dao

import androidx.room.*
import com.mbds.bpst.parcoursnfc.data.entities.Etape
import com.mbds.bpst.parcoursnfc.data.entities.Parcours
import com.mbds.bpst.parcoursnfc.data.entities.ParcoursWithEtapes

@Dao
interface ParcoursDao {
    @Transaction
    @Query("SELECT * FROM parcours")
    fun getAllParcours(): List<Parcours>

    @Query("SELECT * FROM etape WHERE status LIKE :read ORDER BY id")
    fun getAllEtapeByRead(read: Boolean): List<Etape>

    @Query("SELECT * FROM etape")
    fun getAllEtape(): List<Etape>

    @Query("SELECT * FROM parcours WHERE name LIKE :name LIMIT 1")
    fun findParcoursByName(name: String): Parcours


    @Insert
    fun insertParcours(vararg parcours: Parcours)

    @Delete
    fun deleteParcours(parcours: Parcours)


    @Insert
    fun insertEtape(vararg etape: Etape)

    @Delete
    fun deleteEtape(etape: Etape)

    @Insert
    fun insertParcoursWithEtapes(parcours: Parcours, etapes: List<Etape>)


}