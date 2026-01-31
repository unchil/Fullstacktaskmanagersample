package com.unchil.full_stack_task_manager_sample.viewmodel

import com.unchil.full_stack_task_manager_sample.DATA_DIVISION
import com.unchil.full_stack_task_manager_sample.OceanWaterRepository
import com.unchil.full_stack_task_manager_sample.SeawaterInformationByObservationPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NifsSeaWaterInfoCurrentViewModel ( scope:  CoroutineScope){


    private val repository = OceanWaterRepository()

    val _seaWaterInfo: MutableStateFlow<List<SeawaterInformationByObservationPoint>>
            = MutableStateFlow(emptyList())

    init {

        scope.launch {
            repository._seaWaterInfoCurrentStateFlow.collectLatest {
                _seaWaterInfo.value = it
            }
        }
    }

    suspend fun onEvent(event: NifsSeaWaterInfoCurrentViewModel.Event) {
        when (event) {
            is NifsSeaWaterInfoCurrentViewModel.Event.Refresh -> {
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