package com.ntg.stepcounter.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ntg.stepcounter.R

internal enum class UISystem(@StringRes val labelResourceID: Int, @DrawableRes val iconResourceID: Int) {
    Compose(R.string.app_name, R.drawable.icons8_trainers_1),
    Views(R.string.app_name_farsi, R.drawable.trash_16_1_5),
}