package com.ottistech.indespensa.data.exception

class ResourceUnauthorizedException(
    override val message: String?
) : Exception(message)