package com.unchil.full_stack_task_manager_sample.data

import com.unchil.full_stack_task_manager_sample.Observatory
import com.unchil.full_stack_task_manager_sample.SeaWaterInfoByOneHourStat
import com.unchil.full_stack_task_manager_sample.SeawaterInformationByObservationPoint


interface RepositoryInterface {
    suspend fun fetchSeaWaterInfoFromDb(division:String, ):List<SeawaterInformationByObservationPoint>
    suspend fun fetchSeaWaterInfoStatisticsFromDb():List<SeaWaterInfoByOneHourStat>
    suspend fun observatoryInfo():List<Observatory>
}