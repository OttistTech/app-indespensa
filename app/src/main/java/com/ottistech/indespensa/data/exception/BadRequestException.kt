package com.ottistech.indespensa.data.exception

class BadRequestException(
    override val message: String?
) : Exception(message)