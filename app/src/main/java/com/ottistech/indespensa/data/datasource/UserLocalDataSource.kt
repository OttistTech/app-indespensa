package com.ottistech.indespensa.data.datasource

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.ottistech.indespensa.webclient.dto.user.UserCredentialsDTO

class UserLocalDataSource(
    context: Context
) {

    companion object {
        private const val TAG = "USER LOCAL DATASOURCE"
        private const val USER_PREFERENCES_FILE : String = "user-preferences"
        private const val USER_PREFERENCES_KEY : String = "user-data"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        USER_PREFERENCES_FILE,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun save(user: UserCredentialsDTO) {
        Log.d(TAG, "[saveUser] Saving user credentials and token: $user")
        val currentUser = get()
        if (currentUser?.token != null) {
            user.token = currentUser.token
        }

        val gson = Gson()
        val userJson = gson.toJson(user)

        sharedPreferences.edit()
            .putString(USER_PREFERENCES_KEY, userJson)
            .apply()
    }

    fun get(): UserCredentialsDTO? {
        val userJson = sharedPreferences.getString(USER_PREFERENCES_KEY, null)
        return if (userJson != null) {
            val user = Gson().fromJson(userJson, UserCredentialsDTO::class.java)
            Log.d(TAG, "[getUser] Reading user credentials: $user")
            user
        } else {
            null
        }
    }

    fun clear() {
        Log.d(TAG, "[removeUser] Removing user credentials")
        sharedPreferences.edit().remove(USER_PREFERENCES_KEY).apply()
    }
}