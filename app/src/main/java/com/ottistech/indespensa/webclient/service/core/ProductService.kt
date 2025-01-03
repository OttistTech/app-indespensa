package com.ottistech.indespensa.webclient.service.core

import com.ottistech.indespensa.webclient.dto.product.ProductDTO
import com.ottistech.indespensa.webclient.dto.product.ProductSearchResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {

    @GET("products/barcode/{barcode}")
    suspend fun findByBarcode(
        @Path("barcode") barcode: String,
        @Header("Authorization") token: String
    ) : Response<ProductDTO>

    @GET("products/search")
    suspend fun search(
        @Query("pattern") query: String,
        @Header("Authorization") token: String
    ) : Response<List<ProductSearchResponseDTO>>

    @GET("products/{product_id}/details")
    suspend fun findById(
        @Path("product_id") productId: Long,
        @Header("Authorization") token: String
    ) : Response<ProductDTO>

}