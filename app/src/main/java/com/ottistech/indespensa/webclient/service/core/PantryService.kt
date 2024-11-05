package com.ottistech.indespensa.webclient.service.core

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
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface PantryService {

    @POST("pantry/{user_id}/create")
    suspend fun create(
        @Path("user_id") userId: Long,
        @Body pantryItem: PantryItemCreateDTO,
        @Header("Authorization") token: String
    ) : Response<PantryItemFullDTO>

    @GET("pantry/{user_id}/list")
    suspend fun list(
        @Path("user_id") userId: Long,
        @Header("Authorization") token: String
    ) : Response<List<PantryItemPartialDTO>>

    @PATCH("pantry/update-items-amount")
    suspend fun updateAmount(
        @Body pantryItems: List<ProductItemUpdateAmountDTO>,
        @Header("Authorization") token: String
    ) : Response<Void>

    @GET("pantry/{pantry_item_id}/details")
    suspend fun getDetails(
        @Path("pantry_item_id") pantryItemId: Long,
        @Header("Authorization") token: String
    ) : Response<PantryItemDetailsDTO>

    @POST("pantry/{user_id}/add")
    suspend fun add(
        @Path("user_id") userId: Long,
        @Body pantryItemAdd: PantryItemAddDTO,
        @Header("Authorization") token: String
    ) : Response<PantryItemFullDTO>

    @POST("pantry/{user_id}/add-all")
    suspend fun addAllShopItems(
        @Path("user_id") userId: Long,
        @Header("Authorization") token: String
    ) : Response<Any>

    @GET("pantry/{user_id}/soon-to-expire")
    suspend fun listCloseValidityItems(
        @Path("user_id") userId: Long,
        @Header("Authorization") token: String
    ) : Response<List<PantryItemCloseValidityDTO>>
}