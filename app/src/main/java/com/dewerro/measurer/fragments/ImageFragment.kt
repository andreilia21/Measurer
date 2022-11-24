package com.dewerro.measurer.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.R
import com.dewerro.measurer.databinding.FragmentImageBinding
import com.dewerro.measurer.math.Vector2d

class ImageFragment : Fragment() {

    private var _binding: FragmentImageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        arguments?.getString("imageURI")?.let {
            binding.imageToPaint.setImageURI(Uri.parse(it))
        }

        setCoordinatesListener()
        setClearButtonListener()
        setSliderListener()
        setNextButtonListener()
    }

    private fun setCoordinatesListener() {
        val imageToPaint = binding.imageToPaint

        imageToPaint.setOnTouchListener { view, motionEvent ->
            if (imageToPaint.getPointsAmount() < 4) {
                val point = Vector2d(motionEvent.x, motionEvent.y)

                imageToPaint.addPoint(point)
            }

            if(imageToPaint.getPointsAmount() == 4 && !binding.imageNextButton.isEnabled){
                binding.imageNextButton.isEnabled = true
            }

            view.performClick()
        }
    }
    
    private fun setClearButtonListener() {
        binding.imageClearButton.setOnClickListener { 
            binding.imageToPaint.clearPoints()
            binding.imageNextButton.isEnabled = false
        }
    }

    private fun setSliderListener() {
        binding.slider.addOnChangeListener { _, value, _ ->
            binding.imageToPaint.setPointLengthRatio(value)
        }
    }

    private fun setNextButtonListener() {
        binding.imageNextButton.setOnClickListener {
            val imageToPaint = binding.imageToPaint

            if(imageToPaint.getPointsAmount() >= 4){

                findNavController().navigate(
                    R.id.action_ImageFragment_to_MeasureFragment,
                    MeasureFragment.BundleFactory.of(imageToPaint.shapeWidth, imageToPaint.shapeHeight)
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}