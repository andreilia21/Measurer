package com.dewerro.measurer.fragments.order

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.HandlerCompat
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.K
import com.dewerro.measurer.K.Fragments.OrderProcessingFragment.PROCESS_WAIT_MILLIS
import com.dewerro.measurer.R
import com.dewerro.measurer.auth.Auth
import com.dewerro.measurer.databinding.FragmentOrderProcessingBinding
import com.dewerro.measurer.fragments.data.OrderData
import com.dewerro.measurer.order.Orders
import com.dewerro.measurer.util.async.scheduleTask

class OrderProcessingFragment : OrderFragment() {

    private var _binding: FragmentOrderProcessingBinding? = null
    private val binding get() = _binding!!

    private val mainThreadHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())

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

        // Получаем данные о заказе из аргументов
        orderData = getOrderDataFromArguments()!!

        // Выполняем задачу через несколько секунд
        scheduleTask(PROCESS_WAIT_MILLIS) {
            // Отправляем заказ с основного потока
            mainThreadHandler.post {
                createOrder(orderData)
            }
        }
    }

    /**
     * Получение адреса электронной почты из локального хранилища
     */
    private fun getEmail(): String? {
        return Auth.getSavedEmail(requireActivity())
    }

    /**
     * Формирует и отправляет заказ в Firebase
     * @param orderData данные заказа
     */
    private fun createOrder(orderData: OrderData) {
        val service = Orders.getOrderService()

        getEmail()?.let { email ->
            service.createOrder(email, orderData)
                .onComplete { showSuccessFragment(it) }
                .onError { if (it != null) showFailureFragment(it) }
        }
    }

    /**
     * Показывает фрагмент успешной отправки заказа
     * @see OrderSuccessFragment
     * @param orderCode Код отправленного заказа
     */
    private fun showSuccessFragment(orderCode: Long) {
        // Добавляем код заказа в аргументы
        val argumentBundle = Bundle().apply {
            putLong(K.Bundle.ORDER_CODE, orderCode)
        }

        // Переходим во фрагмент с указанными аргументами
        findNavController().navigate(
            R.id.action_OrderProcessingFragment_to_OrderSuccessFragment,
            argumentBundle
        )
    }

    /**
     * Показывает фрагмент неудачной отправки заказа
     * @see OrderFailureFragment
     * @param exception Исключение, вызвавшее проблему отправки
     */
    private fun showFailureFragment(exception: Throwable) {
        // "Пакуем" данные заказа для передачи во фрагмент
        val argumentBundle = ArgumentWrapper.of(orderData)

        // Получаем причину вызова исключения
        val detailsMessage = exception.localizedMessage!!

        // Добавляем сообщение в аргументы
        argumentBundle.putString(K.Bundle.ORDER_ERROR_DETAILS, detailsMessage)

        // Отправляемся во фрагмент
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