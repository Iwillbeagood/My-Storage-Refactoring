package com.example.mystorage.utils.etc

import com.example.mystorage.data.entity.InfoEntity
import org.json.JSONObject

object LoadInfoForSpinner {
    fun infoToList(infoEntity: InfoEntity): MutableList<String> {
        val infoList = mutableListOf<String>()
        val jsonObjectRoom = JSONObject(infoEntity.room_names.toString())
        val roomNames = jsonObjectRoom.getJSONArray("room_names")
        for (i in 0 until roomNames.length()) {
            val roomName = roomNames.getString(i)
            infoList.add(roomName)
        }

        val jsonObjectBath = JSONObject(infoEntity.bathroom_names.toString())
        val bathNames = jsonObjectBath.getJSONArray("bath_names")
        for (i in 0 until bathNames.length()) {
            val bathName = bathNames.getString(i)
            infoList.add(bathName)
        }

        if (infoEntity.living_room) infoList.add("거실")
        if (infoEntity.kitchen) infoList.add("주방")
        if (infoEntity.storage) infoList.add("창고")

        val jsonObjectEtc = JSONObject(infoEntity.etc_name.toString())
        val etcNames = jsonObjectEtc.getJSONArray("etc_name")
        for (i in 0 until etcNames.length()) {
            val etcName = etcNames.getString(i)
            infoList.add(etcName)
        }

        return infoList
    }

    fun infoToListForEdit(infoEntity: InfoEntity): Triple<List<String>, List<String>, List<String>> {
        val roomList = mutableListOf<String>()
        val jsonObjectRoom = JSONObject(infoEntity.room_names.toString())
        val roomNames = jsonObjectRoom.getJSONArray("room_names")
        for (i in 0 until roomNames.length()) {
            val roomName = roomNames.getString(i)
            roomList.add(roomName)
        }

        val bathList = mutableListOf<String>()
        val jsonObjectBath = JSONObject(infoEntity.bathroom_names.toString())
        val bathNames = jsonObjectBath.getJSONArray("bath_names")
        for (i in 0 until bathNames.length()) {
            val bathName = bathNames.getString(i)
            bathList.add(bathName)
        }

        val etcList = mutableListOf<String>()
        val jsonObjectEtc = JSONObject(infoEntity.etc_name.toString())
        val etcNames = jsonObjectEtc.getJSONArray("etc_name")
        for (i in 0 until etcNames.length()) {
            val etcName = etcNames.getString(i)
            etcList.add(etcName)
        }

        return Triple(roomList, bathList, etcList)
    }
}