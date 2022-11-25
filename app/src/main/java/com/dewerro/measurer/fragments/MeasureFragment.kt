package com.dewerro.measurer.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.K
import com.dewerro.measurer.R
import com.dewerro.measurer.databinding.FragmentMeasureBinding
import com.dewerro.measurer.math.round
import com.dewerro.measurer.view.listeners.FloatInputListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MeasureFragment : Fragment() {

    private var _binding: FragmentMeasureBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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

        arguments?.apply {
            shapeWidth = getFloat(BundleFactory.WIDTH_BUNDLE_KEY)
            shapeHeight = getFloat(BundleFactory.HEIGHT_BUNDLE_KEY)
        }

        binding.sendButton.setOnClickListener {
            val material = binding.materialEditText.text.toString()

            createOrder(material, shapeWidth, shapeHeight, shapeArea)
        }

        applyInputListenerTo(binding.widthEditText) { shapeWidth = it }
        applyInputListenerTo(binding.heightEditText) { shapeHeight = it }

        updateMeasurements()
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

    private var creatingOrder = false

    /**
     * Формирует и отправляет заказ в Firebase
     * @param material Материал изделия
     * @param width Ширина изделия
     * @param height Высота изделия
     * @param area Площадь изделия
     */
    private fun createOrder(material: String, width: Float, height: Float, area: Float) {
        creatingOrder = true

        val db = Firebase.firestore

        val order = hashMapOf(
            "email" to getEmail(),
            "material" to material,
            "width" to width,
            "height" to height,
            "area" to area
        )

        db.collection(K.Firebase.DOORS_COLLECTION_NAME).add(order).addOnCompleteListener {
            if (it.isSuccessful) {
                Snackbar.make(binding.root, R.string.sent, Toast.LENGTH_LONG).show()
                Log.i("Firebase", "Order sent successfully.")
            } else {
                Snackbar.make(binding.root, it.exception!!.localizedMessage!!, Toast.LENGTH_LONG)
                Log.e("Firebase", "Error sending order.", it.exception)
            }
            creatingOrder = false
        }
    }

    /**
     * Получение адреса электронной почты из локального хранилища
     */
    private fun getEmail(): String? {
        val preferences = activity?.getSharedPreferences(K.SharedPreferences.FIREBASE_EMAIL, Context.MODE_PRIVATE)
        return preferences?.getString(K.SharedPreferences.FIREBASE_EMAIL, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    object BundleFactory {
        internal const val WIDTH_BUNDLE_KEY = "width"
        internal const val HEIGHT_BUNDLE_KEY = "height"

        fun of(width: Float, height: Float): Bundle {
            return Bundle().apply {
                putFloat(WIDTH_BUNDLE_KEY, width)
                putFloat(HEIGHT_BUNDLE_KEY, height)
            }
        }
    }
}