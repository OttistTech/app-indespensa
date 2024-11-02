package com.ottistech.indespensa.ui.viewmodel.state

import com.ottistech.indespensa.shared.AppAccountType
import com.ottistech.indespensa.webclient.dto.user.UserUpdateDTO

data class UpdateProfileFormFieldsState (
    var name: String? = null,
    var enterpriseType: String? = null,
    var email: String? = null,
    var password: String? = null,
    var cep: String? = null,
    var addressNumber: String? = null,
    var street: String? = null,
    var city: String? = null,
    var state: String? = null,
) {

    fun toUserUpdateDTO() : UserUpdateDTO {
        return UserUpdateDTO(
            name=name ?: "",
            enterpriseType=enterpriseType ?: "",
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