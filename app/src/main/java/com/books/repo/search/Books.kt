package com.books.repo.search

import com.google.gson.annotations.SerializedName

data class Books(
    @SerializedName("total") val total: String,
    @SerializedName("page") val page: String,
    @SerializedName("books") val books: ArrayList<Book>
)