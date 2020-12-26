package com.thuraaung.chats.vm

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.thuraaung.chats.Constants
import com.thuraaung.chats.Constants.APP_USERS
import com.thuraaung.chats.Constants.ROOM_INFO
import com.thuraaung.chats.IDGenerator
import com.thuraaung.chats.IDGenerator.generateRoomId
import com.thuraaung.chats.model.AppUser
import com.thuraaung.chats.model.Room

class UserViewModel @ViewModelInject constructor(
    private val db : FirebaseFirestore) : ViewModel() {

    val userList = MutableLiveData<List<AppUser>>()

    init {
        loadUserList()
    }

    private fun loadUserList() {

        db.collection(APP_USERS)
            .addSnapshotListener { snapshot , error ->

                if (error != null) {
                    Log.d(UserViewModel::class.simpleName,"Load user list failed")
                    return@addSnapshotListener
                }

                userList.postValue(snapshot?.let {

                    val tempUsers = mutableListOf<AppUser>()
                    for (data in snapshot.iterator()) {
                        val user = data.toObject(AppUser::class.java)
                        tempUsers.add(user)
                    }

                    tempUsers

                } ?: listOf())

            }
    }

    fun getRoomId(uidList : List<String>) : String {

        //Todo check room exist for uidList

        val query : Query = db.collection(ROOM_INFO)
            .whereIn("userList",uidList)

        return createNewRoom(uidList)
    }

    private fun createNewRoom(uidList : List<String>) : String {

        val roomId = generateRoomId()

        //Todo add new room info for new room

        db.collection(Constants.ROOM_LIST)
            .document(roomId)
            .set(createRoomModel(roomId,uidList))

        return roomId
    }

    private fun createRoomModel(roomId : String, uidList : List<String>) : Room {

        return Room(
            id = roomId ,
            name = "Default room name",
            idList = uidList,
            isGroupChat = uidList.size > 2 )
    }

}