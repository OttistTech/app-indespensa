package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.product.ProductItemUpdateAmountDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemCreateDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemDetailsDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemPartialDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.ShopService
import org.json.JSONObject
import java.net.HttpURLConnection

class ShopRemoteDatasource {

    private val TAG = "SHOP REMOTE DATASOURCE"
    private val service : ShopService =
        RetrofitInitializer().getService(ShopService::class.java)

    suspend fun addItem(
        userId: Long,
        shopItem: ShopItemCreateDTO
    ) : ResultWrapper<ShopItemPartialDTO> {
        try {
            Log.d(TAG, "[addItem] Trying to add shop item with $shopItem")
            val response = service.addItem(userId, shopItem)
            return if(response.isSuccessful) {
                Log.d(TAG, "[addItem] Shop item added successfully")
                ResultWrapper.Success(
                    response.body() as ShopItemPartialDTO
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[addItem] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[addItem] A not mapped error occurred")
                        ResultWrapper.Error(null, "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[addItem] Failed while adding shop item", e)
            return ResultWrapper.NetworkError
        }
    }

    suspend fun listItems(userId: Long) : ResultWrapper<List<ShopItemPartialDTO>> {
        try {
            Log.d(TAG, "[listItems] Trying to fetch shop items for user $userId")
            val response = service.listItems(userId)
            return if(response.isSuccessful) {
                Log.d(TAG, "[listItems] Found shop items for user $userId")
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
                    Log.e(TAG, "[listItems] A not mapped error occurred")
                    ResultWrapper.Error(null, "Unexpected Error")
                }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[listItems] Failed while listing shop items", e)
            return ResultWrapper.NetworkError
        }
    }

    suspend fun updateItemsAmount(pantryItems : List<ProductItemUpdateAmountDTO>) : ResultWrapper<List<ProductItemUpdateAmountDTO>> {
        try {
            Log.d(TAG, "[updateItemsAmount] Trying to update amount of ${pantryItems.size} items")
            val response = service.updateItemsAmount(pantryItems)
            return if(response.isSuccessful) {
                Log.d(TAG, "[updateItemsAmount] Updated amount successfully for ${response.body()?.size}/${pantryItems.size} items")
                ResultWrapper.Success(
                    response.body() as List<ProductItemUpdateAmountDTO>
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                Log.e(TAG, "[updateItemsAmount] A not mapped error occurred")
                ResultWrapper.Error(response.code(), error.get("detail").toString())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[updateItemsAmount] Failed while updating items amount", e)
            return ResultWrapper.NetworkError
        }
    }

    suspend fun getItemDetails(itemId: Long): ResultWrapper<ShopItemDetailsDTO> {
        try {
            Log.d(TAG, "[getItemDetails] Trying to get pantry item details for $itemId")
            val response = service.getItemDetails(itemId)
            return if(response.isSuccessful) {
                Log.d(TAG, "[getItemDetails] Found pantry item details successfully $itemId")
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
                    Log.e(TAG, "[getItemDetails] A not mapped error occurred")
                    ResultWrapper.Error(null, "Unexpected Error")
                }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[getItemDetails] Failed while getting pantry item by id", e)
            return ResultWrapper.NetworkError
        }
    }


}
