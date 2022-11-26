package com.dewerro.measurer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.R
import com.dewerro.measurer.auth.Auth
import com.dewerro.measurer.databinding.FragmentSignUpBinding
import com.google.android.material.snackbar.Snackbar

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
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

        binding.createAccountButton.setOnClickListener { onAccountCreate() }
    }

    private fun onAccountCreate() {
        val email = binding.emailTextField.text.toString()
        val password = binding.passwordTextField.text.toString()

        createUser(email, password)
    }

    /**
     * Создание пользователя по электронной почте и паролю
     * @param email электронная почта
     * @param password пароль
     */
    private fun createUser(email: String, password: String) {
        val task = try {
            Auth.register(email, password)
        } catch (e: IllegalArgumentException) {
            Snackbar.make(binding.root, R.string.empty_fields, Snackbar.LENGTH_LONG).show()
            return
        } catch (e: IllegalStateException) {
            Snackbar.make(binding.root, R.string.already_registering, Snackbar.LENGTH_LONG).show()
            return
        }

        task.apply {
            onComplete {
                Auth.saveCredentials(activity!!, email, password)
                findNavController().navigate(R.id.action_SignupFragment_to_SelectImageFragment)
            }
            onError {
                Snackbar.make(binding.root, it?.localizedMessage!!, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}