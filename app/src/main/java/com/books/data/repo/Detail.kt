package com.books.data.repo

import com.google.gson.annotations.SerializedName

data class Detail(
    @SerializedName("error") val error: String,
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String,
    @SerializedName("authors") val authors: String,
    @SerializedName("isbn10") val isbn10: String,
    @SerializedName("isbn13") val isbn13: String,
    @SerializedName("pages") val pages: String,
    @SerializedName("year") val year: String,
    @SerializedName("rating") val rating: String,
    @SerializedName("desc") val desc: String,
    @SerializedName("price") val price: String,
    @SerializedName("image") val image: String,
    @SerializedName("url") val url: String,
)