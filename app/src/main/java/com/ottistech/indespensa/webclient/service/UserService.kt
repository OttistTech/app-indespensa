package com.ottistech.indespensa.webclient.service

import com.ottistech.indespensa.webclient.dto.UserCreateDTO
import com.ottistech.indespensa.webclient.dto.UserCredentialsDTO
import com.ottistech.indespensa.webclient.dto.UserLoginDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {

    @POST("users/signup")
    suspend fun create (
        @Body user: UserCreateDTO
    ) : Response<UserCredentialsDTO>

    @POST("users/login")
    suspend fun getUser(
        @Body user: UserLoginDTO
    ) : Response<UserCredentialsDTO>
}