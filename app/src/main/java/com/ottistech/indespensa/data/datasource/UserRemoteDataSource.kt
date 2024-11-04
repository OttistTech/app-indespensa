package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.user.UserCreateDTO
import com.ottistech.indespensa.webclient.dto.user.UserCredentialsDTO
import com.ottistech.indespensa.webclient.dto.user.UserFullDTO
import com.ottistech.indespensa.webclient.dto.user.UserLoginDTO
import com.ottistech.indespensa.webclient.dto.user.UserUpdateDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.core.UserService
import org.json.JSONObject
import java.net.HttpURLConnection
import kotlin.Exception

class UserRemoteDataSource {

    private val TAG = "USER REMOTE DATASOURCE"
    private val service : UserService =
        RetrofitInitializer().getCoreService(UserService::class.java)

    suspend fun create(
        user: UserCreateDTO
    ) : ResultWrapper<UserCredentialsDTO> {
        return try {
            Log.d(TAG, "[create] Trying to create user with: $user")
            val response = service.create(user)
            if(response.isSuccessful) {
                Log.d(TAG, "[create] User successfully")
                ResultWrapper.Success(
                    response.body() as UserCredentialsDTO
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
                    HttpURLConnection.HTTP_CONFLICT -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[create] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        val detail = error.get(error.keys().next()).toString()
                        Log.e(TAG, "[create] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[create] Not mapped error")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }

            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun login(
        userInfo : UserLoginDTO
    ) : ResultWrapper<UserCredentialsDTO> {
        return try {
            Log.d(TAG, "[loginUser] Trying to log user in with: $userInfo")
            val response = service.getUser(userInfo)
            if (response.isSuccessful) {
                Log.d(TAG, "[loginUser] Logged in successfully")
                ResultWrapper.Success(
                    response.body() as UserCredentialsDTO
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[loginUser] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    HttpURLConnection.HTTP_UNAUTHORIZED -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[loginUser] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        val detail = error.get(error.keys().next()).toString()
                        Log.e(TAG, "[loginUser] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[loginUser] Not mapped error")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun getData(
        userId: Long,
        fullInfo: Boolean,
        token: String
    ) : ResultWrapper<UserFullDTO> {
        try {
            Log.d(TAG, "[getUserFullInfo] Fetching user info")
            val response = service.getUserFullInfo(userId, fullInfo, token)
            return if (response.isSuccessful) {
                Log.d(TAG, "[getUserFullInfo] Found successfully")
                ResultWrapper.Success(
                    response.body() as UserFullDTO
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[getUserFullInfo] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[getUserFullInfo] Not mapped error")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed", e)
            return ResultWrapper.ConnectionError
        }
    }

    suspend fun update(
        userId: Long,
        user: UserUpdateDTO,
        token: String
    ): ResultWrapper<UserCredentialsDTO> {
        return try {
            Log.d(TAG, "[updateUser] Trying to update user with: $user")
            val response = service.updateUser(userId, user, token)
            if (response.isSuccessful) {
                Log.d(TAG, "[updateUser] Updated successfully")
                ResultWrapper.Success(
                    response.body() as UserCredentialsDTO
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when (response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[updateUser] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    HttpURLConnection.HTTP_CONFLICT -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[updateUser] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        val detail = error.get(error.keys().next()).toString()
                        Log.e(TAG, "[updateUser] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[updateUser] Not mapped error")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[updateUser] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun deactivate(
        userId: Long,
        token: String
    ) : ResultWrapper<Boolean> {
        return try {
            Log.d(TAG, "[deactivateUser] Deactivating user")
            val response = service.deactivateUser(userId, token)
            return if (response.isSuccessful) {
                Log.d(TAG, "[deactivateUser] Deactivated successfully")
                ResultWrapper.Success(true)
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                return when (response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[deactivateUser] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[deactivateUser] Not mapped error")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun switchPremium(
        userId: Long,
        token: String
    ) : ResultWrapper<Boolean> {
        return try {
            Log.d(TAG, "[switchPremium] Trying to switch user premium plan")
            val response = service.switchPremium(userId, token)
            return if (response.isSuccessful) {
                Log.d(TAG, "[switchPremium] Switched successfully")
                ResultWrapper.Success(true)
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                return when (response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[switchPremium] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    HttpURLConnection.HTTP_GONE -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[switchPremium] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[switchPremium] Not mapped error")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed", e)
            ResultWrapper.ConnectionError
        }
    }
}
