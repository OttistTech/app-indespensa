package com.ottistech.indespensa.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.ottistech.indespensa.data.DataConstants
import com.ottistech.indespensa.data.datasource.ImageFirebaseDatasource
import com.ottistech.indespensa.data.datasource.PantryRemoteDatasource
import com.ottistech.indespensa.data.exception.BadRequestException
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.exception.ResourceUnauthorizedException
import com.ottistech.indespensa.ui.helpers.getCurrentUser
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemCreateDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemDetailsDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemFullDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemPartialDTO
import com.ottistech.indespensa.webclient.dto.product.ProductItemUpdateAmountDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection
import java.util.Date

class PantryRepository(
    private val context: Context
) {

    private val TAG = "PANTRY REPOSITORY"
    private val remoteDataSource = PantryRemoteDatasource()
    private val imageDatasource = ImageFirebaseDatasource(DataConstants.FIREBASE_STORAGE_PRODUCTS)

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
        val result : ResultWrapper<PantryItemFullDTO> = remoteDataSource.createItem(userId, pantryItem)
        return when(result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[createItem] Pantry item created successfully: ${result.value}")
                true
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[createItem] Error while creating pantry item: $result")
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        throw ResourceNotFoundException(result.error)
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

    suspend fun updateItemsAmount(vararg items: ProductItemUpdateAmountDTO) {
        if(items.isNotEmpty()) {
            Log.d(TAG, "[updateItemsAmount] Trying to update amount of ${items.size} items")
            val result : ResultWrapper<List<ProductItemUpdateAmountDTO>> = remoteDataSource.updateItemsAmount(items.asList())
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

    suspend fun getItemDetails(itemId: Long) : PantryItemDetailsDTO? {
        Log.d(TAG, "[getItemDetails] Trying to get item details with id $itemId")
        val result : ResultWrapper<PantryItemDetailsDTO> = remoteDataSource.getItemDetails(itemId)
        return when(result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[getItemDetails] Found pantry item details successfully")
                result.value
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[getItemDetails] Error while getting pantry item details: $result")
                throw ResourceNotFoundException("Could not find pantry item")
            }
            else -> {
                Log.e(TAG, "[getItemDetails] Unexpected error occurred while getting pantry item details")
                null
            }
        }
    }

    suspend fun addItem(shopItemId: Long, validityDate: Date): PantryItemFullDTO? {
        val userId = context.getCurrentUser().userId
        Log.d(TAG, "[addItem] Trying to add item to pantry for user $userId")
        val result : ResultWrapper<PantryItemFullDTO> = remoteDataSource.addItem(userId, shopItemId, validityDate)

        return when (result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[addItem] Added pantry item successfully")
                result.value
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[addItem] Error adding item to pantry: ${result.error}, code: ${result.code}")
                when (result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        throw ResourceNotFoundException(result.error)
                    }
                    HttpURLConnection.HTTP_UNAUTHORIZED -> {
                        throw ResourceUnauthorizedException(result.error)
                    }
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        throw BadRequestException(result.error)
                    }
                    else -> null
                }
            }
            else -> {
                Log.e(TAG, "[addItem] Unexpected result: $result")
                null
            }
        }
    }

    suspend fun addAllShopItemsToPantry(): Boolean {
        val userId = context.getCurrentUser().userId

        Log.d(TAG, "[addAllShopItemsToPantry] Trying to add all shop items to pantry of user $userId")
        val result: ResultWrapper<Any> = remoteDataSource.addAllShopItemsToPantry(userId)

        return when (result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[addAllShopItemsToPantry] Added all shop items to pantry successfully")
                true
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[addAllShopItemsToPantry] Error adding all shop items to pantry of user: ${result.error}, code: ${result.code}")
                when (result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        throw ResourceNotFoundException(result.error)
                    }
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        throw BadRequestException(result.error)
                    }
                    else -> false
                }
            }
            else -> {
                Log.e(TAG, "[addAllShopItemsToPantry] Unexpected result: $result")
                false
            }
        }

    }

}