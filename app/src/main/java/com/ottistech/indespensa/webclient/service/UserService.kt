package com.ottistech.indespensa.webclient.service

import com.ottistech.indespensa.webclient.dto.user.UserCreateDTO
import com.ottistech.indespensa.webclient.dto.user.UserCredentialsDTO
import com.ottistech.indespensa.webclient.dto.user.UserFullIDTO
import com.ottistech.indespensa.webclient.dto.user.UserLoginDTO
import com.ottistech.indespensa.webclient.dto.user.UserUpdateDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
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
    suspend fun getUser(
        @Body user: UserLoginDTO
    ) : Response<UserCredentialsDTO>

    @GET("users/{id}")
    suspend fun getUserFullInfo(
        @Path("id") userId: Long,
        @Query("full-info") fullInfo: Boolean = true
    ) : Response<UserFullIDTO>

    @PUT("users/update/{id}")
    suspend fun updateUser(
        @Path("id") userId: Long,
        @Body updateUserDTO: UserUpdateDTO
    ): Response<UserCredentialsDTO>

    @DELETE("users/deactivation/{id}")
    suspend fun deactivateUser(
        @Path("id") userId: Long
    ) : Response<Any>

}