package com.unchil.full_stack_task_manager_sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
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
import kotlin.text.compareTo


val padding = 8.dp
 val paddingMod = Modifier.padding(padding)



@Composable
fun OceanWaterInfo(){

    AppTheme(enableDarkMode=false) {

            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
                    .safeDrawingPadding() // Android/iOS의 Safe Area(상태바 등)를 자동으로 계산하여 패딩 추가
            ) {

                Surface(
                    shadowElevation = 2.dp,
                    modifier = Modifier.padding(10.dp),
                    shape = RoundedCornerShape(6.dp)

                ) {


                Column(
                    modifier = paddingMod.fillMaxSize().padding(0.dp)
                        .verticalScroll(rememberScrollState())
                        .safeContentPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Text(
                        "Korea Ocean Water Information",
                        modifier = Modifier.padding(vertical = 20.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    OceanWaterInfoDataGrid()
                    HorizontalDivider(modifier = Modifier.padding(10.dp))
                    OceanWaterInfoBarChart()
                    HorizontalDivider(modifier = Modifier.padding(10.dp))
                    OceanWaterInfoLineChart()

                }


            } //--- BoxWithConstraints

        }
    }


}