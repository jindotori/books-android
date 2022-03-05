package com.books.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.books.repo.detail.Detail
import com.books.repo.detail.DetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.books.repo.Result
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val detailRepository: DetailRepository
) : ViewModel() {

    companion object {
        private const val TAG = "DetailBookViewModel"
    }

    private val _bookDetailsResult: MutableLiveData<Result<Detail>> = MutableLiveData()
    val bookDetailsResult: LiveData<Result<Detail>> = _bookDetailsResult

    fun getDetailBook(isbn13: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = detailRepository.getBookDetails(isbn13)
            _bookDetailsResult.postValue(result)
        }
    }
}