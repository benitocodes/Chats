package com.thuraaung.chats.model

import java.util.*

data class Room(
    val id : String = "",
    val name : String = "",
    val isGroupChat : Boolean = false,
    val date : Date = Date(),
    val idList : List<String> = listOf())