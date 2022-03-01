package com.books.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.books.data.repo.Book
import com.books.data.repo.SearchRepository
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

    private val _bookList: MutableLiveData<List<Book>> = MutableLiveData()
    val bookList: LiveData<List<Book>> = _bookList

    private val _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String> = _error

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
                        _bookList.postValue(books)
                    } else {
                        _error.postValue("bookList is empty")
                    }
                } catch (e: IllegalArgumentException) {
                    Log.e(TAG, "IllegalArgumentException: ${e.message}")
                    _error.postValue("IllegalArgumentException: ${e.message}")
                } catch (e: Exception) {
                    Log.e(TAG, "something wrong happened: ${e.message}")
                    _error.postValue("something wrong happened: ${e.message}")
                }
            }
        }
    }
}