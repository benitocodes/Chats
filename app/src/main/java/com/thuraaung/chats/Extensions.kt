package com.thuraaung.chats

import com.google.firebase.auth.FirebaseAuth
import com.thuraaung.chats.model.AppUser
import com.thuraaung.chats.model.MessageSender
import java.util.*


fun FirebaseAuth.getMessageSender() : MessageSender {

    return MessageSender(
        uid = currentUser!!.uid,
        name = currentUser!!.displayName.toString(),
        photoUrl = currentUser!!.photoUrl.toString(),
        email = currentUser!!.email.toString()
    )
}

fun FirebaseAuth.getCurrentAppUser() : AppUser {
    return AppUser(
        uid = currentUser!!.uid,
        name = currentUser!!.displayName.toString(),
        photoUrl = currentUser!!.photoUrl.toString(),
        email = currentUser!!.email.toString(),
        signInDate = Date(),
        isOnline = true)
}