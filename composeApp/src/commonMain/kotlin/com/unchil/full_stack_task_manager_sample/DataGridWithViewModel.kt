package com.unchil.full_stack_task_manager_sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
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

import androidx.lifecycle.viewmodel.compose.viewModel
import com.unchil.full_stack_task_manager_sample.theme.AppTheme
import com.unchil.full_stack_task_manager_sample.viewmodel.MofSeaWaterInfoViewModel
import com.unchil.full_stack_task_manager_sample.viewmodel.NifsSeaWaterInfoViewModel
import com.unchil.un7datagrid.Un7KCMPDataGrid
import com.unchil.un7datagrid.toMap
import kotlinx.coroutines.launch

val LocalPlatform = compositionLocalOf<Platform> { error("No Platform found!") }


@Composable
fun DataGridWithViewModel(
    viewModel: NifsSeaWaterInfoViewModel = viewModel { NifsSeaWaterInfoViewModel() }
){
    val platform = LocalPlatform.current

    LaunchedEffect(key1 = viewModel){
        viewModel.onEvent(NifsSeaWaterInfoViewModel.Event.Refresh)
    }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val reloadData :()->Unit = {
        coroutineScope.launch{
            viewModel.onEvent(NifsSeaWaterInfoViewModel.Event.Refresh)
        }
    }

    val seaWaterInfo = viewModel._seaWaterInfo.collectAsState()

    var isVisible by remember { mutableStateOf(false) }
    val columnNames = remember { mutableStateOf(emptyList<String>() ) }
    val data = remember { mutableStateOf(emptyList<List<Any?>>()) }
    val gridData = remember { mutableStateOf(mutableMapOf<String, List<Any?>>() ) }


    LaunchedEffect(seaWaterInfo.value){
        isVisible = seaWaterInfo.value.isNotEmpty()
        if(isVisible){
            columnNames.value = seaWaterInfo.value.first().makeGridColumns()
            data.value = seaWaterInfo.value.map {
                it.toGridData()
            }

            gridData.value = Pair(columnNames.value, data.value).toMap()
        }
    }

    AppTheme(enableDarkMode=false){
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().safeDrawingPadding() // Android/iOS의 Safe Area(상태바 등)를 자동으로 계산하여 패딩 추가
        ) {
            val isLandscape = maxWidth > maxHeight
            val titleVerticalPadding = remember {  mutableStateOf(10.dp) }
            val titleAreaHeight = remember {  mutableStateOf(24.dp + titleVerticalPadding.value*2) }
            val modifier = when(platform.alias){
                PlatformAlias.ANDROID -> {
                    Modifier.width(maxWidth ).height(maxHeight - titleAreaHeight.value  )
                }
                PlatformAlias.IOS -> {
                    Modifier.width(maxWidth).height(maxHeight - titleAreaHeight.value  )
                }
                PlatformAlias.JVM -> {
                    Modifier.fillMaxWidth(0.95f).height(500.dp ).padding(0.dp)
                }
                PlatformAlias.WASM -> {
                    Modifier.fillMaxWidth(0.95f).height(640.dp ).padding(0.dp)
                }

                PlatformAlias.JS -> TODO()
            }

            Column(
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                //   .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "Korea Ocean Water Information",
                    modifier = Modifier.padding( vertical = titleVerticalPadding.value),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )

                if (isVisible) {
                    Un7KCMPDataGrid(
                        gridData.value,
                        modifier = modifier,
                        onClick = {
                                rowsData->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Selected Rows : ${rowsData}"
                                )
                            }
                        },
                        onLongClick = {
                                rowsData->
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

            } //--- Column

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )




        } //--- BoxWithConstraints
    }
}