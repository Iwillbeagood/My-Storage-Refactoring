package com.example.mystorage.data.repository

import com.example.mystorage.data.entity.InfoEntity
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.utils.etc.ItemState
import kotlinx.coroutines.flow.Flow

interface ContentRepository {
    fun flowAllItems(itemState: ItemState): Flow<List<ItemEntity>>?

    suspend fun getItemByID(itemID: Int): ItemEntity

    suspend fun getItemByName(itemName: String): ItemEntity?

    suspend fun insertItem(itemEntity: ItemEntity): Boolean

    suspend fun updateItem(itemEntity: ItemEntity): Boolean

    suspend fun deleteItem(itemEntity: ItemEntity): Boolean

    suspend fun deleteAllItems(): Boolean

    suspend fun insertInfo(infoEntity: InfoEntity): Boolean

    suspend fun getInfo(): InfoEntity?

    suspend fun infoCheck(): Boolean
}