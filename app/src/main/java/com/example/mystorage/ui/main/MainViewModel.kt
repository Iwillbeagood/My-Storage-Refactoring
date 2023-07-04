package com.example.mystorage.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystorage.data.repository.ContentRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val contentRepository: ContentRepositoryImpl
): ViewModel() {

    private val _eventFlow = MutableSharedFlow<MainEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val contentExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    init {
        viewModelScope.launch {
            contentRepository.getInfo() ?: mainEvent(MainEvent.SetInfo(""))
        }
    }

    private fun mainEvent(event: MainEvent) =
        viewModelScope.launch {
            _eventFlow.emit(event)
        }

    fun deleteAllItems() =
        viewModelScope.launch {
            contentRepository.deleteAllItems()
        }

    sealed class MainEvent {
        data class Success(val message: String): MainEvent()
        data class Error(val message: String): MainEvent()
        data class SetInfo(val message: String): MainEvent()
    }
}