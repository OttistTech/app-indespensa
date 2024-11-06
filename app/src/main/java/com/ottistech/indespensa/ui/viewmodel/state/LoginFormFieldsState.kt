package com.ottistech.indespensa.ui.viewmodel.state

import com.ottistech.indespensa.webclient.dto.user.UserLoginDTO

data class LoginFormFieldsState (
    var email: String? = null,
    var password: String? = null
) {
    fun toUserLoginDTO() : UserLoginDTO {
        return UserLoginDTO(
            email=email ?: "",
            password=password ?: ""
        )
    }
}