package com.unchil.full_stack_task_manager_sample.chart


import com.unchil.full_stack_task_manager_sample.SeaWaterBoxPlotStat
import com.unchil.full_stack_task_manager_sample.SeaWaterInformation
import com.unchil.full_stack_task_manager_sample.SeawaterInformationByObservationPoint
import com.unchil.un7datagrid.toMap
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.toMap
import kotlin.time.ExperimentalTime


@OptIn(FormatStringsInDatetimeFormats::class)
fun formatLongToDateTime(millis: Any): String {

    // 1. Long을 Instant로 변환
    val instant = Instant.fromEpochMilliseconds(   (millis as Double).toLong())

    // 2. 시스템 기본 시간대(TimeZone)를 적용하여 LocalDateTime으로 변환
    val localDateTime = instant.toLocalDateTime(TimeZone.UTC)

    // 3. 사용자 정의 포맷 정의
    val myFormat = LocalDateTime.Format {
        byUnicodePattern("yy/MM/dd HH:mm")
    }

    // 4. 포맷팅 실행
    return myFormat.format(localDateTime)
}


@OptIn(FormatStringsInDatetimeFormats::class)
fun List<*>.toMofLineMap(qualityType: WATER_QUALITY.QualityType):Map<String, Any> {
    val inputFormat = LocalDateTime.Format { byUnicodePattern("yyyy-MM-dd HH:mm:ss") }
    val outputFormat = LocalDateTime.Format { byUnicodePattern("yy/MM/dd HH:mm") }

    // 1. 기본 필터링 및 데이터 추출 (시간순 정렬 포함)
    val rawData = this.filterIsInstance<SeaWaterInformation>()
       // .filter { !listOf("SEA6001", "SEA1005").contains(it.rtmWqWtchStaCd) }
        .sortedBy { it.rtmWqWtchDtlDt } // 이전 값을 참조하기 위해 시간순 정렬 필수


    // 2. 관측소별로 그룹화하여 결측치 보정 (Forward Fill)
    val validData = rawData.groupBy { it.rtmWqWtchStaName }
        .flatMap { (staName, items) ->

            var lastValidValue = 0f // 이전 인덱스의 유효한 값을 저장

            items.map { it ->
     //           val formattedTime = inputFormat.parse(it.rtmWqWtchDtlDt)
                val formattedTime = LocalDateTime.parse(it.rtmWqWtchDtlDt, inputFormat)
                    .toInstant(TimeZone.UTC)
                    .toEpochMilliseconds().toDouble()


                // 현재 값 추출
                val currentValue = when (qualityType) {
                    WATER_QUALITY.QualityType.rtmWtchWtem -> it.rtmWtchWtem
                    WATER_QUALITY.QualityType.rtmWqCndctv -> it.rtmWqCndctv
                    WATER_QUALITY.QualityType.ph -> it.ph
                    WATER_QUALITY.QualityType.rtmWqDoxn -> it.rtmWqDoxn
                    WATER_QUALITY.QualityType.rtmWqTu -> it.rtmWqTu
                    WATER_QUALITY.QualityType.rtmWqChpla -> it.rtmWqChpla
                    WATER_QUALITY.QualityType.rtmWqSlnty -> it.rtmWqSlnty
                }.trim().toFloatOrNull()

                val finalValue:Float = if ( currentValue == null ) {
                    lastValidValue
                } else {
                    lastValidValue = currentValue
                    currentValue
                }

                staName to (formattedTime to finalValue)
            }

        }

    val xValues = validData.map { it.second.first }.distinct().sorted()
    val groupedByStation = validData
        .groupBy({ it.first }, { it.second })
        .mapValues { (_, timeValuePairs) ->
            // 시간별로 맵을 만들어 xValues 순서대로 값을 배치 (데이터가 없으면 0f)
            val timeMap = timeValuePairs.toMap()
            xValues.map {  time ->

                // 1. 현재 시간에 데이터가 있으면 사용
                // 2. 없으면 timeValuePairs(리스트)에서 현재 time보다 이전인 것 중 가장 늦은 시간의 값을 가져옴
                timeMap[time] ?: timeValuePairs
                    .filter { it.first < time }
                    .maxByOrNull { it.first }?.second
                ?: 0f // 이전 데이터도 전혀 없으면 0f



            }
        }

    val entries = groupedByStation.keys.sorted()

    return mapOf(
        "entries" to entries,
        "xValue" to xValues,
        "values" to groupedByStation
    )

}


