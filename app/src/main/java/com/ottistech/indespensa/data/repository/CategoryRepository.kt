package com.ottistech.indespensa.data.repository

import android.content.Context
import com.ottistech.indespensa.data.datasource.CategoryRemoteDataSource
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.exception.ResourceUnauthorizedException
import com.ottistech.indespensa.ui.helpers.getCurrentUser
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class CategoryRepository (
    private val context: Context
) {

    private val remoteDataSource = CategoryRemoteDataSource()

    suspend fun list(pattern: String = "") : List<String> {
        val token = context.getCurrentUser().token
        val result =
            remoteDataSource.list(pattern, token)
        when(result) {
            is ResultWrapper.Success -> {
                return result.value
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
            else -> throw Exception("Error while listing categories")
        }
    }
}