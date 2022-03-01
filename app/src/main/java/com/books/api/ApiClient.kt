package com.books.api

import com.books.data.repo.Detail
import com.books.data.repo.Books
import javax.inject.Inject

class ApiClient @Inject constructor(private val retrofit: ApiService) {

    suspend fun searchBook(query: String, page: Int): Books {
        return retrofit.searchBook(query, page)
    }

    suspend fun getDetailBook(isbn13: String): Detail {
        return retrofit.getDetailBook(isbn13)
    }
}