package com.books.data.repo

import android.util.Log
import com.books.api.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val apiClient: ApiClient
) {
    companion object {
        private const val TAG = "SearchRepository"
    }

    private var page: Int = 1

    suspend fun init() = withContext(Dispatchers.IO) {
        page = 1
    }

    suspend fun searchBook(query: String): List<Book> {
        val bookList: ArrayList<Book> = ArrayList()
        when {
            query.matches(Regex("\\w+")) || query.matches(Regex("\\w+\\|\\w+")) -> {
                val keywords = query.split("|")
                for (keyword in keywords) {
                    val booksData = apiClient.searchBook(keyword, page)
                    for (bookItem in booksData.books) {
                        bookList.add(bookItem)
                    }
                }
            }
            query.matches(Regex("\\w+-\\w+")) -> {
                val includeKeyword = query.split("-")[0]
                val excludeKeyword = query.split("-")[1]
                val booksData = apiClient.searchBook(includeKeyword, page)
                Log.d(TAG, "total ${booksData.total}")
                for (book in booksData.books) {
                    Log.d(TAG, "1 ${book.title}")
                }
                booksData.books.filterNot { book ->
                    book.title.contains(excludeKeyword, ignoreCase = true)
                }.map { bookList.add(it)
                    Log.d(TAG, "2 ${it.title}")}
            }
            else -> {
                throw IllegalArgumentException("$query is invalid.")
            }
        }

        page++

        return bookList
    }
}