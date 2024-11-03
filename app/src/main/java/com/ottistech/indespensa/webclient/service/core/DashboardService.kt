package com.ottistech.indespensa.webclient.service.core

import com.ottistech.indespensa.webclient.dto.dashboard.PersonalDashboardDTO
import com.ottistech.indespensa.webclient.dto.dashboard.ProfileDashboardDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface DashboardService {

    @GET("dashboard/{user_id}/personal")
    suspend fun getPersonalData(
        @Path("user_id") userId: Long,
        @Header("Authorization") token: String
    ) : Response<PersonalDashboardDTO>

    @GET("dashboard/{user_id}/profile")
    suspend fun getProfileData(
        @Path("user_id") userId: Long,
        @Header("Authorization") token: String
    ) : Response<ProfileDashboardDTO>
}