package com.thuraaung.chats

import java.util.*

object IDGenerator {

    private fun getUUID() : String {
        return UUID.randomUUID().toString()
    }

    fun generateRoomId() : String {
        return "Room_${getUUID()}"
    }

    fun generateMessageId() : String {
        return getUUID()
    }
}