package com.ottistech.indespensa.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.ottistech.indespensa.data.DataConstants
import com.ottistech.indespensa.data.datasource.ImageFirebaseDatasource
import com.ottistech.indespensa.data.datasource.PantryRemoteDatasource
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.exception.ResourceUnauthorizedException
import com.ottistech.indespensa.ui.helpers.getCurrentUser
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemCloseValidityDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemCreateDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemDetailsDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemPartialDTO
import com.ottistech.indespensa.webclient.dto.product.ProductItemUpdateAmountDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection
import java.util.Date

class PantryRepository(
    private val context: Context
) {

    private val remoteDataSource = PantryRemoteDatasource()
    private val imageDatasource = ImageFirebaseDatasource(DataConstants.FIREBASE_STORAGE_PRODUCTS)

    suspend fun create(
        pantryItem: PantryItemCreateDTO,
        imageBitmap: Bitmap?
    ) : Boolean {
        if (pantryItem.productImageUrl == null && imageBitmap != null) {
            pantryItem.productImageUrl = imageDatasource.upload(imageBitmap)
        }
        val currentUser = context.getCurrentUser()
        val result =
            remoteDataSource.create(currentUser.userId, pantryItem, currentUser.token)
        return when(result) {
            is ResultWrapper.Success -> {
                result.value
            }
            is ResultWrapper.Error -> {
                when(result.code) {
                    HttpURLConnection.HTTP_UNAUTHORIZED ->
                        throw ResourceUnauthorizedException(result.error)
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    else ->
                        false
                }
            }
            else -> false
        }
    }

    suspend fun list() : List<PantryItemPartialDTO> {
        val currentUser = context.getCurrentUser()
        val result =
            remoteDataSource.list(currentUser.userId, currentUser.token)
        return when(result) {
            is ResultWrapper.Success -> {
                result.value
            }
            is ResultWrapper.Error -> {
                when(result.code) {
                    HttpURLConnection.HTTP_UNAUTHORIZED ->
                        throw ResourceUnauthorizedException(result.error)
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    else ->
                        throw Exception(result.error)
                }
            }
            else -> {
                throw Exception("Error while listing pantry items")
            }
        }
    }

    suspend fun updateAmount(vararg items: ProductItemUpdateAmountDTO) : Boolean {
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

    suspend fun getDetails(itemId: Long) : PantryItemDetailsDTO {
        val token = context.getCurrentUser().token
        val result =
            remoteDataSource.getDetails(itemId, token)
        when(result) {
            is ResultWrapper.Success -> {
                return result.value
            }
            is ResultWrapper.Error -> {
                when(result.code) {
                    HttpURLConnection.HTTP_UNAUTHORIZED ->
                        throw ResourceUnauthorizedException(result.error)
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    else ->
                        throw Exception(result.error)
                }
            }
            else -> {
                throw Exception("Error while getting pantry item details")
            }
        }
    }

    suspend fun addShopItem(
        shopItemId: Long,
        validityDate: Date
    ) : Boolean {
        val currentUser = context.getCurrentUser()
        val result =
            remoteDataSource.addShopItem(currentUser.userId, shopItemId, validityDate, currentUser.token)
        when (result) {
            is ResultWrapper.Success -> {
                return result.value
            }
            is ResultWrapper.Error -> {
                when(result.code) {
                    HttpURLConnection.HTTP_UNAUTHORIZED ->
                        throw ResourceUnauthorizedException(result.error)
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    else ->
                        throw Exception(result.error)
                }
            }
            else -> {
                throw Exception("Error while adding item")
            }
        }
    }

    suspend fun addAllShopItems(): Boolean {
        val currentUser = context.getCurrentUser()
        val result =
            remoteDataSource.addAllShopItems(currentUser.userId, currentUser.token)
        return when (result) {
            is ResultWrapper.Success -> {
                result.value
            }
            is ResultWrapper.Error -> {
                when(result.code) {
                    HttpURLConnection.HTTP_UNAUTHORIZED ->
                        throw ResourceUnauthorizedException(result.error)
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    else ->
                        throw Exception(result.error)
                }
            }
            else -> {
                throw Exception("Error while adding all items to pantry")
            }
        }
    }

    suspend fun listCloseValidityItems() : List<PantryItemCloseValidityDTO> {
        val currentUser = context.getCurrentUser()
        val result =
            remoteDataSource.listCloseValidityItems(currentUser.userId, currentUser.token)
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
                    else ->
                        throw Exception(result.error)
                }
            }
            else -> {
                throw Exception("Error while listing close validity pantry items")
            }
        }
    }
}