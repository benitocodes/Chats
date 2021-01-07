package com.thuraaung.chats.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thuraaung.chats.R
import com.thuraaung.chats.adapter.ChatAdapter
import com.thuraaung.chats.databinding.ActivityChatBinding
import com.thuraaung.chats.vm.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var db: FirebaseFirestore

    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private val uid: String by lazy {
        intent.getStringExtra("uid") ?: throw Exception("Room id cannot be null")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatViewModel.readMessage(uid)
        chatViewModel.getUser(uid)

        showUserData()
        readMessageList()

        chatAdapter = ChatAdapter(auth.currentUser!!.uid, auth, db)

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

    private fun showUserData() {

        chatViewModel.userData.observe(this) { user ->
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