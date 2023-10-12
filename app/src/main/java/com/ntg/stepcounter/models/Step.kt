package com.ntg.stepcounter.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Step(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: String,
    val count: Int? = null,
    val synced: Int? = 0
)
