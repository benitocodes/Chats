package com.thuraaung.chats.adapter

import android.content.Context
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
import com.thuraaung.chats.Constants.CHAT_INFO
import com.thuraaung.chats.Constants.CHAT_LIST
import com.thuraaung.chats.Constants.MESSAGE_LIST
import com.thuraaung.chats.DateFormatter
import com.thuraaung.chats.R
import com.thuraaung.chats.model.AppUser
import com.thuraaung.chats.model.Chat
import com.thuraaung.chats.model.Message

class ChatListAdapter(
    private val context : Context,
    private val auth : FirebaseAuth,
    private val db : FirebaseFirestore,
    private val clickListener : ((Chat) -> Unit)? = null) : RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>() {

    private var uidList = emptyList<Chat>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_room_list_item,parent,false)
        return ChatListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        holder.bind(uidList[position])
    }

    override fun getItemCount(): Int = uidList.size

    fun updateRoomList(chats : List<Chat>) {
        uidList = chats
        notifyDataSetChanged()
    }

    inner class ChatListViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        private val imgUser = view.findViewById<ImageView>(R.id.img_user)
        private val tvMessage = view.findViewById<TextView>(R.id.tv_message)
        private val tvUserName = view.findViewById<TextView>(R.id.tv_user_name)
        private val imgStatus = view.findViewById<ImageView>(R.id.img_status)
        private val tvChatDate = view.findViewById<TextView>(R.id.tv_chat_date)

        init {
            view.setOnClickListener { clickListener?.invoke(uidList[adapterPosition]) }
        }

        fun bind(chat : Chat) {

            tvChatDate.text = DateFormatter.formatDate(chat.date)

            db.collection(APP_USERS)
                .document(chat.uid)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    val appUser = value!!.toObject(AppUser::class.java)!!
                    tvUserName.text = appUser.name

                    imgUser.load(appUser.photoUrl) {
                        crossfade(true)
                        placeholder(R.drawable.ic_baseline_account_circle_24)
                        transformations(CircleCropTransformation())
                    }
                }

            db.collection(CHAT_INFO)
                .document(auth.currentUser!!.uid)
                .collection(CHAT_LIST)
                .document(chat.uid)
                .collection(MESSAGE_LIST)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    value?.let {

                        for(data in it.iterator()) {

                            val message = data.toObject(Message::class.java)
                            if (auth.currentUser!!.uid != message.sender && !message.seen) {
                                imgStatus.visibility = View.VISIBLE
                                tvMessage.setTextColor(context.getColor(R.color.black_900))
                            } else {
                                imgStatus.visibility = View.INVISIBLE
                                tvMessage.setTextColor(context.getColor(R.color.grey_700))
                            }

                            tvMessage.text = message.message
                        }

                    }

                }
        }
    }
}