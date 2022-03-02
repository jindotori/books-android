package com.books.api

import com.books.repo.detail.Detail
import com.books.repo.search.Books
import javax.inject.Inject

class ApiClient @Inject constructor(private val retrofit: ApiService) {

    suspend fun searchBook(query: String, page: Int): Books {
        return retrofit.searchBook(query, page)
    }

    suspend fun getBookDetails(isbn13: String): Detail {
        return retrofit.getBookDetails(isbn13)
    }
}