package com.dewerro.measurer.view.measurement.impl

import android.content.Context
import com.dewerro.measurer.R
import com.dewerro.measurer.fragments.data.OrderData
import com.dewerro.measurer.view.measurement.MeasureCalculatorView
import com.google.android.material.textfield.TextInputEditText

class DoorCalculatorView(context: Context) : MeasureCalculatorView(context) {

    init {
        inflate(context, R.layout.door_calculation_view, this)
    }

    val areaEditText: TextInputEditText
        get() {
            return findViewById(R.id.area_edit_text)
        }

    val heightEditText: TextInputEditText
        get() {
            return findViewById(R.id.height_edit_text)
        }

    val widthEditText: TextInputEditText
        get() {
            return findViewById(R.id.width_edit_text)
        }

    val materialEditText: TextInputEditText
        get() {
            return findViewById(R.id.material_edit_text)
        }

    override fun performCalculation(orderData: OrderData?): OrderData {
        TODO("Not yet implemented")
    }
}