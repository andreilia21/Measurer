package com.dewerro.measurer.order

import com.dewerro.measurer.util.async.PendingTask

interface OrderService {

    fun createOrder(
        email: String,
        material: String,
        width: Float,
        height: Float,
        area: Float
    ): PendingTask<Long>

    fun getOrderCode(): PendingTask<Long>

    fun updateOrderCode(value: Long): PendingTask<Unit>
}