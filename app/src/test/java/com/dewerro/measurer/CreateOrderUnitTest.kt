package com.dewerro.measurer

import com.dewerro.measurer.data.order.firebase.FirebaseOrderService
import org.junit.Test

class CreateOrderUnitTest {
    @Test
    fun test() {
        val service = FirebaseOrderService()
        for (i in 0..10) {
            service.createOrder(
                "tashkentonelove@mail.ru",
                "шпон брат",
                10f,
                10f,
                100f
            )
                .onComplete { println(it) }
                .onError { println(it) }
        }
    }
}