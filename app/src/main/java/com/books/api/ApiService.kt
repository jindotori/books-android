package com.books.api


import com.books.repo.detail.Detail
import com.books.repo.search.Books
import retrofit2.http.*

interface ApiService {
    @GET("search/{query}/{page}")
    suspend fun searchBook(
        @Path("query") query: String,
        @Path("page") page: Int
    ): Books

    @GET("books/{isbn13}")
    suspend fun getBookDetails(
        @Path("isbn13") isbn13: String
    ): Detail
}