package com.unchil.full_stack_task_manager_sample.chart


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp


@Composable
fun CaptionText(text: String, modifier:Modifier=Modifier) {
    Text(text, fontStyle = FontStyle.Italic, style = MaterialTheme.typography.bodySmall, modifier=modifier)
}


@Composable
fun ChartTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
fun LegendTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        title,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier,
    )
}

@Composable
fun AxisTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        title,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier,
    )
}

@Composable
fun AxisLabel(
    label: String,
    modifier: Modifier = Modifier,
) {
    Text(
        label,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
    )
}

@Composable
fun BoxPlotTooltips(
    text: String,
    color:Color? = null
) {
    Text(
        text,
        textAlign= TextAlign.Center,
        style = TextStyle(fontSize=TextUnit(14f, TextUnitType.Sp)),
        modifier = Modifier
            .padding(vertical = 1.dp)
            .fillMaxWidth()
            .background( color = color ?: Color.Gray, shape = ShapeDefaults.Small),
        color = Color.White)

}

@Composable
fun HoverSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        shadowElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        color = Color.LightGray,
        modifier = modifier.padding(padding),
    ) {
        Box(modifier = paddingMod) {
            content()
        }
    }
}

