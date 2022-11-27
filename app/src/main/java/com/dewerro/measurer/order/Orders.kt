package com.dewerro.measurer.order

object Orders {

    private var orderService: OrderService? = null

    fun setOrderService(orderService: OrderService) {
        if (this.orderService != null) {
            throw IllegalStateException("Order service cannot be initialized twice")
        }

        this.orderService = orderService
    }

    fun getOrderService(): OrderService = orderService!!

}