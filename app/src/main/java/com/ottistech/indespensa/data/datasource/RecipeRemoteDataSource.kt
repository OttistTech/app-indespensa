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
import com.ottistech.indespensa.webclient.service.core.RecipeService
import org.json.JSONObject
import java.net.HttpURLConnection

class RecipeRemoteDataSource {

    private val TAG = "RECIPE REMOTE DATASOURCE"
    private val service : RecipeService =
        RetrofitInitializer().getCoreService(RecipeService::class.java)

    suspend fun create(
        recipe: RecipeCreateDTO,
        token: String
    ) : ResultWrapper<Boolean> {
        return try {
            Log.d(TAG, "[create] Creating recipe with: $recipe")
            val response = service.create(recipe, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[create] Created successfully")
                ResultWrapper.Success(true)
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
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
            Log.e(TAG, "[create] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun getDetails(
        recipeId: Long,
        userId: Long,
        token: String
    ) : ResultWrapper<RecipeFullDTO> {
        return try {
            Log.d(TAG, "[getRecipeDetails] Fetching recipe by id: $recipeId")
            val response = service.getRecipeDetails(recipeId, userId, token)
            if(response.isSuccessful) {
                ResultWrapper.Success(
                    response.body() as RecipeFullDTO
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.d(TAG, "[getRecipeDetails] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[getRecipeDetails] Not mapped error")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[getRecipeDetails] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun rate(
        recipeId: Long,
        rateRecipeRequestDTO: RateRecipeRequestDTO,
        token: String
    ): ResultWrapper<Boolean> {
        return try {
            Log.d(TAG, "[rateRecipe] Rating recipe with id: $recipeId")
            val response = service.rateRecipe(recipeId, rateRecipeRequestDTO, token)
            if (response.isSuccessful) {
                ResultWrapper.Success(true)
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when (response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.d(TAG, "[rateRecipe] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        val detail = error.get(error.keys().next()).toString()
                        Log.d(TAG, "[rateRecipe] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[rateRecipe] Not mapped error")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[rateRecipe] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun list(
        queryText: String?,
        userId: Long,
        pageNumber: Int,
        level: RecipeLevel?,
        availability: IngredientState?,
        minPreparationTime: Int?,
        maxPreparationTime: Int?,
        createdByYou: Boolean?,
        token: String
    ) : ResultWrapper<Pageable<List<RecipePartialDTO>>?> {
        return try {
            Log.d(TAG, "[list] Fetching recipes with filters")
            val response = service.list(
                queryText=queryText,
                userId=userId,
                pageNumber=pageNumber,
                pageSize=10,
                level=level,
                availability=availability,
                minPreparationTime=minPreparationTime,
                maxPreparationTime=maxPreparationTime,
                createdByYou=createdByYou,
                token=token
            )
            if (response.isSuccessful) {
                Log.d(TAG, "[list] Found successfully")
                ResultWrapper.Success(
                    response.body()
                )
            } else {
                Log.d(TAG, response.code().toString())
                val error = JSONObject(response.errorBody()!!.string())
                return when (response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.d(TAG, "[list] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.d(TAG, "[list] Not mapped error")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[list] Failed", e)
            ResultWrapper.ConnectionError
        }
    }
}
