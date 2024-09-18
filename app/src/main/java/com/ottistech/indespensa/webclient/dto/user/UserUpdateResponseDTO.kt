package com.ottistech.indespensa.webclient.dto.user

data class UserUpdateResponseDTO (
    val name: String,
    val email: String,
    val cep: String,
    val addressNumber: Int,
    val street: String,
    val city: String,
    val state: String
)