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
    private var doorHeight: Float = 0f
    private var doorWidth: Float = 0f
    private var doorArea: Float = 0f
    private var material: String? = null

    init {
        binding = DoorCalculationViewBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

        applyFloatInputListenerTo(binding.widthEditText) { doorWidth = it }
        applyFloatInputListenerTo(binding.heightEditText) { doorHeight = it }
        applyStringInputListener(binding.materialEditText) { material = it }
    }

    private fun applyFloatInputListenerTo(editText: EditText, onInput: (Float) -> Unit) {
        applyFloatInputListener(
            editText,
            { it.replace("m", "") },
            onInput
        )
    }

    override fun setOrderData(orderData: OrderData) {
        doorWidth = orderData.width
        doorHeight = orderData.height
        material = orderData.material

        super.setOrderData(orderData)
    }

    override fun updateMeasurements() {
        doorArea = (doorWidth * doorHeight).round(2)

        binding.heightEditText.setText("$doorHeight m")
        binding.widthEditText.setText("$doorWidth m")
        binding.areaEditText.setText("$doorArea m²")
    }

    override fun performCalculation(): OrderData {
        return OrderData(doorWidth, doorHeight, orderData.orderType, material)
    }

}