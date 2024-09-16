package com.ottistech.indespensa.data.repository

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.util.Log
import com.ottistech.indespensa.data.DataConstants
import com.ottistech.indespensa.data.datasource.ImageFirebaseDatasource
import com.ottistech.indespensa.data.datasource.PantryRemoteDatasource
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.ui.helpers.getCurrentUser
import com.ottistech.indespensa.webclient.dto.PantryItemCreateDTO
import com.ottistech.indespensa.webclient.dto.PantryItemDTO
import com.ottistech.indespensa.webclient.dto.PantryItemPartialDTO
import com.ottistech.indespensa.webclient.dto.PantryItemUpdateDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class PantryRepository(
    private val context: Context
) {

    private val TAG = "PANTRY REPOSITORY"
    private val remoteDataSource = PantryRemoteDatasource()
    private val imageDatasource = ImageFirebaseDatasource(DataConstants.FIREBASE_STORAGE_PANTRY_ITEM)

    suspend fun createItem(
        pantryItem: PantryItemCreateDTO,
        imageBitmap: Bitmap?
    ) : Boolean {
        val userId = context.getCurrentUser().userId
        if (pantryItem.productImageUrl == null && imageBitmap != null) {
            Log.d(TAG, "[createItem] Detected new product image")
            pantryItem.productImageUrl = imageDatasource.uploadImage(imageBitmap)
        }
        Log.d(TAG, "[createItem] Trying to create pantry item with $pantryItem")
        val result : ResultWrapper<PantryItemDTO> = remoteDataSource.createItem(userId, pantryItem)
        return when(result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[createItem] Pantry item created successfully: ${result.value}")
                true
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[createItem] Error while creating pantry item: $result")
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        throw NotFoundException(result.error)
                    }
                    else -> false
                }
            }
            else -> false
        }
    }

    suspend fun listItems() : List<PantryItemPartialDTO>? {
        val userId = context.getCurrentUser().userId
        Log.d(TAG, "[listItems] Trying to fetch pantry items for user $userId")
        val result : ResultWrapper<List<PantryItemPartialDTO>> = remoteDataSource.listItems(userId)
        return when(result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[listItems] Found pantry items successfully")
                result.value
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[listItems] Error while fetching user pantry items: $result")
                throw ResourceNotFoundException("Could not find any pantry item")
            }
            else -> {
                Log.e(TAG, "[listItems] Unexpected error occurred while fetching user pantry items")
                null
            }
        }
    }

    suspend fun updateItemsAmount(pantryItems: List<PantryItemUpdateDTO>) {
        if(pantryItems.isNotEmpty()) {
            Log.d(TAG, "[updateItemsAmount] Trying to update amount of ${pantryItems.size} items")
            val result : ResultWrapper<List<PantryItemUpdateDTO>> = remoteDataSource.updateItemsAmount(pantryItems)
            when(result) {
                is ResultWrapper.Success -> {
                    Log.d(TAG, "[updateItemsAmount] Updated successfully ${result.value.size} items")
                }
                is ResultWrapper.Error -> {
                    Log.e(TAG, "[updateItemsAmount] Error while while updating items amount: $result")
                }
                else -> {
                    Log.e(TAG, "[updateItemsAmount] Unexpected error occurred while updating items amount")
                }
            }
        }
    }
}