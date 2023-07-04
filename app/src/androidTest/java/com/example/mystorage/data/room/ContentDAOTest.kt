package com.example.mystorage.data.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.mystorage.data.entity.InfoEntity
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.utils.etc.ItemState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import java.sql.Timestamp

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ContentDAOTest {

    private val item1 = ItemEntity(
        item_ID = 1,
        item_name = "Item 1",
        item_image = null,
        item_place = "",
        item_store = "",
        item_count = 1,
        item_state = ItemState.NOT_USED,
        created_at = Timestamp(System.currentTimeMillis()),
        updated_at = Timestamp(System.currentTimeMillis()),
        isSelected = false
    )

    private val item2 = ItemEntity(
        item_ID = 2,
        item_name = "Item 2",
        item_image = null,
        item_place = "",
        item_store = "",
        item_count = 2,
        item_state = ItemState.NOT_USED,
        created_at = Timestamp(System.currentTimeMillis()),
        updated_at = Timestamp(System.currentTimeMillis()),
        isSelected = false
    )

    private lateinit var database: ContentRoomDB
    private lateinit var contentDao: ContentDAO

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ContentRoomDB::class.java
        ).allowMainThreadQueries().build()
        contentDao = database.contentDao()
    }

    @Test
    fun `물건_추가하고_이름으로_찾기`() = runTest {
        contentDao.insertItem(item1)

        val retrievedItem = contentDao.getItemByItemName("Item 1")
        assertEquals(item1, retrievedItem)
    }

    @Test
    fun `ID로_물건찾기`() = runTest {
        contentDao.insertItem(item1)
        contentDao.insertItem(item2)

        val retrievedItem1 = contentDao.getItemByItemID(item1.item_ID)
        val retrievedItem2 = contentDao.getItemByItemID(item2.item_ID)

        assertEquals(item1, retrievedItem1)
        assertEquals(item2, retrievedItem2)
    }

    @Test
    fun `물건_업데이트`() = runTest {
        contentDao.insertItem(item1)

        val updatedItem = item1.copy(item_name = "Updated Item")
        contentDao.updateItem(updatedItem)

        val retrievedItem = contentDao.getItemByItemID(1)
        assertEquals(updatedItem, retrievedItem)
    }

    @Test
    fun `물건_삭제`() = runTest {
        contentDao.insertItem(item1)

        contentDao.deleteItem(item1)

        val retrievedItem = contentDao.getItemByItemID(1)
        assertNull(retrievedItem)
    }

    @Test
    fun `유저_집_정보_저장_및_조회`() = runTest {
        val infoEntity = InfoEntity(
            infoID = 1,
            living_room = true,
            kitchen = true,
            storage = false,
            room_names = "1",
            bathroom_names = "2",
            etc_name = "3"
        )

        contentDao.insertInfo(infoEntity)
        val retrievedInfo = contentDao.getInfo()
        val infoCount = contentDao.infoCheck()

        assertEquals(infoEntity, retrievedInfo)
        assertEquals(1, infoCount)
    }

    @After
    fun cleanup() {
        database.close()
    }
}