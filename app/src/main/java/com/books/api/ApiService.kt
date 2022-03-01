package com.books.api


import com.books.data.repo.Detail
import com.books.data.repo.Books
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