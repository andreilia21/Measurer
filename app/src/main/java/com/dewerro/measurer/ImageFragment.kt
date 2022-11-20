package com.dewerro.measurer

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.databinding.FragmentImageBinding
import com.dewerro.measurer.models.Vector2d
import kotlin.math.pow
import kotlin.math.sqrt

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
    }

    private fun setCoordinatesListener() {
        var clicksCount = 0

        val dots = mutableListOf<Vector2d>()

        binding.imageToPaint.setOnTouchListener { view, motionEvent ->
            if (clicksCount <= 4) {
                dots.add(Vector2d(motionEvent.x, motionEvent.y))
                clicksCount++
            } else {
                for (i in dots.indices step 2) {
                    val firstDot = dots[i]
                    val secondDot = dots[i + 1]

                    println(calculateDistance(Pair(firstDot, secondDot)))
                }
            }

            view.performClick()
        }
    }

    fun calculateDistance(coordinates: Pair<Vector2d, Vector2d>): Float {
        val firstDot = coordinates.first
        val secondDot = coordinates.second

        val vector = Vector2d(secondDot.x - firstDot.x, secondDot.y - firstDot.y)

        return sqrt(vector.x.pow(2) + vector.y.pow(2))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}