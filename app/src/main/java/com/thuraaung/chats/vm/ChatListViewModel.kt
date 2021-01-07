package com.thuraaung.chats.vm

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.thuraaung.chats.Constants.CHAT_INFO
import com.thuraaung.chats.Constants.CHAT_LIST
import com.thuraaung.chats.model.Chat
import java.util.*

class ChatListViewModel @ViewModelInject constructor(
    private val auth : FirebaseAuth,
    private val db : FirebaseFirestore
): ViewModel() {

    private val _chatDataList = MutableLiveData<List<Chat>>()
    val chatDataList : LiveData<List<Chat>> = _chatDataList.distinctUntilChanged()

    init {
        Log.d("Chat List","Chat list view model : ${UUID.randomUUID()}")
        loadChatList()
    }

    private fun loadChatList() {

        db.collection(CHAT_INFO)
            .document(auth.currentUser!!.uid)
            .collection(CHAT_LIST)
            .orderBy("date",Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->

                if ( error != null) {
                    return@addSnapshotListener
                }

                val chatList = value?.let {

                    val tempChatList = mutableListOf<Chat>()
                    for(data in it.documents) {
                        val chat = data.toObject(Chat::class.java)!!
                        tempChatList.add(chat)
                    }
                    tempChatList

                } ?: listOf()

                _chatDataList.postValue(chatList)

            }

    }

}