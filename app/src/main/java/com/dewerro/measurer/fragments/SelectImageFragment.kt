package com.dewerro.measurer.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.K
import com.dewerro.measurer.R
import com.dewerro.measurer.auth.Auth
import com.dewerro.measurer.databinding.FragmentSelectImageBinding

class SelectImageFragment : Fragment() {

    private var _binding: FragmentSelectImageBinding? = null
    private val binding get() = _binding!!

    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Регистрируем контракт для получения изображения из файлов
        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            val bundle = Bundle()
            bundle.putString(K.Bundle.GALLERY_IMAGE_URI, it.toString())

            createDialog(R.id.action_SelectImageFragment_to_ImageFragment, bundle)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализируем виджеты фрагмента
        initMeasureWithArButton()
        initMeasureWithImageButton()
        initLogoutButton()
    }

    /**
     * Инициализируем кнопку "Измерить с помощью AR".
     * При нажатии на кнопку открывает фрагмент дополненной реальности.
     * @see ARFragment
     */
    private fun initMeasureWithArButton() {
        binding.measureWithArButton.setOnClickListener {
            createDialog(R.id.action_SelectImageFragment_to_ARFragment)
        }
    }

    /**
     * Инициализируем кнопку "Измерить с помощью фотографии".
     * При нажатии на кнопку просит пользователя выбрать фотографию.
     * Затем открывает фрагмент измерения с помощью фотографии.
     * @see ImageFragment
     */
    private fun initMeasureWithImageButton() {
        binding.measureWithImage.setOnClickListener {
            galleryLauncher.launch("image/*")
        }
    }

    /**
     * Инициализируем кнопку выхода из аккаунта.
     * Производит выход из аккаунта. После этого открывает фрагмент авторизации.
     * @see LoginFragment
     */
    private fun initLogoutButton() {
        binding.logoutButton.setOnClickListener {
            Auth.logout()
            Auth.clearCredentials(requireActivity())

            findNavController().navigate(R.id.action_SelectImageFragment_to_LoginFragment)
        }
    }

    /**
     * Создает диалог выбора объекта для измерения.
     * После чего переходит на фрагмент, переданный в navigationResId
     * @param navigationResId действие перехода во фрагмент
     */
    private fun createDialog(navigationResId: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.select_object_to_measure)

        var checkedItem = 0
        builder.setSingleChoiceItems(R.array.user_measure_choices, checkedItem) { _, which ->
            checkedItem = which
        }

        builder.setPositiveButton(R.string.measure) { _, _ ->
            val bundle = Bundle()
            bundle.putInt(K.Bundle.MEASUREMENT_OBJECT_CHOICE, checkedItem)
            findNavController().navigate(navigationResId, bundle)
        }
        builder.setNegativeButton(R.string.cancel, null)

        val dialog = builder.create()
        dialog.show()
    }

    /**
     * Создает диалог выбора объекта для измерения.
     * После чего переходит на фрагмент, переданный в navigationResId
     * @param navigationResId действие перехода во фрагмент
     * @param bundle существующие аргументы навигации
     */
    private fun createDialog(navigationResId: Int, bundle: Bundle) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.select_object_to_measure)

        var checkedItem = 0
        builder.setSingleChoiceItems(R.array.user_measure_choices, checkedItem) { _, which ->
            checkedItem = which
        }

        builder.setPositiveButton(R.string.measure) { _, _ ->
            bundle.putInt(K.Bundle.MEASUREMENT_OBJECT_CHOICE, checkedItem)
            findNavController().navigate(navigationResId, bundle)
        }
        builder.setNegativeButton(R.string.cancel, null)

        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}