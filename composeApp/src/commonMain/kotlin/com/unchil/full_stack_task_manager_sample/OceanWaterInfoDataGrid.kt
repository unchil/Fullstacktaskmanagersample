package com.unchil.full_stack_task_manager_sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unchil.full_stack_task_manager_sample.chart.toGridDataMap
import com.unchil.full_stack_task_manager_sample.theme.AppTheme
import com.unchil.full_stack_task_manager_sample.viewmodel.NifsSeaWaterInfoCurrentViewModel
import com.unchil.full_stack_task_manager_sample.viewmodel.NifsSeaWaterInfoViewModel
import com.unchil.un7datagrid.Un7KCMPDataGrid
import com.unchil.un7datagrid.Un7KCMPDataGridConfig
import com.unchil.un7datagrid.toMap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val LocalPlatform = compositionLocalOf<Platform> { error("No Platform found!") }


@Composable
fun OceanWaterInfoDataGrid(){


    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var isVisible by remember { mutableStateOf(false) }
    val gridData = remember { mutableStateOf(mapOf<String, List<Any?>>() ) }

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

    val reloadData :()->Unit = {
        coroutineScope.launch{
            viewModel.onEvent(NifsSeaWaterInfoCurrentViewModel.Event.Refresh)
        }
    }

    val seaWaterInfo = viewModel._seaWaterInfo.collectAsState()

    LaunchedEffect(seaWaterInfo.value){
        isVisible = seaWaterInfo.value.isNotEmpty()
        if(isVisible){
            gridData.value = seaWaterInfo.value.toGridDataMap()
        }
    }

    AppTheme(enableDarkMode=false){
        Box{
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "Recent Surface Sea Temperature Data",
                    modifier = Modifier.padding(vertical = 6.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                )

                if (isVisible) {
                    Un7KCMPDataGrid(
                        gridData.value,
                        Un7KCMPDataGridConfig(gridHeight = 500.dp),
                        onClick = { rowsData ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Selected Rows : ${rowsData}"
                                )
                            }
                        },
                        onLongClick = { rowsData ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Selected Rows : ${rowsData}"
                                )
                            }
                        },
                    )

                    Text(
                        "from https://www.nifs.go.kr/openApi/actionOpenapiInfoList.do#fnContentsView",
                        modifier = Modifier.fillMaxWidth().padding(end = 40.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 10.sp,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Right
                    )
                }


            }//--- Column

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

        }


    }
}