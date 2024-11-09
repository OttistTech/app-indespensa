package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.address.Address
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.core.AddressService

class AddressRemoteDataSource {

    private val TAG = "ADDRESS REMOTE DATASOURCE"
    private val service : AddressService =
        RetrofitInitializer().getCoreService(AddressService::class.java)

    suspend fun get(
        cep: String
    ) : ResultWrapper<Address> {
        return try {
            Log.d(TAG, "[getAddress] Fetching address with CEP: $cep")
            val response = service.getCepAddress(cep)
            if(response.isSuccessful) {
                Log.d(TAG, "[getAddress] Found successfully: ${response.body()?.street}")
                ResultWrapper.Success(
                    response.body() as Address
                )
            } else {
                Log.e(TAG, "[getAddress] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[getAddress] Failed", e)
            ResultWrapper.ConnectionError
        }
    }
}