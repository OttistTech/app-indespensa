package com.ottistech.indespensa.data.repository

import android.util.Log
import com.ottistech.indespensa.data.datasource.ShopListRemoteDataSource
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.webclient.dto.ShopListIngredientResponseDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class ShopListRepository {

    private val TAG = "SHOP_LIST REPOSITORY"
    private val remoteDataSource = ShopListRemoteDataSource()

    suspend fun listShopItems(userId: Long) : List<ShopListIngredientResponseDTO> {
        Log.d(TAG, "[listShopItems] Trying to get all shop items")
        val result : ResultWrapper<List<ShopListIngredientResponseDTO>> = remoteDataSource.listShopItems(userId)

        when(result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[listShopItems] Found shop items successfully")
                return result.value
            }

            is ResultWrapper.Error -> {
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        throw ResourceNotFoundException(result.error)
                    }
                    else -> throw Exception(result.error)
                }
            }
            else -> throw Exception()
        }
    }

}