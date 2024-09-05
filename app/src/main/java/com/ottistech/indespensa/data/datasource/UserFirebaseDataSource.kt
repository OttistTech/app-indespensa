package com.ottistech.indespensa.data.datasource

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.util.concurrent.Executor

class UserFirebaseDataSource(
    context: Context
) {

    private val TAG = "USER FIREBASE DATASOURCE"
    private val auth = Firebase.auth
    private val executor: Executor = ContextCompat.getMainExecutor(context)

    fun isUserAuthenticated() : Boolean {
        val currentAuthenticatedUser = auth.currentUser
        Log.d(TAG, "[isUserAuthenticated] User authenticated: $currentAuthenticatedUser")
        return currentAuthenticatedUser != null
    }

    fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(executor) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Log.d(TAG, "[createUser] User authenticated: $user")
                } else {
                    Log.e(TAG, "[createUser] Failed while authenticating the user")
                }
            }
    }

    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(executor) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Log.d(TAG, "[loginUser] User logged: $user")
                } else {
                    Log.e(TAG, "[loginUser] Failed while authenticating the user")
                }
            }
    }

    // TODO: implement updateUser on Firebase

    fun logoutUser() {
        auth.signOut()
        Log.d(TAG, "[logoutUser] User logout successfully")
    }

}