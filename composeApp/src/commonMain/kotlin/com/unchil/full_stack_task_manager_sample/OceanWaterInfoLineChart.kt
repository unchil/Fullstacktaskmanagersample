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
import com.unchil.full_stack_task_manager_sample.chart.LayoutData
import com.unchil.full_stack_task_manager_sample.chart.LegendConfig
import com.unchil.full_stack_task_manager_sample.chart.TitleConfig
import com.unchil.full_stack_task_manager_sample.chart.toLineMap
import com.unchil.full_stack_task_manager_sample.viewmodel.NifsSeaWaterInfoViewModel
import io.github.koalaplot.core.xygraph.AxisModel
import io.github.koalaplot.core.xygraph.AxisStyle
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.DoubleLinearAxisModel
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import kotlinx.coroutines.delay


@Composable
fun OceanWaterInfoLineChart(){

    val coroutineScope = rememberCoroutineScope()

    val viewModel: NifsSeaWaterInfoViewModel = remember {
        NifsSeaWaterInfoViewModel( coroutineScope )
    }

    LaunchedEffect(key1 = viewModel){
        viewModel.onEvent(NifsSeaWaterInfoViewModel.Event.Refresh)
        while(true){
            delay(10 * 60 * 1000L).let{
                viewModel.onEvent(NifsSeaWaterInfoViewModel.Event.Refresh)
            }
        }
    }

    var isVisible by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(SEA_AREA.GRU_NAME.entries[0]) }

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
            val data = seaWaterInfo.value.toLineMap(selectedOption.gru_nam())
            entries.value = data["entries"] as List<String>
            xValue.value = data["xValue" ] as List<Double>
            rawData.value = data["values" ] as Map<String, List<Float>>
            val max = rawData.value.maxOf { it.value.maxOf { it } }
            range.value =  0f..( max + (max * 0.1f) )

            val xRange = xValue.value.min()..xValue.value.max()

            chartLayout.value = LayoutData(
                type = ChartType.Line,
                layout = TitleConfig(true, "24-hour Surface Sea Temperature"),
                legend = LegendConfig(true, true, legendTitle),
                xAxis = AxisConfig("Collecting Time",
                    model = DoubleLinearAxisModel(xRange) as AxisModel<Any>,
                    style = AxisStyle(labelRotation = 0)
                ),
                yAxis = AxisConfig(
                    "Water Temperature °C",
                    model = FloatLinearAxisModel(range.value) as AxisModel<Any>
                ),
                caption = CaptionConfig(true,
                    "from https://www.nifs.go.kr (National Institute of Fisheries Science)"
                ),
            )

        }
    }

    Column (modifier = paddingMod) {
        if (isVisible) {
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
            ComposePlot(
                layout = chartLayout.value,
                data = rawData.value,
                xValues = xValue.value,
                entries = entries.value
            )

        }
    }



}

