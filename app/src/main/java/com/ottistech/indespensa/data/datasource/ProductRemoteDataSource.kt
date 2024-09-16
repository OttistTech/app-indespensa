package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.ProductDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.ProductService
import org.json.JSONObject
import java.net.HttpURLConnection

class ProductRemoteDataSource {

    private val TAG = "PRODUCT REMOTE DATASOURCE"
    private val service : ProductService =
        RetrofitInitializer().getService(ProductService::class.java)

    suspend fun findByBarcode(barcode: String) : ResultWrapper<ProductDTO> {
        try {
            Log.d(TAG, "[findByBarcode] Trying to find product by barcode")
            val response = service.findByBarcode(barcode)
            return if(response.isSuccessful) {
                Log.d(TAG, "[findByBarcode] Product found successfully")
                ResultWrapper.Success(
                    response.body() as ProductDTO
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[findByBarcode] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[findByBarcode] A not mapped error occurred")
                        ResultWrapper.Error(null, "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed while looking for product by barcode", e)
            return ResultWrapper.NetworkError
        }
    }
}