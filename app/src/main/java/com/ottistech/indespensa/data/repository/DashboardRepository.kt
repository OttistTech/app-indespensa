package com.ottistech.indespensa.data.repository

import android.content.Context
import com.ottistech.indespensa.data.datasource.DashboardRemoteDataSource
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.exception.ResourceUnauthorizedException
import com.ottistech.indespensa.shared.getCurrentUser
import com.ottistech.indespensa.webclient.dto.dashboard.PersonalDashboardDTO
import com.ottistech.indespensa.webclient.dto.dashboard.ProfileDashboardDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class DashboardRepository (
    private val context: Context
) {

    private val remoteDataSource = DashboardRemoteDataSource()

    suspend fun getPersonalData() : PersonalDashboardDTO {
        val currentUser = context.getCurrentUser()
        val result =
            remoteDataSource.getPersonalData(currentUser.userId, currentUser.token)
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
                throw Exception("Error while fetching dashboard")
            }
        }
    }

    suspend fun getProfileData() : ProfileDashboardDTO {
        val currentUser = context.getCurrentUser()
        val result =
            remoteDataSource.getProfileData(currentUser.userId, currentUser.token)
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
                throw Exception("Error while fetching dashboard")
            }
        }
    }
}