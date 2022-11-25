package com.dewerro.measurer.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.R
import com.dewerro.measurer.databinding.FragmentSignUpBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            val email = binding.emailTextField.text.toString()
            val password = binding.passwordTextField.text.toString()

            createUser(email, password)
        }
    }

    // переменная, чтобы нельзя было несколько раз создать пользователя
    private var isCreatingUser = false

    /**
     * Создание пользователя по электронной почте и паролю
     * @param email электронная почта
     * @param password пароль
     */
    private fun createUser(email: String, password: String) {
        isCreatingUser = true

        val task = try {
            Firebase.auth.createUserWithEmailAndPassword(email, password)
        } catch (e: IllegalArgumentException) {
            Snackbar.make(binding.root, R.string.empty_fields, Snackbar.LENGTH_LONG).show()
            e.printStackTrace()
            isCreatingUser = false

            return
        }
        // если пользователь успешно создан, то переходим в SelectImageFragment
        task.addOnCompleteListener {
            if (it.isSuccessful) {
                findNavController().navigate(R.id.action_SignupFragment_to_SelectImageFragment)

                Log.i("Firebase", "User created successfully.")

                saveData(email, password)
            } else {
                Snackbar.make(binding.root, it.exception!!.localizedMessage!!, Snackbar.LENGTH_LONG).show()

                Log.e("Firebase", "Error creating user.", it.exception)
            }
            isCreatingUser = false
        }
    }

    /**
     * Сохраняет почту и пароль в локальное хранилище
     * @param email электронная почта
     * @param password пароль
     */
    private fun saveData(email: String, password: String) {
        val preferences = activity?.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        preferences?.edit {
            putString("email", email)
            putString("password", password)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}