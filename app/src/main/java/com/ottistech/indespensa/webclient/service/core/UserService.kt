package com.ottistech.indespensa.webclient.service.core

import com.ottistech.indespensa.webclient.dto.user.UserCreateDTO
import com.ottistech.indespensa.webclient.dto.user.UserCredentialsDTO
import com.ottistech.indespensa.webclient.dto.user.UserFullDTO
import com.ottistech.indespensa.webclient.dto.user.UserLoginDTO
import com.ottistech.indespensa.webclient.dto.user.UserUpdateDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {

    @POST("users/signup")
    suspend fun create (
        @Body user: UserCreateDTO
    ) : Response<UserCredentialsDTO>

    @POST("users/login")
    suspend fun login (
        @Body user: UserLoginDTO
    ) : Response<UserCredentialsDTO>

    @GET("users/{id}")
    suspend fun getUserData (
        @Path("id") userId: Long,
        @Query("full-info") fullInfo: Boolean = true,
        @Header("Authorization") token: String
    ) : Response<UserFullDTO>

    @PUT("users/update/{id}")
    suspend fun update (
        @Path("id") userId: Long,
        @Body updateUserDTO: UserUpdateDTO,
        @Header("Authorization") token: String
    ): Response<UserCredentialsDTO>

    @DELETE("users/deactivation/{id}")
    suspend fun deactivate (
        @Path("id") userId: Long,
        @Header("Authorization") token: String
    ) : Response<Any>

    @PATCH("users/{id}")
    suspend fun switchPremium (
        @Path("id") userId: Long,
        @Header("Authorization") token: String
    ) : Response<Any>
}