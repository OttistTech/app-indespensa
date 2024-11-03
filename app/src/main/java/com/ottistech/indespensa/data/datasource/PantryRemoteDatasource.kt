package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemAddDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemCloseValidityDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemCreateDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemDetailsDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemFullDTO
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

    suspend fun createItem(
        userId: Long,
        pantryItem: PantryItemCreateDTO,
        token: String
    ) : ResultWrapper<PantryItemFullDTO> {
        try {
            Log.d(TAG, "[createItem] Trying to create pantry item with $pantryItem")
            val response = service.createItem(userId, pantryItem, token)
            return if(response.isSuccessful) {
                Log.d(TAG, "[createItem] Pantry item created successfully")
                ResultWrapper.Success(
                    response.body() as PantryItemFullDTO
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
                    HttpURLConnection.HTTP_CONFLICT -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[createItem] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        val detail = error.get(error.keys().next()).toString()
                        Log.e(TAG, "[createItem] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[createItem] A not mapped error occurred")
                        ResultWrapper.Error(null, "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[createItem] Failed while creating pantry item", e)
            return ResultWrapper.NetworkError
        }
    }

    suspend fun listItems(userId: Long, token: String) : ResultWrapper<List<PantryItemPartialDTO>> {
        try {
            Log.d(TAG, "[listItems] Trying to fetch pantry items for user $userId")
            val response = service.listItems(userId, token)
            return if(response.isSuccessful) {
                Log.d(TAG, "[listItems] Found pantry items for user $userId")
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
                        Log.e(TAG, "[listItems] A not mapped error occurred")
                        ResultWrapper.Error(null, "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[listItems] Failed while listing pantry items", e)
            return ResultWrapper.NetworkError
        }
    }

    suspend fun updateItemsAmount(pantryItems : List<ProductItemUpdateAmountDTO>, token: String) : ResultWrapper<Boolean> {
        return try {
            Log.d(TAG, "[updateItemsAmount] Trying to update amount of ${pantryItems.size} items")
            val response = service.updateItemsAmount(pantryItems, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[updateItemsAmount] Updated amount successfully")
                ResultWrapper.Success(true)
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                Log.e(TAG, "[updateItemsAmount] A not mapped error occurred")
                ResultWrapper.Error(response.code(), error.get("detail").toString())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[updateItemsAmount] Failed while updating items amount", e)
            ResultWrapper.NetworkError
        }
    }

    suspend fun getItemDetails(pantryItemId: Long, token: String) : ResultWrapper<PantryItemDetailsDTO> {
        try {
            Log.d(TAG, "[getItemDetails] Trying to get pantry item details for $pantryItemId")
            val response = service.getItemDetails(pantryItemId, token)
            return if(response.isSuccessful) {
                Log.d(TAG, "[getItemDetails] Found pantry item details successfully $pantryItemId")
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

    suspend fun addItem(userId: Long, shopItemId: Long, validityDate: Date, token: String) : ResultWrapper<PantryItemFullDTO> {
        try {
            val pantryItemAdd = PantryItemAddDTO(
                shopItemId,
                validityDate
            )

            Log.d(TAG, "[addItem] Trying to add item to pantry")
            val response = service.addPantryItem(userId, pantryItemAdd, token)

            return if(response.isSuccessful) {
                Log.d(TAG, "[addItem] Added items to pantry successfully")
                ResultWrapper.Success(
                    response.body() as PantryItemFullDTO
                )
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
                        Log.e(TAG, "[addItem] A not mapped error occurred")
                        ResultWrapper.Error(null, "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[addAllShopItemsToPantry] Failed while adding shop item to pantry", e)
            return ResultWrapper.NetworkError
        }
    }

    suspend fun addAllShopItemsToPantry(userId: Long, token: String): ResultWrapper<Any> {
        return try {
            Log.d(TAG, "[addAllShopItemsToPantry] Trying to add all shop items to pantry")
            val response = service.addAllShopItemsToPantry(userId, token)
            if (response.isSuccessful) {
                Log.d(TAG, "[addAllShopItemsToPantry] Added all shop items to pantry successfully")
                ResultWrapper.Success("Added all shop items to pantry successfully")
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                Log.d(TAG, response.code().toString())

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
                        Log.e(TAG, "[addAllShopItemsToPantry] A not mapped error occurred")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed while adding shop all items to pantry", e)
            ResultWrapper.NetworkError
        }
    }

    suspend fun listCloseValidityItems(userId: Long, token: String) : ResultWrapper<List<PantryItemCloseValidityDTO>> {
        try {
            Log.d(TAG, "[listCloseValidityItems] Trying to fetch pantry items for user $userId")
            val response = service.listCloseValidityItems(userId, token)
            return if(response.isSuccessful) {
                Log.d(TAG, "[listCloseValidityItems] Found pantry items for user $userId")
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
                    Log.e(TAG, "[listCloseValidityItems] A not mapped error occurred")
                    ResultWrapper.Error(null, "Unexpected Error")
                }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[listCloseValidityItems] Failed while listing pantry items", e)
            return ResultWrapper.NetworkError
        }
    }


}