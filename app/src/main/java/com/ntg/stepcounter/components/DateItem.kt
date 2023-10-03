package com.ntg.stepcounter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ntg.stepcounter.ui.theme.PRIMARY100
import com.ntg.stepcounter.ui.theme.PRIMARY500
import com.ntg.stepcounter.ui.theme.SECONDARY100
import com.ntg.stepcounter.ui.theme.SECONDARY500
import com.ntg.stepcounter.ui.theme.fontMedium14
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import java.text.SimpleDateFormat


@Composable
fun DateItem(
    modifier: Modifier = Modifier,
    date: String,
    isSelected: Boolean,
    onClick:(String) -> Unit
) {

//    val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        DateTimeFormatter.ofPattern("yyyy-MM-dd")
//    } else {
//        SimpleDateFormat("yyyy-MM-dd")
//    }
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    val userDate = try {
        formatter.parse(date)
    }catch (e: Exception){
        return
    }
    val pDate = PersianDate(userDate)
    val pDateFormat = PersianDateFormat("F/j")
    val finalDate = pDateFormat.format(pDate)


    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) PRIMARY100 else SECONDARY100)
            .clickable {
                onClick.invoke(date)
            }, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .padding(top = 14.dp)
                .padding(horizontal = 12.dp),
            text = finalDate.split("/")[0],
            style = fontMedium14(if (isSelected) PRIMARY500 else SECONDARY500)
        )
        Text(
            modifier = Modifier.padding(top = 2.dp, bottom = 14.dp),
            text = finalDate.split("/")[1],
            style = fontMedium14(if (isSelected) PRIMARY500 else SECONDARY500)
        )
    }
}

private fun convertToPersianDate(){

}