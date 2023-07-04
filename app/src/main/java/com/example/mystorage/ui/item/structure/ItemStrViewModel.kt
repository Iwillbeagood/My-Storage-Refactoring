package com.example.mystorage.ui.item.structure

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.data.repository.ContentRepositoryImpl
import com.example.mystorage.ui.item.structure.adapter.ItemStrAdapter
import com.example.mystorage.utils.etc.ItemState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemStrViewModel @Inject constructor(
    private val contentRepository: ContentRepositoryImpl
): ViewModel() {

    lateinit var itemStrAdapter: ItemStrAdapter

    private val _eventFlow = MutableSharedFlow<ItemStrEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _items = MutableLiveData<List<ItemEntity>>()
    val items: LiveData<List<ItemEntity>> = _items

    private val _originalItems = MutableLiveData<List<ItemEntity>>()

    private val contentExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    init {
        contentRepository.flowAllItems(ItemState.NOT_USED)
            ?.onEach { items ->
                _items.value = items
                _originalItems.value = items
            }?.launchIn(viewModelScope)
    }

    private fun itemListEvent(event: ItemStrEvent) =
        viewModelScope.launch {
            _eventFlow.emit(event)
        }

    fun filterItemStrList(place_name: String) =
        if (place_name == "전체") {
            _items.value = _originalItems.value
        } else {
            _items.value = _originalItems.value?.filter { item ->
                item.item_place.equals(place_name)
            }
        }


    sealed class ItemStrEvent {
        data class Success(val message: String): ItemStrEvent()
        data class Error(val message: String): ItemStrEvent()
    }
}