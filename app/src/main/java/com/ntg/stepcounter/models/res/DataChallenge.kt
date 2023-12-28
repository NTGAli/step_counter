package com.ntg.stepcounter.models.res

data class DataChallenge(
    val rank: String? =null,
    val leagueName: String? = null,
    val numberOfUsers: Int? =null,
    val timeLeft: String? =null,
    val users: List<SummaryRes>? =null,
)
