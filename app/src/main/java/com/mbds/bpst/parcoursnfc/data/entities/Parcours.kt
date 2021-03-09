package com.mbds.bpst.parcoursnfc.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mbds.bpst.parcoursnfc.data.entities.Etape

@Entity
data class Parcours (
    @PrimaryKey val uid: Int,
    @ColumnInfo(name= "name") val name: String?,
    @ColumnInfo(name = "etapes") val etape: MutableList<Etape>?
)