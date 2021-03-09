package com.mbds.bpst.parcoursnfc.data.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mbds.bpst.parcoursnfc.data.entities.Parcours
import com.mbds.bpst.parcoursnfc.data.repository.ParcoursRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class ParcoursViewModel(private val repository: ParcoursRepository): ViewModel() {


    fun getAllParcours() = repository.allParcours


    fun insert(parcours: Parcours) = viewModelScope.launch {
        repository.insert(parcours)
    }

    fun deleteParcours(parcours: Parcours) = viewModelScope.launch {
        repository.delete(parcours)
    }

}

class ParcoursViewModelFactory(private val repository: ParcoursRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParcoursViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ParcoursViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
