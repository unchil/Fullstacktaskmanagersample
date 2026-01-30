package com.unchil.full_stack_task_manager_sample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unchil.full_stack_task_manager_sample.DATA_DIVISION
import com.unchil.full_stack_task_manager_sample.OceanWaterRepository
import com.unchil.full_stack_task_manager_sample.SeawaterInformationByObservationPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NifsSeaWaterInfoViewModel : ViewModel(){

    private val repository = OceanWaterRepository()

    val _seaWaterInfo: MutableStateFlow<List<SeawaterInformationByObservationPoint>>
            = MutableStateFlow(emptyList())


    init {
        viewModelScope.launch {
            repository._seaWaterInfoOneDayStateFlow.collectLatest {
                _seaWaterInfo.value = it
            }

        }
    }

    suspend fun onEvent(event: NifsSeaWaterInfoViewModel.Event) {
        when (event) {
            is NifsSeaWaterInfoViewModel.Event.Refresh -> {
                getSeaWaterInfo()

            }
        }
    }

    suspend fun getSeaWaterInfo(){
        repository.getSeaWaterInfo(DATA_DIVISION.oneday)
    }



    sealed class Event {
        object Refresh : Event()
    }

}