package com.thuraaung.chats.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thuraaung.chats.R
import com.thuraaung.chats.model.Message

class ChatAdapter(private val currentUid : String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {

        const val SEND_MESSAGE = 0
        const val RECEIVED_MESSAGE = 1
    }

    private val messageList = mutableListOf<Message>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_left_message,parent,false)
            LeftViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_right_message,parent,false)
            RightViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == RECEIVED_MESSAGE)
            (holder as LeftViewHolder).bind(messageList[position])
        else
            (holder as RightViewHolder).bind(messageList[position])
    }

    override fun getItemCount(): Int = messageList.size

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if(message.sender == currentUid) SEND_MESSAGE else RECEIVED_MESSAGE
    }

    fun updateMessage(messages : List<Message>) {
        messageList.clear()
        messageList.addAll(messages)
        notifyDataSetChanged()
    }

    class LeftViewHolder(view : View) : RecyclerView.ViewHolder(view) {

//        private val imgProfile = view.findViewById<ImageView>(R.id.img_profile)
        private val tvMessage = view.findViewById<TextView>(R.id.tv_message)

        fun bind(message : Message) {
            tvMessage.text = message.message
        }
    }

    inner class RightViewHolder(view : View) : RecyclerView.ViewHolder(view) {

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