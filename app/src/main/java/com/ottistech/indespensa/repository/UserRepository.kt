package com.ottistech.indespensa.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class UserRepository {

    private val firebaseAuth = Firebase.auth

    fun isUserAuthenticated() : Boolean {
        val currentAuthenticatedUser = firebaseAuth.currentUser
        return currentAuthenticatedUser != null
    }
}