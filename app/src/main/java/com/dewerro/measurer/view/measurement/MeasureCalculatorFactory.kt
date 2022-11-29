package com.dewerro.measurer.view.measurement

import android.content.Context

interface MeasureCalculatorFactory {

    fun createCalculatorView(context: Context, orderType: String): MeasureCalculatorView

}