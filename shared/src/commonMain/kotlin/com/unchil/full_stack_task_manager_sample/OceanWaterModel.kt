package com.unchil.full_stack_task_manager_sample


import kotlinx.serialization.Serializable



enum class DATA_DIVISION {
    oneday, grid, current, statistics, mof_oneday
}
enum class PlatformAlias {
    ANDROID, IOS, JVM, WASM, JS
}

object SEA_AREA {

    enum class GRU_NAME {
        WEST, EAST, SOUTH
    }

    fun GRU_NAME.gru_nam():String {
        return when(this) {
            GRU_NAME.WEST -> "서해"
            GRU_NAME.EAST -> "동해"
            GRU_NAME.SOUTH -> "남해"
        }
    }
}


@Serializable
data class SeaWaterInformation(
    val rtmWqWtchDtlDt:String,
    val rtmWqWtchStaCd:String,
    val rtmWqWtchStaName:String,
    val rtmWtchWtem:String,
    val rtmWqCndctv:String,
    val ph:String,
    val rtmWqDoxn:String,
    val rtmWqTu:String,
    val rtmWqChpla:String,
    val rtmWqSlnty:String,
    val lon: Double,
    val lat: Double,
)



@Serializable
data class SeawaterInformationByObservationPoint(
    val sta_cde: String,
    val sta_nam_kor: String,
    val obs_datetime: String,
    val obs_lay: String,
    val wtr_tmp: String,
    val gru_nam: String,
    val lon: Double,
    val lat: Double,
)

@Serializable
data class SeaWaterInfoByOneHourStat(
    val gru_nam: String,
    val sta_cde: String,
    val sta_nam_kor: String,
    val obs_datetime: String,
    val tmp_min: String,
    val tmp_max: String,
    val tmp_avg: String
)

@Serializable
data class Observation(
    val sta_cde: String,
    val sta_nam_kor: String,
    val obs_datetime: String,
    val repair_gbn: String,
    val obs_lay: String,
    val wtr_tmp: String
)

@Serializable
data class Observatory(
    val sta_cde: String,
    val sta_nam_kor: String,
    val bld_dat: String,
    val end_dat: String?,
    val gru_nam: String,
    val lon: Double,
    val lat: Double,
    val sur_tmp_yn: String,
    val mid_tmp_yn: String,
    val bot_tmp_yn: String,
    val sur_dep: String?,
    val mid_dep: String?,
    val bot_dep: String?,
    val sta_des: String?
)


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
    columns.add("Sea Area")
    columns.add("Observation Station Code")
    columns.add("Observation Station Name")
    columns.add("Observation Layer")
    columns.add("Water Temperature")
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
        "1" -> "Superficial"
        "2" -> "Middle"
        "3" -> "Deep"
        else -> ""
    })
    data.add(this.wtr_tmp)
    data.add(this.lon)
    data.add(this.lat)
    return data
}

