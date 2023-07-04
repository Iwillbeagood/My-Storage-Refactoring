package com.example.mystorage.data.room

import androidx.room.*
import com.example.mystorage.data.entity.InfoEntity
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.utils.etc.ItemState
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDAO {
    // 유저 아이템 정보
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItem(itemEntity: ItemEntity)

    @Query("SELECT * FROM items_table WHERE item_state = :state")
    fun getAllItems(state: ItemState): Flow<List<ItemEntity>>?

    @Query("SELECT * FROM items_table WHERE item_ID = :itemID")
    fun getItemByItemID(itemID: Int): ItemEntity

    @Query("SELECT * FROM items_table WHERE item_name = :itemName")
    fun getItemByItemName(itemName: String): ItemEntity?

    @Update
    suspend fun updateItem(item: ItemEntity)

    @Delete
    suspend fun deleteItem(item: ItemEntity)

    @Query("DELETE FROM items_table")
    suspend fun deleteAllItems()

    // 유저의 집 정보
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInfo(infoEntity: InfoEntity)

    @Query("SELECT * FROM info_table")
    suspend fun getInfo(): InfoEntity?

    @Query("SELECT COUNT(*) FROM info_table")
    suspend fun infoCheck(): Int
}