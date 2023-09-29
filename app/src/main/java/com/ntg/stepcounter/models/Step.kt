package com.ntg.stepcounter.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Step(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val timeUnix: String? = null,
    val inBackground: Boolean = false
)
