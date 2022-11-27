package com.dewerro.measurer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.R
import com.dewerro.measurer.auth.Auth
import com.dewerro.measurer.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar

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

        tryToPerformAutomaticLogin()

        initLoginButton()
        initSignUpButton()
    }

    private fun tryToPerformAutomaticLogin() {
        val preferences = Auth.getUserDataContainer(activity!!)

        // получаем сохранненый email и пароль
        val prefsEmail = Auth.getSavedEmail(preferences)
        val prefsPassword = Auth.getSavedPassword(preferences)

        // если email и пароль существует, то пробуем авторизовать пользователя
        if (prefsEmail != null && prefsPassword != null) {
            loginUser(prefsEmail, prefsPassword)
        }
    }

    private fun initLoginButton() {
        binding.loginButton.setOnClickListener { onLogin() }
    }

    private fun onLogin() {
        val email = binding.emailTextField.text.toString()
        val password = binding.passwordTextField.text.toString()

        loginUser(email, password)
    }

    private fun initSignUpButton() {
        binding.signUpButton.setOnClickListener { onSignUp() }
    }

    private fun onSignUp() {
        findNavController().navigate(R.id.action_LoginFragment_to_SignupFragment)
    }

    /**
     * Логинит пользователя по электронной почте и паролю
     * @param email электронная почта пользователя
     * @param password пароль, который ввел пользователь
    **/
    private fun loginUser(email: String, password: String) {
        val task = try {
            Auth.login(email, password)
        } catch (e: IllegalArgumentException) {
            Snackbar.make(binding.root, R.string.empty_fields, Snackbar.LENGTH_LONG).show()
            return
        } catch (e: IllegalStateException) {
            Snackbar.make(binding.root, R.string.already_logging_in, Snackbar.LENGTH_LONG).show()
            return
        }

        task.apply {
            onComplete {
                Auth.saveCredentials(requireActivity(), email, password)
                findNavController().navigate(R.id.action_LoginFragment_to_SelectImageFragment)
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