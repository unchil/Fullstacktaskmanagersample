package com.unchil.full_stack_task_manager_sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unchil.composedatagrid.theme.getColorScheme
import com.unchil.composedatagrid.theme.getTypography
import com.unchil.full_stack_task_manager_sample.chart.paddingMod
import com.unchil.full_stack_task_manager_sample.viewmodel.MofSeaWaterInfoViewModel
import com.unchil.full_stack_task_manager_sample.viewmodel.NifsSeaWaterInfoCurrentViewModel
import com.unchil.full_stack_task_manager_sample.viewmodel.NifsSeaWaterInfoViewModel
import kotlinx.coroutines.delay


@Composable
fun OceanWaterInfo(){

    val coroutineScope = rememberCoroutineScope()

    val viewModelCurrent: NifsSeaWaterInfoCurrentViewModel = remember {
        NifsSeaWaterInfoCurrentViewModel(  coroutineScope  )
    }

    val viewModelOneDay: NifsSeaWaterInfoViewModel = remember {
        NifsSeaWaterInfoViewModel( coroutineScope )
    }

    val viewModelMofOneDay: MofSeaWaterInfoViewModel = remember {
        MofSeaWaterInfoViewModel( coroutineScope )
    }


    LaunchedEffect(key1 = viewModelCurrent, key2 = viewModelOneDay, key3 = viewModelMofOneDay){
        while(true){
            delay(1 * 60 * 1000L).let{
                viewModelCurrent.onEvent(NifsSeaWaterInfoCurrentViewModel.Event.Refresh)
                viewModelOneDay.onEvent(NifsSeaWaterInfoViewModel.Event.Refresh)
                viewModelMofOneDay.onEvent(MofSeaWaterInfoViewModel.Event.Refresh)
            }
        }
    }


    MaterialTheme(
        typography = getTypography(),
        colorScheme = getColorScheme(false)
    ) {

        Surface(
            shadowElevation = 2.dp,
            modifier = Modifier.padding(10.dp),
            shape = RoundedCornerShape(6.dp)

        ) {

            Column(
                modifier = paddingMod.fillMaxSize()
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

                OceanWaterInfoDataGrid(viewModel = viewModelCurrent)
                HorizontalDivider(modifier = Modifier.padding(10.dp))
                OceanWaterInfoBarChart(viewModel = viewModelCurrent)
                HorizontalDivider(modifier = Modifier.padding(10.dp))
                OceanWaterInfoBoxPlotChart(viewModelOneDay)
                HorizontalDivider(modifier = Modifier.padding(10.dp))
                OceanWaterInfoLineChart(viewModelOneDay)
                HorizontalDivider(modifier = Modifier.padding(10.dp))
                OceanWaterInfoLineChart_MOF(viewModelMofOneDay)



            }

        }
    }

}