package com.books.repo.search

import android.util.Log
import com.books.api.ApiClient
import com.books.repo.Result
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val apiClient: ApiClient
) {
    companion object {
        private const val TAG = "SearchRepository"
        private const val ITEMS_PER_PAGE = 10
    }

    enum class OPERATOR {
        OR, NOT, NO_OP
    }

    private var total: Int = 0
    private var page: Int = 1
    private var query: String = ""
    private var operator: OPERATOR = OPERATOR.NO_OP

    private val isFull: Boolean
        get() = (total == 0 || total < ((page - 1) * ITEMS_PER_PAGE))


//    private fun isFull(): Boolean {
//        return total == 0 || total < ((page - 1) * ITEMS_PER_PAGE)
//    }

    fun init(q: String) {
        total = 0
        page = 1
        query = q
        operator = when {
            query.matches(Regex("\\w+\\|\\w+")) -> OPERATOR.OR
            query.matches(Regex("\\w+-\\w+")) -> OPERATOR.NOT
            else -> OPERATOR.NO_OP
        }
    }

    suspend fun searchBook(): Result<List<Book>> {
        try {
            val bookList: ArrayList<Book> = ArrayList()
            when (operator) {
                OPERATOR.OR -> {
                    val keywords = query.split("|")
                    for (keyword in keywords) {
                        val booksData = apiClient.searchBook(keyword, page)
                        total = booksData.total.toInt()
                        page = booksData.page.toInt()
                        Log.d(TAG, booksData.toString())

                        if (!isFull) {
                            booksData.books.map { book ->
                                bookList.add(book)
                            }
                        }
                    }
                }
                OPERATOR.NOT -> {
                    val firstKeyword = query.split("-")[0]
                    val secondKeyword = query.split("-")[1]
                    val booksData = apiClient.searchBook(firstKeyword, page)
                    total = booksData.total.toInt()
                    page = booksData.page.toInt()
                    Log.d(TAG, booksData.toString())

                    if (!isFull) {
                        booksData.books
                            .filterNot { book ->
                                Log.d(TAG, "Total ${book.title}")
                                book.title.contains(secondKeyword, ignoreCase = true)
                            }.map { book ->
                                bookList.add(book)
                                Log.d(TAG, "Exclude ${book.title}")
                            }
                    }
                }
                else -> {
                    val booksData = apiClient.searchBook(query, page)
                    total = booksData.total.toInt()
                    page = booksData.page.toInt()
                    Log.d(TAG, booksData.toString())

                    if (!isFull) {
                        booksData.books.map { book ->
                            bookList.add(book)
                        }
                    }
                }
            }
            page++

            return if (bookList.isNotEmpty()) {
                Result.Success(bookList)
            } else {
                Result.Error(NoSuchElementException("No more data."))
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}