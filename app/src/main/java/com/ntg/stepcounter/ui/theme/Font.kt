package com.ntg.stepcounter.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ntg.stepcounter.R

val mFont = FontFamily(
    Font(R.font.yekan_regular),
    Font(R.font.yekan_regular, FontWeight.Normal),
    Font(R.font.yekan_medium, FontWeight.Medium),
    Font(R.font.yekan_thin, FontWeight.Thin),
    Font(R.font.yekan_bold, FontWeight.Bold),
    Font(R.font.yekan_regular, FontWeight.ExtraLight),
    Font(R.font.yekan_black, FontWeight.Black),
    Font(R.font.yekan_extra_bold, FontWeight.ExtraBold),
    Font(R.font.yekan_extra_black, FontWeight.SemiBold),
)


// Thin
fun FontThin10(color: Color = SECONDARY100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Thin,
    fontSize = 10.sp,
    color = color
)

fun fontThin12(color: Color = SECONDARY100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Thin,
    fontSize = 12.sp,
    color = color
)



fun fontThin14(color: Color = SECONDARY100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Thin,
    fontSize = 14.sp,
    color = color
)

// Regular
fun FontRegular10(color: Color = SECONDARY100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Normal,
    fontSize = 10.sp,
    color = color
)

fun fontRegular12(color: Color = SECONDARY100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    color = color
)



fun fontRegular14(color: Color = SECONDARY100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    color = color
)

// Medium

fun fontMedium10(color: Color = SECONDARY100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Medium,
    fontSize = 10.sp,
    color = color
)

fun fontMedium12(color: Color = SECONDARY100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    color = color
)


fun fontMedium14(color: Color = SECONDARY100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    color = color
)

fun fontMedium16(color: Color = SECONDARY100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    color = color
)

fun fontMedium24(color: Color = SECONDARY100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Medium,
    fontSize = 24.sp,
    color = color
)

fun fontMedium36(color: Color = SECONDARY100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Medium,
    fontSize = 36.sp,
    color = color
)

// Bold

fun FontBold10(color: Color = SECONDARY100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Bold,
    fontSize = 10.sp,
    color = color
)

fun fontBold12(color: Color = SECONDARY100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Bold,
    fontSize = 12.sp,
    color = color
)


fun fontBold14(color: Color = SECONDARY100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
    color = color
)

fun fontBold24(color: Color = SECONDARY100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    color = color
)

fun fontBold36(color: Color = SECONDARY100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Bold,
    fontSize = 36.sp,
    color = color
)

//Black
fun fontBlack14(color: Color = SECONDARY100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Black,
    fontSize = 14.sp,
    color = color
)


fun fontBlack24(color: Color = SECONDARY100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Black,
    fontSize = 24.sp,
    color = color
)

