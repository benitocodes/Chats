package com.thuraaung.chats.frag

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.thuraaung.chats.Constants.ROOM_ID
import com.thuraaung.chats.R
import com.thuraaung.chats.activity.ChatActivity
import com.thuraaung.chats.adapter.UserAdapter
import com.thuraaung.chats.vm.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserListFragment : Fragment() {

    @Inject
    lateinit var auth : FirebaseAuth

    private val viewModel : UserViewModel by viewModels()

    private val userAdapter = UserAdapter { user ->

        val roomId = viewModel.getRoomId(listOf(user.uid,auth.currentUser!!.uid))
        val intent = Intent(context,ChatActivity::class.java)
        intent.putExtra(ROOM_ID,roomId)
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvUsers = view.findViewById<RecyclerView>(R.id.rv_users)
        rvUsers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }

        viewModel.userList.observe(viewLifecycleOwner, {
            userAdapter.updateUserList(it)
        })

    }
}