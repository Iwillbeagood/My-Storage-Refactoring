package com.example.mystorage.ui.item.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.data.repository.ContentRepositoryImpl
import com.example.mystorage.utils.etc.ItemState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ItemOptionsViewModel @Inject constructor(
    private val contentRepository: ContentRepositoryImpl
): ViewModel() {

    private val _eventFlow = MutableSharedFlow<OptionEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val contentExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    private fun optionEvent(event: OptionEvent) =
        viewModelScope.launch {
            _eventFlow.emit(event)
        }

    private fun updateItem(itemEntity: ItemEntity) =
        viewModelScope.launch {
            if (contentRepository.updateItem(itemEntity))
                optionSuccess("물건 정보를 수정 했습니다")
            else
                optionError("물건 정보를 수정하지 못했습니다")
        }
    private fun deleteItem(itemEntity: ItemEntity) =
        viewModelScope.launch {
            if (contentRepository.deleteItem(itemEntity))
                optionSuccess("물건 삭제에 성공 했습니다")
            else
                optionError("물건 삭제에 실패했습니다")
        }

    private suspend fun getItemByID(itemID: Int): ItemEntity =
        withContext(viewModelScope.coroutineContext) {
            contentRepository.getItemByID(itemID)
        }

    fun onItemUsed(itemID: Int) =
        viewModelScope.launch {
            val currentItem = getItemByID(itemID)
            updateItem(currentItem.copy(item_state = ItemState.USED))
        }


    fun onItemSingleUse(itemID: Int) =
        viewModelScope.launch {
            val currentItem = getItemByID(itemID)
            val currentItemCount = currentItem.item_count

            if (currentItemCount > 1) {
                updateItem(currentItem.copy(item_count = currentItem.item_count.plus(-1)))
            } else {
                optionError("물건이 하나밖에 남지가 않았습니다")
            }
        }

    fun onItemDelete(itemID: Int) =
        viewModelScope.launch {
            val currentItem = getItemByID(itemID)
            deleteItem(currentItem)
        }

    private fun optionSuccess(message: String) =
        optionEvent(OptionEvent.Success(message))

    private fun optionError(message: String) =
        optionEvent(OptionEvent.Error(message))

    sealed class OptionEvent {
        data class Success(val message: String): OptionEvent()
        data class Error(val message: String): OptionEvent()
    }
}