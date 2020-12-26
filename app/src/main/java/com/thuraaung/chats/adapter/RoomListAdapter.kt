package com.thuraaung.chats.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thuraaung.chats.R
import com.thuraaung.chats.model.Room

class RoomListAdapter(private val clickListener : ((Room) -> Unit)? = null) : RecyclerView.Adapter<RoomListAdapter.ChatListViewHolder>() {

    private val roomList = mutableListOf<Room>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_room_list_item,parent,false)
        return ChatListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        holder.bind(roomList[position])
    }

    override fun getItemCount(): Int = roomList.size

    fun updateRoomList(rooms : List<Room>) {
        roomList.clear()
        roomList.addAll(rooms)
        notifyDataSetChanged()
    }

    inner class ChatListViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        init {
            view.setOnClickListener { clickListener?.invoke(roomList[adapterPosition]) }
        }

        fun bind(room : Room) {

        }
    }
}