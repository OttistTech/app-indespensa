package com.ottistech.indespensa.webclient.service

import com.ottistech.indespensa.webclient.dto.pantry.PantryItemAddDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemCloseValidityDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemCreateDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemFullDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemDetailsDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemPartialDTO
import com.ottistech.indespensa.webclient.dto.product.ProductItemUpdateAmountDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface PantryService {

    @POST("pantry/{user_id}/create")
    suspend fun createItem(
        @Path("user_id") userId: Long,
        @Body pantryItem: PantryItemCreateDTO
    ) : Response<PantryItemFullDTO>

    @GET("pantry/{user_id}/list")
    suspend fun listItems(
        @Path("user_id") userId: Long
    ) : Response<List<PantryItemPartialDTO>>

    @PATCH("pantry/update-items-amount")
    suspend fun updateItemsAmount(
        @Body pantryItems: List<ProductItemUpdateAmountDTO>
    ) : Response<List<ProductItemUpdateAmountDTO>>

    @GET("pantry/{pantry_item_id}/details")
    suspend fun getItemDetails(
        @Path("pantry_item_id") pantryItemId: Long
    ) : Response<PantryItemDetailsDTO>

    @POST("pantry/{user_id}/add")
    suspend fun addPantryItem(
        @Path("user_id") userId: Long,
        @Body pantryItemAdd: PantryItemAddDTO
    ) : Response<PantryItemFullDTO>

    @POST("pantry/{user_id}/add-all")
    suspend fun addAllShopItemsToPantry(
        @Path("user_id") userId: Long
    ) : Response<Any>

    @GET("pantry/{user_id}/soon-to-expire")
    suspend fun listCloseValidityItems(
        @Path("user_id") userId: Long
    ) : Response<List<PantryItemCloseValidityDTO>>
}