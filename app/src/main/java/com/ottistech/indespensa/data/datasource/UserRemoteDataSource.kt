package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.UserCreateDTO
import com.ottistech.indespensa.webclient.dto.UserCredentialsDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.UserService
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection

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
                        ResultWrapper.Error(null, "Unexpected Error")
                    }
                }

            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed while creating user", e)
            return ResultWrapper.NetworkError
        }
    }
}