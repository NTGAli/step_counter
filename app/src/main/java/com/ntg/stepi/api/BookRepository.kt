package com.ntg.stepi.api

class BookRepository(private val apiService: ApiService) {

    suspend fun getBooks(query: String, page: Int) =
        apiService.getUserBase(query, page)
}