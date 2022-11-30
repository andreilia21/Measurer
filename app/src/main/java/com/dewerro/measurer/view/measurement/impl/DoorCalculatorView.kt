package com.dewerro.measurer.view.measurement.impl

import android.content.Context
import android.view.LayoutInflater
import com.dewerro.measurer.databinding.DoorCalculationViewBinding
import com.dewerro.measurer.fragments.data.OrderData
import com.dewerro.measurer.view.measurement.MeasureCalculatorView

class DoorCalculatorView(context: Context) : MeasureCalculatorView(context) {

    private val binding: DoorCalculationViewBinding

    init {
        binding = DoorCalculationViewBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
    }

    override fun performCalculation(): OrderData {
        TODO()
    }

    private fun updateMeasurements() {

    }
}