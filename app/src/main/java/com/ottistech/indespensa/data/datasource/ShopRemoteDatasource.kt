package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemCreateDTO
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
            Log.d(TAG, "[addItem] Trying to ad shop item with $shopItem")
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
}
