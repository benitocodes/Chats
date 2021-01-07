package com.thuraaung.chats.frag

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.thuraaung.chats.activity.ChatActivity
import com.thuraaung.chats.adapter.UserAdapter
import com.thuraaung.chats.databinding.FragmentUserListBinding
import com.thuraaung.chats.vm.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserListFragment : Fragment() {

    @Inject
    lateinit var auth : FirebaseAuth
    private val viewModel : UserViewModel by viewModels()
    private val userAdapter = UserAdapter { user ->
        val intent = Intent(context,ChatActivity::class.java)
        intent.putExtra("uid",user.uid)
        startActivity(intent)
    }

    private lateinit var binding : FragmentUserListBinding

    companion object {
        fun newInstance() : Fragment {
            return UserListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserListBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }

        viewModel.userList.observe(viewLifecycleOwner, {
            userAdapter.updateUserList(it)
        })

    }
}