package com.dewerro.measurer.ui.view.measurement

import com.dewerro.measurer.ui.data.OrderData

interface MeasureCalculator {

    // TODO: rename
    fun performCalculation(): OrderData

    fun setOrderData(orderData: OrderData)

}