package com.mbds.bpst.parcoursnfc.models

class Parcours {
    var etapes: MutableList<Etape>

    constructor(etapes: MutableList<Etape>){
        this.etapes = etapes
    }
}