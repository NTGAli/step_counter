package com.ntg.stepi.models.res

data class Achievement(
    val n3Days : Int? = null,
    val n7Days : Int?= null,
    val n30Days : Int?= null,
    val totalTop: Int? = null,
    val is10: Boolean? = false,
    val is20: Boolean? = false,
    val is30: Boolean? = false,
    val is40: Boolean? = false,
    val is50: Boolean? = false,
)
