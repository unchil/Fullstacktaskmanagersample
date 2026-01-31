package com.unchil.full_stack_task_manager_sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unchil.full_stack_task_manager_sample.chart.AxisConfig
import com.unchil.full_stack_task_manager_sample.chart.CaptionConfig
import com.unchil.full_stack_task_manager_sample.chart.ChartType
import com.unchil.full_stack_task_manager_sample.chart.ComposePlot
import com.unchil.full_stack_task_manager_sample.chart.LayoutData
import com.unchil.full_stack_task_manager_sample.chart.LegendConfig
import com.unchil.full_stack_task_manager_sample.chart.SizeConfig
import com.unchil.full_stack_task_manager_sample.chart.TitleConfig
import com.unchil.full_stack_task_manager_sample.theme.AppTheme
import com.unchil.full_stack_task_manager_sample.viewmodel.NifsSeaWaterInfoCurrentViewModel
import com.unchil.un7datagrid.Un7KCMPDataGrid
import com.unchil.un7datagrid.toMap
import io.github.koalaplot.core.xygraph.AxisStyle
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel


 val padding = 8.dp
 val paddingMod = Modifier.padding(padding)



@Composable
fun OceanWaterInfoBarChart(){

    val coroutineScope = rememberCoroutineScope()

    val viewModel: NifsSeaWaterInfoCurrentViewModel = remember {
        NifsSeaWaterInfoCurrentViewModel(
            coroutineScope
        )
    }

    LaunchedEffect(key1 = viewModel){
        viewModel.onEvent(NifsSeaWaterInfoCurrentViewModel.Event.Refresh)
    }
    val seaWaterInfo = viewModel._seaWaterInfo.collectAsState()

    val gridData = remember { mutableStateOf(mutableMapOf<String, List<Any?>>() ) }
    val entries = remember { mutableStateOf(emptyList<String>() ) }
    val xValue = remember { mutableStateOf(emptyList<String>()) }
    val rawData = remember { mutableStateOf(emptyList<Float>() ) }
    val chartLayout = remember { mutableStateOf(LayoutData() )}
    val range = remember { mutableStateOf(0f..0f)}

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(seaWaterInfo.value){

        isVisible = seaWaterInfo.value.isNotEmpty()
        if(isVisible){
            val columnNames  = seaWaterInfo.value.first().makeGridColumns()
            val data = seaWaterInfo.value.filter{ it.obs_lay == "1"}.map {
                it.toGridData()
            }
            gridData.value = Pair(columnNames, data).toMap()
            entries.value = gridData.value["Observation Station Name"] as List<String>
            xValue.value = entries.value
            rawData.value = gridData.value["Water Temperature"]?.map { it.toString().toFloat() }!!

            val yMax = rawData.value.maxBy { it }
            range.value = 0f..(yMax + (yMax * 0.1f) )


            chartLayout.value = LayoutData(
                type = ChartType.VerticalBar,
                category =  xValue.value,
                layout = TitleConfig(false, "Korea Ocean Water Information"),
                legend = LegendConfig(true, true, "Observation Station Name"),
                xAxis = AxisConfig(
                    "Observation Station Name",
                    range = range.value,
                    //   model = FloatLinearAxisModel( 0.5f..(rawData.value.size.toFloat() + 0.5f))
                    model = CategoryAxisModel(xValue.value),
                    style = AxisStyle(labelRotation = 45)
                ),
                yAxis = AxisConfig(
                    "Water Temperature °C",
                    range = range.value,
                    model = FloatLinearAxisModel(range.value)
                ),
                size = SizeConfig(height = 600.dp),
                caption = CaptionConfig(true, "from https://www.nifs.go.kr/openApi/actionOpenapiInfoList.do#fnContentsView"),
            )
        }
    }



    AppTheme(enableDarkMode=false){

        Surface (
            shadowElevation = 2.dp,
            modifier = Modifier.padding(10.dp),
            shape = RoundedCornerShape(6.dp)

            ){
            Column(
                modifier = paddingMod.fillMaxSize().verticalScroll(rememberScrollState()  )
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .safeContentPadding()
                ,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Text(
                    "Korea Ocean Water Information",
                    modifier = Modifier.padding( vertical = 40.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )



                if (isVisible) {


                    Text(
                        "Un7-KCMP-DataGrid",
                        modifier = Modifier.padding( vertical = 10.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Un7KCMPDataGrid(
                        data = gridData.value,
                        modifier = Modifier.height(400.dp)
                    )



                    HorizontalDivider(modifier = Modifier.padding(20.dp))

                    Text(
                        "Koala Plot",
                        modifier = Modifier.padding( vertical = 10.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
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

    }



}