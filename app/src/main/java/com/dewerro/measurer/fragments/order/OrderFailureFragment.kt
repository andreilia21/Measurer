package com.dewerro.measurer.fragments.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.K.Bundle.ORDER_ERROR_DETAILS
import com.dewerro.measurer.K.Placeholders.P_ORDER_ERROR_DETAILS
import com.dewerro.measurer.R
import com.dewerro.measurer.databinding.FragmentOrderFailureBinding

class OrderFailureFragment : OrderFragment() {

    private var _binding: FragmentOrderFailureBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderFailureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRetryButton()
        initContinueButton()

        fillErrorDetails()
    }

    private fun initRetryButton() {
        val orderData = getOrderDataFromArguments()!!

        binding.errorRetryButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_OrderFailureFragment_to_OrderProcessingFragment,
                ArgumentWrapper.of(orderData)
            )
        }
    }

    private fun initContinueButton() {
        binding.errorContinueButton.setOnClickListener {
            findNavController().navigate(R.id.action_OrderFailureFragment_to_SelectImageFragment)
        }
    }

    private fun fillErrorDetails() {
        val errorDetails = arguments?.getString(ORDER_ERROR_DETAILS) ?: ""

        binding.errorDetails.text = resources
            .getString(R.string.loading_error_details)
            .replace(P_ORDER_ERROR_DETAILS, errorDetails)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}