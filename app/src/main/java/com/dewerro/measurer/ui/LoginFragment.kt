package com.dewerro.measurer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.R
import com.dewerro.measurer.data.auth.Auth
import com.dewerro.measurer.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
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

        // Пробуем войти в аккаунт автоматически
        tryToPerformAutomaticLogin()

        // Инициализируем виджеты фрагмента
        initLoginButton()
        initSignUpButton()
    }

    /**
     * Пробует произвести автоматический вход в аккаунт,
     * если данные для входа сохранены
     */
    private fun tryToPerformAutomaticLogin() {
        val preferences = Auth.getUserDataContainer(requireActivity())

        // получаем сохраненный email и пароль
        val prefsEmail = Auth.getSavedEmail(preferences)
        val prefsPassword = Auth.getSavedPassword(preferences)

        // если email и пароль существует, то пробуем авторизовать пользователя
        if (prefsEmail != null && prefsPassword != null) {
            loginUser(prefsEmail, prefsPassword)
        }
    }

    /**
     * Инициализирует кнопку входа. Ставит обработчик события при нажатии на кнопку
     * @see onLogin
     */
    private fun initLoginButton() {
        binding.loginButton.setOnClickListener { onLogin() }
    }

    /**
     * Получает данные для входа из соответствующих виджетов во фрагменте.
     * Пытается произвести вход с веденными данными.
     * @see loginUser
     */
    private fun onLogin() {
        val email = binding.emailTextField.text.toString()
        val password = binding.passwordTextField.text.toString()

        loginUser(email, password)
    }

    /**
     * Инициализирует кнопку для регистрации. Ставит обработчик события при нажатии на кнопку.
     * @see onSignUp
     */
    private fun initSignUpButton() {
        binding.signUpButton.setOnClickListener { onSignUp() }
    }

    /**
     * При вызове этого метода открывается фрагмент регистрации
     * @see SignUpFragment
     */
    private fun onSignUp() {
        findNavController().navigate(R.id.action_LoginFragment_to_SignupFragment)
    }

    /**
     * Логинит пользователя по электронной почте и паролю.
     * Если пользователь уже логинится, отображает ошибку.
     * Если пользователь ввёл некорректные данные, отображает ошибку.
     * Если авторизация успешна, переходит в основной фрагмент.
     * @see SelectImageFragment
     * @param email электронная почта пользователя
     * @param password пароль, который ввел пользователь
     */
    private fun loginUser(email: String, password: String) {
        val task = try {
            // Пробуем залогинить пользователя
            Auth.login(email, password)
        } catch (e: IllegalArgumentException) {
            // Пользователь ввёл некорректные данные, отображаем ошибку.
            Snackbar.make(binding.root, R.string.empty_fields, Snackbar.LENGTH_LONG).show()
            return
        } catch (e: IllegalStateException) {
            // Пользователь уже логинится, поэтому тоже отображаем ошибку.
            Snackbar.make(binding.root, R.string.already_logging_in, Snackbar.LENGTH_LONG).show()
            return
        }

        // Если условия выше выполнились, добавляем задачи при завершении авторизации
        task.apply {
            // Этот блок кода выполнится, если авторизация успешна
            onComplete {
                // Сохраняем данные пользователя в локальное хранилище
                Auth.saveCredentials(requireActivity(), email, password)

                // Переходим в основной фрагмент
                findNavController().navigate(R.id.action_LoginFragment_to_SelectImageFragment)
            }
            // Этот блок кода выполнится, если при авторизации произошла ошибка
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