package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.ShopListIngredientResponseDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.ShopListService
import org.json.JSONObject
import java.net.HttpURLConnection

class ShopListRemoteDataSource {

    private val TAG = "SHOP_LIST REMOTE DATASOURCE"
    private val service : ShopListService =
        RetrofitInitializer().getService(ShopListService::class.java)

    suspend fun listShopItems(userId: Long) : ResultWrapper<List<ShopListIngredientResponseDTO>> {
        try {
            Log.d(TAG, "[listShopItems] Trying to get shop items")
            val response = service.getShopItemList(userId)
            return if(response.isSuccessful) {
                Log.d(TAG, "[listShopItems] Found shop items successfully")
                ResultWrapper.Success(
                    response.body() as List<ShopListIngredientResponseDTO>
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[listCategories] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[listCategories] A not mapped error occurred")
                        ResultWrapper.Error(null, "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[listCategories] Failed while getting shop items", e)
            return ResultWrapper.NetworkError
        }
    }
}