package com.example.mystorage.data.entity

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mystorage.utils.etc.InfoType
import com.example.mystorage.utils.etc.ItemState
import java.sql.Timestamp

@Entity(tableName = "items_table")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val item_ID : Int = 0,
    val item_name : String,
    val item_image : Bitmap?,
    val item_place : String?,
    val item_store : String?,
    val item_count : Int,
    val item_state : ItemState,
    val created_at : Timestamp,
    val updated_at : Timestamp,
    val isSelected : Boolean
)

@Entity(tableName = "info_table")
data class InfoEntity(
    @PrimaryKey(autoGenerate = true) val infoID : Int,
    val living_room : Boolean,
    val kitchen : Boolean,
    val storage : Boolean,
    val room_names : String?,
    val bathroom_names : String?,
    val etc_name : String?
)

data class InfoName(
    val name: String,
    val isRoom: Boolean
)

data class InfoNameEdit(
    val origin_name: String,
    val new_name: String,
    val type: InfoType
)

data class ItemGroup(
    val group_name: String,
    val itemList: List<ItemEntity>
)

