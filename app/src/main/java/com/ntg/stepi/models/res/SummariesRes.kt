package com.ntg.stepi.models.res

import android.os.Parcel
import android.os.Parcelable

data class SummariesRes(
    val rank: String? = null,
    val claps: Int? = null,
    val topMonth: List<SummaryRes>? = null,
    val today: List<SummaryRes>? = null,
    val fos: List<SummaryRes>? = null,
    val all: List<SummaryRes>? = null,
    val deadLineChallenge: String? = null,
    val messagesId: List<String>? = null,
    val versionCode: Int? = null,
    val deadVersionCode: Int? = null,
    val ads: List<ADSRes>? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        listOf(),
        listOf(),
        listOf(),
        listOf(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        listOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(rank)
        parcel.writeValue(claps)
        parcel.writeString(deadLineChallenge)
        parcel.writeStringList(messagesId)
        parcel.writeValue(versionCode)
        parcel.writeValue(deadVersionCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SummariesRes> {
        override fun createFromParcel(parcel: Parcel): SummariesRes {
            return SummariesRes(parcel)
        }

        override fun newArray(size: Int): Array<SummariesRes?> {
            return arrayOfNulls(size)
        }
    }
}
