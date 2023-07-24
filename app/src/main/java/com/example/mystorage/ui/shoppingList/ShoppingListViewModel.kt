package com.example.mystorage.ui.shoppingList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.data.repository.ContentRepositoryImpl
import com.example.mystorage.ui.item.used_item.UsedItemViewModel
import com.example.mystorage.ui.shoppingList.adapter.ShoppingListAdapter
import com.example.mystorage.utils.etc.ItemState
import com.example.mystorage.utils.listener.ShoppingListClickListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val contentRepository: ContentRepositoryImpl
): ViewModel(), ShoppingListClickListener {

    lateinit var shoppingListAdapter : ShoppingListAdapter

    private val _eventFlow = MutableSharedFlow<ShoppingListEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _usedItems = MutableLiveData<List<ItemEntity>>()
    val usedItems: LiveData<List<ItemEntity>> = _usedItems

    init {
        contentRepository.flowAllItems(ItemState.USED)
            ?.onEach { items ->
                _usedItems.value = items
            }?.launchIn(viewModelScope)
    }

    private fun shoppingListEvent(event: ShoppingListEvent) =
        viewModelScope.launch {
            _eventFlow.emit(event)
        }

    private fun updateItem(itemEntity: ItemEntity) =
        viewModelScope.launch {
            if (contentRepository.updateItem(itemEntity))
                shoppingEventSuccess("쇼핑한 물건을 창고에 넣었습니다")
            else
                shoppingEventError("쇼핑한 물건을 창고에 넣지 못했습니다")
        }

    fun onShoppingCompleted() {
        _usedItems.value?.filter { it.isSelected }.takeIf { it!!.isNotEmpty() }?.forEach { item ->
            updateItem(item.copy(item_state = ItemState.NOT_USED))
        } ?: shoppingEventError("쇼핑한 물건을 선택해 주세요")
    }

    override fun onShoppingListClicked(position: Int, selected: Boolean) {
        val selectedItem = shoppingListAdapter[position].item_ID
        _usedItems.value = _usedItems.value?.map { item ->
            if (item.item_ID == selectedItem) {
                item.copy(isSelected = !item.isSelected)
            } else {
                item
            }
        }
    }

    private fun shoppingEventSuccess(message: String) =
        shoppingListEvent(ShoppingListEvent.Success(message))

    private fun shoppingEventError(message: String) =
        shoppingListEvent(ShoppingListEvent.Error(message))

    sealed class ShoppingListEvent {
        data class Success(val message: String): ShoppingListEvent()
        data class Error(val message: String): ShoppingListEvent()
    }
}