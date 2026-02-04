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
import com.unchil.full_stack_task_manager_sample.chart.SizeConfig
import com.unchil.full_stack_task_manager_sample.chart.TitleConfig
import com.unchil.full_stack_task_manager_sample.chart.toBarChartMap
import com.unchil.full_stack_task_manager_sample.viewmodel.NifsSeaWaterInfoCurrentViewModel
import io.github.koalaplot.core.xygraph.AxisModel
import io.github.koalaplot.core.xygraph.AxisStyle
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import kotlinx.coroutines.delay

@Composable
fun OceanWaterInfoBarChart(){

    val coroutineScope = rememberCoroutineScope()

    val viewModel: NifsSeaWaterInfoCurrentViewModel = remember {
        NifsSeaWaterInfoCurrentViewModel(  coroutineScope  )
    }

    LaunchedEffect(key1 = viewModel){
        viewModel.onEvent(NifsSeaWaterInfoCurrentViewModel.Event.Refresh)
        while(true){
            delay(10 * 60 * 1000L).let{
                viewModel.onEvent(NifsSeaWaterInfoCurrentViewModel.Event.Refresh)
            }
        }
    }

    var isVisible by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(SEA_AREA.GRU_NAME.entries[0]) }

    val seaWaterInfo = viewModel._seaWaterInfo.collectAsState()

    val data = remember { mutableStateOf(mapOf<String, List<*>?>() ) }
    val entries = remember { mutableStateOf(emptyList<String>() ) }
    val xValue = remember { mutableStateOf(emptyList<String>()) }
    val values = remember { mutableStateOf(emptyList<Float>() ) }
    val chartLayout = remember { mutableStateOf(LayoutData() )}
    val range = remember { mutableStateOf(0f..0f)}

    LaunchedEffect(seaWaterInfo.value, selectedOption){

         val tempData = seaWaterInfo.value.filter {
             it.gru_nam.equals(selectedOption.gru_nam()) &&  it.obs_lay == "1"
         }


        isVisible = tempData.size > 0

        if(selectedOption == SEA_AREA.GRU_NAME.SOUTH) isVisible = false

        if(isVisible){
            val legendTitle = "Observatory"
            data.value = tempData.toBarChartMap()
            entries.value = data.value["entries"] as List<String>
            xValue.value = entries.value
            values.value = data.value["values"] as List<Float>
            val yMax = values.value.maxBy { it }
            range.value = 0f..(yMax + (yMax * 0.1f) )

            chartLayout.value = LayoutData(
                type = ChartType.VerticalBar,
                layout = TitleConfig(true, "${tempData.first().obs_datetime}  Surface Temperature"),
                legend = LegendConfig(true, true, legendTitle),
                xAxis = AxisConfig(
                    legendTitle,
                    range = range.value,
                    //   model = FloatLinearAxisModel( 0.5f..(rawData.value.size.toFloat() + 0.5f))
                    model = CategoryAxisModel(xValue.value),
                    style = AxisStyle(labelRotation = 45)
                ),
                yAxis = AxisConfig(
                    "Water Temperature °C",
                    range = range.value,
                    model = FloatLinearAxisModel(range.value) as AxisModel<Any>
                ),
                size = SizeConfig(height = 400.dp),
                caption = CaptionConfig(true,
                    "from https://www.nifs.go.kr (National Institute of Fisheries Science)"
                ),
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
        }
    }



}