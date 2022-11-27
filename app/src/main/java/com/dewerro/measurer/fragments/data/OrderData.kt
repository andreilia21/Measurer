package com.dewerro.measurer.fragments.data

import android.os.Parcel
import android.os.Parcelable

class OrderData(
    val width: Float,
    val height: Float,
    val material: String? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeFloat(width)
        dest.writeFloat(height)
        dest.writeString(material)
    }

    companion object CREATOR : Parcelable.Creator<OrderData> {
        override fun createFromParcel(parcel: Parcel): OrderData {
            return OrderData(parcel)
        }

        override fun newArray(size: Int): Array<OrderData?> {
            return arrayOfNulls(size)
        }
    }
}