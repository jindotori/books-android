package com.books.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.books.repo.search.Book
import com.books.repo.search.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.books.repo.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {
    companion object {
        private const val TAG = "SearchViewModel"
    }

    private val _searchBookResult: MutableLiveData<Result<List<Book>>> = MutableLiveData()
    val searchBookResult: LiveData<Result<List<Book>>> = _searchBookResult

    fun init(query: String) {
        viewModelScope.launch {
            searchRepository.init(query)
        }
    }

    fun searchBook() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = searchRepository.searchBook()
            withContext(Dispatchers.Main) {
                _searchBookResult.postValue(result)
            }
        }
    }

    fun clear() {
        viewModelScope.launch {
            _searchBookResult.postValue(null)
        }
    }
}