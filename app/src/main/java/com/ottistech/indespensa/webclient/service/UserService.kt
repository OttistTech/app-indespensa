package com.ottistech.indespensa.webclient.service

import com.ottistech.indespensa.webclient.dto.UserCreateDTO
import com.ottistech.indespensa.webclient.dto.UserCredentialsDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("users/signup")
    suspend fun create (
        @Body user: UserCreateDTO
    ) : Response<UserCredentialsDTO>
}