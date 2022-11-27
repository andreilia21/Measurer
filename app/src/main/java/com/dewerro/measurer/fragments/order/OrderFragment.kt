package com.dewerro.measurer.fragments.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.dewerro.measurer.K
import com.dewerro.measurer.fragments.data.OrderData

open class OrderFragment : Fragment() {

    protected fun getOrderDataFromArguments(): OrderData? {
        return arguments?.getParcelable(K.Bundle.ORDER_DATA_KEY)
    }

    object ArgumentWrapper {

        fun of(width: Float, height: Float, material: String? = null): Bundle {
            return of(
                OrderData(
                    width,
                    height,
                    material
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