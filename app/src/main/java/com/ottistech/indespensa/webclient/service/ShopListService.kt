package com.ottistech.indespensa.webclient.service

import com.ottistech.indespensa.webclient.dto.ShopListIngredientResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ShopListService {

    @GET("shopping-item/{user_id}/list")
    suspend fun getShopItemList(
        @Path("user_id") userId: Long
    ) : Response<List<ShopListIngredientResponseDTO>>

}