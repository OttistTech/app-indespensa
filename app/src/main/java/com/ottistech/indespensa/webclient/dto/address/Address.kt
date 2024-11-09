package com.ottistech.indespensa.webclient.dto.address

import com.google.gson.annotations.SerializedName

data class Address (
    @SerializedName("cepId")
    val cep: String,
    val state: String,
    val city: String,
    val street: String
)