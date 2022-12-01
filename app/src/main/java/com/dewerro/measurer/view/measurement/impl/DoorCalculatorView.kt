package com.dewerro.measurer.view.measurement.impl

import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import com.dewerro.measurer.databinding.DoorCalculationViewBinding
import com.dewerro.measurer.fragments.data.OrderData
import com.dewerro.measurer.util.math.round
import com.dewerro.measurer.view.measurement.MeasureCalculatorView

class DoorCalculatorView(context: Context) : MeasureCalculatorView(context) {

    private val binding: DoorCalculationViewBinding
    private var shapeHeight: Float = 0f
    private var shapeWidth: Float = 0f
    private var shapeArea: Float = 0f
    private var material: String? = null

    init {
        binding = DoorCalculationViewBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

        applyInputListenerTo(binding.widthEditText) { shapeWidth = it }
        applyInputListenerTo(binding.heightEditText) { shapeHeight = it }
        applyStringInputListener(binding.materialEditText) { material = it }
    }

    private fun applyInputListenerTo(editText: EditText, onInput: (Float) -> Unit) {
        applyFloatInputListener(
            editText,
            { it.replace("m", "") },
            onInput
        )
    }

    override fun setOrderData(orderData: OrderData) {
        shapeWidth = orderData.width
        shapeHeight = orderData.height

        super.setOrderData(orderData)
    }

    override fun updateMeasurements() {
        shapeArea = (shapeWidth * shapeHeight).round(2)

        binding.heightEditText.setText("$shapeHeight m")
        binding.widthEditText.setText("$shapeWidth m")
        binding.areaEditText.setText("$shapeArea m²")
    }

    override fun performCalculation(): OrderData {
        return OrderData(shapeWidth, shapeHeight, orderData.orderType, material)
    }

}