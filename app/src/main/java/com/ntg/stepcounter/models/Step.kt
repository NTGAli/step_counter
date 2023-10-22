package com.ntg.stepcounter.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Step(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: String,
    val start: Int? = null,
    val count: Int = 0,
    val synced: Int? = 0,
    val exp: Boolean = false
)
