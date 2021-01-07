package com.thuraaung.chats.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thuraaung.chats.Constants.APP_USERS
import com.thuraaung.chats.R
import com.thuraaung.chats.model.AppUser
import com.thuraaung.chats.model.Message

class ChatAdapter(
    private val currentUser : String,
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
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_left_message,parent,false)
                LeftMessageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_right_message,parent,false)
                RightMessageViewHolder(view)
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

    inner class LeftMessageViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        private val imgProfile = view.findViewById<ImageView>(R.id.img_profile)
        private val tvMessage = view.findViewById<TextView>(R.id.tv_message)

        fun bind(message : Message) {
            tvMessage.text = message.message

            db.collection(APP_USERS)
                .document(message.sender)
                .addSnapshotListener { value, error ->

                    if (error != null) {
                        return@addSnapshotListener
                    }

                    val user = value!!.toObject(AppUser::class.java)!!

                    imgProfile.load(user.photoUrl) {
                        crossfade(true)
                        placeholder(R.drawable.ic_baseline_account_circle_24)
                        transformations(CircleCropTransformation())
                    }

                }
        }
    }

    inner class RightMessageViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        private val tvMessage = view.findViewById<TextView>(R.id.tv_message)
        private val tvStatus = view.findViewById<TextView>(R.id.tv_status)

        fun bind(message: Message) {

            val isLastMessage = adapterPosition == 0

            if (isLastMessage) {

                tvStatus.apply {
                    text = if (message.seen) "Seen" else "Sent"
                    visibility = View.VISIBLE
                }

            } else {
                tvStatus.visibility = View.GONE
            }
            tvMessage.text = message.message

        }
    }

}