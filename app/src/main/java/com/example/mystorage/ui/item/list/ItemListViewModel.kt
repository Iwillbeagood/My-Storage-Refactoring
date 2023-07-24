package com.example.mystorage.ui.item.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystorage.data.entity.InfoEntity
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.data.entity.ListItem
import com.example.mystorage.data.repository.ContentRepositoryImpl
import com.example.mystorage.ui.item.list.adapter.ItemListAdapter
import com.example.mystorage.utils.etc.ItemState
import com.example.mystorage.utils.listener.ItemClickListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemListViewModel @Inject constructor(
    private val contentRepository: ContentRepositoryImpl
): ViewModel(), ItemClickListener {

    lateinit var listAdapter: ItemListAdapter

    private val _eventFlow = MutableSharedFlow<ItemListEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _items = MutableLiveData<List<ListItem>>()
    val items: LiveData<List<ListItem>> = _items

    private val _originalItems = MutableLiveData<List<ItemEntity>>()

    private val contentExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        itemListError(throwable.message.toString())
    }

    init {
        contentRepository.flowAllItems(ItemState.NOT_USED)
            ?.onEach { items ->
                _items.value = items.map { ListItem(it.copy(isSelected = false), false) }
                _originalItems.value = items
            }?.launchIn(viewModelScope)

        viewModelScope.launch {
            contentRepository.getInfo()?.let { infoEntity ->
                itemListEvent(ItemListEvent.GetInfo(infoEntity))
            }
        }
    }

    private fun itemListEvent(event: ItemListEvent) =
        viewModelScope.launch {
            _eventFlow.emit(event)
        }

    private fun updateItem(itemEntity: ItemEntity) =
        viewModelScope.launch {
            contentRepository.updateItem(itemEntity)
        }

    private fun deleteItem(itemEntity: ItemEntity) =
        viewModelScope.launch {
            contentRepository.deleteItem(itemEntity)
        }

    fun onSelectedItemDelete() =
        viewModelScope.launch {
            _items.value?.let { items ->
                items.filter { it.itemEntity.isSelected }.takeIf { it.isNotEmpty() }?.forEach { item ->
                    deleteItem(item.itemEntity)
                } ?: itemListError("선택된 물건이 없습니다")
            }
            itemListSuccess("선택된 물건을 삭제했습니다")
            exitLongMode()
        }

    fun onUpdateSelectedItemPlace(updatePlace: String) =
        viewModelScope.launch {
            _items.value?.let { items ->
                items.filter { it.itemEntity.isSelected }.takeIf { it.isNotEmpty() }?.forEach { item ->
                    updateItem(item.itemEntity.copy(item_place = updatePlace))
                } ?: itemListError("선택된 물건이 없습니다")
            }
            itemListSuccess("선택된 물건을 이동시켰습니다")
            exitLongMode()
        }

    fun updateAllItemsSelectedState(isSelected: Boolean) {
        _items.value = _items.value?.map { item ->
            item.copy(itemEntity = item.itemEntity.copy(isSelected = isSelected))
        }
    }

    fun changeClickMode(mode: Boolean) {
        _items.value = _items.value?.map { item ->
            item.copy(clickMode = mode)
        }
    }

    fun filterItemList(searchText: String) {
        _items.value = _originalItems.value?.filter { item ->
            item.item_name.contains(searchText, ignoreCase = true)
        }?.map { ListItem(it, false) }
    }

    override fun onItemClick(position: Int, state: Boolean) {
        if (!state) {
            itemListEvent(ItemListEvent.SendItem(listAdapter[position].itemEntity))
        } else {
            onClickInLongMode(position)
        }
    }

    override fun onItemLongClick(position: Int, state: Boolean) {
        if (!state) {
            itemListEvent(ItemListEvent.LongClickMode(true))
            onLongClick(position)
        } else {
            exitLongMode()
        }
    }

    private fun onClickInLongMode(position: Int) {
        val selectedItemID = listAdapter[position].itemEntity.item_ID
        _items.value = _items.value?.map { item ->
            if (item.itemEntity.item_ID == selectedItemID) {
                item.copy(itemEntity = item.itemEntity.copy(isSelected = !item.itemEntity.isSelected))
            } else {
                item
            }
        }
    }

    private fun onLongClick(position: Int) {
        val selectedItemID = listAdapter[position].itemEntity.item_ID
        _items.value = _items.value?.map { item ->
            if (item.itemEntity.item_ID == selectedItemID) {
                item.copy(itemEntity = item.itemEntity.copy(isSelected = !item.itemEntity.isSelected))
            } else {
                item
            }.copy(clickMode = true)
        }
    }

    fun exitLongMode() {
        itemListEvent(ItemListEvent.LongClickMode(false))
        _items.value = _items.value?.map { item ->
            item.copy(itemEntity = item.itemEntity.copy(isSelected = false))
                .copy(clickMode = false)
        }
    }

    private fun itemListSuccess(message: String) =
        itemListEvent(ItemListEvent.Success(message))

    private fun itemListError(message: String) =
        itemListEvent(ItemListEvent.Error(message))

    sealed class ItemListEvent {
        data class Success(val message: String): ItemListEvent()
        data class Error(val message: String): ItemListEvent()
        data class SendItem(val item: ItemEntity): ItemListEvent()
        data class GetInfo(val info: InfoEntity): ItemListEvent()
        data class LongClickMode(val state: Boolean): ItemListEvent()
    }
}