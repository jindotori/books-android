package com.books.data.repo

import androidx.lifecycle.MutableLiveData
import com.books.api.ApiClient
import javax.inject.Inject

class DetailRepository @Inject constructor(
    private val apiClient: ApiClient
) {
    companion object {
        private const val TAG = "DetailBookRepository"
    }

    var resultDetailBook = MutableLiveData<Detail>()

    suspend fun getDetailBook(isbn13:String): Detail {
        return apiClient.getDetailBook(isbn13)
    }
}