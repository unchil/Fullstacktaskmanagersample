@file:Suppress("MagicNumber")

package com.unchil.full_stack_task_manager_sample.chart


import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.koalaplot.core.xygraph.AxisStyle
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import io.github.koalaplot.core.xygraph.TickPosition
import kotlin.math.ceil


// 1. 차트 제목 및 레이아웃 설정
data class TitleConfig(
    val isTitle: Boolean = false,
    val title: String = ""
)

// 2. 범례 설정
data class LegendConfig(
    val isUsable: Boolean = false,
    val isTitle: Boolean = false,
    val title: String = "",
    val location: LegendLocation = LegendLocation.RIGHT
)

// 3. 축(Axis) 설정
data class AxisConfig(
    val title: String = "",
    val isTitle: Boolean = true,
    val isLabels: Boolean = true,
    val style: AxisStyle? = null, // 아래에서 기본값 할당
    val range: ClosedFloatingPointRange<Float> = 0.0f..0.0f,
    val model: Any? = null
)

// 4. 캡션 설정
data class CaptionConfig(
    val isCaption: Boolean = false,
    val title: String = "",
    val location: Alignment = Alignment.BottomEnd
)

// 5. 툴팁 및 크기 설정
data class TooltipConfig(val isTooltips: Boolean = true)

data class SizeConfig(
    val height: Dp = 400.dp,
    val minHeight: Dp = 200.dp,
    val maxHeight: Dp = height
)

data class BarConfig(val widthWeight: Float = 0.8f)


data class LayoutData(

    val type: ChartType = ChartType.XYGraph,
    val category: List<String> = emptyList(),
    val layout: TitleConfig = TitleConfig(),
    val legend: LegendConfig = LegendConfig(),
    val xAxis: AxisConfig = AxisConfig(
        style = AxisStyle(
            color = Color.LightGray,
            majorTickSize = 0.dp,
            minorTickSize = 0.dp,
            tickPosition = TickPosition.None,
            lineWidth = 1.dp,
            labelRotation = 0
        )
    ),
    val yAxis: AxisConfig = AxisConfig(
        style = AxisStyle(
            color = Color.LightGray,
            majorTickSize = 0.dp,
            minorTickSize = 0.dp,
            tickPosition = TickPosition.None,
            lineWidth = 1.dp,
            labelRotation = 0
        )
    ),
    val caption: CaptionConfig = CaptionConfig(),
    val tooltips: TooltipConfig = TooltipConfig(),
    val size: SizeConfig = SizeConfig(),
    val barConf: BarConfig = BarConfig()
) {

}

enum class ChartType {
    XYGraph, Line, VerticalBar, GroupVerticalBar
}

internal val padding = 8.dp
internal val paddingMod = Modifier.padding(padding)


sealed class ChartData {

    data class LineChartData(
        val rawData:  Map<String, List<Any>>,
        val legendTitle:String,
    ) {
        val entries = rawData[legendTitle]?.map { it.toString() } ?: emptyList()
        val xValues = rawData.keys.filter { it != legendTitle }
        val values = entries.mapIndexed { index, id ->
            id to xValues.map { columnName ->
                rawData[columnName]?.getOrNull(index) as? Double ?: 0.0
            }
        }.toMap()

        val max = values.maxOf { it.value.maxOf { it } }
        val min = values.minOf { it.value.minOf { it } }

        val range =  0f..(ceil(max / 50.0) * 50.0).toFloat()

        val chartLayout = LayoutData(
            type = ChartType.Line,
            category = xValues,
            layout = TitleConfig(true, "Rain Fall"),
            legend = LegendConfig(true, true, legendTitle),
            xAxis = AxisConfig("Month", model = CategoryAxisModel(xValues)),
            yAxis = AxisConfig(
                "Rainfall (mm)",
                range = range,
                model = FloatLinearAxisModel(range)
            ),
            caption = CaptionConfig(true, "from www.worldclimate.com"),
        )
    }


    data class BarChartData(
        val rawData: List<Float>,
        val legendTitle: String,
    ) {
        val entries = rawData.indices.map { "$legendTitle-${it+1}" }
        val xValues = entries

        val yMax = rawData.maxBy { it }
        val range = 0f..(yMax + (yMax * 0.1f) )

        val chartLayout = LayoutData(
            type = ChartType.VerticalBar,
            category = xValues,
            layout = TitleConfig(true, "Fibonacci Sequence"),
            legend = LegendConfig(true, true, legendTitle),
            xAxis = AxisConfig(
                "Position in Sequence",
                range = 0.5f..(rawData.size.toFloat() + 0.5f),
                model = FloatLinearAxisModel( 0.5f..(rawData.size.toFloat() + 0.5f))
            ),
            yAxis = AxisConfig(
                "Value",
                range = range,
                model = FloatLinearAxisModel(range)
            ),
            caption = CaptionConfig(true, "from The Koala Plot"),
        )
    }

    data class GroupBarChartData(
        val rawData: Map<String, List<Int>>,
        val legendTitle: String,
    ):ChartData(){
        val entries = rawData.keys.toList()
        val xValues = listOf(1950, 1960, 1970, 1980, 1990, 2000, 2010, 2020)

        val yMax by lazy {
            rawData.maxOf { entry ->
                entry.value.maxOf { it }
            }
        }
        val yMin by lazy {
            rawData.minOf { entry ->
                entry.value.minOf { it }
            }
        }

        val yRange = 0f..(yMax + (yMax * 0.1f) )

        val chartLayout = LayoutData(
            type = ChartType.GroupVerticalBar,
            category = entries,
            layout = TitleConfig(true, "Population (Millions)"),
            legend = LegendConfig(true, true, legendTitle),
            xAxis = AxisConfig(
                "Year",
                model = CategoryAxisModel( xValues)
            ),
            yAxis = AxisConfig(
                "Population (Millions)",
                range = yRange,
                model = FloatLinearAxisModel(yRange)
            ),
            caption = CaptionConfig(true, "from https://data.cityofnewyork.us/City-Government/New-York-City-Population-by-Borough-1950-2040/xywu-7bv9"),
        )


    }

}



val getColors = { entries:List<String> ->
    buildMap {
        val colors = generateHueColorPalette(entries.size)
        entries.sortedBy { it }.forEachIndexed { index, it ->
            put(it, colors[index])
        }
    }
}


