package com.yaropaul.chatbotaiassistant.data.model

import java.util.Date
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val id: String,
    val text: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val date: Date,
    val imageUrl: String?
)