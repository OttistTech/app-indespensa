package com.ottistech.indespensa.data.repository

import android.content.Context
import android.util.Log
import com.ottistech.indespensa.data.datasource.ShopRemoteDatasource
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.ui.helpers.getCurrentUser
import com.ottistech.indespensa.webclient.dto.product.ProductItemUpdateAmountDTO
import com.ottistech.indespensa.webclient.dto.shoplist.PurchaseDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemCreateDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemDetailsDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemPartialDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class ShopRepository(
    private val context: Context
) {
    private val TAG = "SHOP REPOSITORY"
    private val remoteDataSource = ShopRemoteDatasource()

    suspend fun addItem(productId: Long) {
        val userId = context.getCurrentUser().userId
        Log.d(TAG, "[addItem] Trying to add shop item with $productId")
        val result: ResultWrapper<ShopItemPartialDTO> = remoteDataSource.addItem(
            userId, ShopItemCreateDTO(productId)
        )
        when(result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[addItem] Shop item added successfully: ${result.value}")
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[addItem] Error while adding shop item: $result")
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        throw ResourceNotFoundException(result.error)
                    }
                    else -> throw Exception("Could not add item to shop list")
                }
            }
            else -> throw Exception("Could not add item to shop list")
        }
    }

    suspend fun listItems() : List<ShopItemPartialDTO>? {
        val userId = context.getCurrentUser().userId
        val result : ResultWrapper<List<ShopItemPartialDTO>> = remoteDataSource.listItems(userId)
        return when(result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[listItems] Found shop items successfully")
                result.value
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[listItems] Error while fetching user shop items: $result")
                throw ResourceNotFoundException("Could not find any shop item")
            }
            else -> {
                Log.e(TAG, "[listItems] Unexpected error occurred while fetching user shop items")
                null
            }
        }
    }

    suspend fun getItemDetails(itemId: Long): ShopItemDetailsDTO? {
        Log.d(TAG, "[getItemDetails] Trying to get item details with id $itemId")
        val result : ResultWrapper<ShopItemDetailsDTO> = remoteDataSource.getItemDetails(itemId)
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

    suspend fun updateItemsAmount(vararg items: ProductItemUpdateAmountDTO) {
        if(items.isNotEmpty()) {
            Log.d(TAG, "[updateItemsAmount] Trying to update amount of ${items.size} items")
            val result : ResultWrapper<Boolean> = remoteDataSource.updateItemsAmount(items.asList())
            when(result) {
                is ResultWrapper.Success -> {
                    Log.d(TAG, "[updateItemsAmount] Updated successfully items")
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

    suspend fun getHistory(): List<PurchaseDTO>? {
        val userId = context.getCurrentUser().userId
        Log.d(TAG, "[getHistory] Trying to get history with id $userId")
        val result : ResultWrapper<List<PurchaseDTO>> = remoteDataSource.getHistory(userId)
        return when(result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[getHistory] Found history successfully")
                result.value
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[getHistory] Error while getting history: $result")
                throw ResourceNotFoundException("Could not find history")
            }
            else -> {
                Log.e(TAG, "[getHistory] Unexpected error occurred while getting history")
                null
            }
        }
    }
}
