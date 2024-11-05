package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.user.UserCreateDTO
import com.ottistech.indespensa.webclient.dto.user.UserCredentialsDTO
import com.ottistech.indespensa.webclient.dto.user.UserFullDTO
import com.ottistech.indespensa.webclient.dto.user.UserLoginDTO
import com.ottistech.indespensa.webclient.dto.user.UserUpdateDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.core.UserService

class UserRemoteDataSource {

    private val TAG = "USER REMOTE DATASOURCE"
    private val service : UserService =
        RetrofitInitializer().getCoreService(UserService::class.java)

    suspend fun create(
        user: UserCreateDTO
    ) : ResultWrapper<UserCredentialsDTO> {
        return try {
            Log.d(TAG, "[create] Trying to create user with: $user")
            val response = service.create(user)
            if(response.isSuccessful) {
                Log.d(TAG, "[create] User successfully")
                ResultWrapper.Success(
                    response.body() as UserCredentialsDTO
                )
            } else {
                Log.e(TAG, "[create] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[create] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun login(
        userInfo : UserLoginDTO
    ) : ResultWrapper<UserCredentialsDTO> {
        return try {
            Log.d(TAG, "[login] Trying to log user in with: $userInfo")
            val response = service.getUser(userInfo)
            if (response.isSuccessful) {
                Log.d(TAG, "[login] Logged in successfully")
                ResultWrapper.Success(
                    response.body() as UserCredentialsDTO
                )
            } else {
                Log.e(TAG, "[login] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[login] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun getData(
        userId: Long,
        fullInfo: Boolean,
        token: String
    ) : ResultWrapper<UserFullDTO> {
        try {
            Log.d(TAG, "[getData] Fetching user info")
            val response = service.getUserFullInfo(userId, fullInfo, token)
            return if (response.isSuccessful) {
                Log.d(TAG, "[getData] Found successfully")
                ResultWrapper.Success(
                    response.body() as UserFullDTO
                )
            } else {
                Log.e(TAG, "[getData] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[getData] Failed", e)
            return ResultWrapper.ConnectionError
        }
    }

    suspend fun update(
        userId: Long,
        user: UserUpdateDTO,
        token: String
    ): ResultWrapper<UserCredentialsDTO> {
        return try {
            Log.d(TAG, "[update] Trying to update user with: $user")
            val response = service.updateUser(userId, user, token)
            if (response.isSuccessful) {
                Log.d(TAG, "[update] Updated successfully")
                ResultWrapper.Success(
                    response.body() as UserCredentialsDTO
                )
            } else {
                Log.e(TAG, "[update] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[update] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun deactivate(
        userId: Long,
        token: String
    ) : ResultWrapper<Boolean> {
        return try {
            Log.d(TAG, "[deactivate] Deactivating user")
            val response = service.deactivateUser(userId, token)
            return if (response.isSuccessful) {
                Log.d(TAG, "[deactivate] Deactivated successfully")
                ResultWrapper.Success(true)
            } else {
                Log.e(TAG, "[deactivate] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[deactivate] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun switchPremium(
        userId: Long,
        token: String
    ) : ResultWrapper<Boolean> {
        return try {
            Log.d(TAG, "[switchPremium] Trying to switch user premium plan")
            val response = service.switchPremium(userId, token)
            return if (response.isSuccessful) {
                Log.d(TAG, "[switchPremium] Switched successfully")
                ResultWrapper.Success(true)
            } else {
                Log.e(TAG, "[switchPremium] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[switchPremium] Failed", e)
            ResultWrapper.ConnectionError
        }
    }
}
