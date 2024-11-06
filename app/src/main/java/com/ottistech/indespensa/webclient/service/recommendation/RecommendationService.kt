package com.ottistech.indespensa.webclient.service.recommendation

import com.ottistech.indespensa.webclient.dto.recommendation.ProductRecommendationDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RecommendationService {

    @GET("products/recommendation")
    suspend fun getProductRecommendations(
        @Query("userId") userId: Long
    ) : Response<ProductRecommendationDTO>

}