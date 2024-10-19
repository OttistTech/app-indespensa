package com.ottistech.indespensa.data.repository

import android.content.Context
import android.util.Log
import com.ottistech.indespensa.data.datasource.DashboardRemoteDataSource
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.ui.helpers.getCurrentUser
import com.ottistech.indespensa.webclient.dto.dashboard.PersonalDashboardDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper

class DashboardRepository (
    private val context: Context
) {

    private val TAG = "DASHBOARD REPOSITORY"
    private val remoteDataSource = DashboardRemoteDataSource()
    suspend fun getPersonalData() : PersonalDashboardDTO {
        val userId = context.getCurrentUser().userId
        Log.d(TAG, "[getPersonalData] Trying to get data for user $userId")
        val result : ResultWrapper<PersonalDashboardDTO> = remoteDataSource.getPersonalData(userId)
        return when(result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[getPersonalData] Found data successfully")
                result.value
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[getPersonalData] Error while getting data: $result")
                throw ResourceNotFoundException("Could not find dashboard data")
            }
            else -> {
                Log.e(TAG, "[getPersonalData] Unexpected error occurred while getting data")
                throw Exception("Could not fetch dashboard data")
            }
        }
    }
}