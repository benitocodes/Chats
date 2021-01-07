package com.thuraaung.chats

import com.google.firebase.auth.FirebaseAuth
import com.thuraaung.chats.model.AppUser
import java.util.*


fun FirebaseAuth.getCurrentAppUser() : AppUser {
    return AppUser(
        uid = currentUser!!.uid,
        name = currentUser!!.displayName.toString(),
        photoUrl = currentUser!!.photoUrl.toString(),
        email = currentUser!!.email.toString(),
        signInDate = Date(),
        isOnline = true)
}