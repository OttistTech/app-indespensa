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

    fun isAuthenticated() : Boolean {
        val currentAuthenticatedUser = auth.currentUser
        Log.d(TAG, "[isUserAuthenticated] Current user: $currentAuthenticatedUser")
        return currentAuthenticatedUser != null
    }

    fun save(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(executor) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Log.d(TAG, "[saveUser] Saved and authenticated: $user")
                } else {
                    Log.e(TAG, "[saveUser] Failed")
                }
            }
    }

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(executor) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Log.d(TAG, "[loginUser] Logged in: $user")
                } else {
                    Log.e(TAG, "[loginUser] Failed")
                }
            }
    }

    fun update(
        currentEmail: String,
        currentPassword: String,
        newEmail: String,
        newPassword: String
    ) {
        if(
            currentEmail != newEmail ||
            currentPassword != newPassword
        ) {
            remove(currentEmail, currentPassword)
            save(newEmail, newPassword)
        }
        Log.d(TAG, "[updateUser] Updated successfully")
    }

    fun logout() {
        Log.d(TAG, "[logoutUser] Logged out successfully")
        auth.signOut()
    }

    private fun remove(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        auth.currentUser?.let { user ->
            user.reauthenticate(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        user.delete()
                            .addOnCompleteListener { deleteTask ->
                                if (deleteTask.isSuccessful) {
                                    Log.d(TAG, "[removeUser] Removed successfully")
                                } else {
                                    Log.e(TAG, "[removeUser] Could not remove", deleteTask.exception)
                                }
                            }
                    } else {
                        Log.e(TAG, "[removeUser] Authentication before removing user failed", authTask.exception)
                    }
                }
        }
    }
}
