package com.mbds.bpst.parcoursnfc.data.repository

import androidx.annotation.WorkerThread
import com.mbds.bpst.parcoursnfc.data.dao.EtapeDao
import com.mbds.bpst.parcoursnfc.data.entities.Etape

class EtapeRepository(private val parcoursDao: EtapeDao) {

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    suspend fun insert(etape: Etape) {
        parcoursDao.insertEtape(etape)
    }

    suspend fun delete(etape: Etape){
        parcoursDao.deleteEtape(etape)
    }

    suspend fun getAllEtapes(): List<Etape> {
        return parcoursDao.getAllEtape()
    }

    suspend fun getAllEtapeByRead(read: Boolean): List<Etape>{
        return parcoursDao.getAllEtapeByRead(read)
    }



}