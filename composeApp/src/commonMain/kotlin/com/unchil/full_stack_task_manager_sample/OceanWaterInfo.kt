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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unchil.full_stack_task_manager_sample.theme.AppTheme


val padding = 8.dp
 val paddingMod = Modifier.padding(padding)

@Composable
fun OceanWaterInfo(){

    AppTheme(enableDarkMode=false) {

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

        }
    }

}