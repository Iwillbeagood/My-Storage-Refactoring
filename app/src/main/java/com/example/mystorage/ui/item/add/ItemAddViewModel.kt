package com.example.mystorage.ui.item.add

import android.graphics.Bitmap
import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystorage.data.entity.InfoEntity
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.data.repository.ContentRepositoryImpl
import com.example.mystorage.utils.etc.ItemState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Timestamp
import javax.inject.Inject

@HiltViewModel
class ItemAddViewModel @Inject constructor(
    private val contentRepository: ContentRepositoryImpl
): ViewModel() {

    private val _eventFlow = MutableSharedFlow<ItemAddEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val showConfirmationDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showConfirmationDialogFlow: StateFlow<Boolean> = showConfirmationDialog.asStateFlow()

    private var currentItem = ItemEntity(
            item_ID = 0,
            item_name = "",
            item_image = null,
            item_place = "",
            item_store = "",
            item_count = 1,
            item_state = ItemState.NOT_USED,
            created_at = Timestamp(0),
            updated_at = Timestamp(0),
            isSelected = false
        )

    private val contentExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    init {
        viewModelScope.launch {
            contentRepository.getInfo()?.let { infoEntity ->
                itemAddEvent(ItemAddEvent.GetInfo(infoEntity))
            }
        }
    }

    private fun itemAddEvent(event: ItemAddEvent) =
        viewModelScope.launch {
            _eventFlow.emit(event)
        }

    private fun insertItem(itemEntity: ItemEntity) =
        viewModelScope.launch {
            if (contentRepository.insertItem(itemEntity))
                itemAddSuccess("물건 추가에 성공했습니다")
            else
                itemAddError("물건 추가에 실패했습니다")
        }

    private fun updateItem(itemEntity: ItemEntity) =
        viewModelScope.launch {
            if (contentRepository.updateItem(itemEntity))
                itemAddSuccess("물건 개수 변경에 성공했습니다")
            else
                itemAddError("물건 개수 변경에 실패했습니다")
        }

    private suspend fun getItemByName(itemName: String): ItemEntity? =
        withContext(viewModelScope.coroutineContext) {
            contentRepository.getItemByName(itemName)
        }

    fun onItemAdd(itemEntity: ItemEntity) = viewModelScope.launch {
        currentItem = getItemByName(itemEntity.item_name) ?: currentItem

        if (currentItem.item_name == "") {
            if (itemIsValid(itemEntity))
                insertItem(itemEntity)
        } else {
            itemAddEvent(ItemAddEvent.ShowConfirmationDialog(true))
        }
    }

    fun onItemImageAdd(itemImage: Bitmap) =
        itemAddEvent(ItemAddEvent.ItemImageAdd(itemImage))


    private fun itemIsValid(itemEntity: ItemEntity): Boolean =
        if (itemEntity.item_name == "") {
            itemAddError("물건 이름은 필수 입니다!")
            false
        } else {
            true
        }

    private fun itemAddSuccess(message: String) =
        itemAddEvent(ItemAddEvent.Success(message))

    private fun itemAddError(message: String) =
        itemAddEvent(ItemAddEvent.Error(message))

    fun onConfirmationDialogConfirmed(itemEntity: ItemEntity) = viewModelScope.launch {
        if (currentItem.item_name != "") {
            val updatedCount = currentItem.item_count + itemEntity.item_count
            val updatedItem = currentItem.copy(item_count = updatedCount)
            updateItem(updatedItem)
        }
    }

    sealed class ItemAddEvent {
        data class Success(val message: String): ItemAddEvent()
        data class Error(val message: String): ItemAddEvent()
        data class GetInfo(val infoEntity: InfoEntity): ItemAddEvent()
        data class ItemImageAdd(val itemImage: Bitmap): ItemAddEvent()
        data class ShowConfirmationDialog(val showConfirmation: Boolean): ItemAddEvent()
    }
}

