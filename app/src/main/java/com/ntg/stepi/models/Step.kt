package com.ntg.stepi.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Step(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: String,
    val start: Int? = null,
    var count: Int = 0,
    var synced: Int? = 0,
    val exp: Boolean = false
)
