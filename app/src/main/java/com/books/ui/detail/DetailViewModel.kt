package com.books.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.books.data.repo.Book
import com.books.data.repo.Detail
import com.books.data.repo.DetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val detailRepository: DetailRepository
) : ViewModel() {

    companion object {
        private const val TAG = "DetailBookViewModel"
    }

    private val _bookDetails: MutableLiveData<Detail> = MutableLiveData()
    val bookDetails: LiveData<Detail> = _bookDetails

    fun getDetailBook(isbn13: String) {
        viewModelScope.launch {
            _bookDetails.postValue(detailRepository.getBookDetails(isbn13))
        }
    }
}