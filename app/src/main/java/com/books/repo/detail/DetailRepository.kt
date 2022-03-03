package com.books.repo.detail

import android.util.Log
import com.books.api.ApiClient
import com.books.repo.Result
import javax.inject.Inject

class DetailRepository @Inject constructor(
    private val apiClient: ApiClient
) {
    companion object {
        private const val TAG = "DetailBookRepository"
    }

    suspend fun getBookDetails(isbn13: String): Result<Detail> {
        return try {
            val detail = apiClient.getBookDetails(isbn13)
            Log.d(TAG, "detail $detail")

            detail.pdf?.let { pdf ->
                pdf.keys.let {
                    Log.d(TAG, "key $it")
                }
            }
            Result.Success(detail)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}