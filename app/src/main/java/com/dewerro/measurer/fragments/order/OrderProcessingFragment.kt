package com.dewerro.measurer.fragments.order

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.K
import com.dewerro.measurer.R
import com.dewerro.measurer.databinding.FragmentOrderProcessingBinding
import com.dewerro.measurer.fragments.data.OrderData
import com.dewerro.measurer.order.impl.FirebaseOrderService

class OrderProcessingFragment : OrderFragment() {

    private var _binding: FragmentOrderProcessingBinding? = null
    private val binding get() = _binding!!

    private lateinit var orderData: OrderData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderProcessingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderData = getOrderDataFromArguments()!!
        createOrder("шпон брат", 10f, 10f, 100f)
    }

    /**
     * Получение адреса электронной почты из локального хранилища
     */
    private fun getEmail(): String? {
        val preferences = requireActivity().getSharedPreferences(
            K.SharedPreferences.FIREBASE_EMAIL,
            Context.MODE_PRIVATE
        )
        return preferences?.getString(K.SharedPreferences.FIREBASE_EMAIL, null)
    }

    /**
     * Формирует и отправляет заказ в Firebase
     * @param material Материал изделия
     * @param width Ширина изделия
     * @param height Высота изделия
     * @param area Площадь изделия
     */
    private fun createOrder(material: String, width: Float, height: Float, area: Float) {
        val service = FirebaseOrderService()

        getEmail()?.let { email ->
            service.createOrder(email, material, width, height, area)
                .onComplete { showSuccessFragment(it) }
                .onError { if (it != null) showFailureFragment(it) }
        }
    }

    private fun showSuccessFragment(orderCode: Long) {
        val argumentBundle = Bundle().apply {
            putLong(K.Bundle.ORDER_CODE, orderCode)
        }

        findNavController().navigate(
            R.id.action_OrderProcessingFragment_to_OrderSuccessFragment,
            argumentBundle
        )
    }

    private fun showFailureFragment(exception: Throwable) {
        val argumentBundle = ArgumentWrapper.of(orderData)
        val detailsMessage = exception.localizedMessage!!

        argumentBundle.putString(K.Bundle.ORDER_ERROR_DETAILS, detailsMessage)

        findNavController().navigate(
            R.id.action_OrderProcessingFragment_to_OrderFailureFragment,
            argumentBundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}