package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.core.CategoryService
import org.json.JSONObject
import java.net.HttpURLConnection

class CategoryRemoteDataSource {

    private val TAG = "CATEGORY REMOTE DATASOURCE"
    private val service : CategoryService =
        RetrofitInitializer().getCoreService(CategoryService::class.java)

    suspend fun list(
        pattern: String,
        token: String
    ) : ResultWrapper<List<String>> {
        return try {
            Log.d(TAG, "[listCategories] Fetching categories")
            val response = service.listCategories(pattern, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[listCategories] Found successfully")
                ResultWrapper.Success(
                    response.body() as List<String>
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[listCategories] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[listCategories] Not mapped error")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[listCategories] Failed", e)
            ResultWrapper.ConnectionError
        }
    }
}