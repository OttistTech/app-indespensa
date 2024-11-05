package com.ottistech.indespensa.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.ottistech.indespensa.data.DataConstants
import com.ottistech.indespensa.data.datasource.ImageFirebaseDatasource
import com.ottistech.indespensa.data.datasource.RecipeRemoteDataSource
import com.ottistech.indespensa.data.exception.BadRequestException
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.exception.ResourceUnauthorizedException
import com.ottistech.indespensa.shared.AppAccountType
import com.ottistech.indespensa.shared.IngredientState
import com.ottistech.indespensa.shared.RecipeLevel
import com.ottistech.indespensa.ui.helpers.getCurrentUser
import com.ottistech.indespensa.webclient.dto.Pageable
import com.ottistech.indespensa.webclient.dto.recipe.RateRecipeRequestDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeCreateDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeFullDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipePartialDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class RecipeRepository(
    private val context: Context
) {

    private val remoteDataSource = RecipeRemoteDataSource()
    private val imageDatasource = ImageFirebaseDatasource(DataConstants.FIREBASE_STORAGE_RECIPES)

    suspend fun create(
        recipe: RecipeCreateDTO,
        imageBitmap: Bitmap?
    ) : Boolean {
        val currentUser = context.getCurrentUser()
        recipe.createdBy = currentUser.userId
        recipe.isShared = currentUser.type == AppAccountType.PERSONAL
        recipe.imageUrl = imageBitmap?.let {
            imageDatasource.upload(it)
        }
        val token = context.getCurrentUser().token
        val result =
            remoteDataSource.create(recipe, token)
        return when(result) {
            is ResultWrapper.Success -> {
                result.value
            }
            is ResultWrapper.Error -> {
                when(result.code) {
                    HttpURLConnection.HTTP_BAD_REQUEST ->
                        throw BadRequestException(result.error)
                    HttpURLConnection.HTTP_UNAUTHORIZED ->
                        throw ResourceUnauthorizedException(result.error)
                    else ->
                        false
                }
            }
            else -> false
        }
    }

    suspend fun getDetails(
        recipeId: Long
    ): RecipeFullDTO {
        val currentUser = context.getCurrentUser()
        val result =
            remoteDataSource.getDetails(recipeId, currentUser.userId, currentUser.token)
        when(result) {
            is ResultWrapper.Success -> {
                return result.value
            }
            is ResultWrapper.Error -> {
                when (result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    HttpURLConnection.HTTP_UNAUTHORIZED ->
                        throw ResourceUnauthorizedException(result.error)
                    else ->
                        throw Exception(result.error)
                }
            }
            else ->  throw Exception("Error while getting recipe details")
        }
    }

    suspend fun rateRecipe(
        recipeId: Long,
        rateRecipeRequestDTO: RateRecipeRequestDTO
    ) : Boolean {
        val token = context.getCurrentUser().token
        val result =
            remoteDataSource.rate(recipeId, rateRecipeRequestDTO, token)
        return when (result) {
            is ResultWrapper.Success -> {
                result.value
            }
            is ResultWrapper.Error -> {
                when (result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    HttpURLConnection.HTTP_BAD_REQUEST ->
                        throw BadRequestException(result.error)
                    HttpURLConnection.HTTP_UNAUTHORIZED ->
                        throw ResourceUnauthorizedException(result.error)
                    else -> false
                }
            }
            else -> false
        }
    }

    suspend fun list(
        queryText: String? = null,
        pageNumber: Int = 0,
        level: RecipeLevel? = null,
        availability: IngredientState? = null,
        minPreparationTime: Int? = null,
        maxPreparationTime: Int? = null,
        createdByYou: Boolean? = false
    ) : Pageable<List<RecipePartialDTO>> {
        val currentUser = context.getCurrentUser()
        val result: ResultWrapper<Pageable<List<RecipePartialDTO>>?> = remoteDataSource.list(
            queryText=queryText,
            userId=currentUser.userId,
            pageNumber=pageNumber,
            level=level,
            availability=availability,
            minPreparationTime=minPreparationTime,
            maxPreparationTime=maxPreparationTime,
            createdByYou=createdByYou,
            token=currentUser.token
        )
        when (result) {
            is ResultWrapper.Success -> {
                if(result.value?.content?.isNotEmpty() == true) {
                    return result.value
                } else {
                    throw ResourceNotFoundException("No recipes found")
                }
            }
            is ResultWrapper.Error -> {
                when (result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    HttpURLConnection.HTTP_UNAUTHORIZED ->
                        throw ResourceUnauthorizedException(result.error)
                    else -> throw Exception("Could not list recipes with filters")
                }
            }
            else -> throw Exception("Error while listing recipes")
        }
    }
}