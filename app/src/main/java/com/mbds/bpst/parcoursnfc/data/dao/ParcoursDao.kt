package com.mbds.bpst.parcoursnfc.data.dao

import androidx.room.*
import com.mbds.bpst.parcoursnfc.data.entities.Etape
import com.mbds.bpst.parcoursnfc.data.entities.Parcours
import com.mbds.bpst.parcoursnfc.data.entities.ParcoursWithEtapes

@Dao
interface ParcoursDao {
    @Transaction
    @Query("SELECT * FROM parcours")
    fun getAll(): List<Parcours>


    @Query("SELECT * FROM parcours WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Parcours

    @Insert
    fun insert(vararg parcours: Parcours)

    @Delete
    fun delete(parcours: Parcours)
}