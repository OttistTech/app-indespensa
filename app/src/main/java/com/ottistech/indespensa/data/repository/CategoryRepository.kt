package com.ottistech.indespensa.data.repository

import android.util.Log
import com.ottistech.indespensa.data.datasource.CategoryRemoteDataSource
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.exception.ResourceUnauthorizedException
import com.ottistech.indespensa.webclient.dto.UserCredentialsDTO
import com.ottistech.indespensa.webclient.dto.UserLoginDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class CategoryRepository {

    private val TAG = "CATEGORY REPOSITORY"
    private val remoteDataSource = CategoryRemoteDataSource()

    suspend fun listCategories(pattern: String = "") : List<String> {
        Log.d(TAG, "[listCategories] Trying to fetch product categories")
        val result : ResultWrapper<List<String>> = remoteDataSource.listCategories(pattern)
        when(result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[listCategories] Found categories successfully")
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