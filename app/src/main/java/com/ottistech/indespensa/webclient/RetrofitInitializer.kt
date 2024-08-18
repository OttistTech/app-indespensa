package com.ottistech.indespensa.webclient

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitializer {

    private fun getRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> getService(clazz: Class<T>) : T {
        return getRetrofit().create(clazz)
    }

}