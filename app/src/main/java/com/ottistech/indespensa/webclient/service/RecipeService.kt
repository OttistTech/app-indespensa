package com.ottistech.indespensa.webclient.service

import com.ottistech.indespensa.webclient.dto.recipe.RecipeCreateDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeFullDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RecipeService {

    @POST("recipes/create")
    suspend fun create(
        @Body recipe: RecipeCreateDTO
    ) : Response<RecipeFullDTO>
}