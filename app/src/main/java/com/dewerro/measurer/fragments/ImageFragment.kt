package com.dewerro.measurer.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.K
import com.dewerro.measurer.R
import com.dewerro.measurer.databinding.FragmentImageBinding
import com.dewerro.measurer.fragments.order.OrderFragment
import com.dewerro.measurer.util.math.Vector2d
import com.dewerro.measurer.view.MeasurerImageView

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

        // Инициализируем виджеты фрагмента
        initImageToolbar()
        initImageView()
        initClearButton()
        initSliderView()
        initNextButton()
    }

    /**
     * Инициализирует верхнюю панель во фрагменте.
     * При нажатии кнопки назад возвращает в предыдущий фрагмент
     */
    private fun initImageToolbar() {
        binding.imageToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    /**
     * Инициализирует виджет картинки.
     * Ставит обработчик события при нажатии на картинку.
     * Ставит выбранную картинку на виджет.
     * @see MeasurerImageView
     */
    private fun initImageView() {
        val imageToPaint = binding.imageToPaint

        // Получаем выбранную картинку
        arguments?.getString(K.Bundle.GALLERY_IMAGE_URI)?.let {
            // Ставим выбранную картинку на виджет
            imageToPaint.setImageURI(Uri.parse(it))
        }

        // Обновляем масштаб измерений на картинке
        imageToPaint.setPointLengthRatio(binding.slider.value)

        // Устанавливаем обработчик событий при нажатии на картинку
        imageToPaint.setOnTouchListener { view, motionEvent ->

            // Если лимит точек не превышен, ставим точку на картинку
            if (imageToPaint.getPointsAmount() < 4) {
                val point = Vector2d(motionEvent.x, motionEvent.y)

                imageToPaint.addPoint(point)
            }

            // Если поставлено достаточно точек, включаем кнопку "Далее"
            if (imageToPaint.getPointsAmount() == 4 && !binding.imageNextButton.isEnabled) {
                binding.imageNextButton.isEnabled = true
            }

            view.performClick()
        }
    }

    /**
     * Инициализирует кнопку очистки. При нажатии на кнопку очищаются точки на виджете картинки и
     * отключается кнопка "Далее"
     */
    private fun initClearButton() {
        binding.imageClearButton.setOnClickListener {
            // Очищаем точки на картинке
            binding.imageToPaint.clearPoints()

            // Отключаем кнопку "Далее"
            binding.imageNextButton.isEnabled = false
        }
    }

    /**
     * Инициализирует виджет масштаба. Устанавливает обработчик события при обновлении значения.
     */
    private fun initSliderView() {
        binding.slider.addOnChangeListener { _, value, _ ->
            // При обновлении масштаба обновляем значения на картинке.
            binding.imageToPaint.setPointLengthRatio(value)
        }
    }

    /**
     * Инциализирует кнопку "Далее". При нажатии на кнопку переходит во фрагмент отправки данных.
     * @see MeasureFragment
     */
    private fun initNextButton() {
        binding.imageNextButton.setOnClickListener {
            val imageToPaint = binding.imageToPaint

            // Если пользователь поставил достаточное количество точек, переходим во фрагмент
            if (imageToPaint.getPointsAmount() >= 4) {

                findNavController().navigate(
                    R.id.action_ImageFragment_to_MeasureFragment,
                    // Отправляем во фрагмент измеренные данные
                    OrderFragment.ArgumentWrapper.of(
                        imageToPaint.shapeWidth,
                        imageToPaint.shapeHeight
                    )
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}