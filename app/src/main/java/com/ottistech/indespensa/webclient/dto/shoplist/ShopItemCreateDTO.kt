package com.ottistech.indespensa.webclient.dto.shoplist

data class ShopItemCreateDTO (
    val productId: Long,
    val amount: Int = 1
)
