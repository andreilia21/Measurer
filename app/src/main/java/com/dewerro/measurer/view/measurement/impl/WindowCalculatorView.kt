package com.dewerro.measurer.view.measurement.impl

import android.content.Context
import com.dewerro.measurer.fragments.data.OrderData
import com.dewerro.measurer.view.measurement.MeasureCalculatorView

class WindowCalculatorView(context: Context) : MeasureCalculatorView(context) {

    init {
        inflate(context, ... /* айди лэйаута */, this)
    }

    override fun performCalculation(orderData: OrderData?): OrderData {
        TODO("Not yet implemented")
    }

}