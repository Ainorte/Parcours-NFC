package com.mbds.bpst.parcoursnfc.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ParcoursWithEtapes (
    @Embedded val parcours: Parcours,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
            )
    val etapes: List<Etape>
        )