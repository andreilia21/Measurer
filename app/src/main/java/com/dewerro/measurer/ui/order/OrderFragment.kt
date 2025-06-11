package com.dewerro.measurer.ui.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.dewerro.measurer.K
import com.dewerro.measurer.ui.data.OrderData

open class OrderFragment : Fragment() {

    /**
     * Получает заказ через аргументы фрагмента.
     * @return OrderData - данные заказа
     * @see OrderData
     */
    protected fun getOrderDataFromArguments(): OrderData? {
        return arguments?.getParcelable(K.Bundle.ORDER_DATA_KEY)
    }

    object ArgumentWrapper {

        fun of(
            width: Float,
            height: Float,
            orderType: String,
            material: String? = null,
            frame: Float = 0f,
            glass: Float = 0f,
            windowsill: Float = 0f,
            lowTide: Float = 0f,
            fittings: String? = null
        ): Bundle {
            return of(
                OrderData(
                    width = width,
                    height = height,
                    orderType = orderType,
                    material = material,
                    frame = frame,
                    glass = glass,
                    windowsill = windowsill,
                    lowTide = lowTide,
                    fittings = fittings
                )
            )
        }

        fun of(orderData: OrderData): Bundle {
            return Bundle().apply {
                putParcelable(K.Bundle.ORDER_DATA_KEY, orderData)
            }
        }
    }
}