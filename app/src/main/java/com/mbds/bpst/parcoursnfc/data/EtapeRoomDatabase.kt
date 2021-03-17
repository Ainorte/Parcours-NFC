package com.mbds.bpst.parcoursnfc.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.android.gms.maps.model.LatLng
import com.mbds.bpst.parcoursnfc.data.dao.EtapeDao
import com.mbds.bpst.parcoursnfc.data.entities.Etape
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = arrayOf(Etape::class), version = 3, exportSchema = false)
@TypeConverters(LocationConverters::class)

abstract class EtapeRoomDatabase : RoomDatabase() {
    abstract fun etapeDao(): EtapeDao

    private class EtapeDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
    }


    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: EtapeRoomDatabase? = null
        //private val LOCK = Any()
        fun destroyDataBase(context: Context, scope: CoroutineScope): EtapeRoomDatabase{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EtapeRoomDatabase::class.java,
                    "parcours_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }

        fun getDatabase(context: Context,
                        scope: CoroutineScope
        ): EtapeRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EtapeRoomDatabase::class.java,
                    "parcours_database"
                )
                    .allowMainThreadQueries()
                    .addCallback(EtapeDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}