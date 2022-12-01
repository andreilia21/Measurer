package com.dewerro.measurer.view.measurement.impl

import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import com.dewerro.measurer.databinding.WindowCalculationViewBinding
import com.dewerro.measurer.fragments.data.OrderData
import com.dewerro.measurer.util.math.round
import com.dewerro.measurer.view.measurement.MeasureCalculatorView

class WindowCalculatorView(context: Context) : MeasureCalculatorView(context) {

    private val binding: WindowCalculationViewBinding

    private var windowHeight = 0.0f
    private var windowWidth = 0.0f
    private var frameArea = 0.0f
    private var material: String? = null
    private var framePercent = 100.0f
    private var glassPercent = 0.0f
    private var windowsill: Float = 0.0f
    private var lowTide: Float = 0.0f
    private var fittings: String? = null

    init {
        binding = WindowCalculationViewBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

        applyFloatInputListenerTo(binding.heightEditText) { windowHeight = it }
        applyFloatInputListenerTo(binding.widthEditText) { windowWidth = it }
        applyStringInputListener(binding.materialEditText) { material = it }
        applyFloatInputListenerTo(binding.frameEditText) {
            framePercent = it.coercePercent().round(2)
            glassPercent = (100f - framePercent).round(2)
        }
        applyFloatInputListenerTo(binding.glassEditText) {
            glassPercent = it.coercePercent().round(2)
            framePercent = (100f - glassPercent).round(2)
        }
        applyFloatInputListenerTo(binding.windowsillEditText) { windowsill = it }
        applyFloatInputListenerTo(binding.lowTideEditText) { lowTide = it }
        applyStringInputListener(binding.fittingsEditText) { fittings = it }
    }

    private fun applyFloatInputListenerTo(editText: EditText, onInput: (Float) -> Unit) {
        applyFloatInputListener(
            editText,
            {
                it.replace("m", "").replace("%", "")
            },
            onInput
        )
    }

    override fun setOrderData(orderData: OrderData) {
        windowHeight = orderData.height
        windowWidth = orderData.width
        material = orderData.material
        framePercent = orderData.frame.coercePercent()
        glassPercent = orderData.glass.coercePercent()
        windowsill = orderData.windowsill
        lowTide = orderData.lowTide
        fittings = orderData.fittings

        if(framePercent.compareTo(0f) == 0) {
            framePercent = 100f
            glassPercent = 0f
        }

        super.setOrderData(orderData)
    }

    override fun updateMeasurements() {
        val shapeArea = windowWidth * windowHeight
        val glassArea = shapeArea * glassPercent.toPercent()

        frameArea = (shapeArea - glassArea).round(2)

        binding.areaEditText.setText("$frameArea m²")
        binding.heightEditText.setText("$windowWidth m")
        binding.widthEditText.setText("$windowHeight m")
        binding.frameEditText.setText("$framePercent %")
        binding.glassEditText.setText("$glassPercent %")
        binding.windowsillEditText.setText("$windowsill m")
        binding.lowTideEditText.setText("$lowTide m")
    }

    override fun performCalculation(): OrderData {
        return OrderData(
            windowWidth,
            windowHeight,
            orderData.orderType,
            material,
            framePercent,
            glassPercent,
            windowsill,
            lowTide,
            fittings
        )
    }

    private fun Float.coercePercent(): Float {
        return this.coerceIn(0f, 100f)
    }

    private fun Float.toPercent(): Float {
        return this / 100f
    }
}