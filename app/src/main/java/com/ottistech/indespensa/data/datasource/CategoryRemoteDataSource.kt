package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.core.CategoryService

class CategoryRemoteDataSource {

    private val TAG = "CATEGORY REMOTE DATASOURCE"
    private val service : CategoryService =
        RetrofitInitializer().getCoreService(CategoryService::class.java)

    suspend fun list(
        pattern: String,
        token: String
    ) : ResultWrapper<List<String>> {
        return try {
            Log.d(TAG, "[list] Fetching categories")
            val response = service.list(pattern, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[list] Found successfully")
                ResultWrapper.Success(
                    response.body() as List<String>
                )
            } else {
                Log.e(TAG, "[list] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[list] Failed", e)
            ResultWrapper.ConnectionError
        }
    }
}