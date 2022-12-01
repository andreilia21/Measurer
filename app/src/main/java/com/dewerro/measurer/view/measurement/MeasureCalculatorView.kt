package com.dewerro.measurer.view.measurement

import android.content.Context
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.dewerro.measurer.fragments.data.OrderData
import com.dewerro.measurer.view.listeners.FloatInputListener
import com.dewerro.measurer.view.listeners.StringInputListener

abstract class MeasureCalculatorView(context: Context) : ConstraintLayout(context), MeasureCalculator {

    private lateinit var _orderData: OrderData
    protected val orderData: OrderData get() = _orderData

    override fun setOrderData(orderData: OrderData) {
        _orderData = orderData

        updateMeasurements()
    }

    /**
     * Устанавливает слушатель на текстовое поле. Когда пользователь что-либо ввёл, вызывается
     * метод обновления данных на текстовых полях.
     * @see updateMeasurements
     * @param editText Текстовое поле
     * @param onInput Лямбда-функция, которая выполняется, когда пользователь что-то ввел
     */
    protected fun applyFloatInputListener(
        editText: EditText,
        preprocess: ((String) -> String) = { it },
        onInput: (Float) -> Unit
    ) {
        FloatInputListener().apply {
            preprocessor {
                preprocess(it)
            }
            onInput {
                onInput(it)
                updateMeasurements()
            }
            applyListenerTo(editText)
        }
    }

    /**
     * Устанавливает слушатель на текстовое поле. Когда пользователь что-либо ввёл, вызывается
     * метод обновления данных на текстовых полях.
     * @see updateMeasurements
     * @param editText Текстовое поле
     * @param onInput Лямбда-функция, которая выполняется, когда пользователь что-то ввел
     */
    protected fun applyStringInputListener(
        editText: EditText,
        onInput: (String) -> Unit
    ) {
        StringInputListener().apply {
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
    protected abstract fun updateMeasurements()

}