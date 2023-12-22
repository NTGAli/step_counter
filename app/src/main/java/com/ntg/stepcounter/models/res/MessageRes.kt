package com.ntg.stepcounter.models.res

data class MessageRes(
    val id: String,
    val title: String,
    val description: String,
    val buttonText: String?,
    val link: String?,
    val date: String,
)
