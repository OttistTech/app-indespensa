package com.ottistech.indespensa.data.exception

class FieldConflictException(
    override val message: String?
): Exception(message)