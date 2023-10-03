package com.ntg.stepcounter.models

data class SocialNetwork(
    val name: String,
    val link: String,
    var isSelected: Boolean = false
)
