package com.mbds.bpst.parcoursnfc.data

import android.app.Application
import com.mbds.bpst.parcoursnfc.data.repository.ParcoursRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class ParcoursApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    private val database by lazy {
        ParcoursRoomDatabase.destroyDataBase(this, applicationScope)
        ParcoursRoomDatabase.getDatabase(this, applicationScope)
    }
    val repository by lazy { ParcoursRepository(database.parcoursDao()) }

}