package com.example.mystorage.ui.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystorage.data.entity.InfoEntity
import com.example.mystorage.data.entity.InfoName
import com.example.mystorage.data.repository.ContentRepositoryImpl
import com.example.mystorage.ui.info.adapter.InfoNameAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val contentRepository: ContentRepositoryImpl
) : ViewModel() {

    lateinit var roomNameAdapter : InfoNameAdapter
    lateinit var bathNameAdapter : InfoNameAdapter

    private val _eventFlow = MutableSharedFlow<InfoEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val contentExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    private var currentInfo = InfoEntity(0,
        living_room = false,
        kitchen = false,
        storage = false,
        room_names = null,
        bathroom_names = null,
        etc_name = null
    )

    private fun infoEvent(event: InfoEvent) =
        viewModelScope.launch {
            _eventFlow.emit(event)
        }

    private fun insertInfo(infoEntity: InfoEntity) =
        viewModelScope.launch {
            if (contentRepository.insertInfo(infoEntity))
                infoEvent(InfoEvent.Success("정보 저장에 성공했습니다"))
            else
                infoEvent(InfoEvent.Error("정보 저장에 실패했습니다"))
        }

    fun onInsertInfo(etc_name: String) =
        viewModelScope.launch {
            currentInfo = currentInfo.copy(
                room_names = getJsonStringRoomNameFromCurrentList(),
                bathroom_names = getJsonStringBathNameFromCurrentList(),
                etc_name = etc_name)

            insertInfo(currentInfo)
        }


    fun currentInfoUpdate(living_room: Boolean, kitchen: Boolean, storage: Boolean) {
        currentInfo = currentInfo.copy(
            living_room = living_room,
            kitchen = kitchen,
            storage = storage)
    }

    fun setRoomNames(room_count: Int) {
        val roomList = mutableListOf<InfoName>()
        roomList.clear()
        if (room_count >= 1) {
            roomList.add(InfoName("안방", true))
            for (i in 1 until room_count)
                roomList.add(InfoName("방 $i", true))
        }

        infoEvent(InfoEvent.GetRoomNames(roomList))
    }

    fun setBathNames(bathroom_count: Int) {
        val bathList = mutableListOf<InfoName>()
        bathList.clear()
        if (bathroom_count >= 1) {
            bathList.add(InfoName("화장실", false))
            for (i in 1 until bathroom_count)
                bathList.add(InfoName("화장실 ${i + 1}", false))
        }

        infoEvent(InfoEvent.GetBathNames(bathList))
    }

    private fun getJsonStringRoomNameFromCurrentList(): String {
        val roomNameList = roomNameAdapter.currentList
        val roomJsonArray = JSONArray()

        roomNameList.map {
            roomJsonArray.put(it.name)
        }

        return JSONObject().put("room_names", roomJsonArray).toString()
    }

    private fun getJsonStringBathNameFromCurrentList(): String {
        val bathNameList = bathNameAdapter.currentList
        val bathJsonArray = JSONArray()

        bathNameList.map {
            bathJsonArray.put(it.name)
        }

        return JSONObject().put("bath_names", bathJsonArray).toString()
    }

    sealed class InfoEvent {
        data class Success(val message: String): InfoEvent()
        data class Error(val message: String): InfoEvent()
        data class GetRoomNames(val roomNames: List<InfoName>): InfoEvent()
        data class GetBathNames(val bathNames: List<InfoName>): InfoEvent()
    }
}