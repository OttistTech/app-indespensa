package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemCreateDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemFullDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeCreateDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeFullDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.PantryService
import com.ottistech.indespensa.webclient.service.RecipeService
import org.json.JSONObject
import java.net.HttpURLConnection

class RecipeRemoteDataSource {
    private val TAG = "RECIPE REMOTE DATASOURCE"
    private val service : RecipeService =
        RetrofitInitializer().getService(RecipeService::class.java)

    suspend fun create(
        recipe: RecipeCreateDTO
    ) : ResultWrapper<RecipeFullDTO> {
        try {
            Log.d(TAG, "[create] Trying to create recipe with $recipe")
            val response = service.create(recipe)
            return if(response.isSuccessful) {
                Log.d(TAG, "[create] Recipe created successfully")
                ResultWrapper.Success(
                    response.body() as RecipeFullDTO
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
            Log.e(TAG, "[create] Failed while creating recipe", e)
            return ResultWrapper.NetworkError
        }
    }
}