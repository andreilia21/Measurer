package com.dewerro.measurer

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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

        val preferences = activity?.getSharedPreferences("user_data", Context.MODE_PRIVATE)

        val prefsEmail = preferences?.getString("email", "")
        val prefsPassword = preferences?.getString("password", "")

        if (prefsEmail.isNullOrEmpty() && prefsPassword.isNullOrEmpty()) {
            binding.loginButton.setOnClickListener {
                val email = binding.emailTextField.text.toString()
                val password = binding.passwordTextField.text.toString()

                loginUser(email, password)
            }
        } else {
            loginUser(prefsEmail!!, prefsPassword!!)
        }

        binding.signUpButton.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_SignupFragment)
        }
    }

    private fun loginUser(email: String, password: String) {
        val task = try {
            Firebase.auth.signInWithEmailAndPassword(email, password)
        } catch (e: IllegalArgumentException) {
            Snackbar.make(binding.root, R.string.empty_fields, Snackbar.LENGTH_LONG).show()

            e.printStackTrace()

            return
        }

        task.addOnCompleteListener {
            if (it.isSuccessful) {
                findNavController().navigate(R.id.action_LoginFragment_to_SelectImageFragment)

                Log.i("Firebase", "Sign in successfully.")

                saveData(email, password)
            } else {
                Snackbar.make(binding.root, it.exception!!.localizedMessage!!, Snackbar.LENGTH_LONG).show()

                Log.e("Firebase", "Error signing in.", it.exception)
            }
        }
    }

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