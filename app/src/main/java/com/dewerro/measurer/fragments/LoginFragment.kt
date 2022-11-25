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
import com.dewerro.measurer.K
import com.dewerro.measurer.R
import com.dewerro.measurer.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferences =
            activity?.getSharedPreferences(K.SharedPreferences.FIREBASE_USER_DATA, Context.MODE_PRIVATE)
        // получаем email и пароль, сохраненный
        val prefsEmail = preferences?.getString(K.SharedPreferences.FIREBASE_EMAIL, null)
        val prefsPassword = preferences?.getString(K.SharedPreferences.FIREBASE_PASSWORD, null)

        // если email и пароль существует, то сразу запускаем LoginFragment, иначе вешаем listener на кнопку
        if (prefsEmail != null && prefsPassword != null) {
            loginUser(prefsEmail, prefsPassword)
        } else {
            binding.loginButton.setOnClickListener {
                findNavController().navigate(R.id.action_LoginFragment_to_SelectImageFragment)
            }
        }

        binding.signUpButton.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_SignupFragment)
        }
    }
    // переменная, чтобы нельзя было несколько раз залогиниться
    private var isSigningIn = false

    /**
     * Логинит пользователя по электронной почте и паролю
     * @param email электронная почта пользователя
     * @param password пароль, который ввел пользователь
    **/
    private fun loginUser(email: String, password: String) {
        isSigningIn = true

        val task = try {
            Firebase.auth.signInWithEmailAndPassword(email, password)
        } catch (e: IllegalArgumentException) {
            // если поля пустые, то срабатывает этот блок
            Snackbar.make(binding.root, R.string.empty_fields, Snackbar.LENGTH_LONG).show()
            e.printStackTrace()
            isSigningIn = false

            return
        }

        task.addOnCompleteListener {
            // если успешно залогинились, отправляемся в SelectImageFragment
            if (it.isSuccessful) {
                findNavController().navigate(R.id.action_LoginFragment_to_SelectImageFragment)

                Log.i("Firebase", "Sign in successfully.")

                saveData(email, password)
            } else {
                Snackbar.make(binding.root, it.exception!!.localizedMessage!!, Snackbar.LENGTH_LONG)
                    .show()

                Log.e("Firebase", "Error signing in.", it.exception)
            }
            isSigningIn = false
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