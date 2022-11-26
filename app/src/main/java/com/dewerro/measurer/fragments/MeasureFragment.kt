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
import com.dewerro.measurer.util.math.round
import com.dewerro.measurer.view.listeners.FloatInputListener

class MeasureFragment : Fragment() {

    private var _binding: FragmentMeasureBinding? = null
    private val binding get() = _binding!!

    private var shapeWidth = 0.0f
    private var shapeHeight = 0.0f
    private var shapeArea = 0.0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeasureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.measurementToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        arguments?.getParcelable<OrderData>(ORDER_DATA_KEY)?.apply {
            shapeWidth = width
            shapeHeight = height
        }

        binding.sendButton.setOnClickListener {
            onSendButtonPressed()
        }

        applyInputListenerTo(binding.widthEditText) { shapeWidth = it }
        applyInputListenerTo(binding.heightEditText) { shapeHeight = it }

        updateMeasurements()
    }

    private fun onSendButtonPressed() {
        val material = binding.materialEditText.text.toString()
        val bundle = OrderFragment.ArgumentWrapper.of(shapeWidth, shapeHeight, material)

        findNavController().navigate(
            R.id.action_MeasureFragment_to_OrderProcessingFragment,
            bundle
        )
    }

    /**
     * Устанавливает слушатель на текстовое поле
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
     * Установливает значения текстовым полям
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