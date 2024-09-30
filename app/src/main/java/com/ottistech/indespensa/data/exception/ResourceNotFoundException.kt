package com.ottistech.indespensa.data.exception

class ResourceNotFoundException (
    override val message: String?
) : Exception(message)