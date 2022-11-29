package com.dewerro.measurer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.K.Bundle.ORDER_DATA_KEY
import com.dewerro.measurer.R
import com.dewerro.measurer.databinding.FragmentMeasureBinding
import com.dewerro.measurer.fragments.data.OrderData
import com.dewerro.measurer.fragments.order.OrderFragment
import com.dewerro.measurer.fragments.order.OrderProcessingFragment
import com.dewerro.measurer.util.math.round
import com.dewerro.measurer.view.listeners.FloatInputListener
import com.dewerro.measurer.view.measurement.MeasureCalculator
import com.dewerro.measurer.view.measurement.MeasureCalculatorFactory
import com.dewerro.measurer.view.measurement.impl.MeasureCalculatorFactoryImpl

class MeasureFragment : Fragment() {

    private var _binding: FragmentMeasureBinding? = null
    private val binding get() = _binding!!

    private val measureCalculatorFactory: MeasureCalculatorFactory = MeasureCalculatorFactoryImpl()

    private var shapeWidth = 0.0f
    private var shapeHeight = 0.0f
    private var shapeArea = 0.0f
    private lateinit var orderType: String

    private lateinit var measureCalculator: MeasureCalculator

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
        initEditTextViews()

        // Обновляем текстовые виджеты измерений
        updateMeasurements()
    }

    /**
     * Получает данные заказа через переданные аргументы.
     */
    private fun getOrderData() {
        arguments?.getParcelable<OrderData>(ORDER_DATA_KEY)?.apply {
            shapeWidth = width
            shapeHeight = height
            this@MeasureFragment.orderType = orderType
        }
    }

    private fun initMeasurementCalculator() {
        val calculator = measureCalculatorFactory.createCalculatorView(context!!, orderType)

        // binding.calculatorSocket.addView(calculator)

        measureCalculator = calculator
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
        val material = binding.materialEditText.text.toString()
        val bundle = OrderFragment.ArgumentWrapper.of(shapeWidth, shapeHeight, material)

        findNavController().navigate(
            R.id.action_MeasureFragment_to_OrderProcessingFragment,
            bundle
        )
    }

    /**
     * Инициализирует текстовые поля. Ставит обработчики событий при пользовательском вводе.
     * При вводе чего-либо в поля присваивает введённое значение в соответствующую переменную.
     */
    private fun initEditTextViews() {
        applyInputListenerTo(binding.widthEditText) { shapeWidth = it }
        applyInputListenerTo(binding.heightEditText) { shapeHeight = it }
    }

    /**
     * Устанавливает слушатель на текстовое поле. Когда пользователь что-либо ввёл, вызывается
     * метод обновления данных на текстовых полях.
     * @see updateMeasurements
     * @param editText Текстовое поле
     * @param onInput Лямбда-функция, которая выполняется, когда пользователь что-то ввел
     */
    private fun applyInputListenerTo(editText: EditText, onInput: (Float) -> Unit) {
        FloatInputListener().apply {
            preprocessor {
                it.replace("m", "")
            }
            onInput {
                onInput(it)
                updateMeasurements()
            }
            applyListenerTo(editText)
        }
    }

    /**
     * Обновляет значения на текстовых полях.
     * Также локализирует значения по типу 0.0 -> 0.0 м
     */
    private fun updateMeasurements() {
        shapeArea = (shapeWidth * shapeHeight).round(2)

        binding.heightEditText.setText("$shapeHeight m")
        binding.widthEditText.setText("$shapeWidth m")
        binding.areaEditText.setText("$shapeArea m²")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}