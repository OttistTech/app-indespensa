package com.ottistech.indespensa.data.repository

import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.util.Log
import com.ottistech.indespensa.data.DataConstants
import com.ottistech.indespensa.data.datasource.ImageFirebaseDatasource
import com.ottistech.indespensa.data.datasource.PantryRemoteDatasource
import com.ottistech.indespensa.webclient.dto.PantryItemCreateDTO
import com.ottistech.indespensa.webclient.dto.PantryItemResponseDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class PantryRepository {

    private val TAG = "PANTRY REPOSITORY"
    private val remoteDataSource = PantryRemoteDatasource()
    private val imageDatasource = ImageFirebaseDatasource(DataConstants.FIREBASE_STORAGE_PANTRY_ITEM)

    suspend fun createItem(
        userId: Long,
        pantryItem: PantryItemCreateDTO,
        imageBitmap: Bitmap?
    ) : Boolean {
        if (pantryItem.productImageUrl == null && imageBitmap != null) {
            Log.d(TAG, "[createItem] Detected new product image")
            pantryItem.productImageUrl = imageDatasource.uploadImage(imageBitmap)
        }
        Log.d(TAG, "[createItem] Trying to create pantry item with $pantryItem")
        val result : ResultWrapper<PantryItemResponseDTO> = remoteDataSource.createItem(userId, pantryItem)
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
}