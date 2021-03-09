package com.mbds.bpst.parcoursnfc.data.entities

import androidx.lifecycle.LiveData
import androidx.room.Embedded
import androidx.room.Relation

data class ParcoursWithEtapes (
    @Embedded val parcours: Parcours,
    @Relation(
        parentColumn = "parcoursId",
        entityColumn = "parcoursId"
            )
    val etapes: List<Etape>
        )