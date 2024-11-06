package com.ottistech.indespensa.webclient.dto.recommendation

import com.ottistech.indespensa.webclient.dto.product.ProductDTO

data class ProductRecommendationDTO (
    val products: List<ProductDTO>
)