package com.ottistech.indespensa.ui.viewmodel.state

data class SignupFormErrorState (
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
    var termsCheck: String? = null
)