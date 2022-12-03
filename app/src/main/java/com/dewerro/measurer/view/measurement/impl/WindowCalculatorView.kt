package com.dewerro.measurer.view.measurement.impl

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import com.dewerro.measurer.K.Placeholders.P_HINGES
import com.dewerro.measurer.R
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
    private var glassArea = 0.0f
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
        applyStringInputListener(binding.windowMaterialEditText) { material = it }
        applyFloatInputListenerTo(binding.frameEditText) {
            framePercent = it.coercePercent().round(2)
        }
        applyFloatInputListenerTo(binding.windowsillEditText) { windowsill = it }
        applyFloatInputListenerTo(binding.lowTideEditText) { lowTide = it }
        applyStringInputListener(binding.windowFittingsEditText) { fittings = it }
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
        windowsill = orderData.windowsill
        lowTide = orderData.lowTide
        fittings = orderData.fittings

        if(framePercent.isZero()) {
            framePercent = 5f
        }

        if(windowsill.isZero()) {
            windowsill = (windowWidth + 0.15f).round(2)
        }

        if(lowTide.isZero()) {
            lowTide = 0f.coerceAtLeast((windowWidth - 0.15f)).round(2)
        }

        if(fittings.isNullOrEmpty()) {
            var hinges = 2

            if (windowHeight > 1.2f)
                hinges++

            fittings = resources.getString(R.string.window_fittings)
                .replace(P_HINGES, "$hinges")

            binding.windowFittingsEditText.setText(fittings)
        }

        super.setOrderData(orderData)
    }

    @SuppressLint("SetTextI18n")
    override fun updateMeasurements() {
        val shapeArea = windowWidth * windowHeight
        val glassPercent = (100f - framePercent).toPercent()
        glassArea = shapeArea * glassPercent

        frameArea = (shapeArea - glassArea).round(2)

        binding.areaEditText.setText("$frameArea m²")
        binding.heightEditText.setText("$windowHeight m")
        binding.widthEditText.setText("$windowWidth m")
        binding.frameEditText.setText("$framePercent %")
        binding.glassEditText.setText("${glassArea.round(2)} m²")
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
            glassArea,
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

    private fun Float.isZero(): Boolean {
        return this.compareTo(0f) == 0
    }
}