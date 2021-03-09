package com.mbds.bpst.parcoursnfc.data.repository

import androidx.annotation.WorkerThread
import com.mbds.bpst.parcoursnfc.data.dao.ParcoursDao
import com.mbds.bpst.parcoursnfc.data.entities.Etape
import com.mbds.bpst.parcoursnfc.data.entities.Parcours

class EtapeRepository(private val parcoursDao: ParcoursDao) {

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(etape: Etape) {
        parcoursDao.insertEtape(etape)
    }

    @WorkerThread
    suspend fun delete(etape: Etape){
        parcoursDao.deleteEtape(etape)
    }

    @JvmName("getAllEtape1")
    fun getAllEtapes(): List<Etape> {
        return parcoursDao.getAllEtape()
    }


}