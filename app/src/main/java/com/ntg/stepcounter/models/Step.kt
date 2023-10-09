package com.ntg.stepcounter.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Step(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val count: Int? = null,
    val date: String,
    val inBackground: Boolean = false
)
