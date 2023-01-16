package com.example.testapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.testapp.utils.Event

open class BaseViewModel : ViewModel() {

    private val _viewEvent = MutableLiveData<Event<Any>>()
    val viewEvent: LiveData<Event<Any>>
        get() = _viewEvent

    fun viewEvent(content: Any) {
        _viewEvent.value = Event(content)
    }

}