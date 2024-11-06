package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.dashboard.PersonalDashboardDTO
import com.ottistech.indespensa.webclient.dto.dashboard.ProfileDashboardDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.core.DashboardService

class DashboardRemoteDataSource {

    private val TAG = "DASHBOARD REMOTE DATASOURCE"
    private val service : DashboardService =
        RetrofitInitializer().getCoreService(DashboardService::class.java)

    suspend fun getPersonalData(
        userId: Long,
        token: String
    ) : ResultWrapper<PersonalDashboardDTO> {
        return try {
            Log.d(TAG, "[getPersonalData] Fetching personal dashboard data")
            val response = service.getPersonalData(userId, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[getPersonalData] Found successfully")
                ResultWrapper.Success(
                    response.body() as PersonalDashboardDTO
                )
            } else {
                Log.e(TAG, "[getPersonalData] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[getPersonalData] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun getProfileData(
        userId: Long,
        token: String
    ) : ResultWrapper<ProfileDashboardDTO> {
        return try {
            Log.d(TAG, "[getProfileData] Fetching profile dashboard data")
            val response = service.getProfileData(userId, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[getProfileData] Found successfully")
                ResultWrapper.Success(
                    response.body() as ProfileDashboardDTO
                )
            } else {
                Log.e(TAG, "[getProfileData] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[getProfileData] Failed", e)
            ResultWrapper.ConnectionError
        }
    }
}