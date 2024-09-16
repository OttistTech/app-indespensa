package com.ottistech.indespensa.webclient.service

import com.ottistech.indespensa.webclient.dto.PantryItemCreateDTO
import com.ottistech.indespensa.webclient.dto.PantryItemDTO
import com.ottistech.indespensa.webclient.dto.PantryItemPartialDTO
import com.ottistech.indespensa.webclient.dto.PantryItemUpdateDTO
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
    ) : Response<PantryItemDTO>

    @GET("pantry/{user_id}/list")
    suspend fun listItems(
        @Path("user_id") userId: Long
    ) : Response<List<PantryItemPartialDTO>>

    @PATCH("pantry/update-items-amount")
    suspend fun updateItemsAmount(
        @Body pantryItems: List<PantryItemUpdateDTO>
    ) : Response<List<PantryItemUpdateDTO>>
}