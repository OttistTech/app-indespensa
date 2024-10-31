package com.ottistech.indespensa.ui.viewmodel.state

import com.ottistech.indespensa.shared.AppAccountType
import com.ottistech.indespensa.ui.helpers.toDate
import com.ottistech.indespensa.webclient.dto.user.UserCreateDTO
import java.util.Date

data class SignupFormFieldsState (
    val accountType: AppAccountType,
    var name: String? = null,
    var enterpriseType: String? = null,
    var email: String? = null,
    var birthdate: String? = null,
    var password: String? = null,
    var passwordConfirmation: String? = null,
    var cep: String? = null,
    var addressNumber: String? = null,
    var street: String? = null,
    var city: String? = null,
    var state: String? = null,
    var termsCheck: Boolean = false
) {

    fun toUserCreateDTO(): UserCreateDTO {
        return UserCreateDTO(
            type=accountType,
            name=name ?: "",
            enterpriseType=enterpriseType,
            birthDate=birthdate?.toDate() ?: Date(),
            email=email ?: "",
            password=password ?: "",
            cep=cep ?: "",
            addressNumber=addressNumber?.toInt() ?: 0,
            street=street ?: "",
            city=city ?: "",
            state=state ?: "",
        )
    }
}