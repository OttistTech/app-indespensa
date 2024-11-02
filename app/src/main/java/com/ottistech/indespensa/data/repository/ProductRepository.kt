package com.ottistech.indespensa.data.repository

import android.content.Context
import android.util.Log
import com.ottistech.indespensa.data.datasource.ProductRemoteDataSource
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.ui.helpers.getCurrentUser
import com.ottistech.indespensa.webclient.dto.product.ProductDTO
import com.ottistech.indespensa.webclient.dto.product.ProductSearchResponseDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class ProductRepository(
    val context: Context
) {

    private val TAG = "PRODUCT REPOSITORY"
    private val remoteDataSource = ProductRemoteDataSource()

    suspend fun findByBarcode(barcode: String) : ProductDTO? {
        Log.d(TAG, "[findByBarcode] trying to find product by barcode")
        val token = context.getCurrentUser().token

        val result : ResultWrapper<ProductDTO> = remoteDataSource.findByBarcode(barcode, token)
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
        val token = context.getCurrentUser().token

        val result : ResultWrapper<List<ProductSearchResponseDTO>> = remoteDataSource.search(query, token)
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

    suspend fun findById(productId: Long) : ProductDTO {
        Log.d(TAG, "[findById] trying to find product by id")
        val token = context.getCurrentUser().token
        val result : ResultWrapper<ProductDTO> = remoteDataSource.findById(productId, token)

        return when(result) {
            is ResultWrapper.Success -> {
                val productFound = result.value
                Log.d(TAG, "[findById] Found product successfully: $productFound")
                productFound
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[findById] Error while looking for product by id: $result")
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        throw ResourceNotFoundException(result.error)
                    }
                    else -> throw Exception(result.error)
                }
            }
            else -> throw Exception("Could not fetch product")
        }
    }

}