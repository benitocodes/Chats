package com.thuraaung.chats.temp

import com.thuraaung.chats.model.Message
import java.util.*

object MessageFactory {

    private val dummyMessage = listOf(
        "Hello world from message testing",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua",
        " Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
        " Sem nulla pharetra diam sit amet. Adipiscing commodo elit at imperdiet dui.")

    fun getMessageList(size : Int) : List<Message> {

        val messageList = mutableListOf<Message>()

        for (i in 1..size) {
            val message = Message(
                id = i,
                message = getRandomMessage(),
                sender = i % 2,
                date = Date())

            messageList.add(message)
        }

        return messageList
    }

    private fun getRandomMessage() : String {

        val random = Random()
        return dummyMessage[ random.nextInt(dummyMessage.size) ]

    }
}