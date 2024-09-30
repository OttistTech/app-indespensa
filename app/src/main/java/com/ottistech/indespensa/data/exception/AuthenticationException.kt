package com.ottistech.indespensa.data.exception

class AuthenticationException (
    override val message: String = "Authentication needed"
) : Exception(message)