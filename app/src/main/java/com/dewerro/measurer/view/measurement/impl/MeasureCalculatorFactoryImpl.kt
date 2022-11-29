package com.dewerro.measurer.view.measurement.impl

import android.content.Context
import com.dewerro.measurer.view.measurement.MeasureCalculatorFactory
import com.dewerro.measurer.view.measurement.MeasureCalculatorView

class MeasureCalculatorFactoryImpl : MeasureCalculatorFactory {
    override fun createCalculatorView(context: Context, orderType: String): MeasureCalculatorView {

        when(orderType){
            "door" -> return DoorCalculatorView(context)
            "window" -> return WindowCalculatorView(context)
        }

        throw IllegalArgumentException("Unknown order type")
    }
}