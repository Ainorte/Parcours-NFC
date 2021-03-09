package com.mbds.bpst.parcoursnfc.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Etape (
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "indice") val indice: String?,
    @ColumnInfo(name = "location") val location: String?
)