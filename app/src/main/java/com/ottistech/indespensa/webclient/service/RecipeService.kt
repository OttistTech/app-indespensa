package com.ottistech.indespensa.webclient.service

import com.ottistech.indespensa.webclient.dto.recipe.RateRecipeRequestDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeDetailsDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeService {

    @GET("recipes/{recipe_id}/details")
    suspend fun getRecipeDetails(
        @Path("recipe_id") recipeId: Long,
        @Query("userId") userId: Long
    ): Response<RecipeDetailsDTO>

    @POST("recipes/{recipe_id}/rating")
    suspend fun rateRecipe(
        @Path("recipe_id") recipeId: Long,
        @Body rateRecipeRequestDTO: RateRecipeRequestDTO
    ): Response<Any>
}