package com.mbds.bpst.parcoursnfc.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.android.gms.maps.model.LatLng
import com.mbds.bpst.parcoursnfc.data.dao.ParcoursDao
import com.mbds.bpst.parcoursnfc.data.entities.Etape
import com.mbds.bpst.parcoursnfc.data.entities.Parcours
import com.mbds.bpst.parcoursnfc.data.entities.ParcoursWithEtapes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = arrayOf(Parcours::class, Etape::class), version = 1)
@TypeConverters(LocationConverters::class)

abstract class ParcoursRoomDatabase : RoomDatabase() {
    abstract fun parcoursDao(): ParcoursDao

    private class ParcoursDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {

                    populateDatabase(database.parcoursDao())
                }
            }
        }

        suspend fun populateDatabase(parcoursDao: ParcoursDao) {
            // Delete all content here.
            //parcoursDao.deleteAll()

            var parcours1 = Parcours("Parcours 1")
            var indice3 = "Fin du parcours"
            var latLng3 = LatLng(0.000000, 0.000000)
            var etape3 = Etape(indice3, latLng3)

            var indice2 = "Près de la cafetière"
            var latLng2= LatLng(43.615115,7.061264)
            var etape2 = Etape(indice2, latLng2)

            var indice1 = "Point de départ"
            var latLng1 = LatLng(43.750234, 7.072342)
            var etape1 = Etape(indice1, latLng1)

            var listEtape = listOf<Etape>(etape1, etape2, etape3)
            // Add sample articles.

            parcoursDao.insertParcours(parcours1)
            parcoursDao.insertEtape(etape1)
            parcoursDao.insertEtape(etape2)
            parcoursDao.insertEtape(etape3)
            parcoursDao.insertParcoursWithEtapes(parcours1,listEtape)


            //article = Article("World!")
            //articleDao.insert(article)

            // TODO: Add your own articles!
        }
    }


    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ParcoursRoomDatabase? = null
        //private val LOCK = Any()
        fun destroyDataBase(context: Context, scope: CoroutineScope): ParcoursRoomDatabase{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ParcoursRoomDatabase::class.java,
                    "parcours_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }

        fun getDatabase(context: Context,
                        scope: CoroutineScope
        ): ParcoursRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ParcoursRoomDatabase::class.java,
                    "parcours_database"
                ).addCallback(ParcoursDatabaseCallback(scope)).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}