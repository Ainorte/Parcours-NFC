package com.mbds.bpst.parcoursnfc.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.android.gms.maps.model.LatLng
import com.mbds.bpst.parcoursnfc.data.dao.ParcoursDao
import com.mbds.bpst.parcoursnfc.data.entities.Etape
import com.mbds.bpst.parcoursnfc.data.entities.Parcours
import com.mbds.bpst.parcoursnfc.data.entities.ParcoursWithEtapes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Parcours::class), version = 1)
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
            var parcours1 = Parcours(1, "Parcours 1")
            var listEtape: List<Etape> = emptyList()
            var indice3 = "Fin du parcours"
            var latLng3= LatLng(0.000000, 0.000000)
            var etape3 = Etape(3, indice3, latLng3, parcours1.parcoursId)

            var indice2 = "Près de la cafetière"
            var latLng2= LatLng(43.615115,7.061264)
            var etape2 = Etape(2, indice2, latLng2, parcours1.parcoursId)

            var indice1 = "Point de départ"
            var latLng1 = LatLng(43.750234, 7.072342)
            var etape1 = Etape(1, indice1, latLng1, parcours1.parcoursId)



            // Add sample articles.

            //parcoursDao.insert(parcours1, )
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