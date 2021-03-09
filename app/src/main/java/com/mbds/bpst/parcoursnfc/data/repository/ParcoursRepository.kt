package com.mbds.bpst.parcoursnfc.data.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.mbds.bpst.parcoursnfc.data.dao.ParcoursDao
import com.mbds.bpst.parcoursnfc.data.entities.Parcours

class ParcoursRepository(private val parcoursDao: ParcoursDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allParcours: List<Parcours> = parcoursDao.getAll() //LiveData to change to Flow

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(parcours: Parcours) {
        parcoursDao.insert(parcours)
    }

    @WorkerThread
    suspend fun delete(parcours: Parcours){
        parcoursDao.delete(parcours)
    }

    @JvmName("getAllParcourss1")
    fun getAllParcourss(): List<Parcours> {
        return parcoursDao.getAll()
    }


}