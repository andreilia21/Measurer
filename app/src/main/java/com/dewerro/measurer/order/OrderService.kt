package com.dewerro.measurer.order

import com.dewerro.measurer.util.async.PendingTask

/**
 * Интерфейс, который позволяет взаимодействовать с удалённой базой данных.
 * Абстрагирует логику взаимодействия с внешними библиотеками, делая архитектуру в разы гибче.
 */
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