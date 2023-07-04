package com.example.mystorage.ui.item.used_item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.data.repository.ContentRepositoryImpl
import com.example.mystorage.ui.item.list.ItemOptionsViewModel
import com.example.mystorage.ui.item.used_item.adapter.UsedItemAdapter
import com.example.mystorage.utils.etc.ItemState
import com.example.mystorage.utils.listener.UsedItemClickListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsedItemViewModel @Inject constructor(
    private val contentRepository: ContentRepositoryImpl
): ViewModel(), UsedItemClickListener {

    lateinit var usedItemAdapter : UsedItemAdapter

    private val _eventFlow = MutableSharedFlow<UsedItemEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _usedItems = MutableLiveData<List<ItemEntity>>()
    val usedItems: LiveData<List<ItemEntity>> = _usedItems

    private val contentExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    init {
        contentRepository.flowAllItems(ItemState.USED)
            ?.onEach { items ->
                _usedItems.value = items
            }?.launchIn(viewModelScope)
    }

    private fun usedItemEvent(event: UsedItemEvent) =
        viewModelScope.launch {
            _eventFlow.emit(event)
        }

    private fun deleteItem(itemEntity: ItemEntity) =
        viewModelScope.launch {
            if (contentRepository.deleteItem(itemEntity))
                usedItemEventSuccess("사용 완료된 물건을 전부 비웠습니다")
            else
                usedItemEventError("사용 완료된 물건을 전부 비우지 못했습니다")
        }

    fun onUsedItemDeleteAll() =
        viewModelScope.launch {
            _usedItems.value!!
                .forEach { item -> deleteItem(item) }
        }

    override fun onUsedItemClick(position: Int) {
        val item = usedItemAdapter[position]
        usedItemEvent(UsedItemEvent.SendItem(item))
    }

    private fun usedItemEventSuccess(message: String) =
        usedItemEvent(UsedItemEvent.Success(message))

    private fun usedItemEventError(message: String) =
        usedItemEvent(UsedItemEvent.Error(message))

    sealed class UsedItemEvent {
        data class Success(val message: String): UsedItemEvent()
        data class Error(val message: String): UsedItemEvent()
        data class SendItem(val item: ItemEntity): UsedItemEvent()
    }
}