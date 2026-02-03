package com.unchil.full_stack_task_manager_sample.viewmodel

import com.unchil.full_stack_task_manager_sample.DATA_DIVISION
import com.unchil.full_stack_task_manager_sample.OceanWaterRepository
import com.unchil.full_stack_task_manager_sample.SeaWaterBoxPlotStat
import com.unchil.full_stack_task_manager_sample.SeawaterInformationByObservationPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.text.trim

class NifsSeaWaterInfoViewModel ( scope:  CoroutineScope){

    private val repository = OceanWaterRepository()

    val _seaWaterInfo: MutableStateFlow<List<SeawaterInformationByObservationPoint>>
            = MutableStateFlow(emptyList())


    init {
        scope.launch {
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