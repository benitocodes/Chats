package com.thuraaung.chats.activity

import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.thuraaung.chats.vm.ChatViewModel
import com.thuraaung.chats.R
import com.thuraaung.chats.adapter.ChatAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    private val chatViewModel: ChatViewModel by viewModels()

    private val uid: String by lazy {
        intent.getStringExtra("uid") ?: throw Exception("Room id cannot be null")
    }
    private lateinit var chatAdapter: ChatAdapter

    private lateinit var lblUser: TextView
    private lateinit var imgUser: ImageView
    private lateinit var rvChat : RecyclerView
    private lateinit var etMessage : EditText
    private lateinit var btnSend : ImageButton
    private lateinit var imgBack : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatViewModel.readMessage(uid)
        chatViewModel.getUser(uid)

        rvChat = findViewById(R.id.rv_chat)
        lblUser = findViewById(R.id.lbl_user)
        imgUser = findViewById(R.id.img_user)
        etMessage = findViewById(R.id.et_message)
        btnSend = findViewById(R.id.btn_send)
        imgBack = findViewById(R.id.img_back)

        chatAdapter = ChatAdapter(auth.currentUser!!.uid)

        rvChat.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = false
                reverseLayout = true
            }
            adapter = chatAdapter
        }

        imgBack.setOnClickListener {
            finish()
        }

        btnSend.setOnClickListener {
            val message = etMessage.text.toString().trim()
            etMessage.text.clear()
            if (message.isNotEmpty() || message.isNotBlank()) {
                chatViewModel.sendMessage(
                    uid = uid,
                    message = message,
                    doOnSuccess = null,
                    doOnFailure = null
                )
            } else {
                Toast.makeText(this, "Empty message cannot be sent", Toast.LENGTH_SHORT).show()
            }
        }

        chatViewModel.messageList.observe(this, { messageList ->
            chatViewModel.seenMessages(uid, messageList)
            chatAdapter.updateMessage(messageList)
        })

        chatViewModel.userData.observe(this) { user ->
            user?.let {
                lblUser.text = user.name
                imgUser.load(user.photoUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_baseline_account_circle_24)
                    transformations(CircleCropTransformation())
                }
            }
        }

    }
}