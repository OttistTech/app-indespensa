package com.ottistech.indespensa.data.repository

import android.content.Context
import android.util.Log
import com.ottistech.indespensa.data.datasource.RecommendationRemoteDataSource
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.ui.helpers.getCurrentUser
import com.ottistech.indespensa.webclient.dto.product.ProductDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class RecommendationRepository (
    private val context: Context
) {

    private val TAG = "RECOMMENDATION REPOSITORY"
    private val remoteDataSource = RecommendationRemoteDataSource()

    suspend fun getProductRecommendations() : List<ProductDTO> {
        val userId = context.getCurrentUser().userId
        Log.d(TAG, "[getProductRecommendations] Trying to get recommendations")
        val result : ResultWrapper<List<ProductDTO>> = remoteDataSource.getProductRecommendations(userId)
        when(result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[getProductRecommendations] found recommendations")
                return result.value
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[getProductRecommendations] Error while fetching recommendations: $result")
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        throw ResourceNotFoundException(result.error)
                    }
                    else -> throw Exception("Could not fetch recommendations")
                }
            }
            else -> throw Exception("Could not fetch recommendations")
        }
    }
}