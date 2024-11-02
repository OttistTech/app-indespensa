package com.ottistech.indespensa.data.datasource

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.Executor

class UserFirebaseDataSource private constructor(
    context: Context
) {

    private val TAG = "USER FIREBASE DATASOURCE"
    private val executor: Executor = ContextCompat.getMainExecutor(context)

    companion object {
        @Volatile
        private var instance: UserFirebaseDataSource? = null

        fun getInstance(context: Context): UserFirebaseDataSource {
            return instance ?: synchronized(this) {
                instance ?: UserFirebaseDataSource(context).also { instance = it }
            }
        }

        val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    }

    fun isUserAuthenticated() : Boolean {
        val currentAuthenticatedUser = auth.currentUser
        Log.d(TAG, "[isUserAuthenticated] Current user: $currentAuthenticatedUser")
        return currentAuthenticatedUser != null
    }

    fun saveUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(executor) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Log.d(TAG, "[saveUser] Saved and authenticated user: $user")
                } else {
                    Log.e(TAG, "[saveUser] Failed while saving the user")
                }
            }
    }

    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(executor) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Log.d(TAG, "[loginUser] User logged in: $user")
                } else {
                    Log.e(TAG, "[loginUser] Failed while authenticating the user")
                }
            }
    }

    fun updateUser(
        currentEmail: String,
        currentPassword: String,
        newEmail: String,
        newPassword: String
    ) {
        if(
            currentEmail != newEmail ||
            currentPassword != newPassword
        ) {
            removeUser(currentEmail, currentPassword)
            saveUser(newEmail, newPassword)
        }
        Log.d(TAG, "[updateUser] Updated user successfully")
    }

    fun logoutUser() {
        Log.d(TAG, "[logoutUser] User logged out successfully")
        auth.signOut()
    }

    private fun removeUser(email: String, password: String) {
        Log.d(TAG, "[removeUser] trying to remove user with email $email")
        val credential = EmailAuthProvider.getCredential(email, password)
        auth.currentUser?.let { user ->
            user.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        user.delete()
                            .addOnCompleteListener { deleteTask ->
                                if (deleteTask.isSuccessful) {
                                    Log.d(TAG, "[removeUser] User account removed successfully")
                                } else {
                                    Log.e(TAG, "[removeUser] Could not remove user", deleteTask.exception)
                                }
                            }
                    } else {
                        Log.e(TAG, "[removeUser] Reauthentication failed", reauthTask.exception)
                    }
                }
        }
    }
}
