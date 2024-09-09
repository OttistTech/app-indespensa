package com.ottistech.indespensa.webclient.service

import com.ottistech.indespensa.webclient.dto.PantryItemCreateDTO
import com.ottistech.indespensa.webclient.dto.PantryItemResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface PantryService {

    @POST("pantry/{user_id}/create")
    suspend fun createItem(
        @Path("user_id") userId: Long,
        @Body pantryItem: PantryItemCreateDTO
    ) : Response<PantryItemResponseDTO>
}