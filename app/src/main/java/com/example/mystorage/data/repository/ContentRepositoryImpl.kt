package com.example.mystorage.data.repository

import com.example.mystorage.data.entity.InfoEntity
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.data.repository.ContentRepository
import com.example.mystorage.data.room.ContentDAO
import com.example.mystorage.di.coroutines.IoDispatcher
import com.example.mystorage.utils.etc.ItemState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContentRepositoryImpl @Inject constructor(
    private val contentDAO: ContentDAO,
    @IoDispatcher private val isDispatcher: CoroutineDispatcher
): ContentRepository {
    override fun flowAllItems(itemState: ItemState): Flow<List<ItemEntity>>? =
        contentDAO.getAllItems(itemState)

    override suspend fun getItemByID(itemID: Int): ItemEntity = withContext(isDispatcher) {
        contentDAO.getItemByItemID(itemID)
    }

    override suspend fun getItemByName(itemName: String): ItemEntity? = withContext(isDispatcher) {
        contentDAO.getItemByItemName(itemName)
    }

    override suspend fun insertItem(itemEntity: ItemEntity): Boolean = withContext(isDispatcher) {
        try {
            contentDAO.insertItem(itemEntity)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun updateItem(itemEntity: ItemEntity): Boolean = withContext(isDispatcher) {
        try {
            contentDAO.updateItem(itemEntity)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    override suspend fun deleteItem(itemEntity: ItemEntity): Boolean = withContext(isDispatcher) {
        try {
            contentDAO.deleteItem(itemEntity)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun deleteAllItems(): Boolean = withContext(isDispatcher) {
        try {
            contentDAO.deleteAllItems()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun insertInfo(infoEntity: InfoEntity): Boolean = withContext(isDispatcher) {
        try {
            contentDAO.insertInfo(infoEntity)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getInfo(): InfoEntity? = withContext(isDispatcher) {
        contentDAO.getInfo()
    }

    override suspend fun infoCheck(): Boolean = withContext(isDispatcher) {
        contentDAO.infoCheck() > 0
    }
}