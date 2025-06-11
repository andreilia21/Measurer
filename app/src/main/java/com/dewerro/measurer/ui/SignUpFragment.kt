package com.dewerro.measurer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dewerro.measurer.R
import com.dewerro.measurer.data.auth.Auth
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

        // Инициализируем кнопку создания аккаунта
        initCreateAccountButton()
    }

    /**
     * Инициализирует кнопку создания аккаунта. Ставит обработчик события при нажатии кнопки.
     * @see onAccountCreate
     */
    private fun initCreateAccountButton() {
        binding.createAccountButton.setOnClickListener { onAccountCreate() }
    }

    /**
     * Получает данные для регистрации из соответствующих текстовых виджетов.
     * Пытается зарегистрировать пользователя.
     * @see createUser
     */
    private fun onAccountCreate() {
        val email = binding.emailTextField.text.toString()
        val password = binding.passwordTextField.text.toString()

        createUser(email, password)
    }

    /**
     * Создание пользователя по электронной почте и паролю.
     * Если пользователь уже регистрируется, отображает ошибку.
     * Если пользователь ввёл некорректные данные, отображает ошибку.
     * Если регистрация успешна, переходит в основной фрагмент.
     * @see SelectImageFragment
     * @param email электронная почта
     * @param password пароль
     */
    private fun createUser(email: String, password: String) {
        val task = try {
            // Пытается зарегистрировать пользователя
            Auth.register(email, password)
        } catch (e: IllegalArgumentException) {
            // Пользователь ввёл некорректные данные, отображаем ошибку.
            Snackbar.make(binding.root, R.string.empty_fields, Snackbar.LENGTH_LONG).show()
            return
        } catch (e: IllegalStateException) {
            // Пользователь уже регистрируется, отображаем ошибку.
            Snackbar.make(binding.root, R.string.already_registering, Snackbar.LENGTH_LONG).show()
            return
        }

        // Если условия выше выполнились, добавляем задачи при завершении регистрации
        task.apply {
            // Этот блок кода выполнится, если регистрация успешна
            onComplete {
                // Сохраняем данные пользователя
                Auth.saveCredentials(requireActivity(), email, password)

                // Переходим в основной фрагмент
                findNavController().navigate(R.id.action_SignupFragment_to_SelectImageFragment)
            }
            // Этот блок кода выполнится, если при регистрации произошла ошибка
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