package com.thuraaung.chats.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class AppUser(
    val uid : String = "",
    val name : String = "",
    val photoUrl : String = "",
    val email : String = "",
    val signInDate : Date = Date(),
    val isOnline : Boolean = false) : Parcelable