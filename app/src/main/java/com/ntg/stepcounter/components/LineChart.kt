package com.ntg.stepcounter.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ntg.stepcounter.R
import com.ntg.stepcounter.ui.theme.SECONDARY100
import com.ntg.stepcounter.ui.theme.SECONDARY500
import com.ntg.stepcounter.util.extension.timber
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.formatter.DecimalFormatAxisValueFormatter
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerVisibilityChangeListener
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SingleColumnChartWithNegativeValues(
    modifier: Modifier = Modifier,
    data: Map<LocalDate, Float>?,
    onMark: (String) -> Unit = {}
) {



    val z = ZoneId.of("Asia/Tehran")
    val zdt = ZonedDateTime.now(z)
    val today = zdt.toLocalDate()

    var xValuesToDates by remember {
        mutableStateOf(mapOf<Float, LocalDate>())
    }

    var chartEntryModel by remember {
        mutableStateOf(listOf<FloatEntry>())
    }

    var columnColor by remember {
        mutableStateOf(listOf<LineComponent>())
    }




    xValuesToDates = data?.keys?.associateBy { it.toEpochDay().toFloat() } ?: mapOf()

    chartEntryModel = xValuesToDates.keys.zip(data!!.values)
        .map { (x, y) -> FloatEntry(today.toEpochDay().toFloat() - x, y) }



    columnColor = listOf(
        lineComponent(
            color = MaterialTheme.colors.primary,
            thickness = 8.dp,
            shape = RoundedCornerShape(24.dp)
        )
    )


    val chartEntryModels by remember {
        mutableStateOf(entryModelOf(chartEntryModel))
    }


    var mValue by remember {
        mutableStateOf(0f)
    }

    var date2 by remember {
        mutableStateOf(Date())
    }

    var pDate by remember {
        mutableStateOf(PersianDate())
    }


    var pDateFormat by remember {
        mutableStateOf(PersianDateFormat())
    }

//    var datkke2 = remember {
//        mutableStateOf(AxisValueFormatter<AxisPosition.Horizontal.Bottom>())
//    }

    var horizontalAxisValueFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom>? = null

        horizontalAxisValueFormatter =
            AxisValueFormatter { value, _ ->

                mValue = today.toEpochDay().toFloat() - value

                date2 = try {
                    Date.from(xValuesToDates[mValue]?.atStartOfDay(ZoneId.systemDefault())?.toInstant())
                } catch (e: Exception) {
                    Date.from(
                        LocalDate.ofEpochDay(mValue.toLong())?.atStartOfDay(ZoneId.systemDefault())
                            ?.toInstant()
                    )

                }

                pDate = PersianDate(date2)
                pDateFormat = PersianDateFormat("j F")
                pDateFormat.format(pDate)

            }

    var persistentMarkers by remember { mutableStateOf(emptyMap<Float, Marker>()) }

    val markerVisibilityChangeListener = object : MarkerVisibilityChangeListener {
        override fun onMarkerMoved(
            marker: Marker,
            markerEntryModels: List<Marker.EntryModel>
        ) {
            persistentMarkers = mapOf(markerEntryModels.first().entry.x to marker)
        }

        override fun onMarkerShown(
            marker: Marker,
            markerEntryModels: List<Marker.EntryModel>
        ) {
            val markerXValue = markerEntryModels.first().entry.x

            persistentMarkers =
                if (persistentMarkers.containsKey(markerXValue)) emptyMap() else
                    mapOf(markerXValue to marker)
        }
    }


    Chart(
        modifier = modifier.aspectRatio(1.77f),
        chart = columnChart(
            columns = columnColor,
            persistentMarkers = persistentMarkers,
        ),
        model = chartEntryModels,
        marker = rememberMarker(),
        markerVisibilityChangeListener = markerVisibilityChangeListener,
        bottomAxis = rememberBottomAxis(
            valueFormatter = horizontalAxisValueFormatter ?: DecimalFormatAxisValueFormatter(),
            label = com.patrykandpatrick.vico.compose.component.textComponent(
                background = null,
                lineCount = 1,
                typeface = LocalContext.current.resources.getFont(R.font.yekan_regular),
                color = MaterialTheme.colors.primary
            ),
            axis = LineComponent(color = MaterialTheme.colors.primaryVariant.toArgb()),
            guideline = LineComponent(color = MaterialTheme.colors.background.toArgb())
        ),
        isZoomEnabled = false,


        )
}

