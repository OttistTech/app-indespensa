package com.ottistech.indespensa.webclient.service

import com.ottistech.indespensa.webclient.dto.ProductDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductService {

    @GET("products/barcode/{barcode}")
    suspend fun findByBarcode(
        @Path("barcode") barcode: String
    ) : Response<ProductDTO>

}