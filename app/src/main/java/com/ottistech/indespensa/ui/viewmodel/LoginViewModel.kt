package com.ottistech.indespensa.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.exception.ResourceGoneException
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.exception.ResourceUnauthorizedException
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.ui.viewmodel.state.LoginFormFieldsState
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    val formState = MutableLiveData(LoginFormFieldsState())
    val isLoading = MutableLiveData(false)

    private val _feedback: MutableLiveData<Feedback?> = MutableLiveData(null)
    val feedback: LiveData<Feedback?> = _feedback

    fun submit() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                userRepository.login(formState.value!!.toUserLoginDTO())
                _feedback.value = Feedback(FeedbackId.LOGIN, FeedbackCode.SUCCESS, "Bem vindo de volta!")
            } catch (e: ResourceNotFoundException) {
                _feedback.value = Feedback(FeedbackId.LOGIN, FeedbackCode.NOT_FOUND, "O usuário não existe!")
            } catch (e: ResourceUnauthorizedException) {
                _feedback.value = Feedback(FeedbackId.LOGIN, FeedbackCode.UNAUTHORIZED, "Verifique seu usuário e senha!")
            } catch (e: ResourceGoneException) {
                _feedback.value = Feedback(FeedbackId.LOGIN, FeedbackCode.GONE, "O usuário não existe!")
            } catch (e: Exception) {
                _feedback.value = Feedback(FeedbackId.LOGIN, FeedbackCode.UNHANDLED, "Não foi possível realizar login! Verifique suas informações")
            }
            isLoading.value = false
        }
    }
}