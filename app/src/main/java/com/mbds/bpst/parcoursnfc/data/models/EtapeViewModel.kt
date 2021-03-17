package com.mbds.bpst.parcoursnfc.data.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mbds.bpst.parcoursnfc.data.entities.Etape
import com.mbds.bpst.parcoursnfc.data.repository.EtapeRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class EtapeViewModel(private val repository: EtapeRepository): ViewModel() {


    suspend fun getAllEtape() = repository.getAllEtapes()

    suspend fun getAllEtapeByRead(read: Boolean) = repository.getAllEtapeByRead(read)


    fun insert(etape: Etape) = viewModelScope.launch {
        repository.insert(etape)
    }

    fun deleteEtape(etape: Etape) = viewModelScope.launch {
        repository.delete(etape)
    }

}

class EtapeViewModelFactory(private val repository: EtapeRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EtapeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EtapeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}