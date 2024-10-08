package com.ottistech.indespensa.data.repository

import android.util.Log
import com.ottistech.indespensa.data.datasource.ProductRemoteDataSource
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.webclient.dto.product.ProductDTO
import com.ottistech.indespensa.webclient.dto.product.ProductSearchResponseDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class ProductRepository {

    private val TAG = "PRODUCT REPOSITORY"
    private val remoteDataSource = ProductRemoteDataSource()

    suspend fun findByBarcode(barcode: String) : ProductDTO? {
        Log.d(TAG, "[findByBarcode] trying to find product by barcode")
        val result : ResultWrapper<ProductDTO> = remoteDataSource.findByBarcode(barcode)
        return when(result) {
            is ResultWrapper.Success -> {
                val productFound = result.value
                Log.d(TAG, "[findByBarcode] Found product successfully: $productFound")
                productFound
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[findByBarcode] Error while looking for product by barcode: $result")
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        throw ResourceNotFoundException(result.error)
                    }
                    else -> null
                }
            }
            else -> null
        }
    }

    suspend fun search(query: String) : List<ProductSearchResponseDTO> {
        Log.d(TAG, "[search] Trying to search products matching $query")
        val result : ResultWrapper<List<ProductSearchResponseDTO>> = remoteDataSource.search(query)
        return when(result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[search] Found ${result.value.size} products successfully")
                result.value
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[search] Error while looking for product by query: $result")
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        throw ResourceNotFoundException(result.error)
                    }
                    else -> throw Exception(result.error)
                }
            }
            else -> throw Exception("A not mapped error occurred")
        }
    }

}