package com.yaropaul.chatbotaiassistant.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yaropaul.chatbotaiassistant.data.model.Message
import com.yaropaul.chatbotaiassistant.utils.Converters

@Database(entities = [Message::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}