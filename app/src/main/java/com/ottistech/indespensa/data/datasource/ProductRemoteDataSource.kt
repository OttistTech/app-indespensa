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

    suspend fun findByBarcode(barcode: String, token: String) : ResultWrapper<ProductDTO> {
        try {
            Log.d(TAG, "[findByBarcode] Trying to find product by barcode")
            val response = service.findByBarcode(barcode, token)
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
            Log.e(TAG, "[findByBarcode] Failed while looking for product by barcode", e)
            return ResultWrapper.NetworkError
        }
    }

    suspend fun search(query: String, token: String) : ResultWrapper<List<ProductSearchResponseDTO>> {
        try {
            Log.d(TAG, "[search] Trying to search products matching $query")
            val response = service.search(query, token)
            return if(response.isSuccessful) {
                Log.d(TAG, "[search] ${response.body()?.size} products found successfully")
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
                        Log.e(TAG, "[search] A not mapped error occurred")
                        ResultWrapper.Error(null, "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[search] Failed while looking for products by query", e)
            return ResultWrapper.NetworkError
        }
    }

    suspend fun findById(productId: Long, token: String) : ResultWrapper<ProductDTO> {
        try {
            Log.d(TAG, "[findById] Trying to find product by id")
            val response = service.findById(productId, token)
            return if(response.isSuccessful) {
                Log.d(TAG, "[findById] Product found successfully")
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
                        Log.e(TAG, "[findById] A not mapped error occurred")
                        ResultWrapper.Error(null, "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[findById] Failed while looking for product by id", e)
            return ResultWrapper.NetworkError
        }
    }
}