package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.shared.IngredientState
import com.ottistech.indespensa.shared.RecipeLevel
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.Pageable
import com.ottistech.indespensa.webclient.dto.recipe.RateRecipeRequestDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeCreateDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeFullDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipePartialDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.core.RecipeService

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
                Log.e(TAG, "[create] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
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
            Log.d(TAG, "[getDetails] Fetching recipe by id: $recipeId")
            val response = service.getDetails(recipeId, userId, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[getDetails] Fetched successfully")
                ResultWrapper.Success(
                    response.body() as RecipeFullDTO
                )
            } else {
                Log.e(TAG, "[getDetails] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[getDetails] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun rate(
        recipeId: Long,
        rateRecipeRequestDTO: RateRecipeRequestDTO,
        token: String
    ): ResultWrapper<Boolean> {
        return try {
            Log.d(TAG, "[rate] Rating recipe with id: $recipeId")
            val response = service.rate(recipeId, rateRecipeRequestDTO, token)
            if (response.isSuccessful) {
                Log.d(TAG, "[rate] Rated successfully")
                ResultWrapper.Success(true)
            } else {
                Log.e(TAG, "[rate] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[rate] Failed", e)
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
                Log.e(TAG, "[list] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[list] Failed", e)
            ResultWrapper.ConnectionError
        }
    }
}
