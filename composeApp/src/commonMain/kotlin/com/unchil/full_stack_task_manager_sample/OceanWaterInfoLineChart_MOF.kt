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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unchil.full_stack_task_manager_sample.chart.AxisConfig
import com.unchil.full_stack_task_manager_sample.chart.CaptionConfig
import com.unchil.full_stack_task_manager_sample.chart.ChartType
import com.unchil.full_stack_task_manager_sample.chart.ComposePlot
import com.unchil.full_stack_task_manager_sample.chart.LayoutData
import com.unchil.full_stack_task_manager_sample.chart.LegendConfig
import com.unchil.full_stack_task_manager_sample.chart.SizeConfig
import com.unchil.full_stack_task_manager_sample.chart.TitleConfig
import com.unchil.full_stack_task_manager_sample.viewmodel.MofSeaWaterInfoViewModel
import com.unchil.full_stack_task_manager_sample.chart.WATER_QUALITY
import com.unchil.full_stack_task_manager_sample.chart.WATER_QUALITY.desc
import com.unchil.full_stack_task_manager_sample.chart.WATER_QUALITY.name
import com.unchil.full_stack_task_manager_sample.chart.WATER_QUALITY.unit
import com.unchil.full_stack_task_manager_sample.chart.toMofLineMap
import io.github.koalaplot.core.xygraph.AxisModel
import io.github.koalaplot.core.xygraph.AxisStyle
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.DoubleLinearAxisModel
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import kotlinx.coroutines.delay

@Composable
fun OceanWaterInfoLineChart_MOF(){

    val coroutineScope = rememberCoroutineScope()

    val viewModel: MofSeaWaterInfoViewModel = remember {
        MofSeaWaterInfoViewModel( coroutineScope )
    }

    LaunchedEffect(key1 = viewModel){
        viewModel.onEvent(MofSeaWaterInfoViewModel.Event.Refresh)
        while(true){
            delay(10 * 60 * 1000L).let{
                viewModel.onEvent(MofSeaWaterInfoViewModel.Event.Refresh)
            }
        }
    }

    var isVisible by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(WATER_QUALITY.QualityType.entries[0]) }

    val seaWaterInfo = viewModel._seaWaterInfo.collectAsState()

    val entries = remember { mutableStateOf(emptyList<String>() ) }
    val xValue = remember { mutableStateOf(emptyList<Double>()) }
    val rawData = remember { mutableStateOf(mapOf<String, List<Float>>() ) }
    val chartLayout = remember { mutableStateOf(LayoutData() )}
    val range = remember { mutableStateOf(0f..0f)}

    LaunchedEffect(key1= seaWaterInfo.value, key2=selectedOption){

        isVisible = seaWaterInfo.value.isNotEmpty()

        if(isVisible) {

            val legendTitle = "Observatory"
            val data = seaWaterInfo.value.toMofLineMap(selectedOption)
            entries.value = data["entries"] as List<String>
            xValue.value = data["xValue" ] as List<Double>
            rawData.value = data["values" ] as Map<String, List<Float>>


            val min = when(selectedOption){
                WATER_QUALITY.QualityType.rtmWtchWtem -> 0f
                WATER_QUALITY.QualityType.rtmWqCndctv -> 10f
                WATER_QUALITY.QualityType.ph -> 7f
                WATER_QUALITY.QualityType.rtmWqDoxn -> 0f
                WATER_QUALITY.QualityType.rtmWqTu -> 0f
                WATER_QUALITY.QualityType.rtmWqChpla -> 0f
                WATER_QUALITY.QualityType.rtmWqSlnty -> 6f
            }

            val max = rawData.value.maxOf { it.value.maxOf { it } }
            range.value =  min..( max + (max * 0.1f) )


            val xRange = xValue.value.min()..xValue.value.max()

            chartLayout.value = LayoutData(
                size = SizeConfig(height = 500.dp),
                type = ChartType.Line,
                layout = TitleConfig(true, "24-hour Ocean Water Information"),
                legend = LegendConfig(true, true, legendTitle),
                xAxis = AxisConfig("Collecting Time",
                    model = DoubleLinearAxisModel(xRange) as AxisModel<Any>,
                    style = AxisStyle(labelRotation = 45),

                ),
                yAxis = AxisConfig(
                    "${selectedOption.unit()}",
                    model = FloatLinearAxisModel(range.value) as AxisModel<Any>
                ),
                caption = CaptionConfig(true,
                    "from https://www.mof.go.kr (Ministry of Oceans and Fisheries)"
                ),
            )

        }
    }

    Column (modifier = paddingMod) {
        if (isVisible) {
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

            Text(
                text = selectedOption.desc(),
                modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                textAlign = TextAlign.Center
            )

            ComposePlot(
                layout = chartLayout.value,
                data = rawData.value,
                xValues = xValue.value,
                entries = entries.value
            )

        }
    }


}