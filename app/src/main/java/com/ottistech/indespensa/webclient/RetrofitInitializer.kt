package com.ottistech.indespensa.webclient

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitializer {

    val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
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