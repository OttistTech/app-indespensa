package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.product.ProductDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.recommendation.RecommendationService

class RecommendationRemoteDataSource {

    private val TAG = "RECOMMENDATION REMOTE DATASOURCE"
    private val service : RecommendationService =
        RetrofitInitializer().getRecommendationService(RecommendationService::class.java)

    suspend fun getProductRecommendations(
        userId: Long
    ) : ResultWrapper<List<ProductDTO>> {
        return try {
            Log.d(TAG, "[getProductRecommendations] Fetching recommendations")
            val response = service.getProductRecommendations(userId)
            if(response.isSuccessful) {
                Log.d(TAG, "[getProductRecommendations] Found successfully")
                ResultWrapper.Success(
                    response.body()?.products ?: emptyList()
                )
            } else {
                Log.e(TAG, "[getProductRecommendations] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[getProductRecommendations] Failed", e)
            ResultWrapper.ConnectionError
        }
    }
}