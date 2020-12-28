package com.thuraaung.chats.vm

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.thuraaung.chats.Constants.APP_USERS
import com.thuraaung.chats.Constants.ROOM_INFO
import com.thuraaung.chats.Constants.ROOM_LIST
import com.thuraaung.chats.IDGenerator.generateRoomId
import com.thuraaung.chats.model.AppUser
import com.thuraaung.chats.model.Room
import com.thuraaung.chats.model.RoomInfo
import java.util.*

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
        return checkRoom(uidList) ?: createNewRoom(uidList)
    }

    private fun checkRoom(uidList : List<String>) : String? {
        return null
    }

    private fun createNewRoom(uidList : List<String>) : String {

        val roomId = generateRoomId()

        val roomRef = db.collection(ROOM_LIST)
            .document(roomId)

        val roomInfoRef = db.collection(ROOM_INFO)
            .document(roomId)

        db.runBatch {
            it.set(roomRef,createRoomModel(roomId,uidList))
            it.set(roomInfoRef,createRoomInfo(roomId,uidList))
        }

        return roomId
    }

    private fun createRoomInfo(roomId: String,uidList: List<String>) : RoomInfo {
        return RoomInfo(
            roomId = roomId,
            userList = uidList,
            isGroupChat = uidList.size > 2
        )
    }

    private fun createRoomModel(roomId : String, uidList : List<String>) : Room {

        return Room(
            id = roomId ,
            name = "Default room name",
            idList = uidList,
            date = Date(),
            isGroupChat = uidList.size > 2 )
    }

}