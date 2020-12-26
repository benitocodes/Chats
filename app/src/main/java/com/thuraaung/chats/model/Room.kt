package com.thuraaung.chats.model

data class Room(
    val id : String = "",
    val name : String = "",
    val isGroupChat : Boolean = false,
    val idList : List<String> = listOf())