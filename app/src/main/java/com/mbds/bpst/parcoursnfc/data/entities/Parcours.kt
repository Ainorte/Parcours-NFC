package com.mbds.bpst.parcoursnfc.data.entities

import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mbds.bpst.parcoursnfc.data.entities.Etape

@Entity
data class Parcours (
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "name") val name: String,
    //@ColumnInfo(name = "etapes") val etapes: List<Etape>
)