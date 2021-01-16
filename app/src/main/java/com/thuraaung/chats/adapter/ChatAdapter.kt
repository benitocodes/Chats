package com.thuraaung.chats.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thuraaung.chats.utils.Constants.APP_USERS
import com.thuraaung.chats.R
import com.thuraaung.chats.databinding.LayoutLeftMessageBinding
import com.thuraaung.chats.databinding.LayoutRightMessageBinding
import com.thuraaung.chats.model.AppUser
import com.thuraaung.chats.model.Message
import com.thuraaung.chats.utils.DateFormatter

class ChatAdapter(
    private val auth : FirebaseAuth,
    private val db : FirebaseFirestore) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {

        const val SEND_MESSAGE = 0
        const val RECEIVED_MESSAGE = 2
    }

    private val messageList = mutableListOf<Message>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType) {
            RECEIVED_MESSAGE -> {

                val binding = LayoutLeftMessageBinding
                    .inflate(LayoutInflater.from(parent.context),parent,false)
                LeftMessageViewHolder(binding)
            }
            else -> {
                val binding = LayoutRightMessageBinding
                    .inflate(LayoutInflater.from(parent.context),parent,false)
                RightMessageViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == RECEIVED_MESSAGE)
            (holder as LeftMessageViewHolder).bind(messageList[position])
        else
            (holder as RightMessageViewHolder).bind(messageList[position])
    }

    override fun getItemCount(): Int = messageList.size

    override fun getItemViewType(position: Int): Int {
        return if(messageList[position].sender == auth.currentUser!!.uid) SEND_MESSAGE else RECEIVED_MESSAGE
    }

    fun updateMessage(messages : List<Message>) {
        messageList.clear()
        messageList.addAll(messages)
        notifyDataSetChanged()
    }

    inner class LeftMessageViewHolder(private val binding : LayoutLeftMessageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message : Message) {
            binding.tvMessage.text = message.message
            binding.tvDate.text = DateFormatter.formatDate(message.date)
            db.collection(APP_USERS)
                .document(message.sender)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    val user = value!!.toObject(AppUser::class.java)!!
                    binding.imgProfile.load(user.photoUrl) {
                        crossfade(true)
                        placeholder(R.drawable.ic_baseline_account_circle_24)
                        transformations(CircleCropTransformation())
                    }

                }
        }
    }

    inner class RightMessageViewHolder(private val binding : LayoutRightMessageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            val isLastMessage = adapterPosition == 0
            if (isLastMessage) {
                binding.tvStatus.apply {
                    text = if (message.seen) "Seen" else "Sent"
                    visibility = View.VISIBLE
                }
            } else {
                binding.tvStatus.visibility = View.GONE
            }
            binding.tvMessage.text = message.message
            binding.tvDate.text = DateFormatter.formatDate(message.date)
        }
    }

}