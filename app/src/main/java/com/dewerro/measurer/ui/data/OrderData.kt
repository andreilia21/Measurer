package com.dewerro.measurer.ui.data

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
    val frame: Float = 0.0f,
    val glass: Float = 0.0f,
    val windowsill: Float = 0.0f,
    val lowTide: Float = 0.0f,
    val fittings: String? = null
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
        fittings = parcel.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeFloat(width)
        dest.writeFloat(height)
        dest.writeString(orderType)
        dest.writeString(material)
        dest.writeFloat(frame)
        dest.writeFloat(glass)
        dest.writeFloat(windowsill)
        dest.writeFloat(lowTide)
        dest.writeString(fittings)
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