package com.unchil.full_stack_task_manager_sample.chart


import com.unchil.full_stack_task_manager_sample.SeawaterInformationByObservationPoint
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

@OptIn(FormatStringsInDatetimeFormats::class)
fun List<*>.toLineData(gruName: String): Map<String, Any> {
    val inputFormat = LocalDateTime.Format { byUnicodePattern("yyyy-MM-dd HH:mm:ss") }
    val outputFormat = LocalDateTime.Format { byUnicodePattern("yy/MM/dd HH:mm") }

    // 1. 유효한 데이터만 필터링 및 DTO 변환 (반복 순회 최소화)
    val validData = this.filterIsInstance<SeawaterInformationByObservationPoint>()
        .filter { it.gru_nam == gruName && it.obs_lay == "1" }
        .map {
            val formattedTime = inputFormat.parse(it.obs_datetime).format(outputFormat)
            val temp = it.wtr_tmp.trim().toFloatOrNull() ?: 0f
            it.sta_nam_kor to (formattedTime to temp)
        }

    // 2. 전체 x축 시간축 생성 (중복 제거 및 정렬)
    val xValues = validData.map { it.second.first }.distinct().sorted()

    // 3. 관측소별로 그룹화 및 데이터 정렬
    // 차트에서 데이터 누락 방지를 위해 xValues 순서에 맞춰 List<Float>를 생성
    val groupedByStation = validData.groupBy({ it.first }, { it.second })

    val valuesMap = groupedByStation.mapValues { (_, timeValuePairs) ->
        // 시간별로 맵을 만들어 xValues 순서대로 값을 배치 (데이터가 없으면 0f)
        val timeMap = timeValuePairs.toMap()
        xValues.map { time -> timeMap[time] ?: 0f }
    }

    // 4. 관측소 이름 목록
    val entries = valuesMap.keys.sorted()

    return mapOf(
        "entries" to entries,
        "xValue" to xValues,
        "values" to valuesMap
    )
}
