package com.example.mystorage.ui.item.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.data.repository.ContentRepositoryImpl
import com.example.mystorage.ui.item.list.adapter.ItemListAdapter
import com.example.mystorage.utils.etc.Constants.TAG
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

    private val _items = MutableLiveData<List<ItemEntity>>()
    val items: LiveData<List<ItemEntity>> = _items

    private val _originalItems = MutableLiveData<List<ItemEntity>>()

    private val contentExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        itemListError(throwable.message.toString())
    }

    init {
        contentRepository.flowAllItems(ItemState.NOT_USED)
            ?.onEach { items ->
                _items.value = items
                _originalItems.value = items
            }?.launchIn(viewModelScope)
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
            _items.value!!
                .filter { it.isSelected }
                .forEach { item -> deleteItem(item) }
        }

    fun onUpdateSelectedItemPlace(updatePlace: String) =
        viewModelScope.launch {
            _items.value!!
                .filter { it.isSelected }
                .forEach { item -> updateItem(item.copy(item_place = updatePlace)) }
        }

    fun updateAllItemsSelectedState(isSelected: Boolean) {
        _items.value = _items.value?.map { item ->
            item.copy(isSelected = isSelected)
        }
    }

    fun filterItemList(searchText: String) {
        _items.value = _originalItems.value?.filter { item ->
            item.item_name.contains(searchText, ignoreCase = true)
        }
    }

    override fun onItemClick(position: Int, state: Boolean) {
        val item = listAdapter[position]

        Log.d(TAG, "ItemListViewModel - onItemClick() $item")

        if (!state) {
            itemListEvent(ItemListEvent.SendItem(item))
        } else {
            onItemLongModeClick(position)
        }
    }

    override fun onItemLongClick(position: Int, state: Boolean) {
        val selectedItem = listAdapter[position]

        if (!state) {
            itemListEvent(ItemListEvent.LongClickMode(true))
            _items.value = _items.value?.map { item ->
                item.copy(isSelected = item == selectedItem && item.isSelected.not())
            }
        } else {
            itemListEvent(ItemListEvent.LongClickMode(false))
        }
    }

    private fun onItemLongModeClick(position: Int) {
        val selectedItem = listAdapter[position]
        _items.value = _items.value?.map { item ->
            item.copy(isSelected = item == selectedItem && item.isSelected.not())
        }
    }

    fun exitItemLongMode() {
        _items.value = _items.value?.map { item ->
            item.copy(isSelected = false)
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
        data class LongClickMode(val state: Boolean): ItemListEvent()
    }
}