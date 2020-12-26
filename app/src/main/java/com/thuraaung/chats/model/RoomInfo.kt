package com.thuraaung.chats.model

data class RoomInfo(
    val roomId : String,
    val userList : List<String>,
    val isGroupChat : Boolean,
)