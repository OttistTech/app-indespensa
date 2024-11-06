package com.ottistech.indespensa.data.repository

import android.content.Context
import com.ottistech.indespensa.data.datasource.ShopRemoteDatasource
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.exception.ResourceUnauthorizedException
import com.ottistech.indespensa.shared.getCurrentUser
import com.ottistech.indespensa.webclient.dto.product.ProductItemUpdateAmountDTO
import com.ottistech.indespensa.webclient.dto.shoplist.PurchaseDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemCreateDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemDetailsDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemPartialDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class ShopRepository(
    private val context: Context
) {

    private val remoteDataSource = ShopRemoteDatasource()

    suspend fun add(productId: Long) : Boolean {
        val currentUser = context.getCurrentUser()
        val result =
            remoteDataSource.add(currentUser.userId, ShopItemCreateDTO(productId), currentUser.token)
        return when(result) {
            is ResultWrapper.Success -> {
                result.value
            }
            is ResultWrapper.Error -> {
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    HttpURLConnection.HTTP_UNAUTHORIZED ->
                        throw ResourceUnauthorizedException(result.error)
                    else -> false
                }
            }
            else -> false
        }
    }

    suspend fun list() : List<ShopItemPartialDTO>? {
        val currentUser = context.getCurrentUser()
        val result =
            remoteDataSource.list(currentUser.userId, currentUser.token)
        return when(result) {
            is ResultWrapper.Success -> {
                result.value
            }
            is ResultWrapper.Error -> {
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    HttpURLConnection.HTTP_UNAUTHORIZED ->
                        throw ResourceUnauthorizedException(result.error)
                    else -> throw Exception(result.error)
                }
            }
            else -> throw Exception("Error while listing shop items")
        }
    }

    suspend fun getDetails(
        itemId: Long
    ): ShopItemDetailsDTO {
        val token = context.getCurrentUser().token
        val result =
            remoteDataSource.getDetails(itemId, token)
        return when(result) {
            is ResultWrapper.Success -> {
                result.value
            }
            is ResultWrapper.Error -> {
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    HttpURLConnection.HTTP_UNAUTHORIZED ->
                        throw ResourceUnauthorizedException(result.error)
                    else -> throw Exception(result.error)
                }
            }
            else -> throw Exception("Error while getting item details")
        }
    }

    suspend fun updateItemsAmount(
        vararg items: ProductItemUpdateAmountDTO
    ) : Boolean {
        return if(items.isNotEmpty()) {
            val token = context.getCurrentUser().token
            val result =
                remoteDataSource.updateAmount(items.asList(), token)
            if(result is ResultWrapper.Success) {
                result.value
            } else {
                false
            }
        } else {
            false
        }
    }

    suspend fun getHistory(): List<PurchaseDTO>? {
        val currentUser = context.getCurrentUser()
        val result =
            remoteDataSource.getHistory(currentUser.userId, currentUser.token)
        return when(result) {
            is ResultWrapper.Success -> {
                result.value
            }
            is ResultWrapper.Error -> {
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    HttpURLConnection.HTTP_UNAUTHORIZED ->
                        throw ResourceUnauthorizedException(result.error)
                    else -> throw Exception(result.error)
                }
            }
            else -> throw Exception("Error while fetching shop history")
        }
    }
}
