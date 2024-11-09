package com.ottistech.indespensa.data.repository

import com.ottistech.indespensa.data.datasource.AddressRemoteDataSource
import com.ottistech.indespensa.data.exception.BadRequestException
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.webclient.dto.address.Address
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class AddressRepository {

    private val remoteDataSource = AddressRemoteDataSource()

    suspend fun get(cep: String) : Address {
        val result =
            remoteDataSource.get(cep)
        when(result) {
            is ResultWrapper.Success -> {
                return result.value
            }
            is ResultWrapper.Error -> {
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    HttpURLConnection.HTTP_BAD_REQUEST ->
                        throw BadRequestException(result.error)
                    else ->
                        throw Exception(result.error)
                }
            }
            else -> throw Exception("Error while getting address")
        }
    }
}