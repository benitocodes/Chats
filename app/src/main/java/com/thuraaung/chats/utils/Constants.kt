package com.thuraaung.chats.utils

object Constants {

    const val APP_USERS = "APP_USERS"
    const val MESSAGE_LIST = "MESSAGE_LIST"
    const val CHAT_LIST = "CHAT_LIST"
    const val CHAT_INFO = "CHAT_INFO"
    const val TOKEN = "TOKEN"
    const val CHAT_PREF = "CHAT_PREF"
    const val CHATTING_USER = "CHATTING_USER"
    const val DEFAULT_USER = "DEFAULT_USER"

    const val BASE_URL = "https://fcm.googleapis.com"
    const val SERVER_KEY = "AAAAcnDauDU:APA91bFEQnFGC1h0E4lg60Khq8si7Ne7FctzkwYwwMedwfzaZnugrK0hhj2h7P3VkMPvFjnjSuAxCohLrZlQqPOuWyRXosjtEI68VJoqrzPfUiEmAPigngifUw9UcH-xzxEOhvqgI34v"
    const val CONTENT_TYPE = "application/json"

    fun getUserRef(uid : String) : String {
        return "$APP_USERS/$uid"
    }



}