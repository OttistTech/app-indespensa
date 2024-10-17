package com.ottistech.indespensa.webclient.dto

data class Pageable<T> (
    val totalPages: Int,
    val totalElements: Int,
    val size: Int,
    val content: T,
    val number: Int,
    val sort: PaginationSort,
    val numberOfElements: Int,
    val pageable: PageableMetadata,
    val first: Boolean,
    val last: Boolean,
    val empty: Boolean
)

data class PaginationSort(
    val empty: Boolean,
    val sorted: Boolean,
    val unsorted: Boolean
)

data class PageableMetadata(
    val offset: Int,
    val sort: PaginationSort,
    val paged: Boolean,
    val pageNumber: Int,
    val pageSize: Int,
    val unpaged: Boolean
)