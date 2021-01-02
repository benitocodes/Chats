package com.thuraaung.chats.vm

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.thuraaung.chats.Constants.APP_USERS
import com.thuraaung.chats.IDGenerator.generateMessageId
import com.thuraaung.chats.model.AppUser
import com.thuraaung.chats.model.Message
import java.util.*

class ChatViewModel @ViewModelInject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    val messageList = MutableLiveData<List<Message>>()
    val userData = MutableLiveData<AppUser?>()

    fun readMessage(uid : String) {

        db.collection(APP_USERS)
            .document(auth.currentUser!!.uid)
            .collection(uid)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot , error ->

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

    fun sendMessage(uid : String,message : String,doOnSuccess: (() -> Unit)?,doOnFailure: (() -> Unit)?) {

        val msgModel = createMessage(message)

        val userListRef = db.collection(APP_USERS)

        val currentUser = userListRef
            .document(auth.currentUser!!.uid)
            .collection(uid)
            .document(msgModel.id)

        val otherUser = userListRef
            .document(uid)
            .collection(auth.currentUser!!.uid)
            .document(msgModel.id)

        db.runBatch { batch ->
            batch.set(currentUser,msgModel)
            batch.set(otherUser,msgModel) }
            .addOnSuccessListener { doOnSuccess?.invoke() }
            .addOnFailureListener { doOnFailure?.invoke() }


    }

    fun seenMessages(uid : String,messageList : List<Message>) {

        for(message in messageList) {
            if (message.sender != auth.currentUser!!.uid && !message.seen) {
                updateMessage(uid,message)
            }
        }
    }

    private fun updateMessage(uid : String,message : Message) {

        db.collection(APP_USERS)
            .document(uid)
            .collection(auth.currentUser!!.uid)
            .document(message.id)
            .update("seen",true)

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

    fun getUser(uid : String) {

        db.collection(APP_USERS)
            .document(uid)
            .addSnapshotListener { value, error ->

                if (error != null) {
                    return@addSnapshotListener
                }

                userData.postValue(value?.toObject(AppUser::class.java))

            }
    }

}