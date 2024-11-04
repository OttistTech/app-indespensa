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
import com.ottistech.indespensa.webclient.dto.user.UserCreateDTO
import com.ottistech.indespensa.webclient.dto.user.UserCredentialsDTO
import com.ottistech.indespensa.webclient.dto.user.UserFullDTO
import com.ottistech.indespensa.webclient.dto.user.UserLoginDTO
import com.ottistech.indespensa.webclient.dto.user.UserUpdateDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import java.net.HttpURLConnection

class UserRepository (
    private val context: Context
) {

    private val firebaseDataSource = UserFirebaseDataSource.getInstance(context)
    private val remoteDataSource = UserRemoteDataSource()
    private val localDataSource = UserLocalDataSource(context)

    fun isAuthenticated() : Boolean {
        return firebaseDataSource.isAuthenticated()
    }

    suspend fun signup(user: UserCreateDTO) {
        val result : ResultWrapper<UserCredentialsDTO> = remoteDataSource.create(user)
        when(result) {
            is ResultWrapper.Success -> {
                firebaseDataSource.save(
                    email = user.email,
                    password = user.password
                )
                localDataSource.save(result.value)
            }
            is ResultWrapper.Error -> {
                when(result.code) {
                    HttpURLConnection.HTTP_CONFLICT ->
                        throw FieldConflictException(result.error)
                    HttpURLConnection.HTTP_BAD_REQUEST ->
                        throw BadRequestException(result.error)
                    else ->
                        throw Exception("Could not sign up")
                }
            }
            else -> throw Exception("Error while signing up")
        }
    }

    suspend fun login(
        user: UserLoginDTO
    ) {
        val result : ResultWrapper<UserCredentialsDTO> = remoteDataSource.login(user)
        when(result) {
            is ResultWrapper.Success -> {
                firebaseDataSource.login(
                    email = user.email,
                    password = user.password
                )
                localDataSource.save(result.value)
            }
            is ResultWrapper.Error -> {
                when(result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    HttpURLConnection.HTTP_UNAUTHORIZED ->
                        throw ResourceUnauthorizedException(result.error)
                    HttpURLConnection.HTTP_GONE ->
                        throw ResourceGoneException(result.error)
                    else ->
                        throw Exception("Could not log in")
                }
            }
            else -> throw Exception("Error while logging in")
        }
    }

    suspend fun getData(fullInfo: Boolean) : UserFullDTO {
        val currentUser = getUserCredentials()
        val result =
            remoteDataSource.getData(currentUser.userId, fullInfo, currentUser.token)
        return when (result) {
            is ResultWrapper.Success -> {
                result.value
            }
            is ResultWrapper.Error -> {
                when (result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    HttpURLConnection.HTTP_UNAUTHORIZED ->
                        throw ResourceUnauthorizedException(result.error)
                    else ->
                        throw Exception("Could not get user data")
                }
            }
            else -> throw Exception("Error while getting user data")
        }
    }

    suspend fun updateUser(
        user: UserUpdateDTO
    ) : UserCredentialsDTO {
        val currentUser = getUserCredentials()
        val result =
            remoteDataSource.update(currentUser.userId, user, currentUser.token)
        return when(result) {
            is ResultWrapper.Success -> {
                firebaseDataSource.update(
                    getUserCredentials().email,
                    getUserCredentials().password,
                    user.email,
                    user.password
                )
                localDataSource.save(result.value)
                result.value
            }
            is ResultWrapper.Error -> {
                when (result.code) {
                    HttpURLConnection.HTTP_CONFLICT ->
                        throw FieldConflictException(result.error)
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    HttpURLConnection.HTTP_BAD_REQUEST ->
                        throw BadRequestException(result.error)
                    else -> throw Exception(result.error)
                }
            }
            else -> throw Exception("Error while updating user")
        }
    }

    fun logoutUser() {
        firebaseDataSource.logout()
        localDataSource.clear()
    }

    suspend fun deactivateUser() {
        val currentUser = getUserCredentials()
        val result =
            remoteDataSource.deactivate(currentUser.userId, currentUser.token)
        when (result) {
            is ResultWrapper.Success -> {
                logoutUser()
            }
            is ResultWrapper.Error -> {
                when (result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    else ->
                        throw Exception(result.error)
                }
            }
            else -> throw Exception("Error while deactivating user")
        }
    }

    fun getUserCredentials() : UserCredentialsDTO {
        val user = localDataSource.get()
        if(user != null) {
            return user
        }
        throw AuthenticationException()
    }

    suspend fun switchPremium() : Boolean {
        var currentUser = getUserCredentials()
        val result =
            remoteDataSource.switchPremium(currentUser.userId, currentUser.token)
        return when (result) {
            is ResultWrapper.Success -> {
                currentUser = if (currentUser.isPremium) {
                    currentUser.copy(isPremium=false)
                } else {
                    currentUser.copy(isPremium=true)
                }
                localDataSource.save(currentUser)
                true
            }
            is ResultWrapper.Error -> {
                when (result.code) {
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        throw ResourceNotFoundException(result.error)
                    HttpURLConnection.HTTP_GONE ->
                        throw ResourceGoneException(result.error)
                    HttpURLConnection.HTTP_BAD_REQUEST ->
                        throw BadRequestException(result.error)
                    else ->
                        false
                }
            }
            else -> false
        }
    }
}