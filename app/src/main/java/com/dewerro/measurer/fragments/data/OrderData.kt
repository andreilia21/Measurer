package com.dewerro.measurer.fragments.data

import android.os.Parcel
import android.os.Parcelable

/**
 * Класс данных о заказе, нужен для передачи между фрагментами.
 */
class OrderData(
    val width: Float,
    val height: Float,
    val orderType: String,
    val material: String? = null,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readString() ?: "door",
        parcel.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeFloat(width)
        dest.writeFloat(height)
        dest.writeString(orderType)
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