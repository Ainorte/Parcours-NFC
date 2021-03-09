package com.mbds.bpst.parcoursnfc.data.repository

import androidx.annotation.WorkerThread
import com.mbds.bpst.parcoursnfc.data.dao.ParcoursDao
import com.mbds.bpst.parcoursnfc.data.entities.Etape
import com.mbds.bpst.parcoursnfc.data.entities.Parcours

class EtapeRepository(private val parcoursDao: ParcoursDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allEtape: List<Etape> = parcoursDao.getAllEtape() //LiveData to change to Flow

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(parcours: Etape) {
        parcoursDao.insertEtape(parcours)
    }

    @WorkerThread
    suspend fun delete(parcours: Etape){
        parcoursDao.deleteEtape(parcours)
    }

    @JvmName("getAllEtape1")
    fun getAllEtapes(): List<Etape> {
        return parcoursDao.getAllEtape()
    }


}