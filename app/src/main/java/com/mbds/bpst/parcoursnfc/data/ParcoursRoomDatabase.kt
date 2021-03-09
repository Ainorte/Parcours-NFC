package com.mbds.bpst.parcoursnfc.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mbds.bpst.parcoursnfc.data.dao.ParcoursDao
import com.mbds.bpst.parcoursnfc.data.entities.Parcours

@Database(entities = arrayOf(Parcours::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun parcoursDao(): ParcoursDao
}