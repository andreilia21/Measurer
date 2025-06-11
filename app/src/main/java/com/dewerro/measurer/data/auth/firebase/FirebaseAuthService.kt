package com.dewerro.measurer.data.auth.firebase

import androidx.lifecycle.LiveData
import com.dewerro.measurer.data.auth.AuthService
import com.dewerro.measurer.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseAuthService : AuthService {

    private class AuthStatusLiveData : LiveData<User?>(), FirebaseAuth.AuthStateListener {
        override fun onActive() {
            Firebase.auth.addAuthStateListener(this)
        }

        override fun onInactive() {
            Firebase.auth.removeAuthStateListener(this)
        }

        override fun onAuthStateChanged(auth: FirebaseAuth) {
            val user = auth.currentUser?.let { User(it.uid) }
            postValue(user)
        }
    }

    override val authStatus: LiveData<User?>
        get() = AuthStatusLiveData()

    override suspend fun login(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun register(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
    }

    override suspend fun logout() {
        Firebase.auth.signOut()
    }
}