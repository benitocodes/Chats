package com.thuraaung.chats.model

import java.util.*

data class AppUser(
    val uid : String,
    val name : String,
    val email : String,
    val lastSignIn : Date,
    val isOnline : Boolean)