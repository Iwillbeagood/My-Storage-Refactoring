package com.example.mystorage.ui.item.used_item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.data.repository.ContentRepositoryImpl
import com.example.mystorage.ui.item.add.ItemAddViewModel
import com.example.mystorage.utils.etc.ItemState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel

class UsedItemOptionsViewModel  @Inject constructor(
    private val contentRepository: ContentRepositoryImpl
): ViewModel() {

    private val _eventFlow = MutableSharedFlow<UsedOptionEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val contentExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    private fun usedOptionEvent(event: UsedOptionEvent) =
        viewModelScope.launch {
            _eventFlow.emit(event)
        }

    private fun updateItem(itemEntity: ItemEntity) =
        viewModelScope.launch {
            if (contentRepository.updateItem(itemEntity))
                usedOptionSuccess("사용 완료 물건을 되돌렸습니다")
            else
                usedOptionError("사용 완료 물건을 되돌리지 못했습니다")
        }

    private fun deleteItem(itemEntity: ItemEntity) =
        viewModelScope.launch {
            if (contentRepository.deleteItem(itemEntity))
                usedOptionSuccess("사용 완료 물건 삭제에 성공 했습니다")
            else
                usedOptionError("사용 완료 물건 삭제에 실패했습니다")
        }

    fun onUsedItemReturn(itemID: Int) =
        viewModelScope.launch {
            val currentItem = getItemByID(itemID)
            updateItem(currentItem.copy(item_state = ItemState.NOT_USED))
        }

    fun onItemDelete(itemID: Int) =
        viewModelScope.launch {
            val currentItem = getItemByID(itemID)
            deleteItem(currentItem)
        }

    private suspend fun getItemByID(itemID: Int): ItemEntity =
        withContext(viewModelScope.coroutineContext) {
            contentRepository.getItemByID(itemID)
        }

    private fun usedOptionSuccess(message: String) =
        usedOptionEvent(UsedOptionEvent.Success(message))

    private fun usedOptionError(message: String) =
        usedOptionEvent(UsedOptionEvent.Error(message))

    sealed class UsedOptionEvent {
        data class Success(val message: String): UsedOptionEvent()
        data class Error(val message: String): UsedOptionEvent()
    }
}