package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.product.ProductDTO
import com.ottistech.indespensa.webclient.dto.product.ProductSearchResponseDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.core.ProductService
import org.json.JSONObject
import java.net.HttpURLConnection

class ProductRemoteDataSource {

    private val TAG = "PRODUCT REMOTE DATASOURCE"
    private val service : ProductService =
        RetrofitInitializer().getCoreService(ProductService::class.java)

    suspend fun findByBarcode(
        barcode: String,
        token: String
    ) : ResultWrapper<ProductDTO> {
        return try {
            Log.d(TAG, "[findByBarcode] Looking for product by barcode")
            val response = service.findByBarcode(barcode, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[findByBarcode] Found successfully")
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
                        Log.e(TAG, "[findByBarcode] Not mapped error")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[findByBarcode] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun search(
        query: String,
        token: String
    ) : ResultWrapper<List<ProductSearchResponseDTO>> {
        return try {
            Log.d(TAG, "[search] Searching products matching $query")
            val response = service.search(query, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[search] Found ${response.body()?.size} successfully")
                ResultWrapper.Success(
                    response.body() as List<ProductSearchResponseDTO>
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[search] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[search] Not mapped error")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[search] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun findById(productId: Long, token: String) : ResultWrapper<ProductDTO> {
        return try {
            Log.d(TAG, "[findById] Fetching product by id $productId")
            val response = service.findById(productId, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[findById] Found successfully")
                ResultWrapper.Success(
                    response.body() as ProductDTO
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[findById] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[findById] Not mapped error")
                        ResultWrapper.Error(response.code(), "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[findById] Failed", e)
            ResultWrapper.ConnectionError
        }
    }
}