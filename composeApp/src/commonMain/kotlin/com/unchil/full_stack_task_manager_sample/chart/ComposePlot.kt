package com.unchil.full_stack_task_manager_sample.chart


import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.bar.BarPlotGroupedPointEntry
import io.github.koalaplot.core.bar.BarPosition
import io.github.koalaplot.core.bar.DefaultBar
import io.github.koalaplot.core.bar.DefaultBarPosition
import io.github.koalaplot.core.bar.DefaultVerticalBarPlotEntry
import io.github.koalaplot.core.bar.GroupedVerticalBarPlot
import io.github.koalaplot.core.bar.VerticalBarPlot
import io.github.koalaplot.core.bar.VerticalBarPlotEntry
import io.github.koalaplot.core.legend.ColumnLegend
import io.github.koalaplot.core.legend.FlowLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.LinePlot2
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.XYGraphScope
import io.github.koalaplot.core.xygraph.rememberAxisStyle
import kotlin.toString


@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun ComposePlot(
    layout: LayoutData,
    data:Any,
    xValues:Any,
    entries:List<String>
){
    val colors = getColors(entries)


    Box( modifier = Modifier.fillMaxWidth()
        .height(layout.size.height)
        .background(color = MaterialTheme.colorScheme.background),
        contentAlignment =  Alignment.Center
    ) {
        ChartLayout(
            modifier = paddingMod.sizeIn(minHeight = layout.size.minHeight, maxHeight = layout.size.maxHeight)
                .background(color = MaterialTheme.colorScheme.background),
            title = {
                if (layout.layout.isTitle) {
                    ChartTitle(layout.layout.title, modifier = paddingMod)
                }
            },
            legend = {
                if(layout.legend.isUsable ) {
                    Legend(layout, entries, colors)
                }
            },
            legendLocation = layout.legend.location
        ) {

            XYGraph(
                xAxisModel = when (layout.type) {
                    ChartType.Line -> layout.xAxis.model as CategoryAxisModel<*>
                    ChartType.VerticalBar -> layout.xAxis.model as CategoryAxisModel<*>
                    ChartType.GroupVerticalBar -> layout.xAxis.model as CategoryAxisModel<*>
                    ChartType.XYGraph -> layout.xAxis.model as CategoryAxisModel<*>
                },
                xAxisLabels = {
                    if (layout.xAxis.isLabels) {
                        AxisLabel(
                            it.toString(),
                            Modifier.padding(top = 2.dp)
                        )
                    }
                },
                xAxisStyle = rememberAxisStyle(
                    color = layout.xAxis.style?.color ?:  KoalaPlotTheme.axis.color,
                    majorTickSize = layout.xAxis.style?.majorTickSize ?:  KoalaPlotTheme.axis.majorTickSize,
                    minorTickSize = layout.xAxis.style?.minorTickSize ?:  KoalaPlotTheme.axis.minorTickSize,
                    tickPosition =  layout.xAxis.style?.tickPosition ?:  KoalaPlotTheme.axis.xyGraphTickPosition,
                    lineWidth = layout.xAxis.style?.lineWidth ?:  KoalaPlotTheme.axis.lineThickness,
                    labelRotation = layout.xAxis.style?.labelRotation ?:  0,
                ),
                xAxisTitle = {
                    if (layout.xAxis.isTitle) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            AxisTitle(layout.xAxis.title, paddingMod)
                        }
                    }
                },
                yAxisModel = when (layout.type) {
                    ChartType.XYGraph -> layout.yAxis.model as FloatLinearAxisModel
                    ChartType.VerticalBar -> layout.yAxis.model as FloatLinearAxisModel
                    ChartType.GroupVerticalBar -> layout.yAxis.model as FloatLinearAxisModel
                    ChartType.Line -> layout.yAxis.model as FloatLinearAxisModel
                },
                yAxisLabels = {
                    if (layout.yAxis.isLabels) {
                        AxisLabel(it.toString(), Modifier.absolutePadding(right = 2.dp))
                    }
                },
                yAxisStyle = rememberAxisStyle(
                    color = layout.yAxis.style?.color ?:  KoalaPlotTheme.axis.color,
                    majorTickSize = layout.yAxis.style?.majorTickSize ?:  KoalaPlotTheme.axis.majorTickSize,
                    minorTickSize = layout.yAxis.style?.minorTickSize ?:  KoalaPlotTheme.axis.minorTickSize,
                    tickPosition =  layout.yAxis.style?.tickPosition ?:  KoalaPlotTheme.axis.xyGraphTickPosition,
                    lineWidth = layout.yAxis.style?.lineWidth ?:  KoalaPlotTheme.axis.lineThickness,
                    labelRotation = layout.yAxis.style?.labelRotation ?:  0,
                ),
                yAxisTitle = {
                    if (layout.yAxis.isTitle) {
                        Box(
                            modifier = Modifier.fillMaxHeight(),
                            contentAlignment = Alignment.Center,
                        ) {
                            AxisTitle(
                                layout.yAxis.title,
                                modifier = paddingMod
                                    .rotateVertically(VerticalRotation.COUNTER_CLOCKWISE)
                            )
                        }
                    }
                },
            ) {

                when(layout.type){
                    ChartType.Line -> {
                        val scope = this as XYGraphScope<String, Float>
                        (data as Map<String, List<Double>>).entries.sortedBy { it.key }.forEach { (key, values) ->
                            scope.LineChart(
                                key,
                                values = values.mapIndexed { index, value ->
                                    DefaultPoint((xValues as List<String>)[index], value.toFloat())
                                },
                                layout.tooltips.isTooltips,
                                colors
                            )
                        }
                    }
                    ChartType.VerticalBar -> {
                        val scope = this as XYGraphScope<String, Float>
                        scope.VerticalBarChart(
                            (data as List<Float>),
                            (xValues as List<String>),
                            layout.tooltips.isTooltips,
                            colors,
                            layout.barConf.widthWeight
                        )
                    }
                    ChartType.GroupVerticalBar -> {
                        val scope = this as XYGraphScope<Int, Float>
                        scope.GroupVerticalBarChart(
                            (data as Map<String,List<Int>>),
                            (xValues as List<Int>),
                            layout.tooltips.isTooltips,
                            colors,
                        )
                    }

                    ChartType.XYGraph -> TODO()
                }


            } //-- XYGraph

        } //-- ChartLayout



        if (layout.caption.isCaption) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = layout.caption.location
            ) {
                CaptionText(layout.caption.title, modifier = paddingMod)
            }
        } //-- Caption


    }
}



