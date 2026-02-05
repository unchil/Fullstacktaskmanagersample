package com.unchil.full_stack_task_manager_sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.unchil.full_stack_task_manager_sample.SEA_AREA.gru_nam
import com.unchil.full_stack_task_manager_sample.chart.AxisConfig
import com.unchil.full_stack_task_manager_sample.chart.CaptionConfig
import com.unchil.full_stack_task_manager_sample.chart.ChartType
import com.unchil.full_stack_task_manager_sample.chart.ComposePlot
import com.unchil.full_stack_task_manager_sample.chart.EmptyChart
import com.unchil.full_stack_task_manager_sample.chart.LayoutData
import com.unchil.full_stack_task_manager_sample.chart.LegendConfig
import com.unchil.full_stack_task_manager_sample.chart.SizeConfig
import com.unchil.full_stack_task_manager_sample.chart.TitleConfig
import com.unchil.full_stack_task_manager_sample.chart.paddingMod
import com.unchil.full_stack_task_manager_sample.chart.toLineTriple
import com.unchil.full_stack_task_manager_sample.viewmodel.NifsSeaWaterInfoViewModel
import io.github.koalaplot.core.xygraph.AxisModel
import io.github.koalaplot.core.xygraph.AxisStyle
import io.github.koalaplot.core.xygraph.DoubleLinearAxisModel
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import kotlinx.coroutines.delay


@Composable
fun OceanWaterInfoLineChart(viewModel: NifsSeaWaterInfoViewModel){



    var isVisible by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(SEA_AREA.GRU_NAME.entries[0]) }

    val seaWaterInfo = viewModel._seaWaterInfo.collectAsState()

    val entries = remember { mutableStateOf(emptyList<String>() ) }
    val xValue = remember { mutableStateOf(emptyList<Double>()) }
    val values = remember { mutableStateOf(mapOf<String, List<Float>>() ) }
    val chartLayout = remember { mutableStateOf(LayoutData() )}
    val chartHeight = remember {400.dp}
    val chartTitle = remember {"24-hour Surface Sea Temperature"}
    val chartXTitle = remember { "DateTime"}
    val chartYTitle = remember { "Water Temperature °C"}
    val chartCaption = remember {"from https://www.nifs.go.kr (National Institute of Fisheries Science)"}


    LaunchedEffect(key1= seaWaterInfo.value, key2=selectedOption){

        val filteredList = seaWaterInfo.value.filter {
            it.gru_nam.equals(selectedOption.gru_nam()) &&  it.obs_lay == "1"
        }
        isVisible = filteredList.isNotEmpty()

        if(isVisible) {

            val chartData = filteredList.toLineTriple()

            val currentEntries = chartData.first
            val currentXValues = chartData.second
            val currentValues = chartData.third

            // 상태 업데이트
            xValue.value = currentXValues
            entries.value = currentEntries
            values.value = currentValues

            val yMax = currentValues.maxOf { it.value.maxOf { it } }
            val range = 0f..(yMax * 1.1f)
            val xRange = currentXValues.min()..currentXValues.max()

            chartLayout.value = LayoutData(
                type = ChartType.Line,
                layout = TitleConfig(true, chartTitle),
                legend = LegendConfig(true, true, "Observatory"),
                xAxis = AxisConfig(
                    model = DoubleLinearAxisModel(xRange) ,
                    style = AxisStyle(labelRotation = 0)
                ),
                yAxis = AxisConfig(
                    chartYTitle,
                    range = range,
                    model = FloatLinearAxisModel(range)
                ),
                size = SizeConfig(height = chartHeight),
                caption = CaptionConfig(true,chartCaption ),
            )

        }else {
            chartLayout.value = LayoutData(
                layout = TitleConfig(true, chartTitle),
                legend = LegendConfig(false, true, chartXTitle),
                xAxis = AxisConfig(chartXTitle),
                yAxis = AxisConfig( chartYTitle),
                size = SizeConfig(height = chartHeight),
                caption = CaptionConfig(true,  chartCaption  )
            )
        }
    }



    Column (modifier = paddingMod) {

            Row {
                SEA_AREA.GRU_NAME.entries.forEach { entrie ->
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
                            text = entrie.name,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }
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
    }



}

