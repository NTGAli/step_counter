package com.ntg.stepi.models.res

data class UpdateRes(
    val versionName: String? = null,
    val size: Int? = null,
    val date: String? = null,
    val links: List<UpdateLink>? = null,
)
