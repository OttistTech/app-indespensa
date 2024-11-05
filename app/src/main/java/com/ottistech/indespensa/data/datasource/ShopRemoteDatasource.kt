package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.product.ProductItemUpdateAmountDTO
import com.ottistech.indespensa.webclient.dto.shoplist.PurchaseDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemCreateDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemDetailsDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemPartialDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.core.ShopService

class ShopRemoteDatasource {

    private val TAG = "SHOP REMOTE DATASOURCE"
    private val service : ShopService =
        RetrofitInitializer().getCoreService(ShopService::class.java)

    suspend fun add(
        userId: Long,
        shopItem: ShopItemCreateDTO,
        token: String
    ) : ResultWrapper<Boolean> {
        return try {
            Log.d(TAG, "[add] Adding shop item with: $shopItem")
            val response = service.add(userId, shopItem, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[add] Added successfully")
                ResultWrapper.Success(true)
            } else {
                Log.e(TAG, "[add] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[add] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun list(
        userId: Long,
        token: String
    ) : ResultWrapper<List<ShopItemPartialDTO>> {
        return try {
            Log.d(TAG, "[list] Fetching shop items")
            val response = service.list(userId, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[list] Found successfully")
                ResultWrapper.Success(
                    response.body() as List<ShopItemPartialDTO>
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

    suspend fun updateAmount(
        pantryItems : List<ProductItemUpdateAmountDTO>,
        token: String
    ) : ResultWrapper<Boolean> {
        return try {
            Log.d(TAG, "[updateAmount] Trying to update amount of ${pantryItems.size} items")
            val response = service.updateAmount(pantryItems, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[updateAmount] Updated successfully")
                ResultWrapper.Success(true)
            } else {
                Log.e(TAG, "[updateAmount] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[updateAmount] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun getDetails(
        itemId: Long,
        token: String
    ): ResultWrapper<ShopItemDetailsDTO> {
        return try {
            Log.d(TAG, "[getDetails] Fetching item details by id $itemId")
            val response = service.getDetails(itemId, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[getDetails] Found successfully")
                ResultWrapper.Success(
                    response.body() as ShopItemDetailsDTO
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

    suspend fun getHistory(
        userId: Long,
        token: String
    ): ResultWrapper<List<PurchaseDTO>> {
        try {
            Log.d(TAG, "[getHistory] Fetching history")
            val response = service.getHistory(userId, token)
            return if(response.isSuccessful) {
                Log.d(TAG, "[getHistory] Found successfully")
                ResultWrapper.Success(
                    response.body() as List<PurchaseDTO>
                )
            } else {
                Log.e(TAG, "[getHistory] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[getHistory] Failed", e)
            return ResultWrapper.ConnectionError
        }
    }
}
