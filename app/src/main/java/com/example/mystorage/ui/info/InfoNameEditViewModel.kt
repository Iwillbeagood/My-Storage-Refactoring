package com.example.mystorage.ui.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystorage.data.entity.InfoEntity
import com.example.mystorage.data.entity.InfoNameEdit
import com.example.mystorage.data.repository.ContentRepositoryImpl
import com.example.mystorage.ui.info.adapter.InfoNameEditAdapter
import com.example.mystorage.utils.etc.InfoType
import com.example.mystorage.utils.etc.LoadInfoForSpinner.infoToListForEdit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class InfoNameEditViewModel @Inject constructor(
    private val contentRepository: ContentRepositoryImpl
): ViewModel() {

    lateinit var infoNameEditAdapter: InfoNameEditAdapter

    private val _eventFlow = MutableSharedFlow<InfoNameEditEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentInfo = InfoEntity(0,
        living_room = false,
        kitchen = false,
        storage = false,
        room_names = null,
        bathroom_names = null,
        etc_name = null
    )

    init {
        viewModelScope.launch {
            contentRepository.getInfo()?.let { infoEntity ->
                currentInfo = infoEntity
                infoNameEditEvent(InfoNameEditEvent.GetInfoNameList(setList(infoEntity)))
            }
        }
    }

    private fun infoNameEditEvent(event: InfoNameEditEvent) =
        viewModelScope.launch {
            _eventFlow.emit(event)
        }

    private fun insertInfo(infoEntity: InfoEntity) =
        viewModelScope.launch {
            contentRepository.insertInfo(infoEntity)
        }

    fun onInsertInfo() =
        viewModelScope.launch {
            currentInfo = currentInfo.copy(
                room_names = getJsonStringFromCurrentList("room_names", InfoType.ROOM),
                bathroom_names = getJsonStringFromCurrentList("bath_names", InfoType.BATHROOM),
                etc_name = getJsonStringFromCurrentList("etc_name", InfoType.ETC)
            )
        }

    private fun setList(infoEntity: InfoEntity): List<InfoNameEdit> {
        val (roomList, bathList, etcList) = infoToListForEdit(infoEntity)
        val infoNameList = mutableListOf<InfoNameEdit>()

        if (roomList.isNotEmpty()) {
            infoNameList.add(InfoNameEdit("안방", roomList.first(), InfoType.ROOM))

            for (i in 1 until roomList.size)
                infoNameList.add(InfoNameEdit("방 $i", roomList[i], InfoType.ROOM))
        }

        bathList.mapIndexed { i, name ->
            infoNameList.add(InfoNameEdit("화장실 ${i + 1}", name, InfoType.BATHROOM))
        }

        etcList.map { name ->
            infoNameList.add(InfoNameEdit(name, name, InfoType.ETC))
        }

        return infoNameList
    }

    private fun getJsonStringFromCurrentList(keyValue: String, type: InfoType): String {
        val currentList = infoNameEditAdapter.currentList
        val jsonArray = JSONArray()

        currentList.filter { it.type == type }.map {
            jsonArray.put(it.new_name)
        }

        return JSONObject().put(keyValue, jsonArray).toString()
    }


    sealed class InfoNameEditEvent {
        data class Success(val message: String): InfoNameEditEvent()
        data class Error(val message: String): InfoNameEditEvent()
        data class GetInfoNameList(val infoNameList: List<InfoNameEdit>): InfoNameEditEvent()
    }
}