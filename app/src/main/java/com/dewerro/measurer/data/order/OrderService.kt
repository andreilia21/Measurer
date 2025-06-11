package com.dewerro.measurer.data.order

import com.dewerro.measurer.ui.data.OrderData
import com.dewerro.measurer.util.async.PendingTask

/**
 * Интерфейс, который позволяет взаимодействовать с удалённой базой данных.
 * Абстрагирует логику взаимодействия с внешними библиотеками, делая архитектуру в разы гибче.
 */
interface OrderService {

    fun createOrder(email: String, orderData: OrderData): PendingTask<Long>

    fun getOrderCode(): PendingTask<Long>

    fun updateOrderCode(value: Long): PendingTask<Unit>
}