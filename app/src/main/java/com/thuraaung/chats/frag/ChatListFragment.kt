package com.thuraaung.chats.frag

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thuraaung.chats.Constants.ROOM_ID
import com.thuraaung.chats.R
import com.thuraaung.chats.activity.ChatActivity
import com.thuraaung.chats.adapter.RoomListAdapter
import com.thuraaung.chats.vm.ChatListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatListFragment : Fragment() {

    @Inject
    lateinit var auth : FirebaseAuth
    @Inject
    lateinit var db : FirebaseFirestore

    private val chatListViewModel : ChatListViewModel by activityViewModels()
    private val roomListAdapter = RoomListAdapter { room ->
        val intent = Intent(context, ChatActivity::class.java)
        intent.putExtra(ROOM_ID,room.id)
        startActivity(intent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvChat = view.findViewById<RecyclerView>(R.id.rv_chat)
        rvChat.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = roomListAdapter
        }

        chatListViewModel.roomList.observe(viewLifecycleOwner,{ list ->
            roomListAdapter.updateRoomList(list)
            Toast.makeText(context,"Room list size : ${list.size}",Toast.LENGTH_SHORT).show()
        })
    }
}