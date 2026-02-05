package com.unchil.full_stack_task_manager_sample.viewmodel

import com.unchil.full_stack_task_manager_sample.DATA_DIVISION
import com.unchil.full_stack_task_manager_sample.OceanWaterRepository
import com.unchil.full_stack_task_manager_sample.SeaWaterInformation
import com.unchil.full_stack_task_manager_sample.getPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MofSeaWaterInfoViewModel( scope:  CoroutineScope){

    private val repository = getPlatform().repository

    val _seaWaterInfo: MutableStateFlow<List<SeaWaterInformation>>
            = MutableStateFlow(emptyList())

    init {
        scope.launch {
            repository._seaWaterInfoOneDayMofStateFlow.collectLatest {
                _seaWaterInfo.value = it
            }
        }
    }

    suspend fun onEvent(event: MofSeaWaterInfoViewModel.Event) {
        when (event) {
            is MofSeaWaterInfoViewModel.Event.Refresh -> {
                getSeaWaterInfo()
            }
        }
    }

    suspend fun getSeaWaterInfo(){
        repository.getSeaWaterInfo(DATA_DIVISION.mof_oneday)
    }


    sealed class Event {
        object Refresh : Event()
    }
}