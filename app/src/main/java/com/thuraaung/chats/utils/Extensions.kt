package com.thuraaung.chats

import com.google.firebase.auth.FirebaseAuth
import com.thuraaung.chats.model.AppUser
import java.util.*


fun FirebaseAuth.currentUid() : String {
    return currentUser?.uid ?: throw Exception("Current user not found")
}