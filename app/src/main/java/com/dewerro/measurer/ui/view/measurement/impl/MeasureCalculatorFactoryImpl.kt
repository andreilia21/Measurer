package com.dewerro.measurer.ui.view.measurement.impl

import android.content.Context
import com.dewerro.measurer.K
import com.dewerro.measurer.ui.view.measurement.MeasureCalculatorFactory
import com.dewerro.measurer.ui.view.measurement.MeasureCalculatorView

class MeasureCalculatorFactoryImpl : MeasureCalculatorFactory {
    override fun createCalculatorView(context: Context, orderType: String): MeasureCalculatorView {

        when(orderType){
            K.Bundle.DOOR_CHOICE -> return DoorCalculatorView(context)
            K.Bundle.WINDOW_CHOICE -> return WindowCalculatorView(context)
        }

        throw IllegalArgumentException("Unknown order type")
    }
}