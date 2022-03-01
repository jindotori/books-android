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
    private var total: Int = -1
    private val isFull
        get() = total == 0

    suspend fun init() = withContext(Dispatchers.IO) {
        page = 1
        total = -1
    }
/*
    suspend fun searchBook(query: String) = withContext(Dispatchers.IO) {
        when {
            "|" in query -> {
                for (title in query.split("|")) {
                    apiClient.searchBook(title, page, object : ApiCallback<Books>() {
                        override fun onSuccess(result: Books?) {
                            resultBooks.value = SimpleResult(Status.SUCCESS, result)
                        }

                        override fun onFail(code: Int, result: String) {
                            resultBooks.value = when (code) {
                                0 -> SimpleResult(Status.ERROR, result)
                                else -> SimpleResult(Status.FAIL, result)
                            }
                        }
                    })
                }
            }
            "-" in query -> {
                val includeKeyword = query.split("-")[0]
                val excludeKeyword = query.split("-")[1]
                Log.d(TAG, excludeKeyword)
                apiClient.searchBook(includeKeyword, page, object : ApiCallback<Books>() {
                    override fun onSuccess(result: Books?) {
                        result?.let {
                            val iterator = result.books.iterator()
                            while(iterator.hasNext()) {
                                val item = iterator.next()
                                if (excludeKeyword in item.title) {
                                    iterator.remove()
                                }
                            }
                            total = result.total.toInt()
                            page = result.page.toInt() + 1
                            if (!isFull) {
                                resultBooks.postValue(SimpleResult(Status.SUCCESS, result))
                            } else {
                                resultBooks.postValue(SimpleResult(Status.ERROR, "full"))
                            }
                        }

                    }

                    override fun onFail(code: Int, result: String) {
                        resultBooks.value = when (code) {
                            0 -> SimpleResult(Status.ERROR, result)
                            else -> SimpleResult(Status.FAIL, result)
                        }
                    }
                })
            }
            else -> {
                if (isFull) {
                    resultBooks.postValue(SimpleResult(Status.ERROR, "full"))
                } else {
                    apiClient.searchBook(query, page, object : ApiCallback<Books>() {
                        override fun onSuccess(result: Books?) {
                            total = (result?.total?.toInt() ?: 0)
                            page = (result?.page?.toInt() ?: 0) + 1
                            if (!isFull) {
                                resultBooks.value = SimpleResult(Status.SUCCESS, result)
                            } else {
                                resultBooks.postValue(SimpleResult(Status.ERROR, "full"))
                            }
                        }

                        override fun onFail(code: Int, result: String) {
                            resultBooks.value = when (code) {
                                0 -> SimpleResult(Status.ERROR, result)
                                else -> SimpleResult(Status.FAIL, result)
                            }
                        }
                    })
                }
            }
        }
    }
*/

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