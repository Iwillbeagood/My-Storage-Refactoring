package com.example.mystorage.ui.item.update

import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystorage.data.entity.InfoEntity
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.data.repository.ContentRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ItemUpdateViewModel @Inject constructor(
    private val contentRepository: ContentRepositoryImpl
)
    : ViewModel() {

    private val _eventFlow = MutableSharedFlow<ItemUpdateEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val contentExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    init {
        viewModelScope.launch {
            itemUpdateEvent(ItemUpdateEvent.GetInfo(contentRepository.getInfo()!!))
        }
    }

    fun onItemUpdate(itemEntity: ItemEntity) =
        viewModelScope.launch {
            if (itemIsValid(itemEntity))
                updateItem(itemEntity)
        }

    private fun itemUpdateEvent(event: ItemUpdateEvent) =
        viewModelScope.launch {
            _eventFlow.emit(event)
        }

    private fun updateItem(itemEntity: ItemEntity) =
        viewModelScope.launch {
            contentRepository.updateItem(itemEntity)
        }

    private suspend fun getItemByID(itemID: Int): ItemEntity =
        withContext(viewModelScope.coroutineContext) {
            contentRepository.getItemByID(itemID)
        }

    fun getItem(itemID: Int) =
        viewModelScope.launch {
            itemUpdateEvent(ItemUpdateEvent.GetItem(getItemByID(itemID)))
        }

    private fun itemIsValid(itemEntity: ItemEntity): Boolean =
        if (TextUtils.isEmpty(itemEntity.item_name)) {
            itemUpdateSuccess("물건 이름은 필수 입니다!")
            false
        } else {
            itemUpdateError("Success")
            true
        }

    private fun itemUpdateSuccess(message: String) =
        itemUpdateEvent(ItemUpdateEvent.Success(message))

    private fun itemUpdateError(message: String) =
        itemUpdateEvent(ItemUpdateEvent.Error(message))


    sealed class ItemUpdateEvent {
        data class Success(val message: String): ItemUpdateEvent()
        data class Error(val message: String): ItemUpdateEvent()
        data class GetInfo(val infoEntity: InfoEntity): ItemUpdateEvent()
        data class GetItem(val item: ItemEntity): ItemUpdateEvent()
    }
}