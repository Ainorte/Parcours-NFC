package com.mbds.bpst.parcoursnfc.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity
data class Etape (
    @PrimaryKey val etapeId: Long,
    @ColumnInfo(name = "indice") val indice: String,
    @ColumnInfo(name = "location") val location: LatLng,
    @ColumnInfo(name = "parcoursId") val parcoursId: Long
)