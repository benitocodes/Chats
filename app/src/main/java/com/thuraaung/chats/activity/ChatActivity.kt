package com.thuraaung.chats.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thuraaung.chats.adapter.ChatAdapter
import com.thuraaung.chats.R
import com.thuraaung.chats.temp.MessageFactory

class ChatActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val rvChat = findViewById<RecyclerView>(R.id.rv_chat)
        rvChat.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ChatAdapter(MessageFactory.getMessageList(20))
        }
    }
}