package com.ottistech.indespensa.data.repository

import android.content.Context
import com.ottistech.indespensa.data.datasource.RecipeRemoteDataSource
import com.ottistech.indespensa.data.exception.BadRequestException
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.exception.ResourceUnauthorizedException
import com.ottistech.indespensa.ui.helpers.getCurrentUser
import com.ottistech.indespensa.webclient.dto.recipe.RateRecipeRequestDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeDetailsDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class RecipeRepository(
    private val context: Context
) {

    private val remoteDataSource = RecipeRemoteDataSource()

    suspend fun getRecipeDetails(recipeId: Long): RecipeDetailsDTO? {
        val userId = context.getCurrentUser().userId
        val result : ResultWrapper<RecipeDetailsDTO> = remoteDataSource.getRecipeDetails(recipeId, userId)

        return when(result) {
            is ResultWrapper.Success -> {
                result.value
            }
            is ResultWrapper.Error -> {
                when (result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND -> throw ResourceNotFoundException("Não foi possível encontrar essa receita")
                    HttpURLConnection.HTTP_BAD_REQUEST -> throw BadRequestException("Tente novamente mais tarde!")
                    else -> {
                        null
                    }
                }
            }
            else -> {
                null
            }
        }
    }

    suspend fun rateRecipe(recipeId: Long, rateRecipeRequestDTO: RateRecipeRequestDTO) : Boolean {
        val result: ResultWrapper<Any> = remoteDataSource.rateRecipe(recipeId, rateRecipeRequestDTO)

        return when (result) {
            is ResultWrapper.Success -> {
                result.value
                true
            }
            is ResultWrapper.Error -> {
                when (result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        throw ResourceNotFoundException(result.error)
                    }
                    HttpURLConnection.HTTP_UNAUTHORIZED -> {
                        throw ResourceUnauthorizedException(result.error)
                    }
                    else -> false
                }
            }
            else -> {
                false
            }
        }
    }
}