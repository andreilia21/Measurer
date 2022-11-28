package com.dewerro.measurer.auth

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.dewerro.measurer.K
import com.dewerro.measurer.util.async.PendingTask

/**
 * Глобальный доступ к единой реализации AuthService
 */
object Auth {

    private var authService: AuthService? = null
    private var isLoggingIn: Boolean = false
    private var isRegistering: Boolean = false

    /**
     * Устанавливает текущую реализацию AuthService
     * @see AuthService
     */
    fun setAuthService(authService: AuthService) {
        if (this.authService != null) {
            throw IllegalStateException("Authentication cannot be initialized twice")
        }

        this.authService = authService
    }

    /**
     * Производит вход в аккаунт по указанным данным
     * @param email электронная почта пользователя
     * @param password пароль пользователя
     */
    fun login(email: String, password: String): PendingTask<Unit> {
        if (isLoggingIn) throw IllegalStateException("Already logging in")

        isLoggingIn = true

        return authService!!.login(email, password).onAny { isLoggingIn = false }
    }

    /**
     * Регистрирует пользователя по указанным данным.
     * @param email электронная почта пользователя
     * @param password пароль пользователя
     */
    fun register(email: String, password: String): PendingTask<Unit> {
        if (isRegistering) throw IllegalStateException("Already registering")

        isRegistering = true

        return authService!!.register(email, password).onAny { isRegistering = false }
    }

    /**
     * Производит выход из аккаунта
     */
    fun logout(): PendingTask<Unit> {
        return authService!!.logout()
    }

    /**
     * Получает контейнер данных пользователя.
     * @param activity активити фрагмента
     */
    fun getUserDataContainer(activity: Activity): SharedPreferences {
        return activity
            .getSharedPreferences(K.SharedPreferences.FIREBASE_USER_DATA, Context.MODE_PRIVATE)
    }

    /**
     * Сохраняет почту и пароль в локальное хранилище
     * @param activity активити фрагмента, используется для получения SharedPreferences
     * @param email электронная почта
     * @param password пароль
     */
    fun saveCredentials(activity: Activity, email: String, password: String) {
        val preferences = getUserDataContainer(activity)
        preferences.edit {
            putString("email", email)
            putString("password", password)
        }
    }

    /**
     * Очищает сохраненные данные для входа из локального хранилища
     * @param activity активити фрагмента, используется для получения SharedPreferences
     */
    fun clearCredentials(activity: Activity) {
        val preferences = getUserDataContainer(activity)
        preferences.edit {
            remove(K.SharedPreferences.FIREBASE_EMAIL)
            remove(K.SharedPreferences.FIREBASE_PASSWORD)
        }
    }

    /**
     * Получает сохраненный пароль из локального хранилища
     * @param activity активити фрагмента, используется для получения SharedPreferences
     */
    fun getSavedPassword(activity: Activity): String? {
        return getSavedPassword(getUserDataContainer(activity))
    }

    /**
     * Получает сохраненный пароль из локального хранилища
     * @param sharedPreferences контейнер данных
     */
    fun getSavedPassword(sharedPreferences: SharedPreferences): String? {
        return sharedPreferences.getString(K.SharedPreferences.FIREBASE_PASSWORD, null)
    }

    /**
     * Получает сохраненную электронную почту из локального хранилища
     * @param activity активити фрагмента, используется для получения SharedPreferences
     */
    fun getSavedEmail(activity: Activity): String? {
        return getSavedEmail(getUserDataContainer(activity))
    }

    /**
     * Получает сохраненную электронную почту из локального хранилища
     * @param sharedPreferences контейнер данных
     */
    fun getSavedEmail(sharedPreferences: SharedPreferences): String? {
        return sharedPreferences.getString(K.SharedPreferences.FIREBASE_EMAIL, null)
    }

}