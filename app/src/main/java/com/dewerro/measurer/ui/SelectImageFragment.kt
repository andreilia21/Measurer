package com.dewerro.measurer.ui

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.K
import com.dewerro.measurer.K.Bundle.DOOR_CHOICE
import com.dewerro.measurer.K.Bundle.WINDOW_CHOICE
import com.dewerro.measurer.R
import com.dewerro.measurer.data.auth.Auth
import com.dewerro.measurer.databinding.FragmentSelectImageBinding

class SelectImageFragment : Fragment() {

    private var _binding: FragmentSelectImageBinding? = null
    private val binding get() = _binding!!

    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private lateinit var dialogCallback: (Uri?) -> Unit

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
            dialogCallback.invoke(it)
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
            createDialog(R.id.action_SelectImageFragment_to_ARFragment) {
                dialogCallback(null)
            }
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
            createDialog(R.id.action_SelectImageFragment_to_ImageFragment) {
                galleryLauncher.launch("image/*")
            }
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
     * @param onPositive лямбда-выражение, вызываемое при нажатии кнопки "Измерить"
     */
    private fun createDialog(navigationResId: Int, onPositive: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.select_object_to_measure)

        var checkedItem = 0
        builder.setSingleChoiceItems(R.array.user_measure_choices, checkedItem) { _, which ->
            checkedItem = which
        }

        builder.setPositiveButton(R.string.measure) { _, _ ->
            dialogCallback = { imageURI ->
                val bundle = Bundle().apply {
                    when (checkedItem) {
                        0 -> putString(K.Bundle.MEASUREMENT_OBJECT_CHOICE, DOOR_CHOICE)
                        1 -> putString(K.Bundle.MEASUREMENT_OBJECT_CHOICE, WINDOW_CHOICE)
                    }

                    imageURI?.let { putString(K.Bundle.GALLERY_IMAGE_URI, it.toString()) }
                }

                findNavController().navigate(navigationResId, bundle)
            }
            onPositive()
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