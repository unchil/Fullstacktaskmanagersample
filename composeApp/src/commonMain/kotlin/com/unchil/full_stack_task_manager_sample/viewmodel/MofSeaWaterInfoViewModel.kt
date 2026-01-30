package com.unchil.full_stack_task_manager_sample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unchil.full_stack_task_manager_sample.DATA_DIVISION
import com.unchil.full_stack_task_manager_sample.OceanWaterRepository
import com.unchil.full_stack_task_manager_sample.SeaWaterInformation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MofSeaWaterInfoViewModel: ViewModel() {

    private val repository = OceanWaterRepository()

    val _seaWaterInfo: MutableStateFlow<List<SeaWaterInformation>>
            = MutableStateFlow(emptyList())

    init {
        viewModelScope.launch {
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