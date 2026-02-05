package com.unchil.full_stack_task_manager_sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unchil.full_stack_task_manager_sample.chart.AxisConfig
import com.unchil.full_stack_task_manager_sample.chart.CaptionConfig
import com.unchil.full_stack_task_manager_sample.chart.ChartType
import com.unchil.full_stack_task_manager_sample.chart.ComposePlot
import com.unchil.full_stack_task_manager_sample.chart.EmptyChart
import com.unchil.full_stack_task_manager_sample.chart.LayoutData
import com.unchil.full_stack_task_manager_sample.chart.LegendConfig
import com.unchil.full_stack_task_manager_sample.chart.SizeConfig
import com.unchil.full_stack_task_manager_sample.chart.TitleConfig
import com.unchil.full_stack_task_manager_sample.chart.WATER_QUALITY
import com.unchil.full_stack_task_manager_sample.chart.WATER_QUALITY.desc
import com.unchil.full_stack_task_manager_sample.chart.WATER_QUALITY.name
import com.unchil.full_stack_task_manager_sample.chart.WATER_QUALITY.unit
import com.unchil.full_stack_task_manager_sample.chart.paddingMod
import com.unchil.full_stack_task_manager_sample.chart.toMofLineTriple
import com.unchil.full_stack_task_manager_sample.viewmodel.MofSeaWaterInfoViewModel
import io.github.koalaplot.core.xygraph.AxisStyle
import io.github.koalaplot.core.xygraph.DoubleLinearAxisModel
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import kotlinx.coroutines.delay

@Composable
fun OceanWaterInfoLineChart_MOF(viewModel: MofSeaWaterInfoViewModel){


    var isVisible by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(WATER_QUALITY.QualityType.entries[0]) }

    val seaWaterInfo = viewModel._seaWaterInfo.collectAsState()

    val entries = remember { mutableStateOf(emptyList<String>() ) }
    val xValue = remember { mutableStateOf(emptyList<Double>()) }
    val values = remember { mutableStateOf(mapOf<String, List<Float>>() ) }
    val chartLayout = remember { mutableStateOf(LayoutData() )}


    val maxTurbidity = remember { 500f}
    val minElectricalConductivity = remember { 20f}
    val minDissolvedOxygen = remember { 7f }
    val minSalinity = remember { 15f }
    val minHydrogenIonConcentration = remember { 6f }
    val chartHeight = remember {500.dp}
    val chartTitle = remember {"24-hour Ocean Water Information"}
    val chartXTitle = remember { "DateTime"}
    val chartCaption = remember {"from https://www.mof.go.kr (Ministry of Oceans and Fisheries)"}


    LaunchedEffect(key1= seaWaterInfo.value, key2=selectedOption){

        isVisible = seaWaterInfo.value.isNotEmpty()

        if(isVisible) {

            val legendTitle = "Observatory"
            val chartData = seaWaterInfo.value.toMofLineTriple(selectedOption)

            val currentEntries = chartData.first
            val currentXValues = chartData.second
            val currentValues = chartData.third

            // 상태 업데이트
            xValue.value = currentXValues
            entries.value = currentEntries
            values.value = currentValues


            val min = when(selectedOption){
                WATER_QUALITY.QualityType.rtmWtchWtem -> currentValues.minOf { it.value.minOf { it } }
                WATER_QUALITY.QualityType.rtmWqCndctv -> {
                    if( currentValues.minOf { it.value.minOf { it } } < minElectricalConductivity) {
                        minElectricalConductivity
                    } else {
                        currentValues.minOf { it.value.minOf { it } }
                    }
                }
                WATER_QUALITY.QualityType.ph -> {
                    if( currentValues.minOf { it.value.minOf { it } } < minHydrogenIonConcentration) {
                        minHydrogenIonConcentration
                    } else {
                        currentValues.minOf { it.value.minOf { it } }
                    }
                }
                WATER_QUALITY.QualityType.rtmWqDoxn -> {
                    if( currentValues.minOf { it.value.minOf { it } } < minDissolvedOxygen) {
                        minDissolvedOxygen
                    } else {
                        currentValues.minOf { it.value.minOf { it } }
                    }
                }
                WATER_QUALITY.QualityType.rtmWqTu -> currentValues.minOf { it.value.minOf { it } }
                WATER_QUALITY.QualityType.rtmWqChpla -> currentValues.minOf { it.value.minOf { it } }
                WATER_QUALITY.QualityType.rtmWqSlnty -> {
                    if( currentValues.minOf { it.value.minOf { it } } < minSalinity) {
                        minSalinity
                    } else {
                        currentValues.minOf { it.value.minOf { it } }
                    }
                }
            }
            val max = when(selectedOption){
                WATER_QUALITY.QualityType.rtmWqTu -> {
                    if( currentValues.maxOf { it.value.maxOf { it } } > maxTurbidity) {
                        maxTurbidity
                    } else {
                        currentValues.maxOf { it.value.maxOf { it } }
                    }
                }
                else -> {
                    currentValues.maxOf { it.value.maxOf { it } }
                }
            }
            val range =  min..( max * 1.1f )
            val xRange = xValue.value.min()..xValue.value.max()

            chartLayout.value = LayoutData(
                type = ChartType.Line,
                layout = TitleConfig(true, "${chartTitle} (${selectedOption.name()})", description = selectedOption.desc()),
                legend = LegendConfig(true, true, legendTitle),
                xAxis = AxisConfig(
                    model = DoubleLinearAxisModel(xRange) ,
                    style = AxisStyle(labelRotation = 0),
                ),
                yAxis = AxisConfig(
                    selectedOption.unit(),
                    range = range,
                    model = FloatLinearAxisModel(range)
                ),
                size = SizeConfig(chartHeight),
                caption = CaptionConfig(true,chartCaption  ),
            )

        }else {
            chartLayout.value = LayoutData(
                layout = TitleConfig(true, chartTitle),
                legend = LegendConfig(false, true, chartXTitle),
                xAxis = AxisConfig(chartXTitle),
                yAxis = AxisConfig( selectedOption.unit()),
                size = SizeConfig(height = chartHeight),
                caption = CaptionConfig(true,  chartCaption  )
            )
        }
    }

    Column (modifier = paddingMod) {

        if (isVisible) {

            ComposePlot(
                layout = chartLayout.value,
                data = values.value,
                xValues = xValue.value,
                entries = entries.value
            )
        }else{
            EmptyChart(chartLayout.value )
        }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                WATER_QUALITY.QualityType.entries.forEach { entrie ->
                    Row(
                        Modifier
                            .selectable(
                                selected = (entrie == selectedOption),
                                onClick = { selectedOption = entrie }
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (entrie == selectedOption),
                            onClick = { selectedOption = entrie }
                        )
                        Text(
                            text = entrie.name(),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }


    }


}