package com.ntg.stepcounter.models.res

data class SummariesRes(
    val rank: String? = null,
    val claps: Int? = null,
    val today: List<SummaryRes>? = null,
    val fos: List<SummaryRes>? = null,
    val all: List<SummaryRes>? = null,
    val messagesId: List<String>? = null,
    val versionCode: Int? = null,
    val deadVersionCode: Int,
)
