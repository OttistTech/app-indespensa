package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.dashboard.PersonalDashboardDTO
import com.ottistech.indespensa.webclient.dto.dashboard.ProfileDashboardDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.core.DashboardService
import org.json.JSONObject
import java.net.HttpURLConnection

class DashboardRemoteDataSource {

    private val TAG = "DASHBOARD REMOTE DATASOURCE"
    private val service : DashboardService =
        RetrofitInitializer().getCoreService(DashboardService::class.java)

    suspend fun getPersonalData(userId: Long) : ResultWrapper<PersonalDashboardDTO> {
        try {
            Log.d(TAG, "[getPersonalData] Trying to get personal dashboard data for $userId")
            val response = service.getPersonalData(userId)
            return if(response.isSuccessful) {
                Log.d(TAG, "[getPersonalData] Found personal dashboard data successfully $userId")
                ResultWrapper.Success(
                    response.body() as PersonalDashboardDTO
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[getPersonalData] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    } else -> {
                        Log.e(TAG, "[getPersonalData] A not mapped error occurred")
                        ResultWrapper.Error(null, "Unexpected Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[getPersonalData] Failed while getting personal dashboard data", e)
            return ResultWrapper.NetworkError
        }
    }

    suspend fun getProfileData(userId: Long) : ResultWrapper<ProfileDashboardDTO> {
        try {
            Log.d(TAG, "[getProfileData] Trying to get profile dashboard data for $userId")
            val response = service.getProfileData(userId)
            return if(response.isSuccessful) {
                Log.d(TAG, "[getProfileData] Found personal profile data successfully $userId")
                ResultWrapper.Success(
                    response.body() as ProfileDashboardDTO
                )
            } else {
                val error = JSONObject(response.errorBody()!!.string())
                when(response.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        val detail = error.get("detail").toString()
                        Log.e(TAG, "[getProfileData] $detail")
                        ResultWrapper.Error(response.code(), detail)
                    } else -> {
                    Log.e(TAG, "[getProfileData] A not mapped error occurred")
                    ResultWrapper.Error(null, "Unexpected Error")
                }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[getProfileData] Failed while getting profile dashboard data", e)
            return ResultWrapper.NetworkError
        }
    }

}