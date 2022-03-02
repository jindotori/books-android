package com.books.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.books.repo.SimpleResult
import com.books.repo.Status
import com.books.repo.search.Book
import com.books.repo.search.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {
    companion object {
        private const val TAG = "SearchViewModel"
    }

    private val _searchBookResult: MutableLiveData<SimpleResult<List<Book>>> = MutableLiveData()
    val searchBookResult: LiveData<SimpleResult<List<Book>>> = _searchBookResult

    fun init() {
        viewModelScope.launch {
            searchRepository.init()
        }
    }

    fun searchBook(query: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val books = searchRepository.searchBook(query)
                    if (books.isNotEmpty()) {
                        Log.d(TAG, "searchBook called")
                        _searchBookResult.postValue(SimpleResult(Status.SUCCESS, books))
                    } else {
                        _searchBookResult.postValue(SimpleResult(Status.ERROR, "bookList is empty"))
                    }
                } catch (e: IllegalArgumentException) {
                    Log.e(TAG, "IllegalArgumentException: ${e.message}")
                    _searchBookResult.postValue(
                        SimpleResult(
                            Status.ERROR,
                            "IllegalArgumentException: ${e.message}"
                        )
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "something wrong happened: ${e.message}")
                    _searchBookResult.postValue(
                        SimpleResult(
                            Status.ERROR,
                            "something wrong happened: ${e.message}"
                        )
                    )
                }
            }
        }
    }

    fun clear() {
        viewModelScope.launch {
            _searchBookResult.postValue(null)
        }
    }
}