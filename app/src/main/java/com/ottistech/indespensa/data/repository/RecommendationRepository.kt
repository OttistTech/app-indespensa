package com.ottistech.indespensa.data.repository

import android.content.Context
import com.ottistech.indespensa.data.datasource.RecommendationRemoteDataSource
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.shared.getCurrentUser
import com.ottistech.indespensa.webclient.dto.product.ProductDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class RecommendationRepository (
    private val context: Context
) {

    private val remoteDataSource = RecommendationRemoteDataSource()

    suspend fun getProductRecommendations() : List<ProductDTO> {
        val userId = context.getCurrentUser().userId
        val result =
            remoteDataSource.getProductRecommendations(userId)
        when(result) {
            is ResultWrapper.Success -> {
                return result.value
            }
            is ResultWrapper.Error -> {
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        throw ResourceNotFoundException(result.error)
                    }
                    else -> throw Exception(result.error)
                }
            }
            else -> throw Exception("Error while fetching recommendations")
        }
    }
}