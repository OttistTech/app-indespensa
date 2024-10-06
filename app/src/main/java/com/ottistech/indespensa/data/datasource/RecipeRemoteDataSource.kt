package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.recipe.RateRecipeRequestDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeDetailsDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.RecipeService
import org.json.JSONObject
import java.net.HttpURLConnection

class RecipeRemoteDataSource {

    private val TAG = "RECIPE REMOTE DATASOURCE"
    private val service : RecipeService =
        RetrofitInitializer().getService(RecipeService::class.java)

    suspend fun getRecipeDetails(recipeId: Long, userId: Long) : ResultWrapper<RecipeDetailsDTO> {
        try {
            val response = service.getRecipeDetails(recipeId, userId)

            return if(response.isSuccessful) {
                ResultWrapper.Success(
                    response.body() as RecipeDetailsDTO
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())

                when(response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        ResultWrapper.Error(response.code(), detail)
                    }
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        val detail = error.get(error.keys().next()).toString()
                        Log.e(TAG, "[deactivateUser] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        ResultWrapper.Error(null, "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[getRecipeDetails] DEU RED", e)
            return ResultWrapper.NetworkError
        }
    }

    suspend fun rateRecipe(recipeId: Long, rateRecipeRequestDTO: RateRecipeRequestDTO): ResultWrapper<Any> {
        return try {
            val response = service.rateRecipe(recipeId, rateRecipeRequestDTO)

            return if (response.isSuccessful) {
                ResultWrapper.Success("Recipe rated successfully")
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                Log.d(TAG, response.code().toString())

                return when (response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        ResultWrapper.Error(response.code(), detail)
                    }
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        val detail = error.get(error.keys().next()).toString()
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            ResultWrapper.NetworkError
        }
    }
}