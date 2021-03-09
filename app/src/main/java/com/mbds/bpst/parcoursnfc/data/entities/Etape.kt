package com.mbds.bpst.parcoursnfc.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity
data class Etape (

    @ColumnInfo(name = "indice") val indice: String?,
    @PrimaryKey
    @ColumnInfo(name = "location") val location: LatLng?
)