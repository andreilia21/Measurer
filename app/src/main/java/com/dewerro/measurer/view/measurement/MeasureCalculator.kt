package com.dewerro.measurer.view.measurement

import com.dewerro.measurer.fragments.data.OrderData

interface MeasureCalculator {

    // TODO: rename
    fun performCalculation(): OrderData

    fun setOrderData(orderData: OrderData)

}