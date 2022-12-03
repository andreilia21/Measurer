package com.dewerro.measurer.view.measurement.impl

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import com.dewerro.measurer.K.Placeholders.P_HINGES
import com.dewerro.measurer.R
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
    private var fittings: String? = null

    init {
        binding = DoorCalculationViewBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

        applyFloatInputListenerTo(binding.widthEditText) { doorWidth = it }
        applyFloatInputListenerTo(binding.heightEditText) { doorHeight = it }
        applyStringInputListener(binding.doorMaterialEditText) { material = it }
        applyStringInputListener(binding.doorFittingsEditText) { fittings = it }
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
        fittings = orderData.fittings

        if(fittings.isNullOrEmpty()) {
            var hinges = 2

            if (doorHeight > 1.2f)
                hinges++

            fittings = resources.getString(R.string.window_fittings)
                .replace(P_HINGES, "$hinges")


            binding.doorFittingsEditText.setText(fittings)
        }

        super.setOrderData(orderData)
    }

    @SuppressLint("SetTextI18n")
    override fun updateMeasurements() {
        doorArea = (doorWidth * doorHeight).round(2)

        binding.heightEditText.setText("$doorHeight m")
        binding.widthEditText.setText("$doorWidth m")
        binding.areaEditText.setText("$doorArea m²")
    }

    override fun performCalculation(): OrderData {
        return OrderData(
            doorWidth,
            doorHeight,
            orderData.orderType,
            material,
            fittings = fittings
        )
    }

}