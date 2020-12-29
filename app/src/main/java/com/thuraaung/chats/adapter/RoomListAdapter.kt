package com.thuraaung.chats.adapter

import android.util.Log
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
import com.google.firebase.firestore.Query
import com.thuraaung.chats.Constants.APP_USERS
import com.thuraaung.chats.Constants.MESSAGE_LIST
import com.thuraaung.chats.Constants.ROOM_LIST
import com.thuraaung.chats.R
import com.thuraaung.chats.model.AppUser
import com.thuraaung.chats.model.Message
import com.thuraaung.chats.model.Room

class RoomListAdapter(
    private val auth : FirebaseAuth,
    private val db : FirebaseFirestore,
    private val clickListener : ((Room) -> Unit)? = null) : RecyclerView.Adapter<RoomListAdapter.ChatListViewHolder>() {

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

        private val imgUser = view.findViewById<ImageView>(R.id.img_user)
        private val tvMessage = view.findViewById<TextView>(R.id.tv_message)
        private val tvUserName = view.findViewById<TextView>(R.id.tv_user_name)

        init {
            view.setOnClickListener { clickListener?.invoke(roomList[adapterPosition]) }
        }

        fun bind(room : Room) {

            val uid : String = room.idList.filter { auth.currentUser!!.uid != it }[0]

            db.collection(APP_USERS)
                .document(uid)
                .addSnapshotListener { value , error ->

                    if (error != null) {
                        Log.d(RoomListAdapter::class.java.simpleName,"Read user failed")
                        return@addSnapshotListener
                    }

                    value?.let {

                        val user = it.toObject(AppUser::class.java)!!
                        tvUserName.text = user.name

                        imgUser.load(user.photoUrl) {
                            crossfade(true)
                            placeholder(R.drawable.ic_baseline_account_circle_24)
                            transformations(CircleCropTransformation())
                        }


                    } ?: Log.d(RoomListAdapter::class.java.simpleName,"Read user failed")

                }


            db.collection(ROOM_LIST)
                .document(room.id)
                .collection(MESSAGE_LIST)
                .orderBy("date",Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener { value, error ->

                    if (error != null) {
                        Log.d("Latest message","Getting latest message failed")
                        return@addSnapshotListener
                    }

                    value?.let {
                        for(data in it.iterator()) {
                            val message = data.toObject(Message::class.java)
                            tvMessage.text = message.message
                            Log.d("Latest message","Getting latest message success")

                        }
                    }

                }
        }
    }
}