package com.ottistech.indespensa.webclient.dto

data class UserUpdateDTO(
    val name: String,
    val email: String,
    val password: String,
    val cep: String,
    val addressNumber: Int,
    val street: String,
    val city: String,
    val state: String
)
