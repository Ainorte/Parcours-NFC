package com.mbds.bpst.parcoursnfc.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mbds.bpst.parcoursnfc.data.entities.Etape
import com.mbds.bpst.parcoursnfc.data.entities.Parcours

@Dao
interface ParcoursDao {
    @Query("SELECT * FROM parcours")
    fun getAll(): List<Parcours>

    @Query("SELECT * FROM parcours WHERE name IN (:parcours)")
    fun loadAllByIds(parcours: MutableList<Parcours>): MutableList<Etape>

    @Query("SELECT * FROM parcours WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Parcours

    @Insert
    fun insert(vararg parcours: Parcours)

    @Delete
    fun delete(parcours: Parcours)
}