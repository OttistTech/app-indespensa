package com.ottistech.indespensa.data.repository

import android.util.Log
import com.ottistech.indespensa.data.datasource.ProductRemoteDataSource
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.webclient.dto.ProductResponseDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class ProductRepository {

    private val TAG = "PRODUCT REPOSITORY"
    private val remoteDataSource = ProductRemoteDataSource()

    suspend fun findByBarcode(barcode: String) : ProductResponseDTO? {
        Log.d(TAG, "[findByBarcode] trying to find product by barcode")
        val result : ResultWrapper<ProductResponseDTO> = remoteDataSource.findByBarcode(barcode)
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

}