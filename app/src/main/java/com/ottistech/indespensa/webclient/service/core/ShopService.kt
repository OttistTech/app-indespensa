package com.ottistech.indespensa.webclient.service.core

import com.ottistech.indespensa.webclient.dto.product.ProductItemUpdateAmountDTO
import com.ottistech.indespensa.webclient.dto.shoplist.PurchaseDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemCreateDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemDetailsDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemPartialDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ShopService {

    @POST("shop/{user_id}/add")
    suspend fun addItem(
        @Path("user_id") userId: Long,
        @Body listItem: ShopItemCreateDTO
    ) : Response<ShopItemPartialDTO>

    @GET("shop/{user_id}/list")
    suspend fun listItems(
        @Path("user_id") userId: Long
    ) : Response<List<ShopItemPartialDTO>>

    @PATCH("shop/update-items-amount")
    suspend fun updateItemsAmount(
        @Body pantryItems: List<ProductItemUpdateAmountDTO>
    ) : Response<Void>

    @GET("shop/{pantry_item_id}/details")
    suspend fun getItemDetails(
        @Path("pantry_item_id") pantryItemId: Long
    ) : Response<ShopItemDetailsDTO>

    @GET("shop/{user_id}/list/history")
    suspend fun getHistory(
        @Path("user_id") userId: Long
    ) : Response<List<PurchaseDTO>>
}