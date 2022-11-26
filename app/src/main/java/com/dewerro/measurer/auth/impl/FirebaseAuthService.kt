package com.dewerro.measurer.auth.impl

import android.util.Log
import com.dewerro.measurer.auth.AuthService
import com.dewerro.measurer.util.async.PendingTask
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FirebaseAuthService : AuthService {

    override fun login(email: String, password: String): PendingTask<Unit> {
        val task = PendingTask<Unit>()

        Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful) {
                Log.i("Firebase", "Sign in successfully.")
                task.setCompleted(Unit)
            } else {
                Log.e("Firebase", "Error signing in.", it.exception)
                task.setFailure(it.exception)
            }
        }

        return task
    }

    override fun register(email: String, password: String): PendingTask<Unit> {
        val task = PendingTask<Unit>()

        Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful) {
                Log.i("Firebase", "User created successfully.")
                task.setCompleted(Unit)
            } else {
                Log.e("Firebase", "Error creating user.", it.exception)
                task.setFailure(it.exception)
            }
        }

        return task
    }

    override fun logout(): PendingTask<Unit> {
        val task = PendingTask<Unit>()

        try {
            Firebase.auth.signOut()
        } catch (e: Throwable) {
            task.setFailure(e)
            return task
        }

        task.setCompleted(Unit)

        return task
    }

}