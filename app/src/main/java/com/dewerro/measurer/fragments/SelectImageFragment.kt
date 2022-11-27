package com.dewerro.measurer.fragments

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

            findNavController().navigate(R.id.action_SelectImageFragment_to_ImageFragment, bundle)
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
            findNavController().navigate(R.id.action_SelectImageFragment_to_ARFragment)
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
            Auth.clearCredentials(activity!!)

            findNavController().navigate(R.id.action_SelectImageFragment_to_LoginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}