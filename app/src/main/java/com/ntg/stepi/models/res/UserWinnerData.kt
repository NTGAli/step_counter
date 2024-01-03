package com.ntg.stepi.models.res

import android.os.Parcel
import android.os.Parcelable
import com.ntg.stepi.util.extension.orZero

data class UserWinnerData(
    val uid: String? = null,
    val fullName: String? = null,
    val steps: Int? = null,
    val leagueName: String? = null,
    val days: Int? = null,
    val sponsor: String? = null,
    val sponsorLink: String? = null,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(fullName)
        parcel.writeInt(steps.orZero())
        parcel.writeString(leagueName)
        parcel.writeInt(days.orZero())
        parcel.writeString(sponsor)
        parcel.writeString(sponsorLink)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserWinnerData> {
        override fun createFromParcel(parcel: Parcel): UserWinnerData {
            return UserWinnerData(parcel)
        }

        override fun newArray(size: Int): Array<UserWinnerData?> {
            return arrayOfNulls(size)
        }
    }
}
