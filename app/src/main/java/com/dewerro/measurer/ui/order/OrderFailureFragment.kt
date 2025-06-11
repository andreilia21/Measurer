package com.dewerro.measurer.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.K.Bundle.ORDER_ERROR_DETAILS
import com.dewerro.measurer.K.Placeholders.P_ORDER_ERROR_DETAILS
import com.dewerro.measurer.R
import com.dewerro.measurer.databinding.FragmentOrderFailureBinding
import com.dewerro.measurer.ui.SelectImageFragment

class OrderFailureFragment : OrderFragment() {

    private var _binding: FragmentOrderFailureBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderFailureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализируем виджеты фрагмента
        initRetryButton()
        initContinueButton()

        // Заполняем текстовый виджет деталями вызванного исключения
        fillErrorDetails()
    }

    /**
     * Инициализирует кнопку "Повторить". При нажатии на кнопку
     * переходит во фрагмент отправки заказа.
     * @see OrderProcessingFragment
     */
    private fun initRetryButton() {
        // Получаем данные заказа
        val orderData = getOrderDataFromArguments()!!

        // Устанавливаем обработчик события нажатия
        binding.errorRetryButton.setOnClickListener {
            // Переходим во фрагмент отправки заказа
            findNavController().navigate(
                R.id.action_OrderFailureFragment_to_OrderProcessingFragment,
                ArgumentWrapper.of(orderData)
            )
        }
    }

    /**
     * Инициализирует кнопку "Продолжить". При нажатии на кнопку переходит в основной фрагмент
     * @see SelectImageFragment
     */
    private fun initContinueButton() {
        // Устанавливаем обработчик события нажатия
        binding.errorContinueButton.setOnClickListener {
            // Переходим в основной фрагмент
            findNavController().navigate(R.id.action_OrderFailureFragment_to_SelectImageFragment)
        }
    }

    /**
     * Получает детали ошибки из аргументов и устанавливает в текстовый виджет.
     */
    private fun fillErrorDetails() {
        // Получаем детали ошибки
        val errorDetails = arguments?.getString(ORDER_ERROR_DETAILS) ?: ""

        // Форматируем текстовый виджет и устанавливаем детали ошибки
        binding.errorDetails.text = resources
            .getString(R.string.loading_error_details)
            .replace(P_ORDER_ERROR_DETAILS, errorDetails)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}