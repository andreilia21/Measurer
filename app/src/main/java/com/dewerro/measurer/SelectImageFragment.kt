package com.dewerro.measurer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast.LENGTH_LONG
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.databinding.FragmentSelectImageBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SelectImageFragment : Fragment() {

    private var _binding: FragmentSelectImageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.measureWithArButton.setOnClickListener {
            findNavController().navigate(R.id.action_SelectImageFragment_to_ARFragment)

            createOrder()
        }
    }

    private fun createOrder() {
        val db = Firebase.firestore

        val orderExample = hashMapOf(
            "material" to "wood",
            "width" to 100,
            "height" to 100,
            "area" to 100 * 100
        )

        db.collection("doors").add(orderExample).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.i("Firebase", "Order sent successfully.")
            } else {
                Snackbar.make(binding.root, it.exception!!.localizedMessage!!, LENGTH_LONG)
                Log.e("Firebase", "Error sending order.", it.exception)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}