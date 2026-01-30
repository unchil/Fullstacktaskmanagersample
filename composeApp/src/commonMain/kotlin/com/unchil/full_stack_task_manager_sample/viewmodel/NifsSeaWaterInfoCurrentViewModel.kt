package com.unchil.full_stack_task_manager_sample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unchil.full_stack_task_manager_sample.DATA_DIVISION
import com.unchil.full_stack_task_manager_sample.OceanWaterRepository
import com.unchil.full_stack_task_manager_sample.SeawaterInformationByObservationPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NifsSeaWaterInfoCurrentViewModel : ViewModel(){

    private val repository = OceanWaterRepository()

    val _seaWaterInfo: MutableStateFlow<List<SeawaterInformationByObservationPoint>>
            = MutableStateFlow(emptyList())


    init {
        viewModelScope.launch {

            repository._seaWaterInfoCurrentStateFlow.collectLatest {
                _seaWaterInfo.value = it
            }
        }
    }

    suspend fun onEvent(event: NifsSeaWaterInfoViewModel.Event) {
        when (event) {
            is NifsSeaWaterInfoViewModel.Event.Refresh -> {
                getSeaWaterInfoCurrent()
            }
        }
    }


    suspend fun getSeaWaterInfoCurrent(){
        repository.getSeaWaterInfo(DATA_DIVISION.current)
    }

    sealed class Event {
        object Refresh : Event()
    }

}