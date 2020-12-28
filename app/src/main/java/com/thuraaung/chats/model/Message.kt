package com.thuraaung.chats.model

import java.util.*

data class Message(
    val id : String = "",
    val roomId : String = "",
    val message : String = "",
    val date : Date = Date(),
    val sender : String = "",
    val status : String = ""
)