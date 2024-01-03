package com.ntg.stepi.models.res

data class UserBaseRes(
    val users: List<SummaryRes>? = null,
    val ads: List<ADSRes>? = null
)
