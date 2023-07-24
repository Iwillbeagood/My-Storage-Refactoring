package com.example.mystorage.ui.info

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystorage.data.entity.InfoEntity
import com.example.mystorage.data.entity.InfoNameEdit
import com.example.mystorage.data.repository.ContentRepositoryImpl
import com.example.mystorage.ui.info.adapter.InfoNameEditAdapter
import com.example.mystorage.utils.etc.Constants.TAG
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

    private fun insertInfo() =
        viewModelScope.launch {
            if (contentRepository.insertInfo(currentInfo))
                infoNameEditEvent(InfoNameEditEvent.Success("집 구조 이름을 수정했습니다"))
            else
                infoNameEditEvent(InfoNameEditEvent.Error("집 구조 이름 수정에 실패했습니다"))
        }

    private fun setEditInfo() =
        viewModelScope.launch {
            currentInfo = currentInfo.copy(
                room_names = getJsonStringFromCurrentList("room_names", InfoType.ROOM),
                bathroom_names = getJsonStringFromCurrentList("bath_names", InfoType.BATHROOM),
                etc_name = getJsonStringFromCurrentList("etc_name", InfoType.ETC)
            )
        }

    fun onInsertInfo() {
        setEditInfo()

        if (checkInfo())
            insertInfo()
        else
            infoNameEditEvent(InfoNameEditEvent.Error("집 구조 이름에는 빈칸이 들어갈 수 없습니다."))
    }

    private fun checkInfo(): Boolean =
        if (currentInfo.room_names == "")
            false
        else if (currentInfo.bathroom_names == "")
            false
        else currentInfo.etc_name != ""

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

        Log.d(TAG, "InfoNameEditViewModel - getJsonStringFromCurrentList() $currentList")

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