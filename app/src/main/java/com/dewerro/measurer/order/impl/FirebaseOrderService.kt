package com.dewerro.measurer.order.impl

import android.util.Log
import com.dewerro.measurer.K
import com.dewerro.measurer.fragments.data.OrderData
import com.dewerro.measurer.order.OrderService
import com.dewerro.measurer.util.async.PendingTask
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseOrderService : OrderService {

    override fun createOrder(email: String, orderData: OrderData): PendingTask<Long> {
        val db = Firebase.firestore
        val task = PendingTask<Long>()

        val codeTask = getOrderCode()

        codeTask.onComplete { orderCode ->
            val order = hashMapOf(
                K.Firebase.Orders.Fields.EMAIL to email,
                K.Firebase.Orders.Fields.ORDER_CODE to orderCode,
                K.Firebase.Orders.Fields.TYPE to orderData.orderType,
                K.Firebase.Orders.Fields.MATERIAL to "${orderData.material}",
                K.Firebase.Orders.Fields.WIDTH to orderData.width,
                K.Firebase.Orders.Fields.HEIGHT to orderData.height,
                K.Firebase.Orders.Fields.AREA to orderData.width * orderData.height,
                K.Firebase.Orders.Fields.fittings to orderData.fittings,
                K.Firebase.Orders.Fields.WINDOWSILL to orderData.windowsill,
                K.Firebase.Orders.Fields.LOW_TIDE to orderData.lowTide,
                K.Firebase.Orders.Fields.GLASS to orderData.glass,
                K.Firebase.Orders.Fields.FRAME to orderData.frame
            )

            db.collection(K.Firebase.Orders.Collections.ORDERS).add(order).addOnCompleteListener {
                if (it.isSuccessful) {
                    task.setCompleted(orderCode)
                    Log.i("Firebase", "Order sent successfully.")
                } else {
                    task.setFailure(it.exception)
                    Log.e("Firebase", "Error sending order.", it.exception)
                }
            }
        }

        codeTask.onComplete {
            updateOrderCode(it + 1)
        }

        codeTask.onError { task.setFailure(it) }

        return task
    }

    override fun getOrderCode(): PendingTask<Long> {
        val db = Firebase.firestore
        val task = PendingTask<Long>()

        db.collection(K.Firebase.OrderCode.Collections.ORDER_CODE).get().addOnCompleteListener {
            if (it.isSuccessful) {
                task.setCompleted(it.result.documents[0].get(K.Firebase.OrderCode.Fields.ORDER_CODE) as Long)
                Log.i("Firebase", "Order code successfully received.")
            } else {
                task.setFailure(it.exception)
                Log.e("Firebase", "Error receiving order code.", it.exception)
            }
        }

        return task
    }

    override fun updateOrderCode(value: Long): PendingTask<Unit> {
        val db = Firebase.firestore
        val task = PendingTask<Unit>()

        db.collection(K.Firebase.OrderCode.Collections.ORDER_CODE).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val codeReference = it.result.documents[0].reference
                codeReference.update(K.Firebase.OrderCode.Fields.ORDER_CODE, value)
                task.setCompleted(Unit)
                Log.i("Firebase", "Order code updated successfully.")
            } else {
                task.setFailure(it.exception)
                Log.e("Firebase", "Error updating order code.", it.exception)
            }
        }

        return task
    }
}