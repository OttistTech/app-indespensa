package com.ottistech.indespensa.data.repository

import android.content.Context
import android.util.Log
import com.ottistech.indespensa.data.datasource.ShopRemoteDatasource
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.ui.helpers.getCurrentUser
import com.ottistech.indespensa.webclient.dto.product.ProductItemUpdateAmountDTO
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

    suspend fun addItem(productId: Long) : Boolean {
        val userId = context.getCurrentUser().userId
        Log.d(TAG, "[addItem] Trying to add shop item with $productId")
        val result: ResultWrapper<ShopItemPartialDTO> = remoteDataSource.addItem(
            userId, ShopItemCreateDTO(productId)
        )
        return when(result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[addItem] Shop item added successfully: ${result.value}")
                true
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[addItem] Error while adding shop item: $result")
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

    fun getItemDetails(itemId: Long): ShopItemDetailsDTO? {
        // TODO: Integrate this method
        return null
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
}
