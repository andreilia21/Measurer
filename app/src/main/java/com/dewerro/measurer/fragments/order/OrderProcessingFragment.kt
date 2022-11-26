package com.dewerro.measurer.fragments.order

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.K
import com.dewerro.measurer.R
import com.dewerro.measurer.databinding.FragmentOrderProcessingBinding
import com.dewerro.measurer.fragments.data.OrderData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
    }

    /**
     * Получение адреса электронной почты из локального хранилища
     */
    private fun getEmail(): String? {
        val preferences = activity?.getSharedPreferences(K.SharedPreferences.FIREBASE_EMAIL, Context.MODE_PRIVATE)
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
        val db = Firebase.firestore

        val order = hashMapOf(
            "email" to getEmail(),
            "material" to material,
            "width" to width,
            "height" to height,
            "area" to area
        )

        val orderCode = 12523

        db.collection(K.Firebase.DOORS_COLLECTION_NAME).add(order).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.i("Firebase", "Order sent successfully.")
                showSuccessFragment(orderCode)
            } else {
                Log.e("Firebase", "Error sending order.", it.exception)
                showFailureFragment(it.exception!!)
            }
        }
    }

    private fun showSuccessFragment(orderCode: Int) {
        val argumentBundle = Bundle().apply {
            putInt(K.Bundle.ORDER_CODE, orderCode)
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