@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun Legend(
    layout: LayoutData,
    entries:List<String>,
    colors: Map<String, Color>
){
    val defaultSize = remember {  24.dp}


    val modifier = when(layout.legend.location){
        LegendLocation.LEFT,LegendLocation.RIGHT -> {
            if( (layout.size.height * 0.6f) < ( entries.size.toFloat() * defaultSize )){
                paddingMod.height(layout.size.height * 0.6f)
                    .verticalScroll(rememberScrollState())
                    .horizontalScroll(rememberScrollState())
            } else{
                paddingMod
                    .verticalScroll(rememberScrollState())
                    .horizontalScroll(rememberScrollState())
            }
        }
        LegendLocation.TOP ,LegendLocation.BOTTOM ,LegendLocation.NONE -> {
            paddingMod
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
        }
    }

    Surface(
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceBright,
        modifier = paddingMod,
        shape = RoundedCornerShape(6.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            if(layout.legend.isTitle){
                LegendTitle(layout.legend.title, paddingMod)
            }

            Box(modifier = modifier) {
                when (layout.legend.location) {
                    LegendLocation.LEFT, LegendLocation.RIGHT, LegendLocation.NONE -> {
                        ColumnLegend(
                            itemCount = entries.size,
                            symbol = { i ->
                                Symbol(
                                    modifier = Modifier.size(padding),
                                    fillBrush = SolidColor(colors[entries[i]] ?: Color.Black),
                                )
                            },
                            label = { i ->
                                Text(entries[i])
                            },
                            modifier = paddingMod,
                        )
                    }

                    LegendLocation.TOP, LegendLocation.BOTTOM -> {
                        FlowLegend(
                            itemCount = entries.size,
                            symbol = { i ->
                                Symbol(
                                    modifier = Modifier.size(padding),
                                    fillBrush = SolidColor(colors[entries[i]] ?: Color.Black),
                                )
                            },
                            label = { i ->
                                Text(entries[i])
                            },
                            modifier = paddingMod,
                        )

                    }

                }
            }
        }
    }

}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalKoalaPlotApi::class)
@Composable
fun XYGraphScope<String, Float>.LineChart(
    key: String,
    values: List<DefaultPoint<String, Float>>,
    usableTooltips: Boolean,
    colors: Map<String, Color>
) {
    LinePlot2(
        data = values,
        lineStyle = LineStyle(
            brush = SolidColor(colors[key] ?: Color.Black),
            strokeWidth = 2.dp),
        symbol = { point ->
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    TooltipAnchorPosition.Above),
                tooltip = {
                    if (usableTooltips) {
                        PlainTooltip { Text("${key}\n${point.x}\n${ point.y}") }
                    }
                },
                state = rememberTooltipState(),
            ) {
                Symbol(
                    shape = CircleShape,
                    fillBrush = SolidColor(colors[key] ?: Color.Black),
                )
            }
        },
    )
}




