package com.dewerro.measurer.data.auth

import androidx.lifecycle.LiveData
import com.dewerro.measurer.model.User

interface AuthService {

    val authStatus: LiveData<User?>

    suspend fun login(email: String, password: String)

    suspend fun register(email: String, password: String)

    suspend fun logout()

}