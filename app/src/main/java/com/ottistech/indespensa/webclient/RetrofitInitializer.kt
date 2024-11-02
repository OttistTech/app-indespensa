package com.ottistech.indespensa.webclient

import com.google.gson.GsonBuilder
import com.ottistech.indespensa.shared.RecipeLevel
import com.ottistech.indespensa.webclient.adapters.RecipeLevelAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitializer {

    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .registerTypeAdapter(RecipeLevel::class.java, RecipeLevelAdapter())
        .create()

    private fun getRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun <T> getService(clazz: Class<T>) : T {
        return getRetrofit().create(clazz)
    }

}