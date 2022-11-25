package com.dewerro.measurer.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.K
import com.dewerro.measurer.R
import com.dewerro.measurer.databinding.FragmentSelectImageBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SelectImageFragment : Fragment() {

    private var _binding: FragmentSelectImageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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

        binding.measureWithArButton.setOnClickListener {
            findNavController().navigate(R.id.action_SelectImageFragment_to_ARFragment)
        }

        binding.measureWithImage.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.logoutButton.setOnClickListener {
            Firebase.auth.signOut()

            val preferences = activity?.getSharedPreferences(K.SharedPreferences.FIREBASE_USER_DATA, Context.MODE_PRIVATE)
            preferences?.edit {
                remove(K.SharedPreferences.FIREBASE_EMAIL)
                remove(K.SharedPreferences.FIREBASE_PASSWORD)
            }

            findNavController().navigate(R.id.action_SelectImageFragment_to_LoginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}