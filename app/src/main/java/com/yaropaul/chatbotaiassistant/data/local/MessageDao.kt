package com.yaropaul.chatbotaiassistant.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yaropaul.chatbotaiassistant.data.model.Message

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY date ASC")
    suspend fun getAllMessages(): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
}