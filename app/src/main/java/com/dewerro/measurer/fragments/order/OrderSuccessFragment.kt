package com.dewerro.measurer.fragments.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.K
import com.dewerro.measurer.K.Bundle.ORDER_CODE
import com.dewerro.measurer.R
import com.dewerro.measurer.databinding.FragmentOrderSuccessBinding

class OrderSuccessFragment : Fragment() {

    private var _binding: FragmentOrderSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initContinueButton()

        setOrderCode()
    }

    private fun initContinueButton() {
        binding.continueButton.setOnClickListener {
            findNavController().navigate(R.id.action_OrderSuccessFragment_to_SelectImageFragment)
        }
    }

    private fun setOrderCode() {
        val orderCode = arguments?.getLong(ORDER_CODE, -1)

        binding.orderCode.text = resources
            .getString(R.string.loading_order_code)
            .replace(K.Placeholders.P_ORDER_CODE, "$orderCode")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}