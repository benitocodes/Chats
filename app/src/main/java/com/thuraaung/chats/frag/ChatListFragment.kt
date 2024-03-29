package com.thuraaung.chats.frag

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.thuraaung.chats.activity.ChatActivity
import com.thuraaung.chats.adapter.ChatListAdapter
import com.thuraaung.chats.databinding.FragmentChatListBinding
import com.thuraaung.chats.vm.ChatListViewModel

class ChatListFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    private val chatListViewModel : ChatListViewModel by activityViewModels()
    private lateinit var roomListAdapter :  ChatListAdapter
    private lateinit var binding : FragmentChatListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentChatListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        roomListAdapter = ChatListAdapter(requireContext(),auth,db) { chat ->
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("uid", chat.uid)
            startActivity(intent)
        }

        binding.rvChat.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = roomListAdapter
//            addItemDecoration(
//                DividerItemDecoration(
//                    context,
//                    LinearLayoutManager.VERTICAL
//                )
//            )
        }

        chatListViewModel.chatDataList.observe(viewLifecycleOwner,{ list ->
            roomListAdapter.updateRoomList(list)
        })

    }

}