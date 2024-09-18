package com.ottistech.indespensa.webclient.dto.shoplist

import com.ottistech.indespensa.webclient.dto.product.ProductItemDetailsDTO
import java.math.BigDecimal

data class ShopItemDetailsDTO (
    override val itemId: Long,
    override val userId: Long,
    override val productId: Long,
    override val productImageUrl: String,
    override val productFood: String,
    override val productName: String,
    override val productBrand: String,
    override val productDescription: String,
    override val productAmount: BigDecimal,
    override val productUnit: String,
    override val amount: Int
) : ProductItemDetailsDTO
