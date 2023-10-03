package com.ntg.stepcounter.models.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.ntg.stepcounter.ui.theme.SECONDARY500

data class AppbarItem (
    val id: Int,
    val imageVector: ImageVector,
    val iconColor: Color = SECONDARY500,
        )