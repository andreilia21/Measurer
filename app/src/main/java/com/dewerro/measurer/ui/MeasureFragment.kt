package com.dewerro.measurer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.R
import com.dewerro.measurer.databinding.FragmentMeasureBinding
import com.dewerro.measurer.ui.data.OrderData
import com.dewerro.measurer.ui.order.OrderFragment
import com.dewerro.measurer.ui.order.OrderProcessingFragment
import com.dewerro.measurer.ui.view.measurement.MeasureCalculator
import com.dewerro.measurer.ui.view.measurement.MeasureCalculatorFactory
import com.dewerro.measurer.ui.view.measurement.impl.MeasureCalculatorFactoryImpl

class MeasureFragment : OrderFragment() {

    private var _binding: FragmentMeasureBinding? = null
    private val binding get() = _binding!!

    private val measureCalculatorFactory: MeasureCalculatorFactory = MeasureCalculatorFactoryImpl()
    private lateinit var measureCalculator: MeasureCalculator

    private lateinit var orderData: OrderData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeasureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем данные заказа
        getOrderData()

        // Инициализируем виджеты
        initMeasurementCalculator()
        initMeasurementToolbar()
        initSendButton()
    }

    /**
     * Получает данные заказа через переданные аргументы.
     */
    private fun getOrderData() {
        orderData = getOrderDataFromArguments()!!
    }

    /**
     * Инициализирует виджет подсчёта измерений.
     * Создаёт виджет относительно выбранного пользователем типа измерения.
     * @see MeasureCalculatorFactory
     */
    private fun initMeasurementCalculator() {
        // Создаём виджет
        val calculator = measureCalculatorFactory.createCalculatorView(
            requireContext(),
            orderData.orderType // относительно типа заказа
        )

        // Добавляем виджет во фрагмент
        binding.calculatorSocket.addView(calculator)

        // Инициализируем виджет
        measureCalculator = calculator
        calculator.setOrderData(orderData)
    }

    /**
     * Инициализирует верхнюю панель во фрагменте.
     * При нажатии кнопки назад возвращает в предыдущий фрагмент
     */
    private fun initMeasurementToolbar() {
        binding.measurementToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    /**
     * Инициализирует виджет кнопки "Отправить". Ставит обработчик события при нажатии на кнопку.
     * @see onSendButtonPressed
     */
    private fun initSendButton() {
        binding.sendButton.setOnClickListener {
            onSendButtonPressed()
        }
    }

    /**
     * Обработчик события при нажатии кнопки "Отправить".
     * Получает значения из текстовых полей и переходит во фрагмент ожидания.
     * @see OrderProcessingFragment
     */
    private fun onSendButtonPressed() {
        val orderData = measureCalculator.performCalculation()
        val bundle = ArgumentWrapper.of(orderData)

        findNavController().navigate(
            R.id.action_MeasureFragment_to_OrderProcessingFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}