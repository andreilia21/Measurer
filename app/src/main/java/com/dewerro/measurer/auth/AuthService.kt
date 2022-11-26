package com.dewerro.measurer.auth

import com.dewerro.measurer.util.async.PendingTask

interface AuthService {

    fun login(email: String, password: String): PendingTask<Unit>

    fun register(email: String, password: String): PendingTask<Unit>

    fun logout(): PendingTask<Unit>

}