@OptIn(ExperimentalMaterial3Api::class, ExperimentalKoalaPlotApi::class)
@Composable
fun XYGraphScope<String, Float>.VerticalBarChart(
    data: List<Float>,
    xValues: List<String>,
    usableTooltips: Boolean,
    colors: Map<String, Color>,
    barWidth: Float = 0.9f
){
    val values: List<VerticalBarPlotEntry<String, Float>> = buildList {
        data.forEachIndexed { index, fl ->
            add(
                DefaultVerticalBarPlotEntry(
                 //   (index + 1).toFloat(),
                   xValues[index],
                    DefaultBarPosition(0f, fl)
                )
            )
        }
    }

    VerticalBarPlot(
        values,
        bar = { index, _, _ ->
            DefaultBar(
                brush = SolidColor(colors[xValues[index]] ?: Color.Black),
                modifier = Modifier.fillMaxWidth(),
            ){ if (usableTooltips) { HoverSurface { Text("${values[index].x}\n${values[index].y.end }" ) } } }
        },
        barWidth = barWidth
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalKoalaPlotApi::class)
@Composable
fun XYGraphScope<Int, Float>.GroupVerticalBarChart(
    data: Map<String, List<Int>>,
    xValues: List<Int>,
    usableTooltips: Boolean,
    colors: Map<String, Color>,
){
    val values:List<BarPlotGroupedPointEntry<Int, Float>> =
        xValues.mapIndexed { xIndex, value ->
            object : BarPlotGroupedPointEntry<Int, Float> {
                override val i: Int = value

                override val d: List<BarPosition<Float>> =
                    object : AbstractList<BarPosition<Float>>() {
                        override val size: Int
                            get() =  data.keys.size

                        override fun get(index: Int): BarPosition<Float> = DefaultBarPosition(
                            0f,
                            data[data.keys.toList()[index]]!![xIndex].toFloat(),
                        )
                    }
            }
        }

    GroupedVerticalBarPlot(
        data = values,
        bar = { dataIndex, groupIndex, _ ->
            DefaultBar(
                brush = SolidColor(
                    colors[data.keys.toList()[groupIndex]] ?: Color.Black
                ),
                modifier = Modifier.sizeIn(
                    minWidth = 5.dp,
                    maxWidth = 20.dp
                ),
            ) {
                if (usableTooltips) {
                    val borough = data.keys.toList()[groupIndex]
                    val pop = data[borough]!![dataIndex]
                    HoverSurface { Text("$borough: $pop") }
                }
            }
        },
    )

}

