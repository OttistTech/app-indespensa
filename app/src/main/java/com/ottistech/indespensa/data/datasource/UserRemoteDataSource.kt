package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.UserCreateDTO
import com.ottistech.indespensa.webclient.dto.UserCredentialsDTO
import com.ottistech.indespensa.webclient.dto.UserFullCredentialsDTO
import com.ottistech.indespensa.webclient.dto.UserLoginDTO
import com.ottistech.indespensa.webclient.dto.UserUpdateDTO
import com.ottistech.indespensa.webclient.dto.UserUpdateResponseDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.UserService
import org.json.JSONObject
import java.net.HttpURLConnection
import kotlin.Exception

class UserRemoteDataSource {

    private val TAG = "USER REMOTE DATASOURCE"
    private val service : UserService =
        RetrofitInitializer().getService(UserService::class.java)

    suspend fun create(user: UserCreateDTO) : ResultWrapper<UserCredentialsDTO> {
        try {
            val response = service.create(user)
            return if(response.isSuccessful) {
                Log.d(TAG, "[create] User created successfully")
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
                        Log.e(TAG, "[create] A not mapped error occurred")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }

            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed while creating user", e)
            return ResultWrapper.NetworkError
        }
    }

    suspend fun loginUser(userInfo : UserLoginDTO) : ResultWrapper<UserCredentialsDTO> {
        try {
            val response = service.getUser(userInfo)

            return if (response.isSuccessful) {
                Log.d(TAG, "[loginUser] User logged successfully")
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
                        val detail =  error.get("detail").toString()
                        Log.e(TAG, "[loginUser] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        val detail = error.get(error.keys().next()).toString()
                        Log.e(TAG, "[loginUser] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[loginUser] A not mapped error occurred")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed while logging user", e)
            return ResultWrapper.NetworkError
        }
    }

    suspend fun getUserFullInfo(userId: Long, fullInfo: Boolean) : ResultWrapper<UserFullCredentialsDTO> {
        try {
            val response = service.getUserFullInfo(userId, fullInfo)

            return if (response.isSuccessful) {
                Log.d(TAG, "[getUserFullInfo] User logged successfully")
                ResultWrapper.Success(
                    response.body() as UserFullCredentialsDTO
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())

                when(response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[getUserFullInfo] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    HttpURLConnection.HTTP_UNAUTHORIZED -> {
                        val detail =  error.get("detail").toString()
                        Log.e(TAG, "[getUserFullInfo] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        val detail = error.get(error.keys().next()).toString()
                        Log.e(TAG, "[getUserFullInfo] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[getUserFullInfo] A not mapped error occurred")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed while fetching user info", e)
            return ResultWrapper.NetworkError
        }
    }

    suspend fun updateUser(userId: Long, updateUserDTO: UserUpdateDTO): ResultWrapper<UserUpdateResponseDTO> {
        return try {
            val response = service.updateUser(userId, updateUserDTO)

            if (response.isSuccessful) {
                Log.d(TAG, "[updateUser] User updated successfully")
                ResultWrapper.Success(response.body() as UserUpdateResponseDTO)
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
                        Log.e(TAG, "[updateUser] A not mapped error occurred")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to update user", e)
            ResultWrapper.NetworkError
        }
    }

    suspend fun deactivateUser(userId: Long) : ResultWrapper<Any> {
        return try {
            val response = service.deactivateUser(userId)

            if (response.isSuccessful) {
                Log.d(TAG, "[deactivateUser] User deactivated successfully")
                ResultWrapper.Success("User deactivated successfully")
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                Log.d(TAG, response.code().toString())

                when (response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[deactivateUser] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    HttpURLConnection.HTTP_CONFLICT -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[deactivateUser] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        val detail = error.get(error.keys().next()).toString()
                        Log.e(TAG, "[deactivateUser] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[deactivateUser] A not mapped error occurred")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to deactivate user", e)
            ResultWrapper.NetworkError
        }
    }

}