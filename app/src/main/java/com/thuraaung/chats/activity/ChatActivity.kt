package com.thuraaung.chats.activity

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.thuraaung.chats.Constants.ROOM_ID
import com.thuraaung.chats.vm.ChatViewModel
import com.thuraaung.chats.R
import com.thuraaung.chats.adapter.ChatAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    @Inject
    lateinit var auth : FirebaseAuth

    private val chatViewModel : ChatViewModel by viewModels()

    private val roomId : String by lazy {
        intent.getStringExtra(ROOM_ID) ?: throw Exception("Room id cannot be null")
    }

    private lateinit var chatAdapter : ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatAdapter = ChatAdapter(auth.currentUser!!.uid)

        val rvChat = findViewById<RecyclerView>(R.id.rv_chat)
        rvChat.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = false
                reverseLayout = true
            }
            adapter = chatAdapter
        }

        val et_message = findViewById<EditText>(R.id.et_message)
        val btnSend = findViewById<ImageButton>(R.id.btn_send)

        btnSend.setOnClickListener {
            val message = et_message.text.toString()
            et_message.text.clear()
            if (message.isNotEmpty() || message.isNotBlank()) {
                sendMessage(message)
            } else {
                Toast.makeText(this,"Empty message cannot be sent",Toast.LENGTH_SHORT).show()
            }
        }

        chatViewModel.readMessage(roomId)

        chatViewModel.messageList.observe(this, { messageList ->
            chatViewModel.seenMessages(roomId,messageList)
            chatAdapter.updateMessage(messageList)
        })

    }

    private fun sendMessage(message : String) {

        chatViewModel.sendMessage(
            roomId = roomId,
            message = message,
            null,null)

    }
}