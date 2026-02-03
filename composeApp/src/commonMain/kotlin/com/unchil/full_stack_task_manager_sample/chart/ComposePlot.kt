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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.ShapeDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.unchil.full_stack_task_manager_sample.SeaWaterBoxPlotStat
import com.unchil.full_stack_task_manager_sample.chart.WATER_QUALITY.desc
import com.unchil.full_stack_task_manager_sample.padding
import com.unchil.full_stack_task_manager_sample.paddingMod
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.bar.BarPlotGroupedPointEntry
import io.github.koalaplot.core.bar.BarPosition
import io.github.koalaplot.core.bar.BarScope
import io.github.koalaplot.core.bar.DefaultBar
import io.github.koalaplot.core.bar.DefaultBarPosition
import io.github.koalaplot.core.bar.DefaultVerticalBarPlotEntry
import io.github.koalaplot.core.bar.GroupedVerticalBarPlot
import io.github.koalaplot.core.bar.VerticalBarPlot
import io.github.koalaplot.core.bar.VerticalBarPlotEntry
import io.github.koalaplot.core.legend.ColumnLegend
import io.github.koalaplot.core.legend.ColumnLegend2
import io.github.koalaplot.core.legend.FlowLegend2
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.LinePlot2
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.util.toString
import io.github.koalaplot.core.xygraph.AxisContent
import io.github.koalaplot.core.xygraph.AxisLabelScope
import io.github.koalaplot.core.xygraph.AxisStyle
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.DoubleLinearAxisModel
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

            Column {

                if(!layout.layout.description.isNullOrBlank()){
                    Text(
                        text = layout.layout.description,
                        modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                        textAlign = TextAlign.Start
                    )
                    HorizontalDivider(modifier = Modifier.padding(10.dp))
                }

                XYGraph(
                    xAxisModel = when (layout.type) {
                        ChartType.Line -> layout.xAxis.model as DoubleLinearAxisModel
                        ChartType.VerticalBar, ChartType.BoxPlot -> layout.xAxis.model as CategoryAxisModel<Any>
                        ChartType.GroupVerticalBar -> layout.xAxis.model as CategoryAxisModel<Any>
                        ChartType.XYGraph -> layout.xAxis.model as CategoryAxisModel<Any>
                    },
                    yAxisModel = when (layout.type) {
                        ChartType.XYGraph -> layout.yAxis.model as FloatLinearAxisModel
                        ChartType.VerticalBar -> layout.yAxis.model as FloatLinearAxisModel
                        ChartType.BoxPlot -> layout.yAxis.model as FloatLinearAxisModel
                        ChartType.GroupVerticalBar -> layout.yAxis.model as FloatLinearAxisModel
                        ChartType.Line -> layout.yAxis.model as FloatLinearAxisModel
                    },
                    xAxisContent = AxisContent(
                        labels = {
                            if (layout.xAxis.isLabels) {
                                when (layout.type) {
                                    ChartType.XYGraph, ChartType.VerticalBar, ChartType.GroupVerticalBar, ChartType.BoxPlot -> {
                                        AxisLabel(it.toString(), Modifier.padding(top = 2.dp))
                                    }
                                    ChartType.Line -> {
                                        AxisLabel(formatLongToDateTime(it), Modifier.padding(top = 2.dp))
                                    }
                                }


                            }
                        },
                        title = {
                            if (layout.xAxis.isTitle) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AxisTitle(layout.xAxis.title, paddingMod)
                                }
                            }
                        },
                        style = AxisStyle(
                            color = layout.xAxis.style?.color ?: KoalaPlotTheme.axis.color,
                            majorTickSize = layout.xAxis.style?.majorTickSize
                                ?: KoalaPlotTheme.axis.majorTickSize,
                            minorTickSize = layout.xAxis.style?.minorTickSize
                                ?: KoalaPlotTheme.axis.minorTickSize,
                            tickPosition = layout.xAxis.style?.tickPosition
                                ?: KoalaPlotTheme.axis.xyGraphTickPosition,
                            lineWidth = layout.xAxis.style?.lineWidth
                                ?: KoalaPlotTheme.axis.lineThickness,
                            labelRotation = layout.xAxis.style?.labelRotation ?: 0,
                        ),
                    ),
                    yAxisContent = AxisContent(
                        labels = {
                            if (layout.yAxis.isLabels) {
                                AxisLabel(it.toString(), Modifier.absolutePadding(right = 2.dp))
                            }
                        },
                        title = {
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
                        style = AxisStyle(
                            color = layout.yAxis.style?.color ?: KoalaPlotTheme.axis.color,
                            majorTickSize = layout.yAxis.style?.majorTickSize
                                ?: KoalaPlotTheme.axis.majorTickSize,
                            minorTickSize = layout.yAxis.style?.minorTickSize
                                ?: KoalaPlotTheme.axis.minorTickSize,
                            tickPosition = layout.yAxis.style?.tickPosition
                                ?: KoalaPlotTheme.axis.xyGraphTickPosition,
                            lineWidth = layout.yAxis.style?.lineWidth
                                ?: KoalaPlotTheme.axis.lineThickness,
                            labelRotation = layout.yAxis.style?.labelRotation ?: 0,
                        )
                    ),
                ) {
                    when (layout.type) {
                        ChartType.Line -> {
                            val scope = this as XYGraphScope<Double, Float>
                            scope.LineChart(data, xValues, layout.tooltips.isTooltips, colors)
                        }

                        ChartType.VerticalBar -> {
                            val scope = this as XYGraphScope<String, Float>
                            scope.VerticalBarChart(data, xValues,layout.tooltips.isTooltips, colors,layout.barConf.widthWeight  )
                        }

                        ChartType.BoxPlot -> {
                            val scope = this as XYGraphScope<String, Float>
                            scope.BoxPlot(data, xValues,layout.tooltips.isTooltips, colors )
                        }


                        ChartType.GroupVerticalBar -> {
                            val scope = this as XYGraphScope<Int, Float>
                            scope.GroupVerticalBarChart(data, xValues,layout.tooltips.isTooltips, colors  )
                        }



                        ChartType.XYGraph -> TODO()
                    }

                }


            }




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
            if( (layout.size.height * 0.4f) < ( entries.size.toFloat() * defaultSize )){
                paddingMod.height(layout.size.height * 0.4f)
                    .verticalScroll(rememberScrollState())
                //    .horizontalScroll(rememberScrollState())
            } else{
                paddingMod
                    .verticalScroll(rememberScrollState())
                  //  .horizontalScroll(rememberScrollState())
            }
        }
        LegendLocation.TOP ,LegendLocation.BOTTOM ,LegendLocation.NONE -> {
            paddingMod
              //  .verticalScroll(rememberScrollState())
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

                        ColumnLegend2(
                            itemCount  = entries.size,
                            modifier = paddingMod,
                            symbol = { i ->
                                Symbol(
                                    modifier = Modifier.size(padding),
                                    fillBrush = SolidColor(colors[entries[i]] ?: Color.Black),
                                )
                            },
                            label = { i ->
                                Text(entries[i])
                            },
                            value = {  },
                        )
                    }

                    LegendLocation.TOP, LegendLocation.BOTTOM -> {
                        FlowLegend2(
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
fun XYGraphScope<String, Float>.BoxPlot(
    data:Any,
    xValues:Any,
    usableTooltips: Boolean,
    colors: Map<String, Color>,
) {
    BoxPlotChart(data, xValues,usableTooltips, colors , BoxPlotRange.Q1_Q3)
    BoxPlotChart(data, xValues,usableTooltips, colors, BoxPlotRange.MIN_MAX )
    BoxPlotChart(data, xValues,usableTooltips, colors, BoxPlotRange.MIN )
    BoxPlotChart(data, xValues,usableTooltips, colors, BoxPlotRange.MAX)
    BoxPlotChart(data, xValues,usableTooltips, colors, BoxPlotRange.Q2)
    BoxPlotOutliers(data, xValues, usableTooltips, colors)

}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalKoalaPlotApi::class)
@Composable
fun XYGraphScope<String, Float>.BoxPlotChart(
    data:Any,
    xValues:Any,
    usableTooltips: Boolean,
    colors: Map<String, Color>,
    range:BoxPlotRange
) {
    val data = (data as List<SeaWaterBoxPlotStat>)
    val xValues = (xValues as List<String>)

    val barWidth = when(range){
        BoxPlotRange.Q1_Q3 -> 0.5f
        BoxPlotRange.MIN_MAX  -> 0.05f
        BoxPlotRange.MIN, BoxPlotRange.MAX -> 0.2f
        BoxPlotRange.Q2 -> 0.4f
    }

    val values : List<VerticalBarPlotEntry<String, Float>> = when(range){
            BoxPlotRange.Q1_Q3 -> {
                buildList {
                    data.forEachIndexed { index, seaWaterBoxPlotStat ->
                        add(
                            DefaultVerticalBarPlotEntry(

                                xValues[index],
                                DefaultBarPosition(seaWaterBoxPlotStat.q1, seaWaterBoxPlotStat.q3)
                            )
                        )
                    }
                }
            }
            BoxPlotRange.MIN_MAX -> {
                buildList {
                    data.forEachIndexed { index, seaWaterBoxPlotStat ->
                        add(
                            DefaultVerticalBarPlotEntry(

                                xValues[index],
                                DefaultBarPosition(seaWaterBoxPlotStat.min, seaWaterBoxPlotStat.max )
                            )
                        )
                    }
                }
            }
         BoxPlotRange.MIN -> {
             buildList {
                 data.forEachIndexed { index, seaWaterBoxPlotStat ->
                     add(
                         DefaultVerticalBarPlotEntry(

                             xValues[index],
                             DefaultBarPosition(seaWaterBoxPlotStat.min, seaWaterBoxPlotStat.min + 0.05f)
                         )
                     )
                 }
             }
         }
         BoxPlotRange.MAX -> {
             buildList {
                 data.forEachIndexed { index, seaWaterBoxPlotStat ->
                     add(
                         DefaultVerticalBarPlotEntry(

                             xValues[index],
                             DefaultBarPosition(seaWaterBoxPlotStat.max - 0.05f, seaWaterBoxPlotStat.max)
                         )
                     )
                 }
             }
         }
         BoxPlotRange.Q2 -> {
             buildList {
                 data.forEachIndexed { index, seaWaterBoxPlotStat ->
                     add(
                         DefaultVerticalBarPlotEntry(

                             xValues[index],
                             DefaultBarPosition(seaWaterBoxPlotStat.median - 0.05f, seaWaterBoxPlotStat.median + 0.05f)
                         )
                     )
                 }
             }
         }
     }

    VerticalBarPlot(
        values,
        bar = { index, _, _ ->

            val text =
                "${values[index].x}\nmax:${data[index].max}\nq3:${data[index].q3}\nq2:${data[index].median}\nq1:${data[index].q1}\nmin:${data[index].min}"

            val color = when(range){
                BoxPlotRange.Q1_Q3 -> colors[xValues[index]] ?: Color.Black
                BoxPlotRange.MIN_MAX -> colors[xValues[index]] ?: Color.Black
                BoxPlotRange.MIN, BoxPlotRange.MAX -> Color.Black
                BoxPlotRange.Q2 -> Color.Yellow
            }

            DefaultBar(
                brush = SolidColor(color ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (usableTooltips) {
                    Box(
                        modifier = paddingMod.shadow(2.dp).background(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            shape = MaterialTheme.shapes.medium
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text, modifier = Modifier.padding(horizontal = 6.dp))
                    }
                }
            }

        },
        barWidth = barWidth
    )

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalKoalaPlotApi::class)
@Composable
fun XYGraphScope<String, Float>.BoxPlotOutliers(
    data:Any,
    xValues:Any,
    usableTooltips: Boolean,
    colors: Map<String, Color>
){
    val data = (data as List<SeaWaterBoxPlotStat>).map {
        it.outliers
    }
    val xValues = (xValues as List<String>)

    data.forEachIndexed { index, floats ->

        LinePlot2(
            data =  floats.map {
                DefaultPoint(
                    xValues[index],
                    it
                )
            },
            lineStyle = LineStyle(
                brush = SolidColor(colors[xValues[index]] ?: Color.Black),
                strokeWidth = 0.dp),
            symbol = { point ->
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                        TooltipAnchorPosition.Above),
                    tooltip = {
                        if (usableTooltips) {
                            PlainTooltip { Text("${xValues[index]}\n${point.y}") }
                        }
                    },
                    state = rememberTooltipState(),
                ) {
                    Symbol(
                        shape = ShapeDefaults.ExtraSmall,
                        fillBrush = SolidColor(colors[xValues[index]] ?: Color.Black),
                        size = 4.dp,
                    )
                }
            },
        )

    }


}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalKoalaPlotApi::class)
@Composable
fun XYGraphScope<Double, Float>.LineChart(
    data:Any,
    xValues:Any,
    usableTooltips: Boolean,
    colors: Map<String, Color>
) {

    val data = (data as Map<String, List<Float>>)
    val xValues = (xValues as List<Double>)
    data.entries.sortedBy { it.key }
        .forEach { (key, values) ->

        LinePlot2(
            data = values.mapIndexed { index, value ->
                DefaultPoint(
                    xValues[index],
                    value.toFloat()
                )
            },
            lineStyle = LineStyle(
                brush = SolidColor(colors[key] ?: Color.Black),
                strokeWidth = 1.dp),
            symbol = { point ->
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                        TooltipAnchorPosition.Above),
                    tooltip = {
                        if (usableTooltips) {
                            PlainTooltip { Text("${key}\n${formatLongToDateTime(point.x)}\n${ kotlin.math.round(point.y * 10) / 10.0}") }
                        }
                    },
                    state = rememberTooltipState(),
                ) {
                    Symbol(
                        shape = ShapeDefaults.ExtraSmall,
                        fillBrush = SolidColor(colors[key] ?: Color.Black),
                        size = 6.dp,
                    )
                }
            },
        )
    }
}




@OptIn(ExperimentalMaterial3Api::class, ExperimentalKoalaPlotApi::class)
@Composable
fun XYGraphScope<String, Float>.VerticalBarChart(
    data: Any,
    xValues: Any,
    usableTooltips: Boolean,
    colors: Map<String, Color>,
    barWidth: Float = 0.9f
){
    val data = (data as List<Float>)
    val xValues = (xValues as  List<String>)

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
    data:Any,
    xValues: Any,
    usableTooltips: Boolean,
    colors: Map<String, Color>,
){
    val data = (data as Map<String, List<Int>>)
    val xValues =  (xValues as List<Int>)
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

