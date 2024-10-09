package com.ottistech.indespensa.webclient.service

import com.ottistech.indespensa.webclient.dto.product.ProductDTO
import com.ottistech.indespensa.webclient.dto.product.ProductSearchResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {

    @GET("products/barcode/{barcode}")
    suspend fun findByBarcode(
        @Path("barcode") barcode: String
    ) : Response<ProductDTO>

    @GET("products/search")
    suspend fun search(
        @Query("pattern") query: String
    ) : Response<List<ProductSearchResponseDTO>>

}