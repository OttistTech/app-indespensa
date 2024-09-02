package com.ottistech.indespensa.data.repository

import android.content.Context
import android.util.Log
import com.ottistech.indespensa.data.datasource.UserFirebaseDataSource
import com.ottistech.indespensa.data.datasource.UserRemoteDataSource
import com.ottistech.indespensa.data.exception.BadRequestException
import com.ottistech.indespensa.data.exception.FieldConflictException
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.exception.ResourceUnauthorizedException
import com.ottistech.indespensa.webclient.dto.UserCreateDTO
import com.ottistech.indespensa.webclient.dto.UserCredentialsDTO
import com.ottistech.indespensa.webclient.dto.UserFullCredentialsDTO
import com.ottistech.indespensa.webclient.dto.UserLoginDTO
import com.ottistech.indespensa.webclient.dto.UserUpdateDTO
import com.ottistech.indespensa.webclient.dto.UserUpdateResponseDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class UserRepository (
    context: Context
) {

    private val TAG = "USER REPOSITORY"
    private val firebaseDataSource = UserFirebaseDataSource(context)
    private val remoteDataSource = UserRemoteDataSource()

    fun isUserAuthenticated() : Boolean {
        val result = firebaseDataSource.isUserAuthenticated()
        Log.d(TAG, "[isUserAuthenticated] User authenticated? $result")

        return result
    }

    suspend fun signupUser(user: UserCreateDTO) : Boolean {
        val result : ResultWrapper<UserCredentialsDTO> = remoteDataSource.create(user)

        return when(result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[signupUser] Signed up user successfully: ${result.value}")
                firebaseDataSource.createUser(
                    email = user.email,
                    password = user.password
                )
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
            else -> false
        }
    }

    suspend fun loginUser(user: UserLoginDTO) : Boolean {
        val result : ResultWrapper<UserCredentialsDTO> = remoteDataSource.loginUser(user)
        return when(result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[getUser] Logging user successfully: ${result.value}")
                firebaseDataSource.loginUser(
                    email = user.email,
                    password = user.password
                )
                true
            }

            is ResultWrapper.Error -> {
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        throw ResourceNotFoundException(result.error)
                    }
                    HttpURLConnection.HTTP_UNAUTHORIZED -> {
                        throw ResourceUnauthorizedException(result.error)
                    }

                    else -> false
                }
            }
            else -> false
        }
    }

    suspend fun infoUserDetails(userId: Long, fullInfo: Boolean) : UserFullCredentialsDTO? {
        val result: ResultWrapper<UserFullCredentialsDTO> = remoteDataSource.getUserFullInfo(userId, fullInfo)
        return when (result) {
            is ResultWrapper.Success -> {
                Log.d(TAG, "[infoUserDetails] Successfully retrieved user info: ${result.value}")
                result.value
            }

            is ResultWrapper.Error -> {
                Log.e(TAG, "[infoUserDetails] Error retrieving user info: ${result.error}, code: ${result.code}")
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
                Log.e(TAG, "[infoUserDetails] Unexpected result: $result")
                null
            }
        }
    }

    suspend fun updateUser(userId: Long, user: UserUpdateDTO) : UserUpdateResponseDTO? {
        val result: ResultWrapper<UserUpdateResponseDTO> = remoteDataSource.updateUser(userId, user)
        return when(result) {

            is ResultWrapper.Success -> {
                Log.d(TAG, "[updateUser] Successfully retrieved user info: ${result.value}")
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


}