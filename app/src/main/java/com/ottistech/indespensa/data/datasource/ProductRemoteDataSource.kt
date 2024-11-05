package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.product.ProductDTO
import com.ottistech.indespensa.webclient.dto.product.ProductSearchResponseDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.core.ProductService

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
                Log.e(TAG, "[findByBarcode] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
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
                Log.e(TAG, "[search] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
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
                Log.e(TAG, "[findById] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[findById] Failed", e)
            ResultWrapper.ConnectionError
        }
    }
}