package com.books.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _delay: MutableLiveData<Any> = MutableLiveData()

    fun delayShow() : LiveData<Any> {
        viewModelScope.launch {
                delay(1_000)
                _delay.postValue(0)
        }
        return _delay
    }
}
