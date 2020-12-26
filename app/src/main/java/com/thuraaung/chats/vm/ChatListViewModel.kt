package com.thuraaung.chats.vm

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thuraaung.chats.Constants.ROOM_LIST
import com.thuraaung.chats.model.Room

class ChatListViewModel @ViewModelInject constructor(
    private val auth : FirebaseAuth,
    private val db : FirebaseFirestore
): ViewModel() {

    val roomList = MutableLiveData<List<Room>>()

    init {
        getChatList()
    }

    fun getChatList()  {

        db.collection(ROOM_LIST)
            .whereArrayContains("idList",auth.currentUser!!.uid)
            .addSnapshotListener { value, error ->

                if (error != null) {
                    return@addSnapshotListener
                }

                roomList.postValue(value?.let {

                    val tempRooms = mutableListOf<Room>()
                    for(data in it.documents) {
                        val room = data.toObject(Room::class.java)!!
                        tempRooms.add(room)
                    }
                    tempRooms
                } ?: listOf())

            }

    }
}