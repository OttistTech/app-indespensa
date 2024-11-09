package com.ottistech.indespensa.webclient.service.core

import retrofit2.Response
import com.ottistech.indespensa.webclient.dto.address.Address
import retrofit2.http.GET
import retrofit2.http.Path

interface AddressService {

    @GET("address/{cep}")
    suspend fun getCepAddress(
        @Path("cep") cep: String
    ) : Response<Address>

}