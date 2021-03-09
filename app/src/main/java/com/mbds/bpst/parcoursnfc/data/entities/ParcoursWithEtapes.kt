package com.mbds.bpst.parcoursnfc.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ParcoursWithEtapes (
    val parcours: Parcours,
    /*@Relation(
        parentColumn = "id",
        entityColumn = "id"
            )
    @Embedded*/
    val etapes: List<Etape>
        )