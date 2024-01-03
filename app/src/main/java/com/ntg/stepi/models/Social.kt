package com.ntg.stepi.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Social(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String,
    val pageId: String
)
