package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemAddDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemCloseValidityDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemCreateDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemDetailsDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemPartialDTO
import com.ottistech.indespensa.webclient.dto.product.ProductItemUpdateAmountDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.core.PantryService
import org.json.JSONObject
import java.net.HttpURLConnection
import java.util.Date

class PantryRemoteDatasource {

    private val TAG = "PANTRY REMOTE DATASOURCE"
    private val service : PantryService =
        RetrofitInitializer().getCoreService(PantryService::class.java)

    suspend fun create(
        userId: Long,
        pantryItem: PantryItemCreateDTO,
        token: String
    ) : ResultWrapper<Boolean> {
        return try {
            Log.d(TAG, "[createItem] Trying to create with: $pantryItem")
            val response = service.createItem(userId, pantryItem, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[createItem] Created successfully")
                ResultWrapper.Success(true)
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        val detail = error.get(error.keys().next()).toString()
                        Log.e(TAG, "[createItem] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[createItem] Not mapped error")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[createItem] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun list(
        userId: Long,
        token: String
    ) : ResultWrapper<List<PantryItemPartialDTO>> {
        return try {
            Log.d(TAG, "[listItems] Fetching pantry items")
            val response = service.listItems(userId, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[listItems] Found pantry items successfully")
                ResultWrapper.Success(
                    response.body() as List<PantryItemPartialDTO>
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
        pantryItemId: Long,
        token: String
    ) : ResultWrapper<PantryItemDetailsDTO> {
        return try {
            Log.d(TAG, "[getItemDetails] Fetching pantry item details for $pantryItemId")
            val response = service.getItemDetails(pantryItemId, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[getItemDetails] Found successfully")
                ResultWrapper.Success(
                    response.body() as PantryItemDetailsDTO
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

    suspend fun addShopItem(
        userId: Long,
        shopItemId: Long,
        validityDate: Date,
        token: String
    ) : ResultWrapper<Boolean> {
        return try {
            val pantryItemAdd = PantryItemAddDTO(shopItemId, validityDate)
            Log.d(TAG, "[addItem] Trying to add item with: $pantryItemAdd")
            val response = service.addPantryItem(userId, pantryItemAdd, token)
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
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
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
            Log.e(TAG, "[addAllShopItemsToPantry] Failed", e)
            return ResultWrapper.ConnectionError
        }
    }

    suspend fun addAllShopItems(
        userId: Long,
        token: String
    ): ResultWrapper<Boolean> {
        return try {
            Log.d(TAG, "[addAllShopItemsToPantry] Trying to add all shop items")
            val response = service.addAllShopItemsToPantry(userId, token)
            if (response.isSuccessful) {
                Log.d(TAG, "[addAllShopItemsToPantry] Added all successfully")
                ResultWrapper.Success(true)
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when (response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[addAllShopItemsToPantry] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        val detail = error.get(error.keys().next()).toString()
                        Log.e(TAG, "[addAllShopItemsToPantry] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[addAllShopItemsToPantry] Not mapped error")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun listCloseValidityItems(
        userId: Long,
        token: String
    ) : ResultWrapper<List<PantryItemCloseValidityDTO>> {
        return try {
            Log.d(TAG, "[listCloseValidityItems] Fetching close validity items")
            val response = service.listCloseValidityItems(userId, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[listCloseValidityItems] Found successfully")
                ResultWrapper.Success(
                    response.body() as List<PantryItemCloseValidityDTO>
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[listCloseValidityItems] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    } else -> {
                        Log.e(TAG, "[listCloseValidityItems] Not mapped error")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[listCloseValidityItems] Failed", e)
            return ResultWrapper.ConnectionError
        }
    }
}