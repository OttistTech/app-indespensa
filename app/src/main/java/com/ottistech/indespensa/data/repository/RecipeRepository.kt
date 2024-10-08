package com.ottistech.indespensa.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.ottistech.indespensa.data.DataConstants
import com.ottistech.indespensa.data.datasource.ImageFirebaseDatasource
import com.ottistech.indespensa.data.datasource.RecipeRemoteDataSource
import com.ottistech.indespensa.data.exception.BadRequestException
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.shared.AppAccountType
import com.ottistech.indespensa.ui.helpers.getCurrentUser
import com.ottistech.indespensa.webclient.dto.recipe.RecipeCreateDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeFullDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class RecipeRepository (
    private val context: Context
){
    private val TAG = "RECIPE REPOSITORY"
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
            imageDatasource.uploadImage(it)
        }
        Log.d(TAG, "[create] Trying to create recipe with $recipe")
        val result : ResultWrapper<RecipeFullDTO> = remoteDataSource.create(recipe)
        return when(result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[create] recipe created successfully: ${result.value}")
                true
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[create] Error while creating recipe: $result")
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        throw ResourceNotFoundException(result.error)
                    }
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        throw BadRequestException(result.error)
                    }
                    else -> false
                }
            }
            else -> false
        }
    }
}