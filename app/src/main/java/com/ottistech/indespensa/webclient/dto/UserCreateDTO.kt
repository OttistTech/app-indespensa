package com.ottistech.indespensa.webclient.dto

import com.ottistech.indespensa.shared.AppAccountType
import java.util.Date


data class UserCreateDTO(
    val type: AppAccountType,
    val name: String,
    val enterpriseType: String? = null,
    val email: String,
    val birthDate: Date?,
    val password: String,
    val cep: String,
    val addressNumber: Int,
    val street: String,
    val city: String,
    val state: String
)
