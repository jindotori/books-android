package com.books.repo.search

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

    enum class Operator {
        OR, NOT, NO_OP, UNKNOWN
    }

    private var total: Int = 0
    private var page: Int = 1
    private var query: String = ""
    private var operator: Operator = Operator.NO_OP

    private val isFull: Boolean
        get() = (total == 0 || total < ((page - 1) * ITEMS_PER_PAGE))

    fun init(q: String) {
        total = 0
        page = 1
        query = q
        operator = when {
            query.matches(Regex("\\w+\\|\\w+")) -> Operator.OR
            query.matches(Regex("\\w+-\\w+")) -> Operator.NOT
            query.matches(Regex("\\w+")) -> Operator.NO_OP
            else -> Operator.UNKNOWN
        }
    }

    suspend fun searchBook(): Result<List<Book>> {
        try {
            val bookList: ArrayList<Book> = ArrayList()
            when (operator) {
                Operator.OR -> {
                    val keywords = query.split("|")
                    keywords.map { keyword ->
                        val booksData = apiClient.searchBook(keyword, page)
                        total = booksData.total.toInt()
                        page = booksData.page.toInt()

                        if (!isFull) {
                            booksData.books.map { book ->
                                bookList.add(book)
                            }
                        }
                    }
                }
                Operator.NOT -> {
                    val firstKeyword = query.split("-")[0]
                    val secondKeyword = query.split("-")[1]
                    val booksData = apiClient.searchBook(firstKeyword, page)
                    total = booksData.total.toInt()
                    page = booksData.page.toInt()

                    if (!isFull) {
                        booksData.books
                            .filterNot { book ->
                                book.title.contains(secondKeyword, ignoreCase = true)
                            }.map { book ->
                                bookList.add(book)
                            }
                    }
                }
                Operator.NO_OP -> {
                    val booksData = apiClient.searchBook(query, page)
                    total = booksData.total.toInt()
                    page = booksData.page.toInt()

                    if (!isFull) {
                        booksData.books.map { book ->
                            bookList.add(book)
                        }
                    }
                }
                else -> {
                    return Result.Error(IllegalArgumentException("Keyword is invalid."))
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