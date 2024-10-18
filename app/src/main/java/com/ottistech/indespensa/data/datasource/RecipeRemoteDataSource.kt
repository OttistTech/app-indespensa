package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.shared.IngredientState
import com.ottistech.indespensa.shared.RecipeLevel
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.Pageable

import com.ottistech.indespensa.webclient.dto.recipe.RateRecipeRequestDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.dto.recipe.RecipeCreateDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeFullDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipePartialDTO
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

    suspend fun getRecipeDetails(recipeId: Long, userId: Long) : ResultWrapper<RecipeFullDTO> {
        try {
            val response = service.getRecipeDetails(recipeId, userId)

            return if(response.isSuccessful) {
                ResultWrapper.Success(
                    response.body() as RecipeFullDTO
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

    suspend fun list(
        queryText: String?,
        userId: Long,
        pageNumber: Int,
        level: RecipeLevel?,
        availability: IngredientState?,
        minPreparationTime: Int?,
        maxPreparationTime: Int?
    ) : ResultWrapper<Pageable<List<RecipePartialDTO>>?> {
        return try {
            val response = service.list(
                queryText=queryText,
                userId=userId,
                pageNumber=pageNumber,
                pageSize=10,
                level=level,
                availability=availability,
                minPreparationTime=minPreparationTime,
                maxPreparationTime=maxPreparationTime
            )

            return if (response.isSuccessful) {
                ResultWrapper.Success(response.body())
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                Log.d(TAG, response.code().toString())

                return when (response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
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

    suspend fun list(
        userId: Long,
        pageNumber: Int
    ) : ResultWrapper<Pageable<List<RecipePartialDTO>>?> {
        return try {
            val response = service.list(
                userId=userId,
                pageNumber=pageNumber,
                pageSize=10,
                createdByYou=true
            )

            return if (response.isSuccessful) {
                ResultWrapper.Success(response.body())
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                Log.d(TAG, response.code().toString())

                return when (response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
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
