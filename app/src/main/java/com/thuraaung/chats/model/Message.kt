package com.thuraaung.chats.model

import java.util.*

data class Message(
    val id : String = "",
    val message : String = "",
    val sender : String = "",
    val isSend : Boolean = false,
    val date : Date = Date()
)