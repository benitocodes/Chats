package com.thuraaung.chats.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.thuraaung.chats.utils.Constants.CHATTING_USER
import com.thuraaung.chats.utils.Constants.CHAT_PREF
import com.thuraaung.chats.utils.Constants.DEFAULT_USER
import com.thuraaung.chats.R
import com.thuraaung.chats.adapter.ChatAdapter
import com.thuraaung.chats.databinding.ActivityChatBinding
import com.thuraaung.chats.vm.ChatViewModel


class ChatActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private val uid: String by lazy {
        intent.getStringExtra("uid") ?: throw Exception("uid cannot be null")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatViewModel.readMessage(uid)
        chatViewModel.getUser(uid)

        showUserData()
        readMessageList()

        chatAdapter = ChatAdapter(auth, db)

        binding.rvChat.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = false
                reverseLayout = true
            }
            adapter = chatAdapter
        }

        binding.layoutToolBar.imgBack.setOnClickListener {
            finish()
        }

        binding.btnSend.setOnClickListener {

            val message = binding.etMessage.text.toString().trim()
            binding.etMessage.text.clear()
            if (message.isNotEmpty() && message.isNotBlank()) {

                chatViewModel.sendMessage(
                    uid = uid,
                    message = message,
                    doOnSuccess = null,
                    doOnFailure = null
                )
            }
        }

    }

    override fun onResume() {
        super.onResume()
        saveChattingUser(uid)
    }

    override fun onPause() {
        super.onPause()
        saveChattingUser(DEFAULT_USER)
    }

    private fun saveChattingUser(uid : String) {

        getSharedPreferences(CHAT_PREF, MODE_PRIVATE).edit().run {
            putString(CHATTING_USER,uid)
            apply()
        }
    }

    private fun showUserData() {

        chatViewModel.chattingUser.observe(this) { user ->
            user?.let {
                binding.layoutToolBar.lblUser.text = user.name
                binding.layoutToolBar.imgUser.load(user.photoUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_baseline_account_circle_24)
                    transformations(CircleCropTransformation())
                }
            }
        }
    }

    private fun readMessageList() {

        chatViewModel.messageList.observe(this, { messageList ->
            chatViewModel.seenMessages(uid, messageList)
            chatAdapter.updateMessage(messageList)
        })

    }
}