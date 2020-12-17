package com.thuraaung.chats.model

import java.util.*

data class Message(
    val id : Int,
    val message : String,
    val sender : Int,
    val date : Date
)