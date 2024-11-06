package com.ottistech.indespensa.data.repository

import android.content.Context
import com.ottistech.indespensa.data.datasource.ProductRemoteDataSource
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.exception.ResourceUnauthorizedException
import com.ottistech.indespensa.shared.getCurrentUser
import com.ottistech.indespensa.webclient.dto.product.ProductDTO
import com.ottistech.indespensa.webclient.dto.product.ProductSearchResponseDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class ProductRepository(
    val context: Context
) {

    private val remoteDataSource = ProductRemoteDataSource()

    suspend fun findByBarcode(barcode: String) : ProductDTO {
        val token = context.getCurrentUser().token
        val result =
            remoteDataSource.findByBarcode(barcode, token)
        return when(result) {
            is ResultWrapper.Success -> {
                result.value
            }
            is ResultWrapper.Error -> {
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    HttpURLConnection.HTTP_UNAUTHORIZED ->
                        throw ResourceUnauthorizedException(result.error)
                    else ->
                        throw Exception(result.error)
                }
            }
            else -> throw Exception("Error while looking for product by barcode")
        }
    }

    suspend fun search(query: String) : List<ProductSearchResponseDTO> {
        val token = context.getCurrentUser().token
        val result =
            remoteDataSource.search(query, token)
        return when(result) {
            is ResultWrapper.Success -> {
                result.value
            }
            is ResultWrapper.Error -> {
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    HttpURLConnection.HTTP_UNAUTHORIZED ->
                        throw ResourceUnauthorizedException(result.error)
                    else ->
                        throw Exception(result.error)
                }
            }
            else -> throw Exception("Error while searching products")
        }
    }

    suspend fun findById(productId: Long) : ProductDTO {
        val token = context.getCurrentUser().token
        val result =
            remoteDataSource.findById(productId, token)
        return when(result) {
            is ResultWrapper.Success -> {
                result.value
            }
            is ResultWrapper.Error -> {
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    HttpURLConnection.HTTP_UNAUTHORIZED ->
                        throw ResourceUnauthorizedException(result.error)
                    else ->
                        throw Exception(result.error)
                }
            }
            else -> throw Exception("Error while finding product by id")
        }
    }
}