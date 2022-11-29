package com.dewerro.measurer.view.measurement

import com.dewerro.measurer.fragments.data.OrderData

interface MeasureCalculator {

    fun performCalculation(orderData: OrderData?): OrderData

}