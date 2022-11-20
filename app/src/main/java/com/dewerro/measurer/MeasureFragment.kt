package com.dewerro.measurer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dewerro.measurer.databinding.FragmentMeasureBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MeasureFragment : Fragment() {

    private var _binding: FragmentMeasureBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeasureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sendButton.setOnClickListener {
            val material = binding.materialEditText.text.toString()
            val width = binding.widthEditText.text.toString().toFloat()
            val height = binding.heightEditText.text.toString().toFloat()
            val area = binding.areaEditText.text.toString().toFloat()

            createOrder(material, width, height, area)
        }
    }

    private fun createOrder(material: String, width: Float, height: Float, area: Float) {
        val db = Firebase.firestore

        val order = hashMapOf(
            "material" to material,
            "width" to width,
            "height" to height,
            "area" to area
        )

        db.collection("doors").add(order).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.i("Firebase", "Order sent successfully.")
            } else {
                Snackbar.make(binding.root, it.exception!!.localizedMessage!!, Toast.LENGTH_LONG)
                Log.e("Firebase", "Error sending order.", it.exception)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}