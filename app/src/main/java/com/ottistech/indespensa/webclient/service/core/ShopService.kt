package com.ottistech.indespensa.webclient.service.core

import com.ottistech.indespensa.webclient.dto.product.ProductItemUpdateAmountDTO
import com.ottistech.indespensa.webclient.dto.shoplist.PurchaseDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemCreateDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemDetailsDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemPartialDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ShopService {

    @POST("shop/{user_id}/add")
    suspend fun add(
        @Path("user_id") userId: Long,
        @Body listItem: ShopItemCreateDTO,
        @Header("Authorization") token: String
    ) : Response<ShopItemPartialDTO>

    @GET("shop/{user_id}/list")
    suspend fun list(
        @Path("user_id") userId: Long,
        @Header("Authorization") token: String
    ) : Response<List<ShopItemPartialDTO>>

    @PATCH("shop/update-items-amount")
    suspend fun updateAmount(
        @Body pantryItems: List<ProductItemUpdateAmountDTO>,
        @Header("Authorization") token: String
    ) : Response<Void>

    @GET("shop/{pantry_item_id}/details")
    suspend fun getDetails(
        @Path("pantry_item_id") pantryItemId: Long,
        @Header("Authorization") token: String
    ) : Response<ShopItemDetailsDTO>

    @GET("shop/{user_id}/list/history")
    suspend fun getHistory(
        @Path("user_id") userId: Long,
        @Header("Authorization") token: String
    ) : Response<List<PurchaseDTO>>
}