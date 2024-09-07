package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.PantryItemCreateDTO
import com.ottistech.indespensa.webclient.dto.PantryItemResponseDTO
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
    ) : ResultWrapper<PantryItemResponseDTO> {
        try {
            Log.d(TAG, "[createItem] Trying to create pantry item with $pantryItem")
            val response = service.createItem(userId, pantryItem)
            return if(response.isSuccessful) {
                Log.d(TAG, "[createItem] Pantry item created successfully")
                ResultWrapper.Success(
                    response.body() as PantryItemResponseDTO
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
}