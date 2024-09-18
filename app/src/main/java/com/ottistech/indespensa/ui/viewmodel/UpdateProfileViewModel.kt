package com.ottistech.indespensa.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.exception.BadRequestException
import com.ottistech.indespensa.data.exception.FieldConflictException
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.shared.AppAccountType
import com.ottistech.indespensa.ui.UiConstants
import com.ottistech.indespensa.ui.model.FieldError
import com.ottistech.indespensa.webclient.dto.user.UserFullIDTO
import com.ottistech.indespensa.webclient.dto.user.UserUpdateDTO
import kotlinx.coroutines.launch

class UpdateProfileViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val TAG = "UPDATE USER VIEWMODEL"

    private val _userInfo = MutableLiveData<UserFullIDTO?>()
    val userInfo: LiveData<UserFullIDTO?> = _userInfo

    private val _updateResult = MutableLiveData<Boolean>()
    val updateResult: LiveData<Boolean> = _updateResult

    private val _userMessage = MutableLiveData<String?>()
    val userMessage: LiveData<String?> = _userMessage

    private val _fieldError = MutableLiveData<FieldError>()
    val fieldError: LiveData<FieldError> = _fieldError

    private val currentUser = repository.getUserCredentials()

    fun fetchUserInfo() {
        viewModelScope.launch {
            try {
                val userInfo = repository.getUserInfo(currentUser.userId, true)
                Log.d(TAG, "[fetchUserInfo] Fetched user information successfully")
                _userInfo.value = userInfo
            } catch (e: Exception) {
                Log.e(TAG, "[fetchUserInfo] Could not fetch user information", e)
                _userMessage.value = "Não foi possível carregar"
            }
        }
    }

    fun updateUser(user: UserUpdateDTO) {
        viewModelScope.launch {
            try {
                val result = repository.updateUser(currentUser.userId, user)
                if (result != null) {
                    Log.d(TAG, "[updateUser] User updated successfully")
                    _updateResult.value = true
                    fetchUserInfo()
                }
            } catch (e: FieldConflictException) {
                Log.e(TAG, "[updateUser] User tried to migrate to an existing email", e)
                _fieldError.value = FieldError("email", UiConstants.ERROR_CONFLICT)
            } catch (e: BadRequestException) {
                Log.e(TAG, "[updateUser] Bad request for updating user", e)
            }
        }
    }

    fun deactivateUser() {
        viewModelScope.launch {
            try {
                repository.deactivateUser(currentUser.userId)
                Log.d(TAG, "[removeUser] Deactivated user successfully")
                _userMessage.value = "Conta removida com sucesso"
            } catch (e: Exception) {
                Log.e(TAG, "[removeUser] Error while deactivating user", e)
                _userMessage.value = "Erro ao remover a conta"
            }
        }
    }

    fun logoutUser() {
        repository.logoutUser()
        Log.d(TAG, "[logoutUser] User logged out")
    }

    fun getUserType(): AppAccountType {
        Log.d(TAG, "[getUserType] User type: ${currentUser.type}")
        return currentUser.type
    }
}