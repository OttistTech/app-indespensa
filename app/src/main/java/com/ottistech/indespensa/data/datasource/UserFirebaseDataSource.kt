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

    fun saveUser(email: String, password: String) {
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

    fun updateUser(email: String, password: String) {
        removeUser()
        saveUser(email, password)
        Log.d(TAG, "[updateUser] Updated user successfully")
    }

    fun logoutUser() {
        Log.d(TAG, "[logoutUser] User logout successfully")
        auth.signOut()
    }

    private fun removeUser() {
        Log.d(TAG, "[removeUser] trying to remove user")
        auth.currentUser!!.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "[removeUser] User account removed successfully")
                } else {
                    Log.e(TAG, "[removeUser] Could not remove user")
                }
            }
    }
}