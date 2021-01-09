package com.thuraaung.chats.vm

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.thuraaung.chats.Constants.APP_USERS
import com.thuraaung.chats.Constants.CHAT_INFO
import com.thuraaung.chats.Constants.CHAT_LIST
import com.thuraaung.chats.Constants.MESSAGE_LIST
import com.thuraaung.chats.Constants.TOKEN
import com.thuraaung.chats.IDGenerator.generateMessageId
import com.thuraaung.chats.currentUid
import com.thuraaung.chats.model.AppUser
import com.thuraaung.chats.model.Chat
import com.thuraaung.chats.model.Message
import com.thuraaung.chats.model.Token
import com.thuraaung.chats.noti.NotificationData
import com.thuraaung.chats.noti.PushNotification
import com.thuraaung.chats.noti.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ChatViewModel @ViewModelInject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    val messageList = MutableLiveData<List<Message>>()
    private val _chattingUser = MutableLiveData<AppUser>()
    val chattingUser : LiveData<AppUser> = _chattingUser

    fun readMessage(uid : String) {

        db.collection(CHAT_INFO)
            .document(auth.currentUser!!.uid)
            .collection(CHAT_LIST)
            .document(uid)
            .collection(MESSAGE_LIST)
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

        val ccRef = db.collection(CHAT_INFO)
            .document(auth.currentUser!!.uid)
            .collection(CHAT_LIST)
            .document(uid)

        val ccc = ccRef
            .collection(MESSAGE_LIST)
            .document(msgModel.id)

        val aaRef = db.collection(CHAT_INFO)
            .document(uid)
            .collection(CHAT_LIST)
            .document(auth.currentUser!!.uid)

        val aaa = aaRef
            .collection(MESSAGE_LIST)
            .document(msgModel.id)

        db.runBatch { batch ->
            batch.set(ccRef,Chat(uid,Date()))
            batch.set(ccc,msgModel)
            batch.set(aaRef,Chat(auth.currentUser!!.uid,Date()))
            batch.set(aaa,msgModel) }
            .addOnSuccessListener {
                sendNotificationToken(
                    sendTo = uid,
                    message = message,
                    doOnSuccess = doOnSuccess,
                    doOnFailure = doOnFailure)
            }
            .addOnFailureListener {
                doOnFailure?.invoke()
            }
    }

    private fun sendNotificationToken(
        sendTo : String,
        message: String,
        doOnSuccess: (() -> Unit)? = null,
        doOnFailure: (() -> Unit)? = null
    ) {

        db.collection(TOKEN)
            .document(sendTo)
            .get()
            .addOnSuccessListener { docSnapShot ->
                val token = docSnapShot.toObject(Token::class.java)
                token?.let {
                    val data = NotificationData(
                        uid = auth.currentUid(),
                        title = auth.currentUser!!.displayName!!,
                        message = message)
                    pushNotification(PushNotification(data = data,to = it.token))
                }
                doOnSuccess?.invoke()
            }
            .addOnFailureListener { doOnFailure?.invoke() }
    }

    fun seenMessages(uid : String,messageList : List<Message>) {
        messageList
            .filter { message -> uid == message.sender && !message.seen }
            .forEach { message -> updateMessage(uid,message) }
    }

    private fun pushNotification(postNotification : PushNotification) {

        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = RetrofitInstance.api.postNotification(postNotification)
                if (response.isSuccessful) {
                    Log.d("Send notification","Send notification success")
                } else {
                    Log.d("Send notification","Send notification failed")
                }

            } catch (e : Exception) {
                Log.e("Send notification","Send notification failed")
            }
        }

    }

    private fun updateMessage(
        uid : String,message : Message,
        doOnSuccess: (() -> Unit)? = null,
        doOnFailure: (() -> Unit)? = null) {

        val ccc = db.collection(CHAT_INFO)
            .document(uid)
            .collection(CHAT_LIST)
            .document(auth.currentUser!!.uid)
            .collection(MESSAGE_LIST)
            .document(message.id)

        val aaa = db.collection(CHAT_INFO)
            .document(auth.currentUser!!.uid)
            .collection(CHAT_LIST)
            .document(uid)
            .collection(MESSAGE_LIST)
            .document(message.id)

        db.runBatch { batch ->
            batch.update(ccc,mapOf("seen" to true))
            batch.update(aaa,mapOf("seen" to true)) }
            .addOnSuccessListener { doOnSuccess?.invoke() }
            .addOnFailureListener { doOnFailure?.invoke() }

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
                _chattingUser.postValue(value!!.toObject(AppUser::class.java))
            }
    }

}