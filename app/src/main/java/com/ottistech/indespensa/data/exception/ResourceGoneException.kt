package com.ottistech.indespensa.data.exception

class ResourceGoneException(
    override val message: String?
) : Exception(message)