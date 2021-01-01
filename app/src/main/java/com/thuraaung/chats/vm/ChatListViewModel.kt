package com.thuraaung.chats.vm

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.thuraaung.chats.Constants.MESSAGE_LIST
import com.thuraaung.chats.Constants.ROOM_LIST
import com.thuraaung.chats.model.Room

class ChatListViewModel @ViewModelInject constructor(
    private val auth : FirebaseAuth,
    private val db : FirebaseFirestore
): ViewModel() {

    val roomDataList = MutableLiveData<List<Room>>()
    val unReadMessage = MutableLiveData(0)

    init {
        getChatList()
    }

    private fun getChatList()  {

        db.collection(ROOM_LIST)
            .whereArrayContains("idList",auth.currentUser!!.uid)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->

                if (error != null) {
                    return@addSnapshotListener
                }

                val roomList = value?.let {

                    val tempRooms = mutableListOf<Room>()
                    for(data in it.documents) {
                        val room = data.toObject(Room::class.java)!!
                        tempRooms.add(room)
                    }

                    tempRooms

                } ?: listOf()

                roomDataList.postValue(roomList)

            }
    }

    private fun countUnreadMessage() {

        roomDataList.value?.forEach { room ->
            getUnreadMessage(room.id)
        }
    }


    private fun getUnreadMessage(roomId : String) {

        db.collection(ROOM_LIST)
            .document(roomId)
            .collection(MESSAGE_LIST)
            .whereEqualTo("seen",false)
            .whereNotEqualTo("sender",auth.currentUser!!.uid)
            .addSnapshotListener { value, error ->

//                if (error != null) {
//                    Log.d("Unread message","Unread message failed")
//                    return@addSnapshotListener
//                }

                unReadMessage.postValue( value?.size() ?: 0 )
                Log.d("Unread message","Unread message size : ${value?.size() ?: 0}")
            }
    }
}