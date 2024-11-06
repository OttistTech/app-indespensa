package com.ottistech.indespensa.webclient.dto.user

import com.ottistech.indespensa.shared.AppAccountType

data class UserCredentialsDTO(
    val userId: Long,
    val type: AppAccountType,
    val name: String,
    val email: String,
    val password: String,
    val enterpriseType: String?,
    val isPremium: Boolean,
    var token: String
)
