package com.ottistech.indespensa.data.repository

import android.content.Context
import android.util.Log
import com.ottistech.indespensa.data.datasource.UserFirebaseDataSource
import com.ottistech.indespensa.data.datasource.UserLocalDataSource
import com.ottistech.indespensa.data.datasource.UserRemoteDataSource
import com.ottistech.indespensa.data.exception.AuthenticationException
import com.ottistech.indespensa.data.exception.BadRequestException
import com.ottistech.indespensa.data.exception.FieldConflictException
import com.ottistech.indespensa.data.exception.ResourceGoneException
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.exception.ResourceUnauthorizedException
import com.ottistech.indespensa.ui.helpers.getCurrentUser
import com.ottistech.indespensa.webclient.dto.user.UserCreateDTO
import com.ottistech.indespensa.webclient.dto.user.UserCredentialsDTO
import com.ottistech.indespensa.webclient.dto.user.UserFullIDTO
import com.ottistech.indespensa.webclient.dto.user.UserLoginDTO
import com.ottistech.indespensa.webclient.dto.user.UserUpdateDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class UserRepository (
    private val context: Context
) {

    private val TAG = "USER REPOSITORY"
    private val firebaseDataSource = UserFirebaseDataSource(context)
    private val remoteDataSource = UserRemoteDataSource()
    private val localDataSource = UserLocalDataSource(context)

    fun isUserAuthenticated() : Boolean {
        val result = firebaseDataSource.isUserAuthenticated()
        Log.d(TAG, "[isUserAuthenticated] User authenticated: $result")
        return result
    }

    suspend fun signupUser(user: UserCreateDTO) : Boolean {
        Log.d(TAG, "[signupUser] Trying to sign user up with $user")
        val result : ResultWrapper<UserCredentialsDTO> = remoteDataSource.create(user)

        return when(result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[signupUser] Signed up user successfully: ${result.value}")
                firebaseDataSource.saveUser(
                    email = user.email,
                    password = user.password
                )
                localDataSource.saveUser(result.value)
                true
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[signupUser] Error while signing up user: $result")
                when(result.code) {
                    HttpURLConnection.HTTP_CONFLICT -> {
                        throw FieldConflictException(result.error)
                    }
                    else -> false
                }
            }
            else -> {
                Log.e(TAG, "[signupUser] Unexpected result: $result")
                false
            }
        }
    }

    suspend fun loginUser(user: UserLoginDTO) : Boolean {
        Log.d(TAG, "[loginUser] Trying to log user in with credentials $user")
        val result : ResultWrapper<UserCredentialsDTO> = remoteDataSource.loginUser(user)

        return when(result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[loginUser] Logging user successfully: ${result.value}")
                firebaseDataSource.loginUser(
                    email = user.email,
                    password = user.password
                )
                localDataSource.saveUser(result.value)
                true
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[loginUser] Error while logging user: ${result.error}, code: ${result.code}")
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        throw ResourceNotFoundException(result.error)
                    }
                    HttpURLConnection.HTTP_UNAUTHORIZED -> {
                        throw ResourceUnauthorizedException(result.error)
                    }
                    HttpURLConnection.HTTP_GONE -> {
                        throw ResourceGoneException(result.error)
                    }
                    else -> false
                }
            }
            else -> {
                Log.e(TAG, "[loginUser] Unexpected result: $result")
                false
            }
        }
    }

    suspend fun getUserInfo(userId: Long, fullInfo: Boolean) : UserFullIDTO? {
        Log.d(TAG, "[getUserInfo] Trying to find user $userId info")
        val result: ResultWrapper<UserFullIDTO> = remoteDataSource.getUserFullInfo(userId, fullInfo)

        return when (result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[getUserInfo] Successfully retrieved user info: ${result.value}")
                result.value
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[getUserInfo] Error retrieving user info: ${result.error}, code: ${result.code}")
                when (result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        throw ResourceNotFoundException(result.error)
                    }
                    HttpURLConnection.HTTP_UNAUTHORIZED -> {
                        throw ResourceUnauthorizedException(result.error)
                    }
                    else -> null
                }
            }
            else -> {
                Log.e(TAG, "[getUserInfo] Unexpected result: $result")
                null
            }
        }
    }

    suspend fun updateUser(
        userId: Long,
        user: UserUpdateDTO
    ) : UserCredentialsDTO? {
        Log.d(TAG, "[updateUser] Trying to update user $userId with $user")
        val result: ResultWrapper<UserCredentialsDTO> = remoteDataSource.updateUser(userId, user)

        return when(result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[updateUser] Successfully retrieved user info: ${result.value}")
                firebaseDataSource.updateUser(
                    getUserCredentials().email,
                    getUserCredentials().password,
                    user.email,
                    user.password
                )
                localDataSource.saveUser(result.value)
                result.value
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[updateUser] Error retrieving user info: ${result.error}, code: ${result.code}")
                when (result.code) {
                    HttpURLConnection.HTTP_CONFLICT -> {
                        throw FieldConflictException(result.error)
                    }
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        throw ResourceNotFoundException(result.error)
                    }
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        throw BadRequestException(result.error)
                    }
                    else -> null
                }
            }
            else -> {
                Log.e(TAG, "[updateUser] Unexpected result: $result")
                null
            }
        }
    }

    fun logoutUser() {
        Log.d(TAG, "[logoutUser] logging user out")
        firebaseDataSource.logoutUser()
        localDataSource.removeUser()
    }

    suspend fun deactivateUser(userId: Long) : Boolean {
        Log.d(TAG, "[deactivateUser] Trying to deactivate user $userId")
        val result: ResultWrapper<Any> = remoteDataSource.deactivateUser(userId)

        return when (result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[deactivateUser] User deactivated successfully")
                localDataSource.removeUser()
                true
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[deactivateUser] Error deactivating user: ${result.error}, code: ${result.code}")
                when (result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        throw ResourceNotFoundException(result.error)
                    }
                    HttpURLConnection.HTTP_UNAUTHORIZED -> {
                        throw ResourceUnauthorizedException(result.error)
                    }
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        throw BadRequestException(result.error)
                    }
                    else -> false
                }
            }
            else -> {
                Log.e(TAG, "[deactivateUser] Unexpected result: $result")
                false
            }
        }
    }

    fun getUserCredentials() : UserCredentialsDTO {
        Log.d(TAG, "[getUserCredentials] Trying to get current user credentials")
        val user = localDataSource.getUser()
        if(user != null) {
            Log.d(TAG, "[getUserCredentials] User credentials: $user")
            return user
        }
        Log.e(TAG, "[getUserCredentials] Failed while getting current user credentials")
        throw AuthenticationException()
    }

    suspend fun updateUserBecomePremium() : Boolean {
        val userId = context.getCurrentUser().userId

        val result: ResultWrapper<Any> = remoteDataSource.updateUserBecomePremium(userId)

        return when (result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[updateUserBecomePremium] User switched to premium successfully")

                if (getUserCredentials().isPremium) {
                    localDataSource.saveUser(getUserCredentials().copy(
                        isPremium = false
                    ))
                } else {
                    localDataSource.saveUser(getUserCredentials().copy(
                        isPremium = true
                    ))
                }

                true
            }
            is ResultWrapper.Error -> {
                Log.e(TAG, "[updateUserBecomePremium] Error switching user plan: ${result.error}, code: ${result.code}")
                when (result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        throw ResourceNotFoundException(result.error)
                    }
                    HttpURLConnection.HTTP_GONE -> {
                        throw ResourceGoneException(result.error)
                    }
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        throw BadRequestException(result.error)
                    }
                    else -> false
                }
            }
            else -> {
                Log.e(TAG, "[updateUserBecomePremium] Unexpected result: $result")
                false
            }
        }
    }

}