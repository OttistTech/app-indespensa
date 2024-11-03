package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemCreateDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemFullDTO
import com.ottistech.indespensa.webclient.dto.product.ProductDTO
import com.ottistech.indespensa.webclient.dto.recommendation.ProductRecommendationDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.core.PantryService
import com.ottistech.indespensa.webclient.service.recommendation.RecommendationService
import org.json.JSONObject
import java.net.HttpURLConnection

class RecommendationRemoteDataSource {

    private val TAG = "RECOMMENDATION REMOTE DATASOURCE"
    private val service : RecommendationService =
        RetrofitInitializer().getRecommendationService(RecommendationService::class.java)

    suspend fun getProductRecommendations(userId: Long) : ResultWrapper<List<ProductDTO>> {
        try {
            Log.d(TAG, "[getProductRecommendations] Trying to get recommendations")
            val response = service.getProductRecommendations(userId)
            return if(response.isSuccessful) {
                Log.d(TAG, "[getProductRecommendations] Found recommendations")
                ResultWrapper.Success(
                    response.body()?.products ?: emptyList()
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[getProductRecommendations] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    }
                    else -> {
                        Log.e(TAG, "[getProductRecommendations] A not mapped error occurred")
                        ResultWrapper.Error(null, "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[getProductRecommendations] Failed while fetching recommendations", e)
            return ResultWrapper.NetworkError
        }
    }

}