@OptIn(FormatStringsInDatetimeFormats::class)
fun List<*>.toLineMap(): Map<String, Any> {
    val inputFormat = LocalDateTime.Format { byUnicodePattern("yyyy-MM-dd HH:mm:ss") }
    val outputFormat = LocalDateTime.Format { byUnicodePattern("yy/MM/dd HH:mm") }

    // 1. 기본 필터링 및 데이터 추출 (시간순 정렬 포함)
    val rawData = this.filterIsInstance<SeawaterInformationByObservationPoint>()
        .sortedBy { it.obs_datetime } // 이전 값을 참조하기 위해 시간순 정렬 필수

    // 2. 관측소별로 그룹화하여 결측치 보정 (Forward Fill)
    val validData = rawData.groupBy { it.sta_nam_kor }
        .flatMap { (staName, items) ->
            var lastValidValue = 0f // 이전 인덱스의 유효한 값을 저장
            items.map { it ->

                val formattedTime = LocalDateTime.parse(it.obs_datetime, inputFormat)
                    .toInstant(TimeZone.UTC)
                    .toEpochMilliseconds().toDouble()

                val currentValue = it.wtr_tmp.trim().toFloatOrNull()

                val finalValue:Float = if ( currentValue == null ) {
                    lastValidValue
                } else {
                    lastValidValue = currentValue
                    currentValue
                }

                staName to (formattedTime to finalValue)

            }
        }

    val xValues = validData.map { it.second.first }.distinct().sorted()

    val groupedByStation = validData
        .groupBy({ it.first }, { it.second })
        .mapValues { (_, timeValuePairs) ->
            // 시간별로 맵을 만들어 xValues 순서대로 값을 배치 (데이터가 없으면 0f)
            val timeMap = timeValuePairs.toMap()
            xValues.map {  time ->

                // 1. 현재 시간에 데이터가 있으면 사용
                // 2. 없으면 timeValuePairs(리스트)에서 현재 time보다 이전인 것 중 가장 늦은 시간의 값을 가져옴
                timeMap[time] ?: timeValuePairs
                    .filter { it.first < time }
                    .maxByOrNull { it.first }?.second
                ?: 0f // 이전 데이터도 전혀 없으면 0f

            }
        }

    val entries = groupedByStation.keys.sorted()

    return mapOf(
        "entries" to entries,
        "xValue" to xValues,
        "values" to groupedByStation
    )
}

// TypeTransform.kt 또는 적절한 위치에 추가
fun List<SeawaterInformationByObservationPoint>.toGridDataMap(): Map<String, List<Any?>> {
    if (this.isEmpty()) return mutableMapOf()

    // 첫 번째 아이템에서 컬럼 이름을 추출
    val columns = this.first().makeGridColumns()
    // 모든 데이터를 리스트의 리스트 형태로 변환
    val rows = this.map { it.toGridData() }

    // Pair(컬럼 리스트, 로우 리스트).toMap() 호출
    return (columns to rows).toMap()
}



fun List<SeawaterInformationByObservationPoint>.toBarChartMap(): Map<String, List<Any?>> {


    val gridData = this.toGridDataMap()
    val entries = gridData["Observatory"] ?: emptyList()
    val values = gridData["WaterTemperature"]?.map {  it.toString().trim().toFloatOrNull() ?: 0f } ?: emptyList()

    return mapOf("entries" to entries, "xValue" to entries, "values" to values)
}




