package com.ntg.stepi.models

import com.ntg.stepi.models.res.SummaryRes

data class ScreenState(
    val isLoading: Boolean = false,
    val items: List<SummaryRes>? = emptyList(),
    val error: String? = null,
    val endReached: Boolean = false,
    val page: Int = 0
)