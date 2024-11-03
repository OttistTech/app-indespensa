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

    suspend fun listCategories(
        pattern: String
    ) : ResultWrapper<List<String>> {
        try {
            Log.d(TAG, "[listCategories] Trying to fetch categories")
            val response = service.listCategories(pattern)
            return if(response.isSuccessful) {
                Log.d(TAG, "[listCategories] Found categories successfully")
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
                        Log.e(TAG, "[listCategories] A not mapped error occurred")
                        ResultWrapper.Error(null, "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[listCategories] Failed while fetching categories", e)
            return ResultWrapper.NetworkError
        }
    }
}