package com.ottistech.indespensa.webclient.dto.user

data class UserUpdateDTO(
    val name: String,
    val enterpriseType: String,
    val email: String,
    val password: String,
    val cep: String,
    val addressNumber: Int,
    val street: String,
    val city: String,
    val state: String
)
