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
import com.thuraaung.chats.IDGenerator.generateMessageId
import com.thuraaung.chats.model.Message
import java.util.*

class ChatViewModel @ViewModelInject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    val messageList = MutableLiveData<List<Message>>()

    fun readMessage(roomId : String) {

        db.collection(ROOM_LIST)
            .document(roomId)
            .collection(MESSAGE_LIST)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Log.d(ChatViewModel::class.simpleName, "Read message failed")
                    return@addSnapshotListener
                }

                messageList.postValue(snapshot?.let {
                    val messageList = mutableListOf<Message>()
                    for(msg in it.documents) {
                        val message = msg.toObject(Message::class.java)!!
                        messageList.add(message)
                    }
                    messageList
                } ?: listOf())

            }
    }

    fun sendMessage(roomId: String,message : String,
                    doOnSuccess : (() -> Unit)? = null,
                    doOnFailure : (() -> Unit)? = null ) {

        val messageModel = createMessage(message)

        val messageRef = db.collection(ROOM_LIST)
            .document(roomId)
            .collection(MESSAGE_LIST)
            .document(messageModel.id)

        val roomRef = db.collection(ROOM_LIST)
            .document(roomId)

        db.runBatch { batch ->
            batch.set(messageRef,messageModel)
            batch.update(roomRef,"date",Date()) }
            .addOnSuccessListener { doOnSuccess?.invoke() }
            .addOnFailureListener { doOnFailure?.invoke() }
    }

    fun seenMessages(roomId : String,messageList : List<Message>) {

        for(message in messageList) {
            if (message.sender != auth.currentUser!!.uid && !message.seen) {
                updateMessage(roomId,message)
            }
        }
    }

    private fun createMessage(message : String) : Message {

        return Message(
            id = generateMessageId(),
            message = message,
            sender = auth.currentUser!!.uid,
            date = Date(),
            seen = false
        )

    }

    private fun updateMessage(roomId : String,message : Message) {

        db.collection(ROOM_LIST)
            .document(roomId)
            .collection(MESSAGE_LIST)
            .document(message.id)
            .update("seen",true)
    }

}