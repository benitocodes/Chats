package com.thuraaung.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thuraaung.chats.model.Message

class ChatAdapter(private val messageList : List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        if (getItemViewType(position) == 1)
            (holder as LeftViewHolder).bind(messageList[position])
        else
            (holder as RightViewHolder).bind(messageList[position])
    }

    override fun getItemCount(): Int = messageList.size

    override fun getItemViewType(position: Int): Int {
        return messageList[position].sender
    }

    class LeftViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        fun bind(message : Message) {

        }
    }

    class RightViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        fun bind(message: Message) {}
    }
}