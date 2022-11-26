package com.dewerro.measurer.auth

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.dewerro.measurer.K
import com.dewerro.measurer.util.async.PendingTask

object Auth {

    private var authService: AuthService? = null
    private var isLoggingIn: Boolean = false
    private var isRegistering: Boolean = false

    fun setAuthService(authService: AuthService) {
        if(this.authService != null) {
            throw IllegalStateException("Authentication cannot be initialized twice")
        }

        this.authService = authService
    }

    fun login(email: String, password: String): PendingTask<Unit> {
        if(isLoggingIn) throw IllegalStateException("Already logging in")

        isLoggingIn = true

        return authService!!.login(email, password).onAny { isLoggingIn = false }
    }

    fun register(email: String, password: String): PendingTask<Unit> {
        if(isRegistering) throw IllegalStateException("Already registering")

        isRegistering = true

        return authService!!.register(email, password).onAny { isRegistering = false }
    }

    fun logout(): PendingTask<Unit> {
        return authService!!.logout()
    }

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

    fun clearCredentials(activity: Activity) {
        val preferences = getUserDataContainer(activity)
        preferences.edit {
            remove(K.SharedPreferences.FIREBASE_EMAIL)
            remove(K.SharedPreferences.FIREBASE_PASSWORD)
        }
    }

    fun getSavedPassword(activity: Activity): String? {
        return getSavedPassword(getUserDataContainer(activity))
    }

    fun getSavedPassword(sharedPreferences: SharedPreferences): String? {
        return sharedPreferences.getString(K.SharedPreferences.FIREBASE_PASSWORD, null)
    }

    fun getSavedEmail(activity: Activity): String? {
        return getSavedEmail(getUserDataContainer(activity))
    }

    fun getSavedEmail(sharedPreferences: SharedPreferences): String? {
        return sharedPreferences.getString(K.SharedPreferences.FIREBASE_EMAIL, null)
    }

}