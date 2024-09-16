package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.PantryItemCreateDTO
import com.ottistech.indespensa.webclient.dto.PantryItemDTO
import com.ottistech.indespensa.webclient.dto.PantryItemPartialDTO
import com.ottistech.indespensa.webclient.dto.PantryItemUpdateDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.PantryService
import org.json.JSONObject
import java.net.HttpURLConnection

class PantryRemoteDatasource {

    private val TAG = "PANTRY REMOTE DATASOURCE"
    private val service : PantryService =
        RetrofitInitializer().getService(PantryService::class.java)

    suspend fun createItem(
        userId: Long,
        pantryItem: PantryItemCreateDTO
    ) : ResultWrapper<PantryItemDTO> {
        try {
            Log.d(TAG, "[createItem] Trying to create pantry item with $pantryItem")
            val response = service.createItem(userId, pantryItem)
            return if(response.isSuccessful) {
                Log.d(TAG, "[createItem] Pantry item created successfully")
                ResultWrapper.Success(
                    response.body() as PantryItemDTO
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

    suspend fun listItems(userId: Long) : ResultWrapper<List<PantryItemPartialDTO>> {
        try {
            Log.d(TAG, "[listItems] Trying to fetch pantry items for user $userId")
            val response = service.listItems(userId)
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

    suspend fun updateItemsAmount(pantryItems : List<PantryItemUpdateDTO>) : ResultWrapper<List<PantryItemUpdateDTO>> {
        try {
            Log.d(TAG, "[updateItemsAmount] Trying to update amount of ${pantryItems.size} items")
            val response = service.updateItemsAmount(pantryItems)
            return if(response.isSuccessful) {
                Log.d(TAG, "[updateItemsAmount] Updated amount successfully for ${response.body()?.size}/${pantryItems.size} items")
                ResultWrapper.Success(
                    response.body() as List<PantryItemUpdateDTO>
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
}