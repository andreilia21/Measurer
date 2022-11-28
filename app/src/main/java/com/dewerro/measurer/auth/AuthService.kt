package com.dewerro.measurer.auth

import com.dewerro.measurer.util.async.PendingTask

/**
 * Интерфейс, который позволяет произвести авторизацию/регистрацию пользователя.
 * Абстрагирует логику взаимодействия с внешними библиотеками, делая архитектуру в разы гибче.
 */
interface AuthService {

    fun login(email: String, password: String): PendingTask<Unit>

    fun register(email: String, password: String): PendingTask<Unit>

    fun logout(): PendingTask<Unit>

}