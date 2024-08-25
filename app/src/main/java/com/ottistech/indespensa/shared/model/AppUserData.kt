package com.ottistech.indespensa.shared.model

import com.ottistech.indespensa.shared.AppAccountType

data class AppUserData(
    val userId: Long,
    val name: String,
    val email: String,
    val type: AppAccountType,
    val isPremium: Boolean
)