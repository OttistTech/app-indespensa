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
import org.json.JSONObject
import java.net.HttpURLConnection

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
            Log.d(TAG, "[addItem] Adding shop item with: $shopItem")
            val response = service.addItem(userId, shopItem, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[addItem] Added successfully")
                ResultWrapper.Success(true)
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[addItem] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[addItem] Not mapped error")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[addItem] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun list(
        userId: Long,
        token: String
    ) : ResultWrapper<List<ShopItemPartialDTO>> {
        return try {
            Log.d(TAG, "[listItems] Fetching shop items")
            val response = service.listItems(userId, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[listItems] Found successfully")
                ResultWrapper.Success(
                    response.body() as List<ShopItemPartialDTO>
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[listItems] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    } else -> {
                    Log.e(TAG, "[listItems] Not mapped error")
                    ResultWrapper.Error(response.code(), "Unexpected Error")
                }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[listItems] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun updateAmount(
        pantryItems : List<ProductItemUpdateAmountDTO>,
        token: String
    ) : ResultWrapper<Boolean> {
        return try {
            Log.d(TAG, "[updateItemsAmount] Trying to update amount of ${pantryItems.size} items")
            val response = service.updateItemsAmount(pantryItems, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[updateItemsAmount] Updated successfully")
                ResultWrapper.Success(true)
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                Log.e(TAG, "[updateItemsAmount] Not mapped error")
                ResultWrapper.Error(response.code(), error.get("detail").toString())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[updateItemsAmount] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun getDetails(
        itemId: Long,
        token: String
    ): ResultWrapper<ShopItemDetailsDTO> {
        return try {
            Log.d(TAG, "[getItemDetails] Fetching item details by id $itemId")
            val response = service.getItemDetails(itemId, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[getItemDetails] Found successfully")
                ResultWrapper.Success(
                    response.body() as ShopItemDetailsDTO
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[getItemDetails] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    } else -> {
                        Log.e(TAG, "[getItemDetails] Not mapped error")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[getItemDetails] Failed", e)
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
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[getHistory] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    } else -> {
                    Log.e(TAG, "[getHistory] Not mapped error")
                    ResultWrapper.Error(response.code(), "Unexpected Error")
                }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[getHistory] Failed", e)
            return ResultWrapper.ConnectionError
        }
    }
}
