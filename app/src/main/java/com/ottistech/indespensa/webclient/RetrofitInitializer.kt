package com.ottistech.indespensa.webclient

import com.google.gson.GsonBuilder
import com.ottistech.indespensa.shared.RecipeLevel
import com.ottistech.indespensa.webclient.adapters.RecipeLevelAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitializer {

    val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .registerTypeAdapter(RecipeLevel::class.java, RecipeLevelAdapter())
        .create()

    private fun getRetrofit(url: String) : Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun <T> getCoreService(clazz: Class<T>) : T {
        return getRetrofit(ApiConstants.BASE_URL).create(clazz)
    }

    fun <T> getRecommendationService(clazz: Class<T>) : T {
        return getRetrofit(ApiConstants.RECOMMENDATION_URL).create(clazz)
    }

}