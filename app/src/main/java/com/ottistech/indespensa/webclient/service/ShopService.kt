package com.ottistech.indespensa.webclient.service

import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemCreateDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemPartialDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ShopService {

    @POST("shop/{user_id}/add") //TODO: change endpoint after API refact
    suspend fun addItem(
        @Path("user_id") userId: Long,
        @Body listItem: ShopItemCreateDTO
    ) : Response<ShopItemPartialDTO>
}