package com.books.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun getDetailBook(isbn13: String) {
        viewModelScope.launch {
            detailRepository.getDetailBook(isbn13)
        }
    }
}