fun List<SeawaterInformationByObservationPoint>.toBoxPlotMap():Map<String, List<Any>> {
    val result =  this.map {
        Pair(
            it.sta_nam_kor,
            it.wtr_tmp.trim().toFloatOrNull() ?: 0f
        )
    }.groupBy { it.first  } // Pair(gru_nam, sta_nam_kor) 기준
        .mapValues { (key, values) ->
            val temps = values.map { it.second }.sorted() // 오름차순 정렬
            val n = temps.size
            if (n == 0) return mapOf()
            // 사분위수 계산 (단순 인덱스 방식)
            val q1 = temps[n / 4] // 25% 지점인 1사분위수(Q1)
            val median = temps[n / 2] // 50% 지점인 중앙값(Q2)
            val q3 = temps[n * 3 / 4] // 75% 지점인 3사분위수(Q3)

            // 이상치(Outlier) 계산 로직
            val iqr = q3 - q1
            val lowerFence = q1 - (1.5f * iqr)
            val upperFence = q3 + (1.5f * iqr)

            // Fence 내부에 있는 값들 중 실제 최소/최대값 결정 (Whiskers 끝점)
            val actualMin = temps.firstOrNull { it >= lowerFence } ?: temps.first()
            val actualMax = temps.lastOrNull { it <= upperFence } ?: temps.last()

            // Fence를 벗어나는 값들을 이상치로 추출
            val outliers = temps.filter { it !in lowerFence..upperFence }

            SeaWaterBoxPlotStat(
                gruNam = "",
                staName = key,
                min = actualMin,
                q1 = q1,
                median = median,
                q3 = q3,
                max = actualMax,
                outliers = outliers
            )
        }

        val entries = result.keys.toList()
        val values = result.values.toList()

        return mapOf("entries" to entries, "xValue" to entries, "values" to values)

}





fun SeaWaterInformation.makeGridColumns():List<String>{
    val columns = mutableListOf<String>()

    columns.add("Collection Time")
    columns.add("Observation Point")
    columns.add("Water Temperature")
    columns.add("Hydrogen Ion Concentration")
    columns.add("Dissolved Oxygen")
    columns.add("Turbidity")
    columns.add("Chlorophyll")
    columns.add("Salinity")

    return columns
}

fun SeaWaterInformation.toGridData():List<Any?>{
    val data = mutableListOf<Any?>()

    data.add(this.rtmWqWtchDtlDt)
    data.add(this.rtmWqWtchStaName)
    data.add(this.rtmWtchWtem.toFloat())
    data.add(this.ph.toFloat())
    data.add(this.rtmWqDoxn.toFloat())
    data.add(  if(this.rtmWqTu.isBlank() ) -1 else  this.rtmWqTu.toInt() )
    data.add(  if(this.rtmWqChpla.isBlank() ) -999f else  this.rtmWqChpla.toFloat() )
    data.add(this.rtmWqSlnty.toFloat())

    return data
}

fun SeawaterInformationByObservationPoint.makeGridColumns():List<String>{
    val columns = mutableListOf<String>()
    columns.add("Collection Time")
    columns.add("SeaArea")
    columns.add("ObservatoryCode")
    columns.add("Observatory")
    columns.add("ObservationLayer")
    columns.add("WaterTemperature")
    columns.add("Longitude")
    columns.add("Latitude")
    return columns
}

fun SeawaterInformationByObservationPoint.toGridData():List<Any?>{

    val data = mutableListOf<Any?>()
    data.add(this.obs_datetime)
    data.add(this.gru_nam)
    data.add(this.sta_cde)
    data.add(this.sta_nam_kor)
    data.add(when(this.obs_lay){
        "1" -> "Surface"
        "2" -> "Middle"
        "3" -> "Deep"
        else -> ""
    })
    data.add( if(this.wtr_tmp.isBlank() ) 0f else  this.wtr_tmp.toFloat())
    data.add(this.lon)
    data.add(this.lat)
    return data
}

