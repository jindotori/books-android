package com.books.data.repo

import android.util.Log
import com.books.api.ApiClient
import javax.inject.Inject

class DetailRepository @Inject constructor(
    private val apiClient: ApiClient
) {
    companion object {
        private const val TAG = "DetailBookRepository"
    }

    suspend fun getBookDetails(isbn13:String): Detail {
        val detail = apiClient.getBookDetails(isbn13)
        Log.d(TAG, "detail $detail")

        return detail
    }
}