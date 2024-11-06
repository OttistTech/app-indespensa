package com.ottistech.indespensa.webclient.dto.user

import java.util.Date

data class UserFullDTO(
    val userId: Long,
    val type: String,
    val name: String,
    val birthDate: Date,
    val enterpriseType: String,
    val email: String,
    val password: String,
    val cep: String,
    val addressNumber: Int,
    val street: String,
    val city: String,
    val state: String,
    val isPremium: Boolean
)
