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
    var material: String? = null,
    val frame: Float? = null,
    val glass: Float? = null,
    val windowsill: Float? = null,
    val lowTide: Float? = null,
    val fittings: Float? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        width = parcel.readFloat(),
        height = parcel.readFloat(),
        orderType = parcel.readString() ?: "door",
        material = parcel.readString(),
        frame = parcel.readFloat(),
        glass = parcel.readFloat(),
        windowsill = parcel.readFloat(),
        lowTide = parcel.readFloat(),
        fittings = parcel.readFloat()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeFloat(width)
        dest.writeFloat(height)
        dest.writeString(orderType)
        dest.writeString(material)
        frame?.let { dest.writeFloat(it) }
        glass?.let { dest.writeFloat(it) }
        windowsill?.let { dest.writeFloat(it) }
        lowTide?.let { dest.writeFloat(it) }
        fittings?.let { dest.writeFloat(it) }
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