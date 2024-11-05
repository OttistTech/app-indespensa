package com.ottistech.indespensa.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.exception.BadRequestException
import com.ottistech.indespensa.data.exception.FieldConflictException
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.shared.AppAccountType
import com.ottistech.indespensa.ui.helpers.validIsEmail
import com.ottistech.indespensa.ui.helpers.validMaxLength
import com.ottistech.indespensa.ui.helpers.validMinLength
import com.ottistech.indespensa.ui.helpers.validNotNull
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.ui.viewmodel.state.UpdateProfileFormErrorState
import com.ottistech.indespensa.ui.viewmodel.state.UpdateProfileFormFieldsState
import com.ottistech.indespensa.webclient.dto.user.UserFullDTO
import kotlinx.coroutines.launch

class UpdateProfileViewModel(
    private val repository: UserRepository
) : ViewModel() {

    val userType = repository.getUserCredentials().type

    val formState = MutableLiveData(UpdateProfileFormFieldsState())
    val formErrorState = MutableLiveData(UpdateProfileFormErrorState())

    val isFormValid = MutableLiveData(true)
    val isLoading = MutableLiveData(false)

    private val _feedback = MutableLiveData<Feedback?>()
    val feedback: LiveData<Feedback?> = _feedback

    fun fetchUserInfo() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val userInfo = repository.getData(true)
                setStateWithUser(userInfo)
            } catch (e: Exception) {
                _feedback.value =
                    Feedback(FeedbackId.GET_PROFILE_DATA, FeedbackCode.UNHANDLED, "Não foi possível carregar suas informações!")
            }
            isLoading.value = false
        }
    }

    private fun setStateWithUser(data: UserFullDTO) {
        formState.value = UpdateProfileFormFieldsState(
            name=data.name,
            enterpriseType=data.enterpriseType,
            email=data.email,
            password=data.password,
            cep=data.cep,
            addressNumber=data.addressNumber.toString(),
            street=data.street,
            city=data.city,
            state=data.state
        )
    }

    fun update() {
            validAllFields()
            if (isFormValid.value!!) {
                viewModelScope.launch {
                    isLoading.value = true
                    try {
                        repository.updateUser(formState.value!!.toUserUpdateDTO())
                        _feedback.value =
                            Feedback(FeedbackId.UPDATE_PROFILE, FeedbackCode.SUCCESS, "Usuário atualizado")
                        fetchUserInfo()
                    } catch (e: FieldConflictException) {
                        formErrorState.value = formErrorState.value?.copy(email="Este e-mail já está em uso!")
                        _feedback.value =
                            Feedback(FeedbackId.UPDATE_PROFILE, FeedbackCode.CONFLICT, "Escolha um e-mail diferente!")
                    } catch (e: BadRequestException) {
                        _feedback.value =
                            Feedback(FeedbackId.UPDATE_PROFILE, FeedbackCode.BAD_REQUEST, "Verifique as informações digitadas")
                    } catch(e: Exception) {
                        _feedback.value =
                            Feedback(FeedbackId.SIGNUP, FeedbackCode.UNHANDLED, "Não foi possível atualizar a conta!")
                    }
                    isLoading.value = false
            }
        }
    }

    fun deactivate() {
        viewModelScope.launch {
            try {
                repository.deactivateUser()
                _feedback.value =
                    Feedback(FeedbackId.DEACTIVATE_PROFILE, FeedbackCode.SUCCESS, "Sua conta foi desativada!")
            } catch (e: Exception) {
                _feedback.value =
                    Feedback(FeedbackId.DEACTIVATE_PROFILE, FeedbackCode.UNHANDLED, "Não foi possível desativar a conta!")
            }
        }
    }

    fun logout() {
        repository.logoutUser()
    }

    private fun validForm() {
        val errorState = formErrorState.value
        isFormValid.value = errorState?.let {
            it.name == null &&
            it.enterpriseType == null &&
            it.email == null &&
            it.password == null &&
            it.cep == null &&
            it.addressNumber == null &&
            it.street == null &&
            it.city == null &&
            it.state == null
        } ?: true
    }

    fun validName() {
        val value = formState.value!!.name
        val error =
            if( !validNotNull(value) ) { "Nome é obrigatório!" }
            else if( !validMinLength(value, 4) ) { "Nome deve ter no mínimo 4 caracteres!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(name=error)
    }

    fun validEnterpriseType() {
        val value = formState.value!!.enterpriseType
        val error =
            if( !validNotNull(value) ) { "Tipo da empresa é obrigatório!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(enterpriseType=error)
    }

    fun validEmail() {
        val value = formState.value!!.email
        val error =
            if (!validNotNull(value)) {
                "E-mail é obrigatório!"
            } else if (!validIsEmail(value)) {
                "Digite um e-mail válido!"
            } else {
                null
            }
        formErrorState.value = formErrorState.value!!.copy(email = error)
    }

    fun validPassword() {
        val value = formState.value!!.password
        val error =
            if( !validNotNull(value) ) { "Senha é obrigatória!" }
            else if( !validMinLength(value, 8) ) { "A senha deve ter pelo menos 8 caracteres!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(password=error)
    }


    fun validCep() {
        val value = formState.value!!.cep
        val error =
            if( !validNotNull(value) ) { "CEP é obrigatório!" }
            else if( !validMinLength(value, 8) ) { "Digite um CEP válido!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(cep=error)
    }

    fun validAddressNumber() {
        val value = formState.value!!.addressNumber
        val error =
            if( !validNotNull(value) ) { "Número é obrigatório!" }
            else if( !validMaxLength(value, 4) ) { "Digite um número válido!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(addressNumber=error)
    }

    fun validStreet() {
        val value = formState.value!!.street
        val error =
            if( !validNotNull(value) ) { "Rua é obrigatória!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(street=error)
    }

    fun validCity() {
        val value = formState.value!!.city
        val error =
            if( !validNotNull(value) ) { "Cidade é obrigatória!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(city=error)
    }

    fun validState() {
        val value = formState.value!!.state
        val error =
            if( !validNotNull(value) ) { "Estado é obrigatório!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(state=error)
    }


    private fun validAllFields() {
        if(userType == AppAccountType.BUSINESS) {
            validEnterpriseType()
        }
        validName()
        validEmail()
        validPassword()
        validCep()
        validAddressNumber()
        validStreet()
        validCity()
        validState()
        validForm()
    }
}