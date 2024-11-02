package com.ottistech.indespensa.webclient.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CategoryService {

    @GET("categories/list")
    suspend fun listCategories(
        @Query("match") pattern: String,
        @Header("Authorization") token: String
    ) : Response<List<String>>

}